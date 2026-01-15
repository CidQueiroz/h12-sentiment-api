# -*- coding: utf-8 -*-
import pandas as pd
import re
import joblib
import nltk
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import MultinomialNB
from sklearn.svm import LinearSVC
from sklearn.model_selection import train_test_split
import os

# --- DOWNLOAD NLTK STOPWORDS ---
try:
    stopwords.words('portuguese')
except LookupError:
    print("Downloading NLTK stopwords for Portuguese...")
    nltk.download('stopwords')

# --- DEFINIÇÕES GLOBAIS ---
DATA_PATH = 'data-science/olist_order_reviews_dataset.csv'
MODELS_OUTPUT_DIR = 'microservice/models/'
COLUMNS_TO_USE = ['review_comment_message', 'review_score']
TEXT_COLUMN = 'review_comment_message'
TARGET_COLUMN = 'review_score'

# --- FUNÇÃO DE LIMPEZA ---
def clean_text(text):
    """
    Limpeza de texto validada e alinhada com a API.
    Mantém acentos, remove URLs, caracteres não alfanuméricos (exceto acentos) e espaços extras.
    """
    if not isinstance(text, str):
        return ""
    text = text.lower()
    text = re.sub(r'http\S+|www\S+', '', text)
    text = re.sub(r'[^a-zà-ÿáéíóúãõñç\s]', '', text)
    text = re.sub(r'\s+', ' ', text).strip()
    return text

# --- PIPELINE PRINCIPAL ---
def run_training_pipeline():
    """
    Executa o pipeline completo de Carga, Transformação, Treinamento e Exportação.
    """
    print("--- INICIANDO PIPELINE DE TREINAMENTO (DOMAIN ADAPTATION) ---")

    # 1. CARGA DE DADOS
    print(f"1/5: Carregando dados de '{DATA_PATH}'...")
    df = pd.read_csv(DATA_PATH, usecols=COLUMNS_TO_USE)
    df.dropna(subset=[TEXT_COLUMN], inplace=True)
    print(f"     -> Dados carregados. Shape inicial: {df.shape}")

    # 2. TRANSFORMAÇÃO DE TARGET
    print("2/5: Transformando scores em labels binários (0/1)...")
    df = df[df[TARGET_COLUMN] != 3]
    df['sentiment'] = df[TARGET_COLUMN].apply(lambda x: 1 if x >= 4 else 0)
    # Shape após remover neutros
    print(f"     -> Scores neutros (3) removidos. Shape: {df.shape}")
    print(f"     -> Distribuição de classes:\n{df['sentiment'].value_counts(normalize=True)}")

    # 3. PRÉ-PROCESSAMENTO
    print("3/5: Aplicando limpeza de texto e vetorização TF-IDF...")
    df['cleaned_text'] = df[TEXT_COLUMN].apply(clean_text)
    
    portuguese_stopwords = stopwords.words('portuguese')
    
    vectorizer = TfidfVectorizer(
        max_features=5000,
        stop_words=portuguese_stopwords,
        ngram_range=(1, 2)
    )

    X = vectorizer.fit_transform(df['cleaned_text'])
    y = df['sentiment']
    
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
    print("     -> Vetorização concluída. Features: 5000")

    # 4. TREINAMENTO
    print("4/5: Treinando modelos...")
    models_to_train = {
        'lr': LogisticRegression(max_iter=1000, random_state=42),
        'nb': MultinomialNB(),
        'svm': LinearSVC(random_state=42)
    }
    
    trained_models = {}
    for name, model in models_to_train.items():
        print(f"     -> Treinando {name.upper()}...")
        model.fit(X_train, y_train)
        score = model.score(X_test, y_test)
        print(f"     -> Acurácia {name.upper()}: {score:.4f}")
        trained_models[name] = model

    # 5. EXPORTAÇÃO
    print(f"5/5: Exportando artefatos para '{MODELS_OUTPUT_DIR}'...")
    
    # Garante que o diretório de saída existe
    os.makedirs(MODELS_OUTPUT_DIR, exist_ok=True)

    # Nomes de arquivo padronizados ("Golden Path")
    joblib.dump(vectorizer, os.path.join(MODELS_OUTPUT_DIR, 'tfidf_pt.pkl'))
    joblib.dump(trained_models['lr'], os.path.join(MODELS_OUTPUT_DIR, 'lr_model_pt.pkl'))
    joblib.dump(trained_models['nb'], os.path.join(MODELS_OUTPUT_DIR, 'nb_model_pt.pkl'))
    joblib.dump(trained_models['svm'], os.path.join(MODELS_OUTPUT_DIR, 'svm_model_pt.pkl'))
    
    print("--- PIPELINE CONCLUÍDO ---")
    print("Os seguintes arquivos foram salvos com sucesso:")
    for item in os.listdir(MODELS_OUTPUT_DIR):
        if 'pt.pkl' in item:
            print(f"  - {item}")

if __name__ == "__main__":
    run_training_pipeline()