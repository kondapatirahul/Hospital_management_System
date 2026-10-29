/**
 * CareFlow Healthcare Management System - Vanilla JavaScript
 * Zero external JS dependencies. Pure HTML5 / ES6+.
 */

document.addEventListener('DOMContentLoaded', function () {
    initAlertDismissal();
    initDepartmentDoctorDropdown();
    initModalHandlers();
    initClientTableSearch();
    initMobileNav();
    initDashboardShortcutFilter();
});

/**
 * Auto-dismiss alerts after 5 seconds
 */
function initAlertDismissal() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 400);
        }, 5000);
    });
}

/**
 * Dynamic Department -> Doctor selection & available slots
 */
function initDepartmentDoctorDropdown() {
    const deptSelect = document.getElementById('departmentSelect');
    const doctorSelect = document.getElementById('doctorSelect');
    const doctorFeeDisplay = document.getElementById('doctorFeeDisplay');
    const doctorHoursDisplay = document.getElementById('doctorHoursDisplay');

    if (deptSelect && doctorSelect) {
        deptSelect.addEventListener('change', function () {
            const deptId = this.value;
            doctorSelect.innerHTML = '<option value="">-- Loading Doctors... --</option>';
            doctorSelect.disabled = true;

            if (!deptId) {
                doctorSelect.innerHTML = '<option value="">-- Select Department First --</option>';
                doctorSelect.disabled = true;
                if (doctorFeeDisplay) doctorFeeDisplay.textContent = '-';
                return;
            }

            fetch('/api/doctors-by-department?departmentId=' + encodeURIComponent(deptId))
                .then(response => {
                    if (!response.ok) throw new Error('Network error loading doctors');
                    return response.json();
                })
                .then(doctors => {
                    doctorSelect.innerHTML = '<option value="">-- Choose a Doctor --</option>';
                    if (doctors.length === 0) {
                        doctorSelect.innerHTML = '<option value="">-- No Doctors in this Department --</option>';
                        doctorSelect.disabled = true;
                        return;
                    }

                    doctors.forEach(doc => {
                        const opt = document.createElement('option');
                        opt.value = doc.id;
                        opt.textContent = `${doc.name} (${doc.qualification}) - Room ${doc.room}`;
                        opt.dataset.fee = doc.fee;
                        opt.dataset.hours = doc.workingHours;
                        doctorSelect.appendChild(opt);
                    });
                    doctorSelect.disabled = false;
                })
                .catch(err => {
                    console.error('Error fetching doctors:', err);
                    doctorSelect.innerHTML = '<option value="">-- Error loading doctors --</option>';
                });
        });

        doctorSelect.addEventListener('change', function () {
            const selected = this.options[this.selectedIndex];
            if (doctorFeeDisplay && selected && selected.dataset.fee) {
                doctorFeeDisplay.textContent = '$' + parseFloat(selected.dataset.fee).toFixed(2);
            }
            if (doctorHoursDisplay && selected && selected.dataset.hours) {
                doctorHoursDisplay.textContent = selected.dataset.hours;
            }
        });
    }
}

/**
 * Universal Modal open / close logic
 */
function initModalHandlers() {
    // Open modal buttons
    document.querySelectorAll('[data-modal-target]').forEach(btn => {
        btn.addEventListener('click', function () {
            const targetId = this.getAttribute('data-modal-target');
            const modal = document.getElementById(targetId);
            if (modal) {
                // If action attributes exist, pass them into form
                const actionUrl = this.getAttribute('data-action-url');
                const form = modal.querySelector('form');
                if (form && actionUrl) {
                    form.action = actionUrl;
                }
                const apptInfo = this.getAttribute('data-appt-info');
                const infoSlot = modal.querySelector('.modal-appt-info');
                if (infoSlot && apptInfo) {
                    infoSlot.textContent = apptInfo;
                }
                modal.classList.add('active');
            }
        });
    });

    // Close modal buttons
    document.querySelectorAll('.modal-close-btn, [data-modal-close]').forEach(btn => {
        btn.addEventListener('click', function () {
            const modal = this.closest('.modal-overlay');
            if (modal) modal.classList.remove('active');
        });
    });

    // Close on background click
    document.querySelectorAll('.modal-overlay').forEach(modal => {
        modal.addEventListener('click', function (e) {
            if (e.target === this) {
                this.classList.remove('active');
            }
        });
    });
}

