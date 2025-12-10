
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import pandas as pd
import numpy as np

app = FastAPI()

# Definição dos modelos de entrada e saída
class ModelInput(BaseModel):
    text: str

class ModelOutput(BaseModel):
    previsao: str
    probabilidade: float

# Simulação de carregamento do modelo
# Em um cenário real, você carregaria seu modelo treinado aqui.
# Por exemplo: model = joblib.load("model.joblib")
# e tfidf_vectorizer = joblib.load("tfidf_vectorizer.joblib")

# Para este MVP, vamos simular um modelo simples
class MockModel:
    def predict(self, X):
        # Simula a predição: "Positivo" se a palavra "bom" estiver presente, "Negativo" caso contrário
        return np.array(["Positivo" if "bom" in text.lower() else "Negativo" for text in X])

    def predict_proba(self, X):
        # Simula probabilidades. Se "Positivo", probabilidade alta para Positivo, baixa para Negativo.
        # Caso contrário, o oposto.
        probas = []
        for text in X:
            if "bom" in text.lower():
                probas.append([0.1, 0.9])  # Probabilidade para Negativo, Positivo
            else:
                probas.append([0.8, 0.2])
        return np.array(probas)

# Carrega o "modelo" simulado
# Em um cenário real, esses arquivos seriam gerados pelo time de Data Science.
# Aqui, estamos apenas instanciando o mock.
model = MockModel()

@app.get("/")
async def read_root():
    return {"message": "Microserviço de Sentimento Online (FastAPI) está rodando!"}

@app.post("/predict", response_model=ModelOutput)
async def predict_sentiment(input: ModelInput):
    if not input.text or len(input.text) < 3:
        raise HTTPException(status_code=400, detail="O campo 'text' é obrigatório e deve ter pelo menos 3 caracteres.")

    # Em um cenário real, você aplicaria o TF-IDF e então faria a predição.
    # Ex: text_vectorized = tfidf_vectorizer.transform([input.text])
    #     prediction = model.predict(text_vectorized)[0]
    #     proba = model.predict_proba(text_vectorized)[0]

    # Simulação da predição
    prediction_label = model.predict([input.text])[0]
    probabilities = model.predict_proba([input.text])[0]

    # Mapeia a probabilidade para a previsão correta
    if prediction_label == "Positivo":
        predicted_probability = float(probabilities[1]) # Probabilidade de ser Positivo
    else:
        predicted_probability = float(probabilities[0]) # Probabilidade de ser Negativo

    return ModelOutput(previsao=prediction_label, probabilidade=predicted_probability)

