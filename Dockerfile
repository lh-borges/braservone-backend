FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

COPY target/braserv-one-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]