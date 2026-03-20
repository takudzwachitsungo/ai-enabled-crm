# Documentation Standard

## Purpose
Ensure the codebase stays maintainable through clear and consistent technical documentation.

## Required Documentation
- Root `README.md` for project-level orientation.
- `docs/architecture/*` for architectural decisions and boundaries.
- `docs/planning/*` for roadmap, timelines, and delivery phases.
- Module-level README (to be added once code modules exist).

## Code Documentation Rules
- Public classes and methods must include concise JavaDoc.
- Explain business intent, not only technical behavior.
- Document side effects (events, outbound integrations, DB writes).
- Document failure modes and thrown exceptions.
- Keep comments current; stale comments must be removed.

## API Documentation Rules
- Each endpoint documents request schema, response schema, and errors.
- Include tenant and permission requirements explicitly.
- Include idempotency/retry notes for mutating endpoints.

## Change Management
For any major feature, update at least:
- architecture docs (if boundaries change),
- phase/timeline docs (if scope moves),
- module docs (once module exists).

## Definition of Done (Documentation)
A task is not complete unless:
- behavior is implemented,
- tests are present,
- docs are updated for developer and reviewer understanding.
