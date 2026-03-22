import React, { useState } from 'react';
import {
  LayoutListIcon,
  ChevronDownIcon,
  PlusIcon,
  RefreshCwIcon,
  FilterIcon,
  ArrowUpDownIcon,
  ColumnsIcon,
  MoreHorizontalIcon,
  PhoneIcon,
} from 'lucide-react';
import { Avatar } from './ui/Avatar';
import { StatusBadge } from './ui/StatusBadge';
import { Modal } from './ui/Modal';
import { AuthSession, LeadRecord } from '../types/crm';
import { createLead } from '../lib/api';

interface LeadsListProps {
  onLeadClick: (leadId: string) => void;
  records?: LeadRecord[];
  session: AuthSession;
  onRefresh: () => Promise<void>;
}

export function LeadsList({ onLeadClick, records, session, onRefresh }: LeadsListProps) {
  const [creating, setCreating] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [query, setQuery] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const leadRows = (records ?? []).map((lead) => ({
    id: String(lead.id),
    name: lead.fullName,
    organization: '',
    status: lead.status,
    email: lead.email,
    mobile: '',
    lastModified: new Date(lead.createdAt).toLocaleDateString(),
    avatar: `https://i.pravatar.cc/150?u=lead-${lead.id}`,
  }));
  const filteredLeads = leadRows.filter((lead) =>
    [lead.name, lead.email, lead.status, lead.organization]
      .join(' ')
      .toLowerCase()
      .includes(query.toLowerCase()),
  );

  async function handleCreate() {
    setSaving(true);
    setMessage(null);
    try {
      await createLead(session, { fullName: name, email });
      setName('');
      setEmail('');
      setCreating(false);
      setMessage('Lead created successfully.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to create lead.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Leads /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <LayoutListIcon className="w-5 h-5 text-gray-400" />
            List
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button
          onClick={() => setCreating((value) => !value)}
          className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors"
        >
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      <Modal isOpen={creating} onClose={() => setCreating(false)} title="Create Lead" width="sm">
        <div className="space-y-4">
          {message && (
            <div className={`rounded-lg px-3 py-2 text-xs font-medium ${message.includes('success') ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
              {message}
            </div>
          )}
          <div className="space-y-3">
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Full name</span>
              <input value={name} onChange={(e) => setName(e.target.value)} placeholder="e.g. Jane Doe" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Email address</span>
              <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="jane@example.com" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
          </div>
          <div className="mt-6 flex justify-end gap-3 border-t border-gray-100 pt-4">
            <button onClick={() => setCreating(false)} className="rounded-lg px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button onClick={handleCreate} disabled={saving || !name || !email} className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors">
              {saving ? 'Creating...' : 'Create Lead'}
            </button>
          </div>
        </div>
      </Modal>

      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="relative w-full lg:w-64">
          <input value={query} onChange={(e) => setQuery(e.target.value)} type="text" placeholder="Search leads" className="w-full pl-3 pr-10 py-1.5 bg-gray-100 border-transparent rounded-md text-sm focus:bg-white focus:border-gray-300 focus:ring-0 transition-colors" />
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <button className="p-1.5 text-gray-500 hover:bg-gray-100 rounded-md transition-colors"><RefreshCwIcon className="w-4 h-4" /></button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200"><FilterIcon className="w-4 h-4" /> Filter</button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200"><ArrowUpDownIcon className="w-4 h-4" /> Sort</button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200"><ColumnsIcon className="w-4 h-4" /> Columns</button>
          <button className="p-1.5 text-gray-500 hover:bg-gray-100 rounded-md transition-colors border border-gray-200"><MoreHorizontalIcon className="w-4 h-4" /></button>
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto bg-white">
        <table className="w-full text-sm text-left whitespace-nowrap">
          <thead className="text-xs text-gray-500 bg-gray-50 sticky top-0 z-10 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 font-medium w-12"><input type="checkbox" className="rounded border-gray-300 text-black focus:ring-black" /></th>
              <th className="px-6 py-3 font-medium">Name</th>
              <th className="px-6 py-3 font-medium">Organization</th>
              <th className="px-6 py-3 font-medium">Status</th>
              <th className="px-6 py-3 font-medium">Email</th>
              <th className="px-6 py-3 font-medium">Mobile No</th>
              <th className="px-6 py-3 font-medium">Assigned To</th>
              <th className="px-6 py-3 font-medium">Last Modified</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {filteredLeads.map((lead) => {
              return (
                <tr key={lead.id} onClick={() => onLeadClick(lead.id)} className="hover:bg-gray-50 cursor-pointer transition-colors group">
                  <td className="px-6 py-3" onClick={(e) => e.stopPropagation()}><input type="checkbox" className="rounded border-gray-300 text-black focus:ring-black" /></td>
                  <td className="px-6 py-3">
                    <div className="flex items-center gap-3">
                      <Avatar src={lead.avatar} fallback={lead.name} size="md" />
                      <span className="font-medium text-gray-900 group-hover:text-blue-600 transition-colors">{lead.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-3">
                    {lead.organization ? (
                      <div className="flex items-center gap-2">
                        <Avatar fallback={lead.organization} size="sm" className="bg-gray-100 text-gray-500" />
                        <span className="text-gray-600">{lead.organization}</span>
                      </div>
                    ) : <span className="text-gray-400">-</span>}
                  </td>
                  <td className="px-6 py-3"><StatusBadge status={lead.status} /></td>
                  <td className="px-6 py-3 text-gray-600">{lead.email || '-'}</td>
                  <td className="px-6 py-3 text-gray-600 flex items-center gap-2">{lead.mobile && <PhoneIcon className="w-3 h-3 text-gray-400" />}{lead.mobile || '-'}</td>
                  <td className="px-6 py-3">
                    <span className="text-gray-400">Unassigned</span>
                  </td>
                  <td className="px-6 py-3 text-gray-500">{lead.lastModified}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {!filteredLeads.length ? (
          <div className="px-6 py-12 text-center text-sm text-gray-500">
            No leads match the current search yet.
          </div>
        ) : null}
      </div>

      <div className="bg-white px-4 py-3 border-t border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0 text-sm">
        <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-md">
          <button className="px-3 py-1 bg-white shadow-sm rounded text-gray-900 font-medium">20</button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">50</button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">100</button>
        </div>
        <div className="flex items-center gap-4">
          <button className="px-4 py-1.5 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors font-medium">Load More</button>
          <span className="text-gray-500">{filteredLeads.length} loaded</span>
        </div>
      </div>
    </div>
  );
}
