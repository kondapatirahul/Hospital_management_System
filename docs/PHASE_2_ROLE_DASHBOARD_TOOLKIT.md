# CareFlow Phase 2 — Role Dashboard Interaction Toolkit

## Scope delivered

Phase 2 adds a shared dashboard table interaction layer to the existing eight role dashboards: Patient, Receptionist, Doctor, Pharmacy, Laboratory, Billing & Finance, Admin, and Staff & Nursing. Existing controller routes, Thymeleaf form actions, and server-side role checks remain the source of truth.

### User-facing behavior

- Search within each enabled dashboard table as the user types.
- Filter table rows by a detected `Status` column when the table has one.
- Display a live visible-row count and a clear-filters action.
- Display a no-match message when a search or status filter hides all data rows.
- Preserve existing table content and forms; filtering is client-side and does not mutate server records.
- Provide CSV export only when a table explicitly opts in with `data-cf-export="true"`. Phase 2 opts in only the pharmacy recent stock-activity table.
- Escape CSV quotes and prefix formula-leading cell values so spreadsheet software is less likely to execute them as formulas.
- Exclude form controls and their values from exported cells. Exports contain displayed table text only.
- Keep controls keyboard accessible, announce row counts, and hide the tool strip when printing.

## Implementation files

- `frontend/static/js/dashboard-table-tools.js` — shared search, status filtering, row counts, no-match state, and guarded CSV export.
- `frontend/static/css/dashboard-table-tools.css` — responsive control layout, focus indicators, and print behavior.
- `frontend/templates/{patient,receptionist,doctor,pharmacy,laboratory,billing,admin,staff}/dashboard.html` — wired assets and enabled the existing dashboard data tables.
- `tools/phase2_dashboard_toolkit_check.py` — dependency-free static regression checks for all eight role dashboards and export safety guardrails.

## LOC accounting

LOC is reported as physical nonblank source lines, not statements or a measure of completeness.

| Category | Phase 2 added LOC |
|---|---:|
| Production JavaScript | 158 physical lines |
| Production CSS | 75 |
| New production assets subtotal | 233 |
| Static regression checker (test/tooling) | 55 nonblank lines |
| Existing dashboard templates | 8 files modified; each has a new asset reference and table wiring changes on existing markup lines |

No existing Java production LOC is counted as newly added in this phase. The Java application code was not changed in Phase 2.

## Validation performed

- `node --check frontend/static/js/dashboard-table-tools.js` — passed.
- `python tools/phase2_dashboard_toolkit_check.py` — passed; confirms all eight dashboard templates reference the CSS and JS, each enables at least one table, and the pharmacy export is explicitly opted in while patient dashboard export is not enabled.
- Basic Python HTML parser check on the eight dashboard templates — passed.
- Maven, MySQL integration, and browser interaction tests were not run in this environment. The static checks do not prove the entire application works end-to-end.

## Local verification

From the project root:

```powershell
python tools\phase2_dashboard_toolkit_check.py
node --check frontend\static\js\dashboard-table-tools.js
mvn -f backend\pom.xml clean test
```

Then run the application and verify each role using test accounts. Do not use real patient data for screenshots or test exports.
