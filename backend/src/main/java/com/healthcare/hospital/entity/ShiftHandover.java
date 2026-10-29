package com.healthcare.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Structured shift-to-shift handover record with explicit receipt acknowledgement. */
@Entity
@Table(name="shift_handovers", indexes={
    @Index(name="idx_handover_department", columnList="department"),
    @Index(name="idx_handover_shift_date", columnList="shift_date"),
    @Index(name="idx_handover_status", columnList="status")
})
public class ShiftHandover {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=32)
    private String referenceCode;
    @Column(nullable=false, length=100)
    private String department;
    @Column(nullable=false, length=30)
    private String shiftName;
    @Column(name="shift_date", nullable=false)
    private LocalDateTime shiftDate;
    @Column(nullable=false, length=100)
    private String outgoingStaff;
    @Column(nullable=false, length=100)
    private String incomingStaff;
    @Column(nullable=false, length=3000)
    private String summary;
    @Column(length=3000)
    private String outstandingTasks;
    @Column(length=3000)
    private String patientWatchlist;
    @Column(length=2000)
    private String equipmentOrSupplyIssues;
    @Column(length=2000)
    private String escalationNotes;
    @Column(nullable=false, length=24)
    private String status="DRAFT";
    @Column(nullable=false)
    private boolean urgent;
    @Column(nullable=false)
    private boolean medicationCountVerified;
    @Column(nullable=false)
    private boolean controlledItemsVerified;
    @Column(nullable=false)
    private boolean patientBoardReviewed;
    @Column(nullable=false)
    private boolean safetyChecksComplete;
    @Column(nullable=false)
    private LocalDateTime createdAt=LocalDateTime.now();
    private LocalDateTime submittedAt;
    private LocalDateTime acknowledgedAt;
    @Column(length=100)
    private String acknowledgedBy;
    @Column(length=1000)
    private String receiverComment;
    @Version private Long version;

    public ShiftHandover() { }
    public Long getId(){return id;}
    public String getReferenceCode(){return referenceCode;} public void setReferenceCode(String v){referenceCode=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public String getShiftName(){return shiftName;} public void setShiftName(String v){shiftName=v;}
    public LocalDateTime getShiftDate(){return shiftDate;} public void setShiftDate(LocalDateTime v){shiftDate=v;}
    public String getOutgoingStaff(){return outgoingStaff;} public void setOutgoingStaff(String v){outgoingStaff=v;}
    public String getIncomingStaff(){return incomingStaff;} public void setIncomingStaff(String v){incomingStaff=v;}
    public String getSummary(){return summary;} public void setSummary(String v){summary=v;}
    public String getOutstandingTasks(){return outstandingTasks;} public void setOutstandingTasks(String v){outstandingTasks=v;}
    public String getPatientWatchlist(){return patientWatchlist;} public void setPatientWatchlist(String v){patientWatchlist=v;}
    public String getEquipmentOrSupplyIssues(){return equipmentOrSupplyIssues;} public void setEquipmentOrSupplyIssues(String v){equipmentOrSupplyIssues=v;}
    public String getEscalationNotes(){return escalationNotes;} public void setEscalationNotes(String v){escalationNotes=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public boolean isUrgent(){return urgent;} public void setUrgent(boolean v){urgent=v;}
    public boolean isMedicationCountVerified(){return medicationCountVerified;} public void setMedicationCountVerified(boolean v){medicationCountVerified=v;}
    public boolean isControlledItemsVerified(){return controlledItemsVerified;} public void setControlledItemsVerified(boolean v){controlledItemsVerified=v;}
    public boolean isPatientBoardReviewed(){return patientBoardReviewed;} public void setPatientBoardReviewed(boolean v){patientBoardReviewed=v;}
    public boolean isSafetyChecksComplete(){return safetyChecksComplete;} public void setSafetyChecksComplete(boolean v){safetyChecksComplete=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getSubmittedAt(){return submittedAt;} public void setSubmittedAt(LocalDateTime v){submittedAt=v;}
    public LocalDateTime getAcknowledgedAt(){return acknowledgedAt;} public void setAcknowledgedAt(LocalDateTime v){acknowledgedAt=v;}
    public String getAcknowledgedBy(){return acknowledgedBy;} public void setAcknowledgedBy(String v){acknowledgedBy=v;}
    public String getReceiverComment(){return receiverComment;} public void setReceiverComment(String v){receiverComment=v;}
    public Long getVersion(){return version;}
}
