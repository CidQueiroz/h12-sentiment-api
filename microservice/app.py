from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import os
from typing import Dict
from langdetect import detect, LangDetectException

app = FastAPI()

# --- Modelos de Entrada e Saída (Contrato Original) ---
class ModelInput(BaseModel):
    text: str

class ModelOutput(BaseModel):
    previsao: str
    probabilidade: float

# --- Carregamento Dinâmico dos Modelos ---
# Dicionários para armazenar os modelos e vetorizadores carregados
models: Dict[str, joblib.memory.MemorizedResult] = {}
tfidfs: Dict[str, joblib.memory.MemorizedResult] = {}
model_classes: Dict[str, list] = {}
DEFAULT_LANG = 'pt' # Usar português como padrão se a detecção falhar ou o modelo não existir

def load_models():
    """
    Escaneia o diretório 'models', encontra todos os modelos de idioma
    (com sufixos _pt, _es, _en) e os carrega nos dicionários.
    """
    MODEL_DIR = os.path.join(os.path.dirname(__file__), "models")
    supported_languages = ['pt', 'es', 'en']
    
    for lang in supported_languages:
        model_path = os.path.join(MODEL_DIR, f"svm_sentiment_model_{lang}.pkl")
        tfidf_path = os.path.join(MODEL_DIR, f"tfidf_{lang}.pkl")

        if os.path.exists(model_path) and os.path.exists(tfidf_path):
            print(f"Carregando modelo para o idioma: '{lang}'")
            try:
                models[lang] = joblib.load(model_path)
                tfidfs[lang] = joblib.load(tfidf_path)
                model_classes[lang] = models[lang].classes_
                print(f"Modelo '{lang}' carregado com sucesso.")
            except Exception as e:
                print(f"ERRO: Falha ao carregar o modelo '{lang}'. Erro: {e}")
        else:
            print(f"AVISO: Arquivos de modelo para o idioma '{lang}' não encontrados. Pulando.")

# Executa o carregamento na inicialização da aplicação
load_models()


# --- Endpoints da API ---
@app.get("/")
async def read_root():
    if not models:
        return {"message": "ERRO: Nenhum modelo de IA foi carregado. Verifique os arquivos .pkl."}
    
    loaded_langs = list(models.keys())
    return {
        "message": "Microserviço de Sentimento Multilíngue Online (FastAPI) está rodando!",
        "idiomas_carregados": loaded_langs,
        "idioma_padrao": DEFAULT_LANG
    }

@app.post("/predict", response_model=ModelOutput)
async def predict_sentiment(input: ModelInput):
    if not models:
         raise HTTPException(status_code=503, detail=f"Nenhum modelo de IA está disponível.")

    if not input.text or len(input.text) < 3:
        raise HTTPException(status_code=400, detail="O campo 'text' é obrigatório e deve ter pelo menos 3 caracteres.")

    # --- Detecção Automática de Idioma ---
    try:
        lang = detect(input.text)
        print(f"Idioma detectado: '{lang}'")
        # Se o idioma detectado não for um dos que temos modelo, usar o padrão
        if lang not in models:
            print(f"AVISO: Modelo para idioma detectado '{lang}' não disponível. Usando modelo padrão '{DEFAULT_LANG}'.")
            lang = DEFAULT_LANG
            
    except LangDetectException:
        print(f"AVISO: Não foi possível detectar o idioma. Usando modelo padrão '{DEFAULT_LANG}'.")
        lang = DEFAULT_LANG
    
    # Seleciona o modelo e vetorizador corretos
    model = models[lang]
    tfidf = tfidfs[lang]
    classes = model_classes[lang]

    # Vetoriza o texto de entrada
    text_vectorized = tfidf.transform([input.text])

    # Faz a predição da classe
    prediction_label = model.predict(text_vectorized)[0]

    # Obtém o array de probabilidades
    if hasattr(model, 'predict_proba'):
        probabilities = model.predict_proba(text_vectorized)[0]
        prob_per_class = dict(zip(classes, probabilities))
        predicted_probability = prob_per_class[prediction_label]
    else:
        print(f"ATENÇÃO: O modelo '{type(model).__name__}' para o idioma '{lang}' não possui 'predict_proba'. Usando probabilidade padrão de 0.5.")
        predicted_probability = 0.5

    return ModelOutput(previsao=prediction_label, probabilidade=float(predicted_probability))
