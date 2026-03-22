import {
  AccountRecord,
  ActivityRecord,
  AiInteractionRecord,
  AiChatConversationMessage,
  AppUserRecord,
  AudienceSegmentRecord,
  AuthSessionResponse,
  AuditLogRecord,
  CampaignRecord,
  CannedResponseRecord,
  CustomEntityDefinitionRecord,
  CustomEntityRecordItem,
  AuthSession,
  CommunicationRecord,
  ContactRecord,
  CrmSnapshot,
  DashboardForecastResponse,
  DashboardSummaryResponse,
  IntegrationConnectionRecord,
  IntegrationMarketplaceAppRecord,
  InvoiceRecord,
  IdentityResponse,
  KnowledgeBaseArticleRecord,
  LeadRecord,
  OpportunityRecord,
  ProductRecord,
  QuoteRecord,
  ReportSnapshotRecord,
  TicketRecord,
  WorkflowBuilderCatalog,
  WorkflowDefinitionRecord,
} from "../types/crm";

function authHeader(session: AuthSession) {
  if (session.accessToken) {
    return `${session.tokenType ?? "Bearer"} ${session.accessToken}`;
  }
  if (session.password) {
    return `Basic ${btoa(`${session.username}:${session.password}`)}`;
  }
  throw new Error("No valid session credentials are available.");
}

function authHeaders(session: AuthSession) {
  return {
    Authorization: authHeader(session),
    "X-Tenant-Id": session.tenantId,
  };
}

