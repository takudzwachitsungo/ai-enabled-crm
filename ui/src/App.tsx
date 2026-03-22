import React, { useEffect, useState } from "react";
import { LogOutIcon, MenuIcon, RefreshCwIcon, ShieldAlertIcon, XIcon } from "lucide-react";
import { Sidebar } from "./components/Sidebar";
import { LeadsList } from "./components/LeadsList";
import { DealsKanban } from "./components/DealsKanban";
import { LeadDetail } from "./components/LeadDetail";
import { NotificationsList } from "./components/NotificationsList";
import { ContactsList } from "./components/ContactsList";
import { OrganizationsList } from "./components/OrganizationsList";
import { NotesList } from "./components/NotesList";
import { TasksList } from "./components/TasksList";
import { CallLogsList } from "./components/CallLogsList";
import { EmailTemplatesList } from "./components/EmailTemplatesList";
import { Dashboard } from "./components/Dashboard";
import { TicketList } from "./components/TicketList";
import { CommerceList } from "./components/CommerceList";
import { AiWorkspace } from "./components/AiWorkspace";
import { PlatformWorkspace } from "./components/PlatformWorkspace";
import { AuthScreen } from "./components/AuthScreen";
import { loadCrmSnapshot, loginWorkspace, registerWorkspace } from "./lib/api";
import { AuthSession, CrmSnapshot } from "./types/crm";

const STORAGE_KEY = "ai-enabled-crm-ui-session";

const DEFAULT_SESSION: AuthSession = {
  baseUrl: "http://localhost:8080",
  tenantId: "tenant-demo",
  username: "local-dev",
  password: "local-dev-pass",
};

const knownViews = [
  "dashboard",
  "leads",
  "deals",
  "lead-detail",
  "notifications",
  "contacts",
  "organizations",
  "tickets",
  "notes",
  "tasks",
  "call-logs",
  "commerce",
  "platform",
  "ai-workspace",
  "email-templates",
];

