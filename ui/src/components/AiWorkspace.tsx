import React, { FormEvent, useState } from 'react';
import { SparklesIcon, ChevronDownIcon, WandSparklesIcon, BrainCircuitIcon } from 'lucide-react';
import { AiInteractionRecord, AuthSession, LeadRecord } from '../types/crm';
import { createAiDraft, createAiRecommendation, createAiSummary } from '../lib/api';

interface AiWorkspaceProps {
  session: AuthSession;
  interactions?: AiInteractionRecord[];
  leads?: LeadRecord[];
  onRefresh: () => Promise<void>;
}

export function AiWorkspace({ session, interactions = [], leads = [], onRefresh }: AiWorkspaceProps) {
  const [summaryText, setSummaryText] = useState('');
  const [draftInstructions, setDraftInstructions] = useState('');
  const [draftTone, setDraftTone] = useState('Professional');
  const [recommendLeadId, setRecommendLeadId] = useState<string>('');
  const [status, setStatus] = useState<string | null>(null);
  const [loading, setLoading] = useState<'summary' | 'draft' | 'recommend' | null>(null);

  const newest = interactions.slice().sort((a, b) => Date.parse(b.createdAt) - Date.parse(a.createdAt)).slice(0, 6);

  async function handleSummary(event: FormEvent) {
    event.preventDefault();
    setLoading('summary');
    setStatus(null);
    try {
      await createAiSummary(session, {
        name: 'Frontend summary',
        sourceType: 'UI',
        text: summaryText,
      });
      setSummaryText('');
      setStatus('AI summary created successfully.');
      await onRefresh();
    } catch (error) {
      setStatus(error instanceof Error ? error.message : 'Unable to create summary.');
    } finally {
      setLoading(null);
    }
  }

  async function handleDraft(event: FormEvent) {
    event.preventDefault();
    setLoading('draft');
    setStatus(null);
    try {
      await createAiDraft(session, {
        name: 'Frontend draft',
        sourceType: 'UI',
        instructions: draftInstructions,
        channel: 'EMAIL',
        tone: draftTone.toUpperCase(),
      });
      setDraftInstructions('');
      setStatus('AI draft created successfully.');
      await onRefresh();
    } catch (error) {
      setStatus(error instanceof Error ? error.message : 'Unable to create draft.');
    } finally {
      setLoading(null);
    }
  }

  async function handleRecommendation(event: FormEvent) {
    event.preventDefault();
    if (!recommendLeadId) {
      setStatus('Select a lead first.');
      return;
    }
    setLoading('recommend');
    setStatus(null);
    try {
      await createAiRecommendation(session, {
        name: 'Frontend recommendation',
        sourceType: 'LEAD',
        sourceId: Number(recommendLeadId),
        objective: 'Suggest the next best sales action',
        autoCreateActivity: true,
      });
      setStatus('Recommendation created and activity automation requested.');
      await onRefresh();
    } catch (error) {
      setStatus(error instanceof Error ? error.message : 'Unable to create recommendation.');
    } finally {
      setLoading(null);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">AI Workspace /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <SparklesIcon className="w-5 h-5 text-gray-400" />
            Operations
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <div className="rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm font-medium text-gray-500">
          Connected to `/api/v1/ai`
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto p-4 sm:p-6">
        <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.2fr_0.8fr]">
          <div className="space-y-6">
            <form onSubmit={handleSummary} className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="mb-4 flex items-center gap-3">
                <div className="rounded-full bg-gray-100 p-2 text-gray-600"><BrainCircuitIcon className="h-4 w-4" /></div>
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
                <button className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400" disabled={loading !== null}>
                  {loading === 'summary' ? 'Creating...' : 'Create Summary'}
                </button>
              </div>
            </form>

            <form onSubmit={handleDraft} className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="mb-4 flex items-center gap-3">
                <div className="rounded-full bg-gray-100 p-2 text-gray-600"><WandSparklesIcon className="h-4 w-4" /></div>
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
                <button className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400" disabled={loading !== null}>
                  {loading === 'draft' ? 'Creating...' : 'Create Draft'}
                </button>
              </div>
            </form>

            <form onSubmit={handleRecommendation} className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="mb-4 font-semibold text-gray-900">Recommendation</div>
              <div className="grid gap-3 sm:grid-cols-[1fr_auto]">
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
                <button className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400" disabled={loading !== null}>
                  {loading === 'recommend' ? 'Creating...' : 'Recommend'}
                </button>
              </div>
            </form>

            {status ? (
              <div className="rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm text-gray-700 shadow-sm">{status}</div>
            ) : null}
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="mb-4 font-semibold text-gray-900">Recent AI interactions</div>
            <div className="space-y-4">
              {newest.map((item) => (
                <div key={item.id} className="rounded-lg border border-gray-100 bg-gray-50 p-4">
                  <div className="flex items-center justify-between gap-3">
                    <div className="font-medium text-gray-900">{item.name}</div>
                    <div className="text-xs text-gray-500">{new Date(item.createdAt).toLocaleString()}</div>
                  </div>
                  <div className="mt-1 text-xs uppercase tracking-wide text-gray-400">{item.operationType}</div>
                  <div className="mt-3 text-sm text-gray-600 line-clamp-4">{item.outputText}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
