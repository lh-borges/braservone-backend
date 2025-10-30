FROM eclipse-temurin:17-jre as runtime

WORKDIR /app

# copia o jar gerado pelo Maven (ajuste o nome se o seu for outro)
COPY target/*.jar app.jar

# Cloud Run manda a porta via env PORT
ENV PORT=8080
EXPOSE 8080

# importante: ouvir em 0.0.0.0
ENTRYPOINT ["java","-jar","/app/app.jar"]
