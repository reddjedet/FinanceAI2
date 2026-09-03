from fastapi import APIRouter, HTTPException, status
from app.schemas.ml import (
    TransactionClassifyRequest,
    TransactionClassifyResponse,
    FinancialHealthRequest,
    FinancialHealthResponse,
    RecommendationRequest,
    RecommendationResponse,
    HealthStatusResponse
)
from app.services.classifier import classifier_service
from app.services.profiler import profiler_service
from app.services.recommender import recommender_service
from app.core.config import settings

router = APIRouter()

@router.get("/health", response_model=HealthStatusResponse, summary="Verificar estado de salud del servicio ML")
async def health_check():
    return HealthStatusResponse(
        status="healthy",
        version=settings.VERSION,
        models_loaded={
            "transaction_classifier": classifier_service.is_loaded,
            "financial_profiler": profiler_service.is_loaded
        }
    )

@router.post("/classify-transaction", response_model=TransactionClassifyResponse, summary="Clasificar descripción de transacción en categorías financieras")
async def classify_transaction(payload: TransactionClassifyRequest):
    if not payload.description or not payload.description.strip():
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="La descripción de la transacción no puede estar vacía."
        )
    category, confidence, scores = classifier_service.predict(payload.description)
    requires_conf = bool(confidence < 0.65)
    return TransactionClassifyResponse(
        description=payload.description,
        category=category,
        confidence=confidence,
        requires_confirmation=requires_conf,
        all_scores=scores
    )


@router.post("/financial-health", response_model=FinancialHealthResponse, summary="Evaluar perfil de salud financiera")
async def evaluate_financial_health(payload: FinancialHealthRequest):
    profile, confidence, risk, summary = profiler_service.evaluate(
        income=payload.monthly_income,
        debt_percentage=payload.debt_percentage,
        saving_freq=payload.saving_frequency
    )
    return FinancialHealthResponse(
        financial_profile=profile,
        confidence=confidence,
        risk_level=risk,
        summary=summary
    )

@router.post("/recommendations", response_model=RecommendationResponse, summary="Generar plan de acción y recomendaciones financieras")
async def generate_recommendations(payload: RecommendationRequest):
    profile = payload.financial_profile
    if not profile:
        profile, _, _, _ = profiler_service.evaluate(
            income=payload.monthly_income,
            debt_percentage=payload.debt_percentage,
            saving_freq=payload.saving_frequency
        )
    return recommender_service.generate(
        monthly_income=payload.monthly_income,
        debt_percentage=payload.debt_percentage,
        saving_frequency=payload.saving_frequency,
        profile=profile
    )
