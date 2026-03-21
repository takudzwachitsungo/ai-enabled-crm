# AI-Enabled CRM Platform

AI-Enabled CRM is a multi-tenant customer relationship management platform designed for SMEs. The backend is built with Spring Boot and a dedicated Python AI service, with tenant isolation, RBAC, and workflow-driven operations treated as first-class concerns from the beginning.

## Overview

This repository currently focuses on the backend foundation and the first CRM workflow slices needed for an internal MVP:

- Tenant-aware authentication and authorization baseline
- Core CRM records for leads, contacts, accounts, and opportunities
- Activities and timeline feed for operational visibility
- AI service boundary for summarization and drafting
- Documentation, CI, and local development infrastructure

## Current Capabilities

### CRM Core
- Leads: create, list, and fetch by ID
- Contacts: create, list, and fetch by ID
- Accounts: create, list, and fetch by ID
- Opportunities: create, list, and fetch by ID
- Activities: create, list, and fetch by ID
- Timeline: tenant-scoped activity feed ordered newest first

### Security
- HTTP Basic authentication for local development
- Tenant header enforcement on protected endpoints
- Role/authority-based endpoint access
- Identity introspection endpoint for local validation

### AI Service
- `POST /v1/summarize`
- `POST /v1/draft`
- Provider abstraction for `mock` and `openai`

## Architecture

The application is being developed as a modular backend platform with strict package boundaries under `com.dala.crm`.

### Design Principles
- Tenant-first data access
- Security by default
- API-first domain delivery
- Clear service and controller separation
- AI integration isolated from transactional CRM logic

### Backend Components
- Spring Boot API for CRM domain operations
- PostgreSQL for persistence
- Python FastAPI service for model-facing AI operations
- Docker Compose for local infrastructure

### Package Layout

```text
com.dala.crm
├── config
├── controller
├── dto
├── entity
├── exception
├── impl
├── repo
├── security
└── service
```

Additional architecture and planning context is available in:

- [docs/architecture/system-architecture.md](docs/architecture/system-architecture.md)
- [docs/architecture/module-packaging-standard.md](docs/architecture/module-packaging-standard.md)
- [docs/planning/project-phases.md](docs/planning/project-phases.md)
- [docs/planning/timeline-guide.md](docs/planning/timeline-guide.md)
- [docs/standards/documentation-standard.md](docs/standards/documentation-standard.md)

## Technology Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Python 3.12
- FastAPI
- OpenAI Python SDK
- Docker Compose
- GitHub Actions

## Repository Structure

```text
.
├── ai-service
│   ├── app
│   ├── requirements.txt
│   └── Dockerfile
├── docs
├── scripts
├── src
│   ├── main
│   └── test
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop or Docker Engine

### Start Infrastructure

```bash
docker compose up -d postgres
docker compose ps
```

To run the AI service as well:

```bash
docker compose up -d
```

### Run the Backend

```bash
mvn spring-boot:run
```

The backend starts on `http://localhost:8080`.

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

AI service configuration is controlled through environment variables:

- `AI_PROVIDER`
- `OPENAI_API_KEY`
- `OPENAI_MODEL`

The included `mock` provider is the default for local development.

## Local Authentication Model

The current local development setup uses in-memory users:

```text
local-dev / local-dev-pass
  Authorities: leads, contacts, accounts, opportunities, activities read/write + identity read

local-view / local-view-pass
  Authorities: leads, contacts, accounts, opportunities, activities read + identity read
```

All protected backend requests must include:

- Basic authentication credentials
- `X-Tenant-Id` header

Example:

```bash
curl -u local-view:local-view-pass \
  -H "X-Tenant-Id: tenant-demo" \
  http://localhost:8080/api/v1/identity/me
```

## API Reference

### Public Endpoint

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

### AI Service

- `GET /health`
- `POST /v1/summarize`
- `POST /v1/draft`

## Example Requests

Create a lead:

```bash
curl -X POST http://localhost:8080/api/v1/leads \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"fullName":"Jane Doe","email":"jane@example.com"}'
```

Create a contact:

```bash
curl -X POST http://localhost:8080/api/v1/contacts \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"fullName":"Jane Contact","email":"contact@example.com","companyName":"Acme"}'
```

Create an account:

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"Acme Corp","industry":"Manufacturing","website":"https://acme.example.com"}'
```

Create an opportunity:

```bash
curl -X POST http://localhost:8080/api/v1/opportunities \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"name":"Acme Renewal","accountName":"Acme Corp","amount":12500.00,"stage":"PROPOSAL"}'
```

Create an activity:

```bash
curl -X POST http://localhost:8080/api/v1/activities \
  -u local-dev:local-dev-pass \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-demo" \
  -d '{"type":"TASK","subject":"Follow up on quote","relatedEntityType":"OPPORTUNITY","relatedEntityId":31,"details":"Call customer on Friday."}'
```

Read the timeline:

```bash
curl -u local-view:local-view-pass \
  -H "X-Tenant-Id: tenant-demo" \
  http://localhost:8080/api/v1/timeline
```

## Testing

### Automated Tests

Run backend tests with Maven:

```bash
mvn test
```

The repository includes web-layer tests covering:

- public health access
- authentication requirements
- tenant header enforcement
- RBAC for viewer vs writer access
- timeline read access

### CI

GitHub Actions runs backend tests through:

- `.github/workflows/backend-ci.yml`

### Manual Validation

For manual validation, confirm the following:

- `/api/health` returns `UP` without authentication
- protected endpoints return `401` without credentials
- protected endpoints return `400` without `X-Tenant-Id`
- `local-view` can read CRM records and timeline
- `local-view` cannot create leads, accounts, opportunities, or activities

## Development Status

Completed so far:

- Phase 0 engineering baseline and hardening
- Local RBAC and tenant enforcement foundation
- Core CRM CRUD for leads, contacts, accounts, and opportunities
- Activity management and timeline feed
- Python AI service boundary

Planned next:

- JWT/OAuth2 integration
- audit logging
- workflow automation basics
- communication adapters
- richer AI traceability in CRM flows

## Notes

- The current auth implementation is intended for development and internal validation.
- Maven is required to run the backend test suite locally.
- The AI service is intentionally isolated so provider changes do not ripple through CRM business logic.
