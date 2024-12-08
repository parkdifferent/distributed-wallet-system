#!/bin/bash

# Ensure using Java 8
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
echo "Using Java version:"
java -version

# Start Redis if not running
if ! pgrep -x "redis-server" > /dev/null; then
    echo "Starting Redis..."
    redis-server &
    sleep 2
fi

# Start Kafka if not running
if ! pgrep -x "kafka" > /dev/null; then
    echo "Starting Kafka..."
    zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &
    sleep 5
    kafka-server-start /usr/local/etc/kafka/server.properties &
    sleep 5
fi

# Create necessary directories
mkdir -p data/rocksdb

# Start the wallet command service
echo "Starting Wallet Command Service..."
cd wallet-parent/wallet-command
mvn spring-boot:run
