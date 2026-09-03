from pydantic import BaseModel, Field
from typing import List, Dict, Optional
from enum import Enum

class SavingFrequencyEnum(str, Enum):
    ALTA = "ALTA"
    MEDIA = "MEDIA"
    BAJA = "BAJA"
    NINGUNA = "NINGUNA"

class TransactionClassifyRequest(BaseModel):
    description: str = Field(..., example="Pago Coto Supermercado", description="Descripción textual de la transacción")

class TransactionClassifyResponse(BaseModel):
    description: str
    category: str
    confidence: float
    requires_confirmation: bool = Field(False, description="Indica si la predicción tiene baja certeza (< 0.65) y requiere validación del usuario")
    all_scores: Optional[Dict[str, float]] = None


class FinancialHealthRequest(BaseModel):
    monthly_income: float = Field(..., gt=0, example=2500.0, description="Ingreso mensual neto del usuario")
    debt_percentage: float = Field(..., ge=0, le=100, example=32.5, description="Porcentaje de endeudamiento o gastos fijos")
    saving_frequency: SavingFrequencyEnum = Field(..., example="ALTA", description="Frecuencia con la que ahorra o invierte")

class FinancialHealthResponse(BaseModel):
    financial_profile: str
    confidence: float
    risk_level: str
    summary: str

class RecommendationRequest(BaseModel):
    monthly_income: float = Field(..., gt=0, example=2500.0)
    debt_percentage: float = Field(..., ge=0, le=100, example=45.0)
    saving_frequency: SavingFrequencyEnum = Field(..., example="BAJA")
    financial_profile: Optional[str] = Field(None, example="En observacion")

class RecommendationResponse(BaseModel):
    financial_profile: str
    summary: str
    action_plan: List[str]
    saving_tips: List[str]

class HealthStatusResponse(BaseModel):
    status: str
    version: str
    models_loaded: Dict[str, bool]
