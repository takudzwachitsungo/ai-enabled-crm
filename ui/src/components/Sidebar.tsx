import React, { useState } from "react";
import {
  UsersIcon,
  HandshakeIcon,
  ContactIcon,
  BuildingIcon,
  TicketIcon,
  CheckSquareIcon,
  PhoneIcon,
  MailIcon,
  ReceiptTextIcon,
  SparklesIcon,
  ChevronDownIcon,
  ChevronRightIcon,
  LayoutDashboardIcon,
  LogOutIcon,
  SettingsIcon,
  MegaphoneIcon,
  BookOpenIcon,
  type LucideIcon,
} from "lucide-react";

interface SidebarProps {
  currentView: string;
  onNavigate: (view: string) => void;
  tenantId?: string;
  username?: string;
  onSignOut?: () => void;
}

interface NavItem {
  id: string;
  label: string;
  icon: LucideIcon;
}

interface NavGroup {
  title: string;
  items: NavItem[];
  defaultOpen?: boolean;
}

const navGroups: NavGroup[] = [
  {
    title: "Overview",
    items: [
      { id: "dashboard", label: "Dashboard", icon: LayoutDashboardIcon },
    ],
    defaultOpen: true,
  },
  {
    title: "Sales",
    items: [
      { id: "leads", label: "Leads", icon: UsersIcon },
      { id: "deals", label: "Opportunities", icon: HandshakeIcon },
      { id: "contacts", label: "Contacts", icon: ContactIcon },
      { id: "organizations", label: "Accounts", icon: BuildingIcon },
    ],
    defaultOpen: true,
  },
  {
    title: "Service",
    items: [
      { id: "tickets", label: "Tickets", icon: TicketIcon },
      { id: "tasks", label: "Activities", icon: CheckSquareIcon },
      { id: "call-logs", label: "Communications", icon: PhoneIcon },
    ],
    defaultOpen: true,
  },
  {
    title: "Marketing",
    items: [
      { id: "notifications", label: "Campaigns", icon: MegaphoneIcon },
    ],
    defaultOpen: false,
  },
  {
    title: "Commerce",
    items: [
      { id: "commerce", label: "Quotes & Invoices", icon: ReceiptTextIcon },
    ],
    defaultOpen: false,
  },
  {
    title: "AI",
    items: [
      { id: "ai-workspace", label: "AI Workspace", icon: SparklesIcon },
    ],
    defaultOpen: false,
  },
  {
    title: "Library",
    items: [
      { id: "email-templates", label: "Knowledge Base", icon: BookOpenIcon },
      { id: "notes", label: "Audit Log", icon: MailIcon },
    ],
    defaultOpen: false,
  },
];

function getInitials(name: string) {
  return name
    .split(" ")
    .map((part) => part.charAt(0))
    .join("")
    .toUpperCase()
    .slice(0, 2);
}

function NavGroupSection({
  group,
  currentView,
  onNavigate,
}: {
  group: NavGroup;
  currentView: string;
  onNavigate: (view: string) => void;
}) {
  const [open, setOpen] = useState(group.defaultOpen ?? false);

  const hasActiveItem = group.items.some(
    (item) =>
      currentView === item.id ||
      (currentView === "lead-detail" && item.id === "leads"),
  );

  // Auto-open groups that contain the active item
  const isOpen = open || hasActiveItem;

  return (
    <div className="pb-1 pt-2">
      <button
        onClick={() => setOpen(!isOpen)}
        className="flex w-full items-center gap-2 px-3 py-1.5 text-xs font-semibold uppercase tracking-wider text-gray-400 hover:text-gray-600"
      >
        {isOpen ? (
          <ChevronDownIcon className="h-3 w-3" />
        ) : (
          <ChevronRightIcon className="h-3 w-3" />
        )}
        {group.title}
      </button>
      {isOpen && (
        <div className="mt-0.5 space-y-0.5">
          {group.items.map((item) => {
            const isActive =
              currentView === item.id ||
              (currentView === "lead-detail" && item.id === "leads");
            return (
              <button
                key={item.id}
                onClick={() => onNavigate(item.id)}
                className={`w-full rounded-md px-3 py-2 pl-7 transition-colors ${
                  isActive
                    ? "bg-gray-100 font-medium text-gray-900"
                    : "text-gray-600 hover:bg-gray-50"
                }`}
              >
                <div className="flex items-center gap-3">
                  <item.icon
                    className={`h-4 w-4 ${isActive ? "text-gray-900" : "text-gray-400"}`}
                  />
                  <span>{item.label}</span>
                </div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}

export function Sidebar({
  currentView,
  onNavigate,
  tenantId,
  username,
  onSignOut,
}: SidebarProps) {
  const displayName = username ?? "User";
  const displayWorkspace = tenantId ?? "Workspace";

  return (
    <div className="flex h-screen w-64 shrink-0 flex-col border-r border-gray-200 bg-[#fcfcfc] text-sm">
      {/* Header: Alad CRM + workspace name */}
      <div className="border-b border-gray-100 px-4 py-4">
        <div className="flex items-center gap-3">
          <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-gray-900 text-xs font-bold text-white">
            A
          </div>
          <div className="min-w-0 flex-1">
            <div className="truncate text-sm font-semibold text-gray-900">
              Alad CRM
            </div>
            <div className="truncate text-xs text-gray-500">
              {displayWorkspace}
            </div>
          </div>
        </div>
      </div>

      {/* Navigation groups */}
      <div className="custom-scrollbar flex-1 overflow-y-auto px-3 py-1">
        {navGroups.map((group) => (
          <NavGroupSection
            key={group.title}
            group={group}
            currentView={currentView}
            onNavigate={onNavigate}
          />
        ))}
      </div>

      {/* Footer: user profile + settings + sign out */}
      <div className="border-t border-gray-100 px-3 py-3">
        {/* User info */}
        <div className="mb-3 flex items-center gap-3 rounded-lg px-2 py-2">
          <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gray-200 text-xs font-semibold text-gray-600">
            {getInitials(displayName)}
          </div>
          <div className="min-w-0 flex-1">
            <div className="truncate text-sm font-medium text-gray-900">
              {displayName}
            </div>
            <div className="truncate text-xs text-gray-500">
              {displayWorkspace}
            </div>
          </div>
        </div>

        {/* Settings */}
        <button
          onClick={() => onNavigate("platform")}
          className={`mb-1 flex w-full items-center gap-3 rounded-md px-3 py-2 transition-colors ${
            currentView === "platform"
              ? "bg-gray-100 font-medium text-gray-900"
              : "text-gray-500 hover:bg-gray-50 hover:text-gray-700"
          }`}
        >
          <SettingsIcon className="h-4 w-4" />
          <span>Settings</span>
        </button>

        {/* Sign out */}
        {onSignOut ? (
          <button
            onClick={onSignOut}
            className="flex w-full items-center gap-3 rounded-md px-3 py-2 text-gray-500 transition-colors hover:bg-gray-50 hover:text-gray-700"
          >
            <LogOutIcon className="h-4 w-4" />
            <span>Sign out</span>
          </button>
        ) : null}
      </div>
    </div>
  );
}
