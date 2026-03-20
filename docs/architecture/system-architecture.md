# System Architecture - AI-First CRM Modular Monolith

## 1. Architecture Style
The system will be implemented as a **modular monolith**:
- Single deployable application.
- Strict internal module boundaries.
- Domain-driven packages and APIs.
- Async/event-driven behavior inside the monolith.

Reasoning:
- Faster delivery for MVP.
- Simpler operations and debugging.
- Better consistency for cross-domain workflows.
- Easier future decomposition into microservices.

## 2. Core Architecture Principles
- **Tenant-first design**: all transactional data is tenant-scoped.
- **Security by default**: RBAC, audit logging, and explicit authorization checks.
- **API-first**: all capabilities exposed through stable APIs.
- **AI guardrails**: no destructive AI action without policy checks and human approval.
- **Extensibility**: custom fields, workflow hooks, and integration endpoints.

## 3. High-Level Component View
- **API Layer**
  - REST controllers for CRM, workflows, channels, and AI endpoints.
- **Application Layer**
  - Use-case orchestration and transactional boundaries.
- **Domain Modules**
  - Core CRM, Sales, Support, Communication, Workflow, AI, Identity/Tenancy.
- **Infrastructure Layer**
  - DB persistence, queue adapters, external integrations, file/object storage.
- **Cross-cutting Layer**
  - Security, validation, exception mapping, observability, audit logging.

## 4. Initial Domain Areas
- `identity-tenancy`: users, roles, permissions, tenant context.
- `crm-core`: leads, contacts, accounts, opportunities, activities.
- `communication`: conversations, messages, templates, channel adapters.
- `workflow`: trigger-condition-action engine and execution tracking.
- `ai-assistant`: summarization, drafting, recommendation endpoints.
- `reporting`: dashboards and aggregate queries.
- `integration`: webhook, import/export, external connector contracts.

Code packaging note:
- Runtime code is packaged under `com.dala.crm` using layered folders (`repo`, `entity`, `exception`, `dto`, `config`, `security`, `service`, `impl`, `controller`).

## 5. Data & Multi-Tenancy Model
### MVP Choice
- Shared PostgreSQL database with tenant isolation via `tenant_id`.

### Mandatory Controls
- Tenant filter in every transactional query.
- Guardrails against cross-tenant joins.
- Tenant-aware cache key strategy.
- Tenant-aware background job payloads.
- Full audit logs for sensitive operations.

## 6. Security Model
- JWT/OAuth2 authentication.
- Role-based permissions by module/action.
- Record-level access controls for team scope.
- Field-level masking for sensitive attributes.
- Immutable audit trail for create/update/delete and admin actions.

## 7. AI Architecture Boundaries
- **AI Orchestrator**: central entry for all AI features.
- **Prompt Templates**: versioned and auditable.
- **Context Builder**: gathers tenant-scoped business data.
- **Policy Layer**: checks data sensitivity and allowed actions.
- **Provider Abstraction**: pluggable LLM providers.
- **AI Logs**: prompt/input metadata, output, and decision trace.

Implementation note:
- Java monolith calls a dedicated Python `ai-service` (FastAPI) for model-facing operations.
- This keeps AI provider churn isolated from core transactional CRM modules.

## 8. Integration Strategy
- REST APIs for first-party clients.
- Webhooks for event notifications.
- Queue-backed integration jobs for retries and resilience.
- Connectors for Email and WhatsApp in MVP.

## 9. Observability and Reliability
- Structured logging with correlation IDs.
- Metrics for API latency, workflow throughput, and AI usage.
- Distributed tracing (OpenTelemetry).
- Retry and dead-letter strategy for async jobs.

## 10. Future Service Extraction Plan
Modules can be extracted later when one or more of these are true:
- Independent scaling need.
- Team ownership boundaries are stable.
- Module has clear API/event contracts.
- Operational overhead is justified.

Likely first candidates: `communication`, `ai-assistant`, `reporting`.
