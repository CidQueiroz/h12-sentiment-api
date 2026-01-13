import pandas as pd
import re
import nltk
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import MultinomialNB
from sklearn.svm import LinearSVC
from sklearn.linear_model import LogisticRegression
import pickle
import argparse

def train_and_save_model(language, model_type):
    print(f"Training {model_type} model for {language}...")

    # Baixar stopwords
    nltk.download('stopwords')
    if language == 'en':
        stopwords_list = set(stopwords.words('english'))
    elif language == 'es':
        stopwords_list = set(stopwords.words('spanish'))
    elif language == 'pt':
        stopwords_list = set(stopwords.words('portuguese'))
    else:
        raise ValueError("Language not supported")

    # Carregar os dados
    try:
        df = pd.read_csv('./data-science/olist_order_reviews_dataset.csv', encoding='utf-8')
    except FileNotFoundError:
        print("Arquivo olist_order_reviews_dataset.csv não encontrado. Certifique-se de que ele está no diretório 'data-science'")
        return

    # Renomear colunas
    df.rename(columns={
        'review_score': 'rating',
        'review_comment_message': 'text',
        'review_comment_title': 'title'
    }, inplace=True)

    if 'title' not in df.columns:
        df['title'] = None

    df = df[['rating', 'title', 'text']]

    # Limpeza dos dados
    df['text'] = df['text'].fillna('')
    df['title'] = df['title'].fillna('')
    df['full_text'] = df['title'] + ' ' + df['text']

    def clean_text(text):
        text = text.lower()
        text = re.sub(r'http\S+|www\.\S+', '', text)
        text = re.sub(r'[^\w\s]', '', text)
        text = re.sub(r'\s+', ' ', text).strip()
        return text

    df['full_text'] = df['full_text'].apply(clean_text)

    # Remover linhas com texto vazio
    df = df[df['full_text'] != '']

    # Criar a coluna 'sentiment'
    def to_sentiment(rating):
        if rating >= 4:
            return 'positive'
        elif rating <= 2:
            return 'negative'
        else:
            return 'neutral'

    df['sentiment'] = df['rating'].apply(to_sentiment)

    # Manter apenas as colunas necessárias
    df = df[['full_text', 'sentiment']]

    # Dividir os dados
    X_train, X_test, y_train, y_test = train_test_split(
        df['full_text'],
        df['sentiment'],
        test_size=0.2,
        random_state=42,
        stratify=df['sentiment']
    )

    # TF-IDF Vectorizer
    tfidf_vectorizer = TfidfVectorizer(stop_words=list(stopwords_list), min_df=5)
    X_train_tfidf = tfidf_vectorizer.fit_transform(X_train)

    # Caminho para salvar os modelos
    model_path = './microservice/models/'
    
    # Treinar e salvar o modelo
    if model_type == 'svm':
        model = LinearSVC()
        model_name = f'svm_model_{language}.pkl'
    elif model_type == 'nb':
        model = MultinomialNB()
        model_name = f'nb_model_{language}.pkl'
    elif model_type == 'lr':
        model = LogisticRegression(max_iter=1000)
        model_name = f'lr_model_{language}.pkl'
    else:
        raise ValueError("Model type not supported")

    model.fit(X_train_tfidf, y_train)
    with open(model_path + model_name, 'wb') as f:
        pickle.dump(model, f)
    print(f"{model_type} model for {language} saved as {model_name}")

    # Salvar o vetorizador
    tfidf_vectorizer_name = f'tfidf_{language}.pkl'
    with open(model_path + tfidf_vectorizer_name, 'wb') as f:
        pickle.dump(tfidf_vectorizer, f)
    print(f"TF-IDF vectorizer for {language} saved as {tfidf_vectorizer_name}")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Train sentiment analysis models.")
    parser.add_argument('--language', type=str, required=True, choices=['en', 'es', 'pt'], help='Language of the model')
    parser.add_argument('--model_type', type=str, required=True, choices=['svm', 'nb', 'lr'], help='Type of model to train (svm, nb, or lr)')
    args = parser.parse_args()
    
    train_and_save_model(args.language, args.model_type)

    print("Model training finished.")
