import React, { useMemo, useState } from 'react';
import {
  StickyNoteIcon,
  ChevronDownIcon,
  RefreshCwIcon,
  FilterIcon,
  ArrowUpDownIcon,
  MoreHorizontalIcon,
} from 'lucide-react';
import { AuditLogRecord } from '../types/crm';

interface NotesListProps {
  records?: AuditLogRecord[];
}

export function NotesList({ records = [] }: NotesListProps) {
  const [query, setQuery] = useState('');

  const noteCards = useMemo(
    () =>
      records
        .map((record) => ({
          id: String(record.id),
          title: `${record.action} ${record.entityType}`.trim(),
          content: record.summary,
          author: record.actor || 'System',
          createdAt: new Date(record.createdAt).toLocaleDateString(),
        }))
        .filter((record) =>
          [record.title, record.content, record.author].join(' ').toLowerCase().includes(query.toLowerCase()),
        ),
    [query, records],
  );

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Audit Notes /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <StickyNoteIcon className="w-5 h-5 text-gray-400" />
            List
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
      </div>

      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="relative w-full lg:w-64">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search audit notes..."
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
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto bg-[#f8f9fa] p-4 sm:p-6">
        {noteCards.length === 0 ? (
          <div className="rounded-xl border border-dashed border-gray-300 bg-white px-6 py-16 text-center text-sm text-gray-500">
            No audit notes match the current search yet.
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {noteCards.map((note) => (
              <div key={note.id} className="bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow cursor-pointer flex flex-col h-48">
                <div className="flex items-start justify-between mb-3">
                  <h3 className="font-semibold text-gray-900 line-clamp-2 leading-tight">{note.title}</h3>
                  <button className="text-gray-400 hover:text-gray-600 shrink-0 ml-2">
                    <MoreHorizontalIcon className="w-4 h-4" />
                  </button>
                </div>

                <p className="text-sm text-gray-500 line-clamp-3 mb-auto leading-relaxed">{note.content}</p>

                <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
                  <span className="text-xs font-medium text-gray-700">{note.author}</span>
                  <span className="text-xs text-gray-400">{note.createdAt}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
