package com.wallet.command.service.grpc;

import com.wallet.command.service.WalletCommandService;
import com.wallet.enums.AssetType;
import com.wallet.service.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class WalletCommandGrpcServiceImpl extends WalletCommandServiceGrpc.WalletCommandServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(WalletCommandGrpcServiceImpl.class);

    private final WalletCommandService walletCommandService;

    public WalletCommandGrpcServiceImpl(WalletCommandService walletCommandService) {
        this.walletCommandService = walletCommandService;
    }

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
        try {
            validateCreateAccountRequest(request);
            
            CompletableFuture<String> future = walletCommandService.createAccount(
                request.getAccountId(),
                new BigDecimal(request.getInitialBalance()),
                AssetType.valueOf(request.getAssetType()),
                new BigDecimal(request.getMinBalance()),
                new BigDecimal(request.getMaxBalance()),
                request.getOperatorId()
            );

            future.thenAccept(accountId -> {
                CreateAccountResponse response = CreateAccountResponse.newBuilder()
                    .setSuccess(true)
                    .setAccountId(accountId)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }).exceptionally(e -> {
                logger.error("Failed to create account", e);
                handleError(responseObserver, e);
                return null;
            });
        } catch (Exception e) {
            logger.error("Error in createAccount request", e);
            handleError(responseObserver, e);
        }
    }

    @Override
    public void transfer(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
        try {
            validateTransferRequest(request);
            
            CompletableFuture<Void> future = walletCommandService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                new BigDecimal(request.getAmount()),
                request.getOperatorId(),
                request.getDedupId()
            );

            future.thenAccept(v -> {
                TransferResponse response = TransferResponse.newBuilder()
                    .setSuccess(true)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }).exceptionally(e -> {
                logger.error("Failed to process transfer", e);
                handleError(responseObserver, e);
                return null;
            });
        } catch (Exception e) {
            logger.error("Error in transfer request", e);
            handleError(responseObserver, e);
        }
    }

    @Override
    public void changeBalance(ChangeBalanceRequest request, StreamObserver<ChangeBalanceResponse> responseObserver) {
        try {
            validateChangeBalanceRequest(request);
            
            CompletableFuture<Void> future = walletCommandService.changeBalance(
                request.getAccountId(),
                new BigDecimal(request.getAmount()),
                request.getOperatorId(),
                request.getDedupId()
            );

            future.thenAccept(v -> {
                ChangeBalanceResponse response = ChangeBalanceResponse.newBuilder()
                    .setSuccess(true)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }).exceptionally(e -> {
                logger.error("Failed to change balance", e);
                handleError(responseObserver, e);
                return null;
            });
        } catch (Exception e) {
            logger.error("Error in changeBalance request", e);
            handleError(responseObserver, e);
        }
    }

    @Override
    public void freezeAccount(FreezeAccountRequest request, StreamObserver<FreezeAccountResponse> responseObserver) {
        try {
            validateFreezeAccountRequest(request);
            
            CompletableFuture<Void> future = walletCommandService.freezeAccount(
                request.getAccountId(),
                request.getOperatorId(),
                request.getReason()
            );

            future.thenAccept(v -> {
                FreezeAccountResponse response = FreezeAccountResponse.newBuilder()
                    .setSuccess(true)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }).exceptionally(e -> {
                logger.error("Failed to freeze account", e);
                handleError(responseObserver, e);
                return null;
            });
        } catch (Exception e) {
            logger.error("Error in freezeAccount request", e);
            handleError(responseObserver, e);
        }
    }

    @Override
    public void unfreezeAccount(UnfreezeAccountRequest request, StreamObserver<UnfreezeAccountResponse> responseObserver) {
        try {
            validateUnfreezeAccountRequest(request);
            
            CompletableFuture<Void> future = walletCommandService.unfreezeAccount(
                request.getAccountId(),
                request.getOperatorId(),
                request.getReason()
            );

            future.thenAccept(v -> {
                UnfreezeAccountResponse response = UnfreezeAccountResponse.newBuilder()
                    .setSuccess(true)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }).exceptionally(e -> {
                logger.error("Failed to unfreeze account", e);
                handleError(responseObserver, e);
                return null;
            });
        } catch (Exception e) {
            logger.error("Error in unfreezeAccount request", e);
            handleError(responseObserver, e);
        }
    }

    private void validateCreateAccountRequest(CreateAccountRequest request) {
        if (request.getAccountId().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be empty");
        }
        if (request.getAssetType().isEmpty()) {
            throw new IllegalArgumentException("Asset type must not be empty");
        }
        if (request.getOperatorId().isEmpty()) {
            throw new IllegalArgumentException("Operator ID must not be empty");
        }
        try {
            new BigDecimal(request.getInitialBalance());
            new BigDecimal(request.getMinBalance());
            new BigDecimal(request.getMaxBalance());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid balance format", e);
        }
    }

    private void validateTransferRequest(TransferRequest request) {
        if (request.getFromAccountId().isEmpty()) {
            throw new IllegalArgumentException("Source account ID must not be empty");
        }
        if (request.getToAccountId().isEmpty()) {
            throw new IllegalArgumentException("Target account ID must not be empty");
        }
        if (request.getOperatorId().isEmpty()) {
            throw new IllegalArgumentException("Operator ID must not be empty");
        }
        if (request.getDedupId().isEmpty()) {
            throw new IllegalArgumentException("Dedup ID must not be empty");
        }
        try {
            new BigDecimal(request.getAmount());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format", e);
        }
    }

    private void validateChangeBalanceRequest(ChangeBalanceRequest request) {
        if (request.getAccountId().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be empty");
        }
        if (request.getOperatorId().isEmpty()) {
            throw new IllegalArgumentException("Operator ID must not be empty");
        }
        if (request.getDedupId().isEmpty()) {
            throw new IllegalArgumentException("Dedup ID must not be empty");
        }
        try {
            new BigDecimal(request.getAmount());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format", e);
        }
    }

    private void validateFreezeAccountRequest(FreezeAccountRequest request) {
        if (request.getAccountId().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be empty");
        }
        if (request.getOperatorId().isEmpty()) {
            throw new IllegalArgumentException("Operator ID must not be empty");
        }
        if (request.getReason().isEmpty()) {
            throw new IllegalArgumentException("Reason must not be empty");
        }
    }

    private void validateUnfreezeAccountRequest(UnfreezeAccountRequest request) {
        if (request.getAccountId().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be empty");
        }
        if (request.getOperatorId().isEmpty()) {
            throw new IllegalArgumentException("Operator ID must not be empty");
        }
        if (request.getReason().isEmpty()) {
            throw new IllegalArgumentException("Reason must not be empty");
        }
    }

    private void handleError(StreamObserver<?> responseObserver, Throwable e) {
        Status status;
        if (e instanceof IllegalArgumentException) {
            status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
        } else if (e instanceof IllegalStateException) {
            status = Status.FAILED_PRECONDITION.withDescription(e.getMessage());
        } else {
            status = Status.INTERNAL.withDescription("Internal error occurred");
        }
        responseObserver.onError(status.asRuntimeException());
    }
}
