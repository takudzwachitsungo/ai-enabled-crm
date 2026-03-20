# Project Phases

## Phase 0 - Foundation Setup
### Goal
Prepare engineering baseline and guardrails.

### Deliverables
- Repository bootstrap and CI checks.
- Architecture and coding standards finalized.
- Base Spring Boot app with module package skeletons.
- Tenant context, auth baseline, global exception handling.

### Exit Criteria
- Project starts and health endpoints pass.
- Authenticated request carries tenant context.
- Base documentation approved.

## Phase 1 - MVP Core CRM
### Goal
Deliver first usable CRM for internal pilot.

### Deliverables
- Identity + RBAC + audit logs.
- Leads, Contacts, Accounts, Opportunities.
- Tasks/Notes/Activities timeline.
- Basic dashboards.
- Email + WhatsApp integration (basic flows).
- AI summarization and drafting endpoints.
- Basic workflow automation (trigger-condition-action).

### Exit Criteria
- End-to-end lead-to-opportunity flow works.
- Role scoping validated.
- AI output traceable and tenant-scoped.

## Phase 2 - Service & Marketing Expansion
### Goal
Extend product to support customer care and engagement.

### Deliverables
- Ticket management + SLA rules.
- Campaigns + audience segmentation.
- Knowledge base and canned responses.
- Advanced analytics and scheduled reports.

### Exit Criteria
- Ticket lifecycle SLA reports available.
- Campaign metrics visible by tenant.

## Phase 3 - Commerce + Advanced AI
### Goal
Increase retention and monetization depth.

### Deliverables
- Product catalog, quotes, invoices (CRM-adjacent commerce).
- POS/ERP connectors.
- Lead scoring and churn/health models.
- Deeper automation and recommendation engine.

### Exit Criteria
- Commerce events linked to CRM records.
- AI insights produce measurable workflow impact.

## Phase 4 - Platformization
### Goal
Scale for larger tenants and partner ecosystem.

### Deliverables
- Custom entities and low-code workflow builder.
- Advanced integration marketplace.
- Optional dedicated-tenant deployment model.

### Exit Criteria
- Extension model stable.
- Large-tenant isolation options production-ready.
