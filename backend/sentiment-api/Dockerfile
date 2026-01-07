# Estágio de Build
FROM eclipse-temurin:17-jdk-focal AS builder

WORKDIR /workspace

# Copia os arquivos do Maven
COPY . .

# Executa o build do Maven para gerar o JAR, tratando permissões no mvnw
RUN chmod +x ./mvnw && ./mvnw package -DskipTests

# Estágio de Produção
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copia o JAR do estágio de build (o nome exato é encontrado automaticamente)
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]