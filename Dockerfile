FROM eclipse-temurin:21-jdk

WORKDIR /app

RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*

COPY . .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

EXPOSE 8080

CMD ["./mvnw","spring-boot:run"]