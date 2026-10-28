package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.DispensingSummaryDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PharmacyReportService {

    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository batchRepository;
    private final StockTransactionRepository transactionRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PrescriptionDispensationRepository dispensationRepository;
    private final DispensationItemRepository dispensationItemRepository;

    public PharmacyReportService(MedicineRepository medicineRepository,
                                 MedicineBatchRepository batchRepository,
                                 StockTransactionRepository transactionRepository,
                                 PurchaseOrderRepository purchaseOrderRepository,
                                 PrescriptionDispensationRepository dispensationRepository,
                                 DispensationItemRepository dispensationItemRepository) {
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.transactionRepository = transactionRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.dispensationRepository = dispensationRepository;
        this.dispensationItemRepository = dispensationItemRepository;
    }

    public List<Medicine> getCurrentInventory() {
        return medicineRepository.findByActiveTrueOrderByNameAsc();
    }

    public List<Medicine> getLowStockReport() {
        return medicineRepository.findLowStockMedicines();
    }

    public List<Medicine> getOutOfStockReport() {
        return medicineRepository.findOutOfStockMedicines();
    }

    public List<MedicineBatch> getExpiredBatchesReport() {
        return batchRepository.findExpiredBatches(LocalDate.now());
    }

    public List<MedicineBatch> getNearExpiryBatchesReport(int days) {
        LocalDate today = LocalDate.now();
        return batchRepository.findNearExpiryBatches(today, today.plusDays(days));
    }

    public List<StockTransaction> getStockMovementReport(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findMovementReport(start, end);
    }

    public List<PurchaseOrder> getPurchasesReport(LocalDate start, LocalDate end, Long supplierId) {
        return purchaseOrderRepository.findPurchasesReport(start, end, supplierId);
    }

    public List<PrescriptionDispensation> getDispensationsReport(LocalDateTime start, LocalDateTime end) {
        return dispensationRepository.findDispensationsReport(start, end);
    }

    public List<DispensingSummaryDto> getMedicineDispensingSummaries(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rawSummaries = dispensationItemRepository.getMedicineDispensingSummaries(start, end);
        List<DispensingSummaryDto> list = new ArrayList<>();
        for (Object[] row : rawSummaries) {
            String code = (String) row[0];
            String name = (String) row[1];
            String form = (String) row[2];
            Long totalQty = row[3] instanceof Number ? ((Number) row[3]).longValue() : 0L;
            BigDecimal revenue = row[4] instanceof BigDecimal ? (BigDecimal) row[4] :
                    (row[4] != null ? BigDecimal.valueOf(((Number) row[4]).doubleValue()) : BigDecimal.ZERO);
            list.add(new DispensingSummaryDto(code, name, form, totalQty, revenue));
        }
        return list;
    }

    public String exportInventoryCsv() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("Medicine Code,Name,Generic Name,Category,Dosage Form,Strength,Reorder Level,Current Stock,Unit Price,Purchase Price,Status");
        for (Medicine m : getCurrentInventory()) {
            pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%.2f,%.2f,\"%s\"%n",
                    escape(m.getMedicineCode()),
                    escape(m.getName()),
                    escape(m.getGenericName() != null ? m.getGenericName() : ""),
                    escape(m.getCategory().getName()),
                    escape(m.getDosageForm()),
                    escape(m.getStrength()),
                    m.getReorderLevel(),
                    m.getTotalStock(),
                    m.getUnitPrice(),
                    m.getPurchasePrice(),
                    m.isOutOfStock() ? "OUT_OF_STOCK" : (m.isLowStock() ? "LOW_STOCK" : "IN_STOCK")
            );
        }
        pw.flush();
        return sw.toString();
    }

    public String exportStockMovementCsv(LocalDateTime start, LocalDateTime end) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("Transaction No,Date & Time,Type,Medicine Code,Medicine Name,Batch No,Quantity,Unit Price,Previous Stock,New Stock,Reference,Performed By");
        for (StockTransaction t : getStockMovementReport(start, end)) {
            pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%.2f,%d,%d,\"%s\",\"%s\"%n",
                    escape(t.getTransactionNumber()),
                    t.getTransactionDate(),
                    t.getTransactionType(),
                    escape(t.getMedicine().getMedicineCode()),
                    escape(t.getMedicine().getName()),
                    t.getBatch() != null ? escape(t.getBatch().getBatchNumber()) : "N/A",
                    t.getQuantity(),
                    t.getUnitPrice(),
                    t.getPreviousStock(),
                    t.getNewStock(),
                    escape(t.getReferenceType() + ": " + (t.getReferenceId() != null ? t.getReferenceId() : "")),
                    escape(t.getPerformedBy() != null ? t.getPerformedBy() : "System")
            );
        }
        pw.flush();
        return sw.toString();
    }

    public String exportDispensationsCsv(LocalDateTime start, LocalDateTime end) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("Dispensation No,Date & Time,Patient ID,Patient Name,Doctor,Prescription ID,Medication Prescribed,Total Items,Total Value,Dispensed By");
        for (PrescriptionDispensation d : getDispensationsReport(start, end)) {
            pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",%d,%.2f,\"%s\"%n",
                    escape(d.getDispensationNumber()),
                    d.getDispensationDate(),
                    escape(d.getPatient().getPatientId()),
                    escape(d.getPatient().getFullName()),
                    escape(d.getDoctor().getFullName()),
                    d.getPrescription().getId(),
                    escape(d.getPrescription().getMedicationName()),
                    d.getTotalQuantity(),
                    d.getTotalAmount(),
                    escape(d.getDispensedBy())
            );
        }
        pw.flush();
        return sw.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }
}
