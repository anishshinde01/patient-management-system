package com.anishshinde.stack;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretStringGenerator;

import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalStack extends Stack {

    // Virtual Private Cloud
    private final Vpc vpc;

    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, StackProps props) {
        super(scope, id, props);

        this.vpc = createVpc();

        // RDS (Relational Database Service) [private subnet - hides db from public internet and keeps data secure]

        DatabaseInstance authServiceDb =
                createDatabase("AuthServiceDB", "auth-service-db");
        DatabaseInstance patientServiceDb =
                createDatabase("PatientServiceDB", "patient-service-db");

        CfnHealthCheck authDbHealthCheck =
                createDbHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");
        CfnHealthCheck patientDbHealthCheck =
                createDbHealthCheck(patientServiceDb, "PatientServiceDBHealthCheck");

        // MSK (Managed Streaming for Apache Kafka) [private subnet]

        CfnCluster mskCluster = createMskCluster();

        // ECS (Elastic Container Service) Cluster [private subnet]

        this.ecsCluster = createEcsCluster();

        // AWS Fargate

        // JWT signing secret stored in AWS Secrets Manager
        Secret jwtSecret = createJwtSecret();

        FargateService authService =
                createFargateService("AuthService",
                        "auth-service",
                        List.of(4005),
                        authServiceDb,
                        null,
                        Map.of(
                                "JWT_SECRET",
                                software.amazon.awscdk.services.ecs.Secret
                                        .fromSecretsManager(jwtSecret)
                        ));

        authService.getNode().addDependency(authDbHealthCheck);
        authService.getNode().addDependency(authServiceDb);

        FargateService billingService =
                createFargateService("BillingService",
                        "billing-service",
                        List.of(4001,9001),
                        null,
                        null,
                        null);

        FargateService analyticsService =
                createFargateService("AnalyticsService",
                        "analytics-service",
                        List.of(4002),
                        null,
                        null,
                        null);

        analyticsService.getNode().addDependency(mskCluster);

        FargateService patientService = createFargateService("PatientService",
                "patient-service",
                List.of(4000),
                patientServiceDb,
                Map.of(
                        "BILLING_SERVICE_ADDRESS", "host.docker.internal", // as Localstack doesn't implement the ECS Cloud Discovery functionality
                        "BILLING_SERVICE_GRPC_PORT", "9001"
                ),
                null);
        patientService.getNode().addDependency(patientServiceDb);
        patientService.getNode().addDependency(patientDbHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);

        // api-gateway and ALB

        createApiGatewayService();
    }

    /**
     * Create private virtual network in AWS for the
     * application's resources (e.g. services, databases, load balancers)
     */
    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "PatientManagementVPC")
                .vpcName("PatientManagementVPC")
                // Use max two Availability Zones so resources can remain available if one zone fails
                .maxAzs(2)
                .build();
    }

    private DatabaseInstance createDatabase(String id, String dbName){
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres( // as we need postgres db
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()))
                .vpc(vpc) // connect our db to vpc
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20) // 20 GiB(Gibibyte), GiB is a bit larger than GB
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY) // delete db when Stack is deleted, would not use in prod w/o backup
                .build();
    }

    /**
     * Create a Route 53 health check to monitor whether the service endpoint is available
     * Cfn = CloudFormation-level resource
     */
    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id){
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP") // use TCP endpoint to check if db is online
                        .port(Token.asNumber(db.getDbInstanceEndpointPort())) // get port on which db is running
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        // try once every 30s, and a max. of 3 times, before reporting failure
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }

    private CfnCluster createMskCluster(){
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("3.9.x")
                .numberOfBrokerNodes(2) // in prod, we'd have >1 for resiliency reasons
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        // specifies size of machine to run on(doesn't matter too much for localstack)
                        .instanceType("kafka.m5.xlarge") // higher values = higher compute power but also higher costs
                        .clientSubnets(vpc.getPrivateSubnets().stream() // connect kafka cluster to vpc
                                .map(ISubnet::getSubnetId)
                                .collect(Collectors.toList()))
                        // how brokers get distributed across different availability zones
                        .brokerAzDistribution("DEFAULT") // DEFAULT = let CDK handle distribution
                        .build())
                .build();
    }

    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(vpc)
                // - setup CloudMapNamespace for service discovery in AWS ECS
                // - allows microservices to find and communicate with each other using specified domain
                //   auth-service.patient-management.local
                // - no need to know IPs or internal addresses of ECS services,
                //   as this is now managed by CloudMap service discovery
                // - similar to how docker works when I add all services to the internal network
                //   (--network internal), just at a higher level
                // - functionality not supported very well with localstack as we run on localhost,
                //   but good for prod
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local") // domain
                        .build())
                .build();
    }

    private FargateService createFargateService(String id,
                                                String imageName,
                                                List<Integer> ports,
                                                DatabaseInstance db,
                                                Map<String, String> additionalEnvVars,
                                                Map<String, software.amazon.awscdk.services.ecs.Secret> additionalSecrets) {

        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, id + "Task")
                .cpu(256) // 256 CPU units = 0.25 vCPU (virtual CPU)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                // - task will pull image from repo and use it to create a container
                // - from ECR generally, but LocalStack will know to pull from local image registry
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream()
                        // take each port and assign to container and host port
                        .map(port -> PortMapping.builder()
                                .containerPort(port) // the port, app is running on
                                .hostPort(port) // the port this container exposes so that other services can access it
                                .protocol(Protocol.TCP)
                                .build())
                        .toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                        .logGroupName("/ecs" + imageName)
                                        .removalPolicy(RemovalPolicy.DESTROY) // when stack is destroyed -> logs are destroyed
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix(imageName)
                        .build()));

        Map<String, String> envVars = new HashMap<>();
        // Tell the Spring Boot service where to connect to Kafka in LocalStack
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS",
                "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

        if(additionalEnvVars != null) {
            envVars.putAll(additionalEnvVars);
        }

        if(db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user"); // as we have hardcoded this in createDatabase(..)
            envVars.put("SPRING_DATASOURCE_PASSWORD",
                    db.getSecret().secretValueFromJson("password").toString());
            // good for development purposes as we will have same data we have been developing with so far
            // for prod - rather leave out and do manual inserts or db migrations to set up data
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always"); // run data.sql script
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000"); // 60 secs
        }

        containerOptions.environment(envVars);

        if (additionalSecrets != null) {
            // Inject sensitive values from Secrets Manager into the container
            containerOptions.secrets(additionalSecrets);
        }

        taskDefinition.addContainer(imageName + "Container", containerOptions.build());

        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false) // as our service is internal
                .serviceName(imageName)
                .build();

    }

    private Secret createJwtSecret() {
        return Secret.Builder.create(this, "JwtSecret")
                .secretName("patient-management/jwt-secret")
                .generateSecretString(
                        SecretStringGenerator.builder()
                                .passwordLength(44)
                                .excludePunctuation(true)
                                .build()
                )
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private void createApiGatewayService(){

        FargateTaskDefinition taskDefinition =
                FargateTaskDefinition.Builder.create(this, "APIGatewayTaskDefinition")
                        .cpu(256)
                        .memoryLimitMiB(512)
                        .build();

        ContainerDefinitionOptions containerOptions = ContainerDefinitionOptions.builder()
                    .image(ContainerImage.fromRegistry("api-gateway"))
                    .environment(Map.of(
                            "SPRING_PROFILES_ACTIVE", "prod",  // use application-prod.yml (in api-gateway)
                            "AUTH_SERVICE_URL", "http://host.docker.internal:4005"
                    ))
                    .portMappings(List.of(4004).stream()
                            .map(port -> PortMapping.builder()
                                    .containerPort(port)
                                    .hostPort(port)
                                    .protocol(Protocol.TCP)
                                    .build())
                            .toList())
                    .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                            .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                    .logGroupName("/ecs/api-gateway")
                                    .removalPolicy(RemovalPolicy.DESTROY)
                                    .retention(RetentionDays.ONE_DAY)
                                    .build())
                            .streamPrefix("api-gateway")
                            .build()))
                    .build();

        taskDefinition.addContainer("APIGatewayContainer", containerOptions);

        // ALB (Application Load Balancer) [private subnet]

        ApplicationLoadBalancedFargateService apiGateway =
                ApplicationLoadBalancedFargateService.Builder.create(this, "APIGatewayService")
                        .cluster(ecsCluster)
                        .serviceName("api-gateway")
                        .taskDefinition(taskDefinition)
                        .desiredCount(1)
                        .healthCheckGracePeriod(Duration.seconds(60))
                        .build();

    }

    public static void main(final String[] args) {
        // create root CDK(Cloud Development Kit) application
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer()) // don't rely on standard CDK bootstrap setup
                .build();

        new LocalStack(app, "localstack", props);

        // Synthesize(convert) CDK constructs into CloudFormation template
        app.synth();
        System.out.println("App synthesizing in progress...");
    }

}
