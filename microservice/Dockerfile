FROM python:3.9-slim-buster

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo de requisitos e instala as dependências
COPY microservice/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copia o código da sua aplicação FastAPI
COPY microservice/. .

# Cria o diretório para os modelos e copia os modelos treinados


# Expõe a porta que o FastAPI usa (padrão para uvicorn é 8000)
EXPOSE 8000

# Comando para rodar a aplicação FastAPI com Uvicorn
# --host 0.0.0.0 é necessário para que a aplicação seja acessível de fora do container
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
