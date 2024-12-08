package com.wallet.query;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wallet.query.mapper")
public class WalletQueryApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletQueryApplication.class, args);
    }
}
