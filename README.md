# h12-sentiment-api: API de Análise de Sentimento Multilíngue

## 🚀 Status do Projeto (Funcional e Pronto para Demo)

Este projeto implementa uma API robusta para classificar o sentimento de textos em tempo real. A arquitetura de **Microserviços** está totalmente funcional, com suporte a múltiplos idiomas e detecção automática.

O sistema é capaz de identificar se um texto está em **Português** ou **Inglês**, aplicar o modelo de Machine Learning correspondente e retornar a análise de sentimento. A arquitetura já está preparada para receber novos idiomas (como o Espanhol) de forma flexível.

---
## **O TIME (Squads):** 
* **🧠 Squad Data Science:**
 Rayra Bandeira de Mello Gomes Dias,
 Moisés Ribeiro dos Santos Junior,
 Daniel Farney Moura Moreira.

* **⚙️ Squad Backend (Java):**
Ailson Moreira,
Leandro Fernandes Moraes, 
Ana Fernandez Cruz.

* **🏗️ Arquitetura & DevOps:**
Cidirclay Santos de Lima Queiroz.

---

## ✨ Features Principais

*   **Arquitetura de Microserviços:** Gateway de API em **Java/Spring Boot** se comunicando com um serviço de inferência em **Python/FastAPI**.
*   **Análise de Sentimento com SVM:** Utiliza modelos `Support Vector Machine (SVC)` treinados para cada idioma, garantindo alta acurácia.
*   **Suporte Multilíngue com Detecção Automática:** Envie um texto em Português ou Inglês e a API detecta o idioma e aplica o modelo correto automaticamente.
*   **API RESTful:** Contrato de comunicação claro e simples via JSON.
*   **Ambiente Conteinerizado:** Orquestração completa com **Docker** e **Docker Compose**, garantindo um ambiente de desenvolvimento e produção consistente.
*   **Front-End Interativo:** Uma página `index.html` para demonstração visual da API, com a interface mudando de cor de acordo com o resultado do sentimento.

---

## 🛠️ Tecnologias Principais

*   **Gateway API:** Java 17, Spring Boot 4
*   **Microserviço DS:** Python 3.9, FastAPI, scikit-learn, joblib, langdetect
*   **DevOps/Infraestrutura:** Docker, Docker Compose, Maven

---
## 🔌 API - Contrato e Uso

A comunicação é feita via `POST` no Gateway Java, que se comunica com o Microserviço Python.

### Endpoint

`POST /sentiment`

### Entrada (Request Body)

O contrato foi mantido simples. Não é necessário enviar o idioma, pois ele é detectado automaticamente.

```json
{
  "text": "O atendimento foi excelente e o produto chegou rápido!"
}
```

### Saída (Response Body)

```json
{
  "previsao": "Positivo",
  "probabilidade": 0.98
}
```

### Exemplo de Teste com cURL

```shell
curl -X POST http://localhost:8080/sentiment \
-H "Content-Type: application/json" \
-d '{"text": "This is a wonderful product!"}'
```

---
## ⚙️ Instruções de Execução Local

**Pré-requisitos:** Docker e Java 17+ (para o build do Maven) instalados.

O processo de build é feito em **duas etapas obrigatórias** para garantir que as alterações no código Java sejam refletidas no contêiner Docker.

### Passo 1: Construir o Projeto Java (Back-end)

Antes de iniciar o Docker, é necessário compilar o projeto Spring Boot para gerar o arquivo `.jar` atualizado. Execute o comando a partir da pasta **raiz** do projeto:

```shell
wsl ./mvnw -f backend/sentiment-api/pom.xml package -DskipTests
```
*(Este comando pode ser executado apenas uma vez, e repetido somente se houver alterações no código Java).*

### Passo 2: Iniciar os Contêineres com Docker Compose

Com o `.jar` atualizado, você pode iniciar todo o ambiente com um único comando, também a partir da **raiz** do projeto.

```shell
wsl docker compose up --build
```
*   A flag `--build` é importante para reconstruir as imagens com as últimas alterações (código Python, dependências, e o novo `.jar` do Java).

A API Java estará disponível em `http://localhost:8080/sentiment` e o microsserviço Python em `http://localhost:8000`.

### Testando com o Front-End

Após os contêineres estarem no ar, simplesmente **abra o arquivo `index.html`** no seu navegador para usar a interface de demonstração.

---

## 🗣️ Adicionando Novos Idiomas

A arquitetura foi projetada para ser extensível. Para adicionar um novo idioma (ex: Espanhol):

1.  Treine seu modelo e o vetorizador TF-IDF.
2.  Salve os arquivos na pasta `microservice/models/` com o sufixo do idioma no padrão ISO 639-1 (ex: `_es`).
    *   `svm_sentiment_model_es.pkl`
    *   `tfidf_es.pkl`
3.  Adicione o código do idioma (ex: `'es'`) à lista `supported_languages` no topo do arquivo `microservice/app.py`.
4.  Reconstrua a imagem Docker com `wsl docker compose up --build`.

A API irá carregar o novo modelo automaticamente e passará a detectá-lo.
