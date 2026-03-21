import React, { useState } from "react";
import {
  BellIcon,
  UsersIcon,
  HandshakeIcon,
  ContactIcon,
  BuildingIcon,
  TicketIcon,
  StickyNoteIcon,
  CheckSquareIcon,
  PhoneIcon,
  MailIcon,
  ReceiptTextIcon,
  ChevronDownIcon,
  ChevronRightIcon,
  PinIcon,
  LayoutListIcon,
  PanelLeftCloseIcon,
  LayoutDashboardIcon,
  LogOutIcon,
} from "lucide-react";

interface SidebarProps {
  currentView: string;
  onNavigate: (view: string) => void;
  tenantId?: string;
  username?: string;
  onSignOut?: () => void;
}

export function Sidebar({ currentView, onNavigate, tenantId, username, onSignOut }: SidebarProps) {
  const [publicViewsOpen, setPublicViewsOpen] = useState(true);
  const [pinnedViewsOpen, setPinnedViewsOpen] = useState(true);

  const navItems = [
    { id: "dashboard", label: "Dashboard", icon: LayoutDashboardIcon },
    { id: "notifications", label: "Notifications", icon: BellIcon, badge: "6" },
    { id: "leads", label: "Leads", icon: UsersIcon },
    { id: "deals", label: "Opportunities", icon: HandshakeIcon },
    { id: "contacts", label: "Contacts", icon: ContactIcon },
    { id: "organizations", label: "Accounts", icon: BuildingIcon },
    { id: "tickets", label: "Tickets", icon: TicketIcon },
    { id: "notes", label: "Audit Notes", icon: StickyNoteIcon },
    { id: "tasks", label: "Activities", icon: CheckSquareIcon },
    { id: "call-logs", label: "Communications", icon: PhoneIcon },
    { id: "commerce", label: "Commerce", icon: ReceiptTextIcon },
    { id: "email-templates", label: "Templates", icon: MailIcon },
  ];

  return (
    <div className="flex h-screen w-64 shrink-0 flex-col border-r border-gray-200 bg-[#fcfcfc] text-sm">
      <div className="cursor-pointer p-4 hover:bg-gray-50">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-pink-500 text-white">
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              </svg>
            </div>
            <div>
              <div className="leading-tight font-semibold text-gray-900">AI CRM</div>
              <div className="text-xs text-gray-500">{tenantId ?? "tenant-demo"}</div>
            </div>
          </div>
          <ChevronDownIcon className="h-4 w-4 text-gray-400" />
        </div>
      </div>

      <div className="flex-1 space-y-0.5 overflow-y-auto px-3 py-2">
        {navItems.map((item) => {
          const isActive =
            currentView === item.id || (currentView === "lead-detail" && item.id === "leads");
          return (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              className={`w-full rounded-md px-3 py-2 transition-colors ${
                isActive
                  ? "bg-gray-100 font-medium text-gray-900"
                  : "text-gray-600 hover:bg-gray-50"
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <item.icon
                    className={`h-4 w-4 ${isActive ? "text-gray-900" : "text-gray-400"}`}
                  />
                  <span>{item.label}</span>
                </div>
                {item.badge ? <span className="text-xs font-medium text-gray-500">{item.badge}</span> : null}
              </div>
            </button>
          );
        })}

        <div className="pb-1 pt-4">
          <button
            onClick={() => setPublicViewsOpen(!publicViewsOpen)}
            className="flex w-full items-center gap-2 px-3 py-1.5 text-xs font-medium text-gray-500 hover:text-gray-700"
          >
            {publicViewsOpen ? (
              <ChevronDownIcon className="h-3.5 w-3.5" />
            ) : (
              <ChevronRightIcon className="h-3.5 w-3.5" />
            )}
            Public views
          </button>
          {publicViewsOpen && (
            <div className="mt-1 space-y-0.5">
              <button className="w-full rounded-md px-3 py-2 pl-8 text-left text-gray-600 hover:bg-gray-50">
                <span>Pipeline overview</span>
              </button>
              <button className="w-full rounded-md px-3 py-2 pl-8 text-left text-gray-600 hover:bg-gray-50">
                <span>Service queue</span>
              </button>
              <button className="w-full rounded-md px-3 py-2 pl-8 text-left text-gray-600 hover:bg-gray-50">
                <span>Revenue watch</span>
              </button>
            </div>
          )}
        </div>

        <div className="pb-1 pt-2">
          <button
            onClick={() => setPinnedViewsOpen(!pinnedViewsOpen)}
            className="flex w-full items-center gap-2 px-3 py-1.5 text-xs font-medium text-gray-500 hover:text-gray-700"
          >
            {pinnedViewsOpen ? (
              <ChevronDownIcon className="h-3.5 w-3.5" />
            ) : (
              <ChevronRightIcon className="h-3.5 w-3.5" />
            )}
            Pinned views
          </button>
          {pinnedViewsOpen && (
            <div className="mt-1 space-y-0.5">
              <button className="w-full rounded-md px-3 py-2 pl-8 text-left text-gray-600 hover:bg-gray-50">
                <LayoutListIcon className="mr-3 inline h-4 w-4 text-blue-400" />
                Qualified leads
              </button>
              <button className="w-full rounded-md px-3 py-2 pl-8 text-left text-gray-600 hover:bg-gray-50">
                <LayoutListIcon className="mr-3 inline h-4 w-4 text-gray-400" />
                Open tickets
              </button>
              <button className="w-full rounded-md px-3 py-2 pl-8 text-left text-gray-600 hover:bg-gray-50">
                <PinIcon className="mr-3 inline h-4 w-4 text-gray-400" />
                Renewals this month
              </button>
              <button className="w-full rounded-md px-3 py-2 pl-8 text-left text-gray-600 hover:bg-gray-50">
                <PinIcon className="mr-3 inline h-4 w-4 text-gray-400" />
                AI follow-up queue
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="border-t border-gray-100 p-4">
        <div className="mb-4 rounded-lg border border-gray-200 bg-white px-3 py-3">
          <div className="text-sm font-medium text-gray-900">{username ?? "local-dev"}</div>
          <div className="text-xs text-gray-500">Authenticated workspace</div>
        </div>
        <button className="mb-3 flex items-center gap-2 text-sm font-medium text-gray-500 transition-colors hover:text-gray-900">
          <PanelLeftCloseIcon className="h-4 w-4" />
          Collapse
        </button>
        {onSignOut ? (
          <button
            onClick={onSignOut}
            className="flex items-center gap-2 text-sm font-medium text-gray-500 transition-colors hover:text-gray-900"
          >
            <LogOutIcon className="h-4 w-4" />
            Sign out
          </button>
        ) : null}
      </div>
    </div>
  );
}
