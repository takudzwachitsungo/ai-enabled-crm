from app.models.schemas import AiResponse, DraftRequest, SummarizeRequest
from app.services.providers import build_provider


class AiOrchestrator:
    """Coordinates AI requests through a provider abstraction layer."""

    def __init__(self) -> None:
        self.provider = build_provider()

    def summarize(self, request: SummarizeRequest) -> AiResponse:
        model, output = self.provider.summarize(request.text, request.max_sentences)
        return AiResponse(provider=self.provider.__class__.__name__, model=model, output=output)

    def draft(self, request: DraftRequest) -> AiResponse:
        model, output = self.provider.draft(request.intent, request.context, request.tone)
        return AiResponse(provider=self.provider.__class__.__name__, model=model, output=output)
