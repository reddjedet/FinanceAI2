import os
import re
import unicodedata
import joblib
from typing import Tuple, Dict
from app.core.config import settings

def limpiar_texto(texto: str) -> str:
    if not isinstance(texto, str):
        return ""
    texto = texto.lower().strip()
    texto = unicodedata.normalize('NFKD', texto).encode('ASCII', 'ignore').decode('utf-8')
    texto = re.sub(r'[^a-zA-Z0-9\s]', ' ', texto)
    texto = re.sub(r'\s+', ' ', texto).strip()
    return texto

class TransactionClassifierService:
    def __init__(self):
        self.model = None
        self.load_model()

    def load_model(self):
        model_path = os.path.join(settings.ARTIFACTS_DIR, "transaction_classifier.joblib")
        if os.path.exists(model_path):
            self.model = joblib.load(model_path)
            print(f"Modelo de clasificación cargado desde {model_path}")
        else:
            print(f"Advertencia: Modelo no encontrado en {model_path}. Se usarán reglas de respaldo.")

    @property
    def is_loaded(self) -> bool:
        return self.model is not None

    def predict(self, description: str) -> Tuple[str, float, Dict[str, float]]:
        cleaned = limpiar_texto(description)
        if not cleaned:
            return "Ocio", 0.0, {}

        if self.model is None:
            raise RuntimeError("El modelo de clasificación de transacciones no se encuentra cargado en el servidor.")

        probas = self.model.predict_proba([cleaned])[0]
        classes = self.model.classes_
        max_idx = probas.argmax()
        best_cat = str(classes[max_idx])
        best_score = float(probas[max_idx])
        score_dict = {str(c): round(float(p), 4) for c, p in zip(classes, probas)}
        return best_cat, round(best_score, 4), score_dict

classifier_service = TransactionClassifierService()

