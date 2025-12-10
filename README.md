# h12-sentiment-api

RESUMO DO KICK-OFF: PROJETO SENTIMENT-API Data: 09/12/2025 | Facilitador: Cidirclay Queiroz

Definimos o escopo, dividimos os times e traçamos a arquitetura do nosso MVP.

Para quem não pôde participar (e para registro oficial), seguem as definições:

1. O TIME (Squads)

🧠 Squad Data Science: Rayra, Moisés, Daniel.

⚙️ Squad Backend (Java): Ailson, Leandro, Ana Consuelo.

🏗️ Arquitetura & DevOps: Cidirclay.

(Aguardando integração dos 3 membros restantes)

2. O PROJETO: SentimentAPI

Objetivo: API que recebe um texto e classifica o sentimento (Positivo/Neutro/Negativo) com grau de probabilidade.

Arquitetura Definida: Microserviços.

Serviço A (Java Spring): Gateway principal. Recebe a requisição do usuário, valida e chama o Serviço de IA.

Serviço B (Python/Flask ou FastAPI): Carrega o modelo treinado e faz a predição.

Fluxo: Cliente -> Backend Java -> API Python (Modelo) -> Backend Java -> Cliente.

3. O "CONTRATO" (Interface de Comunicação) Para que Backend e Data Science possam trabalhar em paralelo sem travar, definimos o formato JSON padrão desde já:

Entrada (Request):

JSON

{ "text": "O atendimento foi excelente!" }
Saída (Response):

JSON

{
  "previsao": "Positivo",
  "probabilidade": 0.95
}
