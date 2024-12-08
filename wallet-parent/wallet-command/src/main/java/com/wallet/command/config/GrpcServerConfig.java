package com.wallet.command.config;

import io.grpc.ServerInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.wallet.command.interceptor.ErrorHandlingInterceptor;
import com.wallet.command.interceptor.ValidationInterceptor;
import com.wallet.command.interceptor.MetricsInterceptor;

@Configuration
public class GrpcServerConfig {
    private final MeterRegistry meterRegistry;

    public GrpcServerConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    @GrpcGlobalServerInterceptor
    public ServerInterceptor validationInterceptor() {
        return new ValidationInterceptor();
    }

    @Bean
    @GrpcGlobalServerInterceptor
    public ServerInterceptor errorHandlingInterceptor() {
        return new ErrorHandlingInterceptor();
    }

    @Bean
    @GrpcGlobalServerInterceptor
    public ServerInterceptor metricsInterceptor() {
        return new MetricsInterceptor(meterRegistry);
    }
}
