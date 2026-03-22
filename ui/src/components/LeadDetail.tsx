import React from 'react';
import {
  ChevronDownIcon,
  ExternalLinkIcon,
  MailIcon,
  PhoneIcon,
  UsersIcon,
} from 'lucide-react';
import { Avatar } from './ui/Avatar';
import { StatusBadge } from './ui/StatusBadge';
import { LeadRecord } from '../types/crm';

interface LeadDetailProps {
  leadId: string;
  onBack: () => void;
  records?: LeadRecord[];
}

export function LeadDetail({ leadId, onBack, records = [] }: LeadDetailProps) {
  const lead = records.find((item) => String(item.id) === leadId) ?? records[0];

  if (!lead) {
    return (
      <div className="flex min-h-0 flex-1 items-center justify-center bg-white p-6">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900">Lead not found</h2>
          <p className="mt-2 text-sm text-gray-500">Return to the lead list and select an available record.</p>
          <button
            onClick={onBack}
            className="mt-4 rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800"
          >
            Back to Leads
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-white overflow-hidden">
      <div className="px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <button
            onClick={onBack}
            className="text-gray-500 hover:text-gray-900 transition-colors"
          >
            Leads /
          </button>
          <span className="font-semibold text-gray-900">{lead.fullName}</span>
        </div>
        <div className="flex flex-wrap items-center gap-3 lg:justify-end">
          <button className="flex items-center gap-2 px-3 py-1.5 bg-orange-50 text-orange-600 rounded-md text-sm font-medium border border-orange-100 hover:bg-orange-100 transition-colors">
            <StatusBadge status={lead.status} showText={false} />
            {lead.status}
            <ChevronDownIcon className="w-4 h-4" />
          </button>
          <button className="bg-black text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-800 transition-colors">
            Convert to Opportunity
          </button>
        </div>
      </div>

      <div className="flex min-h-0 flex-1 flex-col xl:flex-row overflow-hidden">
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto p-4 sm:p-6 lg:p-8 custom-scrollbar">
          <div className="mb-8 grid grid-cols-1 gap-6 lg:grid-cols-[1.2fr_0.8fr]">
            <div className="rounded-xl border border-gray-200 bg-gray-50 p-6">
              <div className="flex items-start gap-4">
                <Avatar
                  src={`https://i.pravatar.cc/150?u=lead-detail-${lead.id}`}
                  fallback={lead.fullName}
                  size="xl"
                />
                <div className="min-w-0">
                  <div className="text-lg font-semibold text-gray-900">{lead.fullName}</div>
                  <div className="mt-1 text-sm text-gray-500">Lead record #{lead.id}</div>
                  <div className="mt-4 flex flex-wrap items-center gap-2">
                    <span className="inline-flex items-center gap-2 rounded-full bg-white px-3 py-1 text-xs text-gray-600">
                      <MailIcon className="h-3.5 w-3.5" />
                      {lead.email}
                    </span>
                    <span className="inline-flex items-center gap-2 rounded-full bg-white px-3 py-1 text-xs text-gray-600">
                      <UsersIcon className="h-3.5 w-3.5" />
                      Tenant scoped
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div className="rounded-xl border border-gray-200 bg-white p-6">
              <div className="text-sm font-medium text-gray-900">Quick actions</div>
              <div className="mt-4 space-y-3">
                <button className="flex w-full items-center gap-2 rounded-md border border-gray-200 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                  <MailIcon className="h-4 w-4" />
                  Send follow-up
                </button>
                <button className="flex w-full items-center gap-2 rounded-md border border-gray-200 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                  <PhoneIcon className="h-4 w-4" />
                  Log call
                </button>
                <button className="flex w-full items-center gap-2 rounded-md border border-gray-200 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                  <ExternalLinkIcon className="h-4 w-4" />
                  Open related workflow
                </button>
              </div>
            </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-6">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Lead overview</h2>
            </div>
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              <div className="space-y-4">
                <div>
                  <div className="text-xs text-gray-500">Full name</div>
                  <div className="mt-1 text-sm font-medium text-gray-900">{lead.fullName}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-500">Email</div>
                  <div className="mt-1 text-sm font-medium text-gray-900">{lead.email}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-500">Status</div>
                  <div className="mt-1 text-sm font-medium text-gray-900">{lead.status}</div>
                </div>
              </div>
              <div className="space-y-4">
                <div>
                  <div className="text-xs text-gray-500">Created</div>
                  <div className="mt-1 text-sm font-medium text-gray-900">{new Date(lead.createdAt).toLocaleString()}</div>
                </div>
                <div>
                  <div className="text-xs text-gray-500">Owner</div>
                  <div className="mt-1 text-sm font-medium text-gray-900">Unassigned</div>
                </div>
                <div>
                  <div className="text-xs text-gray-500">Organization</div>
                  <div className="mt-1 text-sm font-medium text-gray-400">Not captured yet</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="w-full xl:w-80 border-t xl:border-t-0 xl:border-l border-gray-200 bg-[#fcfcfc] overflow-y-auto custom-scrollbar shrink-0">
          <div className="p-6">
            <div className="text-sm font-medium text-gray-900 mb-6">Operational context</div>
            <div className="space-y-4">
              <div className="rounded-lg border border-gray-200 bg-white p-4">
                <div className="text-xs text-gray-500">Next recommended action</div>
                <div className="mt-2 text-sm text-gray-900">Create a follow-up activity and qualify for opportunity conversion.</div>
              </div>
              <div className="rounded-lg border border-gray-200 bg-white p-4">
                <div className="text-xs text-gray-500">Data quality</div>
                <div className="mt-2 text-sm text-gray-900">Core contact data present. Organization and phone can still be enriched.</div>
              </div>
              <div className="rounded-lg border border-gray-200 bg-white p-4">
                <div className="text-xs text-gray-500">Tenant visibility</div>
                <div className="mt-2 text-sm text-gray-900">This lead is available only within the active tenant context.</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
