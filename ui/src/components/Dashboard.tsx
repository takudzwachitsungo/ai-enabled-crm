import React from "react";
import {
  UsersIcon,
  HandshakeIcon,
  DollarSignIcon,
  PercentIcon,
  TrendingUpIcon,
  TrendingDownIcon,
  MoreHorizontalIcon,
  CircleIcon,
} from "lucide-react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  Legend,
} from "recharts";
import { deals, leads, notifications, tasks, users } from "../data/mockData";
import { Avatar } from "./ui/Avatar";
import { StatusBadge } from "./ui/StatusBadge";
import { DashboardForecastResponse, DashboardSummaryResponse, LeadRecord, OpportunityRecord } from "../types/crm";

interface DashboardProps {
  summary?: DashboardSummaryResponse | null;
  forecast?: DashboardForecastResponse | null;
  leadRecords?: LeadRecord[];
  opportunityRecords?: OpportunityRecord[];
}

export function Dashboard({ summary, forecast, leadRecords, opportunityRecords }: DashboardProps) {
  const activeLeadCount = leadRecords?.length ?? leads.length;
  const opportunityCount = opportunityRecords?.length ?? deals.length;
  const forecastValue = forecast?.projectedRevenueAmount
    ? `$${Math.round(forecast.projectedRevenueAmount).toLocaleString()}`
    : "$1.84M";
  const summaryCards = [
    {
      label: "Total Leads",
      value: summary?.leadCount ?? activeLeadCount,
      delta: "+4",
      note: "this week",
      icon: UsersIcon,
      tone: "bg-blue-50 text-blue-600",
    },
    {
      label: "Open Opportunities",
      value: summary?.opportunityCount ?? opportunityCount,
      delta: "+2",
      note: "new conversions",
      icon: HandshakeIcon,
      tone: "bg-purple-50 text-purple-600",
    },
    {
      label: "Revenue Forecast",
      value: forecastValue,
      delta: "+$182k",
      note: "forecast lift",
      icon: DollarSignIcon,
      tone: "bg-green-50 text-green-600",
    },
    {
      label: "AI Conversion Assist",
      value: summary?.aiInteractionCount ?? 24,
      delta: "-1.2%",
      note: "needs tuning",
      icon: PercentIcon,
      tone: "bg-orange-50 text-orange-600",
    },
  ];
  const dealsByStageData = [
    { name: "New", count: 6, fill: "#9ca3af" },
    { name: "Qualified", count: 4, fill: "#22c55e" },
    { name: "Proposal", count: 3, fill: "#3b82f6" },
    { name: "Negotiation", count: 2, fill: "#f97316" },
  ];

  const leadsByStatusData = [
    { name: "New", value: 5, color: "#9ca3af" },
    { name: "Qualified", value: 3, color: "#22c55e" },
    { name: "Contacted", value: 2, color: "#f97316" },
    { name: "Nurture", value: 2, color: "#3b82f6" },
  ];

  const monthlyActivityData = [
    { month: "Jan", leads: 8, deals: 2 },
    { month: "Feb", leads: 11, deals: 3 },
    { month: "Mar", leads: 15, deals: 4 },
    { month: "Apr", leads: 18, deals: 5 },
    { month: "May", leads: 20, deals: 6 },
    { month: "Jun", leads: 23, deals: 8 },
  ];

  const recentActivity = notifications.slice(0, 5);
  const topDeals = [...deals]
    .filter((d) => d.amount)
    .sort((a, b) => {
      const valA = parseInt(a.amount.replace(/[^0-9]/g, "")) || 0;
      const valB = parseInt(b.amount.replace(/[^0-9]/g, "")) || 0;
      return valB - valA;
    })
    .slice(0, 4);

  const upcomingTasks = tasks.filter((t) => t.status !== "Done" && t.status !== "Cancelled").slice(0, 5);

  const getPriorityBadge = (priority: string) => {
    switch (priority) {
      case "Urgent":
        return <span className="rounded-full border border-red-200 bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">Urgent</span>;
      case "High":
        return <span className="rounded-full border border-orange-200 bg-orange-100 px-2 py-0.5 text-xs font-medium text-orange-700">High</span>;
      case "Medium":
        return <span className="rounded-full border border-yellow-200 bg-yellow-100 px-2 py-0.5 text-xs font-medium text-yellow-700">Medium</span>;
      default:
        return <span className="rounded-full border border-gray-200 bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700">Low</span>;
    }
  };

  return (
    <div className="custom-scrollbar flex flex-1 flex-col overflow-y-auto bg-[#f8f9fa]">
      <div className="sticky top-0 z-10 flex flex-col gap-3 border-b border-gray-200 bg-white px-4 py-5 sm:px-6 lg:flex-row lg:items-center lg:justify-between lg:px-8 lg:py-6">
        <h1 className="text-2xl font-semibold text-gray-900">Dashboard</h1>
        <div className="inline-flex w-fit rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm font-medium text-gray-500">
          Phase 3 backend ready
        </div>
      </div>

      <div className="mx-auto w-full max-w-7xl space-y-6 p-4 sm:p-6 lg:space-y-8 lg:p-8">
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
              <div className="flex items-center gap-2 text-sm">
                <span className={`flex items-center gap-1 font-medium ${card.label === "AI Conversion Assist" ? "text-red-500" : "text-green-600"}`}>
                  {card.label === "AI Conversion Assist" ? (
                    <TrendingDownIcon className="h-4 w-4" />
                  ) : (
                    <TrendingUpIcon className="h-4 w-4" />
                  )}
                  {card.delta}
                </span>
                <span className="text-gray-400">{card.note}</span>
              </div>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Opportunities by Stage</h2>
              <button className="text-gray-400 hover:text-gray-600">
                <MoreHorizontalIcon className="h-5 w-5" />
              </button>
            </div>
            <div className="h-64 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={dealsByStageData} layout="vertical" margin={{ top: 5, right: 30, left: 40, bottom: 5 }}>
                  <CartesianGrid stroke="#f3f4f6" strokeDasharray="3 3" horizontal={false} />
                  <XAxis type="number" hide />
                  <YAxis dataKey="name" type="category" axisLine={false} tickLine={false} tick={{ fill: "#4b5563", fontSize: 12 }} width={100} />
                  <RechartsTooltip
                    cursor={{ fill: "#f9fafb" }}
                    contentStyle={{ borderRadius: "8px", border: "1px solid #e5e7eb", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" }}
                  />
                  <Bar dataKey="count" radius={[0, 4, 4, 0]} barSize={24}>
                    {dealsByStageData.map((entry, index) => (
                      <Cell key={`stage-${index}`} fill={entry.fill} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Leads by Status</h2>
              <button className="text-gray-400 hover:text-gray-600">
                <MoreHorizontalIcon className="h-5 w-5" />
              </button>
            </div>
            <div className="flex h-64 w-full items-center justify-center">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={leadsByStatusData} cx="50%" cy="50%" innerRadius={60} outerRadius={80} paddingAngle={2} dataKey="value">
                    {leadsByStatusData.map((entry, index) => (
                      <Cell key={`lead-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <RechartsTooltip contentStyle={{ borderRadius: "8px", border: "1px solid #e5e7eb", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" }} />
                  <Legend verticalAlign="middle" align="right" layout="vertical" iconType="circle" wrapperStyle={{ fontSize: "12px", color: "#4b5563" }} />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
          <div className="mb-6 flex items-center justify-between">
            <h2 className="text-base font-semibold text-gray-900">Monthly Activity</h2>
            <button className="text-gray-400 hover:text-gray-600">
              <MoreHorizontalIcon className="h-5 w-5" />
            </button>
          </div>
          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={monthlyActivityData} margin={{ top: 5, right: 20, left: -20, bottom: 5 }}>
                <CartesianGrid stroke="#f3f4f6" strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="month" axisLine={false} tickLine={false} tick={{ fill: "#6b7280", fontSize: 12 }} dy={10} />
                <YAxis axisLine={false} tickLine={false} tick={{ fill: "#6b7280", fontSize: 12 }} />
                <RechartsTooltip contentStyle={{ borderRadius: "8px", border: "1px solid #e5e7eb", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" }} />
                <Legend iconType="circle" wrapperStyle={{ paddingTop: "20px", fontSize: "12px" }} />
                <Line type="monotone" dataKey="leads" name="Leads Created" stroke="#3b82f6" strokeWidth={3} dot={{ r: 4, strokeWidth: 2 }} activeDot={{ r: 6 }} />
                <Line type="monotone" dataKey="deals" name="Opportunities Advanced" stroke="#22c55e" strokeWidth={3} dot={{ r: 4, strokeWidth: 2 }} activeDot={{ r: 6 }} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="flex flex-col rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Recent Activity</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">View All</button>
            </div>
            <div className="flex-1 space-y-5">
              {recentActivity.map((activity) => {
                const user = users.find((u) => u.id === activity.fromUserId);
                return (
                  <div key={activity.id} className="flex items-start gap-3">
                    {user && <Avatar src={user.avatar} fallback={user.name} size="sm" className="mt-0.5" />}
                    <div className="min-w-0 flex-1">
                      <p className="text-sm leading-snug text-gray-900">
                        <span className="font-semibold">{user?.name}</span>{" "}
                        <span className="text-gray-600">{activity.message}</span>{" "}
                        <span className="font-medium">{activity.relatedTo}</span>
                      </p>
                      <p className="mt-1 text-xs text-gray-400">{activity.timeAgo}</p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="flex flex-col rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-base font-semibold text-gray-900">Top Opportunities</h2>
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700">View All</button>
            </div>
            <div className="flex-1 overflow-x-auto">
              <table className="w-full whitespace-nowrap text-left text-sm">
                <thead className="border-y border-gray-100 bg-gray-50 text-xs text-gray-500">
                  <tr>
                    <th className="px-4 py-2 font-medium">Deal Name</th>
                    <th className="px-4 py-2 font-medium">Amount</th>
                    <th className="px-4 py-2 font-medium">Stage</th>
                    <th className="px-4 py-2 font-medium">Owner</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {topDeals.map((deal) => {
                    const owner = users.find((u) => u.id === deal.assignedToId);
                    return (
                      <tr key={deal.id} className="transition-colors hover:bg-gray-50">
                        <td className="px-4 py-3">
                          <div className="flex items-center gap-2">
                            <Avatar src={deal.avatar} fallback={deal.title} size="sm" className="bg-gray-100" />
                            <span className="font-medium text-gray-900">{deal.title}</span>
                          </div>
                        </td>
                        <td className="px-4 py-3 font-medium text-gray-900">{deal.amount}</td>
                        <td className="px-4 py-3">
                          <StatusBadge status={deal.status} />
                        </td>
                        <td className="px-4 py-3">{owner && <Avatar src={owner.avatar} fallback={owner.name} size="sm" />}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="mb-8 rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
          <div className="mb-6 flex items-center justify-between">
            <h2 className="text-base font-semibold text-gray-900">Upcoming Tasks</h2>
            <button className="text-sm font-medium text-blue-600 hover:text-blue-700">View All</button>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full whitespace-nowrap text-left text-sm">
              <thead className="border-y border-gray-100 bg-gray-50 text-xs text-gray-500">
                <tr>
                  <th className="w-8 px-4 py-2 font-medium"></th>
                  <th className="px-4 py-2 font-medium">Task</th>
                  <th className="px-4 py-2 font-medium">Priority</th>
                  <th className="px-4 py-2 font-medium">Due Date</th>
                  <th className="px-4 py-2 font-medium">Assigned To</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {upcomingTasks.map((task) => {
                  const assignee = users.find((u) => u.id === task.assignedToId);
                  return (
                    <tr key={task.id} className="group transition-colors hover:bg-gray-50">
                      <td className="px-4 py-3">
                        <CircleIcon className="h-4 w-4 cursor-pointer text-gray-300 transition-colors group-hover:text-blue-500" />
                      </td>
                      <td className="px-4 py-3 font-medium text-gray-900">{task.title}</td>
                      <td className="px-4 py-3">{getPriorityBadge(task.priority)}</td>
                      <td className="px-4 py-3 text-gray-600">{task.dueDate}</td>
                      <td className="px-4 py-3">
                        {assignee && (
                          <div className="flex items-center gap-2">
                            <Avatar src={assignee.avatar} fallback={assignee.name} size="sm" />
                            <span className="text-xs text-gray-600">{assignee.name}</span>
                          </div>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
