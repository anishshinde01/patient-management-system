FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy and build shared proto module first
COPY proto ./proto
RUN mvn -f proto/pom.xml clean install -DskipTests

# Copy and build analytics service
COPY analytics-service ./analytics-service
RUN mvn -f analytics-service/pom.xml clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/analytics-service/target/analytics-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4002

ENTRYPOINT ["java", "-jar", "app.jar"]