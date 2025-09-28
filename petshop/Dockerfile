FROM eclipse-temurin:17-jdk-alpine

# Criar pasta da aplicação
WORKDIR /app

# Copiar o JAR gerado pelo Maven
COPY target/petshop-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta (mesma do application.properties)
EXPOSE 8081

# Comando para rodar o Spring Boot
CMD ["java", "-jar", "app.jar"]