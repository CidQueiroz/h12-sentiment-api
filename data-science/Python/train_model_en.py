import pandas as pd
import re
import time
import joblib
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.linear_model import LogisticRegression
from sklearn.svm import LinearSVC
from sklearn.metrics import classification_report

def limpar_texto_en(texto):
    """Limpeza padronizada para textos em Inglês."""
    if pd.isna(texto): return ""
    texto = str(texto).lower()
    texto = re.sub(r'<br\s*/?>', ' ', texto) # Remove tags HTML
    texto = re.sub(r'[^a-z\s]', '', texto)    # Mantém apenas letras de A-Z
    texto = re.sub(r'\s+', ' ', texto).strip() # Remove espaços extras
    return texto

# 1. Carregamento
df = pd.read_csv('dataset_mestre_v2_unificado.csv')
df['sentimento'] = df['sentimento'].str.strip()

# 2. Filtragem Binária (Remove Neutros)
df_bin = df[df['sentimento'].isin(['Positivo', 'Negativo'])].copy()

# 3. Downsampling (Equilíbrio 50/50)
menor_classe = df_bin['sentimento'].value_counts().min()
df_pos = df_bin[df_bin['sentimento'] == 'Positivo'].sample(n=menor_classe, random_state=42)
df_neg = df_bin[df_bin['sentimento'] == 'Negativo'].sample(n=menor_classe, random_state=42)

df_final = pd.concat([df_pos, df_neg]).sample(frac=1, random_state=42).reset_index(drop=True)

print(f"Base balanceada: {df_final['sentimento'].value_counts().to_dict()}")

X = df_final['text_clean'].astype(str)
y = df_final['sentimento']

# Divisão 80/20
X_train_raw, X_test_raw, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

# Vetorização
vectorizer_en = TfidfVectorizer(ngram_range=(1, 2), max_features=100000)
X_train = vectorizer_en.fit_transform(X_train_raw)
X_test = vectorizer_en.transform(X_test_raw)

# Salvar o vetorizador imediatamente (essencial para o novo desafio em PT depois)
joblib.dump(vectorizer_en, 'vectorizer_en.pkl')

def treinar_e_avaliar(nome, modelo, X_tr, y_tr, X_te, y_te):
    t_inicio = time.process_time()
    modelo.fit(X_tr, y_tr)
    t_fim = time.process_time()

    y_pred = modelo.predict(X_te)
    acc = (y_pred == y_te).mean() * 100

    print(f"\n--- {nome} ---")
    print(f"Tempo CPU: {t_fim - t_inicio:.4f}s | Acurácia: {acc:.2f}%")
    return modelo, acc

# Instanciando os modelos
models_to_train = {
    "Naive Bayes": MultinomialNB(),
    "Logistic Regression": LogisticRegression(solver='sag', max_iter=500, n_jobs=-1),
    "Linear SVC": LinearSVC(C=1.0, max_iter=1000, random_state=42)
}

# Dicionário para armazenar modelos treinados
trained_models = {}

for nome, m in models_to_train.items():
    trained_models[nome], _ = treinar_e_avaliar(nome, m, X_train, y_train, X_test, y_test)

# Mapeamento conforme solicitado
joblib.dump(trained_models["Naive Bayes"], 'nb_model_en.pkl')
joblib.dump(trained_models["Logistic Regression"], 'lr_model_en.pkl')
joblib.dump(trained_models["Linear SVC"], 'lsvc_model_en.pkl')

print("✅ Todos os modelos foram salvos com a nomenclatura padrão '_en.pkl'")

def laboratório_comparativo(frases, vetorizador, modelos_dict):
    # 1. Preparação das frases
    frases_limpas = [limpar_texto_en(f) for f in frases]
    X_input = vetorizador.transform(frases_limpas)

    # 2. Cabeçalho Dinâmico
    header = f"{ 'FRASE PARA TESTE':<45} | {'NB':<10} | {'LOG':<10} | {'SVC':<10} | {'CONF. LOG'}"
    print(header)
    print("-" * len(header))

    # 3. Coleta de Predições
    # Assumindo que os nomes no trained_models são exatamente esses
    pred_nb = modelos_dict["Naive Bayes"].predict(X_input)
    pred_log = modelos_dict["Logistic Regression"].predict(X_input)
    pred_svc = modelos_dict["Linear SVC"].predict(X_input)

    # Probabilidade apenas para Logística (confiança)
    prob_log = modelos_dict["Logistic Regression"].predict_proba(X_input)

    # 4. Exibição dos resultados
    for i, frase in enumerate(frases):
        p_nb  = pred_nb[i]
        p_log = pred_log[i]
        p_svc = pred_svc[i]
        conf  = max(prob_log[i]) * 100

        # Formatação para não quebrar a tabela
        frase_display = (frase[:42] + '..') if len(frase) > 42 else frase

        print(f"{frase_display:<45} | {p_nb:<10} | {p_log:<10} | {p_svc:<10} | {conf:.2f}%")

# --- LISTA DE TESTES (Desafios Linguísticos) ---
testes_reais = [
    "Looked great. Nice msterial.",              # Erro de digitação positivo
    "very thin msterial and uncomfortable",      # Erro de digitação negativo
    "The size is too small but I loved the color", # Contraste (Ambiguidade)
    "it is ok, nothing special but works",       # Quase neutro
    "worst purchase ever, waste of money",       # Negativo forte
    "I expected more for this price",            # Negativo implícito
    "Fast shipping, but product arrived broken"  # Conflito logística vs produto
]

# Execução
laboratório_comparativo(testes_reais, vectorizer_en, trained_models)

import matplotlib.pyplot as plt
import seaborn as sns

# 1. Preparação dos Dados para o Gráfico
algoritmos = list(trained_models.keys())
acuracias = [91.10, 92.00, 92.00] # Valores obtidos no benchmark binário
tempos = [1.64, 45.46, 31.85]    # Tempos de CPU registrados

fig, ax1 = plt.subplots(figsize=(12, 6))

# 2. Gráfico de Barras (Acurácia)
sns.barplot(x=algoritmos, y=acuracias, ax=ax1, palette='viridis', alpha=0.7)
ax1.set_ylabel('Acurácia (%)', fontsize=12, fontweight='bold')
ax1.set_ylim(85, 95)
ax1.set_title('Benchmarking Final: Performance vs. Tempo (Idioma: EN)', fontsize=15)

# Adicionando rótulos de acurácia nas barras
for p in ax1.patches:
    ax1.annotate(f'{p.get_height():.2f}%', (p.get_x() + p.get_width() / 2., p.get_height()),
                ha='center', va='center', xytext=(0, 9), textcoords='offset points', fontweight='bold')

# 3. Gráfico de Linha (Tempo de CPU) - Eixo Secundário
ax2 = ax1.twinx()
sns.lineplot(x=algoritmos, y=tempos, ax=ax2, color='red', marker='o', linewidth=3)
ax2.set_ylabel('Tempo de CPU (Segundos)', fontsize=12, color='red', fontweight='bold')

plt.grid(alpha=0.3)
plt.show()
