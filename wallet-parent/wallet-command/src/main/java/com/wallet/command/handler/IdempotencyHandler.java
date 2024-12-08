package com.wallet.command.handler;

import com.wallet.command.model.Command;
import com.wallet.command.model.CommandResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class IdempotencyHandler {
    private final RedisTemplate<String, CommandResult> redisTemplate;
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

    public IdempotencyHandler(RedisTemplate<String, CommandResult> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<CommandResult> checkIdempotency(Command command) {
        String key = buildIdempotencyKey(command);
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void saveResult(Command command, CommandResult result) {
        String key = buildIdempotencyKey(command);
        redisTemplate.opsForValue().set(key, result, IDEMPOTENCY_TTL);
    }

    private String buildIdempotencyKey(Command command) {
        return String.format("idempotency:%s:%s",
            command.getClass().getSimpleName(),
            command.getCommandId());
    }
}
