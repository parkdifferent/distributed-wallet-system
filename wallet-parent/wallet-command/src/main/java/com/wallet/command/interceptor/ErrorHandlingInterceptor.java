package com.wallet.command.interceptor;

import com.wallet.command.exception.InsufficientBalanceException;
import com.wallet.command.exception.InvalidAccountStatusException;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

@Component
public class ErrorHandlingInterceptor implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                if (status.getCode() != Status.Code.OK) {
                    status = handleError(status.getCause());
                }
                super.close(status, trailers);
            }
        };

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(wrappedCall, headers)) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (RuntimeException e) {
                    Status status = handleError(e);
                    call.close(status, new Metadata());
                }
            }

            @Override
            public void onMessage(ReqT message) {
                try {
                    super.onMessage(message);
                } catch (RuntimeException e) {
                    Status status = handleError(e);
                    call.close(status, new Metadata());
                }
            }
        };
    }

    private Status handleError(Throwable error) {
        if (error == null) {
            return Status.INTERNAL.withDescription("Unknown error occurred");
        }

        // Unwrap CompletionException
        if (error instanceof CompletionException) {
            error = error.getCause();
        }

        // Map specific exceptions to appropriate gRPC status codes
        if (error instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT.withDescription(error.getMessage());
        } else if (error instanceof InsufficientBalanceException) {
            return Status.FAILED_PRECONDITION.withDescription("Insufficient balance: " + error.getMessage());
        } else if (error instanceof InvalidAccountStatusException) {
            return Status.FAILED_PRECONDITION.withDescription("Invalid account status: " + error.getMessage());
        }

        // Log unexpected errors
        logger.error("Unexpected error occurred", error);
        return Status.INTERNAL.withDescription("Internal server error: " + error.getMessage());
    }
}
