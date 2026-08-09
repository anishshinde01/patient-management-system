FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# API Gateway only routes HTTP requests and does not depend on the shared proto module

# Copy and build API Gateway
COPY api-gateway ./api-gateway
RUN mvn -f api-gateway/pom.xml clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4004

ENTRYPOINT ["java", "-jar", "app.jar"]