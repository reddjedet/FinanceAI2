import os
import joblib
import numpy as np
from typing import Tuple
from app.core.config import settings
from app.schemas.ml import SavingFrequencyEnum

FRECUENCIA_MAP = {
    SavingFrequencyEnum.ALTA: 3.0,
    SavingFrequencyEnum.MEDIA: 2.0,
    SavingFrequencyEnum.BAJA: 1.0,
    SavingFrequencyEnum.NINGUNA: 0.0
}

class FinancialProfilerService:
    def __init__(self):
        self.model = None
        self.load_model()

    def load_model(self):
        model_path = os.path.join(settings.ARTIFACTS_DIR, "financial_profile_classifier.joblib")
        if os.path.exists(model_path):
            self.model = joblib.load(model_path)
            print(f"Modelo de perfil cargado desde {model_path}")
        else:
            print(f"Advertencia: Modelo no encontrado en {model_path}. Se aplicarán reglas expertas.")

    @property
    def is_loaded(self) -> bool:
        return self.model is not None

    def evaluate(self, income: float, debt_percentage: float, saving_freq: SavingFrequencyEnum) -> Tuple[str, float, str, str]:
        saving_num = FRECUENCIA_MAP.get(saving_freq, 0.0)

        if self.model is None:
            raise RuntimeError("El modelo de perfilado financiero no se encuentra cargado en el servidor.")

        import pandas as pd
        features = pd.DataFrame(
            [[income, debt_percentage, saving_num]],
            columns=["ingreso_mensual", "nivel_endeudamiento", "ahorro_num"]
        )
        pred_profile = str(self.model.predict(features)[0])
        probas = self.model.predict_proba(features)[0]
        confidence = float(probas.max())



        if pred_profile == "Saludable":
            risk = "BAJO"
            summary = "Situación financiera óptima con buen margen de ahorro y endeudamiento controlado."
        elif pred_profile == "En observacion":
            risk = "MEDIO"
            summary = "Situación financiera estable pero con oportunidades de optimización en gastos fijos o ahorro."
        else:
            risk = "ALTO"
            summary = "Situación de alerta: alto nivel de endeudamiento o ausencia de capacidad de ahorro sistemática."

        return pred_profile, round(confidence, 4), risk, summary

profiler_service = FinancialProfilerService()
