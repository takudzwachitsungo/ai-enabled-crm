import React from 'react';
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
  AtSignIcon } from
'lucide-react';
import { deals, users } from '../data/mockData';
import { Avatar } from './ui/Avatar';
import { StatusBadge } from './ui/StatusBadge';
import { OpportunityRecord } from '../types/crm';

interface DealsKanbanProps {
  records?: OpportunityRecord[];
}

export function DealsKanban({ records }: DealsKanbanProps) {
  const dealRows = records && records.length > 0
    ? records.map((deal, index) => ({
        id: String(deal.id),
        title: deal.name,
        email: '',
        phone: '',
        amount: typeof deal.amount === 'number' ? `$${deal.amount.toLocaleString()}` : '',
        assignedToId: users[index % users.length]?.id ?? users[0].id,
        timeAgo: new Date(deal.createdAt).toLocaleDateString(),
        status: deal.stage === 'PROPOSAL' ? 'Proposal/Quotation' : deal.stage === 'NEGOTIATION' ? 'Negotiation' : deal.stage === 'QUALIFIED' ? 'Qualification' : 'Ready to Close',
        avatar: '',
      }))
    : deals;
  const columns = [
  {
    id: 'Qualification',
    title: 'Qualification',
    status: 'Qualification'
  },
  {
    id: 'Proposal/Quotation',
    title: 'Proposal/Quotation',
    status: 'Proposal/Quotation'
  },
  {
    id: 'Negotiation',
    title: 'Negotiation',
    status: 'Negotiation'
  },
  {
    id: 'Ready to Close',
    title: 'Ready to Close',
    status: 'Ready to Close'
  }];

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      {/* Header */}
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Opportunities /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <KanbanIcon className="w-5 h-5 text-gray-400" />
            Kanban
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      {/* Filter Bar */}
      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="flex flex-wrap items-center gap-3">
          <input
            type="text"
            placeholder="ID"
            className="w-32 px-3 py-1.5 bg-gray-100 border-transparent rounded-md text-sm focus:bg-white focus:border-gray-300 focus:ring-0 transition-colors" />
          
          <button className="flex items-center justify-between w-40 px-3 py-1.5 bg-gray-100 text-gray-500 rounded-md text-sm hover:bg-gray-200 transition-colors">
            Organization <ChevronDownIcon className="w-4 h-4" />
          </button>
          <button className="flex items-center justify-between w-32 px-3 py-1.5 bg-gray-100 text-gray-500 rounded-md text-sm hover:bg-gray-200 transition-colors">
            Territory <ChevronDownIcon className="w-4 h-4" />
          </button>
          <button className="flex items-center justify-between w-32 px-3 py-1.5 bg-gray-100 text-gray-500 rounded-md text-sm hover:bg-gray-200 transition-colors">
            Status <ChevronDownIcon className="w-4 h-4" />
          </button>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <button className="p-1.5 text-gray-500 hover:bg-gray-100 rounded-md transition-colors border border-gray-200">
            <RefreshCwIcon className="w-4 h-4" />
          </button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200">
            <FilterIcon className="w-4 h-4" /> Filter
          </button>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200 bg-gray-50">
            <ColumnsIcon className="w-4 h-4" /> Kanban Settings
          </button>
        </div>
      </div>

      {/* Kanban Board */}
      <div className="min-h-0 flex-1 overflow-x-auto overflow-y-hidden p-4 sm:p-6">
        <div className="flex gap-6 h-full items-start">
          {columns.map((col) => {
            const columnDeals = dealRows.filter((d) => d.status === col.status);
            return (
              <div
                key={col.id}
                className="w-80 shrink-0 flex flex-col max-h-full">
                
                {/* Column Header */}
                <div className="flex items-center justify-between mb-4 px-1">
                  <StatusBadge status={col.status} />
                  <button className="text-gray-400 hover:text-gray-600">
                    <PlusIcon className="w-5 h-5" />
                  </button>
                </div>

                {/* Cards Container */}
                <div className="flex-1 overflow-y-auto space-y-4 pb-4 pr-2 custom-scrollbar">
                  {columnDeals.map((deal) => {
                    const assignedUser = users.find(
                      (u) => u.id === deal.assignedToId
                    );
                    return (
                      <div
                        key={deal.id}
                        className="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-shadow p-4 cursor-pointer">
                        
                        <div className="flex items-center gap-3 mb-3">
                          <Avatar
                            src={deal.avatar}
                            fallback={deal.title}
                            size="md"
                            className="bg-gray-100" />
                          
                          <h3 className="font-medium text-gray-900 truncate">
                            {deal.title}
                          </h3>
                        </div>

                        <div className="space-y-2 mb-4">
                          {deal.amount &&
                          <div className="text-gray-900 font-medium">
                              {deal.amount}
                            </div>
                          }
                          {deal.email &&
                          <div className="text-sm text-gray-600 truncate">
                              {deal.email}
                            </div>
                          }
                          {deal.phone &&
                          <div className="text-sm text-gray-600">
                              {deal.phone}
                            </div>
                          }
                        </div>

                        <div className="flex items-center gap-2 mb-4">
                          {assignedUser &&
                          <>
                              <Avatar
                              src={assignedUser.avatar}
                              fallback={assignedUser.name}
                              size="sm" />
                            
                              <span className="text-sm text-gray-600">
                                {assignedUser.name}
                              </span>
                            </>
                          }
                        </div>

                        <div className="text-xs text-gray-500 mb-4">
                          {deal.timeAgo}
                        </div>

                        <div className="flex items-center justify-between pt-3 border-t border-gray-100">
                          <div className="flex items-center gap-3 text-gray-400">
                            <button className="hover:text-gray-600">
                              <AtSignIcon className="w-4 h-4" />
                            </button>
                            <span className="w-1 h-1 rounded-full bg-gray-300"></span>
                            <button className="hover:text-gray-600">
                              <CopyIcon className="w-4 h-4" />
                            </button>
                            <span className="w-1 h-1 rounded-full bg-gray-300"></span>
                            <button className="hover:text-gray-600">
                              <CheckCircle2Icon className="w-4 h-4" />
                            </button>
                            <span className="w-1 h-1 rounded-full bg-gray-300"></span>
                            <button className="hover:text-gray-600">
                              <MessageSquareIcon className="w-4 h-4" />
                            </button>
                          </div>
                          <button className="text-gray-400 hover:text-gray-600">
                            <PlusIcon className="w-4 h-4" />
                          </button>
                        </div>
                      </div>);

                  })}
                </div>
              </div>);

          })}
        </div>
      </div>
    </div>);

}
