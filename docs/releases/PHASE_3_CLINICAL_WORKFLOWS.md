# CareFlow Phase 3 — Clinical Workflow Safeguards

## Scope

Phase 3 adds a shared progressive-enhancement toolkit for clinical forms and record workflows. It is loaded through the shared Thymeleaf layout and explicitly loaded by the standalone enterprise laboratory order pages.

## Included behavior

- Client-side validation for required fields, email and phone formats, numeric ranges, date bounds, and configured text lengths.
- Accessible invalid-field feedback, focus management, and an ARIA live region for status announcements.
- Duplicate-submit protection and a processing state on submit controls.
- Confirmation prompts for actions that appear to cancel, remove, dispense, complete, discharge, approve, reject, or finalize a workflow.
- Unsaved-edit warnings before navigation and browser unload.
- Character counters for textareas configured with a maximum length or `data-character-count="true"`.
- Optional template-based repeat-row hooks (`data-cfw-add-row`, `data-cfw-template`, `data-cfw-row-template`) and remove-row confirmation (`data-cfw-remove-row`).
- Status badge styling, empty-table messaging, keyboard focus visibility, reduced-motion handling, and print-friendly behavior.
- Static regression checker at `tools/phase3_clinical_workflow_check.py`.

## LOC accounting

New production LOC in this phase, counting nonblank lines in new production files:

| File | Nonblank LOC |
|---|---:|
| `frontend/static/js/clinical-workflow-tools.js` | 318 |
| `frontend/static/css/clinical-workflow-tools.css` | 17 |
| Resource includes in shared layout | 2 |
| **Total new production LOC** | **337** |

The new static regression checker has 52 nonblank lines and is reported separately as test/tooling LOC. Existing project LOC and documentation are not counted as new production code.

## Verification

- `node --check frontend/static/js/clinical-workflow-tools.js`: passed.
- `python tools/phase3_clinical_workflow_check.py`: 11/11 static checks passed.
- ZIP archive integrity: checked after packaging.
- Maven tests, database integration, and interactive browser verification were not run in the build environment. Client-side safeguards do not replace server-side validation, authorization, or clinical policy checks.

## Rollback

Remove the `clinical-workflow-tools.css` and `clinical-workflow-tools.js` includes from `frontend/templates/fragments/layout.html` and the standalone laboratory templates. Then remove the two new assets and the Phase 3 checker/report.
