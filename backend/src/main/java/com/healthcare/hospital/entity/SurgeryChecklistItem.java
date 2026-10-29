package com.healthcare.hospital.entity;

import com.healthcare.hospital.enums.ChecklistPhase;
import com.healthcare.hospital.enums.ChecklistStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Preoperative, Intraoperative (WHO Sign-In, Time-Out, Sign-Out) and Postoperative checklist items.
 */
@Entity
@Table(name = "surgery_checklist_items", indexes = {
    @Index(name = "idx_chk_surgery_phase", columnList = "surgery_booking_id, phase"),
    @Index(name = "idx_chk_status", columnList = "status")
})
public class SurgeryChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgery_booking_id", nullable = false)
    private SurgeryBooking surgeryBooking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChecklistPhase phase = ChecklistPhase.PREOPERATIVE;

    @Column(name = "item_code", nullable = false, length = 60)
    private String itemCode;

    @Column(name = "item_label", nullable = false, length = 255)
    private String itemLabel;

    @Column(name = "is_mandatory", nullable = false)
    private boolean isMandatory = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChecklistStatus status = ChecklistStatus.PENDING;

    @Column(name = "completed_by", length = 100)
    private String completedBy;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(length = 1000)
    private String remarks;

    public SurgeryChecklistItem() {
    }

    public SurgeryChecklistItem(SurgeryBooking surgeryBooking, ChecklistPhase phase, String itemCode, String itemLabel, boolean isMandatory) {
        this.surgeryBooking = surgeryBooking;
        this.phase = phase;
        this.itemCode = itemCode;
        this.itemLabel = itemLabel;
        this.isMandatory = isMandatory;
        this.status = ChecklistStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SurgeryBooking getSurgeryBooking() {
        return surgeryBooking;
    }

    public void setSurgeryBooking(SurgeryBooking surgeryBooking) {
        this.surgeryBooking = surgeryBooking;
    }

    public ChecklistPhase getPhase() {
        return phase;
    }

    public void setPhase(ChecklistPhase phase) {
        this.phase = phase;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public ChecklistStatus getStatus() {
        return status;
    }

    public void setStatus(ChecklistStatus status) {
        this.status = status;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