/**
 * Instant Client-Side Search / Filter for Tables
 */
function initClientTableSearch() {
    const searchInputs = document.querySelectorAll('[data-table-search]');
    searchInputs.forEach(input => {
        const tableSelector = input.getAttribute('data-table-search');
        const table = document.querySelector(tableSelector);
        if (!table || input.dataset.cfSearchBound === 'true') return;
        input.dataset.cfSearchBound = 'true';
        const tbody = table.tBodies && table.tBodies[0];
        if (!tbody) return;
        let status = input.parentElement.querySelector('[data-search-status]');
        if (!status) {
            status = document.createElement('small');
            status.className = 'cf-search-status';
            status.setAttribute('data-search-status', '');
            status.setAttribute('aria-live', 'polite');
            input.insertAdjacentElement('afterend', status);
        }
        const rows = Array.from(tbody.rows).filter(row => !row.hasAttribute('data-empty-state'));
        const update = () => {
            const query = input.value.toLocaleLowerCase().trim();
            let visible = 0;
            rows.forEach(row => {
                const matches = row.textContent.toLocaleLowerCase().includes(query);
                row.hidden = !matches;
                if (matches) visible++;
            });
            status.textContent = query ? `${visible} of ${rows.length} records shown` : `${rows.length} records`;
            let empty = tbody.querySelector('[data-search-empty]');
            if (!empty && rows.length) {
                empty = document.createElement('tr');
                empty.setAttribute('data-search-empty', '');
                const cell = document.createElement('td');
                cell.colSpan = Math.max(1, table.tHead?.rows[0]?.cells.length || rows[0]?.cells.length || 1);
                cell.className = 'cf-empty-search';
                cell.textContent = 'No matching records found. Try another search.';
                empty.appendChild(cell);
                tbody.appendChild(empty);
            }
            if (empty) empty.hidden = visible > 0;
        };
        input.addEventListener('input', update);
        update();
    });
}

/** Mobile sidebar: one handler, accessible state and outside-click dismissal. */
function initMobileNav() {
    const toggleBtn = document.getElementById('mobileNavToggle');
    const sidebar = document.querySelector('.app-sidebar');
    if (!toggleBtn || !sidebar || toggleBtn.dataset.cfNavBound === 'true') return;
    toggleBtn.dataset.cfNavBound = 'true';
    toggleBtn.setAttribute('aria-controls', sidebar.id || 'careflowSidebar');
    if (!sidebar.id) sidebar.id = 'careflowSidebar';
    const close = () => { sidebar.classList.remove('mobile-open'); toggleBtn.setAttribute('aria-expanded', 'false'); };
    toggleBtn.setAttribute('aria-expanded', 'false');
    toggleBtn.addEventListener('click', () => {
        const opened = sidebar.classList.toggle('mobile-open');
        toggleBtn.setAttribute('aria-expanded', String(opened));
    });
    document.addEventListener('click', event => {
        if (window.matchMedia('(max-width: 640px)').matches && sidebar.classList.contains('mobile-open') && !sidebar.contains(event.target) && !toggleBtn.contains(event.target)) close();
    });
    document.addEventListener('keydown', event => { if (event.key === 'Escape') close(); });
    window.addEventListener('resize', () => { if (window.matchMedia('(min-width: 641px)').matches) close(); });
}

/**
 * Demo Login Helper
 */
function fillDemoCredentials(role) {
    const userField = document.getElementById('username');
    const passField = document.getElementById('password');
    if (!userField || !passField) return;

    const demoAccounts = {
        admin: 'admin@hospital.com',
        patient: 'patient@hospital.com',
        doctor: 'dr.vance@hospital.com',
        receptionist: 'receptionist@hospital.com',
        pharmacy: 'pharmacist@hospital.com',
        laboratory: 'lab@hospital.com',
        billing: 'billing@hospital.com',
        nurse: 'nurse@hospital.com'
    };
    const username = demoAccounts[role];
    if (!username) return;

    userField.value = username;
    passField.value = 'Password@123';
    userField.dispatchEvent(new Event('input', { bubbles: true }));
    passField.dispatchEvent(new Event('input', { bubbles: true }));
    userField.style.borderColor = '#0d9488';
    passField.style.borderColor = '#0d9488';
    userField.focus();
    setTimeout(() => {
        userField.style.borderColor = '';
        passField.style.borderColor = '';
    }, 1200);
}

