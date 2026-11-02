# CareFlow Phase 7 — Integration Testing & CI Foundation

## Purpose

Phase 7 adds repeatable repository checks and a GitHub Actions pipeline. It is intentionally a quality phase, not a LOC-generation phase: adding thousands of lines without verified behavior would make the project harder to maintain.

## Local checks

From the project root:

```powershell
python tools\phase7_integration_audit.py
Get-ChildItem frontend\static\js -Filter *.js -Recurse | ForEach-Object { node --check $_.FullName }
mvn -f backend\pom.xml clean test
```

The CI workflow runs the source audit, JavaScript syntax checks, and Maven test suite on pushes and pull requests.

## What the checks prove

- Expected dashboard templates and enterprise module directories exist.
- Java and Java test source inventories exist.
- JavaScript files parse with Node.js.
- Maven compilation and tests pass if the CI job succeeds.

## What they do not prove

Static checks do not prove that login works, role boundaries are correctly enforced, database writes persist, templates render with real model data, or multi-step workflows work. Those require a running application, configured database, and browser/API tests. No runtime success is claimed by this phase alone.

## Runtime verification checklist

- [ ] `mvn -f backend/pom.xml clean test` passes.
- [ ] Application starts with a non-empty DB_PASSWORD supplied via environment variable.
- [ ] Each of the eight roles can sign in and reaches the intended dashboard.
- [ ] Cross-role access attempts are denied server-side.
- [ ] Patient, appointment, consultation, lab, prescription, pharmacy, and billing records persist after restart.
- [ ] Phase 4 and Phase 5 records can be created, updated, listed, and exported.
- [ ] Browser console and server logs show no unhandled errors during the main workflows.
