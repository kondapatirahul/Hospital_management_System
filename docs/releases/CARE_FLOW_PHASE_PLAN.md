# CareFlow phased development and release policy

Every phase is delivered as a separate ZIP built on the previous phase. The project should not be restarted from an older baseline between phases.

## Phase 1 — Audit & Foundation
Source inventory, compilation blocker repair, dashboard inventory, test matrix, and acceptance criteria.

## Phase 2 — Dashboards & role workflows
Complete dashboard data sources, route links, form submission behavior, validation, filters, pagination, empty/error/loading states, and role-specific action wiring.

## Phase 3 — Clinical operations
Appointments, consultation notes, patient history, prescriptions, lab orders/results, pharmacy dispensing, nursing observations, handovers, transfers, and discharge flows.

## Phase 4 — Administration & finance
Billing and payments, insurance claims, refunds/adjustments, purchasing/inventory, staff scheduling, reporting, and administrative review.

## Phase 5 — Enterprise integration
Notifications, reminders, calendars, audit trails, analytics, cross-module state transitions, permission review, and operational dashboards.

## Phase 6 — Release verification
Regression and integration tests, setup instructions, environment-variable review, database setup/migrations, known-issues list, and release packaging.

## LOC reporting rules

Each release must report:
- baseline and current production Java non-blank LOC;
- net new production Java LOC (additions minus deletions, excluding blank-only lines);
- test LOC separately;
- HTML/template, CSS, and JavaScript LOC separately;
- deleted/replaced lines and any generated/vendor code excluded.

Do not count duplicate code, generated output, comments-only lines, or blank lines as production LOC. LOC is a progress metric, not a substitute for working features or passing tests.
