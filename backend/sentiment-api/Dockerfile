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

# Copia o JAR do estágio de build
COPY --from=builder /workspace/target/*.jar app.jar

# Copia a Wallet para dentro do container (assumindo que a pasta wallet está no contexto de build)
# Nota: Para o docker-compose, o volume mapeado sobrescreverá isso se existir.
COPY wallet /app/wallet

# Configura a variável de ambiente para o Oracle
ENV TNS_ADMIN=/app/wallet

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]