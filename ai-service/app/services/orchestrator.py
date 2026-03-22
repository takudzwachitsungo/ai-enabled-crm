from typing import TypedDict

from langgraph.graph import END, START, StateGraph

from app.models.schemas import AiResponse, ChatRequest, DraftRequest, SummarizeRequest
from app.services.providers import build_provider


class ChatGraphState(TypedDict):
    tenant_name: str
    company_context: str
    conversation: list[dict[str, str]]
    user_message: str
    provider: str
    model: str
    output: str


class AiOrchestrator:
    """Coordinates AI requests through a provider abstraction layer."""

    def __init__(self) -> None:
        self.provider = build_provider()
        self.chat_graph = self._build_chat_graph()

    def summarize(self, request: SummarizeRequest) -> AiResponse:
        model, output = self.provider.summarize(request.text, request.max_sentences)
        return AiResponse(provider=self.provider.__class__.__name__, model=model, output=output)

    def draft(self, request: DraftRequest) -> AiResponse:
        model, output = self.provider.draft(request.intent, request.context, request.tone)
        return AiResponse(provider=self.provider.__class__.__name__, model=model, output=output)

    def chat(self, request: ChatRequest) -> AiResponse:
        result = self.chat_graph.invoke(
            {
                "tenant_name": request.tenant_name,
                "company_context": request.company_context,
                "conversation": [message.model_dump() for message in request.conversation],
                "user_message": request.message,
                "provider": "",
                "model": "",
                "output": "",
            }
        )
        return AiResponse(
            provider=result["provider"],
            model=result["model"],
            output=result["output"],
        )

    def _build_chat_graph(self):
        graph = StateGraph(ChatGraphState)
        graph.add_node("assistant", self._assistant_node)
        graph.add_edge(START, "assistant")
        graph.add_edge("assistant", END)
        return graph.compile()

    def _assistant_node(self, state: ChatGraphState) -> dict[str, str]:
        model, output = self.provider.chat(
            tenant_name=state["tenant_name"],
            company_context=state["company_context"],
            conversation=state["conversation"],
            user_message=state["user_message"],
        )
        return {
            "provider": self.provider.__class__.__name__,
            "model": model,
            "output": output,
        }
