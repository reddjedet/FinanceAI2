import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health_endpoint():
    response = client.get("/api/v1/ml/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert "models_loaded" in data
    assert data["models_loaded"]["transaction_classifier"] is True
    assert data["models_loaded"]["financial_profiler"] is True

def test_classify_transaction_success():
    response = client.post(
        "/api/v1/ml/classify-transaction",
        json={"description": "COMPRA SUPERMERCADO COTO"}
    )
    assert response.status_code == 200
    data = response.json()
    assert data["category"] == "Alimentacion"
    assert data["confidence"] > 0.5
    assert "requires_confirmation" in data
    assert "all_scores" in data

def test_classify_creditos_y_deudas():
    response = client.post(
        "/api/v1/ml/classify-transaction",
        json={"description": "PAGO RESUMEN TARJETA VISA CREDITO"}
    )
    assert response.status_code == 200
    data = response.json()
    assert data["category"] == "Creditos y Deudas"
    assert data["confidence"] > 0.70
    assert data["requires_confirmation"] is False

def test_classify_ambiguous_requires_confirmation():
    # Descripción muy ambigua y genérica que debe disparar baja certeza
    response = client.post(
        "/api/v1/ml/classify-transaction",
        json={"description": "PAGO VARIOS XWZ"}
    )
    assert response.status_code == 200
    data = response.json()
    assert "requires_confirmation" in data

def test_classify_transaction_empty_error():
    response = client.post(
        "/api/v1/ml/classify-transaction",
        json={"description": "   "}
    )
    assert response.status_code == 422

def test_financial_health_success():
    response = client.post(
        "/api/v1/ml/financial-health",
        json={
            "monthly_income": 3500.0,
            "debt_percentage": 20.0,
            "saving_frequency": "ALTA"
        }
    )
    assert response.status_code == 200
    data = response.json()
    assert data["financial_profile"] in ["Saludable", "En observacion", "En riesgo"]
    assert "risk_level" in data
    assert "summary" in data

def test_financial_health_negative_income_error():
    response = client.post(
        "/api/v1/ml/financial-health",
        json={
            "monthly_income": -100.0,
            "debt_percentage": 20.0,
            "saving_frequency": "ALTA"
        }
    )
    assert response.status_code == 422

def test_financial_health_invalid_debt_error():
    response = client.post(
        "/api/v1/ml/financial-health",
        json={
            "monthly_income": 2500.0,
            "debt_percentage": 150.0,
            "saving_frequency": "ALTA"
        }
    )
    assert response.status_code == 422

def test_recommendations_success():
    response = client.post(
        "/api/v1/ml/recommendations",
        json={
            "monthly_income": 3000.0,
            "debt_percentage": 55.0,
            "saving_frequency": "NINGUNA"
        }
    )
    assert response.status_code == 200
    data = response.json()
    assert data["financial_profile"] == "En riesgo"
    assert len(data["action_plan"]) > 0
    assert len(data["saving_tips"]) > 0
