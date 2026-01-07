<div align="center">

# 🧠 h12-sentiment-api
### Sistema Multilíngue de Análise de Sentimento com Arquitetura de Microserviços

![Python](https://img.shields.io/badge/Python-3.9+-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-Framework-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

[🔗 Repositório Oficial no GitHub](https://github.com/cidirclay/h12-sentiment-api)

</div>

---

## 🚀 Status do Projeto: Funcional e Pronto para Demo
Este projeto implementa uma solução robusta e escalável para classificação de sentimentos em feedbacks de clientes. A arquitetura de microserviços está totalmente operacional, suportando **detecção automática de idioma** e múltiplos algoritmos de Machine Learning.

O sistema processa entradas em **Português, Inglês e Espanhol**, aplicando o modelo mais acurado para cada caso e retornando uma análise tripartida (Positivo, Negativo ou Neutro).

---

## 🏛️ Arquitetura e Fluxo de Dados

A solução foi construída separando a lógica de negócio da lógica de inferência de dados:

1.  **Frontend:** Interface em HTML5/JS que permite a interação do usuário e a escolha dinâmica entre modelos (Naive Bayes vs SVM).
2.  **API Gateway (Java Spring Boot):** * Gerencia o tráfego e as políticas de CORS.
    * Valida os DTOs de entrada.
    * Implementa resiliência: se o serviço de IA estiver offline, o Java retorna um erro `503 Service Unavailable` tratado.
3.  **IA Microservice (Python FastAPI):**
    * **Detecção de Idioma:** Usa processamento em tempo real para identificar a língua do texto.
    * **Pre-processing:** Aplica limpeza via Regex (remoção de URLs, caracteres especiais).
    * **Inferência:** Carrega dinamicamente os arquivos `.pkl` solicitados para realizar a predição.



---

## 👥 O TIME (Squads)

### 🧠 Squad Data Science (NLP & Modelos)
* **Rayra Bandeira de Mello Gomes Dias** - Pesquisa e Limpeza de Dados.
* **Moisés Ribeiro dos Santos Junior** - Treinamento de Modelos e Avaliação.
* **Daniel Farney Moura Moreira** - Notebooks e Otimização.
* **Lidia Lapertosa** - Validação de modelos e suporte a dados em Espanhol.

### ⚙️ Squad Backend (Desenvolvimento Java)
* **Ailson Moreira** - Implementação de Services e Controllers.
* **Leandro Fernandes Moraes** - WebClient e Integração Reativa.
* **Ana Fernandez Cruz** - Validações e DTOs.

### 🏗️ Liderança Técnica & DevOps
* **Cidirclay Santos de Lima Queiroz** - Arquitetura de Containers, Dockerização e Integração entre Squads.

---

## ✨ Features Principais

* **⚡ Arquitetura Desacoplada:** Componentes independentes facilitam o deploy e manutenção.
* **🌐 Suporte Multilíngue Real:** Detecção automática sem necessidade de intervenção do usuário.
* **⚖️ Comparação de Modelos:** Interface permite testar o mesmo texto com **Naive Bayes (Acurácia: 84.4%)** ou **SVM**.
* **🛡️ Blindagem Contra Erros:** Tratamento de erros no Backend Java para evitar quedas em cascata.
* **🏗️ Docker Multi-Stage:** Compilação nativa do Java dentro do container, eliminando necessidade de ferramentas instaladas localmente.

---

## ⚙️ Instruções de Execução (Guia Rápido)

Graças ao build automatizado que implementamos, você só precisa do Docker para rodar o projeto inteiro.

### 1. Iniciar o Ambiente
Na pasta raiz do projeto, execute:
```shell
docker compose up --build
```

A flag --build garante que todas as alterações recentes no código Java e Python sejam compiladas.

2. Acessar a Aplicação
Interface Web: Abra o arquivo frontend/index.html diretamente no navegador.
```shell
API Java: http://localhost:8080/sentiment

IA Microservice: http://localhost:8000/docs (Documentação Swagger)
```

## 🧩 Guia de Expansão (Novos Idiomas)
Para adicionar um novo idioma (ex: Francês - fr):

Treine o modelo e o vetorizador e salve em microservice/models/ como:

nb_model_fr.pkl e tfidf_fr.pkl

Adicione 'fr' à lista supported_languages no arquivo microservice/app.py.

Reinicie o container com docker compose up --build.

## 🧠 Model Training (Data Science)
O retreino pode ser feito via script para garantir consistência:

Shell

# Exemplo: Treinar Naive Bayes para Espanhol
python3 data-science/train_models.py --language es --model_type nb
Métrica de Sucesso: O modelo Naive Bayes (MultinomialNB) foi validado com 84.38% de acurácia no dataset de feedbacks, sendo o padrão atual de produção para espanhol.

<div align="center"> Desenvolvido como projeto integrador para NoCountry 2024. </div>