/**
 * Dynamic Prescription Row addition for Doctor Consultation Form
 */
function addPrescriptionRow() {
    const container = document.getElementById('prescriptionsList');
    if (!container) return;

    const index = container.querySelectorAll('.prescription-item-row').length;
    const row = document.createElement('div');
    row.className = 'prescription-item-row';
    row.style = 'background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px; margin-bottom: 12px; position: relative;';

    row.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
            <strong style="color: #0f2c59; font-size: 0.85rem;">Medication #${index + 1}</strong>
            <button type="button" class="btn btn-outline btn-sm" style="color: #ef4444; border-color: #fca5a5; padding: 2px 8px;" onclick="this.closest('.prescription-item-row').remove()">
                ✕ Remove
            </button>
        </div>
        <div class="form-row" style="display: grid; grid-template-columns: 2fr 1fr 1fr; gap: 10px; margin-bottom: 8px;">
            <div>
                <label class="form-label" style="font-size: 0.75rem;">Medicine Name</label>
                <input type="text" name="prescriptions[${index}].medicationName" class="form-control" placeholder="e.g. Amoxicillin, Paracetamol" required>
            </div>
            <div>
                <label class="form-label" style="font-size: 0.75rem;">Dosage</label>
                <input type="text" name="prescriptions[${index}].dosage" class="form-control" placeholder="e.g. 500 mg, 10 ml" required>
            </div>
            <div>
                <label class="form-label" style="font-size: 0.75rem;">Frequency</label>
                <input type="text" name="prescriptions[${index}].frequency" class="form-control" placeholder="e.g. 2x daily after meals" required>
            </div>
        </div>
        <div class="form-row" style="display: grid; grid-template-columns: 1fr 2fr; gap: 10px;">
            <div>
                <label class="form-label" style="font-size: 0.75rem;">Duration</label>
                <input type="text" name="prescriptions[${index}].duration" class="form-control" placeholder="e.g. 5 days, 1 month">
            </div>
            <div>
                <label class="form-label" style="font-size: 0.75rem;">Instructions / Notes</label>
                <input type="text" name="prescriptions[${index}].instructions" class="form-control" placeholder="e.g. Take with full glass of water">
            </div>
        </div>
    `;

    container.appendChild(row);
}




/* ================================================================
   CareFlow Batch 08 — workflow usability and resilient interactions
   This layer enhances existing server-rendered pages without replacing
   Spring MVC routes, Thymeleaf bindings, or database business logic.
   ================================================================ */
document.addEventListener('DOMContentLoaded', function () {
    cfInitConfirmations();
    cfInitPasswordToggles();
    cfInitSelectAll();
    cfInitPrintActions();
    cfInitCopyActions();
    cfInitDateConstraints();
    cfInitRequiredFieldFeedback();
    cfInitUnsavedChanges();
    cfInitTableSort();
    cfInitNavActiveState();
});

function cfInitConfirmations() {
    document.querySelectorAll('form[data-confirm]').forEach(form => {
        if (form.dataset.cfConfirmBound) return;
        form.dataset.cfConfirmBound = 'true';
        form.addEventListener('submit', event => {
            const message = form.dataset.confirm || 'Are you sure you want to continue?';
            if (!window.confirm(message)) event.preventDefault();
        });
    });
    document.querySelectorAll('[data-confirm-click]').forEach(control => {
        if (control.dataset.cfConfirmBound) return;
        control.dataset.cfConfirmBound = 'true';
        control.addEventListener('click', event => {
            if (!window.confirm(control.dataset.confirmClick || 'Are you sure?')) event.preventDefault();
        });
    });
}

function cfInitPasswordToggles() {
    document.querySelectorAll('input[type="password"][data-password-toggle]').forEach(input => {
        if (input.dataset.cfToggleBound) return;
        input.dataset.cfToggleBound = 'true';
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'cf-password-toggle';
        button.textContent = 'Show';
        button.setAttribute('aria-label', 'Show password');
        input.insertAdjacentElement('afterend', button);
        button.addEventListener('click', () => {
            const reveal = input.type === 'password';
            input.type = reveal ? 'text' : 'password';
            button.textContent = reveal ? 'Hide' : 'Show';
            button.setAttribute('aria-label', reveal ? 'Hide password' : 'Show password');
        });
    });
}

function cfInitSelectAll() {
    document.querySelectorAll('[data-select-all]').forEach(master => {
        if (master.dataset.cfSelectBound) return;
        master.dataset.cfSelectBound = 'true';
        const selector = master.dataset.selectAll;
        if (!selector) return;
        const updateMaster = () => {
            const children = Array.from(document.querySelectorAll(selector)).filter(el => !el.disabled);
            master.checked = children.length > 0 && children.every(el => el.checked);
            master.indeterminate = children.some(el => el.checked) && !master.checked;
        };
        master.addEventListener('change', () => document.querySelectorAll(selector).forEach(el => { if (!el.disabled) el.checked = master.checked; }));
        document.querySelectorAll(selector).forEach(el => el.addEventListener('change', updateMaster));
        updateMaster();
    });
}

function cfInitPrintActions() {
    document.querySelectorAll('[data-print-page]').forEach(control => {
        if (control.dataset.cfPrintBound) return;
        control.dataset.cfPrintBound = 'true';
        control.addEventListener('click', event => { event.preventDefault(); window.print(); });
    });
}

function cfInitCopyActions() {
    document.querySelectorAll('[data-copy-target]').forEach(control => {
        if (control.dataset.cfCopyBound) return;
        control.dataset.cfCopyBound = 'true';
        control.addEventListener('click', async () => {
            const target = document.querySelector(control.dataset.copyTarget);
            if (!target) return;
            const value = 'value' in target ? target.value : target.textContent;
            try {
                if (navigator.clipboard && window.isSecureContext) await navigator.clipboard.writeText(value);
                else {
                    const temporary = document.createElement('textarea');
                    temporary.value = value; temporary.setAttribute('readonly', '');
                    temporary.style.position = 'fixed'; temporary.style.opacity = '0';
                    document.body.appendChild(temporary); temporary.select();
                    const copied = document.execCommand('copy'); temporary.remove();
                    if (!copied) throw new Error('Clipboard copy was blocked');
                }
                cfAnnounce(control, control.dataset.copySuccess || 'Copied');
            } catch (error) {
                cfAnnounce(control, 'Unable to copy automatically');
            }
        });
    });
}

function cfInitDateConstraints() {
    const today = new Date();
    const localToday = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
    document.querySelectorAll('input[type="date"][data-no-past]').forEach(input => {
        if (!input.min || input.min < localToday) input.min = localToday;
    });
    document.querySelectorAll('input[type="number"][min]').forEach(input => {
        input.addEventListener('change', () => {
            if (input.value !== '' && Number(input.value) < Number(input.min)) input.value = input.min;
            if (input.max && input.value !== '' && Number(input.value) > Number(input.max)) input.value = input.max;
        });
    });
}

function cfInitRequiredFieldFeedback() {
    document.querySelectorAll('form').forEach(form => {
        if (form.dataset.cfValidationBound) return;
        form.dataset.cfValidationBound = 'true';
        form.querySelectorAll('input,select,textarea').forEach(field => {
            if (field.required) field.setAttribute('aria-required', 'true');
            field.addEventListener('invalid', () => {
                field.classList.add('cf-invalid');
                const label = field.labels && field.labels[0] ? field.labels[0].textContent.trim() : field.name || 'This field';
                field.setAttribute('title', `${label} needs attention`);
            });
            field.addEventListener('input', () => {
                if (field.checkValidity()) {
                    field.classList.remove('cf-invalid');
                    field.removeAttribute('title');
                }
            });
            field.addEventListener('change', () => { if (field.checkValidity()) field.classList.remove('cf-invalid'); });
        });
    });
}

function cfInitUnsavedChanges() {
    document.querySelectorAll('form[data-warn-unsaved]').forEach(form => {
        if (form.dataset.cfDirtyBound) return;
        form.dataset.cfDirtyBound = 'true';
        let dirty = false;
        form.addEventListener('input', event => {
            if (event.target.matches('input,select,textarea')) dirty = true;
        });
        form.addEventListener('change', event => {
            if (event.target.matches('input,select,textarea')) dirty = true;
        });
        form.addEventListener('submit', () => { dirty = false; });
        window.addEventListener('beforeunload', event => {
            if (!dirty) return;
            event.preventDefault();
            event.returnValue = '';
        });
    });
}

function cfInitTableSort() {
    document.querySelectorAll('table[data-sortable] thead th').forEach((header, columnIndex) => {
        if (header.dataset.noSort !== undefined || header.dataset.cfSortBound) return;
        const table = header.closest('table');
        if (!table || !table.tBodies.length) return;
        header.dataset.cfSortBound = 'true';
        header.classList.add('cf-sortable');
        header.tabIndex = 0;
        header.setAttribute('role', 'button');
        header.setAttribute('aria-label', `Sort by ${header.textContent.trim()}`);
        const sort = () => {
            const tbody = table.tBodies[0];
            const rows = Array.from(tbody.rows).filter(row => !row.hasAttribute('data-search-empty'));
            const current = header.dataset.sortDirection === 'asc' ? 'desc' : 'asc';
            table.querySelectorAll('thead th').forEach(cell => { delete cell.dataset.sortDirection; cell.removeAttribute('aria-sort'); });
            header.dataset.sortDirection = current;
            header.setAttribute('aria-sort', current === 'asc' ? 'ascending' : 'descending');
            const value = row => (row.cells[columnIndex]?.dataset.sortValue || row.cells[columnIndex]?.textContent || '').trim();
            rows.sort((a, b) => {
                const av = value(a), bv = value(b);
                const an = Number(av.replace(/[^0-9.-]/g, '')), bn = Number(bv.replace(/[^0-9.-]/g, ''));
                const bothNumeric = av !== '' && bv !== '' && Number.isFinite(an) && Number.isFinite(bn) && /[0-9]/.test(av) && /[0-9]/.test(bv);
                const result = bothNumeric ? an - bn : av.localeCompare(bv, undefined, { numeric: true, sensitivity: 'base' });
                return current === 'asc' ? result : -result;
            });
            rows.forEach(row => tbody.appendChild(row));
        };
        header.addEventListener('click', sort);
        header.addEventListener('keydown', event => { if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); sort(); } });
    });
}

function cfInitNavActiveState() {
    const current = window.location.pathname.replace(/\/$/, '') || '/';
    document.querySelectorAll('.app-sidebar .nav-item a[href]').forEach(link => {
        try {
            const url = new URL(link.href, window.location.origin);
            const path = url.pathname.replace(/\/$/, '') || '/';
            if (path === current) {
                const item = link.closest('.nav-item');
                if (item) item.classList.add('active');
                link.setAttribute('aria-current', 'page');
            }
        } catch (_) { /* Ignore malformed third-party links. */ }
    });
}

function cfAnnounce(control, message) {
    let status = control.parentElement.querySelector('[data-cf-announcement]');
    if (!status) {
        status = document.createElement('span');
        status.dataset.cfAnnouncement = '';
        status.className = 'cf-inline-announcement';
        status.setAttribute('role', 'status');
        control.insertAdjacentElement('afterend', status);
    }
    status.textContent = message;
}


/**
 * Filter dashboard shortcuts without navigating away. This is presentation-only;
 * the target endpoints remain protected by Spring Security.
 */
function initDashboardShortcutFilter() {
    document.querySelectorAll('[data-cf-shortcut-filter]').forEach(input => {
        const panel = input.closest('.cf-quick-panel');
        const grid = panel && panel.querySelector('.cf-quick-grid');
        if (!grid || input.dataset.cfBound === 'true') return;
        input.dataset.cfBound = 'true';
        const cards = Array.from(grid.querySelectorAll('.cf-quick-action'));
        const count = panel.querySelector('.cf-shortcut-count');
        let empty = grid.querySelector('.cf-no-shortcuts');
        if (!empty) {
            empty = document.createElement('div');
            empty.className = 'cf-no-shortcuts';
            empty.textContent = 'No shortcuts match that search. Try another term.';
            empty.hidden = true;
            grid.appendChild(empty);
        }
        const update = () => {
            const q = input.value.trim().toLocaleLowerCase();
            let visible = 0;
            cards.forEach(card => {
                const match = card.textContent.toLocaleLowerCase().includes(q);
                card.hidden = !match;
                if (match) visible += 1;
            });
            empty.hidden = visible > 0;
            if (count) count.textContent = q ? `${visible} of ${cards.length} shortcuts` : `${cards.length} shortcuts`;
        };
        input.addEventListener('input', update);
        update();
    });
}
