# Dockerfile
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# copia o jar gerado pelo Maven
COPY target/*.jar app.jar

# Cloud Run manda a porta via env PORT
ENV PORT=8080
EXPOSE 8080

# importantíssimo: ouvir em 0.0.0.0
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
