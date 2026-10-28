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
    storage_conditions VARCHAR(150) DEFAULT 'Store below 25°C in a dry place',
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
