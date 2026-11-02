# CareFlow Phase 6 — Release Audit

## Purpose
Phase 6 packages the current Phase 5 project with a repeatable static release audit and a consolidated Windows check script. It does not claim that all workflows are complete or that the Java application has passed Maven tests.

## Static validation
- Eight role dashboard template files are checked.
- Security configuration source presence is checked.
- Ten Phase 5 workspace packages are checked for Java source.
- JavaScript files under `frontend/static/js` are syntax-checked when Node.js is available.
- Config files are scanned for obvious hardcoded database passwords; values are never printed.
- Source inventory reports nonblank LOC separately for production Java, Java tests, templates, and CSS.

## Required release verification on the developer machine
From the project root, run `powershell -ExecutionPolicy Bypass -File tools\run_phase6_checks.ps1`, then run `mvn -f backend\pom.xml clean test`. If Maven tests pass, start MySQL and run `mvn -f backend\pom.xml spring-boot:run`. Test all eight role logins, access restrictions, form submissions, persistence after restart, and key clinical/finance workflows manually or with integration tests.

## Verification limits
This build environment did not have Maven installed. Therefore Phase 6 has not been verified by a Maven compile/test run, a live MySQL integration run, or browser end-to-end tests. Static checks are useful but cannot establish clinical or financial correctness.

## Production readiness
Do not use this package for real patient care or real financial transactions until security review, authorization tests, database backups/migrations, privacy review, and full end-to-end testing are completed.
