from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import os
import re
from typing import Dict
import numpy as np
from langdetect import detect, LangDetectException
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# Habilita CORS para o frontend conseguir acessar o backend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Modelos de Entrada e Saída ---
class ModelInput(BaseModel):
    text: str
    algorithm: str # 'svm', 'nb' ou 'lr'

class ModelOutput(BaseModel):
    previsao: str
    probabilidade: float
    idioma: str
    algoritmo: str

# --- Funções de Limpeza e Carregamento ---
def clean_text(text: str) -> str:
    """
    Limpa o texto removendo URLs, caracteres especiais e espaços extras,
    e convertendo para minúsculas.
    """
    text = text.lower()
    text = re.sub(r'http\S+|www\.\S+', '', text)
    # Agora remove caracteres especiais, mantendo acentos
    text = re.sub(r'[^a-zà-ÿáéíóúãõñç\s]', '', text)
    text = re.sub(r'\s+', ' ', text).strip()
    return text

# Dicionários para armazenar os modelos e vetorizadores carregados
models: Dict[str, object] = {}
tfidfs: Dict[str, object] = {}

MODELS_DIR = os.path.join(os.path.dirname(__file__), "models")

def load_models():
    """
    Escaneia o diretório 'models', encontra todos os modelos de idioma
    e os carrega nos dicionários.
    """
    supported_languages = ['pt', 'en', 'es']
    
    for lang in supported_languages:
        # Carrega o vetorizador
        tfidf_path = os.path.join(MODELS_DIR, f"tfidf_{lang}.pkl")
        if os.path.exists(tfidf_path):
            tfidfs[lang] = joblib.load(tfidf_path)
            print(f"✅ Carregado: tfidf_{lang}.pkl")

        # Carrega o modelo SVM
        svm_model_path = os.path.join(MODELS_DIR, f"svm_model_{lang}.pkl")
        if os.path.exists(svm_model_path):
            models[f"svm_{lang}"] = joblib.load(svm_model_path)
            print(f"✅ Carregado: svm_model_{lang}.pkl")

        # Carrega o modelo Naive Bayes
        nb_model_path = os.path.join(MODELS_DIR, f"nb_model_{lang}.pkl")
        if os.path.exists(nb_model_path):
            models[f"nb_{lang}"] = joblib.load(nb_model_path)
            print(f"✅ Carregado: nb_model_{lang}.pkl")

        # Carrega o modelo Logistic Regression
        lr_model_path = os.path.join(MODELS_DIR, f"lr_model_{lang}.pkl")
        if os.path.exists(lr_model_path):
            models[f"lr_{lang}"] = joblib.load(lr_model_path)
            print(f"✅ Carregado: lr_model_{lang}.pkl")


# Executa o carregamento na inicialização da aplicação
load_models()

# --- Endpoints da API ---
@app.get("/")
async def read_root():
    if not models:
        return {"message": "ERRO: Nenhum modelo de IA foi carregado. Verifique os arquivos .pkl."}
    
    loaded_models = list(models.keys())
    return {
        "message": "Microserviço de Sentimento Multilíngue Online (FastAPI) está rodando!",
        "modelos_carregados": loaded_models,
    }

@app.post("/predict", response_model=ModelOutput)
async def predict(input_data: ModelInput):
    if len(input_data.text.strip()) < 3:
        raise HTTPException(status_code=400, detail="Texto muito curto.")

    # 1. Limpeza do Texto
    cleaned_text = clean_text(input_data.text)
    if not cleaned_text:
        raise HTTPException(status_code=400, detail="O texto inserido é inválido após a limpeza.")

    # 2. Detectar Idioma
    try:
        lang = detect(cleaned_text)
        if lang not in ['pt', 'es', 'en']:
            lang = 'pt' # Fallback para português
    except LangDetectException:
        lang = 'pt'

    # 3. Selecionar Algoritmo
    algorithm = input_data.algorithm.lower()
    model_key = f"{algorithm}_{lang}"
    
    if model_key not in models:
        raise HTTPException(status_code=400, detail=f"Modelo '{algorithm}' para o idioma '{lang}' não encontrado.")

    model = models[model_key]
    tfidf = tfidfs[lang]

    # 4. Predição
    text_vec = tfidf.transform([cleaned_text])
    prediction_int = model.predict(text_vec)[0]
    
    # Mapeia a predição numérica para o label de string correspondente
    prediction_map = {0: "Negativo", 1: "Positivo"}
    prediction = prediction_map.get(prediction_int, "Indefinido")

    prob = 0.5
    if hasattr(model, 'predict_proba'):
        prob = np.max(model.predict_proba(text_vec)[0])
    elif hasattr(model, 'decision_function'):
        decision_value = model.decision_function(text_vec)[0]
        # Simple heuristic to get a confidence score from decision values
        prob = np.max(1 / (1 + np.abs(decision_value)))

    return {
        "previsao": prediction,
        "probabilidade": float(prob),
        "idioma": lang,
        "algoritmo": algorithm.upper()
    }