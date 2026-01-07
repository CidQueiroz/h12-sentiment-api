import pandas as pd
import re, os, pickle, nltk
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.svm import LinearSVC
from sklearn.model_selection import train_test_split

def train_suite():
    nltk.download('stopwords')
    output_path = "microservice/models"
    os.makedirs(output_path, exist_ok=True)
    
    # Carregamento e limpeza básica
    df = pd.read_csv('data-science/olist_order_reviews_dataset.csv')
    df['text'] = (df['review_comment_title'].fillna('') + ' ' + df['review_comment_message'].fillna('')).str.lower()
    df['sentiment'] = df['review_score'].apply(lambda x: 'positive' if x >= 4 else ('negative' if x <= 2 else 'neutral'))
    df = df[df['text'].str.strip() != '']

    langs = {'pt': 'portuguese', 'en': 'english', 'es': 'spanish'}

    for code, name in langs.items():
        print(f"--- Treinando Suite {code.upper()} ---")
        stop_words = list(set(stopwords.words(name)))
        X_train, _, y_train, _ = train_test_split(df['text'], df['sentiment'], test_size=0.2, random_state=42)

        # 1. TF-IDF único por idioma
        tfidf = TfidfVectorizer(stop_words=stop_words, max_features=5000)
        X_tfidf = tfidf.fit_transform(X_train)
        pickle.dump(tfidf, open(f"{output_path}/tfidf_{code}.pkl", "wb"))

        # 2. Naive Bayes (Velocidade)
        nb = MultinomialNB()
        nb.fit(X_tfidf, y_train)
        pickle.dump(nb, open(f"{output_path}/nb_model_{code}.pkl", "wb"))

        # 3. SVM (Robustez)
        svm = LinearSVC(max_iter=2000)
        svm.fit(X_tfidf, y_train)
        pickle.dump(svm, open(f"{output_path}/svm_model_{code}.pkl", "wb"))

    print("✅ Suite de modelos completa em microservice/models/")

if __name__ == "__main__":
    train_suite()
