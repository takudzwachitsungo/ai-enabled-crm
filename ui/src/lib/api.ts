import {
  AccountRecord,
  ActivityRecord,
  AuditLogRecord,
  AuthSession,
  CommunicationRecord,
  ContactRecord,
  CrmSnapshot,
  DashboardForecastResponse,
  DashboardSummaryResponse,
  InvoiceRecord,
  IdentityResponse,
  LeadRecord,
  OpportunityRecord,
  QuoteRecord,
  TicketRecord,
} from "../types/crm";

function authHeader(session: AuthSession) {
  return `Basic ${btoa(`${session.username}:${session.password}`)}`;
}

async function requestJson<T>(session: AuthSession, path: string): Promise<T> {
  const response = await fetch(`${session.baseUrl}${path}`, {
    headers: {
      Authorization: authHeader(session),
      "X-Tenant-Id": session.tenantId,
    },
  });

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const body = await response.json();
      if (body?.message) {
        message = body.message;
      }
    } catch {
      // Ignore JSON parsing errors for non-JSON responses.
    }
    throw new Error(message);
  }

  return response.json() as Promise<T>;
}

export async function loadCrmSnapshot(session: AuthSession): Promise<CrmSnapshot> {
  const [
    identity,
    summary,
    forecast,
    leads,
    contacts,
    accounts,
    opportunities,
    tickets,
    activities,
    auditLogs,
    communications,
    quotes,
    invoices,
  ] = await Promise.all([
    requestJson<IdentityResponse>(session, "/api/v1/identity/me"),
    requestJson<DashboardSummaryResponse>(session, "/api/v1/dashboard/summary"),
    requestJson<DashboardForecastResponse>(session, "/api/v1/dashboard/forecast"),
    requestJson<LeadRecord[]>(session, "/api/v1/leads"),
    requestJson<ContactRecord[]>(session, "/api/v1/contacts"),
    requestJson<AccountRecord[]>(session, "/api/v1/accounts"),
    requestJson<OpportunityRecord[]>(session, "/api/v1/opportunities"),
    requestJson<TicketRecord[]>(session, "/api/v1/tickets"),
    requestJson<ActivityRecord[]>(session, "/api/v1/activities"),
    requestJson<AuditLogRecord[]>(session, "/api/v1/audit-logs"),
    requestJson<CommunicationRecord[]>(session, "/api/v1/communications"),
    requestJson<QuoteRecord[]>(session, "/api/v1/quotes"),
    requestJson<InvoiceRecord[]>(session, "/api/v1/invoices"),
  ]);

  return {
    identity,
    summary,
    forecast,
    leads,
    contacts,
    accounts,
    opportunities,
    tickets,
    activities,
    auditLogs,
    communications,
    quotes,
    invoices,
  };
}
