import React from 'react';
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
import { TicketRecord } from '../types/crm';

interface TicketListProps {
  records?: TicketRecord[];
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

export function TicketList({ records = [] }: TicketListProps) {
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
        <button className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="relative w-full lg:w-64">
          <input
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

      <div className="min-h-0 flex-1 overflow-auto bg-white">
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
            {records.map((ticket) => (
              <tr key={ticket.id} className="hover:bg-gray-50 transition-colors">
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
      </div>

      <div className="bg-white px-4 py-3 border-t border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0 text-sm">
        <div className="rounded-md bg-gray-100 px-3 py-1 text-gray-700">Service desk</div>
        <span className="text-gray-500">{records.length} loaded</span>
      </div>
    </div>
  );
}
