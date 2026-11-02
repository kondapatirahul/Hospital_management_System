# CareFlow architecture mapping

CareFlow is a Java 17 / Spring Boot 3.3.5 application using Thymeleaf, Spring Security, Spring Data JPA and MySQL.

## Backend package layout

- `core/config/` — Spring configuration and application infrastructure.
- `modules/` — domain-oriented enterprise modules (admin, doctor, patient, pharmacy, billing, laboratory, staff, quality, reception, workflows, phase4 and phase5).
- `modules/legacy/` — original cross-cutting controllers, services, entities and repositories retained as a compatibility boundary; migrate these incrementally into domain modules only with focused reference and behavior tests.
- `shared/dto/` and `shared/enums/` — cross-module DTOs and enumerations.
- `CareFlowApplication.java` — root Spring Boot entry point; component scanning remains rooted at `com.healthcare.hospital`.
- `src/main/resources/` — runtime configuration and classpath resources.

The Java source files were relocated to match their declared package names. Package references in Java sources were updated consistently. Existing enterprise packages were promoted from `enterprise` to `modules`.

## Frontend and runtime compatibility

- `frontend/templates/` remains in its existing Thymeleaf lookup layout because controller return values and fragment includes address these runtime paths. It is already separated by role/domain (patient, receptionist, doctor, pharmacy, laboratory, billing, admin, staff, OT, safety, workflows, etc.). Moving it under `templates/modules/` without a complete template-path migration would break page rendering.
- `frontend/static/css/` and `frontend/static/js/` retain their existing public asset paths because templates reference them directly. Organize new assets by domain and migrate existing assets only alongside verified reference updates.
- `database/` retains the existing SQL schemas and operational scripts.
- `docs/` retains implementation and audit reports.

## Validation notes

Production Java line count is preserved exactly by this reorganization. Maven packaging could not be executed in the processing environment because Maven is not installed; run `mvn -f backend/pom.xml clean verify` in a Java 17 + Maven environment before deployment.
