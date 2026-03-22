import React, { useMemo, useState } from 'react';
import {
  TicketIcon,
  ChevronDownIcon,
  PlusIcon,
  RefreshCwIcon,
  FilterIcon,
  ArrowUpDownIcon,
  ColumnsIcon,
  MoreHorizontalIcon,
} from 'lucide-react';
import { StatusBadge } from './ui/StatusBadge';
import { Modal } from './ui/Modal';
import { AuthSession, TicketRecord } from '../types/crm';
import { createTicket, updateTicketAssignment, updateTicketStatus } from '../lib/api';

interface TicketListProps {
  records?: TicketRecord[];
  session: AuthSession;
  onRefresh: () => Promise<void>;
}

function mapTicketStatus(status: string) {
  switch (status) {
    case 'OPEN':
      return 'New';
    case 'IN_PROGRESS':
      return 'Nurture';
    case 'RESOLVED':
      return 'Qualified';
    case 'ESCALATED':
      return 'Contacted';
    default:
      return 'New';
  }
}

function formatDate(value: string | null) {
  return value ? new Date(value).toLocaleDateString() : '-';
}

export function TicketList({ records = [], session, onRefresh }: TicketListProps) {
  const [selectedId, setSelectedId] = useState<number | null>(records[0]?.id ?? null);
  const [statusValue, setStatusValue] = useState('IN_PROGRESS');
  const [assigneeValue, setAssigneeValue] = useState('');
  const [query, setQuery] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const [pending, setPending] = useState<'status' | 'assignment' | null>(null);
  const [creating, setCreating] = useState(false);
  const [createForm, setCreateForm] = useState({
    title: '',
    description: '',
    priority: 'HIGH',
    assignee: '',
    sourceChannel: 'EMAIL',
  });

  const selectedTicket = useMemo(
    () => records.find((ticket) => ticket.id === selectedId) ?? null,
    [records, selectedId],
  );
  const filteredTickets = useMemo(
    () =>
      records.filter((ticket) =>
        [ticket.title, ticket.description, ticket.priority, ticket.status, ticket.assignee, ticket.sourceChannel]
          .join(' ')
          .toLowerCase()
          .includes(query.toLowerCase()),
      ),
    [records, query],
  );

  async function handleStatusUpdate() {
    if (!selectedTicket) {
      return;
    }
    setPending('status');
    setMessage(null);
    try {
      await updateTicketStatus(session, selectedTicket.id, {
        status: statusValue,
        note: 'Updated from UI',
      });
      setMessage('Ticket status updated.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to update status.');
    } finally {
      setPending(null);
    }
  }

  async function handleAssignmentUpdate() {
    if (!selectedTicket || !assigneeValue.trim()) {
      setMessage('Enter an assignee first.');
      return;
    }
    setPending('assignment');
    setMessage(null);
    try {
      await updateTicketAssignment(session, selectedTicket.id, {
        assignee: assigneeValue.trim(),
        note: 'Assigned from UI',
      });
      setAssigneeValue('');
      setMessage('Ticket assignment updated.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to update assignment.');
    } finally {
      setPending(null);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Tickets /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <TicketIcon className="w-5 h-5 text-gray-400" />
            List
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button onClick={() => setCreating((value) => !value)} className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      <Modal isOpen={creating} onClose={() => setCreating(false)} title="Create Ticket" width="sm">
        <div className="space-y-4">
          <div className="space-y-3">
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Ticket title</span>
              <input value={createForm.title} onChange={(e) => setCreateForm((c) => ({ ...c, title: e.target.value }))} placeholder="e.g. Login issue" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Description</span>
              <textarea value={createForm.description} onChange={(e) => setCreateForm((c) => ({ ...c, description: e.target.value }))} placeholder="Provide details..." className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white resize-none" rows={3}></textarea>
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Priority</span>
                <select value={createForm.priority} onChange={(e) => setCreateForm((c) => ({ ...c, priority: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"><option value="LOW">Low</option><option value="MEDIUM">Medium</option><option value="HIGH">High</option><option value="URGENT">Urgent</option></select>
              </label>
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Source Channel</span>
                <select value={createForm.sourceChannel} onChange={(e) => setCreateForm((c) => ({ ...c, sourceChannel: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"><option value="EMAIL">Email</option><option value="PHONE">Phone</option><option value="WHATSAPP">WhatsApp</option></select>
              </label>
            </div>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Assignee</span>
              <input value={createForm.assignee} onChange={(e) => setCreateForm((c) => ({ ...c, assignee: e.target.value }))} placeholder="e.g. Support Team" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
          </div>
          <div className="mt-6 flex justify-end gap-3 border-t border-gray-100 pt-4">
            <button onClick={() => setCreating(false)} className="rounded-lg px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button
              onClick={async () => {
                setPending('status');
                setMessage(null);
                try {
                  await createTicket(session, createForm);
                  setCreateForm({ title: '', description: '', priority: 'HIGH', assignee: '', sourceChannel: 'EMAIL' });
                  setCreating(false);
                  setMessage('Ticket created successfully.');
                  await onRefresh();
                } catch (error) {
                  setMessage(error instanceof Error ? error.message : 'Unable to create ticket.');
                } finally {
                  setPending(null);
                }
              }}
              disabled={pending !== null || !createForm.title}
              className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
            >
              {pending === 'status' ? 'Saving...' : 'Save Ticket'}
            </button>
          </div>
        </div>
      </Modal>

      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="relative w-full lg:w-64">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            type="text"
            placeholder="Search tickets..."
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

      <div className="min-h-0 flex-1 overflow-auto p-4 sm:p-6">
        <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.2fr_0.8fr]">
          <div className="overflow-auto rounded-xl border border-gray-200 bg-white">
        <table className="w-full text-sm text-left whitespace-nowrap">
          <thead className="text-xs text-gray-500 bg-gray-50 sticky top-0 z-10 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 font-medium">Title</th>
              <th className="px-6 py-3 font-medium">Status</th>
              <th className="px-6 py-3 font-medium">Priority</th>
              <th className="px-6 py-3 font-medium">Assignee</th>
              <th className="px-6 py-3 font-medium">Source</th>
              <th className="px-6 py-3 font-medium">Due Date</th>
              <th className="px-6 py-3 font-medium">Created</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {filteredTickets.map((ticket) => (
              <tr key={ticket.id} className={`hover:bg-gray-50 transition-colors cursor-pointer ${selectedTicket?.id === ticket.id ? 'bg-gray-50' : ''}`} onClick={() => setSelectedId(ticket.id)}>
                <td className="px-6 py-3">
                  <div className="min-w-[240px]">
                    <div className="font-medium text-gray-900">{ticket.title}</div>
                    <div className="truncate text-xs text-gray-500">{ticket.description || 'No description provided'}</div>
                  </div>
                </td>
                <td className="px-6 py-3">
                  <StatusBadge status={mapTicketStatus(ticket.status)} />
                </td>
                <td className="px-6 py-3 text-gray-700">{ticket.priority}</td>
                <td className="px-6 py-3 text-gray-700">{ticket.assignee || '-'}</td>
                <td className="px-6 py-3 text-gray-600">{ticket.sourceChannel || '-'}</td>
                <td className="px-6 py-3 text-gray-600">{formatDate(ticket.dueAt)}</td>
                <td className="px-6 py-3 text-gray-500">{formatDate(ticket.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
          {!filteredTickets.length ? <div className="px-6 py-12 text-center text-sm text-gray-500">No tickets match the current search yet.</div> : null}
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="mb-4 font-semibold text-gray-900">Ticket actions</div>
            {selectedTicket ? (
              <div className="space-y-5">
                <div>
                  <div className="text-sm font-medium text-gray-900">{selectedTicket.title}</div>
                  <div className="mt-1 text-sm text-gray-500">{selectedTicket.description || 'No description provided.'}</div>
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">Update status</label>
                  <select
                    value={statusValue}
                    onChange={(event) => setStatusValue(event.target.value)}
                    className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-gray-700"
                  >
                    <option value="OPEN">Open</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="ESCALATED">Escalated</option>
                    <option value="RESOLVED">Resolved</option>
                  </select>
                  <button
                    onClick={handleStatusUpdate}
                    className="w-full rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400"
                    disabled={pending !== null}
                  >
                    {pending === 'status' ? 'Updating...' : 'Update Status'}
                  </button>
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">Assign ticket</label>
                  <input
                    value={assigneeValue}
                    onChange={(event) => setAssigneeValue(event.target.value)}
                    className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-gray-700"
                    placeholder={selectedTicket.assignee || 'Support Lead'}
                  />
                  <button
                    onClick={handleAssignmentUpdate}
                    className="w-full rounded-md border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:bg-gray-100"
                    disabled={pending !== null}
                  >
                    {pending === 'assignment' ? 'Saving...' : 'Update Assignment'}
                  </button>
                </div>

                {message ? <div className="rounded-lg border border-gray-200 bg-gray-50 px-3 py-3 text-sm text-gray-700">{message}</div> : null}
              </div>
            ) : (
              <div className="text-sm text-gray-500">Select a ticket to update it.</div>
            )}
          </div>
        </div>
      </div>

      <div className="bg-white px-4 py-3 border-t border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0 text-sm">
        <div className="rounded-md bg-gray-100 px-3 py-1 text-gray-700">Service desk</div>
        <span className="text-gray-500">{filteredTickets.length} loaded</span>
      </div>
    </div>
  );
}
