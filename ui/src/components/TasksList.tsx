import React, { useState } from 'react';
import {
  CheckSquareIcon,
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
import { ActivityRecord, AuthSession } from '../types/crm';
import { createActivity } from '../lib/api';

interface TasksListProps {
  records?: ActivityRecord[];
  session: AuthSession;
  onRefresh: () => Promise<void>;
}

export function TasksList({ records, session, onRefresh }: TasksListProps) {
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({ type: 'TASK', subject: '', relatedEntityType: 'LEAD', relatedEntityId: '', details: '' });
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [query, setQuery] = useState('');
  const activityRows = (records ?? []).map((activity) => ({
    id: String(activity.id),
    title: activity.subject,
    status: activity.type === 'TASK' ? 'Open' : activity.type === 'MEETING' ? 'In Progress' : 'Done',
    priority: activity.relatedEntityType === 'TICKET' ? 'High' : activity.relatedEntityType === 'INVOICE' ? 'Urgent' : 'Medium',
    relatedTo: activity.relatedEntityType ? `${activity.relatedEntityType} #${activity.relatedEntityId ?? '-'}` : 'Backlog',
    lastModified: new Date(activity.createdAt).toLocaleDateString(),
  })).filter((activity) =>
    [activity.title, activity.status, activity.priority, activity.relatedTo]
      .join(' ')
      .toLowerCase()
      .includes(query.toLowerCase()),
  );

  const getPriorityBadge = (priority: string) => {
    switch (priority) {
      case 'Urgent':
        return <span className="px-2 py-0.5 rounded-full bg-red-100 text-red-700 text-xs font-medium border border-red-200">Urgent</span>;
      case 'High':
        return <span className="px-2 py-0.5 rounded-full bg-orange-100 text-orange-700 text-xs font-medium border border-orange-200">High</span>;
      case 'Medium':
        return <span className="px-2 py-0.5 rounded-full bg-yellow-100 text-yellow-700 text-xs font-medium border border-yellow-200">Medium</span>;
      case 'Low':
        return <span className="px-2 py-0.5 rounded-full bg-gray-100 text-gray-700 text-xs font-medium border border-gray-200">Low</span>;
      default:
        return <span className="px-2 py-0.5 rounded-full bg-gray-100 text-gray-700 text-xs font-medium border border-gray-200">{priority}</span>;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Open':
        return 'New';
      case 'In Progress':
        return 'Nurture';
      case 'Done':
        return 'Qualified';
      case 'Cancelled':
        return 'Contacted';
      default:
        return 'New';
    }
  };

  async function handleCreate() {
    setSaving(true);
    setMessage(null);
    try {
      await createActivity(session, {
        type: form.type,
        subject: form.subject,
        relatedEntityType: form.relatedEntityType,
        relatedEntityId: Number(form.relatedEntityId),
        details: form.details,
      });
      setForm({ type: 'TASK', subject: '', relatedEntityType: 'LEAD', relatedEntityId: '', details: '' });
      setCreating(false);
      setMessage('Activity created successfully.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to create activity.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Activities /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <CheckSquareIcon className="w-5 h-5 text-gray-400" />
            List
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button onClick={() => setCreating((value) => !value)} className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      <Modal isOpen={creating} onClose={() => setCreating(false)} title="Create Activity" width="sm">
        <div className="space-y-4">
          <div className="space-y-3">
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Subject</span>
              <input value={form.subject} onChange={(e) => setForm((c) => ({ ...c, subject: e.target.value }))} placeholder="e.g. Follow up email" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Type</span>
                <select value={form.type} onChange={(e) => setForm((c) => ({ ...c, type: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white">
                  <option value="TASK">Task</option>
                  <option value="NOTE">Note</option>
                  <option value="MEETING">Meeting</option>
                </select>
              </label>
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Related to</span>
                <select value={form.relatedEntityType} onChange={(e) => setForm((c) => ({ ...c, relatedEntityType: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white">
                  <option value="LEAD">Lead</option>
                  <option value="TICKET">Ticket</option>
                  <option value="ACCOUNT">Account</option>
                  <option value="QUOTE">Quote</option>
                </select>
              </label>
            </div>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Related ID</span>
              <input value={form.relatedEntityId} onChange={(e) => setForm((c) => ({ ...c, relatedEntityId: e.target.value }))} placeholder="e.g. 1234" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Details</span>
              <textarea value={form.details} onChange={(e) => setForm((c) => ({ ...c, details: e.target.value }))} placeholder="Activity details..." className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white resize-none" rows={3}></textarea>
            </label>
          </div>
          <div className="mt-6 flex justify-end gap-3 border-t border-gray-100 pt-4">
            <button onClick={() => setCreating(false)} className="rounded-lg px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button onClick={handleCreate} disabled={saving || !form.subject || !form.relatedEntityId} className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors">
              {saving ? 'Saving...' : 'Save Activity'}
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
            placeholder="Search activities..."
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
              <th className="px-6 py-3 font-medium">Title</th>
              <th className="px-6 py-3 font-medium">Status</th>
              <th className="px-6 py-3 font-medium">Priority</th>
              <th className="px-6 py-3 font-medium">Assigned To</th>
              <th className="px-6 py-3 font-medium">Related To</th>
              <th className="px-6 py-3 font-medium">Last Modified</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {activityRows.map((task) => (
                <tr key={task.id} className="hover:bg-gray-50 cursor-pointer transition-colors group">
                  <td className="px-6 py-3" onClick={(e) => e.stopPropagation()}>
                    <input type="checkbox" className="rounded border-gray-300 text-black focus:ring-black" />
                  </td>
                  <td className="px-6 py-3">
                    <span className={`font-medium transition-colors ${task.status === 'Done' ? 'text-gray-400 line-through' : 'text-gray-900 group-hover:text-blue-600'}`}>
                      {task.title}
                    </span>
                  </td>
                  <td className="px-6 py-3">
                    <StatusBadge status={getStatusColor(task.status)} showText={false} />
                    <span className="ml-2 text-gray-700 font-medium">{task.status}</span>
                  </td>
                  <td className="px-6 py-3">{getPriorityBadge(task.priority)}</td>
                  <td className="px-6 py-3 text-gray-500">System</td>
                  <td className="px-6 py-3 text-gray-600">{task.relatedTo}</td>
                  <td className="px-6 py-3 text-gray-500">{task.lastModified}</td>
                </tr>
            ))}
          </tbody>
        </table>
        {!activityRows.length ? <div className="px-6 py-12 text-center text-sm text-gray-500">No activities match the current search yet.</div> : null}
      </div>

      <div className="bg-white px-4 py-3 border-t border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0 text-sm">
        <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-md">
          <button className="px-3 py-1 bg-white shadow-sm rounded text-gray-900 font-medium">20</button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">50</button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">100</button>
        </div>
        <div className="flex items-center gap-4">
          <button className="px-4 py-1.5 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors font-medium">Load More</button>
          <span className="text-gray-500">{activityRows.length} loaded</span>
        </div>
      </div>
    </div>
  );
}
