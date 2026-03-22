import React, { useMemo, useState } from "react";
import { BellIcon, BarChart3Icon, MegaphoneIcon, UsersIcon } from "lucide-react";
import { AudienceSegmentRecord, CampaignRecord, ReportSnapshotRecord } from "../types/crm";

interface NotificationsListProps {
  audienceSegments?: AudienceSegmentRecord[] | null;
  campaigns?: CampaignRecord[] | null;
  reportSnapshots?: ReportSnapshotRecord[] | null;
}

function formatDate(value?: string | null) {
  return value ? new Date(value).toLocaleString() : "Not scheduled";
}

export function NotificationsList({
  audienceSegments = [],
  campaigns = [],
  reportSnapshots = [],
}: NotificationsListProps) {
  const [filter, setFilter] = useState<"all" | "campaigns" | "reports">("all");

  const feedItems = useMemo(() => {
    const campaignItems = campaigns.map((campaign) => ({
      id: `campaign-${campaign.id}`,
      type: "campaign",
      title: campaign.name,
      meta: `${campaign.channelType} · ${campaign.status} · ${campaign.audienceSegmentName}`,
      detail: campaign.subject,
      timestamp: campaign.lastExecutedAt ?? campaign.scheduledAt ?? campaign.createdAt,
      statLabel: "Delivered",
      statValue: String(campaign.deliveredCount),
    }));

    const reportItems = reportSnapshots.map((report) => ({
      id: `report-${report.id}`,
      type: "report",
      title: report.name,
      meta: `${report.reportType} · ${report.status} · ${report.deliveryChannel}`,
      detail: `${report.scheduleCadence} cadence`,
      timestamp: report.generatedAt,
      statLabel: "Generated",
      statValue: formatDate(report.generatedAt),
    }));

    const items = [...campaignItems, ...reportItems].sort(
      (left, right) => new Date(right.timestamp).getTime() - new Date(left.timestamp).getTime(),
    );

    if (filter === "campaigns") {
      return items.filter((item) => item.type === "campaign");
    }

    if (filter === "reports") {
      return items.filter((item) => item.type === "report");
    }

    return items;
  }, [campaigns, filter, reportSnapshots]);

  return (
    <div className="flex min-h-0 flex-1 flex-col overflow-hidden bg-[#f8f9fa]">
      <div className="shrink-0 border-b border-gray-200 bg-white px-4 py-4 sm:px-6">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-2 text-lg">
            <span className="font-semibold text-gray-900">Campaigns & Reports</span>
          </div>
          <div className="rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm text-gray-500">
            {campaigns.length} campaigns · {reportSnapshots.length} reports
          </div>
        </div>
      </div>

      <div className="shrink-0 border-b border-gray-200 bg-white px-4 py-3 sm:px-6">
        <div className="flex flex-wrap items-center gap-2">
          {[
            ["all", "All"],
            ["campaigns", "Campaigns"],
            ["reports", "Reports"],
          ].map(([value, label]) => (
            <button
              key={value}
              onClick={() => setFilter(value as "all" | "campaigns" | "reports")}
              className={`rounded-md px-3 py-1.5 text-sm transition-colors ${
                filter === value ? "bg-gray-100 font-medium text-gray-900" : "border border-gray-200 text-gray-600 hover:bg-gray-50"
              }`}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto">
        <div className="mx-auto max-w-6xl space-y-6 p-4 sm:p-6">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
            <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="flex items-center gap-2 text-sm text-gray-500">
                <MegaphoneIcon className="h-4 w-4" />
                Active campaigns
              </div>
              <div className="mt-3 text-2xl font-semibold text-gray-900">
                {campaigns.filter((item) => item.status !== "DRAFT").length}
              </div>
            </div>
            <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="flex items-center gap-2 text-sm text-gray-500">
                <UsersIcon className="h-4 w-4" />
                Audience segments
              </div>
              <div className="mt-3 text-2xl font-semibold text-gray-900">{audienceSegments.length}</div>
            </div>
            <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
              <div className="flex items-center gap-2 text-sm text-gray-500">
                <BarChart3Icon className="h-4 w-4" />
                Report snapshots
              </div>
              <div className="mt-3 text-2xl font-semibold text-gray-900">{reportSnapshots.length}</div>
            </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
            <div className="border-b border-gray-100 px-5 py-4">
              <div className="font-semibold text-gray-900">Activity feed</div>
            </div>
            <div className="divide-y divide-gray-100">
              {feedItems.length === 0 ? (
                <div className="px-5 py-12 text-center text-sm text-gray-500">
                  No campaign or report activity is available yet.
                </div>
              ) : (
                feedItems.map((item) => (
                  <div key={item.id} className="flex items-start gap-4 px-5 py-4 hover:bg-gray-50">
                    <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-full ${item.type === "campaign" ? "bg-pink-50 text-pink-600" : "bg-blue-50 text-blue-600"}`}>
                      {item.type === "campaign" ? <MegaphoneIcon className="h-4 w-4" /> : <BellIcon className="h-4 w-4" />}
                    </div>
                    <div className="min-w-0 flex-1">
                      <div className="text-sm font-medium text-gray-900">{item.title}</div>
                      <div className="mt-1 text-xs text-gray-500">{item.meta}</div>
                      <div className="mt-2 text-sm text-gray-700">{item.detail}</div>
                    </div>
                    <div className="text-right text-xs text-gray-500">
                      <div className="font-medium text-gray-700">{item.statLabel}</div>
                      <div className="mt-1">{item.statValue}</div>
                      <div className="mt-2">{formatDate(item.timestamp)}</div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
            <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
              <div className="border-b border-gray-100 px-5 py-4 font-semibold text-gray-900">Audience segments</div>
              <div className="overflow-x-auto">
                <table className="w-full whitespace-nowrap text-left text-sm">
                  <thead className="bg-gray-50 text-xs text-gray-500">
                    <tr>
                      <th className="px-5 py-3 font-medium">Segment</th>
                      <th className="px-5 py-3 font-medium">Source</th>
                      <th className="px-5 py-3 font-medium">Estimated Size</th>
                      <th className="px-5 py-3 font-medium">Status</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {audienceSegments.map((segment) => (
                      <tr key={segment.id} className="hover:bg-gray-50">
                        <td className="px-5 py-3">
                          <div className="font-medium text-gray-900">{segment.name}</div>
                          <div className="text-xs text-gray-500">{segment.criteria}</div>
                        </td>
                        <td className="px-5 py-3 text-gray-600">{segment.sourceType}</td>
                        <td className="px-5 py-3 text-gray-600">{segment.estimatedSize ?? "-"}</td>
                        <td className="px-5 py-3 text-gray-600">{segment.active ? "Active" : "Inactive"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
              <div className="border-b border-gray-100 px-5 py-4 font-semibold text-gray-900">Reports</div>
              <div className="overflow-x-auto">
                <table className="w-full whitespace-nowrap text-left text-sm">
                  <thead className="bg-gray-50 text-xs text-gray-500">
                    <tr>
                      <th className="px-5 py-3 font-medium">Report</th>
                      <th className="px-5 py-3 font-medium">Type</th>
                      <th className="px-5 py-3 font-medium">Cadence</th>
                      <th className="px-5 py-3 font-medium">Generated</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {reportSnapshots.map((report) => (
                      <tr key={report.id} className="hover:bg-gray-50">
                        <td className="px-5 py-3 font-medium text-gray-900">{report.name}</td>
                        <td className="px-5 py-3 text-gray-600">{report.reportType}</td>
                        <td className="px-5 py-3 text-gray-600">{report.scheduleCadence}</td>
                        <td className="px-5 py-3 text-gray-500">{formatDate(report.generatedAt)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
