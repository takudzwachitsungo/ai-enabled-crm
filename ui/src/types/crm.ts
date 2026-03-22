export interface AuthSession {
  baseUrl: string;
  tenantId: string;
  username: string;
  password?: string;
  accessToken?: string;
  tokenType?: string;
  expiresAt?: string;
}

export interface IdentityResponse {
  tenantId: string | null;
  tenantName: string | null;
  fullName: string;
  email: string;
  username: string;
  authorities: string[];
}

export interface AuthSessionResponse {
  tenantId: string;
  tenantName: string;
  fullName: string;
  email: string;
  authorities: string[];
  accessToken: string | null;
  tokenType: string | null;
  expiresAt: string | null;
}

export interface AppUserRecord {
  id: number;
  fullName: string;
  email: string;
  role: string;
  active: boolean;
  createdAt: string;
}

export interface IntegrationConnectionRecord {
  id: number;
  name: string;
  channelType: string;
  provider: string;
  marketplaceAppKey: string | null;
  marketplaceVersion: string | null;
  status: string;
  createdAt: string;
}

export interface IntegrationMarketplaceAppRecord {
  appKey: string;
  name: string;
  category: string;
  channelType: string;
  provider: string;
  version: string;
  status: string;
  summary: string;
  capabilities: string[];
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

export interface AudienceSegmentRecord {
  id: number;
  name: string;
  sourceType: string;
  criteria: string;
  estimatedSize: number | null;
  active: boolean;
  createdAt: string;
}

export interface CampaignRecord {
  id: number;
  name: string;
  channelType: string;
  status: string;
  audienceSegmentId: number;
  audienceSegmentName: string;
  subject: string;
  body: string;
  scheduledAt: string | null;
  deliveredCount: number;
  lastExecutedAt: string | null;
  createdAt: string;
}

export interface ProductRecord {
  id: number;
  name: string;
  description: string | null;
  unitPrice: number;
  status: string;
  createdAt: string;
}

export interface ReportSnapshotRecord {
  id: number;
  name: string;
  reportType: string;
  deliveryChannel: string;
  scheduleCadence: string;
  status: string;
  snapshotPayload: string;
  generatedAt: string;
  createdAt: string;
}

export interface KnowledgeBaseArticleRecord {
  id: number;
  title: string;
  category: string;
  body: string;
  published: boolean;
  createdAt: string;
}

export interface CannedResponseRecord {
  id: number;
  title: string;
  channelType: string;
  category: string | null;
  body: string;
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

export interface AiInteractionRecord {
  id: number;
  name: string;
  operationType: string;
  sourceType: string;
  sourceId: number | null;
  promptText: string;
  outputText: string;
  modelName: string;
  createdAt: string;
}

export interface CustomEntityDefinitionRecord {
  id: number;
  name: string;
  apiName: string;
  pluralLabel: string | null;
  fieldSchemaJson: string;
  active: boolean;
  createdAt: string;
}

export interface CustomEntityRecordItem {
  id: number;
  definitionId: number;
  recordDataJson: string;
  createdAt: string;
}

export interface WorkflowDefinitionRecord {
  id: number;
  name: string;
  triggerType: string;
  triggerFilter: string | null;
  targetEntityType: string | null;
  targetEntityApiName: string | null;
  conditionsJson: string | null;
  actionType: string;
  actionSubject: string;
  actionDetails: string | null;
  actionConfigJson: string | null;
  active: boolean;
  createdAt: string;
}

export interface WorkflowBuilderCustomEntityOption {
  definitionId: number;
  apiName: string;
  name: string;
}

export interface WorkflowBuilderCatalog {
  triggerTypes: string[];
  actionTypes: string[];
  targetEntityTypes: string[];
  customEntities: WorkflowBuilderCustomEntityOption[];
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
  integrations: IntegrationConnectionRecord[];
  marketplaceApps: IntegrationMarketplaceAppRecord[];
  workflowDefinitions: WorkflowDefinitionRecord[];
  workflowCatalog: WorkflowBuilderCatalog | null;
  customEntityDefinitions: CustomEntityDefinitionRecord[];
  users: AppUserRecord[];
  leads: LeadRecord[];
  contacts: ContactRecord[];
  accounts: AccountRecord[];
  opportunities: OpportunityRecord[];
  tickets: TicketRecord[];
  activities: ActivityRecord[];
  auditLogs: AuditLogRecord[];
  communications: CommunicationRecord[];
  audienceSegments: AudienceSegmentRecord[];
  campaigns: CampaignRecord[];
  products: ProductRecord[];
  reportSnapshots: ReportSnapshotRecord[];
  knowledgeArticles: KnowledgeBaseArticleRecord[];
  cannedResponses: CannedResponseRecord[];
  quotes: QuoteRecord[];
  invoices: InvoiceRecord[];
  aiInteractions: AiInteractionRecord[];
}
