from pydantic import BaseModel, Field


class SummarizeRequest(BaseModel):
    text: str = Field(min_length=1, max_length=20000)
    max_sentences: int = Field(default=4, ge=1, le=12)


class DraftRequest(BaseModel):
    intent: str = Field(min_length=1, max_length=200)
    context: str = Field(min_length=1, max_length=20000)
    tone: str = Field(default="professional", min_length=2, max_length=30)


class ChatMessage(BaseModel):
    role: str = Field(min_length=1, max_length=20)
    content: str = Field(min_length=1, max_length=4000)


class ChatRequest(BaseModel):
    tenant_id: str = Field(min_length=1, max_length=100)
    tenant_name: str = Field(min_length=1, max_length=160)
    company_context: str = Field(min_length=1, max_length=32000)
    message: str = Field(min_length=1, max_length=4000)
    conversation: list[ChatMessage] = Field(default_factory=list, max_length=12)


class AiResponse(BaseModel):
    provider: str
    model: str
    output: str
