package com.wallet.command.interceptor;

import io.grpc.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MetricsInterceptor implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MetricsInterceptor.class);
    private final MeterRegistry meterRegistry;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getBareMethodName();
        String serviceName = call.getMethodDescriptor().getServiceName();

        Timer.Sample sample = Timer.start(meterRegistry);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void close(Status status, Metadata trailers) {
                        sample.stop(Timer.builder("grpc.server.calls")
                                .tag("method", methodName)
                                .tag("service", serviceName)
                                .tag("status", status.getCode().name())
                                .description("Timer for gRPC server calls")
                                .register(meterRegistry));

                        // Record call result
                        meterRegistry.counter("grpc.server.calls.total",
                                "method", methodName,
                                "service", serviceName,
                                "status", status.getCode().name())
                                .increment();

                        if (!status.isOk()) {
                            meterRegistry.counter("grpc.server.errors",
                                    "method", methodName,
                                    "service", serviceName,
                                    "code", status.getCode().name())
                                    .increment();

                            logger.warn("gRPC call failed: service={}, method={}, status={}, description={}",
                                    serviceName, methodName, status.getCode(), status.getDescription());
                        }

                        super.close(status, trailers);
                    }
                }, headers)) {
            @Override
            public void onMessage(ReqT message) {
                meterRegistry.counter("grpc.server.messages.received",
                        "method", methodName,
                        "service", serviceName)
                        .increment();
                super.onMessage(message);
            }
        };
    }
}
