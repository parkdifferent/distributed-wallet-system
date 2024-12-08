package com.wallet.command.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValidationInterceptor implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ValidationInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(call, headers)) {
            @Override
            public void onMessage(ReqT message) {
                try {
                    validate(message);
                    super.onMessage(message);
                } catch (IllegalArgumentException e) {
                    logger.error("Validation failed: {}", e.getMessage());
                    call.close(Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage()), new Metadata());
                }
            }
        };
    }

    private <ReqT> void validate(ReqT message) {
        if (message == null) {
            throw new IllegalArgumentException("Request message cannot be null");
        }

        // Extract fields using reflection to avoid direct proto dependencies
        try {
            switch (message.getClass().getSimpleName()) {
                case "CreateAccountRequest":
                    validateAccountId(getFieldValue(message, "getAccountId"));
                    validateOperatorId(getFieldValue(message, "getOperatorId"));
                    break;

                case "ChangeBalanceRequest":
                    validateAccountId(getFieldValue(message, "getAccountId"));
                    validateAmount(getFieldValue(message, "getAmount"));
                    validateOperatorId(getFieldValue(message, "getOperatorId"));
                    validateReason(getFieldValue(message, "getReason"));
                    break;

                case "TransferRequest":
                    validateAccountId(getFieldValue(message, "getSourceAccountId"));
                    validateAccountId(getFieldValue(message, "getTargetAccountId"));
                    validateAmount(getFieldValue(message, "getAmount"));
                    validateOperatorId(getFieldValue(message, "getOperatorId"));
                    validateReason(getFieldValue(message, "getReason"));
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request format: " + e.getMessage());
        }
    }

    private String getFieldValue(Object obj, String getterName) {
        try {
            return (String) obj.getClass().getMethod(getterName).invoke(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get field value: " + getterName);
        }
    }

    private void validateAccountId(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be empty");
        }
        if (accountId.length() > 50) {
            throw new IllegalArgumentException("Account ID cannot exceed 50 characters");
        }
    }

    private void validateOperatorId(String operatorId) {
        if (operatorId == null || operatorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Operator ID cannot be empty");
        }
        if (operatorId.length() > 50) {
            throw new IllegalArgumentException("Operator ID cannot exceed 50 characters");
        }
    }

    private void validateAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }
        try {
            BigDecimal value = new BigDecimal(amount);
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            if (value.scale() > 2) {
                throw new IllegalArgumentException("Amount cannot have more than 2 decimal places");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format");
        }
    }

    private void validateReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        if (reason.length() > 200) {
            throw new IllegalArgumentException("Reason cannot exceed 200 characters");
        }
    }
}
