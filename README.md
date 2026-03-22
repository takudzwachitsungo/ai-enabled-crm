# Alad CRM

## Open-Source AI CRM for Tenant-Aware Customer Operations

[Documentation](docs/architecture/system-architecture.md) · [Project Phases](docs/planning/project-phases.md) · [Timeline](docs/planning/timeline-guide.md) · [Local Setup](#installation) · [AI Service](ai-service/README.md)

![Dashboard](screenshots/Screenshot%202026-03-22%20125311.png)

# Installation

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+
- Docker Desktop or Docker Engine
- Python 3.11+ for the AI service

## Infrastructure

Start PostgreSQL:

```bash
docker compose up -d postgres
```

## Backend

```bash
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

## Frontend

```bash
cd ui
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

## Python AI Service

```bash
cd ai-service
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8090
```

AI service URL:

```text
http://localhost:8090
```

# Why Alad CRM

We built Alad CRM around a simple idea: a modern CRM should not stop at contact storage and pipelines. It should help teams run customer operations across sales, service, marketing, commerce, and AI-assisted execution from one tenant-aware platform.

The platform is designed to:

- give each company its own secure workspace
- keep CRM, service, commerce, and automation flows connected
- make AI outputs traceable and operational instead of decorative
- provide a foundation that can grow from product MVP into an extensible platform

# What You Can Do With Alad CRM

Below are the main areas already implemented in the repository.

- [Manage leads, contacts, accounts, and opportunities](#manage-leads-contacts-accounts-and-opportunities)
- [Run service operations with tickets, SLA rules, and knowledge tools](#run-service-operations-with-tickets-sla-rules-and-knowledge-tools)
- [Execute campaigns, reporting, and customer communication workflows](#execute-campaigns-reporting-and-customer-communication-workflows)
- [Track quotes, invoices, products, and commerce events](#track-quotes-invoices-products-and-commerce-events)
- [Use AI summaries, drafting, recommendations, and workspace chat](#use-ai-summaries-drafting-recommendations-and-workspace-chat)
- [Extend the platform with custom entities, workflows, and integrations](#extend-the-platform-with-custom-entities-workflows-and-integrations)

## Manage leads, contacts, accounts, and opportunities

Alad CRM supports the core commercial workflow with tenant-scoped records, dashboards, and pipeline views.

![Leads Workspace](screenshots/Screenshot%202026-03-22%20125257.png)

![Opportunities Pipeline](screenshots/Screenshot%202026-03-22%20125328.png)

## Run service operations with tickets, SLA rules, and knowledge tools

The backend includes:

- tickets and assignment workflows
- SLA policies and escalation automation
- knowledge base articles
- canned responses
- audit logging and activity history

## Execute campaigns, reporting, and customer communication workflows

Marketing and reporting foundations include:

- audience segments
- campaigns and delivery execution
- report snapshots
- conversation records across channels
- dashboard analytics and operational summaries

## Track quotes, invoices, products, and commerce events

Commerce workflows include:

- product catalog records
- quote and invoice management
- quote-to-invoice conversion
- renewal automation
- refund and cancellation status handling
- POS and ERP style commerce event ingestion

## Use AI summaries, drafting, recommendations, and workspace chat

The platform already supports:

- AI summaries
- AI drafting
- lead scoring
- account health scoring
- churn risk analysis
- next-best-action recommendations
- a workspace assistant chat that uses tenant-scoped company data

The Python AI service includes a LangGraph-backed assistant route and an OpenAI-capable provider abstraction.

## Extend the platform with custom entities, workflows, and integrations

Phase 4 foundations are also in place:

- custom entity definitions and records
- workflow builder foundations
- integration marketplace and connection lifecycle support
- tenant profile deployment metadata for shared and dedicated models

# Authentication and Tenant Model

Each company signs into its own workspace using a workspace ID. The backend enforces tenant isolation through tenant-scoped records and tenant-aware security.

Primary auth flow:

- workspace signup
- workspace login
- JWT-backed authenticated sessions

For local compatibility and testing, `tenant-demo` still supports:

```text
local-dev / local-dev-pass
local-view / local-view-pass
```

# Seed Data

The repository includes a ready-to-use tenant dataset for `dala-inc`:

- [`scripts/seed-dala-inc.sql`](scripts/seed-dala-inc.sql)

That workspace contains live sample records across CRM, service, marketing, commerce, platform, reporting, and AI history.

# Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- React
- Vite
- Python
- FastAPI
- LangGraph
- OpenAI SDK
- Docker Compose
- GitHub Actions

# Repository Structure

```text
.
|-- ai-service
|-- docs
|-- screenshots
|-- scripts
|-- src
|   |-- main
|   `-- test
|-- ui
|-- .github
|-- docker-compose.yml
|-- pom.xml
`-- README.md
```

# Testing

## Backend

```bash
mvn test
```

## Frontend

```bash
cd ui
npm run build
```

## Containerized Backend Verification

```bash
docker run --rm -v %cd%:/workspace -w /workspace maven:3.9.9-eclipse-temurin-21 mvn -q test
```

# Current Status

The repository now covers the planned feature roadmap through platformization scope:

- Phase 0 completed
- Phase 1 completed
- Phase 2 completed
- Phase 3 completed
- Phase 4 completed in foundation scope

What remains is mostly production hardening and maturity work:

- live validation of the Python AI assistant path in your runtime
- deeper end-to-end and integration tests
- richer auth lifecycle flows such as invites and resets
- pagination and backend query consistency
- observability, retries, reconciliation, and deployment hardening
- frontend polish and performance cleanup

# Contributing and Project Context

Additional project context lives in:

- [`docs/architecture/system-architecture.md`](docs/architecture/system-architecture.md)
- [`docs/architecture/module-packaging-standard.md`](docs/architecture/module-packaging-standard.md)
- [`docs/planning/project-phases.md`](docs/planning/project-phases.md)
- [`docs/planning/timeline-guide.md`](docs/planning/timeline-guide.md)

If you are contributing locally, start with the backend, frontend, and AI service setup sections above, then use the seeded `dala-inc` workspace for live validation.
