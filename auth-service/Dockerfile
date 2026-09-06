# =========================
# Stage 1: Build the JAR
# =========================

# INSTRUCTION arguments
# Use existing image with Maven + Java 21, name it builder
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Authentication Service does not depend on the shared proto module

# copy source code of auth service into build image
COPY auth-service ./auth-service
# build the auth-service into a JAR
# -f -> which pom.xml should Maven use
# clean -> remove previous build output
# package -> create JAR
RUN mvn -f auth-service/pom.xml clean package -DskipTests

# =========================
# Stage 2: Run the JAR
# =========================
# Start fresh with only Java 21 as Maven is no longer needed after building JAR
FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

# Take JAR we built above and put here as app.jar
COPY --from=builder /app/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4005

# When container starts -> run JAR
ENTRYPOINT ["java", "-jar", "app.jar"]