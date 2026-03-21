import React from 'react';
import {
  BuildingIcon,
  ChevronDownIcon,
  PlusIcon,
  SearchIcon,
  RefreshCwIcon,
  FilterIcon,
  ArrowUpDownIcon,
  ColumnsIcon,
  MoreHorizontalIcon,
  ExternalLinkIcon } from
'lucide-react';
import { organizations } from '../data/mockData';
import { Avatar } from './ui/Avatar';
import { AccountRecord } from '../types/crm';

interface OrganizationsListProps {
  records?: AccountRecord[];
}

export function OrganizationsList({ records }: OrganizationsListProps) {
  const accountRows = records && records.length > 0
    ? records.map((account) => ({
        id: String(account.id),
        name: account.name,
        website: account.website || '-',
        industry: account.industry || '-',
        territory: 'Tenant scoped',
        annualRevenue: 'Tracked in commerce',
        lastModified: new Date(account.createdAt).toLocaleDateString(),
      }))
    : organizations;
  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      {/* Header */}
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Accounts /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <BuildingIcon className="w-5 h-5 text-gray-400" />
            List
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2 hover:bg-gray-800 transition-colors">
          <PlusIcon className="w-4 h-4" />
          Create
        </button>
      </div>

      {/* Toolbar */}
      <div className="bg-white px-4 py-3 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="relative w-full lg:w-64">
          <input
            type="text"
            placeholder="ID"
            className="w-full pl-3 pr-10 py-1.5 bg-gray-100 border-transparent rounded-md text-sm focus:bg-white focus:border-gray-300 focus:ring-0 transition-colors" />
          
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

      {/* Table Area */}
      <div className="min-h-0 flex-1 overflow-auto bg-white">
        <table className="w-full text-sm text-left whitespace-nowrap">
          <thead className="text-xs text-gray-500 bg-gray-50 sticky top-0 z-10 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 font-medium w-12">
                <input
                  type="checkbox"
                  className="rounded border-gray-300 text-black focus:ring-black" />
                
              </th>
              <th className="px-6 py-3 font-medium">Name</th>
              <th className="px-6 py-3 font-medium">Website</th>
              <th className="px-6 py-3 font-medium">Industry</th>
              <th className="px-6 py-3 font-medium">Territory</th>
              <th className="px-6 py-3 font-medium">Annual Revenue</th>
              <th className="px-6 py-3 font-medium">Last Modified</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {accountRows.map((org) =>
            <tr
              key={org.id}
              className="hover:bg-gray-50 cursor-pointer transition-colors group">
              
                <td className="px-6 py-3" onClick={(e) => e.stopPropagation()}>
                  <input
                  type="checkbox"
                  className="rounded border-gray-300 text-black focus:ring-black" />
                
                </td>
                <td className="px-6 py-3">
                  <div className="flex items-center gap-3">
                    <Avatar
                    fallback={org.name}
                    size="md"
                    className="bg-gray-100 text-gray-500" />
                  
                    <span className="font-medium text-gray-900 group-hover:text-blue-600 transition-colors">
                      {org.name}
                    </span>
                  </div>
                </td>
                <td className="px-6 py-3">
                  <a
                  href={org.website === '-' ? '#' : org.website}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 hover:underline flex items-center gap-1"
                  onClick={(e) => e.stopPropagation()}>
                  
                    {org.website === '-' ? '-' : org.website.replace('https://', '')}
                    <ExternalLinkIcon className="w-3 h-3" />
                  </a>
                </td>
                <td className="px-6 py-3 text-gray-600">{org.industry}</td>
                <td className="px-6 py-3 text-gray-600">{org.territory}</td>
                <td className="px-6 py-3 text-gray-900 font-medium">
                  {org.annualRevenue}
                </td>
                <td className="px-6 py-3 text-gray-500">{org.lastModified}</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Footer Pagination */}
      <div className="bg-white px-4 py-3 border-t border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0 text-sm">
        <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-md">
          <button className="px-3 py-1 bg-white shadow-sm rounded text-gray-900 font-medium">
            20
          </button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">
            50
          </button>
          <button className="px-3 py-1 text-gray-600 hover:text-gray-900">
            100
          </button>
        </div>
        <div className="flex items-center gap-4">
          <button className="px-4 py-1.5 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors font-medium">
            Load More
          </button>
          <span className="text-gray-500">{accountRows.length} loaded</span>
        </div>
      </div>
    </div>);

}
