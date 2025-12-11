# h12-sentiment-api: API de Análise de Sentimento em Microserviços

## 🚀 Status do Projeto (MVP Integrado)

Este projeto implementa uma API robusta para classificar o sentimento de textos (reviews, comentários) em tempo real. O MVP já possui a arquitetura de **Microserviços** totalmente definida e mesclada na `main`.

---
## **O TIME (Squads):** 
* **🧠 Squad Data Science:** Rayra, Moisés, Daniel.
* **⚙️ Squad Backend (Java):** Ailson, Leandro, Ana Fernandez.
* **🏗️ Arquitetura & DevOps:** Cidirclay.

---
## 🛠️ Tecnologias Principais

* **Gateway API:** Java Spring Boot (Controlador principal e validação).
* **Microserviço DS:** Python (Flask/FastAPI) - Responsável por carregar e rodar o modelo de Machine Learning.
* **DevOps/Infraestrutura:** Docker e Docker Compose (Para orquestração da API Java e do Microserviço Python).

---
## 🔌 Contrato API (Comunicação Final)

A comunicação é feita via `POST` no Gateway Java, que se comunica com o Microserviço Python.

---
### Endpoint Final

`POST /api/sentiment`

### Entrada (Request Body)

json
{
  "text": "O atendimento foi excelente!"
} 

### Saída (Response Body)

O retorno é o Contrato Padrão, já implementado no Backend Java:

json
{
  "previsao": "Positivo",
  "probabilidade": 0.95 
}

---
### ⚙️ Instruções de Execução (Com Docker)

Para rodar a arquitetura completa (Java Gateway + Microserviço Python) com um único comando:

Clone o Repositório: git clone https://github.com/CidQueiroz/h12-sentiment-api.git

Navegue para o Diretório Raiz: cd h12-sentiment-api

Inicie os Microserviços: docker-compose up --build

A API Java estará disponível em http://localhost:8080/api/sentiment.

---
## 🎯 Próximos Passos (Pronto para o Push Final)

1. **BACK-END:** Configuração da URL do Microserviço no `application.properties` e Teste de Integração com Docker Compose.
2. **DATA SCIENCE:** Treinamento do Modelo (Foco no SVM) e entrega do arquivo `.joblib` para o Microserviço Python.
3. **TECH LEAD:** Implementação do Front-End de Apresentação para o Demo Day.