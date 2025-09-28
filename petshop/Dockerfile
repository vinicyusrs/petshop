FROM openjdk:17-jdk-slim

# Criar pasta da aplicação
WORKDIR /app

# Copiar o JAR gerado pelo Maven
COPY target/*.jar app.jar

# Expõe a porta (mesma do application.properties)
EXPOSE 8082

# Comando para rodar o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]