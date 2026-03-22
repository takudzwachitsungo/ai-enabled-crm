from fastapi import APIRouter

from app.models.schemas import AiResponse, ChatRequest, DraftRequest, SummarizeRequest
from app.services.orchestrator import AiOrchestrator

router = APIRouter()
orchestrator = AiOrchestrator()


@router.get("/health")
def health() -> dict[str, str]:
    return {"status": "UP"}


@router.post("/v1/summarize", response_model=AiResponse)
def summarize(request: SummarizeRequest) -> AiResponse:
    return orchestrator.summarize(request)


@router.post("/v1/draft", response_model=AiResponse)
def draft(request: DraftRequest) -> AiResponse:
    return orchestrator.draft(request)


@router.post("/v1/chat", response_model=AiResponse)
def chat(request: ChatRequest) -> AiResponse:
    return orchestrator.chat(request)
