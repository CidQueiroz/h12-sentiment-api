FROM eclipse-temurin:17-jre-focal

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR da sua aplicação Spring Boot para o diretório de trabalho
# Assumimos que o JAR será construído no caminho alvo (target)
# e que o nome do arquivo JAR é 'app.jar'.
# Você precisará ajustar 'app.jar' para o nome real do seu JAR, se for diferente.
COPY target/app.jar app.jar

# Expõe a porta que o Spring Boot usa (padrão é 8080)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