async function requestPublicJson<T>(baseUrl: string, path: string, body: unknown): Promise<T> {
  const response = await fetch(`${baseUrl}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const payload = await response.json();
      if (payload?.message) {
        message = payload.message;
      }
    } catch {
      // ignore non-JSON bodies
    }
    throw new Error(message);
  }

  return response.json() as Promise<T>;
}

async function requestJson<T>(session: AuthSession, path: string): Promise<T> {
  const response = await fetch(`${session.baseUrl}${path}`, {
    headers: authHeaders(session),
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

async function requestWithBody<T>(
  session: AuthSession,
  path: string,
  method: "POST" | "PATCH",
  body?: unknown,
): Promise<T> {
  const response = await fetch(`${session.baseUrl}${path}`, {
    method,
    headers: {
      ...authHeaders(session),
      "Content-Type": "application/json",
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const payload = await response.json();
      if (payload?.message) {
        message = payload.message;
      }
    } catch {
      // ignore non-JSON bodies
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
    integrations,
    marketplaceApps,
    workflowDefinitions,
    workflowCatalog,
    customEntityDefinitions,
    users,
    tickets,
    activities,
    auditLogs,
    communications,
    audienceSegments,
    campaigns,
    products,
    reportSnapshots,
    knowledgeArticles,
    cannedResponses,
    quotes,
    invoices,
    aiInteractions,
  ] = await Promise.all([
    requestJson<IdentityResponse>(session, "/api/v1/identity/me"),
    requestJson<DashboardSummaryResponse>(session, "/api/v1/dashboard/summary"),
    requestJson<DashboardForecastResponse>(session, "/api/v1/dashboard/forecast"),
    requestJson<LeadRecord[]>(session, "/api/v1/leads"),
    requestJson<ContactRecord[]>(session, "/api/v1/contacts"),
    requestJson<AccountRecord[]>(session, "/api/v1/accounts"),
    requestJson<OpportunityRecord[]>(session, "/api/v1/opportunities"),
    requestJson<IntegrationConnectionRecord[]>(session, "/api/v1/integrations"),
    requestJson<IntegrationMarketplaceAppRecord[]>(session, "/api/v1/integrations/marketplace"),
    requestJson<WorkflowDefinitionRecord[]>(session, "/api/v1/workflows"),
    requestJson<WorkflowBuilderCatalog>(session, "/api/v1/workflows/builder/catalog"),
    requestJson<CustomEntityDefinitionRecord[]>(session, "/api/v1/custom-entities"),
    requestJson<AppUserRecord[]>(session, "/api/v1/users"),
    requestJson<TicketRecord[]>(session, "/api/v1/tickets"),
    requestJson<ActivityRecord[]>(session, "/api/v1/activities"),
    requestJson<AuditLogRecord[]>(session, "/api/v1/audit-logs"),
    requestJson<CommunicationRecord[]>(session, "/api/v1/communications"),
    requestJson<AudienceSegmentRecord[]>(session, "/api/v1/audience-segments"),
    requestJson<CampaignRecord[]>(session, "/api/v1/campaigns"),
    requestJson<ProductRecord[]>(session, "/api/v1/products"),
    requestJson<ReportSnapshotRecord[]>(session, "/api/v1/reports"),
    requestJson<KnowledgeBaseArticleRecord[]>(session, "/api/v1/knowledge-base"),
    requestJson<CannedResponseRecord[]>(session, "/api/v1/canned-responses"),
    requestJson<QuoteRecord[]>(session, "/api/v1/quotes"),
    requestJson<InvoiceRecord[]>(session, "/api/v1/invoices"),
    requestJson<AiInteractionRecord[]>(session, "/api/v1/ai"),
  ]);

  return {
    identity,
    summary,
    forecast,
    leads,
    contacts,
    accounts,
    opportunities,
    integrations,
    marketplaceApps,
    workflowDefinitions,
    workflowCatalog,
    customEntityDefinitions,
    users,
    tickets,
    activities,
    auditLogs,
    communications,
    audienceSegments,
    campaigns,
    products,
    reportSnapshots,
    knowledgeArticles,
    cannedResponses,
    quotes,
    invoices,
    aiInteractions,
  };
}

export function createWorkspaceUser(
  session: AuthSession,
  payload: { fullName: string; email: string; password: string; role: string },
) {
  return requestWithBody<AppUserRecord>(session, "/api/v1/users", "POST", payload);
}

export function updateWorkspaceUser(
  session: AuthSession,
  userId: number,
  payload: { role: string; active: boolean },
) {
  return requestWithBody<AppUserRecord>(session, `/api/v1/users/${userId}`, "PATCH", payload);
}

export function resetWorkspaceUserPassword(
  session: AuthSession,
  userId: number,
  payload: { password: string },
) {
  return requestWithBody<AppUserRecord>(session, `/api/v1/users/${userId}/password`, "PATCH", payload);
}

export function changeCurrentPassword(
  session: AuthSession,
  payload: { currentPassword: string; newPassword: string },
) {
  return requestWithBody<{ message: string }>(session, "/api/v1/identity/change-password", "PATCH", payload);
}

export function loginWorkspace(
  baseUrl: string,
  payload: { tenantId: string; email: string; password: string },
) {
  return requestPublicJson<AuthSessionResponse>(
    baseUrl,
    "/api/public/auth/login",
    payload,
  );
}

export function registerWorkspace(
  baseUrl: string,
  payload: {
    companyName: string;
    tenantId: string;
    fullName: string;
    email: string;
    password: string;
  },
) {
  return requestPublicJson<AuthSessionResponse>(
    baseUrl,
    "/api/public/auth/signup",
    payload,
  );
}

export function updateTicketStatus(
  session: AuthSession,
  ticketId: number,
  payload: { status: string; note?: string },
) {
  return requestWithBody<TicketRecord>(session, `/api/v1/tickets/${ticketId}/status`, "PATCH", payload);
}

export function updateTicketAssignment(
  session: AuthSession,
  ticketId: number,
  payload: { assignee: string; note?: string },
) {
  return requestWithBody<TicketRecord>(session, `/api/v1/tickets/${ticketId}/assignment`, "PATCH", payload);
}

export function convertQuoteToInvoice(session: AuthSession, quoteId: number) {
  return requestWithBody<InvoiceRecord>(session, `/api/v1/quotes/${quoteId}/convert-to-invoice`, "POST");
}

export function updateQuoteStatus(
  session: AuthSession,
  quoteId: number,
  payload: { status: string; note?: string },
) {
  return requestWithBody<QuoteRecord>(session, `/api/v1/quotes/${quoteId}/status`, "PATCH", payload);
}

export function createAiSummary(
  session: AuthSession,
  payload: { name: string; sourceType: string; sourceId?: number | null; text: string },
) {
  return requestWithBody<AiInteractionRecord>(session, "/api/v1/ai/summarize", "POST", payload);
}

export function createAiDraft(
  session: AuthSession,
  payload: {
    name: string;
    sourceType: string;
    sourceId?: number | null;
    instructions: string;
    channel?: string;
    tone?: string;
  },
) {
  return requestWithBody<AiInteractionRecord>(session, "/api/v1/ai/draft", "POST", payload);
}

export function createAiRecommendation(
  session: AuthSession,
  payload: {
    name: string;
    sourceType: string;
    sourceId: number;
    objective?: string;
    autoCreateActivity: boolean;
  },
) {
  return requestWithBody<AiInteractionRecord>(session, "/api/v1/ai/recommend", "POST", payload);
}

export function createAiChat(
  session: AuthSession,
  payload: {
    name?: string;
    message: string;
    conversation?: AiChatConversationMessage[];
  },
) {
  return requestWithBody<AiInteractionRecord>(session, "/api/v1/ai/chat", "POST", payload);
}

export function createLead(
  session: AuthSession,
  payload: { fullName: string; email: string },
) {
  return requestWithBody<LeadRecord>(session, "/api/v1/leads", "POST", payload);
}

export function createContact(
  session: AuthSession,
  payload: { fullName: string; email: string; companyName?: string },
) {
  return requestWithBody<ContactRecord>(session, "/api/v1/contacts", "POST", payload);
}

export function createTicket(
  session: AuthSession,
  payload: {
    title: string;
    description?: string;
    priority: string;
    assignee?: string;
    sourceChannel?: string;
    relatedEntityType?: string;
    relatedEntityId?: number | null;
  },
) {
  return requestWithBody<TicketRecord>(session, "/api/v1/tickets", "POST", payload);
}

export function createActivity(
  session: AuthSession,
  payload: {
    type: string;
    subject: string;
    relatedEntityType: string;
    relatedEntityId: number;
    details?: string;
  },
) {
  return requestWithBody<ActivityRecord>(session, "/api/v1/activities", "POST", payload);
}

export function createQuote(
  session: AuthSession,
  payload: {
    accountId: number;
    name: string;
    amount: number;
    status: string;
    validUntil?: string | null;
  },
) {
  return requestWithBody<QuoteRecord>(session, "/api/v1/quotes", "POST", payload);
}

export function createCommunication(
  session: AuthSession,
  payload: {
    name: string;
    channelType: string;
    direction: string;
    participant: string;
    subject?: string;
    messageBody: string;
    relatedEntityType?: string;
    relatedEntityId?: number | null;
  },
) {
  return requestWithBody<CommunicationRecord>(session, "/api/v1/communications", "POST", payload);
}

export function createAccount(
  session: AuthSession,
  payload: { name: string; industry?: string; website?: string },
) {
  return requestWithBody<AccountRecord>(session, "/api/v1/accounts", "POST", payload);
}

export function createOpportunity(
  session: AuthSession,
  payload: { name: string; accountName?: string; amount: number; stage: string },
) {
  return requestWithBody<OpportunityRecord>(session, "/api/v1/opportunities", "POST", payload);
}

export function createCustomEntityDefinition(
  session: AuthSession,
  payload: {
    name: string;
    apiName: string;
    pluralLabel?: string;
    fieldSchemaJson: string;
    active: boolean;
  },
) {
  return requestWithBody<CustomEntityDefinitionRecord>(session, "/api/v1/custom-entities", "POST", payload);
}

export function loadCustomEntityRecords(
  session: AuthSession,
  definitionId: number,
) {
  return requestJson<CustomEntityRecordItem[]>(session, `/api/v1/custom-entities/${definitionId}/records`);
}

export function createWorkflowDefinition(
  session: AuthSession,
  payload: {
    name: string;
    triggerType: string;
    triggerFilter?: string;
    targetEntityType?: string;
    targetEntityApiName?: string;
    conditionsJson?: string;
    actionType: string;
    actionSubject: string;
    actionDetails?: string;
    actionConfigJson?: string;
    active: boolean;
  },
) {
  return requestWithBody<WorkflowDefinitionRecord>(session, "/api/v1/workflows", "POST", payload);
}

export function installMarketplaceApp(
  session: AuthSession,
  payload: { appKey: string; connectionName?: string },
) {
  return requestWithBody<IntegrationConnectionRecord>(
    session,
    "/api/v1/integrations/marketplace/install",
    "POST",
    payload,
  );
}
