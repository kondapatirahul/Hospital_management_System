# CareFlow Phase 5 — Enterprise Integration Hub

## Scope
Adds ten persisted operational workspaces: notification tracking, integration endpoint registry, scheduling windows, automation run ledger, access review, data-retention cases, system incidents, analytics snapshots, consent records and webhook delivery tracking.

Each workspace has a JPA entity with optimistic locking, repository queries, transactional service operations, workflow transitions, assignment, notes, evidence reference, rescheduling, priority changes, overdue reporting, dashboard aggregates, filtering, search and CSV export. Shared Thymeleaf views provide list, create, detail, status transition, assignment and note actions.

## Navigation
The hub is available at `/enterprise/phase5`. Each module is under `/enterprise/phase5/<workspace-slug>`.

## Important integration notes
- Spring Security is expected to protect the application globally. Confirm your current security configuration grants access only to intended roles before deployment.
- The module tracks evidence references; it does not upload or store arbitrary evidence files.
- Consent records here are workflow tracking, not a substitute for the existing legal consent process until reviewed and integrated.
- Automation runs are records and do not execute arbitrary jobs or shell commands.
- Analytics snapshots are operational registers; no patient-level analytics is synthesized by this module.
- CSV exports neutralize leading spreadsheet formula characters.
- Validate the full project with Maven and MySQL after merging. Maven/browser integration was not available in the build environment used to assemble this ZIP.

## LOC counting
The accompanying `tools/phase5_loc_report.py` counts nonblank production lines in newly added Phase 5 Java, template, CSS, and SQL assets separately. Existing project code, docs, and test code are not included in production LOC.
