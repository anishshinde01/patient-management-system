package com.anishshinde.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Purpose of this class:
 * to start the gRPC server when the Spring Boot application starts
 *
 * Explanations:
 *  - The .proto file only defines the contract/interface.
 *    From this, gRPC generates an abstract/base class:
 *          BillingServiceImplBase(generated gRPC server stub)
 *    So we extend it and provide the real implementation here.
 */
@GrpcService
public class BillingGrpcService extends BillingServiceImplBase{

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
           StreamObserver<BillingResponse> responseObserver) { // gRPC callback for sending responses (Common interface for all RPC types)

        log.info("createBillingAccount request received {}", billingRequest.toString());

        // Placeholder for business logic - e.g. save to DB, perform calculations etc.
        // This example demonstrates the gRPC communication flow.

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("ACTIVE")
                .build();

        // Send response to client.
        responseObserver.onNext(response);

        // Signal that no more responses will be sent, completing the RPC.
        responseObserver.onCompleted();
    }

}
