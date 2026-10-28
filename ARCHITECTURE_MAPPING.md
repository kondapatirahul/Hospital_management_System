# CareFlow architecture mapping

CareFlow is implemented in Java 17 with Spring Boot, Thymeleaf, Spring Security, Spring Data JPA and MySQL. The Python World Building Platform is a reference for organization and breadth only; its Python modules should not be copied into this Java codebase.

| Reference-style concern | CareFlow location |
|---|---|
| Role dashboards / request handlers | `backend/src/main/java/com/healthcare/hospital/controller/` |
| Domain services and shared business logic | `backend/src/main/java/com/healthcare/hospital/service/` |
| Persistent domain objects | `backend/src/main/java/com/healthcare/hospital/entity/` |
| Data access | `backend/src/main/java/com/healthcare/hospital/repository/` |
| Enumerated workflow states | `backend/src/main/java/com/healthcare/hospital/enums/` |
| Role-specific views | `frontend/templates/patient/`, `receptionist/`, `doctor/`, `pharmacy/`, `laboratory/`, `billing/`, `admin/`, `staff/` |
| Shared styles and browser behavior | `frontend/static/css/`, `frontend/static/js/` |
| SQL schema/reference scripts | `database/` |

Keep new business rules in services when they are shared across controllers; validate and authorize every state-changing route; use repositories instead of direct SQL in controllers; and add tests for each workflow.
