import React from "react";
import {
  ActivityIcon,
  AlertTriangleIcon,
  BotIcon,
  BriefcaseBusinessIcon,
  CircleDollarSignIcon,
  CircleIcon,
  FileTextIcon,
  Layers3Icon,
  MoreHorizontalIcon,
  ShieldCheckIcon,
  TicketIcon,
  UsersIcon,
  WorkflowIcon,
} from "lucide-react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip as RechartsTooltip,
  XAxis,
  YAxis,
} from "recharts";
import {
  ActivityRecord,
  AiInteractionRecord,
  DashboardForecastResponse,
  DashboardSummaryResponse,
  InvoiceRecord,
  LeadRecord,
  OpportunityRecord,
  QuoteRecord,
  TicketRecord,
} from "../types/crm";

interface DashboardProps {
  summary?: DashboardSummaryResponse | null;
  forecast?: DashboardForecastResponse | null;
  leadRecords?: LeadRecord[] | null;
  opportunityRecords?: OpportunityRecord[] | null;
  ticketRecords?: TicketRecord[] | null;
  activityRecords?: ActivityRecord[] | null;
  quoteRecords?: QuoteRecord[] | null;
  invoiceRecords?: InvoiceRecord[] | null;
  aiInteractions?: AiInteractionRecord[] | null;
}

type ChartDatum = {
  name: string;
  value: number;
  fill: string;
};

function formatCurrency(value?: number | null) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 0,
  }).format(value ?? 0);
}

function formatNumber(value?: number | null) {
  return new Intl.NumberFormat("en-US").format(value ?? 0);
}

function formatDateTime(value?: string | null) {
  if (!value) {
    return "Not available";
  }

  return new Date(value).toLocaleString();
}

function buildLeadStatusData(records?: LeadRecord[] | null): ChartDatum[] {
  const buckets = new Map<string, number>();
  (records ?? []).forEach((record) => {
    const key = record.status || "Unspecified";
    buckets.set(key, (buckets.get(key) ?? 0) + 1);
  });

  const palette = ["#9ca3af", "#22c55e", "#f97316", "#3b82f6", "#8b5cf6", "#ef4444"];
  return Array.from(buckets.entries()).map(([name, value], index) => ({
    name,
    value,
    fill: palette[index % palette.length],
  }));
}

function buildOpportunityStageData(records?: OpportunityRecord[] | null): ChartDatum[] {
  const buckets = new Map<string, number>();
  (records ?? []).forEach((record) => {
    const key = record.stage || "Unspecified";
    buckets.set(key, (buckets.get(key) ?? 0) + 1);
  });

  const palette = ["#9ca3af", "#22c55e", "#3b82f6", "#f97316", "#8b5cf6", "#ef4444"];
  return Array.from(buckets.entries()).map(([name, value], index) => ({
    name,
    value,
    fill: palette[index % palette.length],
  }));
}

function EmptyChartState({
  title,
  description,
}: {
  title: string;
  description: string;
}) {
  return (
    <div className="flex h-64 items-center justify-center rounded-xl border border-dashed border-gray-200 bg-gray-50 px-6 text-center">
      <div>
        <div className="text-sm font-semibold text-gray-800">{title}</div>
        <p className="mt-2 max-w-xs text-sm text-gray-500">{description}</p>
      </div>
    </div>
  );
}

