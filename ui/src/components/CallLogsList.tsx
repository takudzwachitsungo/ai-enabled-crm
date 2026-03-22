import React, { useState } from 'react';
import {
  PhoneIcon,
  ChevronDownIcon,
  PlusIcon,
  RefreshCwIcon,
  FilterIcon,
  ArrowUpDownIcon,
  ColumnsIcon,
  MoreHorizontalIcon,
  PhoneIncomingIcon,
  PhoneOutgoingIcon,
  PhoneMissedIcon,
} from 'lucide-react';
import { AuthSession, CommunicationRecord } from '../types/crm';
import { Modal } from './ui/Modal';
import { createCommunication } from '../lib/api';

interface CallLogsListProps {
  records?: CommunicationRecord[];
  session: AuthSession;
  onRefresh: () => Promise<void>;
}

export function CallLogsList({ records, session, onRefresh }: CallLogsListProps) {
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({
    name: '',
    channelType: 'EMAIL',
    direction: 'OUTBOUND',
    participant: '',
    subject: '',
    messageBody: '',
    relatedEntityType: 'LEAD',
    relatedEntityId: '',
  });
  const [saving, setSaving] = useState(false);
  const [query, setQuery] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const communicationRows = (records ?? []).map((record) => ({
    id: String(record.id),
    callerName: record.name,
    participant: record.participant,
    channel: record.channelType,
    type: record.direction === 'OUTBOUND' ? 'Outgoing' : record.direction === 'INBOUND' ? 'Incoming' : 'Missed',
    relatedTo: record.relatedEntityType ? `${record.relatedEntityType} #${record.relatedEntityId ?? '-'}` : record.channelType,
    date: new Date(record.createdAt).toLocaleString(),
    note: record.subject || record.messageBody,
  })).filter((record) =>
    [record.callerName, record.participant, record.channel, record.type, record.note]
      .join(' ')
      .toLowerCase()
      .includes(query.toLowerCase()),
  );

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'Incoming':
        return <PhoneIncomingIcon className="w-4 h-4 text-green-600" />;
      case 'Outgoing':
        return <PhoneOutgoingIcon className="w-4 h-4 text-blue-600" />;
      case 'Missed':
        return <PhoneMissedIcon className="w-4 h-4 text-red-600" />;
      default:
        return <PhoneIcon className="w-4 h-4 text-gray-400" />;
    }
  };

  async function handleCreate() {
    setSaving(true);
    setMessage(null);
    try {
      await createCommunication(session, {
        name: form.name,
        channelType: form.channelType,
        direction: form.direction,
        participant: form.participant,
        subject: form.subject,
        messageBody: form.messageBody,
        relatedEntityType: form.relatedEntityType,
        relatedEntityId: form.relatedEntityId ? Number(form.relatedEntityId) : null,
      });
      setForm({
        name: '',
        channelType: 'EMAIL',
        direction: 'OUTBOUND',
        participant: '',
        subject: '',
        messageBody: '',
        relatedEntityType: 'LEAD',
        relatedEntityId: '',
      });
      setCreating(false);
      setMessage('Communication created successfully.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to create communication.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Communications /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <PhoneIcon className="w-5 h-5 text-gray-400" />
            List
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button onClick={() => setCreating((value) => !value)} className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      <Modal isOpen={creating} onClose={() => setCreating(false)} title="Log Communication" width="md">
        <div className="space-y-4">
          <div className="space-y-3">
            <div className="grid grid-cols-2 gap-3">
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Record Name</span>
                <input value={form.name} onChange={(e) => setForm((c) => ({ ...c, name: e.target.value }))} placeholder="e.g. Intro call" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
              </label>
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Participant</span>
                <input value={form.participant} onChange={(e) => setForm((c) => ({ ...c, participant: e.target.value }))} placeholder="e.g. Jane Doe" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
              </label>
            </div>
            
            <div className="grid grid-cols-2 gap-3">
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Channel Type</span>
                <select value={form.channelType} onChange={(e) => setForm((c) => ({ ...c, channelType: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"><option value="EMAIL">Email</option><option value="WHATSAPP">WhatsApp</option><option value="PHONE">Phone</option></select>
              </label>
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Direction</span>
                <select value={form.direction} onChange={(e) => setForm((c) => ({ ...c, direction: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"><option value="OUTBOUND">Outbound</option><option value="INBOUND">Inbound</option></select>
              </label>
            </div>

            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Subject</span>
              <input value={form.subject} onChange={(e) => setForm((c) => ({ ...c, subject: e.target.value }))} placeholder="Subject line" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Message body</span>
              <textarea value={form.messageBody} onChange={(e) => setForm((c) => ({ ...c, messageBody: e.target.value }))} placeholder="Notes..." className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white resize-none" rows={3}></textarea>
            </label>
          </div>
          <div className="mt-6 flex justify-end gap-3 border-t border-gray-100 pt-4">
            <button onClick={() => setCreating(false)} className="rounded-lg px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button onClick={handleCreate} disabled={saving || !form.name || !form.participant || !form.messageBody} className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors">
              {saving ? 'Saving...' : 'Save Communication'}
            </button>
          </div>
        </div>
      </Modal>

      {message ? <div className="border-b border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-700 sm:px-6">{message}</div> : null}

      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="relative w-full lg:w-64">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search communications..."
            className="w-full pl-3 pr-10 py-1.5 bg-gray-100 border-transparent rounded-md text-sm focus:bg-white focus:border-gray-300 focus:ring-0 transition-colors"
          />
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <button className="p-1.5 text-gray-500 hover:bg-gray-100 rounded-md transition-colors">
            <RefreshCwIcon className="w-4 h-4" />
          </button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200">
            <FilterIcon className="w-4 h-4" /> Filter
          </button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200">
            <ArrowUpDownIcon className="w-4 h-4" /> Sort
          </button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200">
            <ColumnsIcon className="w-4 h-4" /> Columns
          </button>
          <button className="p-1.5 text-gray-500 hover:bg-gray-100 rounded-md transition-colors border border-gray-200">
            <MoreHorizontalIcon className="w-4 h-4" />
          </button>
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto bg-white">
        <table className="w-full text-sm text-left whitespace-nowrap">
          <thead className="text-xs text-gray-500 bg-gray-50 sticky top-0 z-10 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 font-medium w-12">
                <input type="checkbox" className="rounded border-gray-300 text-black focus:ring-black" />
              </th>
              <th className="px-6 py-3 font-medium">Caller</th>
              <th className="px-6 py-3 font-medium">Participant</th>
              <th className="px-6 py-3 font-medium">Channel</th>
              <th className="px-6 py-3 font-medium">Direction</th>
              <th className="px-6 py-3 font-medium">Related To</th>
              <th className="px-6 py-3 font-medium">Date</th>
              <th className="px-6 py-3 font-medium">Note</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {communicationRows.map((log) => {
              return (
                <tr key={log.id} className="hover:bg-gray-50 cursor-pointer transition-colors group">
                  <td className="px-6 py-3" onClick={(e) => e.stopPropagation()}>
                    <input type="checkbox" className="rounded border-gray-300 text-black focus:ring-black" />
                  </td>
                  <td className="px-6 py-3">
                    <span className="font-medium text-gray-900 group-hover:text-blue-600 transition-colors">{log.callerName}</span>
                  </td>
                  <td className="px-6 py-3 text-gray-900 font-medium">{log.participant}</td>
                  <td className="px-6 py-3 text-gray-600">{log.channel}</td>
                  <td className="px-6 py-3">
                    <div className="flex items-center gap-2">
                      {getTypeIcon(log.type)}
                      <span className="text-gray-700 font-medium">{log.type}</span>
                    </div>
                  </td>
                  <td className="px-6 py-3 text-gray-600">{log.relatedTo}</td>
                  <td className="px-6 py-3 text-gray-500">{log.date}</td>
                  <td className="px-6 py-3 text-gray-500 truncate max-w-[220px]">{log.note}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {!communicationRows.length ? <div className="px-6 py-12 text-center text-sm text-gray-500">No communications match the current search yet.</div> : null}
      </div>

      <div className="bg-white px-4 py-3 border-t border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0 text-sm">
        <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-md">
          <button className="px-3 py-1 bg-white shadow-sm rounded text-gray-900 font-medium">20</button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">50</button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">100</button>
        </div>
        <div className="flex items-center gap-4">
          <button className="px-4 py-1.5 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors font-medium">Load More</button>
          <span className="text-gray-500">{communicationRows.length} loaded</span>
        </div>
      </div>
    </div>
  );
}
