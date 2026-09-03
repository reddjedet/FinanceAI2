#!/usr/bin/env python3
"""
Pipeline de entrenamiento automatizado (CLI) con versionado semántico para FinanceAI v2.0.
Entrena el clasificador NLP (11 categorías) y el perfilador financiero sensible al costo.
"""
import os
import json
import time
import joblib
import pandas as pd
import numpy as np
from datetime import datetime, timezone
from pathlib import Path

from sklearn.pipeline import Pipeline, FeatureUnion
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.calibration import CalibratedClassifierCV
from sklearn.model_selection import GridSearchCV, PredefinedSplit
from sklearn.metrics import f1_score
from sklearn.ensemble import HistGradientBoostingClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.compose import ColumnTransformer

VERSION = "2.0.0"
BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR.parent.parent / "notebooks" / "data"
ARTIFACTS_DIR = BASE_DIR / "app" / "artifacts"
ARTIFACTS_DIR.mkdir(parents=True, exist_ok=True)

DOMAIN_STOPWORDS = [
    'de', 'la', 'el', 'en', 'y', 'a', 'los', 'del', 'las', 'por', 'un', 'para',
    'con', 'no', 'una', 'su', 'al', 'lo', 'como', 'mas', 'pero', 'sus', 'le',
    'ya', 'o', 'este', 'si', 'porque', 'esta', 'son', 'entre', 'esta', 'sobre',
    'compra', 'compras', 'pago', 'pagos', 'debito', 'automatico', 'db', 'aut',
    'trf', 'transferencia', 'pos', 'estab', 'cupon', 'suc', 'online', 'fact'
]

import re
import unicodedata

def limpiar_texto(texto: str) -> str:
    if not isinstance(texto, str):
        return ""
    texto = texto.lower().strip()
    texto = unicodedata.normalize('NFKD', texto).encode('ASCII', 'ignore').decode('utf-8')
    texto = re.sub(r'[^a-zA-Z0-9\s]', ' ', texto)
    texto = re.sub(r'\s+', ' ', texto).strip()
    return texto

def train_nlp_model(df_t: pd.DataFrame):
    print("--- Entrenando Clasificador NLP (11 categorías) ---")
    df_t['desc_limpia'] = df_t['descripcion'].apply(limpiar_texto)
    
    df_train_val = df_t[df_t['split'].isin(['train', 'val'])].copy()
    X_tv = df_train_val['desc_limpia'].values
    y_tv = df_train_val['categoria'].values
    
    split_indices = [-1 if s == 'train' else 0 for s in df_train_val['split']]
    pds = PredefinedSplit(test_fold=split_indices)
    
    union_features = FeatureUnion([
        ('word_tfidf', TfidfVectorizer(
            analyzer='word', ngram_range=(1, 2), stop_words=DOMAIN_STOPWORDS,
            sublinear_tf=True, max_features=2500, min_df=2
        )),
        ('char_tfidf', TfidfVectorizer(
            analyzer='char_wb', ngram_range=(3, 5),
            sublinear_tf=True, max_features=5000, min_df=3
        ))
    ])
    
    base_svc = LinearSVC(class_weight='balanced', random_state=42)
    calibrated_clf = CalibratedClassifierCV(estimator=base_svc, method='sigmoid', cv=3)
    
    nlp_pipeline = Pipeline([
        ('features', union_features),
        ('clf', calibrated_clf)
    ])
    
    param_grid = {'clf__estimator__C': [0.1, 0.5, 1.0, 2.0]}
    grid = GridSearchCV(nlp_pipeline, param_grid, cv=pds, scoring='f1_macro', n_jobs=-1)
    grid.fit(X_tv, y_tv)
    
    best_model = grid.best_estimator_
    best_C = grid.best_params_['clf__estimator__C']
    print(f"Mejor parámetro C seleccionado: {best_C}")
    
    # Evaluación sobre conjunto de Test (Out-of-Time)
    df_test = df_t[df_t['split'] == 'test']
    X_test = df_test['desc_limpia'].values
    y_test = df_test['categoria'].values
    y_pred = best_model.predict(X_test)
    
    macro_f1 = f1_score(y_test, y_pred, average='macro')
    print(f"F1-Macro en Test OOT: {macro_f1:.4f}")
    
    model_path = ARTIFACTS_DIR / "transaction_classifier.joblib"
    joblib.dump(best_model, model_path)
    print(f"Modelo NLP serializado en {model_path}")
    
    return float(macro_f1), list(best_model.classes_)

def train_profiler_model(df_u: pd.DataFrame):
    print("--- Entrenando Perfilador Financiero Sensible al Costo ---")
    mapeo_ahorro = {'ALTA': 3, 'MEDIA': 2, 'BAJA': 1, 'NINGUNA': 0}
    df_u['ahorro_num'] = df_u['frecuencia_ahorro'].map(mapeo_ahorro)
    
    X = df_u[['ingreso_mensual', 'nivel_endeudamiento', 'ahorro_num']]
    y = df_u['perfil_financiero']
    
    preprocessor = ColumnTransformer(
        transformers=[('num', StandardScaler(), ['ingreso_mensual', 'nivel_endeudamiento', 'ahorro_num'])],
        remainder='passthrough'
    )
    
    pipe = Pipeline([
        ('prep', preprocessor),
        ('clf', HistGradientBoostingClassifier(random_state=42, max_iter=100))
    ])
    
    pesos_costo = {'En riesgo': 2.5, 'En observacion': 1.0, 'Saludable': 1.0}
    sample_weights = df_u['perfil_financiero'].map(pesos_costo).values
    
    pipe.fit(X, y, clf__sample_weight=sample_weights)
    
    y_pred = pipe.predict(X)
    f1 = f1_score(y, y_pred, average='weighted')
    print(f"F1-Weighted Perfilador: {f1:.4f}")
    
    model_path = ARTIFACTS_DIR / "financial_profile_classifier.joblib"
    joblib.dump(pipe, model_path)
    print(f"Modelo de Perfilado serializado en {model_path}")
    
    return float(f1)

def main():
    start_time = time.time()
    df_t = pd.read_csv(DATA_DIR / "transacciones.csv")
    df_u = pd.read_csv(DATA_DIR / "usuarios.csv")
    
    nlp_f1, classes = train_nlp_model(df_t)
    prof_f1 = train_profiler_model(df_u)
    
    metadata = {
        "version": VERSION,
        "trained_at": datetime.now(timezone.utc).isoformat(),
        "training_duration_seconds": round(time.time() - start_time, 2),
        "transaction_classifier": {
            "num_classes": len(classes),
            "classes": classes,
            "f1_macro_test": round(nlp_f1, 4)
        },
        "financial_profiler": {
            "f1_weighted": round(prof_f1, 4),
            "cost_sensitive_weight_risk": 2.5
        }
    }
    
    metadata_path = ARTIFACTS_DIR / "metadata.json"
    with open(metadata_path, 'w', encoding='utf-8') as f:
        json.dump(metadata, f, indent=2, ensure_ascii=False)
    
    print(f"Metadatos de versionado semántico exportados en {metadata_path}")
    print(f"Pipeline completado exitosamente en {metadata['training_duration_seconds']}s.")

if __name__ == '__main__':
    main()
