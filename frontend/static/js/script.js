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
        input.addEventListener('keyup', function () {
            const tableSelector = this.getAttribute('data-table-search');
            const table = document.querySelector(tableSelector);
            if (!table) return;

            const filterValue = this.value.toLowerCase().trim();
            const rows = table.querySelectorAll('tbody tr');

            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                if (text.includes(filterValue)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    });
}

/**
 * Mobile sidebar toggle
 */
function initMobileNav() {
    const toggleBtn = document.getElementById('mobileNavToggle');
    const sidebar = document.querySelector('.app-sidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('mobile-open');
        });
    }
}

/**
 * Demo Login Helper
 */
function fillDemoCredentials(role) {
    const userField = document.getElementById('username');
    const passField = document.getElementById('password');

    if (!userField || !passField) return;

    if (role === 'receptionist') {
        userField.value = 'receptionist@hospital.com';
        passField.value = 'Password@123';
    } else if (role === 'patient') {
        userField.value = 'patient@hospital.com';
        passField.value = 'Password@123';
    } else if (role === 'doctor') {
        userField.value = 'dr.vance@hospital.com';
        passField.value = 'Password@123';
    }

    // Visual highlight feedback
    userField.style.borderColor = '#0d9488';
    passField.style.borderColor = '#0d9488';
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
