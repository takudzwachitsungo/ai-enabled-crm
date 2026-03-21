import React from 'react';
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
import { callLogs, users } from '../data/mockData';
import { Avatar } from './ui/Avatar';
import { CommunicationRecord } from '../types/crm';

interface CallLogsListProps {
  records?: CommunicationRecord[];
}

export function CallLogsList({ records }: CallLogsListProps) {
  const communicationRows = records && records.length > 0
    ? records.map((record, index) => ({
        id: String(record.id),
        callerId: users[index % users.length]?.id ?? users[0].id,
        receiverName: record.participant,
        receiverPhone: record.channelType,
        type: record.direction === 'OUTBOUND' ? 'Outgoing' : record.direction === 'INBOUND' ? 'Incoming' : 'Missed',
        duration: record.relatedEntityType ? `${record.relatedEntityType} #${record.relatedEntityId ?? '-'}` : record.channelType,
        date: new Date(record.createdAt).toLocaleString(),
        note: record.subject || record.messageBody,
      }))
    : callLogs;

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
        <button className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="relative w-full lg:w-64">
          <input
            type="text"
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
              const caller = users.find((u) => u.id === log.callerId);
              return (
                <tr key={log.id} className="hover:bg-gray-50 cursor-pointer transition-colors group">
                  <td className="px-6 py-3" onClick={(e) => e.stopPropagation()}>
                    <input type="checkbox" className="rounded border-gray-300 text-black focus:ring-black" />
                  </td>
                  <td className="px-6 py-3">
                    {caller && (
                      <div className="flex items-center gap-3">
                        <Avatar src={caller.avatar} fallback={caller.name} size="md" />
                        <span className="font-medium text-gray-900 group-hover:text-blue-600 transition-colors">{caller.name}</span>
                      </div>
                    )}
                  </td>
                  <td className="px-6 py-3 text-gray-900 font-medium">{log.receiverName}</td>
                  <td className="px-6 py-3 text-gray-600">{log.receiverPhone}</td>
                  <td className="px-6 py-3">
                    <div className="flex items-center gap-2">
                      {getTypeIcon(log.type)}
                      <span className="text-gray-700 font-medium">{log.type}</span>
                    </div>
                  </td>
                  <td className="px-6 py-3 text-gray-600">{log.duration}</td>
                  <td className="px-6 py-3 text-gray-500">{log.date}</td>
                  <td className="px-6 py-3 text-gray-500 truncate max-w-[220px]">{log.note}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
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
