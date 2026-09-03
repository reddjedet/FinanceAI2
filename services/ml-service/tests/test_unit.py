import unittest
import sys
import os

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from app.services.classifier import classifier_service
from app.services.profiler import profiler_service
from app.services.recommender import recommender_service
from app.schemas.ml import SavingFrequencyEnum

class TestMLServices(unittest.TestCase):
    def test_classifier(self):
        cat, conf, scores = classifier_service.predict("Pago Supermercado Carrefour")
        self.assertEqual(cat, "Alimentacion")
        self.assertGreater(conf, 0.5)

    def test_profiler_saludable(self):
        prof, conf, risk, summary = profiler_service.evaluate(4000.0, 15.0, SavingFrequencyEnum.ALTA)
        self.assertEqual(prof, "Saludable")
        self.assertEqual(risk, "BAJO")

    def test_profiler_riesgo(self):
        prof, conf, risk, summary = profiler_service.evaluate(1500.0, 75.0, SavingFrequencyEnum.NINGUNA)
        self.assertEqual(prof, "En riesgo")
        self.assertEqual(risk, "ALTO")

    def test_recommender(self):
        rec = recommender_service.generate(2000.0, 60.0, SavingFrequencyEnum.NINGUNA, "En riesgo")
        self.assertEqual(rec.financial_profile, "En riesgo")
        self.assertTrue(len(rec.action_plan) >= 2)

if __name__ == "__main__":
    unittest.main()
