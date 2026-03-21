# AI-Enabled CRM

AI-Enabled CRM is a tenant-aware CRM platform for SMEs, built with Spring Boot and a dedicated Python AI service. The current codebase delivers a complete Phase 1 backend MVP and the first Phase 2 service operations slice, including CRM records, role-based access control, workflow automation, communications logging, ticketing with SLA rules, dashboard metrics, and traceable AI interactions.

## What This Repository Includes

- Multi-tenant Spring Boot backend with tenant header enforcement
- Local RBAC model for development and internal validation
- Core CRM records: leads, contacts, accounts, opportunities
- Activities and timeline feed
- Service tickets and SLA policy management
- Workflow automation with trigger-action rules
- Integration connection registry for email and WhatsApp style channels
- Communication record tracking
- Dashboard summary metrics
- Traceable AI summary and draft endpoints
- Audit logging for key tenant-scoped actions
- Python AI service boundary for model-facing work
- Docker Compose and GitHub Actions for local setup and CI

## Platform Scope Delivered

### CRM Core
- Create, list, and fetch leads
- Create, list, and fetch contacts
- Create, list, and fetch accounts
- Create, list, and fetch opportunities
- Create, list, and fetch activities
- Read tenant timeline feed

### Automation and Operations
- Create and list workflow definitions
- Trigger workflow-created activities from lead and opportunity creation
- Read dashboard summary KPIs
- Create and list service tickets
- Update ticket status and assignment
- Create and list SLA policies
- Register and list integration connections
- Create and list communication records
- Read tenant audit history

### AI
- Create tenant-scoped summaries
- Create tenant-scoped drafts
- Persist AI interaction traces for later review

### Security
- HTTP Basic authentication for local development
- Authority-based endpoint access
- Tenant enforcement through `X-Tenant-Id`
- Identity introspection endpoint for validating effective authorities

## Architecture

### Backend Components
- Spring Boot API for CRM and operational workflows
- PostgreSQL for application persistence
- FastAPI-based AI service for model-facing capabilities
- Docker Compose for local infrastructure

### Design Principles
- Tenant-first data isolation
- Security by default
- API-first delivery
- Clear controller, service, and repository boundaries
- AI outputs treated as auditable application artifacts

### Package Layout

```text
com.dala.crm
|-- controller
|-- dto
|-- entity
|-- exception
|-- impl
|-- repo
|-- security
`-- service
```

Additional design and planning context lives in:

- [docs/architecture/system-architecture.md](docs/architecture/system-architecture.md)
- [docs/architecture/module-packaging-standard.md](docs/architecture/module-packaging-standard.md)
- [docs/planning/project-phases.md](docs/planning/project-phases.md)
- [docs/planning/timeline-guide.md](docs/planning/timeline-guide.md)

## Technology Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Python 3.12
- FastAPI
- Docker Compose
- GitHub Actions

## Repository Structure

```text
.
|-- ai-service
|-- docs
|-- scripts
|-- src
|   |-- main
|   `-- test
|-- .github
|-- docker-compose.yml
|-- pom.xml
`-- README.md
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop or Docker Engine

### Start Infrastructure

```bash
docker compose up -d postgres
```

To run the Python AI service as well:

```bash
docker compose up -d
```

### Start the Backend

```bash
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`.

## Configuration

### Backend

Default backend configuration is defined in `src/main/resources/application.yml`.

Supported database environment variables:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

### AI Service

The Python AI service is controlled through:

- `AI_PROVIDER`
- `OPENAI_API_KEY`
- `OPENAI_MODEL`

The included `mock` provider is the default for local development.

## Local Authentication Model

Current local users:

```text
local-dev / local-dev-pass
  Full read/write access across CRM, tickets, SLA policies, workflows, integrations, communications, AI, dashboard, and audit logs

local-view / local-view-pass
  Read access across CRM, tickets, SLA policies, workflows, integrations, communications, AI history, dashboard, and identity
```

All protected requests must include:

- Basic authentication credentials
- `X-Tenant-Id`

Example:

```bash
curl -u local-view:local-view-pass \
  -H "X-Tenant-Id: tenant-demo" \
  http://localhost:8080/api/v1/identity/me
```

## API Reference

### Public

- `GET /api/health`

### Identity

- `GET /api/v1/identity/me`

### Leads

- `POST /api/v1/leads`
- `GET /api/v1/leads`
- `GET /api/v1/leads/{id}`

### Contacts

- `POST /api/v1/contacts`
- `GET /api/v1/contacts`
- `GET /api/v1/contacts/{id}`

### Accounts

- `POST /api/v1/accounts`
- `GET /api/v1/accounts`
- `GET /api/v1/accounts/{id}`

### Opportunities

- `POST /api/v1/opportunities`
- `GET /api/v1/opportunities`
- `GET /api/v1/opportunities/{id}`

### Activities

- `POST /api/v1/activities`
- `GET /api/v1/activities`
- `GET /api/v1/activities/{id}`

