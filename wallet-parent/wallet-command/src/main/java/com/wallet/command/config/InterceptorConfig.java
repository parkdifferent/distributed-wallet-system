package com.wallet.command.config;

import com.google.common.collect.Lists;
import com.wallet.command.interceptor.ErrorHandlingInterceptor;
import com.wallet.command.interceptor.LoggingInterceptor;
import com.wallet.command.interceptor.MetricsInterceptor;
import com.wallet.command.interceptor.ValidationInterceptor;
import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InterceptorConfig {
    
    @Bean
    public List<ServerInterceptor> serverInterceptors(
            ErrorHandlingInterceptor errorHandlingInterceptor,
            LoggingInterceptor loggingInterceptor,
            MetricsInterceptor metricsInterceptor,
            ValidationInterceptor validationInterceptor) {
        
        // Order is important: validation -> error handling -> metrics -> logging
        return Lists.newArrayList(
                validationInterceptor,      // First validate the request
                errorHandlingInterceptor,    // Then handle any errors
                metricsInterceptor,         // Record metrics
                loggingInterceptor          // Finally log the request/response
        );
    }
}
