package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.GoodsReceivedDto;
import com.healthcare.hospital.dto.GoodsReceivedItemDto;
import com.healthcare.hospital.dto.PurchaseOrderDto;
import com.healthcare.hospital.dto.PurchaseOrderItemDto;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.PurchaseOrderStatus;
import com.healthcare.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PharmacyPurchaseServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Mock
    private GoodsReceivedNoteRepository grnRepository;

    @Mock
    private GoodsReceivedItemRepository grnItemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private MedicineBatchRepository batchRepository;

    @Mock
    private StockTransactionRepository transactionRepository;

    @Mock
    private PharmacyAuditService auditService;

    @InjectMocks
    private PharmacyPurchaseService purchaseService;

    private Supplier supplier;
    private Medicine medicine;
    private PurchaseOrder po;
    private PurchaseOrderItem poItem;

    @BeforeEach
    void setUp() {
        supplier = new Supplier("SUP-001", "Apex Pharma", "Contact", "email@apex.com", "+1 555-1234", "Chicago", "LIC-01", "Net 30");
        supplier.setId(1L);

        medicine = new Medicine("MED-001", "Amoxicillin", "Generic", null, "Tablet", "500mg", "Strip", 20, BigDecimal.TEN, BigDecimal.valueOf(6.0), "Mfr");
        medicine.setId(10L);
        medicine.setTotalStock(0);

        po = new PurchaseOrder("PO-2026-0001", supplier, LocalDate.now(), LocalDate.now().plusDays(10), "Restock order", "pharmacist");
        po.setId(100L);

        poItem = new PurchaseOrderItem(po, medicine, 50, BigDecimal.valueOf(6.0));
        poItem.setId(200L);
        po.addItem(poItem);
    }

    @Test
    void testCreatePurchaseOrder_Success() {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setPoNumber("PO-CUSTOM-001");
        dto.setSupplierId(1L);
        dto.setOrderDate(LocalDate.now());
        dto.setRemarks("Urgent restock");
        dto.setItems(List.of(new PurchaseOrderItemDto(10L, 50, BigDecimal.valueOf(6.0))));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(purchaseOrderRepository.findByPoNumberIgnoreCase("PO-CUSTOM-001")).thenReturn(Optional.empty());
        when(medicineRepository.findById(10L)).thenReturn(Optional.of(medicine));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder p = invocation.getArgument(0);
            p.setId(105L);
            return p;
        });

        PurchaseOrder created = purchaseService.createPurchaseOrder(dto, "pharmacist");

        assertNotNull(created);
        assertEquals(supplier, created.getSupplier());
        assertEquals(BigDecimal.valueOf(300.0), created.getTotalAmount());
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(auditService, times(1)).logEvent(eq("PURCHASE_ORDER_CREATED"), eq("PurchaseOrder"), anyString(), eq("pharmacist"), anyString());
    }

    @Test
    void testProcessGoodsReceipt_FullDelivery_Success() {
        GoodsReceivedDto grnDto = new GoodsReceivedDto();
        grnDto.setPurchaseOrderId(100L);
        grnDto.setReceivedDate(LocalDate.now());
        grnDto.setSupplierInvoiceNumber("INV-9988");

        GoodsReceivedItemDto itemDto = new GoodsReceivedItemDto();
        itemDto.setPurchaseOrderItemId(200L);
        itemDto.setMedicineId(10L);
        itemDto.setBatchNumber("BAT-AUG-2026");
        itemDto.setExpiryDate(LocalDate.now().plusMonths(24));
        itemDto.setQuantityReceived(50);
        itemDto.setUnitCost(BigDecimal.valueOf(6.0));
        grnDto.setItems(List.of(itemDto));

        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(po));
        when(medicineRepository.findById(10L)).thenReturn(Optional.of(medicine));
        when(purchaseOrderItemRepository.findById(200L)).thenReturn(Optional.of(poItem));
        when(batchRepository.findByBatchNumberAndMedicine("BAT-AUG-2026", medicine)).thenReturn(Optional.empty());
        when(batchRepository.save(any(MedicineBatch.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(grnRepository.count()).thenReturn(1L);
        when(grnRepository.save(any(GoodsReceivedNote.class))).thenAnswer(invocation -> {
            GoodsReceivedNote g = invocation.getArgument(0);
            g.setId(888L);
            return g;
        });
        when(purchaseOrderItemRepository.findByPurchaseOrder(po)).thenReturn(List.of(poItem));
        when(batchRepository.calculateValidStockForMedicine(eq(medicine), any(LocalDate.class))).thenReturn(50);

        GoodsReceivedNote grn = purchaseService.processGoodsReceipt(grnDto, "pharmacist");

        assertNotNull(grn);
        assertEquals(50, poItem.getReceivedQuantity());
        assertTrue(poItem.isFullyReceived());
        assertEquals(PurchaseOrderStatus.RECEIVED, po.getStatus());

        verify(batchRepository, times(1)).save(any(MedicineBatch.class));
        verify(transactionRepository, times(1)).save(any(StockTransaction.class));
        verify(medicineRepository, times(1)).save(medicine);
    }

    @Test
    void testProcessGoodsReceipt_ExceedsOrderedQuantity_ThrowsException() {
        GoodsReceivedDto grnDto = new GoodsReceivedDto();
        grnDto.setPurchaseOrderId(100L);
        grnDto.setReceivedDate(LocalDate.now());

        GoodsReceivedItemDto itemDto = new GoodsReceivedItemDto();
        itemDto.setPurchaseOrderItemId(200L);
        itemDto.setMedicineId(10L);
        itemDto.setBatchNumber("BAT-TEST");
        itemDto.setExpiryDate(LocalDate.now().plusMonths(12));
        itemDto.setQuantityReceived(60); // Ordered is 50!
        itemDto.setUnitCost(BigDecimal.valueOf(6.0));
        grnDto.setItems(List.of(itemDto));

        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(po));
        when(medicineRepository.findById(10L)).thenReturn(Optional.of(medicine));
        when(purchaseOrderItemRepository.findById(200L)).thenReturn(Optional.of(poItem));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                purchaseService.processGoodsReceipt(grnDto, "pharmacist")
        );

        assertTrue(ex.getMessage().contains("cannot exceed ordered quantity"));
        verify(grnRepository, never()).save(any(GoodsReceivedNote.class));
    }
}
