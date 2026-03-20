from __future__ import annotations

from abc import ABC, abstractmethod

from openai import OpenAI

from app.core.settings import settings


class AiProvider(ABC):
    @abstractmethod
    def summarize(self, text: str, max_sentences: int) -> tuple[str, str]:
        raise NotImplementedError

    @abstractmethod
    def draft(self, intent: str, context: str, tone: str) -> tuple[str, str]:
        raise NotImplementedError


class MockProvider(AiProvider):
    def summarize(self, text: str, max_sentences: int) -> tuple[str, str]:
        sentences = [s.strip() for s in text.replace("\n", " ").split(".") if s.strip()]
        summary = ". ".join(sentences[:max_sentences]).strip()
        if summary and not summary.endswith("."):
            summary += "."
        return "mock", (summary or "No summary available.")

    def draft(self, intent: str, context: str, tone: str) -> tuple[str, str]:
        output = (
            f"Subject: {intent.title()}\n\n"
            f"Hi,\n\n"
            f"Based on your request, here is a {tone} draft:\n"
            f"{context[:500]}\n\n"
            "Regards,\nCRM Team"
        )
        return "mock", output


class OpenAiProvider(AiProvider):
    def __init__(self) -> None:
        if not settings.openai_api_key:
            raise ValueError("OPENAI_API_KEY is required when AI_PROVIDER=openai")
        self.client = OpenAI(api_key=settings.openai_api_key)

    def summarize(self, text: str, max_sentences: int) -> tuple[str, str]:
        prompt = (
            "Summarize the following CRM interaction text in "
            f"no more than {max_sentences} sentences:\n\n{text}"
        )
        response = self.client.responses.create(
            model=settings.openai_model,
            input=prompt,
        )
        return settings.openai_model, response.output_text.strip()

    def draft(self, intent: str, context: str, tone: str) -> tuple[str, str]:
        prompt = (
            "Draft a CRM message with the following details:\n"
            f"Intent: {intent}\n"
            f"Tone: {tone}\n"
            f"Context: {context}\n"
        )
        response = self.client.responses.create(
            model=settings.openai_model,
            input=prompt,
        )
        return settings.openai_model, response.output_text.strip()


def build_provider() -> AiProvider:
    if settings.ai_provider.lower() == "openai":
        return OpenAiProvider()
    return MockProvider()
