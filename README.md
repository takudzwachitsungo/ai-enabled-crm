# AI-First CRM (Monolith)

An AI-native, multi-tenant CRM platform designed for SMEs. The system is built as a **monolith** with clear domain boundaries and a single shared package convention.

## Project Goals
- Unified customer data across sales, support, and communication.
- AI features embedded in workflows (summaries, drafting, insights).
- Strong tenant isolation, RBAC, auditability, and extensibility.
- API-first and integration-friendly design from day one.

## Phase-1 MVP Scope
- Multi-tenant authentication and authorization.
- Core CRM entities: Leads, Contacts, Accounts, Opportunities.
- Activities: Tasks, Notes, Meetings, Timeline feed.
- Basic channels: Email + WhatsApp integration foundations.
- AI assistance: summarization + message drafting.
- Workflow engine: trigger -> condition -> action (basic).
- Dashboards + audit logs.

## Recommended Technology Baseline
- Backend: Java 21 + Spring Boot
- Database: PostgreSQL
- Cache/queues: Redis
- Async jobs: Spring scheduler + queue workers
- Object storage: S3-compatible
- Frontend (later phase): React + TypeScript

## Repository Structure
```text
.
├── pom.xml
├── README.md
├── ai-service
│   ├── app
│   ├── requirements.txt
│   └── Dockerfile
├── docker-compose.yml
├── src
│   ├── main
│   │   ├── java/com/dala/crm
│   │   │   ├── config
│   │   │   ├── controller
│   │   │   ├── dto
│   │   │   ├── entity
│   │   │   ├── exception
│   │   │   ├── impl
│   │   │   ├── repo
│   │   │   ├── security
│   │   │   └── service
│   │   └── resources
│   │       └── application.yml
│   └── test
└── docs
    ├── architecture
    │   ├── system-architecture.md
    │   └── module-packaging-standard.md
    ├── planning
    │   ├── project-phases.md
    │   └── timeline-guide.md
    └── standards
        └── documentation-standard.md
```

## Package Convention
All backend classes must be packaged under `com.dala.crm` using this layout:
- `entity` - JPA/domain entities
- `dto` - request/response transport objects
- `repo` - repository interfaces and custom queries
- `config` - application and feature configuration
- `security` - auth, tenant context, and authorization policies
- `service` - service contracts/interfaces
- `impl` - service implementations
- `controller` - REST endpoints
- `exception` - domain/application exception handling

Detailed rules: [docs/architecture/module-packaging-standard.md](docs/architecture/module-packaging-standard.md)

## Current Scope
- Flat package structure under `com.dala.crm`.
- Initial lead flow implemented as reference (`LeadController`, `LeadService`, `LeadServiceImpl`).
- Additional domain placeholders exist in the same package convention.

## Documentation Index
- System architecture: [docs/architecture/system-architecture.md](docs/architecture/system-architecture.md)
- Packaging standard: [docs/architecture/module-packaging-standard.md](docs/architecture/module-packaging-standard.md)
- Project phases: [docs/planning/project-phases.md](docs/planning/project-phases.md)
- Timeline guide: [docs/planning/timeline-guide.md](docs/planning/timeline-guide.md)
- Documentation style standard: [docs/standards/documentation-standard.md](docs/standards/documentation-standard.md)

## Next Build Step
Code scaffold is now created. Next, we should implement Phase-1 domain stories starting with:
1. Identity + RBAC foundations.
2. Lead/Contact/Account workflows.
3. Email and WhatsApp adapters.
4. Workflow rules engine (basic trigger-condition-action).

## Local Run (Backend)
Prerequisites:
- Java 21
- Maven 3.9+
- Docker (recommended) or PostgreSQL 14+

Default DB config is in `src/main/resources/application.yml` and can be overridden with env vars.

### Start local infrastructure with Docker
```bash
docker compose up -d
docker compose ps
```

Optional:
```bash
cp .env.example .env
```

Run:
```bash
mvn spring-boot:run
```

Smoke test:
```bash
./scripts/smoke-test.sh
```

Health endpoint:
```bash
curl http://localhost:8080/api/health
```

Lead API examples (tenant header required):
```bash
curl -X POST http://localhost:8080/api/v1/leads \
  -H \"Content-Type: application/json\" \
  -H \"X-Tenant-Id: tenant-demo\" \
  -u local-dev:local-dev-pass \
  -d '{\"fullName\":\"Jane Doe\",\"email\":\"jane@example.com\"}'
```

Stop Docker services:
```bash
docker compose down
```

## AI Python Layer
The repo includes a dedicated Python AI service in [ai-service/README.md](ai-service/README.md) for:
- summarization (`/v1/summarize`)
- drafting (`/v1/draft`)
- provider abstraction (`mock` or `openai`)
