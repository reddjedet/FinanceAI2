"""
Pipeline de entrenamiento avanzado de Machine Learning para FinanceAI v2.
Implementa FeatureUnion (palabras + caracteres subpalabra) y LinearSVC calibrado
para clasificación robusta de transacciones, junto con Gradient Boosting para salud financiera.
"""
import os
import re
import unicodedata
import numpy as np
import pandas as pd
import joblib

from sklearn.pipeline import Pipeline, FeatureUnion
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.calibration import CalibratedClassifierCV
from sklearn.ensemble import HistGradientBoostingClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import classification_report, accuracy_score

ARTIFACTS_DIR = os.path.join(os.path.dirname(__file__), "app", "artifacts")
DATA_DIR = os.path.join(os.path.dirname(__file__), "data")
os.makedirs(ARTIFACTS_DIR, exist_ok=True)

DOMAIN_STOPWORDS = [
    'de', 'la', 'el', 'en', 'y', 'a', 'los', 'del', 'las', 'por', 'un', 'para',
    'con', 'no', 'una', 'su', 'al', 'lo', 'como', 'mas', 'pero', 'sus', 'le',
    'ya', 'o', 'este', 'si', 'porque', 'esta', 'son', 'entre', 'esta', 'sobre',
    'compra', 'compras', 'pago', 'pagos', 'debito', 'automatico', 'db', 'aut',
    'trf', 'transferencia', 'pos', 'estab', 'cupon', 'suc', 'online', 'fact'
]

def limpiar_texto(texto: str) -> str:
    if not isinstance(texto, str):
        return ""
    texto = texto.lower().strip()
    texto = unicodedata.normalize('NFKD', texto).encode('ASCII', 'ignore').decode('utf-8')
    texto = re.sub(r'[^a-zA-Z0-9\s]', ' ', texto)
    texto = re.sub(r'\s+', ' ', texto).strip()
    return texto

def entrenar_clasificador_transacciones():
    print("=== Entrenando Clasificador Híbrido NLP de Transacciones ===")
    csv_path = os.path.join(DATA_DIR, "transacciones.csv")
    
    if os.path.exists(csv_path):
        df = pd.read_csv(csv_path)
        train_df = df[df['split'] == 'train'] if 'split' in df.columns else df
        test_df = df[df['split'] == 'test'] if 'split' in df.columns else df
        
        train_df = train_df.dropna(subset=['descripcion', 'categoria'])
        test_df = test_df.dropna(subset=['descripcion', 'categoria'])
        
        X_train = train_df['descripcion'].apply(limpiar_texto)
        y_train = train_df['categoria']
        X_test = test_df['descripcion'].apply(limpiar_texto)
        y_test = test_df['categoria']
    else:
        raise FileNotFoundError(f"No se encontró el dataset en {csv_path}")

    # Pipeline Híbrido: FeatureUnion de Palabras + Caracteres
    union_features = FeatureUnion([
        ('word_tfidf', TfidfVectorizer(
            analyzer='word',
            ngram_range=(1, 2),
            stop_words=DOMAIN_STOPWORDS,
            min_df=2,
            sublinear_tf=True
        )),
        ('char_tfidf', TfidfVectorizer(
            analyzer='char_wb',
            ngram_range=(3, 5),
            min_df=3,
            sublinear_tf=True
        ))
    ])

    base_svc = LinearSVC(C=1.0, class_weight='balanced', random_state=42)
    calibrated_clf = CalibratedClassifierCV(estimator=base_svc, method='sigmoid', cv=3)

    nlp_pipeline = Pipeline([
        ('features', union_features),
        ('clf', calibrated_clf)
    ])

    print("Ajustando pipeline en conjunto de entrenamiento (Out-of-Time split)...")
    nlp_pipeline.fit(X_train, y_train)

    # Evaluación en Test ciego
    y_pred = nlp_pipeline.predict(X_test)
    acc = accuracy_score(y_test, y_pred)
    print(f"Exactitud (Accuracy) en conjunto de prueba independiente: {acc * 100:.2f}%")
    print("\nReporte de Clasificación:")
    print(classification_report(y_test, y_pred, digits=4))

    # Prueba de robustez ante abreviaturas y errores tipográficos (typos)
    pruebas_robustez = [
        "cot0 suc 14 alimentos",
        "carrefur express compras",
        "edn0r factura luz",
        "trf iol invrtironline",
        "farmacty remedios",
        "zara rpa indumentaria",
        "alquiler dpto palermo",
        "univ uba cuota matricula"
    ]
    print("\nValidación de robustez ante textos con ruido:")
    for prueba in pruebas_robustez:
        clean = limpiar_texto(prueba)
        cat = nlp_pipeline.predict([clean])[0]
        probas = nlp_pipeline.predict_proba([clean])[0]
        conf = probas.max()
        print(f"  Input con ruido: '{prueba}' -> Categoría predicha: '{cat}' ({conf * 100:.1f}%)")

    modelo_path = os.path.join(ARTIFACTS_DIR, "transaction_classifier.joblib")
    joblib.dump(nlp_pipeline, modelo_path)
    print(f"\nModelo NLP guardado en: {modelo_path}")

def entrenar_clasificador_perfil():
    print("\n=== Entrenando Evaluador de Salud Financiera (Gradient Boosting) ===")
    csv_path = os.path.join(DATA_DIR, "usuarios.csv")
    
    if os.path.exists(csv_path):
        df = pd.read_csv(csv_path)
        mapeo_ahorro = {'NINGUNA': 0.0, 'BAJA': 1.0, 'MEDIA': 2.0, 'ALTA': 3.0, 'Ninguna': 0.0, 'Baja': 1.0, 'Media': 2.0, 'Alta': 3.0}
        df['ahorro_num'] = df['frecuencia_ahorro'].map(mapeo_ahorro).fillna(0.0)
        
        train_df = df[df['split'] == 'train'] if 'split' in df.columns else df
        test_df = df[df['split'] == 'test'] if 'split' in df.columns else df
        
        X_train = train_df[['ingreso_mensual', 'nivel_endeudamiento', 'ahorro_num']].values
        y_train = train_df['perfil_financiero'].values
        X_test = test_df[['ingreso_mensual', 'nivel_endeudamiento', 'ahorro_num']].values
        y_test = test_df['perfil_financiero'].values
    else:
        raise FileNotFoundError(f"No se encontró usuarios.csv en {csv_path}")

    pipeline = Pipeline([
        ('scaler', StandardScaler()),
        ('clf', HistGradientBoostingClassifier(random_state=42, max_iter=150, max_leaf_nodes=31))
    ])

    pipeline.fit(X_train, y_train)

    y_pred = pipeline.predict(X_test)
    acc = accuracy_score(y_test, y_pred)
    print(f"Exactitud (Accuracy) del perfilador en prueba: {acc * 100:.2f}%")
    print(classification_report(y_test, y_pred, digits=4))

    modelo_path = os.path.join(ARTIFACTS_DIR, "financial_profile_classifier.joblib")
    joblib.dump(pipeline, modelo_path)
    print(f"Modelo de perfil guardado en: {modelo_path}")

if __name__ == "__main__":
    entrenar_clasificador_transacciones()
    entrenar_clasificador_perfil()
    print("\n¡Entrenamiento y exportación de nuevos modelos completado!")
