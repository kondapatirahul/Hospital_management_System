-- ========================================================
-- CareFlow Healthcare Management System - MySQL Schema & Demo Data
-- Module M1: Patient Dashboard & Receptionist Dashboard
-- Module M2: Doctor Dashboard
-- ========================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon VARCHAR(50)
);

-- 3. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    qualification VARCHAR(150) NOT NULL,
    department_id BIGINT NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(30),
    room_number VARCHAR(50),
    consultation_fee DECIMAL(10, 2) DEFAULT 50.00,
    working_days VARCHAR(100) DEFAULT 'Mon, Tue, Wed, Thu, Fri',
    working_hours VARCHAR(50) DEFAULT '09:00 AM - 05:00 PM',
    available_today BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);

-- 4. Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(30) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    blood_group VARCHAR(10),
    address VARCHAR(255),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(30),
    allergies VARCHAR(255),
    chronic_conditions VARCHAR(255),
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME,
    appointment_slot VARCHAR(30),
    reason_for_visit VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    cancellation_reason VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);

-- 6. Waiting Queue Table
CREATE TABLE IF NOT EXISTS waiting_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    queue_number VARCHAR(20) NOT NULL,
    appointment_id BIGINT NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    queue_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'WAITING',
    check_in_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    called_time DATETIME,
    completed_time DATETIME,
    notes VARCHAR(255),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- 7. Medical Records Table
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT,
    visit_date DATE NOT NULL,
    symptoms VARCHAR(255),
    diagnosis VARCHAR(255) NOT NULL,
    clinical_notes TEXT,
    vital_signs VARCHAR(150),
    follow_up_instructions VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- 8. Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_id BIGINT,
    medical_record_id BIGINT,
    prescription_date DATE NOT NULL,
    medication_name VARCHAR(150) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100),
    instructions VARCHAR(255),
    status VARCHAR(30) DEFAULT 'Active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE SET NULL
);

-- 9. Lab Reports Table
CREATE TABLE IF NOT EXISTS lab_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT,
    test_name VARCHAR(150) NOT NULL,
    test_category VARCHAR(100),
    requested_date DATE NOT NULL,
    completed_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    summary_result TEXT,
    reference_range VARCHAR(255),
    doctor_notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL
);

-- 10. Invoices Table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE,
    service_description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax DECIMAL(10, 2) DEFAULT 0.00,
    discount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- 11. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(50) DEFAULT 'APPOINTMENT_REMINDER',
    read_status BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 12. Doctor Availabilities Table
CREATE TABLE IF NOT EXISTS doctor_availabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    available_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    slot_notes VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);
-- ========================================================
-- CareFlow Healthcare Management System - Module M3 Pharmacy Schema
-- Tables, Foreign Keys, Indexes, and Constraints
-- ========================================================

USE hospital_db;

-- 1. Alter Existing Prescriptions Table to Support Dispensing Integration
ALTER TABLE prescriptions 
    ADD COLUMN IF NOT EXISTS prescribed_quantity INT DEFAULT 10,
    ADD COLUMN IF NOT EXISTS dispensed_quantity INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS dispense_status VARCHAR(30) DEFAULT 'PENDING';

-- 2. Medicine Categories Table
CREATE TABLE IF NOT EXISTS medicine_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(30) NOT NULL,
    address VARCHAR(255),
    license_number VARCHAR(100),
    payment_terms VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. Medicines Table
CREATE TABLE IF NOT EXISTS medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    generic_name VARCHAR(150),
    category_id BIGINT NOT NULL,
    dosage_form VARCHAR(50) NOT NULL,
    strength VARCHAR(50) NOT NULL,
    unit_of_measure VARCHAR(50) NOT NULL,
    reorder_level INT NOT NULL DEFAULT 20,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    purchase_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    manufacturer VARCHAR(150),
    storage_conditions VARCHAR(150) DEFAULT 'Store below 25Â°C in a dry place',
    prescription_required BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    total_stock INT NOT NULL DEFAULT 0,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES medicine_categories(id) ON DELETE RESTRICT
);

-- 5. Medicine Batches Table (FEFO Tracking)
CREATE TABLE IF NOT EXISTS medicine_batches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_number VARCHAR(100) NOT NULL,
    medicine_id BIGINT NOT NULL,
    supplier_id BIGINT,
    initial_quantity INT NOT NULL DEFAULT 0,
    current_quantity INT NOT NULL DEFAULT 0,
    purchase_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    received_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    notes VARCHAR(255),
    version BIGINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL,
    CONSTRAINT chk_batch_non_negative_stock CHECK (current_quantity >= 0)
);

