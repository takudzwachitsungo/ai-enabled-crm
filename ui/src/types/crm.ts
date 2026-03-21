export interface AuthSession {
  baseUrl: string;
  tenantId: string;
  username: string;
  password: string;
}

export interface IdentityResponse {
  username: string;
  authorities: string[];
}

export interface LeadRecord {
  id: number;
  fullName: string;
  email: string;
  status: string;
  createdAt: string;
}

export interface ContactRecord {
  id: number;
  fullName: string;
  email: string;
  companyName: string;
  createdAt: string;
}

export interface AccountRecord {
  id: number;
  name: string;
  industry: string;
  website: string;
  createdAt: string;
}

export interface OpportunityRecord {
  id: number;
  name: string;
  accountName: string;
  amount: number;
  stage: string;
  createdAt: string;
}

export interface TicketRecord {
  id: number;
  title: string;
  description: string;
  priority: string;
  status: string;
  assignee: string;
  sourceChannel: string;
  relatedEntityType: string;
  relatedEntityId: number | null;
  dueAt: string | null;
  escalatedAt: string | null;
  resolvedAt: string | null;
  createdAt: string;
}

export interface ActivityRecord {
  id: number;
  type: string;
  subject: string;
  relatedEntityType: string;
  relatedEntityId: number | null;
  details: string;
  createdAt: string;
}

export interface AuditLogRecord {
  id: number;
  actor: string;
  action: string;
  entityType: string;
  entityId: number | null;
  summary: string;
  createdAt: string;
}

export interface CommunicationRecord {
  id: number;
  name: string;
  channelType: string;
  direction: string;
  participant: string;
  subject: string;
  messageBody: string;
  relatedEntityType: string;
  relatedEntityId: number | null;
  createdAt: string;
}

export interface QuoteRecord {
  id: number;
  accountId: number;
  accountName: string;
  name: string;
  amount: number;
  status: string;
  validUntil: string | null;
  createdAt: string;
}

export interface InvoiceRecord {
  id: number;
  accountId: number;
  accountName: string;
  invoiceNumber: string;
  amount: number;
  status: string;
  dueAt: string | null;
  createdAt: string;
}

export interface DashboardSummaryResponse {
  leadCount: number;
  contactCount: number;
  accountCount: number;
  opportunityCount: number;
  activityCount: number;
  ticketCount: number;
  overdueTicketCount: number;
  activeWorkflowCount: number;
  integrationConnectionCount: number;
  communicationCount: number;
  aiInteractionCount: number;
}

export interface DashboardForecastResponse {
  totalPipelineAmount: number;
  weightedPipelineAmount: number;
  activeQuoteAmount: number;
  issuedInvoiceAmount: number;
  collectedInvoiceAmount: number;
  projectedRevenueAmount: number;
  generatedAt: string;
}

export interface CrmSnapshot {
  identity: IdentityResponse | null;
  summary: DashboardSummaryResponse | null;
  forecast: DashboardForecastResponse | null;
  leads: LeadRecord[];
  contacts: ContactRecord[];
  accounts: AccountRecord[];
  opportunities: OpportunityRecord[];
  tickets: TicketRecord[];
  activities: ActivityRecord[];
  auditLogs: AuditLogRecord[];
  communications: CommunicationRecord[];
  quotes: QuoteRecord[];
  invoices: InvoiceRecord[];
}