export function Dashboard({
  summary,
  forecast,
  leadRecords,
  opportunityRecords,
  ticketRecords,
  activityRecords,
  quoteRecords,
  invoiceRecords,
  aiInteractions,
}: DashboardProps) {
  const leadStatusData = buildLeadStatusData(leadRecords);
  const opportunityStageData = buildOpportunityStageData(opportunityRecords);
  const quotePipelineValue = (quoteRecords ?? []).reduce((sum, quote) => sum + quote.amount, 0);
  const invoiceValue = (invoiceRecords ?? []).reduce((sum, invoice) => sum + invoice.amount, 0);
  const overdueTickets = (ticketRecords ?? []).filter((ticket) => {
    if (!ticket.dueAt || ticket.status === "RESOLVED") {
      return false;
    }

    return new Date(ticket.dueAt).getTime() < Date.now();
  });
  const dueSoonInvoices = (invoiceRecords ?? []).filter((invoice) => {
    if (!invoice.dueAt || invoice.status === "PAID") {
      return false;
    }

    const dueAt = new Date(invoice.dueAt).getTime();
    const withinSevenDays = Date.now() + 7 * 24 * 60 * 60 * 1000;
    return dueAt <= withinSevenDays;
  });
  const recentActivities = (activityRecords ?? [])
    .slice()
    .sort((left, right) => new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime())
    .slice(0, 4);
  const recentAiItems = (aiInteractions ?? [])
    .slice()
    .sort((left, right) => new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime())
    .slice(0, 3);
  const pipelineRegister = (opportunityRecords ?? [])
    .slice()
    .sort((left, right) => right.amount - left.amount)
    .slice(0, 5);

  const summaryCards = [
    {
      label: "Total Leads",
      value: formatNumber(summary?.leadCount),
      note: "Current tenant records",
      icon: UsersIcon,
      tone: "bg-blue-50 text-blue-600",
    },
    {
      label: "Open Opportunities",
      value: formatNumber(summary?.opportunityCount),
      note: "Active commercial pipeline",
      icon: BriefcaseBusinessIcon,
      tone: "bg-purple-50 text-purple-600",
    },
    {
      label: "Revenue Forecast",
      value: formatCurrency(forecast?.projectedRevenueAmount),
      note: "Projected tenant revenue",
      icon: CircleDollarSignIcon,
      tone: "bg-green-50 text-green-600",
    },
    {
      label: "Open Tickets",
      value: formatNumber(summary?.ticketCount),
      note: `${formatNumber(summary?.overdueTicketCount)} overdue`,
      icon: TicketIcon,
      tone: "bg-orange-50 text-orange-600",
    },
  ];

  const revenueBreakdownData = [
    { name: "Pipeline", value: forecast?.totalPipelineAmount ?? 0, fill: "#3b82f6" },
    { name: "Weighted", value: forecast?.weightedPipelineAmount ?? 0, fill: "#22c55e" },
    { name: "Quotes", value: forecast?.activeQuoteAmount ?? 0, fill: "#f97316" },
    { name: "Collected", value: forecast?.collectedInvoiceAmount ?? 0, fill: "#8b5cf6" },
  ];

  const operationsTable = [
    {
      label: "Activities",
      value: formatNumber(summary?.activityCount),
      state: "Operational",
      icon: ActivityIcon,
    },
    {
      label: "Workflows",
      value: formatNumber(summary?.activeWorkflowCount),
      state: "Automation",
      icon: WorkflowIcon,
    },
    {
      label: "Integrations",
      value: formatNumber(summary?.integrationConnectionCount),
      state: "Connected",
      icon: Layers3Icon,
    },
    {
      label: "AI Interactions",
      value: formatNumber(summary?.aiInteractionCount),
      state: "Traceable",
      icon: ShieldCheckIcon,
    },
  ];

  const commercialFocus = [
    {
      label: "Pipeline Total",
      value: formatCurrency(forecast?.totalPipelineAmount),
    },
    {
      label: "Weighted Pipeline",
      value: formatCurrency(forecast?.weightedPipelineAmount),
    },
    {
      label: "Active Quotes",
      value: formatCurrency(forecast?.activeQuoteAmount),
    },
    {
      label: "Issued Invoices",
      value: formatCurrency(forecast?.issuedInvoiceAmount),
    },
    {
      label: "Collected Invoices",
      value: formatCurrency(forecast?.collectedInvoiceAmount),
    },
    {
      label: "Quote Book",
      value: formatCurrency(quotePipelineValue),
    },
    {
      label: "Invoice Book",
      value: formatCurrency(invoiceValue),
    },
  ];

  const readinessItems = [
    {
      title: "CRM data coverage",
      status:
        (summary?.leadCount ?? 0) +
          (summary?.contactCount ?? 0) +
          (summary?.accountCount ?? 0) +
          (summary?.opportunityCount ?? 0) >
        0
          ? "In progress"
          : "Needs data",
      detail: `${formatNumber(summary?.leadCount)} leads · ${formatNumber(summary?.accountCount)} accounts · ${formatNumber(summary?.opportunityCount)} opportunities`,
      priority: (summary?.leadCount ?? 0) === 0 ? "High" : "Medium",
    },
    {
      title: "Service operations",
      status: (summary?.ticketCount ?? 0) > 0 ? "Active" : "Quiet",
      detail: `${formatNumber(summary?.ticketCount)} tickets · ${formatNumber(summary?.overdueTicketCount)} overdue`,
      priority: (summary?.overdueTicketCount ?? 0) > 0 ? "High" : "Low",
    },
    {
      title: "Automation coverage",
      status: (summary?.activeWorkflowCount ?? 0) > 0 ? "Enabled" : "Pending",
      detail: `${formatNumber(summary?.activeWorkflowCount)} active workflows · ${formatNumber(summary?.integrationConnectionCount)} integrations`,
      priority: (summary?.activeWorkflowCount ?? 0) === 0 ? "Medium" : "Low",
    },
    {
      title: "AI operational usage",
      status: (summary?.aiInteractionCount ?? 0) > 0 ? "Active" : "Pending",
      detail: `${formatNumber(summary?.aiInteractionCount)} AI interactions logged`,
      priority: (summary?.aiInteractionCount ?? 0) === 0 ? "Medium" : "Low",
    },
    {
      title: "Forecast refresh",
      status: forecast?.generatedAt ? "Current" : "Pending",
      detail: formatDateTime(forecast?.generatedAt),
      priority: "Low",
    },
  ];

  const priorityBadge = (priority: string) => {
    switch (priority) {
      case "High":
        return (
          <span className="rounded-full border border-orange-200 bg-orange-100 px-2 py-0.5 text-xs font-medium text-orange-700">
            High
          </span>
        );
      case "Medium":
        return (
          <span className="rounded-full border border-yellow-200 bg-yellow-100 px-2 py-0.5 text-xs font-medium text-yellow-700">
            Medium
          </span>
        );
      default:
        return (
          <span className="rounded-full border border-gray-200 bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700">
            Low
          </span>
        );
    }
  };

  return (
    <div className="custom-scrollbar flex h-full min-h-0 flex-1 flex-col overflow-y-auto bg-[#f8f9fa]">
      <div className="sticky top-0 z-10 flex flex-col gap-3 border-b border-gray-200 bg-white px-4 py-5 sm:px-6 lg:flex-row lg:items-center lg:justify-between lg:px-8 lg:py-6">
        <div>
          <h1 className="text-2xl font-semibold text-gray-900">Home</h1>
          <p className="mt-1 text-sm text-gray-500">Live operating view across CRM, service, automation, and revenue.</p>
        </div>
        <div className="inline-flex w-fit rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm font-medium text-gray-500">
          Tenant analytics
        </div>
      </div>

      <div className="mx-auto w-full max-w-7xl space-y-6 p-4 pb-10 sm:p-6 lg:space-y-8 lg:p-8">
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
          {summaryCards.map((card) => (
            <div key={card.label} className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition-shadow hover:shadow-md">
              <div className="mb-4 flex items-start justify-between">
                <div>
                  <p className="mb-1 text-sm font-medium text-gray-500">{card.label}</p>
                  <h3 className="truncate text-3xl font-bold text-gray-900" title={String(card.value)}>
                    {card.value}
                  </h3>
                </div>
                <div className={`flex h-10 w-10 items-center justify-center rounded-full ${card.tone}`}>
                  <card.icon className="h-5 w-5" />
                </div>
              </div>
              <div className="text-sm text-gray-500">{card.note}</div>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Opportunity Stage Mix</h2>
              <button className="text-gray-400 hover:text-gray-600">
                <MoreHorizontalIcon className="h-5 w-5" />
              </button>
            </div>
            {opportunityStageData.length === 0 ? (
              <EmptyChartState
                title="No opportunity pipeline yet"
                description="Create opportunities in the CRM workspace and the stage mix will appear here."
              />
            ) : (
              <div className="h-64 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={opportunityStageData} layout="vertical" margin={{ top: 5, right: 30, left: 40, bottom: 5 }}>
                    <CartesianGrid stroke="#f3f4f6" strokeDasharray="3 3" horizontal={false} />
                    <XAxis type="number" hide />
                    <YAxis dataKey="name" type="category" axisLine={false} tickLine={false} tick={{ fill: "#4b5563", fontSize: 12 }} width={100} />
                    <RechartsTooltip
                      cursor={{ fill: "#f9fafb" }}
                      contentStyle={{ borderRadius: "8px", border: "1px solid #e5e7eb", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" }}
                    />
                    <Bar dataKey="value" radius={[0, 4, 4, 0]} barSize={24}>
                      {opportunityStageData.map((entry) => (
                        <Cell key={entry.name} fill={entry.fill} />
                      ))}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>
            )}
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Lead Status Distribution</h2>
              <button className="text-gray-400 hover:text-gray-600">
                <MoreHorizontalIcon className="h-5 w-5" />
              </button>
            </div>
            {leadStatusData.length === 0 ? (
              <EmptyChartState
                title="No lead data yet"
                description="Create a few leads and this chart will begin reflecting top-of-funnel movement."
              />
            ) : (
              <div className="flex h-64 w-full items-center justify-center">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={leadStatusData} cx="50%" cy="50%" innerRadius={60} outerRadius={80} paddingAngle={2} dataKey="value">
                      {leadStatusData.map((entry) => (
                        <Cell key={entry.name} fill={entry.fill} />
                      ))}
                    </Pie>
                    <RechartsTooltip contentStyle={{ borderRadius: "8px", border: "1px solid #e5e7eb", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" }} />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            )}
            {leadStatusData.length > 0 ? (
              <div className="mt-4 flex flex-wrap gap-2">
                {leadStatusData.map((entry) => (
                  <span key={entry.name} className="inline-flex items-center gap-2 rounded-full bg-gray-50 px-3 py-1 text-xs text-gray-600">
                    <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: entry.fill }} />
                    {entry.name}: {entry.value}
                  </span>
                ))}
              </div>
            ) : null}
          </div>
        </div>

        <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
          <div className="mb-6 flex items-center justify-between">
            <h2 className="text-base font-semibold text-gray-900">Revenue Breakdown</h2>
            <button className="text-gray-400 hover:text-gray-600">
              <MoreHorizontalIcon className="h-5 w-5" />
            </button>
          </div>
          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={revenueBreakdownData} margin={{ top: 5, right: 20, left: -20, bottom: 5 }}>
                <CartesianGrid stroke="#f3f4f6" strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: "#6b7280", fontSize: 12 }} dy={10} />
                <YAxis axisLine={false} tickLine={false} tick={{ fill: "#6b7280", fontSize: 12 }} />
                <RechartsTooltip contentStyle={{ borderRadius: "8px", border: "1px solid #e5e7eb", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" }} />
                <Bar dataKey="value" radius={[6, 6, 0, 0]} barSize={42}>
                  {revenueBreakdownData.map((entry) => (
                    <Cell key={entry.name} fill={entry.fill} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="flex flex-col rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Operational Snapshot</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">Live</button>
            </div>
            <div className="flex-1 space-y-3">
              {operationsTable.map((item) => (
                <div key={item.label} className="flex items-center justify-between rounded-lg border border-gray-200 px-4 py-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-9 w-9 items-center justify-center rounded-full bg-gray-100 text-gray-700">
                      <item.icon className="h-4 w-4" />
                    </div>
                    <div>
                      <div className="text-sm font-medium text-gray-900">{item.label}</div>
                      <div className="text-xs text-gray-500">{item.state}</div>
                    </div>
                  </div>
                  <div className="text-lg font-semibold text-gray-900">{item.value}</div>
                </div>
              ))}
            </div>
          </div>

          <div className="flex flex-col rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Commercial Focus</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">Forecast</button>
            </div>
            <div className="flex-1 overflow-x-auto">
              <table className="w-full whitespace-nowrap text-left text-sm">
                <thead className="border-y border-gray-100 bg-gray-50 text-xs text-gray-500">
                  <tr>
                    <th className="px-4 py-2 font-medium">Metric</th>
                    <th className="px-4 py-2 font-medium">Value</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {commercialFocus.map((item) => (
                    <tr key={item.label} className="transition-colors hover:bg-gray-50">
                      <td className="px-4 py-3 font-medium text-gray-900">{item.label}</td>
                      <td className="px-4 py-3 text-gray-700">{item.value}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Service Watchlist</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">Monitor</button>
            </div>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
              <div className="rounded-lg border border-gray-200 bg-gray-50 px-4 py-4">
                <div className="text-xs font-medium uppercase tracking-wide text-gray-500">Open tickets</div>
                <div className="mt-2 text-2xl font-semibold text-gray-900">{formatNumber(summary?.ticketCount)}</div>
              </div>
              <div className="rounded-lg border border-orange-200 bg-orange-50 px-4 py-4">
                <div className="text-xs font-medium uppercase tracking-wide text-orange-700">Overdue</div>
                <div className="mt-2 text-2xl font-semibold text-orange-700">{formatNumber(overdueTickets.length)}</div>
              </div>
              <div className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-4">
                <div className="text-xs font-medium uppercase tracking-wide text-blue-700">Due soon invoices</div>
                <div className="mt-2 text-2xl font-semibold text-blue-700">{formatNumber(dueSoonInvoices.length)}</div>
              </div>
            </div>
            <div className="mt-5 space-y-3">
              {overdueTickets.length === 0 ? (
                <div className="rounded-lg border border-dashed border-gray-200 bg-gray-50 px-4 py-6 text-sm text-gray-500">
                  No overdue service items at the moment.
                </div>
              ) : (
                overdueTickets.slice(0, 3).map((ticket) => (
                  <div key={ticket.id} className="flex items-start justify-between gap-4 rounded-lg border border-gray-200 px-4 py-3">
                    <div>
                      <div className="text-sm font-medium text-gray-900">{ticket.title}</div>
                      <div className="mt-1 text-xs text-gray-500">
                        {ticket.priority} priority · {ticket.assignee || "Unassigned"} · due {formatDateTime(ticket.dueAt)}
                      </div>
                    </div>
                    <span className="rounded-full border border-orange-200 bg-orange-100 px-2 py-0.5 text-xs font-medium text-orange-700">
                      {ticket.status}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">AI & Activity Feed</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">Traceable</button>
            </div>
            <div className="space-y-3">
              {recentAiItems.length === 0 && recentActivities.length === 0 ? (
                <div className="rounded-lg border border-dashed border-gray-200 bg-gray-50 px-4 py-6 text-sm text-gray-500">
                  No recent AI or activity records yet for this tenant.
                </div>
              ) : (
                <>
                  {recentAiItems.map((item) => (
                    <div key={`ai-${item.id}`} className="flex items-start gap-3 rounded-lg border border-gray-200 px-4 py-3">
                      <div className="flex h-9 w-9 items-center justify-center rounded-full bg-violet-50 text-violet-600">
                        <BotIcon className="h-4 w-4" />
                      </div>
                      <div className="min-w-0 flex-1">
                        <div className="text-sm font-medium text-gray-900">{item.name}</div>
                        <div className="mt-1 text-xs text-gray-500">
                          {item.operationType} · {item.modelName} · {formatDateTime(item.createdAt)}
                        </div>
                      </div>
                    </div>
                  ))}
                  {recentActivities.map((item) => (
                    <div key={`activity-${item.id}`} className="flex items-start gap-3 rounded-lg border border-gray-200 px-4 py-3">
                      <div className="flex h-9 w-9 items-center justify-center rounded-full bg-blue-50 text-blue-600">
                        <FileTextIcon className="h-4 w-4" />
                      </div>
                      <div className="min-w-0 flex-1">
                        <div className="text-sm font-medium text-gray-900">{item.subject}</div>
                        <div className="mt-1 text-xs text-gray-500">
                          {item.type} · {item.relatedEntityType} #{item.relatedEntityId} · {formatDateTime(item.createdAt)}
                        </div>
                      </div>
                    </div>
                  ))}
                </>
              )}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Pipeline Register</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">View All</button>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full whitespace-nowrap text-left text-sm">
                <thead className="border-y border-gray-100 bg-gray-50 text-xs text-gray-500">
                  <tr>
                    <th className="px-4 py-2 font-medium">Opportunity</th>
                    <th className="px-4 py-2 font-medium">Amount</th>
                    <th className="px-4 py-2 font-medium">Stage</th>
                    <th className="px-4 py-2 font-medium">Account</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {pipelineRegister.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="px-4 py-10 text-center text-sm text-gray-500">
                        No opportunities available yet for this tenant.
                      </td>
                    </tr>
                  ) : (
                    pipelineRegister.map((opportunity) => (
                      <tr key={opportunity.id} className="transition-colors hover:bg-gray-50">
                        <td className="px-4 py-3 font-medium text-gray-900">{opportunity.name}</td>
                        <td className="px-4 py-3 text-gray-700">{formatCurrency(opportunity.amount)}</td>
                        <td className="px-4 py-3 text-gray-700">{opportunity.stage}</td>
                        <td className="px-4 py-3 text-gray-700">{opportunity.accountName || "Unassigned"}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Readiness Checklist</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">Review</button>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full whitespace-nowrap text-left text-sm">
                <thead className="border-y border-gray-100 bg-gray-50 text-xs text-gray-500">
                  <tr>
                    <th className="w-8 px-4 py-2 font-medium"></th>
                    <th className="px-4 py-2 font-medium">Area</th>
                    <th className="px-4 py-2 font-medium">Status</th>
                    <th className="px-4 py-2 font-medium">Priority</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {readinessItems.map((item) => (
                    <tr key={item.title} className="group transition-colors hover:bg-gray-50">
                      <td className="px-4 py-3">
                        {item.priority === "High" ? (
                          <AlertTriangleIcon className="h-4 w-4 text-orange-500" />
                        ) : (
                          <CircleIcon className="h-4 w-4 text-gray-300 transition-colors group-hover:text-blue-500" />
                        )}
                      </td>
                      <td className="px-4 py-3">
                        <div className="font-medium text-gray-900">{item.title}</div>
                        <div className="text-xs text-gray-500">{item.detail}</div>
                      </td>
                      <td className="px-4 py-3 text-gray-700">{item.status}</td>
                      <td className="px-4 py-3">{priorityBadge(item.priority)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
