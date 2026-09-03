from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
from app.core.config import settings
from app.api.v1.router import router as api_router
from app.services.classifier import classifier_service
from app.services.profiler import profiler_service

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Carga de modelos al inicio
    print("Iniciando microservicio FinanceAI ML...")
    classifier_service.load_model()
    profiler_service.load_model()
    yield
    print("Cerrando microservicio FinanceAI ML...")

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="Microservicio dedicado de Inferencia y NLP para FinanceAI v2",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(api_router, prefix=settings.API_V1_STR, tags=["Machine Learning"])

@app.get("/", tags=["Root"])
def root():
    return {
        "service": settings.PROJECT_NAME,
        "version": settings.VERSION,
        "docs_url": "/docs"
    }