-- 6. Stock Movement Transactions Table
CREATE TABLE IF NOT EXISTS stock_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_number VARCHAR(50) NOT NULL UNIQUE,
    transaction_type VARCHAR(30) NOT NULL,
    medicine_id BIGINT NOT NULL,
    batch_id BIGINT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) DEFAULT 0.00,
    total_value DECIMAL(10, 2) DEFAULT 0.00,
    previous_stock INT NOT NULL,
    new_stock INT NOT NULL,
    reference_type VARCHAR(50),
    reference_id VARCHAR(100),
    performed_by VARCHAR(100),
    notes VARCHAR(255),
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT,
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(id) ON DELETE SET NULL
);

-- 7. Stock Adjustments Table
CREATE TABLE IF NOT EXISTS stock_adjustments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    adjustment_number VARCHAR(50) NOT NULL UNIQUE,
    medicine_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    adjustment_type VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(50) NOT NULL,
    remarks VARCHAR(255),
    adjusted_by VARCHAR(100) NOT NULL,
    adjustment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT,
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(id) ON DELETE RESTRICT
);

-- 8. Purchase Orders Table
CREATE TABLE IF NOT EXISTS purchase_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_number VARCHAR(50) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    expected_delivery_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    remarks TEXT,
    created_by VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT
);

-- 9. Purchase Order Items Table
CREATE TABLE IF NOT EXISTS purchase_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    ordered_quantity INT NOT NULL,
    received_quantity INT NOT NULL DEFAULT 0,
    unit_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_cost DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT
);

-- 10. Goods Received Notes Table
CREATE TABLE IF NOT EXISTS goods_received_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    grn_number VARCHAR(50) NOT NULL UNIQUE,
    purchase_order_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    received_date DATE NOT NULL,
    supplier_invoice_number VARCHAR(100),
    supplier_invoice_date DATE,
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    remarks TEXT,
    received_by VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE RESTRICT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT
);

-- 11. Goods Received Items Table
CREATE TABLE IF NOT EXISTS goods_received_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    grn_id BIGINT NOT NULL,
    purchase_order_item_id BIGINT,
    medicine_id BIGINT NOT NULL,
    batch_id BIGINT,
    batch_number VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    quantity_received INT NOT NULL,
    unit_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_cost DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (grn_id) REFERENCES goods_received_notes(id) ON DELETE CASCADE,
    FOREIGN KEY (purchase_order_item_id) REFERENCES purchase_order_items(id) ON DELETE SET NULL,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT,
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(id) ON DELETE SET NULL
);

-- 12. Prescription Dispensations Table
CREATE TABLE IF NOT EXISTS prescription_dispensations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dispensation_number VARCHAR(50) NOT NULL UNIQUE,
    prescription_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    dispensed_by VARCHAR(100) NOT NULL,
    dispensation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    total_quantity INT NOT NULL DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    remarks TEXT,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE RESTRICT,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT
);

-- 13. Dispensation Items Table
CREATE TABLE IF NOT EXISTS dispensation_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dispensation_id BIGINT NOT NULL,
    prescription_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    quantity_dispensed INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    directions VARCHAR(255),
    FOREIGN KEY (dispensation_id) REFERENCES prescription_dispensations(id) ON DELETE CASCADE,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE RESTRICT,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT,
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(id) ON DELETE RESTRICT
);

-- 14. Pharmacy Audit Logs Table
CREATE TABLE IF NOT EXISTS pharmacy_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(60) NOT NULL,
    entity_name VARCHAR(60) NOT NULL,
    entity_id VARCHAR(60),
    username VARCHAR(100) NOT NULL,
    action_details TEXT,
    ip_address VARCHAR(50),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Fast Search, FEFO Allocation, and Expiry Alert Scans
CREATE INDEX idx_medicines_name_code ON medicines(name, medicine_code);
CREATE INDEX idx_medicines_category ON medicines(category_id);
CREATE INDEX idx_batches_medicine_expiry ON medicine_batches(medicine_id, expiry_date, current_quantity, status);
CREATE INDEX idx_batches_expiry_status ON medicine_batches(expiry_date, status);
CREATE INDEX idx_transactions_medicine_date ON stock_transactions(medicine_id, transaction_date);
CREATE INDEX idx_transactions_type ON stock_transactions(transaction_type);
CREATE INDEX idx_prescriptions_dispense_status ON prescriptions(dispense_status);
CREATE INDEX idx_dispensations_date ON prescription_dispensations(dispensation_date);
CREATE INDEX idx_purchase_orders_supplier ON purchase_orders(supplier_id, order_date);


-- ========================================================
-- CareFlow Healthcare Management System - Module OT Schema
-- Operation Theatre, Surgery Scheduling & Clinical Workflows
-- ========================================================

