package com.anishshinde.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillingServiceGrpcClient {
    // Client-side proxy for the Billing Service.
    // blocking stub performs synchronous RPC calls and waits for the server's response.
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    // localhost:9001/BillingService/CreatePatientAccount
    public BillingServiceGrpcClient(
            // Read Billing Service host and port from Spring configuration,
            // fall back to localhost:9001 if no values are provided.
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort){
        log.info("Connecting to Billing Service Grpc service at {}:{}", serverAddress, serverPort);

        // Create gRPC communication channel to Billing Service.
        // Use unencrypted (plaintext) connection for local development.
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Equivalent to the manual gRPC request in the
     * grpc-requests/billing-service/create-billing-account.http file
     */
    public BillingResponse createBillingAccount(String patientId, String name, String email){
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId).setName(name).setEmail(email).build();

        // Invoke remote billing service synchronously via gRPC.
        BillingResponse response = blockingStub.createBillingAccount(request);

        log.info("Received response from billing service via gRPC: {}", response);
        return response;
    }
}
