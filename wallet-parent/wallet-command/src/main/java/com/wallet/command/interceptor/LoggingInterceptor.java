package com.wallet.command.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggingInterceptor implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        String requestId = UUID.randomUUID().toString();
        String methodName = call.getMethodDescriptor().getFullMethodName();
        long startTime = System.currentTimeMillis();

        logger.info("[{}] Starting call to method: {}", requestId, methodName);
        logMetadata(requestId, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void sendMessage(RespT message) {
                        logger.debug("[{}] Sending response: {}", requestId, message);
                        super.sendMessage(message);
                    }

                    @Override
                    public void close(Status status, Metadata trailers) {
                        long duration = System.currentTimeMillis() - startTime;
                        if (status.isOk()) {
                            logger.info("[{}] Successfully completed call to {} in {}ms",
                                    requestId, methodName, duration);
                        } else {
                            logger.error("[{}] Call to {} failed in {}ms with status: {} - {}",
                                    requestId, methodName, duration,
                                    status.getCode(), status.getDescription());
                        }
                        super.close(status, trailers);
                    }
                }, headers)) {
            @Override
            public void onMessage(ReqT message) {
                logger.debug("[{}] Received request: {}", requestId, message);
                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                logger.debug("[{}] Client finished sending messages", requestId);
                super.onHalfClose();
            }

            @Override
            public void onCancel() {
                logger.warn("[{}] Call cancelled by client", requestId);
                super.onCancel();
            }

            @Override
            public void onComplete() {
                logger.debug("[{}] Call completed", requestId);
                super.onComplete();
            }
        };
    }

    private void logMetadata(String requestId, Metadata headers) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            headers.keys().forEach(key -> {
                if (!key.endsWith("-bin")) { // Skip binary headers
                    String value = headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
                    sb.append(key).append("=").append(value).append(", ");
                }
            });
            if (sb.length() > 0) {
                logger.debug("[{}] Call metadata: {}", requestId, sb.substring(0, sb.length() - 2));
            }
        }
    }
}
