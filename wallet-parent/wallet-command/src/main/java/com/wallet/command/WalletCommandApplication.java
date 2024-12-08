package com.wallet.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class WalletCommandApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletCommandApplication.class, args);
    }
}