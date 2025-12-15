# -*- coding: utf-8 -*-
"""
Projeto: Análise de Sentimentos de Feedbacks (VERSÃO PORTUGUÊS)
Dataset: Olist E-Commerce Reviews
Modelo: Logistic Regression

Pipeline adaptado para treinar um modelo específico para o idioma português.
"""

import os
import re
import joblib
import pandas as pd

from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, accuracy_score

# ============================================================
# Carregamento do Dataset
# ============================================================
def carregar_dados(caminho: str) -> pd.DataFrame:
    """
    Carrega o dataset Olist e lida com possíveis erros de encoding.
    """
    try:
        return pd.read_csv(caminho, encoding="utf-8")
    except Exception as e:
        raise ValueError(f"Falha ao carregar o dataset: {e}")

# ============================================================
# Funções de pré-processamento (Adaptadas para o Olist)
# ============================================================
def normalizar_texto(text):
    """Converte para minúsculas e remove espaços extras."""
    text = str(text).lower()
    text = re.sub(r'\s+', ' ', text).strip()
    return text

def limpar_dados(df: pd.DataFrame) -> pd.DataFrame:
    """
    Executa o pipeline de limpeza para o dataset Olist.
    """
    # Seleciona colunas úteis e renomeia para padronização
    df = df[["review_score", "review_comment_title", "review_comment_message"]].copy()
    df.rename(columns={
        "review_score": "score",
        "review_comment_title": "title",
        "review_comment_message": "text"
    }, inplace=True)

    # Remove avaliações sem comentário
    df.dropna(subset=["text"], inplace=True)
    
    # Preenche títulos vazios com uma string vazia
    df["title"] = df["title"].fillna("")

    # Normaliza textos
    df["text"] = df["text"].apply(normalizar_texto)
    df["title"] = df["title"].apply(normalizar_texto)

    # Remove duplicidades de comentários
    df = df.drop_duplicates(subset=["text"])
    
    df = df[["score", "title", "text"]].reset_index(drop=True)
    return df

# ============================================================
# Sentiment Labeling (Mapeamento de Score para Sentimento)
# ============================================================
def gerar_sentimento(score):
    """Mapeia o score (1 a 5) para categorias de sentimento."""
    if score >= 4:
        return "Positivo"
    elif score == 3:
        return "Neutro"
    return "Negativo" # Scores 1 e 2

def rotular_sentimentos(df: pd.DataFrame) -> pd.DataFrame:
    """Aplica o mapeamento de sentimentos ao dataframe."""
    df["sentimento"] = df["score"].apply(gerar_sentimento)
    return df

# ============================================================
# Combinação de título + texto
# ============================================================
def criar_full_text(df: pd.DataFrame) -> pd.DataFrame:
    """Combina título e corpo do texto para criar um campo único."""
    df["full_text"] = df["title"] + " " + df["text"]
    return df

# ============================================================
# Separação treino/teste
# ============================================================
def separar_treino_teste(df: pd.DataFrame):
    """
    Separa o conjunto em treino e teste usando stratify para manter
    a proporção das classes de sentimento.
    """
    X = df["full_text"]
    y = df["sentimento"]
    
    return train_test_split(
        X,
        y,
        test_size=0.2,
        stratify=y,
        random_state=42
    )    

# ============================================================
# Treinamento do Modelo (TF-IDF + Logistic Regression para Português)
# ============================================================
def treinar_modelo_pt(X_train, X_test, y_train, y_test):
    """
    Executa vetorização TF-IDF com stop words em português e treina
    o modelo de Regressão Logística.
    """
    print("Iniciando vetorização TF-IDF para português...")
    tfidf = TfidfVectorizer(
        ngram_range=(1, 2),
        min_df=5,
        max_df=0.9,
        max_features=100_000,
        stop_words="portuguese", # <- Ponto CRÍTICO para o idioma
        strip_accents="unicode",
        sublinear_tf=True
    )
    X_train_tfidf = tfidf.fit_transform(X_train)
    X_test_tfidf = tfidf.transform(X_test)
    print("Vetorização concluída.")

    print("Iniciando treinamento do modelo LogisticRegression...")
    modelo = LogisticRegression(
        class_weight="balanced",
        random_state=42,
        max_iter=1000 # Aumentar iterações para garantir convergência
    )
    modelo.fit(X_train_tfidf, y_train)
    print("Treinamento concluído.")
    
    y_pred = modelo.predict(X_test_tfidf)

    print("\nAvaliação do modelo em Português:")
    print("Acurácia:", accuracy_score(y_test, y_pred))
    print(classification_report(y_test, y_pred, zero_division=0))

    return modelo, tfidf   

# ============================================================
# Salvamento dos artefatos (com nomes para PT)
# ============================================================
def salvar_modelo_pt(modelo, tfidf, pasta: str = "models_pt"):
    """Salva o modelo e o vetorizador com sufixo _pt."""
    os.makedirs(pasta, exist_ok=True)
    
    caminho_modelo = os.path.join(pasta, "model_pt.pkl")
    caminho_tfidf = os.path.join(pasta, "tfidf_pt.pkl")

    joblib.dump(modelo, caminho_modelo)
    joblib.dump(tfidf, caminho_tfidf)

    print(f"Modelo e TF-IDF para português salvos com sucesso em '{pasta}'.")

# ============================================================
# Execução principal (main)
# ============================================================
if __name__ == "__main__":
    # Constrói o caminho para o dataset relativo à localização do script
    try:
        script_dir = os.path.dirname(os.path.abspath(__file__))
        csv_path = os.path.join(script_dir, "olist_order_reviews_dataset.csv")
    except NameError:
        # Fallback para ambientes como o Google Colab
        csv_path = "olist_order_reviews_dataset.csv"

    print("Carregando dataset Olist...")
    df = carregar_dados(csv_path)

    print("\nLimpando e preparando dados em português...")
    df_limpo = limpar_dados(df)

    print("Criando rótulos de sentimento...")
    df_rotulado = rotular_sentimentos(df_limpo)

    print("Criando coluna full_text...")
    df_final = criar_full_text(df_rotulado)
    
    print("\nDistribuição dos sentimentos:")
    print(df_final["sentimento"].value_counts(normalize=True))

    print("\nSeparando treino e teste...")
    X_train, X_test, y_train, y_test = separar_treino_teste(df_final)

    print("\nTreinando modelo para Português...")
    modelo_pt, tfidf_pt = treinar_modelo_pt(X_train, X_test, y_train, y_test)

    print("\nSalvando artefatos do modelo PT...")
    salvar_modelo_pt(modelo_pt, tfidf_pt)

    print("\n\nPipeline para Português concluído com sucesso!")
