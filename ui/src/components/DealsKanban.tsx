import React, { useState } from 'react';
import {
  KanbanIcon,
  ChevronDownIcon,
  PlusIcon,
  RefreshCwIcon,
  FilterIcon,
  ColumnsIcon,
  MessageSquareIcon,
  CheckCircle2Icon,
  CopyIcon,
  AtSignIcon,
} from 'lucide-react';
import { Avatar } from './ui/Avatar';
import { StatusBadge } from './ui/StatusBadge';
import { Modal } from './ui/Modal';
import { AuthSession, OpportunityRecord } from '../types/crm';
import { createOpportunity } from '../lib/api';

interface DealsKanbanProps {
  records?: OpportunityRecord[];
  session: AuthSession;
  onRefresh: () => Promise<void>;
}

export function DealsKanban({ records, session, onRefresh }: DealsKanbanProps) {
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({ name: '', accountName: '', amount: '', stage: 'QUALIFIED' });
  const [query, setQuery] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const dealRows = (records ?? []).map((deal) => ({
    id: String(deal.id),
    title: deal.name,
    accountName: deal.accountName || 'Unassigned account',
    amount: typeof deal.amount === 'number' ? `$${deal.amount.toLocaleString()}` : '',
    timeAgo: new Date(deal.createdAt).toLocaleDateString(),
    status: deal.stage === 'PROPOSAL' ? 'Proposal/Quotation' : deal.stage === 'NEGOTIATION' ? 'Negotiation' : deal.stage === 'QUALIFIED' ? 'Qualification' : 'Ready to Close',
    avatar: '',
  }));
  const filteredDeals = dealRows.filter((deal) =>
    [deal.title, deal.amount, deal.status]
      .join(' ')
      .toLowerCase()
      .includes(query.toLowerCase()),
  );

  const columns = [
    { id: 'Qualification', title: 'Qualification', status: 'Qualification' },
    { id: 'Proposal/Quotation', title: 'Proposal/Quotation', status: 'Proposal/Quotation' },
    { id: 'Negotiation', title: 'Negotiation', status: 'Negotiation' },
    { id: 'Ready to Close', title: 'Ready to Close', status: 'Ready to Close' },
  ];

  async function handleCreate() {
    setSaving(true);
    setMessage(null);
    try {
      await createOpportunity(session, {
        name: form.name,
        accountName: form.accountName,
        amount: Number(form.amount),
        stage: form.stage,
      });
      setForm({ name: '', accountName: '', amount: '', stage: 'QUALIFIED' });
      setCreating(false);
      setMessage('Opportunity created successfully.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to create opportunity.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Opportunities /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <KanbanIcon className="w-5 h-5 text-gray-400" />
            Kanban
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button onClick={() => setCreating((v) => !v)} className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      <Modal isOpen={creating} onClose={() => setCreating(false)} title="Create Opportunity" width="sm">
        <div className="space-y-4">
          {message && (
            <div className={`rounded-lg px-3 py-2 text-xs font-medium ${message.includes('success') ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
              {message}
            </div>
          )}
          <div className="space-y-3">
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Opportunity name</span>
              <input value={form.name} onChange={(e) => setForm((c) => ({ ...c, name: e.target.value }))} placeholder="e.g. Enterprise License" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Account name</span>
              <input value={form.accountName} onChange={(e) => setForm((c) => ({ ...c, accountName: e.target.value }))} placeholder="e.g. Acme Corp" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Amount ($)</span>
                <input value={form.amount} onChange={(e) => setForm((c) => ({ ...c, amount: e.target.value }))} type="number" placeholder="5000" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
              </label>
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Stage</span>
                <select value={form.stage} onChange={(e) => setForm((c) => ({ ...c, stage: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white">
                  <option value="QUALIFIED">Qualified</option>
                  <option value="PROPOSAL">Proposal</option>
                  <option value="NEGOTIATION">Negotiation</option>
                  <option value="CLOSED_WON">Ready to Close</option>
                </select>
              </label>
            </div>
          </div>
          <div className="mt-6 flex justify-end gap-3 border-t border-gray-100 pt-4">
            <button onClick={() => setCreating(false)} className="rounded-lg px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button onClick={handleCreate} disabled={saving || !form.name || !form.amount} className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors">
              {saving ? 'Saving...' : 'Save Opportunity'}
            </button>
          </div>
        </div>
      </Modal>

      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="flex flex-wrap items-center gap-3">
          <input value={query} onChange={(e) => setQuery(e.target.value)} type="text" placeholder="Search opportunities" className="w-40 px-3 py-1.5 bg-gray-100 border-transparent rounded-md text-sm focus:bg-white focus:border-gray-300 focus:ring-0 transition-colors" />
          <button className="flex items-center justify-between w-40 px-3 py-1.5 bg-gray-100 text-gray-500 rounded-md text-sm hover:bg-gray-200 transition-colors">Organization <ChevronDownIcon className="w-4 h-4" /></button>
          <button className="flex items-center justify-between w-32 px-3 py-1.5 bg-gray-100 text-gray-500 rounded-md text-sm hover:bg-gray-200 transition-colors">Territory <ChevronDownIcon className="w-4 h-4" /></button>
          <button className="flex items-center justify-between w-32 px-3 py-1.5 bg-gray-100 text-gray-500 rounded-md text-sm hover:bg-gray-200 transition-colors">Status <ChevronDownIcon className="w-4 h-4" /></button>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <button className="p-1.5 text-gray-500 hover:bg-gray-100 rounded-md transition-colors border border-gray-200"><RefreshCwIcon className="w-4 h-4" /></button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200"><FilterIcon className="w-4 h-4" /> Filter</button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200 bg-gray-50"><ColumnsIcon className="w-4 h-4" /> Kanban Settings</button>
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-x-auto overflow-y-hidden p-4 sm:p-6">
        <div className="flex gap-6 h-full items-start">
          {columns.map((col) => {
            const columnDeals = filteredDeals.filter((d) => d.status === col.status);
            return (
              <div key={col.id} className="w-80 shrink-0 flex flex-col max-h-full">
                <div className="flex items-center justify-between mb-4 px-1">
                  <StatusBadge status={col.status} />
                  <button className="text-gray-400 hover:text-gray-600"><PlusIcon className="w-5 h-5" /></button>
                </div>
                <div className="flex-1 overflow-y-auto space-y-4 pb-4 pr-2 custom-scrollbar">
                  {columnDeals.map((deal) => {
                    return (
                      <div key={deal.id} className="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-shadow p-4 cursor-pointer">
                        <div className="flex items-center gap-3 mb-3">
                          <Avatar src={deal.avatar} fallback={deal.title} size="md" className="bg-gray-100" />
                          <h3 className="font-medium text-gray-900 truncate">{deal.title}</h3>
                        </div>
                        <div className="space-y-2 mb-4">
                          {deal.amount && <div className="text-gray-900 font-medium">{deal.amount}</div>}
                          <div className="text-sm text-gray-600 truncate">{deal.accountName}</div>
                        </div>
                        <div className="flex items-center gap-2 mb-4">
                          <span className="text-sm text-gray-500">Tenant pipeline</span>
                        </div>
                        <div className="text-xs text-gray-500 mb-4">{deal.timeAgo}</div>
                        <div className="flex items-center justify-between pt-3 border-t border-gray-100">
                          <div className="flex items-center gap-3 text-gray-400">
                            <button className="hover:text-gray-600"><AtSignIcon className="w-4 h-4" /></button>
                            <span className="w-1 h-1 rounded-full bg-gray-300"></span>
                            <button className="hover:text-gray-600"><CopyIcon className="w-4 h-4" /></button>
                            <span className="w-1 h-1 rounded-full bg-gray-300"></span>
                            <button className="hover:text-gray-600"><CheckCircle2Icon className="w-4 h-4" /></button>
                            <span className="w-1 h-1 rounded-full bg-gray-300"></span>
                            <button className="hover:text-gray-600"><MessageSquareIcon className="w-4 h-4" /></button>
                          </div>
                          <button className="text-gray-400 hover:text-gray-600"><PlusIcon className="w-4 h-4" /></button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
        {!filteredDeals.length ? <div className="flex min-w-[320px] items-center justify-center rounded-xl border border-dashed border-gray-300 bg-white px-6 py-12 text-sm text-gray-500">No opportunities match the current search yet.</div> : null}
      </div>
    </div>
  );
}
