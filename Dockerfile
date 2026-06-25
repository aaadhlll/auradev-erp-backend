# =============================================================================
# AuraDev ERP API — Multi-stage Docker build (Render / Railway / GHCR)
# =============================================================================

FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /workspace

COPY pom.xml ./
RUN mvn dependency:go-offline -B --no-transfer-progress

COPY src ./src
RUN mvn -B package -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S erp && adduser -S erp -G erp
USER erp

WORKDIR /app

COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", "app.jar"]
