package com.wallet.command.config;

import com.wallet.command.model.CommandResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, CommandResult> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CommandResult> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        
        // Use Jackson serializer for values
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(CommandResult.class));
        
        // Also set serializers for hash keys and values
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(CommandResult.class));
        
        template.afterPropertiesSet();
        return template;
    }
}
