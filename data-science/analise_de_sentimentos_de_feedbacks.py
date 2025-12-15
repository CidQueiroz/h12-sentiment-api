# -*- coding: utf-8 -*-
"""
Projeto: Análise de Sentimentos de Feedbacks
Descrição:
Pipeline completo de limpeza, preparação, rotulagem de sentimentos,
treinamento de modelo de Machine Learning (TF-IDF + SVM)
e salvamento dos artefatos para uso em produção.

Este script está organizado em etapas modulares para facilitar
colaboração, versionamento e manutenção.

"""

import os
import re
import joblib
import pandas as pd

from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import SVC
from sklearn.metrics import classification_report, accuracy_score

# ============================================================
# Carregamento do Dataset
# ============================================================
def carregar_dados(caminho: str) -> pd.DataFrame:
    """
    Tenta carregar um CSV usando diferentes encodings.
    Evita erros comuns em bases grandes e heterogêneas.
    """
    encodings = ["utf-8", "latin1", "iso-8859-1"]
    for enc in encodings:
        try:
            return pd.read_csv(caminho, encoding=enc)
        except Exception:
            continue
    raise ValueError("Falha ao carregar o dataset. Verifique o arquivo e encoding.")

# ============================================================
# Análise inicial
# ============================================================
def analise_inicial(df: pd.DataFrame):
    """
    Exibe algumas métricas úteis antes de iniciar limpeza:
    - Dimensão
    - Valores nulos
    - Duplicidades
    - Distribuição dos ratings
    """
    print("Dimensão:", df.shape)
    print("\nValores nulos:\n", df.isna().sum())
    print("\nDuplicidades:", df.duplicated().sum())
    print("\nValores únicos de rating:", df["rating"].unique())

# ============================================================
# Funções de pré-processamento
# ============================================================
def remove_urls(text):
    """Remove URLs do texto para reduzir ruído."""
    return re.sub(r'http\S+|www\.\S+', '', str(text))

def normalizar_texto(text):
    """Converte para minúsculas e remove espaços extras."""
    text = str(text).lower()
    text = re.sub(r'\s+', ' ', text).strip()
    return text

def remove_emoji(text):
    """Remove emojis e caracteres unicode especiais."""
    emoji_pattern = re.compile(
        "[" 
        "\U0001F600-\U0001F64F" 
        "\U0001F300-\U0001F5FF" 
        "\U0001F680-\U0001F6FF" 
        "\U0001F1E0-\U0001F1FF"
        "]+",
        flags=re.UNICODE
    )
    return emoji_pattern.sub("", text)

def limpar_dados(df: pd.DataFrame) -> pd.DataFrame:
    """
    Executa o pipeline de limpeza:
    - Seleção de colunas úteis
    - Remoção de duplicidades
    - Remoção de textos curtos ou vazios
    - Normalização e remoção de ruído
    """
    df = df[["rating", "title", "text"]].copy()

    df = df.drop_duplicates()
    df = df.drop_duplicates(subset=["text"])

    df["text"] = df["text"].fillna("")

    df["text_length"] = df["text"].apply(lambda x: len(str(x)))
    df = df[df["text_length"] > 5]  # evita reviews irrelevantes

    df["text"] = df["text"].apply(remove_urls)
    df["text"] = df["text"].apply(normalizar_texto)
    df["text"] = df["text"].apply(remove_emoji)

    df = df[["rating", "title", "text"]].reset_index(drop=True)
    return df

# ============================================================
# Salvar base limpa
# ============================================================
def salvar_base_limpa(df: pd.DataFrame, caminho: str = "tabela_limpa_para_analise.csv"):
    """Salva a versão final da base pré-processada."""
    df.to_csv(caminho, index=False)
    print(f"Base limpa salva em: {caminho}")

# ============================================================
# Sentiment Labeling
# ============================================================
def gerar_sentimento(rating):
    """Mapeia rating numérico para categorias de sentimento."""
    if rating >= 4:
        return "Positivo"
    elif rating == 3:
        return "Neutro"
    return "Negativo"

def rotular_sentimentos(df: pd.DataFrame) -> pd.DataFrame:
    """Aplica o mapeamento de sentimentos ao dataframe."""
    df["sentimento"] = df["rating"].apply(gerar_sentimento)
    return df

# ============================================================
# Combinação de título + texto
# ============================================================
def criar_full_text(df: pd.DataFrame) -> pd.DataFrame:
    """Combina título e corpo do texto."""
    df["full_text"] = df["title"].astype(str) + " " + df["text"].astype(str)
    return df

# ============================================================
# Separação treino/teste
# ============================================================
def separar_treino_teste(df: pd.DataFrame):
    """
    Separa o conjunto em treino e teste usando stratify para manter
    proporção equilibrada entre as classes.
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
# Treinamento do Modelo (TF-IDF + SVM)
# ============================================================
def treinar_modelo(X_train, X_test, y_train, y_test):
    """
    Executa vetorização TF-IDF, treino do modelo SVM
    e avaliação de desempenho.
    """
    tfidf = TfidfVectorizer(
        ngram_range=(1, 2),
        min_df=5,
        max_df=0.9,
        max_features=100_000,
        stop_words="english",
        sublinear_tf=True,
        strip_accents="unicode",
    )

    X_train_tfidf = tfidf.fit_transform(X_train)
    X_test_tfidf = tfidf.transform(X_test)

    modelo = SVC(
        probability=True,
        class_weight="balanced",
        random_state=42,
    )

    modelo.fit(X_train_tfidf, y_train)
    y_pred = modelo.predict(X_test_tfidf)

    print("\nAvaliação do modelo:")
    print("Acurácia:", accuracy_score(y_test, y_pred))
    print(classification_report(y_test, y_pred))

    return modelo, tfidf   

# ============================================================
# Salvamento dos artefatos
# ============================================================
def salvar_modelo(modelo, tfidf, pasta: str = "models"):
    """Salva o modelo treinado e o TF-IDF."""
    os.makedirs(pasta, exist_ok=True)

    joblib.dump(tfidf, f"{pasta}/tfidf.pkl")
    joblib.dump(modelo, f"{pasta}/svm_sentiment_model.pkl")

    print("Modelo e TF-IDF salvos com sucesso.")

# ============================================================
# Execução principal (main)
# ============================================================
if __name__ == "__main__":
    # Constrói o caminho para o dataset relativo à localização do script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    csv_path = os.path.join(script_dir, "amazon-fashion-800k+-user-reviews-dataset.csv")

    print("Carregando dataset...")
    df = carregar_dados(csv_path)

    print("Análise inicial...")
    analise_inicial(df)

    print("\nLimpando dados...")
    df = limpar_dados(df)

    print("\nSalvando versão limpa...")
    salvar_base_limpa(df)

    print("Criando rótulos de sentimento...")
    df = rotular_sentimentos(df)

    print("Criando coluna full_text...")
    df = criar_full_text(df)

    print("Separando treino e teste...")
    X_train, X_test, y_train, y_test = separar_treino_teste(df)

    print("Treinando modelo SVM...")
    modelo, tfidf = treinar_modelo(X_train, X_test, y_train, y_test)

    print("Salvando artefatos...")
    salvar_modelo(modelo, tfidf)

    print("\nPipeline concluído com sucesso!")
