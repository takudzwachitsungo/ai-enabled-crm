from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Sequence

from openai import OpenAI

from app.core.settings import settings


class AiProvider(ABC):
    @abstractmethod
    def summarize(self, text: str, max_sentences: int) -> tuple[str, str]:
        raise NotImplementedError

    @abstractmethod
    def draft(self, intent: str, context: str, tone: str) -> tuple[str, str]:
        raise NotImplementedError

    @abstractmethod
    def chat(
        self,
        tenant_name: str,
        company_context: str,
        conversation: Sequence[dict[str, str]],
        user_message: str,
    ) -> tuple[str, str]:
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

    def chat(
        self,
        tenant_name: str,
        company_context: str,
        conversation: Sequence[dict[str, str]],
        user_message: str,
    ) -> tuple[str, str]:
        recent_context = company_context[:1200].strip()
        if len(recent_context) < len(company_context):
            recent_context += "\n..."
        conversation_hint = ""
        if conversation:
            last_turn = conversation[-1]
            conversation_hint = (
                f" Previous turn: {last_turn.get('role', 'user')} said "
                f"'{last_turn.get('content', '')[:180]}'."
            )
        output = (
            f"{tenant_name} assistant response:\n\n"
            f"Question: {user_message.strip()}\n\n"
            f"Workspace context:{conversation_hint}\n{recent_context}\n\n"
            "Suggested answer: Based on the current workspace data, prioritize the open pipeline, "
            "active tickets, and upcoming commercial follow-ups visible in the tenant snapshot."
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

    def chat(
        self,
        tenant_name: str,
        company_context: str,
        conversation: Sequence[dict[str, str]],
        user_message: str,
    ) -> tuple[str, str]:
        input_messages: list[dict[str, str]] = [
            {
                "role": "system",
                "content": (
                    "You are a tenant-scoped CRM assistant. Answer only from the provided workspace data. "
                    "If the data is insufficient, say so clearly and suggest what to check next. "
                    "Be concise, operational, and business-friendly."
                ),
            },
            {
                "role": "system",
                "content": f"Workspace: {tenant_name}\n\nCompany data snapshot:\n{company_context}",
            },
        ]
        input_messages.extend(
            {
                "role": item.get("role", "user"),
                "content": item.get("content", ""),
            }
            for item in conversation
            if item.get("content")
        )
        input_messages.append({"role": "user", "content": user_message})

        response = self.client.responses.create(
            model=settings.openai_model,
            input=input_messages,
        )
        return settings.openai_model, response.output_text.strip()


def build_provider() -> AiProvider:
    if settings.ai_provider.lower() == "openai":
        return OpenAiProvider()
    return MockProvider()
