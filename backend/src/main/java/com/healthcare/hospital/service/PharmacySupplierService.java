package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.SupplierDto;
import com.healthcare.hospital.entity.Supplier;
import com.healthcare.hospital.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PharmacySupplierService {

    private final SupplierRepository supplierRepository;
    private final PharmacyAuditService auditService;

    public PharmacySupplierService(SupplierRepository supplierRepository, PharmacyAuditService auditService) {
        this.supplierRepository = supplierRepository;
        this.auditService = auditService;
    }

    public List<Supplier> getAllActiveSuppliers() {
        return supplierRepository.findByActiveTrueOrderByNameAsc();
    }

    public Page<Supplier> searchSuppliers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return supplierRepository.searchSuppliers(query, pageable);
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public Supplier saveSupplier(SupplierDto dto, String username) {
        Supplier supplier;
        boolean isNew = dto.getId() == null;

        if (isNew) {
            String code = dto.getSupplierCode();
            if (code == null || code.trim().isEmpty()) {
                code = generateSupplierCode();
            } else {
                code = code.trim().toUpperCase();
                if (supplierRepository.existsBySupplierCodeIgnoreCase(code)) {
                    throw new IllegalArgumentException("Supplier code already exists: " + code);
                }
            }
            supplier = new Supplier(
                    code,
                    dto.getName().trim(),
                    dto.getContactPerson(),
                    dto.getEmail(),
                    dto.getPhone().trim(),
                    dto.getAddress(),
                    dto.getLicenseNumber(),
                    dto.getPaymentTerms()
            );
        } else {
            supplier = supplierRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getId()));
            supplier.setName(dto.getName().trim());
            supplier.setContactPerson(dto.getContactPerson());
            supplier.setEmail(dto.getEmail());
            supplier.setPhone(dto.getPhone().trim());
            supplier.setAddress(dto.getAddress());
            supplier.setLicenseNumber(dto.getLicenseNumber());
            supplier.setPaymentTerms(dto.getPaymentTerms());
        }

        supplier.setActive(dto.isActive());
        supplier.setNotes(dto.getNotes());

        Supplier saved = supplierRepository.save(supplier);

        auditService.logEvent(
                isNew ? "SUPPLIER_CREATED" : "SUPPLIER_UPDATED",
                "Supplier",
                saved.getId().toString(),
                username,
                "Supplier " + saved.getName() + " (" + saved.getSupplierCode() + ") saved."
        );

        return saved;
    }

    public synchronized String generateSupplierCode() {
        long count = supplierRepository.count() + 1;
        String code = String.format("SUP-%03d", count);
        while (supplierRepository.existsBySupplierCodeIgnoreCase(code)) {
            count++;
            code = String.format("SUP-%03d", count);
        }
        return code;
    }
}
