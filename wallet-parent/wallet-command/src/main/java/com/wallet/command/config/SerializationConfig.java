package com.wallet.command.config;

import com.wallet.command.infrastructure.serialization.EventSerializer;
import com.wallet.command.infrastructure.serialization.JacksonEventSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SerializationConfig {

    @Bean
    @Primary
    public EventSerializer eventSerializer() {
        return new JacksonEventSerializer();
    }
}
