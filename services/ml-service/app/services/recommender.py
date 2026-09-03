from typing import List, Tuple
from app.schemas.ml import SavingFrequencyEnum, RecommendationResponse

class FinancialRecommenderService:
    @staticmethod
    def generate(monthly_income: float, debt_percentage: float, saving_frequency: SavingFrequencyEnum, profile: str) -> RecommendationResponse:
        action_plan: List[str] = []
        saving_tips: List[str] = []
        summary_parts: List[str] = []

        # 1. Análisis de endeudamiento / gastos fijos
        if debt_percentage > 50.0:
            summary_parts.append("Tu nivel de compromisos fijos y deudas supera el 50% de tus ingresos, lo cual compromete tu flexibilidad.")
            action_plan.append("Realiza una auditoría exhaustiva de suscripciones y gastos fijos recurrentes.")
            action_plan.append("Prioriza la amortización acelerada de deudas con mayores tasas de interés (método avalancha).")
            saving_tips.append("Aplica la regla presupuestaria 50/30/20 como objetivo a mediano plazo.")
        elif debt_percentage > 35.0:
            summary_parts.append("Tus gastos fijos están en un rango moderado pero cercano al límite de seguridad.")
            action_plan.append("Negocia tarifas de servicios o proveedores de conectividad para reducir el costo mensual.")
            saving_tips.append("Evita adquirir nuevas compras en cuotas antes de liquidar compromisos previos.")
        else:
            summary_parts.append("Tus gastos fijos y deudas se encuentran en un nivel muy saludable y controlado.")
            action_plan.append("Mantén el control presupuestario actual y destina excedentes al crecimiento patrimonial.")

        # 2. Análisis de ahorro e inversión
        if saving_frequency in [SavingFrequencyEnum.NINGUNA, SavingFrequencyEnum.BAJA]:
            action_plan.append("Automatiza una transferencia de ahorro de al menos el 10% del ingreso el primer día posterior al cobro.")
            saving_tips.append("Constituye un fondo de emergencia que cubra entre 3 y 6 meses de gastos básicos en instrumentos de bajo riesgo y liquidez inmediata.")
        else:
            action_plan.append("Diversifica tu portafolio de inversión evaluando instrumentos que preserven valor frente a la inflación.")
            saving_tips.append("Revisa periódicamente el rendimiento de tus inversiones para ajustar asignaciones de activos.")

        # 3. Síntesis según perfil
        if profile == "Saludable":
            summary_parts.append("Tu perfil es Saludable. Te encuentras en una posición sólida para proyectar metas financieras de mediano y largo plazo.")
        elif profile == "En riesgo":
            summary_parts.append("Tu perfil actual es En riesgo. Es prioritario estabilizar el flujo de caja y evitar compromisos adicionales.")
        else:
            summary_parts.append("Tu perfil es En observación. Con pequeños ajustes en hábitos de gasto puedes alcanzar un estado financiero saludable.")

        return RecommendationResponse(
            financial_profile=profile,
            summary=" ".join(summary_parts),
            action_plan=action_plan,
            saving_tips=saving_tips
        )

recommender_service = FinancialRecommenderService()
