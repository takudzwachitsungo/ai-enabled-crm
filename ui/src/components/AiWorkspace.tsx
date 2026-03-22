import React, { FormEvent, useMemo, useState } from "react";
import {
  BrainCircuitIcon,
  ChevronDownIcon,
  MessageSquareIcon,
  SparklesIcon,
  WandSparklesIcon,
} from "lucide-react";
import { createAiChat, createAiDraft, createAiRecommendation, createAiSummary } from "../lib/api";
import {
  AiChatConversationMessage,
  AiInteractionRecord,
  AuthSession,
  LeadRecord,
} from "../types/crm";

interface AiWorkspaceProps {
  session: AuthSession;
  interactions?: AiInteractionRecord[];
  leads?: LeadRecord[];
  onRefresh: () => Promise<void>;
}

const WELCOME_MESSAGE: AiChatConversationMessage = {
  role: "assistant",
  content:
    "I can help with pipeline, service, commerce, campaigns, and workspace operations using the current tenant data. Ask for priorities, risk areas, or a quick summary.",
};

export function AiWorkspace({
  session,
  interactions = [],
  leads = [],
  onRefresh,
}: AiWorkspaceProps) {
  const [summaryText, setSummaryText] = useState("");
  const [draftInstructions, setDraftInstructions] = useState("");
  const [draftTone, setDraftTone] = useState("Professional");
  const [recommendLeadId, setRecommendLeadId] = useState<string>("");
  const [chatInput, setChatInput] = useState("");
  const [chatMessages, setChatMessages] = useState<AiChatConversationMessage[]>([WELCOME_MESSAGE]);
  const [status, setStatus] = useState<string | null>(null);
  const [loading, setLoading] = useState<"chat" | "summary" | "draft" | "recommend" | null>(null);

  const newest = useMemo(
    () =>
      interactions
        .slice()
        .sort((a, b) => Date.parse(b.createdAt) - Date.parse(a.createdAt))
        .slice(0, 8),
    [interactions],
  );

  const recentChats = newest.filter((item) => item.operationType === "CHAT").slice(0, 4);

  async function handleChat(event: FormEvent) {
    event.preventDefault();
    const message = chatInput.trim();
    if (!message) {
      return;
    }

    const userTurn: AiChatConversationMessage = { role: "user", content: message };
    const conversationForRequest = [...chatMessages, userTurn]
      .filter((entry) => entry.content.trim().length > 0)
      .slice(-8);

    setChatMessages((current) => [...current, userTurn]);
    setChatInput("");
    setStatus(null);
    setLoading("chat");

    try {
      const response = await createAiChat(session, {
        name: "Workspace assistant",
        message,
        conversation: conversationForRequest,
      });
      setChatMessages((current) => [...current, { role: "assistant", content: response.outputText }]);
      await onRefresh();
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Unable to reach the workspace assistant.";
      setChatMessages((current) => [
        ...current,
        {
          role: "assistant",
          content: `I ran into a problem while answering that request. ${errorMessage}`,
        },
      ]);
      setStatus(errorMessage);
    } finally {
      setLoading(null);
    }
  }

  async function handleSummary(event: FormEvent) {
    event.preventDefault();
    setLoading("summary");
    setStatus(null);
    try {
      await createAiSummary(session, {
        name: "Frontend summary",
        sourceType: "UI",
        text: summaryText,
      });
      setSummaryText("");
      setStatus("AI summary created successfully.");
      await onRefresh();
    } catch (error) {
      setStatus(error instanceof Error ? error.message : "Unable to create summary.");
    } finally {
      setLoading(null);
    }
  }

  async function handleDraft(event: FormEvent) {
    event.preventDefault();
    setLoading("draft");
    setStatus(null);
    try {
      await createAiDraft(session, {
        name: "Frontend draft",
        sourceType: "UI",
        instructions: draftInstructions,
        channel: "EMAIL",
        tone: draftTone.toUpperCase(),
      });
      setDraftInstructions("");
      setStatus("AI draft created successfully.");
      await onRefresh();
    } catch (error) {
      setStatus(error instanceof Error ? error.message : "Unable to create draft.");
    } finally {
      setLoading(null);
    }
  }

  async function handleRecommendation(event: FormEvent) {
    event.preventDefault();
    if (!recommendLeadId) {
      setStatus("Select a lead first.");
      return;
    }
    setLoading("recommend");
    setStatus(null);
    try {
      await createAiRecommendation(session, {
        name: "Frontend recommendation",
        sourceType: "LEAD",
        sourceId: Number(recommendLeadId),
        objective: "Suggest the next best sales action",
        autoCreateActivity: true,
      });
      setStatus("Recommendation created and activity automation requested.");
      await onRefresh();
    } catch (error) {
      setStatus(error instanceof Error ? error.message : "Unable to create recommendation.");
    } finally {
      setLoading(null);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col overflow-hidden bg-[#f8f9fa]">
      <div className="shrink-0 border-b border-gray-200 bg-white px-4 py-4 sm:px-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-2 text-lg">
            <span className="text-gray-500">AI Workspace /</span>
            <button className="flex items-center gap-2 rounded-md px-2 py-1 font-semibold text-gray-900 transition-colors hover:bg-gray-50">
              <SparklesIcon className="h-5 w-5 text-gray-400" />
              Assistant
              <ChevronDownIcon className="h-4 w-4 text-gray-400" />
            </button>
          </div>
          <div className="rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm font-medium text-gray-500">
            Workspace-aware assistant
          </div>
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto p-4 sm:p-6">
        <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.25fr_0.75fr]">
          <div className="flex min-h-[560px] flex-col rounded-xl border border-gray-200 bg-white shadow-sm">
            <div className="border-b border-gray-200 px-5 py-4">
              <div className="flex items-center gap-3">
                <div className="rounded-full bg-gray-100 p-2 text-gray-600">
                  <MessageSquareIcon className="h-4 w-4" />
                </div>
                <div>
                  <div className="font-semibold text-gray-900">Workspace assistant</div>
                  <div className="text-sm text-gray-500">
                    Ask about pipeline, tickets, campaigns, revenue, or current account activity.
                  </div>
                </div>
              </div>
            </div>

            <div className="min-h-0 flex-1 space-y-4 overflow-auto px-5 py-5">
              {chatMessages.map((message, index) => {
                const isAssistant = message.role === "assistant";
                return (
                  <div
                    key={`${message.role}-${index}`}
                    className={`flex ${isAssistant ? "justify-start" : "justify-end"}`}
                  >
                    <div
                      className={`max-w-[85%] rounded-2xl px-4 py-3 text-sm leading-6 ${
                        isAssistant
                          ? "border border-gray-200 bg-gray-50 text-gray-700"
                          : "bg-black text-white"
                      }`}
                    >
                      {message.content}
                    </div>
                  </div>
                );
              })}
            </div>

            <div className="border-t border-gray-200 px-5 py-4">
              <form onSubmit={handleChat} className="space-y-3">
                <textarea
                  value={chatInput}
                  onChange={(event) => setChatInput(event.target.value)}
                  className="min-h-[96px] w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
                  placeholder="What should I focus on this week for Dala Inc?"
                />
                <div className="flex items-center justify-between gap-3">
                  <div className="text-xs text-gray-500">
                    Uses current tenant data from CRM, service, commerce, campaigns, and reports.
                  </div>
                  <button
                    className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400"
                    disabled={loading !== null || chatInput.trim().length === 0}
                  >
                    {loading === "chat" ? "Thinking..." : "Send"}
                  </button>
                </div>
              </form>
            </div>
          </div>

          <div className="space-y-6">
            <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="mb-4 font-semibold text-gray-900">Recent assistant and AI activity</div>
              <div className="space-y-4">
                {newest.map((item) => (
                  <div key={item.id} className="rounded-lg border border-gray-100 bg-gray-50 p-4">
                    <div className="flex items-center justify-between gap-3">
                      <div className="font-medium text-gray-900">{item.name}</div>
                      <div className="text-xs text-gray-500">
                        {new Date(item.createdAt).toLocaleString()}
                      </div>
                    </div>
                    <div className="mt-1 text-xs uppercase tracking-wide text-gray-400">
                      {item.operationType}
                    </div>
                    <div className="mt-3 text-sm text-gray-600 line-clamp-4">{item.outputText}</div>
                  </div>
                ))}
              </div>
            </div>

            <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="mb-4 font-semibold text-gray-900">Suggested prompts</div>
              <div className="space-y-2">
                {[
                  "What should I focus on this week?",
                  "Summarize open commercial risk across quotes and invoices.",
                  "Which tickets or accounts look like churn risks?",
                  "Give me a quick workspace summary for leadership.",
                ].map((prompt) => (
                  <button
                    key={prompt}
                    type="button"
                    onClick={() => setChatInput(prompt)}
                    className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-left text-sm text-gray-700 transition-colors hover:bg-white hover:text-gray-900"
                  >
                    {prompt}
                  </button>
                ))}
              </div>
              {recentChats.length > 0 ? (
                <div className="mt-5 border-t border-gray-100 pt-4">
                  <div className="mb-2 text-xs font-medium uppercase tracking-wide text-gray-400">
                    Recent chat outcomes
                  </div>
                  <div className="space-y-2">
                    {recentChats.map((item) => (
                      <div key={item.id} className="text-sm text-gray-600">
                        {item.outputText}
                      </div>
                    ))}
                  </div>
                </div>
              ) : null}
            </div>
          </div>
        </div>

        <div className="mt-6 grid grid-cols-1 gap-6 xl:grid-cols-3">
          <form onSubmit={handleSummary} className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center gap-3">
              <div className="rounded-full bg-gray-100 p-2 text-gray-600">
                <BrainCircuitIcon className="h-4 w-4" />
              </div>
              <div>
                <div className="font-semibold text-gray-900">Summarize</div>
                <div className="text-sm text-gray-500">Create a traceable summary from free text</div>
              </div>
            </div>
            <textarea
              value={summaryText}
              onChange={(event) => setSummaryText(event.target.value)}
              className="min-h-[140px] w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
              placeholder="Paste call notes, a customer email, or CRM activity text..."
              required
            />
            <div className="mt-4 flex justify-end">
              <button
                className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400"
                disabled={loading !== null}
              >
                {loading === "summary" ? "Creating..." : "Create Summary"}
              </button>
            </div>
          </form>

          <form onSubmit={handleDraft} className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center gap-3">
              <div className="rounded-full bg-gray-100 p-2 text-gray-600">
                <WandSparklesIcon className="h-4 w-4" />
              </div>
              <div>
                <div className="font-semibold text-gray-900">Draft</div>
                <div className="text-sm text-gray-500">Generate outreach or follow-up content</div>
              </div>
            </div>
            <textarea
              value={draftInstructions}
              onChange={(event) => setDraftInstructions(event.target.value)}
              className="min-h-[140px] w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"
              placeholder="Draft a renewal follow-up email for Acme with a concise CTA..."
              required
            />
            <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <select
                value={draftTone}
                onChange={(event) => setDraftTone(event.target.value)}
                className="rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-gray-700"
              >
                <option>Professional</option>
                <option>Friendly</option>
                <option>Urgent</option>
              </select>
              <button
                className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400"
                disabled={loading !== null}
              >
                {loading === "draft" ? "Creating..." : "Create Draft"}
              </button>
            </div>
          </form>

          <form onSubmit={handleRecommendation} className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="mb-4 font-semibold text-gray-900">Recommendation</div>
            <div className="grid gap-3">
              <select
                value={recommendLeadId}
                onChange={(event) => setRecommendLeadId(event.target.value)}
                className="rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-gray-700"
                required
              >
                <option value="">Select lead</option>
                {leads.map((lead) => (
                  <option key={lead.id} value={lead.id}>
                    {lead.fullName}
                  </option>
                ))}
              </select>
              <button
                className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400"
                disabled={loading !== null}
              >
                {loading === "recommend" ? "Creating..." : "Recommend"}
              </button>
            </div>
          </form>
        </div>

        {status ? (
          <div className="mt-6 rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm text-gray-700 shadow-sm">
            {status}
          </div>
        ) : null}
      </div>
    </div>
  );
}
