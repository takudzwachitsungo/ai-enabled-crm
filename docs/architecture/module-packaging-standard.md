# Packaging Standard

This document defines mandatory package conventions for the monolith backend.

## 1. Base Package Pattern
```text
com.dala.crm.<layer>
```

Example:
```text
com.dala.crm.service
```

## 2. Required Package Layout
The backend must use this shared layout:
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

Optional packages when needed:
- `mapper` - DTO/entity transformations
- `events` - domain events and event handlers
- `validator` - custom validation rules

## 3. Responsibilities
- `entity`
  - Persisted domain entities.
  - Keep business invariants close to entity behavior where practical.
- `dto`
  - External/API request and response models.
  - Must not leak JPA entities to controllers.
- `repo`
  - Spring Data repositories and custom query contracts.
- `config`
  - Module-local bean configuration and wiring.
- `security`
  - Permission policies, guards, and authorization evaluators.
- `service`
  - Business capability interfaces.
  - Defines use cases.
- `impl`
  - Transactional implementations.
  - Coordinates entities, repos, events, and integrations.
- `controller`
  - REST endpoints exposing use cases.
- `exception`
  - Domain-specific exceptions and error codes.

## 4. Coding Rules
- Controllers call `service` interfaces only.
- Services do not return entities directly to API layer.
- DTO validation is explicit (`jakarta.validation`).
- Keep transactional boundaries in `impl` classes.
- Keep business logic out of controllers.
- Keep cross-domain coordination explicit in service contracts.

## 5. Example Package Usage
```text
com.dala.crm
├── controller
│   └── LeadController.java
├── dto
│   ├── LeadCreateRequest.java
│   └── LeadResponse.java
├── entity
│   └── Lead.java
├── exception
│   └── LeadNotFoundException.java
├── impl
│   └── LeadServiceImpl.java
├── repo
│   └── LeadRepository.java
└── service
    └── LeadService.java
```

## 6. Documentation Requirements
Every public service interface and implementation class must include:
- Class-level JavaDoc describing module use case.
- Method-level JavaDoc with input/output and side effects.
- Exception semantics when business rules fail.

Complex workflows also require a short sequence note in design docs.
