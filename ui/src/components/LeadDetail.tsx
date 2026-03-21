import React from 'react';
import {
  ChevronDownIcon,
  ActivityIcon,
  MailIcon,
  MessageSquareIcon,
  PhoneIcon,
  CheckSquareIcon,
  StickyNoteIcon,
  PaperclipIcon,
  PlusIcon,
  ReplyIcon,
  CornerUpLeftIcon,
  ExternalLinkIcon,
  MessageCircleIcon,
  UsersIcon } from
'lucide-react';
import { leads, users } from '../data/mockData';
import { Avatar } from './ui/Avatar';
import { StatusBadge } from './ui/StatusBadge';
import { LeadRecord } from '../types/crm';
interface LeadDetailProps {
  leadId: string;
  onBack: () => void;
  records?: LeadRecord[];
}
export function LeadDetail({ leadId, onBack, records }: LeadDetailProps) {
  const leadRecords = records && records.length > 0
    ? records.map((lead, index) => ({
        id: String(lead.id),
        name: lead.fullName,
        organization: '',
        status: lead.status,
        email: lead.email,
        mobile: '',
        assignedToId: users[index % users.length]?.id ?? users[0].id,
        lastModified: new Date(lead.createdAt).toLocaleDateString(),
        avatar: `https://i.pravatar.cc/150?u=lead-detail-${lead.id}`,
      }))
    : leads;
  const lead = leadRecords.find((l) => l.id === leadId) || leadRecords[0];
  const assignedUser = users.find((u) => u.id === lead.assignedToId);
  return (
    <div className="flex min-h-0 flex-1 flex-col bg-white overflow-hidden">
      {/* Header */}
      <div className="px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 lg:flex-row lg:items-center lg:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <button
            onClick={onBack}
            className="text-gray-500 hover:text-gray-900 transition-colors">
            
            Leads /
          </button>
          <span className="font-semibold text-gray-900">{lead.name}</span>
        </div>
        <div className="flex flex-wrap items-center gap-3 lg:justify-end">
          {assignedUser &&
          <div className="flex items-center gap-2">
              <Avatar
              src={assignedUser.avatar}
              fallback={assignedUser.name}
              size="sm" />
            
              <span className="text-sm text-gray-600">{assignedUser.name}</span>
            </div>
          }
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

      {/* Tabs */}
      <div className="px-4 sm:px-6 border-b border-gray-200 flex items-center gap-6 shrink-0 overflow-x-auto">
        {[
        {
          id: 'activity',
          label: 'Activity',
          icon: ActivityIcon,
          active: true
        },
        {
          id: 'emails',
          label: 'Emails',
          icon: MailIcon
        },
        {
          id: 'comments',
          label: 'Comments',
          icon: MessageSquareIcon
        },
        {
          id: 'calls',
          label: 'Calls',
          icon: PhoneIcon
        },
        {
          id: 'tasks',
          label: 'Tasks',
          icon: CheckSquareIcon
        },
        {
          id: 'notes',
          label: 'Notes',
          icon: StickyNoteIcon
        },
        {
          id: 'attachments',
          label: 'Attachments',
          icon: PaperclipIcon
        },
        {
          id: 'whatsapp',
          label: 'WhatsApp',
          icon: MessageCircleIcon
        }].
        map((tab) =>
        <button
          key={tab.id}
          className={`flex items-center gap-2 py-3 text-sm font-medium border-b-2 transition-colors whitespace-nowrap ${tab.active ? 'border-black text-black' : 'border-transparent text-gray-500 hover:text-gray-800'}`}>
          
            <tab.icon className="w-4 h-4" />
            {tab.label}
          </button>
        )}
      </div>

      {/* Main Content Area */}
      <div className="flex min-h-0 flex-1 flex-col xl:flex-row overflow-hidden">
        {/* Left: Activity Timeline */}
        <div className="flex min-h-0 flex-1 flex-col relative">
          <div className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 custom-scrollbar">
            <div className="flex items-center justify-between mb-8">
              <h2 className="text-lg font-semibold text-gray-900">Activity</h2>
              <button className="flex items-center gap-2 px-3 py-1.5 bg-black text-white rounded-md text-sm font-medium hover:bg-gray-800 transition-colors">
                <PlusIcon className="w-4 h-4" /> New{' '}
                <ChevronDownIcon className="w-4 h-4" />
              </button>
            </div>

            {/* Timeline */}
            <div className="relative pl-8 space-y-8 before:absolute before:inset-0 before:ml-[15px] before:-translate-x-px md:before:mx-auto md:before:translate-x-0 before:h-full before:w-0.5 before:bg-gray-200">
              {/* Timeline Item: Creation */}
              <div className="relative flex flex-col gap-2 pr-0 sm:flex-row sm:items-center sm:justify-between sm:gap-4 group">
                <div className="absolute left-0 w-8 h-8 flex items-center justify-center -translate-x-1/2 bg-white">
                  <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center text-gray-500">
                    <UsersIcon className="w-3 h-3" />
                  </div>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <span className="font-medium text-gray-900">
                    Shariq Ansari
                  </span>
                  <span className="text-gray-500">created this lead</span>
                </div>
                <span className="text-xs text-gray-400">last year</span>
              </div>

              {/* Timeline Item: Attachment */}
              <div className="relative flex flex-col gap-2 pr-0 sm:flex-row sm:items-center sm:justify-between sm:gap-4 group">
                <div className="absolute left-0 w-8 h-8 flex items-center justify-center -translate-x-1/2 bg-white">
                  <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center text-gray-500">
                    <PaperclipIcon className="w-3 h-3" />
                  </div>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <span className="font-medium text-gray-900">
                    Shariq Ansari
                  </span>
                  <span className="text-gray-500">added</span>
                  <span className="font-medium text-gray-900">
                    Frame 482152 (18).png
                  </span>
                </div>
                <span className="text-xs text-gray-400">last year</span>
              </div>

              {/* Timeline Item: Changes */}
              <div className="relative group">
                <div className="absolute left-0 w-8 h-8 flex items-center justify-center -translate-x-1/2 bg-white">
                  <div className="w-2 h-2 rounded-full bg-gray-300"></div>
                </div>
                <div className="flex items-center justify-between mb-2">
                  <button className="flex items-center gap-2 text-sm font-medium text-gray-900 hover:text-gray-600">
                    Hide +2 changes from Shariq Ansari{' '}
                    <ChevronDownIcon className="w-4 h-4" />
                  </button>
                  <span className="text-xs text-gray-400">9 months ago</span>
                </div>
                <div className="pl-4 border-l-2 border-gray-100 space-y-2 text-sm text-gray-600">
                  <div className="flex items-center gap-2">
                    <span className="w-24 text-gray-400">Image</span> → Added{' '}
                    <span className="font-medium text-gray-900">
                      /files/avatar (23).png
                    </span>
                    <span className="ml-auto text-xs text-gray-400">
                      9 months ago
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-24 text-gray-400">Annual Revenue</span> →
                    Added{' '}
                    <span className="font-medium text-gray-900">
                      45,00,000.00
                    </span>
                    <span className="ml-auto text-xs text-gray-400">
                      last year
                    </span>
                  </div>
                </div>
              </div>

              {/* Timeline Item: Email */}
              <div className="relative group">
                <div className="absolute left-0 w-8 h-8 flex items-center justify-center -translate-x-1/2 bg-white">
                  <Avatar
                    src="https://i.pravatar.cc/150?u=u1"
                    fallback="SA"
                    size="md"
                    className="ring-4 ring-white" />
                  
                </div>
                <div className="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-medium text-gray-900">
                          Shariq Ansari
                        </span>
                        <span className="text-sm text-gray-500">
                          &lt;shariq@erpnext.com&gt;
                        </span>
                      </div>
                      <div className="text-sm font-medium text-gray-900 mb-1">
                        None (#CRM-LEAD-2023-00024)
                      </div>
                      <div className="text-sm text-gray-500">
                        To: {lead.email}
                      </div>
                    </div>
                    <div className="flex items-center gap-3 text-gray-400">
                      <span className="text-xs">8 months ago</span>
                      <button className="hover:text-gray-600">
                        <CornerUpLeftIcon className="w-4 h-4" />
                      </button>
                      <button className="hover:text-gray-600">
                        <ReplyIcon className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                  <div className="text-sm text-gray-700 space-y-4 leading-relaxed">
                    <p>Dear {lead.name.split(' ').pop()},</p>
                    <p>
                      I hope this email finds you well. I wanted to express my
                      gratitude for taking the time to speak with me earlier
                      today regarding your interest in Frappe CRM for None. It
                      was a pleasure learning more about your company.
                    </p>
                    <p>
                      I'd love the opportunity to continue our discussion and
                      provide a personalised demonstration of our platform to
                      address your specific needs and questions. When would be a
                      convenient time for you to schedule a brief demo?
                    </p>
                    <p>
                      Thank you again for considering Frappe CRM. I look forward
                      to the possibility of working with None to achieve your
                      marketing goals.
                    </p>
                    <p className="text-gray-500">Warm regards,</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Sticky Bottom Bar */}
          <div className="absolute bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4 flex flex-wrap items-center gap-3">
            <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 rounded-md transition-colors">
              <MailIcon className="w-4 h-4" /> Reply
            </button>
            <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 rounded-md transition-colors">
              <MessageSquareIcon className="w-4 h-4" /> Comment
            </button>
          </div>
        </div>

        {/* Right Sidebar: Details */}
        <div className="w-full xl:w-80 border-t xl:border-t-0 xl:border-l border-gray-200 bg-[#fcfcfc] overflow-y-auto custom-scrollbar shrink-0">
          <div className="p-6">
            <div className="text-sm font-medium text-gray-900 mb-6">
              CRM-LEAD-2023-00024
            </div>

            <div className="flex flex-col items-center text-center mb-8">
              <Avatar
                src={lead.avatar}
                fallback={lead.name}
                size="xl"
                className="mb-4" />
              
              <h2 className="text-xl font-semibold text-gray-900 mb-4">
                {lead.name}
              </h2>
              <div className="flex items-center gap-2">
                <button className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200 transition-colors">
                  <PhoneIcon className="w-4 h-4" />
                </button>
                <button className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200 transition-colors">
                  <MailIcon className="w-4 h-4" />
                </button>
                <button className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200 transition-colors">
                  <ExternalLinkIcon className="w-4 h-4" />
                </button>
                <button className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200 transition-colors">
                  <PaperclipIcon className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Details Section */}
            <div className="mb-6">
              <div className="flex items-center justify-between mb-4 cursor-pointer group">
                <div className="flex items-center gap-2 font-medium text-gray-900">
                  <ChevronDownIcon className="w-4 h-4 text-gray-400" /> Details
                </div>
                <ExternalLinkIcon className="w-4 h-4 text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity" />
              </div>
              <div className="space-y-4 pl-6">
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Organization</span>
                  <span className="text-sm text-gray-900 font-medium">
                    {lead.organization || '-'}
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Website</span>
                  <span className="text-sm text-gray-900 font-medium truncate">
                    {lead.organization ?
                    `https://${lead.organization.toLowerCase().replace(/\s+/g, '-')}.com` :
                    '-'}
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Territory</span>
                  <span className="text-sm text-gray-400">
                    Select Territory...
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Industry</span>
                  <span className="text-sm text-gray-900 font-medium">
                    Others
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Job Title</span>
                  <span className="text-sm text-gray-900 font-medium">
                    Admin
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Source</span>
                  <span className="text-sm text-gray-900 font-medium">
                    Advertisement
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Lead Owner</span>
                  {assignedUser ?
                  <div className="flex items-center gap-2 mt-1">
                      <Avatar
                      src={assignedUser.avatar}
                      fallback={assignedUser.name}
                      size="sm" />
                    
                      <span className="text-sm text-gray-900 font-medium">
                        {assignedUser.name}
                      </span>
                    </div> :

                  <span className="text-sm text-gray-400">-</span>
                  }
                </div>
              </div>
            </div>

            {/* Person Section */}
            <div>
              <div className="flex items-center justify-between mb-4 cursor-pointer group">
                <div className="flex items-center gap-2 font-medium text-gray-900">
                  <ChevronDownIcon className="w-4 h-4 text-gray-400" /> Person
                </div>
              </div>
              <div className="space-y-4 pl-6">
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Salutation</span>
                  <span className="text-sm text-gray-900 font-medium">
                    {lead.name.split(' ')[0]}
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">
                    First Name <span className="text-red-500">*</span>
                  </span>
                  <span className="text-sm text-gray-900 font-medium">
                    {lead.name.split(' ')[1]}
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Last Name</span>
                  <span className="text-sm text-gray-900 font-medium">
                    {lead.name.split(' ').slice(2).join(' ')}
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Email</span>
                  <span className="text-sm text-gray-900 font-medium truncate">
                    {lead.email}
                  </span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-gray-500">Mobile No</span>
                  <span className="text-sm text-gray-900 font-medium">
                    {lead.mobile}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>);

}
