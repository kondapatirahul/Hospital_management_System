package com.healthcare.hospital.service;

import com.healthcare.hospital.dto.*;
import com.healthcare.hospital.entity.*;
import com.healthcare.hospital.enums.BatchStatus;
import com.healthcare.hospital.enums.PurchaseOrderStatus;
import com.healthcare.hospital.enums.StockTransactionType;
import com.healthcare.hospital.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyPurchaseService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final GoodsReceivedNoteRepository grnRepository;
    private final GoodsReceivedItemRepository grnItemRepository;
    private final SupplierRepository supplierRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository batchRepository;
    private final StockTransactionRepository transactionRepository;
    private final PharmacyAuditService auditService;

    public PharmacyPurchaseService(PurchaseOrderRepository purchaseOrderRepository,
                                   PurchaseOrderItemRepository purchaseOrderItemRepository,
                                   GoodsReceivedNoteRepository grnRepository,
                                   GoodsReceivedItemRepository grnItemRepository,
                                   SupplierRepository supplierRepository,
                                   MedicineRepository medicineRepository,
                                   MedicineBatchRepository batchRepository,
                                   StockTransactionRepository transactionRepository,
                                   PharmacyAuditService auditService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
        this.grnRepository = grnRepository;
        this.grnItemRepository = grnItemRepository;
        this.supplierRepository = supplierRepository;
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    public Page<PurchaseOrder> searchPurchaseOrders(String query, PurchaseOrderStatus status, Long supplierId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return purchaseOrderRepository.searchPurchaseOrders(query, status, supplierId, pageable);
    }

    public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    public List<GoodsReceivedNote> getGrnsForPurchaseOrder(PurchaseOrder po) {
        return grnRepository.findByPurchaseOrder(po);
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrderDto dto, String username) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getSupplierId()));

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Purchase order must contain at least one item.");
        }

        String poNumber = dto.getPoNumber();
        if (poNumber == null || poNumber.trim().isEmpty()) {
            poNumber = generatePoNumber();
        } else {
            poNumber = poNumber.trim().toUpperCase();
            if (purchaseOrderRepository.findByPoNumberIgnoreCase(poNumber).isPresent()) {
                throw new IllegalArgumentException("PO number already exists: " + poNumber);
            }
        }

        PurchaseOrder po = new PurchaseOrder(
                poNumber,
                supplier,
                dto.getOrderDate() != null ? dto.getOrderDate() : LocalDate.now(),
                dto.getExpectedDeliveryDate(),
                dto.getRemarks(),
                username
        );

        for (PurchaseOrderItemDto itemDto : dto.getItems()) {
            Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + itemDto.getMedicineId()));
            PurchaseOrderItem item = new PurchaseOrderItem(
                    po,
                    medicine,
                    itemDto.getOrderedQuantity(),
                    itemDto.getUnitCost() != null ? itemDto.getUnitCost() : medicine.getPurchasePrice()
            );
            po.addItem(item);
        }

        PurchaseOrder savedPo = purchaseOrderRepository.save(po);

        auditService.logEvent(
                "PURCHASE_ORDER_CREATED",
                "PurchaseOrder",
                savedPo.getId() != null ? savedPo.getId().toString() : savedPo.getPoNumber(),
                username,
                String.format("Created PO %s for supplier %s with %d items. Total: $%s",
                        savedPo.getPoNumber(), supplier.getName(), savedPo.getItems().size(), savedPo.getTotalAmount())
        );

        return savedPo;
    }

    @Transactional
    public GoodsReceivedNote processGoodsReceipt(GoodsReceivedDto dto, String username) {
        PurchaseOrder po = purchaseOrderRepository.findById(dto.getPurchaseOrderId())
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + dto.getPurchaseOrderId()));

        if (po.getStatus() == PurchaseOrderStatus.RECEIVED || po.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot receive items for a PO with status: " + po.getStatus());
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Receipt must contain at least one item.");
        }

        String grnNumber = generateGrnNumber();
        GoodsReceivedNote grn = new GoodsReceivedNote(
                grnNumber,
                po,
                po.getSupplier(),
                dto.getReceivedDate() != null ? dto.getReceivedDate() : LocalDate.now(),
                dto.getSupplierInvoiceNumber(),
                dto.getSupplierInvoiceDate(),
                dto.getRemarks(),
                username
        );

        boolean anyPendingRemaining = false;

        for (GoodsReceivedItemDto itemDto : dto.getItems()) {
            if (itemDto.getQuantityReceived() == null || itemDto.getQuantityReceived() <= 0) {
                continue; // skip zero received items
            }

            Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + itemDto.getMedicineId()));

            PurchaseOrderItem poItem = null;
            if (itemDto.getPurchaseOrderItemId() != null) {
                poItem = purchaseOrderItemRepository.findById(itemDto.getPurchaseOrderItemId()).orElse(null);
            }

            if (poItem != null) {
                int newReceived = poItem.getReceivedQuantity() + itemDto.getQuantityReceived();
                if (newReceived > poItem.getOrderedQuantity()) {
                    throw new IllegalArgumentException(String.format(
                            "Received quantity (%d) cannot exceed ordered quantity (%d) for medicine %s",
                            newReceived, poItem.getOrderedQuantity(), medicine.getName()
                    ));
                }
                poItem.setReceivedQuantity(newReceived);
                purchaseOrderItemRepository.save(poItem);
                if (!poItem.isFullyReceived()) {
                    anyPendingRemaining = true;
                }
            }

            // Expiry date verification
            if (itemDto.getExpiryDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Cannot receive expired batch: " + itemDto.getExpiryDate());
            }

            // Create or update batch
            Optional<MedicineBatch> existingBatchOpt = batchRepository.findByBatchNumberAndMedicine(itemDto.getBatchNumber().trim(), medicine);
            MedicineBatch batch;
            int previousStock = medicine.getTotalStock();

            if (existingBatchOpt.isPresent()) {
                batch = existingBatchOpt.get();
                batch.setCurrentQuantity(batch.getCurrentQuantity() + itemDto.getQuantityReceived());
                batch.setInitialQuantity(batch.getInitialQuantity() + itemDto.getQuantityReceived());
                batch.setPurchasePrice(itemDto.getUnitCost());
                batch.setStatus(batch.calculateStatus(LocalDate.now()));
            } else {
                batch = new MedicineBatch(
                        itemDto.getBatchNumber().trim(),
                        medicine,
                        po.getSupplier(),
                        itemDto.getQuantityReceived(),
                        itemDto.getUnitCost(),
                        medicine.getUnitPrice(),
                        grn.getReceivedDate(),
                        itemDto.getExpiryDate()
                );
            }
            MedicineBatch savedBatch = batchRepository.save(batch);

            // Create GRN item
            GoodsReceivedItem grnItem = new GoodsReceivedItem(
                    grn,
                    poItem,
                    medicine,
                    savedBatch,
                    itemDto.getBatchNumber().trim(),
                    itemDto.getExpiryDate(),
                    itemDto.getQuantityReceived(),
                    itemDto.getUnitCost()
            );
            grn.addItem(grnItem);

            // Update medicine total valid stock
            int newValidStock = batchRepository.calculateValidStockForMedicine(medicine, LocalDate.now());
            medicine.setTotalStock(Math.max(0, newValidStock));
            medicineRepository.save(medicine);

            // Record stock movement transaction
            String txNumber = "STX-" + System.currentTimeMillis() % 10000000;
            StockTransaction tx = new StockTransaction(
                    txNumber,
                    StockTransactionType.STOCK_IN,
                    medicine,
                    savedBatch,
                    itemDto.getQuantityReceived(),
                    itemDto.getUnitCost(),
                    previousStock,
                    medicine.getTotalStock(),
                    "GOODS_RECEIVED",
                    grnNumber,
                    username,
                    "PO: " + po.getPoNumber() + " | GRN: " + grnNumber
            );
            transactionRepository.save(tx);
        }

        GoodsReceivedNote savedGrn = grnRepository.save(grn);

        // Update PO Status
        // Check if all items in PO are fully received
        List<PurchaseOrderItem> poItems = purchaseOrderItemRepository.findByPurchaseOrder(po);
        boolean allComplete = poItems.stream().allMatch(PurchaseOrderItem::isFullyReceived);
        po.setStatus(allComplete ? PurchaseOrderStatus.RECEIVED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
        purchaseOrderRepository.save(po);

        auditService.logEvent(
                "GOODS_RECEIVED",
                "GoodsReceivedNote",
                savedGrn.getId() != null ? savedGrn.getId().toString() : savedGrn.getGrnNumber(),
                username,
                String.format("Processed GRN %s for PO %s. Total items received: %d. PO status now %s.",
                        savedGrn.getGrnNumber(), po.getPoNumber(), savedGrn.getItems().size(), po.getStatus())
        );

        return savedGrn;
    }

    public synchronized String generatePoNumber() {
        return "PO-" + LocalDate.now().getYear() + "-" + String.format("%04d", purchaseOrderRepository.count() + 1);
    }

    public synchronized String generateGrnNumber() {
        return "GRN-" + LocalDate.now().getYear() + "-" + String.format("%04d", grnRepository.count() + 1);
    }
}