-- 15. Operation Theatres Table
CREATE TABLE IF NOT EXISTS operation_theatres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theatre_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    location VARCHAR(150) NOT NULL,
    theatre_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    capacity INT NOT NULL DEFAULT 1,
    equipment_details TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- 16. Theatre Maintenance Logs Table
CREATE TABLE IF NOT EXISTS theatre_maintenance_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theatre_id BIGINT NOT NULL,
    maintenance_type VARCHAR(50) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    performed_by VARCHAR(100),
    details TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (theatre_id) REFERENCES operation_theatres(id) ON DELETE CASCADE
);

-- 17. Surgery Bookings Table
CREATE TABLE IF NOT EXISTS surgery_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_number VARCHAR(32) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    theatre_id BIGINT NOT NULL,
    lead_surgeon_id BIGINT NOT NULL,
    procedure_name VARCHAR(200) NOT NULL,
    procedure_category VARCHAR(80) DEFAULT 'General',
    planned_date DATE NOT NULL,
    planned_start_time TIME NOT NULL,
    expected_duration_minutes INT NOT NULL DEFAULT 120,
    priority VARCHAR(20) NOT NULL DEFAULT 'ROUTINE',
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    actual_start_time DATETIME,
    actual_end_time DATETIME,
    appointment_id BIGINT,
    ward_bed_id BIGINT,
    clinical_indication TEXT,
    preoperative_diagnosis TEXT,
    postoperative_diagnosis TEXT,
    operative_notes TEXT,
    anesthesia_notes TEXT,
    recovery_destination VARCHAR(150),
    cancellation_reason TEXT,
    postponed_reason TEXT,
    booked_by VARCHAR(100),
    invoice_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    FOREIGN KEY (theatre_id) REFERENCES operation_theatres(id) ON DELETE RESTRICT,
    FOREIGN KEY (lead_surgeon_id) REFERENCES doctors(id) ON DELETE RESTRICT,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    FOREIGN KEY (ward_bed_id) REFERENCES ward_beds(id) ON DELETE SET NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE SET NULL
);

-- 18. Surgical Team Members Table
CREATE TABLE IF NOT EXISTS surgical_team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    surgery_booking_id BIGINT NOT NULL,
    doctor_id BIGINT,
    user_id BIGINT,
    member_role VARCHAR(50) NOT NULL,
    member_name VARCHAR(120) NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(100),
    FOREIGN KEY (surgery_booking_id) REFERENCES surgery_bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 19. Surgery Checklist Items Table
CREATE TABLE IF NOT EXISTS surgery_checklist_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    surgery_booking_id BIGINT NOT NULL,
    phase VARCHAR(30) NOT NULL,
    item_code VARCHAR(60) NOT NULL,
    item_label VARCHAR(255) NOT NULL,
    is_mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    completed_by VARCHAR(100),
    completed_at DATETIME,
    remarks TEXT,
    FOREIGN KEY (surgery_booking_id) REFERENCES surgery_bookings(id) ON DELETE CASCADE
);

-- 20. Surgery Consumables Usage Table
CREATE TABLE IF NOT EXISTS surgery_consumable_usages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    surgery_booking_id BIGINT NOT NULL,
    medicine_id BIGINT,
    batch_id BIGINT,
    item_name VARCHAR(150) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    is_deducted BOOLEAN NOT NULL DEFAULT FALSE,
    recorded_by VARCHAR(100),
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (surgery_booking_id) REFERENCES surgery_bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE SET NULL,
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(id) ON DELETE SET NULL
);

-- 21. Surgery Audit Trails Table
CREATE TABLE IF NOT EXISTS surgery_audit_trails (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    surgery_booking_id BIGINT NOT NULL,
    transition_from VARCHAR(50),
    transition_to VARCHAR(50) NOT NULL,
    performed_by VARCHAR(100) NOT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    action_details TEXT,
    FOREIGN KEY (surgery_booking_id) REFERENCES surgery_bookings(id) ON DELETE CASCADE
);

-- Indexes for Fast Search and Conflict Detection
CREATE INDEX idx_ot_status ON operation_theatres(status);
CREATE INDEX idx_surg_theatre_date ON surgery_bookings(theatre_id, planned_date);
CREATE INDEX idx_surg_doctor_date ON surgery_bookings(lead_surgeon_id, planned_date);
CREATE INDEX idx_surg_status ON surgery_bookings(status);
CREATE INDEX idx_team_surgery ON surgical_team_members(surgery_booking_id);
CREATE INDEX idx_chk_surgery_phase ON surgery_checklist_items(surgery_booking_id, phase);
CREATE INDEX idx_surg_consumable_booking ON surgery_consumable_usages(surgery_booking_id);
