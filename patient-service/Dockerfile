FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy and build shared proto module first
COPY proto ./proto
RUN mvn -f proto/pom.xml clean install -DskipTests

# Copy and build patient service
COPY patient-service ./patient-service
RUN mvn -f patient-service/pom.xml clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/patient-service/target/patient-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4000

ENTRYPOINT ["java", "-jar", "app.jar"]