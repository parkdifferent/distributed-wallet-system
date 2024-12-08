package com.wallet.command.infrastructure.repository;

import com.wallet.command.event.BaseEvent;
import com.wallet.command.infrastructure.serialization.EventSerializer;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Repository
public class RocksDBEventStore implements EventStore {
    private static final String EVENTS_CF = "events";
    private static final String VERSIONS_CF = "versions";
    private static final String SEQUENCE_CF = "sequence";
    
    private final EventSerializer eventSerializer;
    private final ExecutorService executor;
    private final String dbPath;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private RocksDB db;
    private ColumnFamilyHandle eventsCF;
    private ColumnFamilyHandle versionsCF;
    private ColumnFamilyHandle sequenceCF;
    private AtomicLong sequence;

    public RocksDBEventStore(EventSerializer eventSerializer, @Value("${rocksdb.path}") String dbPath) {
        this.eventSerializer = eventSerializer;
        this.dbPath = dbPath;
        this.executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactory() {
                private int counter = 0;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "event-store-" + counter++);
                    thread.setDaemon(true);
                    return thread;
                }
            });
    }

    @PostConstruct
    public void init() {
        try {
            RocksDB.loadLibrary();
            File dbDir = new File(dbPath);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            final List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>();
            columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
                RocksDB.DEFAULT_COLUMN_FAMILY));
            columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
                EVENTS_CF.getBytes(StandardCharsets.UTF_8)));
            columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
                VERSIONS_CF.getBytes(StandardCharsets.UTF_8)));
            columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
                SEQUENCE_CF.getBytes(StandardCharsets.UTF_8)));

            final List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();

            DBOptions options = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true);

            db = RocksDB.open(options, dbPath, columnFamilyDescriptors, columnFamilyHandles);
            
            // Save column family handles
            eventsCF = columnFamilyHandles.get(1);
            versionsCF = columnFamilyHandles.get(2);
            sequenceCF = columnFamilyHandles.get(3);
            sequence = new AtomicLong(initializeSequence());
        } catch (RocksDBException e) {
            log.error("Failed to initialize RocksDB", e);
            throw new RuntimeException("Failed to initialize RocksDB", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (eventsCF != null) eventsCF.close();
        if (versionsCF != null) versionsCF.close();
        if (sequenceCF != null) sequenceCF.close();
        if (db != null) db.close();
        executor.shutdown();
    }

    @Override
    public CompletableFuture<Void> appendEvents(String aggregateId, long expectedVersion, List<BaseEvent> events) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                WriteOptions writeOpts = new WriteOptions();
                WriteBatch batch = new WriteBatch();

                long currentVersion = getCurrentVersionSync(aggregateId);
                if (currentVersion != expectedVersion) {
                    future.completeExceptionally(
                        new IllegalStateException("Version mismatch: expected " + expectedVersion + 
                            ", but got " + currentVersion + " for aggregate " + aggregateId));
                    return;
                }

                for (BaseEvent event : events) {
                    String eventKey = String.format("%s:%d", aggregateId, ++currentVersion);
                    String serializedEvent = eventSerializer.serialize(event);
                    batch.put(eventsCF, 
                        eventKey.getBytes(StandardCharsets.UTF_8),
                        serializedEvent.getBytes(StandardCharsets.UTF_8));
                }

                batch.put(versionsCF,
                    aggregateId.getBytes(StandardCharsets.UTF_8),
                    String.valueOf(currentVersion).getBytes(StandardCharsets.UTF_8));

                db.write(writeOpts, batch);
                future.complete(null);
            } catch (RocksDBException e) {
                log.error("Failed to append events for aggregate: {}", aggregateId, e);
                future.completeExceptionally(new RuntimeException("Failed to append events for aggregate " + aggregateId, e));
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<List<BaseEvent>> readEvents(String aggregateId, long fromVersion) {
        CompletableFuture<List<BaseEvent>> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                List<BaseEvent> events = new ArrayList<>();
                RocksIterator iterator = db.newIterator(eventsCF);

                String startKey = String.format("%s:%d", aggregateId, fromVersion);
                for (iterator.seek(startKey.getBytes(StandardCharsets.UTF_8)); 
                     iterator.isValid(); 
                     iterator.next()) {
                    String key = new String(iterator.key(), StandardCharsets.UTF_8);
                    if (!key.startsWith(aggregateId + ":")) {
                        break;
                    }
                    String serializedEvent = new String(iterator.value(), StandardCharsets.UTF_8);
                    String eventType = extractEventType(key);
                    events.add(eventSerializer.deserialize(serializedEvent, eventType));
                }

                future.complete(events);
            } catch (Exception e) {
                log.error("Failed to read events for aggregate: {}", aggregateId, e);
                future.completeExceptionally(new RuntimeException("Failed to read events for aggregate " + aggregateId, e));
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<List<BaseEvent>> readAllEvents(String aggregateId) {
        return readEvents(aggregateId, 0);
    }

    @Override
    public CompletableFuture<Long> getCurrentVersion(String aggregateId) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                future.complete(getCurrentVersionSync(aggregateId));
            } catch (Exception e) {
                log.error("Failed to get current version for aggregate: {}", aggregateId, e);
                future.completeExceptionally(new RuntimeException("Failed to get current version for aggregate " + aggregateId, e));
            }
        });
        return future;
    }

    private long getCurrentVersionSync(String aggregateId) throws RocksDBException {
        byte[] versionBytes = db.get(versionsCF, aggregateId.getBytes(StandardCharsets.UTF_8));
        return versionBytes == null ? -1 : Long.parseLong(new String(versionBytes, StandardCharsets.UTF_8));
    }

    private long initializeSequence() {
        try {
            byte[] sequenceBytes = db.get(sequenceCF, "sequence".getBytes(StandardCharsets.UTF_8));
            if (sequenceBytes != null) {
                return Long.parseLong(new String(sequenceBytes, StandardCharsets.UTF_8));
            }
        } catch (RocksDBException e) {
            log.error("Failed to initialize sequence", e);
            throw new RuntimeException("Failed to initialize sequence", e);
        }
        return 0L;
    }

    private String extractEventType(String key) {
        try {
            String[] parts = key.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid event key format: " + key);
            }
            String aggregateId = parts[0];
            String eventData = new String(db.get(eventsCF, key.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            return objectMapper.readTree(eventData).get("type").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract event type from key: " + key, e);
        }
    }
}
