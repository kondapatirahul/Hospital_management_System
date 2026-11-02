# CareFlow Phase 4 — Administration & Finance

## Scope
This phase adds ten persisted operational registers: revenue plans, payer contracts, expense reviews, purchase approvals, asset register, staff credential compliance, audit findings, service-level cases, budget transfers, and compliance attestations. Each register has a JPA entity/repository/service/controller, list and create screens, status transition handling, assignment/evidence endpoints, dashboard summaries, due-date filtering, and CSV export.

## Safety and correctness
- Status transitions are checked in the service layer; unsupported transitions are rejected.
- Reference codes are checked for duplicates.
- Amounts cannot be negative at service validation.
- Decision transitions require a note or evidence for selected terminal/approval states.
- CSV fields are quoted and formula-like cells are prefixed to reduce spreadsheet formula injection risk.
- Optimistic locking uses `@Version` on each persisted record.
- This is operational workflow scaffolding, not a substitute for jurisdiction-specific finance, insurance, HR or clinical compliance review.

## Routes
Each workspace is available under `/enterprise/phase4/<lowercase-domain>`; CSV export is `/export.csv`, create is `/new`, and transitions are POSTed to `/{id}/transition`. Verify these routes against the project's existing Spring Security rules.

## Database
Hibernate development schema generation may create the tables; a matching SQL reference migration is provided at `database/phase4_enterprise_operations.sql`. Review and apply to staging first if production uses Flyway or Liquibase.

## Verification status
Static source and ZIP checks were performed. Maven, MySQL integration, browser workflows, and role authorization were not executed in this environment. Treat this phase as an implementation candidate pending full integration tests.