### Timeline

- `GET /api/v1/timeline`

### Tickets

- `POST /api/v1/tickets`
- `GET /api/v1/tickets`
- `GET /api/v1/tickets/{id}`
- `PATCH /api/v1/tickets/{id}/status`
- `PATCH /api/v1/tickets/{id}/assignment`

### SLA Policies

- `POST /api/v1/sla-policies`
- `GET /api/v1/sla-policies`

### Workflows

- `POST /api/v1/workflows`
- `GET /api/v1/workflows`

### Dashboard

- `GET /api/v1/dashboard/summary`

### Integrations

- `POST /api/v1/integrations`
- `GET /api/v1/integrations`

### Communications

- `POST /api/v1/communications`
- `GET /api/v1/communications`

### AI

- `POST /api/v1/ai/summarize`
- `POST /api/v1/ai/draft`
- `GET /api/v1/ai`

### Audit Logs

- `GET /api/v1/audit-logs`

### Python AI Service

- `GET /health`
- `POST /v1/summarize`
- `POST /v1/draft`

## Example Requests

Create a workflow:

```bash
curl -X POST http://localhost:8080/api/v1/workflows \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"Lead follow-up","triggerType":"LEAD_CREATED","actionType":"CREATE_ACTIVITY","actionSubject":"Call new lead","actionDetails":"Reach out within one business day.","active":true}'
```

Create an SLA policy:

```bash
curl -X POST http://localhost:8080/api/v1/sla-policies \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"High Priority Response","priority":"HIGH","responseHours":4,"active":true}'
```

Create a ticket:

```bash
curl -X POST http://localhost:8080/api/v1/tickets \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"title":"Payment issue","description":"Customer could not complete payment.","priority":"HIGH","assignee":"Support Team","sourceChannel":"EMAIL","relatedEntityType":"ACCOUNT","relatedEntityId":21}'
```

Update ticket status:

```bash
curl -X PATCH http://localhost:8080/api/v1/tickets/101/status \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"status":"IN_PROGRESS","note":"Support team accepted the ticket."}'
```

Update ticket assignment:

```bash
curl -X PATCH http://localhost:8080/api/v1/tickets/101/assignment \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"assignee":"Escalation Team","note":"Escalated to specialist queue."}'
```

Read dashboard summary:

```bash
curl -u local-view:local-view-pass \
  -H "X-Tenant-Id: tenant-demo" \
  http://localhost:8080/api/v1/dashboard/summary
```

Register an integration:

```bash
curl -X POST http://localhost:8080/api/v1/integrations \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"WhatsApp Cloud","channelType":"WHATSAPP","provider":"META","status":"CONNECTED"}'
```

Create a communication record:

```bash
curl -X POST http://localhost:8080/api/v1/communications \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"Welcome email","channelType":"EMAIL","direction":"OUTBOUND","participant":"jane@example.com","subject":"Welcome","messageBody":"Thanks for your interest.","relatedEntityType":"LEAD","relatedEntityId":1}'
```

Create an AI summary:

```bash
curl -X POST http://localhost:8080/api/v1/ai/summarize \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"Lead summary","sourceType":"LEAD","sourceId":1,"text":"Jane requested a pricing walkthrough and wants implementation next month."}'
```

Create an AI draft:

```bash
curl -X POST http://localhost:8080/api/v1/ai/draft \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"Follow-up email","sourceType":"LEAD","sourceId":1,"instructions":"Write a short follow-up email for a pricing walkthrough.","channel":"EMAIL","tone":"PROFESSIONAL"}'
```

Read audit logs:

```bash
curl -u local-dev:local-dev-pass \
  -H "X-Tenant-Id: tenant-demo" \
  http://localhost:8080/api/v1/audit-logs
```

## Testing

### Local Test Run

```bash
mvn test
```

### Current Automated Coverage

- public health access
- authentication and tenant enforcement
- authority checks for writers vs readers
- timeline and audit log access control
- workflow, dashboard, ticket, SLA, integration, communication, and AI endpoint security

### CI

GitHub Actions runs the backend suite through:

- `.github/workflows/backend-ci.yml`

## Current Status

Completed:

- Phase 0 foundation and hardening
- Phase 1 CRM core records
- activities and timeline
- audit logging
- workflow automation basics
- dashboard summary metrics
- communication and integration foundations
- traceable AI summary and draft flows
- Phase 2 ticket management and SLA due-date basics
- Phase 2 ticket status and assignment workflows

Planned next:

- SLA escalations and breach workflows
- knowledge base and canned responses
- campaigns, audience segmentation, and scheduled reporting
- JWT or OAuth2 resource server integration
- production-grade AI provider orchestration from the Spring backend

## Notes

- The current authentication model is for development and internal validation.
- AI responses created by the Spring backend are intentionally traceable and tenant-scoped.
- The Python AI service remains available for deeper provider-facing integration work as the platform evolves.
