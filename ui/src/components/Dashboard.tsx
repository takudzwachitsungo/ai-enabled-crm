import React from "react";
import {
  ActivityIcon,
  AlertTriangleIcon,
  ArrowDownRightIcon,
  ArrowUpRightIcon,
  BotIcon,
  BriefcaseBusinessIcon,
  CircleDollarSignIcon,
  FileTextIcon,
  Layers3Icon,
  MoreHorizontalIcon,
  ReceiptTextIcon,
  ShieldCheckIcon,
  SparklesIcon,
  TicketIcon,
  TrendingUpIcon,
  UsersIcon,
  WorkflowIcon,
  ZapIcon,
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

/* ─── props ─── */

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

/* ─── helpers ─── */

type ChartDatum = { name: string; value: number; fill: string };

function fmt$(v?: number | null) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 0,
  }).format(v ?? 0);
}

function fmtN(v?: number | null) {
  return new Intl.NumberFormat("en-US").format(v ?? 0);
}

function fmtDate(v?: string | null) {
  if (!v) return "—";
  return new Date(v).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
  });
}

function fmtDateTime(v?: string | null) {
  if (!v) return "—";
  return new Date(v).toLocaleString("en-US", {
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
}

function timeAgo(v: string) {
  const diff = Date.now() - new Date(v).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return "just now";
  if (mins < 60) return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `${hrs}h ago`;
  const days = Math.floor(hrs / 24);
  return `${days}d ago`;
}

const PALETTE = ["#6366f1", "#22c55e", "#f59e0b", "#3b82f6", "#ec4899", "#8b5cf6", "#14b8a6", "#ef4444"];

function bucket<T>(records: T[], key: (r: T) => string): ChartDatum[] {
  const m = new Map<string, number>();
  records.forEach((r) => {
    const k = key(r) || "Other";
    m.set(k, (m.get(k) ?? 0) + 1);
  });
  return Array.from(m.entries()).map(([name, value], i) => ({
    name,
    value,
    fill: PALETTE[i % PALETTE.length],
  }));
}

/* ─── sub-components ─── */

function KpiCard({
  label,
  value,
  subtitle,
  icon: Icon,
  iconBg,
  trend,
}: {
  label: string;
  value: string;
  subtitle: string;
  icon: React.ElementType;
  iconBg: string;
  trend?: "up" | "down" | null;
}) {
  return (
    <div className="relative overflow-hidden rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition-shadow hover:shadow-md">
      <div className="flex items-start justify-between">
        <div className="min-w-0 flex-1">
          <p className="text-[10px] font-medium uppercase tracking-wider text-gray-400">
            {label}
          </p>
          <h3 className="mt-1 truncate text-xl font-bold text-gray-900">
            {value}
          </h3>
          <p className="mt-0.5 flex items-center gap-1 text-[11px] text-gray-500">
            {trend === "up" && (
              <ArrowUpRightIcon className="h-3 w-3 text-green-500" />
            )}
            {trend === "down" && (
              <ArrowDownRightIcon className="h-3 w-3 text-red-500" />
            )}
            {subtitle}
          </p>
        </div>
        <div
          className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-lg ${iconBg}`}
        >
          <Icon className="h-4 w-4" />
        </div>
      </div>
    </div>
  );
}

function SectionHeader({
  title,
  badge,
  action,
}: {
  title: string;
  badge?: string;
  action?: string;
}) {
  return (
    <div className="mb-3 flex items-center justify-between">
      <div className="flex items-center gap-2">
        <h2 className="text-[13px] font-semibold text-gray-900">{title}</h2>
        {badge && (
          <span className="rounded-full bg-gray-100 px-2 py-0.5 text-[10px] font-medium text-gray-600">
            {badge}
          </span>
        )}
      </div>
      {action && (
        <button className="text-[11px] font-medium text-gray-400 transition-colors hover:text-gray-600">
          {action}
        </button>
      )}
    </div>
  );
}

function EmptyState({ text }: { text: string }) {
  return (
    <div className="flex h-36 items-center justify-center rounded-lg border border-dashed border-gray-200 bg-gray-50/50 text-xs text-gray-400">
      {text}
    </div>
  );
}

function MetricRow({
  label,
  value,
  accent,
}: {
  label: string;
  value: string;
  accent?: boolean;
}) {
  return (
    <div className="flex items-center justify-between py-1.5">
      <span className="text-xs text-gray-500">{label}</span>
      <span
        className={`text-xs font-semibold ${accent ? "text-indigo-600" : "text-gray-900"}`}
      >
        {value}
      </span>
    </div>
  );
}

/* ─── main ─── */

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
  /* derived data */
  const leads = leadRecords ?? [];
  const opps = opportunityRecords ?? [];
  const tickets = ticketRecords ?? [];
  const activities = activityRecords ?? [];
  const quotes = quoteRecords ?? [];
  const invoices = invoiceRecords ?? [];
  const aiItems = aiInteractions ?? [];

  const leadStatusData = bucket(leads, (r) => r.status);
  const oppStageData = bucket(opps, (r) => r.stage);

  const overdueTickets = tickets.filter(
    (t) => t.dueAt && t.status !== "RESOLVED" && new Date(t.dueAt).getTime() < Date.now(),
  );

  const dueSoonInvoices = invoices.filter((inv) => {
    if (!inv.dueAt || inv.status === "PAID") return false;
    return new Date(inv.dueAt).getTime() <= Date.now() + 7 * 86400000;
  });

  const recentActivities = [...activities]
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5);

  const recentAi = [...aiItems]
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 4);

  const topDeals = [...opps].sort((a, b) => b.amount - a.amount).slice(0, 5);

  const revenueData = [
    { name: "Pipeline", value: forecast?.totalPipelineAmount ?? 0, fill: "#6366f1" },
    { name: "Weighted", value: forecast?.weightedPipelineAmount ?? 0, fill: "#8b5cf6" },
    { name: "Quotes", value: forecast?.activeQuoteAmount ?? 0, fill: "#f59e0b" },
    { name: "Invoiced", value: forecast?.issuedInvoiceAmount ?? 0, fill: "#3b82f6" },
    { name: "Collected", value: forecast?.collectedInvoiceAmount ?? 0, fill: "#22c55e" },
  ];

  const totalCounts = [
    { label: "Leads", value: fmtN(summary?.leadCount), icon: UsersIcon },
    { label: "Contacts", value: fmtN(summary?.contactCount), icon: UsersIcon },
    { label: "Accounts", value: fmtN(summary?.accountCount), icon: Layers3Icon },
    { label: "Activities", value: fmtN(summary?.activityCount), icon: ActivityIcon },
    { label: "Workflows", value: fmtN(summary?.activeWorkflowCount), icon: WorkflowIcon },
    { label: "Integrations", value: fmtN(summary?.integrationConnectionCount), icon: ZapIcon },
  ];

  return (
    <div className="custom-scrollbar flex h-full min-h-0 flex-1 flex-col overflow-y-auto bg-[#f8f9fa]">
      {/* ── page header ── */}
      <div className="sticky top-0 z-10 border-b border-gray-200 bg-white px-4 py-3 sm:px-6 lg:px-8 lg:py-4">
        <div className="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-lg font-semibold text-gray-900">Dashboard</h1>
            <p className="mt-0 text-xs text-gray-500">
              Your workspace at a glance
            </p>
          </div>
          <div className="flex items-center gap-2">
            {forecast?.generatedAt && (
              <span className="text-xs text-gray-400">
                Updated {fmtDateTime(forecast.generatedAt)}
              </span>
            )}
          </div>
        </div>
      </div>

      <div className="mx-auto w-full max-w-7xl space-y-4 p-4 pb-8 sm:p-5 lg:p-6">
        {/* ── KPI hero row ── */}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <KpiCard
            label="Revenue Forecast"
            value={fmt$(forecast?.projectedRevenueAmount)}
            subtitle="Projected total revenue"
            icon={CircleDollarSignIcon}
            iconBg="bg-green-50 text-green-600"
            trend={forecast?.projectedRevenueAmount ? "up" : null}
          />
          <KpiCard
            label="Pipeline Value"
            value={fmt$(forecast?.totalPipelineAmount)}
            subtitle={`${fmtN(summary?.opportunityCount)} open opportunities`}
            icon={BriefcaseBusinessIcon}
            iconBg="bg-indigo-50 text-indigo-600"
          />
          <KpiCard
            label="Open Tickets"
            value={fmtN(summary?.ticketCount)}
            subtitle={
              (summary?.overdueTicketCount ?? 0) > 0
                ? `${fmtN(summary?.overdueTicketCount)} overdue`
                : "All on track"
            }
            icon={TicketIcon}
            iconBg="bg-orange-50 text-orange-600"
            trend={
              (summary?.overdueTicketCount ?? 0) > 0 ? "down" : null
            }
          />
          <KpiCard
            label="AI Interactions"
            value={fmtN(summary?.aiInteractionCount)}
            subtitle="Summaries, drafts & recommendations"
            icon={SparklesIcon}
            iconBg="bg-violet-50 text-violet-600"
          />
        </div>

        {/* ── Revenue & Pipeline ── */}
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-3">
          {/* Revenue chart */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm lg:col-span-2">
            <SectionHeader title="Revenue Pipeline" badge="Forecast" action="View details" />
            <div className="h-64 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={revenueData}
                  margin={{ top: 5, right: 10, left: -25, bottom: 5 }}
                >
                  <CartesianGrid stroke="#f3f4f6" strokeDasharray="3 3" vertical={false} />
                  <XAxis
                    dataKey="name"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: "#9ca3af", fontSize: 12 }}
                    dy={8}
                  />
                  <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: "#9ca3af", fontSize: 11 }}
                  />
                  <RechartsTooltip
                    formatter={(value: number) => fmt$(value)}
                    contentStyle={{
                      borderRadius: "12px",
                      border: "1px solid #e5e7eb",
                      boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
                      fontSize: "13px",
                    }}
                  />
                  <Bar dataKey="value" radius={[8, 8, 0, 0]} barSize={36}>
                    {revenueData.map((entry) => (
                      <Cell key={entry.name} fill={entry.fill} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Revenue metrics */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
            <SectionHeader title="Financial Summary" />
            <div className="divide-y divide-gray-100">
              <MetricRow label="Pipeline Total" value={fmt$(forecast?.totalPipelineAmount)} accent />
              <MetricRow label="Weighted Pipeline" value={fmt$(forecast?.weightedPipelineAmount)} />
              <MetricRow label="Active Quotes" value={fmt$(forecast?.activeQuoteAmount)} />
              <MetricRow label="Issued Invoices" value={fmt$(forecast?.issuedInvoiceAmount)} />
              <MetricRow label="Collected" value={fmt$(forecast?.collectedInvoiceAmount)} />
              <MetricRow
                label="Quote Book"
                value={fmt$(quotes.reduce((s, q) => s + q.amount, 0))}
              />
              <MetricRow
                label="Invoice Book"
                value={fmt$(invoices.reduce((s, i) => s + i.amount, 0))}
              />
            </div>
          </div>
        </div>

        {/* ── Charts: Opportunities + Leads ── */}
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
          {/* Opportunity stages */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
            <SectionHeader
              title="Opportunity Stages"
              badge={`${opps.length} total`}
            />
            {oppStageData.length === 0 ? (
              <EmptyState text="Create opportunities to see the stage mix" />
            ) : (
              <>
                <div className="h-52 w-full">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={oppStageData}
                      layout="vertical"
                      margin={{ top: 0, right: 20, left: 0, bottom: 0 }}
                    >
                      <CartesianGrid stroke="#f3f4f6" strokeDasharray="3 3" horizontal={false} />
                      <XAxis type="number" hide />
                      <YAxis
                        dataKey="name"
                        type="category"
                        axisLine={false}
                        tickLine={false}
                        tick={{ fill: "#6b7280", fontSize: 12 }}
                        width={110}
                      />
                      <RechartsTooltip
                        cursor={{ fill: "#f9fafb" }}
                        contentStyle={{
                          borderRadius: "12px",
                          border: "1px solid #e5e7eb",
                          boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
                        }}
                      />
                      <Bar dataKey="value" radius={[0, 6, 6, 0]} barSize={20}>
                        {oppStageData.map((e) => (
                          <Cell key={e.name} fill={e.fill} />
                        ))}
                      </Bar>
                    </BarChart>
                  </ResponsiveContainer>
                </div>
                <div className="mt-4 flex flex-wrap gap-2">
                  {oppStageData.map((e) => (
                    <span
                      key={e.name}
                      className="inline-flex items-center gap-1.5 rounded-full bg-gray-50 px-2.5 py-1 text-xs text-gray-600"
                    >
                      <span className="h-2 w-2 rounded-full" style={{ backgroundColor: e.fill }} />
                      {e.name}: {e.value}
                    </span>
                  ))}
                </div>
              </>
            )}
          </div>

          {/* Lead status donut */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
            <SectionHeader
              title="Lead Distribution"
              badge={`${leads.length} total`}
            />
            {leadStatusData.length === 0 ? (
              <EmptyState text="Add leads to see the status distribution" />
            ) : (
              <>
                <div className="flex h-52 w-full items-center justify-center">
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={leadStatusData}
                        cx="50%"
                        cy="50%"
                        innerRadius={55}
                        outerRadius={80}
                        paddingAngle={3}
                        dataKey="value"
                        strokeWidth={0}
                      >
                        {leadStatusData.map((e) => (
                          <Cell key={e.name} fill={e.fill} />
                        ))}
                      </Pie>
                      <RechartsTooltip
                        contentStyle={{
                          borderRadius: "12px",
                          border: "1px solid #e5e7eb",
                          boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
                        }}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
                <div className="mt-4 flex flex-wrap gap-2">
                  {leadStatusData.map((e) => (
                    <span
                      key={e.name}
                      className="inline-flex items-center gap-1.5 rounded-full bg-gray-50 px-2.5 py-1 text-xs text-gray-600"
                    >
                      <span className="h-2 w-2 rounded-full" style={{ backgroundColor: e.fill }} />
                      {e.name}: {e.value}
                    </span>
                  ))}
                </div>
              </>
            )}
          </div>
        </div>

        {/* ── Service + Activity Feed ── */}
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
          {/* Service watchlist */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
            <SectionHeader title="Service Health" action="View tickets" />
            <div className="mb-5 grid grid-cols-3 gap-3">
              <div className="rounded-xl bg-gray-50 px-4 py-3.5 text-center">
                <div className="text-lg font-bold text-gray-900">{fmtN(summary?.ticketCount)}</div>
                <div className="mt-0.5 text-[11px] font-medium uppercase tracking-wider text-gray-400">
                  Open
                </div>
              </div>
              <div className={`rounded-xl px-4 py-3.5 text-center ${overdueTickets.length > 0 ? "bg-red-50" : "bg-gray-50"}`}>
                <div className={`text-lg font-bold ${overdueTickets.length > 0 ? "text-red-600" : "text-gray-900"}`}>
                  {fmtN(overdueTickets.length)}
                </div>
                <div className={`mt-0.5 text-[11px] font-medium uppercase tracking-wider ${overdueTickets.length > 0 ? "text-red-400" : "text-gray-400"}`}>
                  Overdue
                </div>
              </div>
              <div className="rounded-xl bg-blue-50 px-4 py-3.5 text-center">
                <div className="text-lg font-bold text-blue-700">{fmtN(dueSoonInvoices.length)}</div>
                <div className="mt-0.5 text-[11px] font-medium uppercase tracking-wider text-blue-400">
                  Due Soon
                </div>
              </div>
            </div>

            {overdueTickets.length === 0 ? (
              <div className="rounded-xl border border-dashed border-gray-200 bg-gray-50/50 px-4 py-5 text-center text-sm text-gray-400">
                <ShieldCheckIcon className="mx-auto mb-2 h-5 w-5 text-green-400" />
                All clear — no overdue items
              </div>
            ) : (
              <div className="space-y-2.5">
                {overdueTickets.slice(0, 3).map((t) => (
                  <div
                    key={t.id}
                    className="flex items-start justify-between gap-3 rounded-xl border border-gray-100 bg-gray-50/50 px-4 py-3"
                  >
                    <div className="min-w-0 flex-1">
                      <div className="truncate text-sm font-medium text-gray-900">{t.title}</div>
                      <div className="mt-0.5 text-xs text-gray-500">
                        {t.priority} · {t.assignee || "Unassigned"} · due {fmtDate(t.dueAt)}
                      </div>
                    </div>
                    <span className="shrink-0 rounded-full bg-red-100 px-2.5 py-0.5 text-xs font-medium text-red-700">
                      Overdue
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Activity & AI feed */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
            <SectionHeader title="Recent Activity" badge="Live" />
            {recentAi.length === 0 && recentActivities.length === 0 ? (
              <EmptyState text="No recent activity yet" />
            ) : (
              <div className="space-y-1">
                {recentAi.map((item) => (
                  <div
                    key={`ai-${item.id}`}
                    className="flex items-start gap-3 rounded-xl px-3 py-3 transition-colors hover:bg-gray-50"
                  >
                    <div className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-violet-50 text-violet-500">
                      <BotIcon className="h-4 w-4" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <div className="text-sm font-medium text-gray-900">
                        {item.name}
                      </div>
                      <div className="mt-0.5 text-xs text-gray-400">
                        {item.operationType} · {item.modelName} · {timeAgo(item.createdAt)}
                      </div>
                    </div>
                  </div>
                ))}
                {recentActivities.map((item) => (
                  <div
                    key={`act-${item.id}`}
                    className="flex items-start gap-3 rounded-xl px-3 py-3 transition-colors hover:bg-gray-50"
                  >
                    <div className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-blue-500">
                      <FileTextIcon className="h-4 w-4" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <div className="text-sm font-medium text-gray-900">
                        {item.subject}
                      </div>
                      <div className="mt-0.5 text-xs text-gray-400">
                        {item.type} · {item.relatedEntityType}{" "}
                        {item.relatedEntityId ? `#${item.relatedEntityId}` : ""} ·{" "}
                        {timeAgo(item.createdAt)}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* ── Top Deals + CRM Metrics ── */}
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-3">
          {/* Top pipeline deals */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm lg:col-span-2">
            <SectionHeader title="Top Pipeline Deals" badge={`${opps.length} total`} action="View all" />
            <div className="overflow-x-auto">
              <table className="w-full whitespace-nowrap text-left text-sm">
                <thead>
                  <tr className="border-b border-gray-100 text-xs font-medium uppercase tracking-wider text-gray-400">
                    <th className="pb-3 pr-4 font-medium">Opportunity</th>
                    <th className="pb-3 pr-4 font-medium">Amount</th>
                    <th className="pb-3 pr-4 font-medium">Stage</th>
                    <th className="pb-3 font-medium">Account</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {topDeals.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="py-10 text-center text-sm text-gray-400">
                        No opportunities yet
                      </td>
                    </tr>
                  ) : (
                    topDeals.map((d) => (
                      <tr key={d.id} className="transition-colors hover:bg-gray-50/50">
                        <td className="py-3 pr-4 font-medium text-gray-900">{d.name}</td>
                        <td className="py-3 pr-4 font-semibold text-gray-900">{fmt$(d.amount)}</td>
                        <td className="py-3 pr-4">
                          <span className="rounded-full bg-indigo-50 px-2.5 py-1 text-xs font-medium text-indigo-700">
                            {d.stage}
                          </span>
                        </td>
                        <td className="py-3 text-gray-500">{d.accountName || "—"}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* CRM health metrics */}
          <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
            <SectionHeader title="CRM Overview" />
            <div className="space-y-3">
              {totalCounts.map((item) => (
                <div
                  key={item.label}
                  className="flex items-center justify-between rounded-xl border border-gray-100 px-4 py-3"
                >
                  <div className="flex items-center gap-3">
                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gray-50 text-gray-500">
                      <item.icon className="h-4 w-4" />
                    </div>
                    <span className="text-sm text-gray-600">{item.label}</span>
                  </div>
                  <span className="text-sm font-semibold text-gray-900">{item.value}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
