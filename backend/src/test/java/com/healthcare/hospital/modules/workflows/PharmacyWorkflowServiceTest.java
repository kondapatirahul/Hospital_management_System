package com.healthcare.hospital.modules.workflows;
import static org.junit.jupiter.api.Assertions.*;
import com.healthcare.hospital.modules.legacy.entity.MedicineBatch;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
class PharmacyWorkflowServiceTest {
    private final PharmacyWorkflowService service=new PharmacyWorkflowService();
    private MedicineBatch batch(String number,int qty,LocalDate expiry) {
        MedicineBatch b=new MedicineBatch(); b.setBatchNumber(number); b.setInitialQuantity(100); b.setCurrentQuantity(qty);
        b.setExpiryDate(expiry); b.setPurchasePrice(new BigDecimal("2.00")); b.setUnitPrice(new BigDecimal("3.00")); return b;
    }
    @Test void detectsExpiredBatches() { assertEquals(1,service.expired(List.of(batch("B1",4,LocalDate.now().minusDays(1))),LocalDate.now()).size()); }
    @Test void detectsNearExpiryBatches() { assertEquals(1,service.expiringWithin(List.of(batch("B1",4,LocalDate.now().plusDays(10))),LocalDate.now(),30).size()); }
    @Test void excludesZeroStockFromNearExpiry() { assertTrue(service.expiringWithin(List.of(batch("B1",0,LocalDate.now().plusDays(10))),LocalDate.now(),30).isEmpty()); }
    @Test void lowStockThresholdIsInclusive() { assertEquals(1,service.lowStock(List.of(batch("B1",10,LocalDate.now().plusDays(90))),10).size()); }
    @Test void rejectsNegativeLowStockThreshold() { assertThrows(IllegalArgumentException.class,()->service.lowStock(List.of(),-1)); }
    @Test void validatesBatchQuantityAgainstInitialQuantity() { assertTrue(service.validateBatch(batch("B1",101,LocalDate.now().plusDays(90))).contains("Current quantity exceeds received quantity")); }
    @Test void calculatesInventoryValue() { assertEquals(new BigDecimal("30.00"),service.inventoryValue(List.of(batch("B1",10,LocalDate.now().plusDays(90))))); }
    @Test void calculatesTotalUnits() { assertEquals(15,service.totalUnits(List.of(batch("B1",10,LocalDate.now()),batch("B2",5,LocalDate.now())))); }
    @Test void findsPendingPrescriptionsByRemainingQuantity() {
        var p=new com.healthcare.hospital.modules.legacy.entity.Prescription();
        p.setPrescribedQuantity(10); p.setDispensedQuantity(2); p.setStatus("Active");
        assertEquals(1,service.pendingDispensing(List.of(p)).size());
    }
}
