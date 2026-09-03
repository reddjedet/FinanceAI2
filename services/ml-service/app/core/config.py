from pydantic import BaseModel
from pathlib import Path
import os

class Settings(BaseModel):
    PROJECT_NAME: str = "FinanceAI ML Inference Service"
    VERSION: str = "2.0.0"
    API_V1_STR: str = "/api/v1/ml"
    ARTIFACTS_DIR: str = os.getenv(
        "ARTIFACTS_DIR",
        str((Path(__file__).resolve().parent.parent / "artifacts").resolve())
    )

settings = Settings()

