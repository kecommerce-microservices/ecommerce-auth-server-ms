FROM eclipse-temurin:17.0.5_8-jre-alpine

COPY build/libs/*.jar /opt/app/application.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --spider -q http://localhost:8080/api/actuator/health || exit 1

CMD java -jar /opt/app/application.jar