export function App() {
  const [currentView, setCurrentView] = useState<string>("dashboard");
  const [selectedLeadId, setSelectedLeadId] = useState<string | null>(null);
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [session, setSession] = useState<AuthSession | null>(() => {
    const saved = window.localStorage.getItem(STORAGE_KEY);
    if (!saved) {
      return null;
    }

    try {
      return JSON.parse(saved) as AuthSession;
    } catch {
      return null;
    }
  });
  const [snapshot, setSnapshot] = useState<CrmSnapshot | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleNavigate = (view: string) => {
    setCurrentView(view);
    setMobileNavOpen(false);
    if (view !== "lead-detail") {
      setSelectedLeadId(null);
    }
  };

  const handleLeadClick = (leadId: string) => {
    setSelectedLeadId(leadId);
    setCurrentView("lead-detail");
  };

  const handleBackToList = () => {
    setCurrentView("leads");
    setSelectedLeadId(null);
  };

  const connect = async (nextSession: AuthSession) => {
    setLoading(true);
    setError(null);

    try {
      const nextSnapshot = await loadCrmSnapshot(nextSession);
      setSession(nextSession);
      setSnapshot(nextSnapshot);
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(nextSession));
    } catch (err) {
      setSnapshot(null);
      setError(err instanceof Error ? err.message : "Unable to connect to the CRM backend.");
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (nextSession: AuthSession) => {
    setLoading(true);
    setError(null);

    try {
      await loginWorkspace(nextSession.baseUrl, {
        tenantId: nextSession.tenantId,
        email: nextSession.username,
        password: nextSession.password,
      });
      await connect(nextSession);
    } catch (err) {
      setSnapshot(null);
      setError(err instanceof Error ? err.message : "Unable to sign in to the workspace.");
      setLoading(false);
    }
  };

  const handleSignup = async (payload: {
    baseUrl: string;
    companyName: string;
    tenantId: string;
    fullName: string;
    email: string;
    password: string;
  }) => {
    setLoading(true);
    setError(null);

    try {
      await registerWorkspace(payload.baseUrl, payload);
      await connect({
        baseUrl: payload.baseUrl,
        tenantId: payload.tenantId,
        username: payload.email,
        password: payload.password,
      });
    } catch (err) {
      setSnapshot(null);
      setError(err instanceof Error ? err.message : "Unable to create the workspace.");
      setLoading(false);
    }
  };

  const refreshSnapshot = async () => {
    if (!session) {
      return;
    }
    await connect(session);
  };

  const handleSignOut = () => {
    setSession(null);
    setSnapshot(null);
    setError(null);
    setCurrentView("dashboard");
    setSelectedLeadId(null);
    window.localStorage.removeItem(STORAGE_KEY);
  };

  useEffect(() => {
    if (session) {
      void connect(session);
    }
  }, []);

  const renderCurrentView = () => {
    switch (currentView) {
      case "leads":
        return (
          <LeadsList
            onLeadClick={handleLeadClick}
            records={snapshot?.leads}
            session={session}
            onRefresh={refreshSnapshot}
          />
        );
      case "deals":
        return <DealsKanban records={snapshot?.opportunities} session={session} onRefresh={refreshSnapshot} />;
      case "lead-detail":
        return selectedLeadId ? (
          <LeadDetail
            leadId={selectedLeadId}
            onBack={handleBackToList}
            records={snapshot?.leads}
          />
        ) : null;
      case "dashboard":
        return (
          <Dashboard
            summary={snapshot?.summary}
            forecast={snapshot?.forecast}
            leadRecords={snapshot?.leads}
            opportunityRecords={snapshot?.opportunities}
            ticketRecords={snapshot?.tickets}
            activityRecords={snapshot?.activities}
            quoteRecords={snapshot?.quotes}
            invoiceRecords={snapshot?.invoices}
            aiInteractions={snapshot?.aiInteractions}
          />
        );
      case "notifications":
        return (
          <NotificationsList
            audienceSegments={snapshot?.audienceSegments}
            campaigns={snapshot?.campaigns}
            reportSnapshots={snapshot?.reportSnapshots}
          />
        );
      case "contacts":
        return <ContactsList records={snapshot?.contacts} session={session} onRefresh={refreshSnapshot} />;
      case "organizations":
        return <OrganizationsList records={snapshot?.accounts} session={session} onRefresh={refreshSnapshot} />;
      case "tickets":
        return <TicketList session={session} records={snapshot?.tickets} onRefresh={refreshSnapshot} />;
      case "notes":
        return <NotesList records={snapshot?.auditLogs} />;
      case "tasks":
        return <TasksList records={snapshot?.activities} session={session} onRefresh={refreshSnapshot} />;
      case "call-logs":
        return <CallLogsList records={snapshot?.communications} session={session} onRefresh={refreshSnapshot} />;
      case "commerce":
        return <CommerceList session={session} quotes={snapshot?.quotes} invoices={snapshot?.invoices} onRefresh={refreshSnapshot} />;
      case "platform":
        return (
          <PlatformWorkspace
            session={session}
            customEntityDefinitions={snapshot?.customEntityDefinitions}
            users={snapshot?.users}
            workflowDefinitions={snapshot?.workflowDefinitions}
            workflowCatalog={snapshot?.workflowCatalog}
            integrations={snapshot?.integrations}
            marketplaceApps={snapshot?.marketplaceApps}
            onRefresh={refreshSnapshot}
          />
        );
      case "ai-workspace":
        return <AiWorkspace session={session} interactions={snapshot?.aiInteractions} leads={snapshot?.leads} onRefresh={refreshSnapshot} />;
      case "email-templates":
        return (
          <EmailTemplatesList
            knowledgeArticles={snapshot?.knowledgeArticles}
            cannedResponses={snapshot?.cannedResponses}
            products={snapshot?.products}
          />
        );
      default:
        return (
          <div className="flex flex-1 items-center justify-center bg-[#f8f9fa] p-6">
            <div className="text-center">
              <h2 className="mb-2 text-2xl font-semibold text-gray-400">
                Coming Soon
              </h2>
              <p className="text-gray-500">The {currentView} view is under construction.</p>
            </div>
          </div>
        );
    }
  };

  if (!session) {
    return (
      <AuthScreen
        initialSession={DEFAULT_SESSION}
        error={error}
        loading={loading}
        onLogin={(nextSession) => {
          void handleLogin(nextSession);
        }}
        onSignup={(payload) => {
          void handleSignup(payload);
        }}
      />
    );
  }

  const displayTenant = snapshot?.identity?.tenantName ?? session.tenantId;
  const displayUser = snapshot?.identity?.fullName ?? snapshot?.identity?.username ?? session.username;

  return (
    <div className="flex h-screen w-full overflow-hidden bg-white font-sans text-gray-900 selection:bg-gray-200">
      <div className="hidden lg:flex">
        <Sidebar
          currentView={currentView}
          onNavigate={handleNavigate}
          tenantId={displayTenant}
          username={displayUser}
          onSignOut={handleSignOut}
        />
      </div>

      <div className="flex min-w-0 flex-1 flex-col">
        <div className="border-b border-gray-200 bg-white px-4 py-3 lg:hidden">
          <div className="flex items-center justify-between gap-3">
            <div>
              <div className="text-sm font-semibold text-gray-900">AI CRM</div>
              <div className="text-xs text-gray-500">
                {displayTenant} · {displayUser}
              </div>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={() => {
                  void connect(session);
                }}
                className="rounded-md border border-gray-200 p-2 text-gray-500 transition-colors hover:bg-gray-50 hover:text-gray-900"
                aria-label="Refresh data"
              >
                <RefreshCwIcon className="h-4 w-4" />
              </button>
              <button
                onClick={() => setMobileNavOpen((value) => !value)}
                className="rounded-md border border-gray-200 p-2 text-gray-500 transition-colors hover:bg-gray-50 hover:text-gray-900"
                aria-label="Toggle navigation"
              >
                {mobileNavOpen ? <XIcon className="h-4 w-4" /> : <MenuIcon className="h-4 w-4" />}
              </button>
            </div>
          </div>

          {mobileNavOpen ? (
            <div className="mt-3 space-y-3 border-t border-gray-100 pt-3">
              <div className="flex gap-2 overflow-x-auto pb-1">
                {knownViews
                  .filter((view) => view !== "lead-detail")
                  .map((view) => {
                    const label =
                      view === "deals"
                        ? "Opportunities"
                        : view === "organizations"
                          ? "Accounts"
                          : view === "tickets"
                            ? "Tickets"
                            : view === "tasks"
                              ? "Activities"
                              : view === "call-logs"
                                ? "Communications"
                                : view === "commerce"
                                  ? "Commerce"
                                  : view === "platform"
                                    ? "Platform"
                                    : view === "ai-workspace"
                                      ? "AI Workspace"
                                      : view === "notifications"
                                        ? "Campaigns"
                                        : view === "email-templates"
                                          ? "Library"
                                          : view === "notes"
                                            ? "Audit Notes"
                                            : view.charAt(0).toUpperCase() + view.slice(1).replace("-", " ");
                    const active =
                      currentView === view || (currentView === "lead-detail" && view === "leads");

                    return (
                      <button
                        key={view}
                        onClick={() => handleNavigate(view)}
                        className={`whitespace-nowrap rounded-md px-3 py-2 text-sm ${
                          active
                            ? "bg-gray-100 font-medium text-gray-900"
                            : "border border-gray-200 text-gray-600"
                        }`}
                      >
                        {label}
                      </button>
                    );
                  })}
              </div>

              <div className="flex gap-2">
                <button
                  onClick={handleSignOut}
                  className="flex items-center gap-2 rounded-md border border-gray-200 px-3 py-2 text-sm text-gray-600 transition-colors hover:bg-gray-50 hover:text-gray-900"
                >
                  <LogOutIcon className="h-4 w-4" />
                  Sign out
                </button>
              </div>
            </div>
          ) : null}
        </div>

        {error ? (
          <div className="border-b border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
            <div className="mx-auto flex max-w-7xl items-start gap-2">
              <ShieldAlertIcon className="mt-0.5 h-4 w-4 shrink-0" />
              <span>
                Showing the current session with the last loaded data. Refresh after the backend is reachable again.
                {` ${error}`}
              </span>
            </div>
          </div>
        ) : null}

        <div className="min-h-0 flex-1">{renderCurrentView()}</div>
      </div>
    </div>
  );
}
