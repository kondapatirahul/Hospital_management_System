package com.healthcare.hospital.enterprise.staff.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Post-discharge outreach record linked to a completed discharge-readiness checklist. */
@Entity
@Table(name="patient_outreach", indexes={
    @Index(name="idx_outreach_state", columnList="status"),
    @Index(name="idx_outreach_due_date", columnList="next_contact_date"),
    @Index(name="idx_outreach_patient_ref", columnList="patient_reference"),
    @Index(name="idx_outreach_risk", columnList="escalation_level")
})
public class PatientOutreach {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=36) private String referenceCode;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="discharge_checklist_id", nullable=false) private DischargeChecklist dischargeChecklist;
    @Column(name="patient_reference", nullable=false, length=60) private String patientReference;
    @Column(name="patient_name", nullable=false, length=140) private String patientName;
    @Column(nullable=false, length=180) private String purpose;
    @Column(nullable=false, length=24) private String channel="PHONE";
    @Column(nullable=false, length=24) private String status="OPEN";
    @Column(nullable=false, length=24) private String outcome="NOT_ATTEMPTED";
    @Column(nullable=false, length=24) private String priority="NORMAL";
    @Column(name="escalation_level", nullable=false, length=24) private String escalationLevel="NONE";
    @Column(nullable=false, length=100) private String assignedTeam="Nursing";
    @Column(length=100) private String assignedTo;
    @Column(length=1000) private String contactNumber;
    @Column(length=120) private String preferredWindow;
    @Column(nullable=false) private boolean consentConfirmed;
    @Column(nullable=false) private boolean identityVerified;
    @Column(nullable=false) private boolean interpreterRequired;
    @Column(nullable=false) private boolean caregiverContacted;
    @Column(length=3000) private String conversationSummary;
    @Column(length=2500) private String reportedSymptoms;
    @Column(length=2500) private String medicationConcerns;
    @Column(length=2500) private String woundConcerns;
    @Column(length=2500) private String mobilityNotes;
    @Column(length=2500) private String socialSupportNotes;
    @Column(length=2500) private String patientQuestions;
    @Column(length=2500) private String adviceProvided;
    @Column(length=2500) private String escalationNotes;
    @Column(length=2500) private String handoffNotes;
    @Column(length=2500) private String closureNotes;
    @Column(nullable=false) private boolean redFlagSymptoms;
    @Column(nullable=false) private boolean breathingConcern;
    @Column(nullable=false) private boolean chestPainConcern;
    @Column(nullable=false) private boolean neurologicalConcern;
    @Column(nullable=false) private boolean uncontrolledPain;
    @Column(nullable=false) private boolean medicationAccessConcern;
    @Column(nullable=false) private boolean appointmentConfirmed;
    @Column(nullable=false) private boolean warningSignsUnderstood;
    @Column(nullable=false) private boolean clinicianHandoffRequired;
    @Column(nullable=false) private boolean supervisorReviewRequired;
    @Column(nullable=false) private int attemptCount;
    @Column(name="next_contact_date") private LocalDate nextContactDate;
    private LocalDateTime lastAttemptAt;
    private LocalDateTime closedAt;
    @Column(length=100, nullable=false) private String createdBy;
    @Column(length=100) private String lastUpdatedBy;
    @Column(length=100) private String closedBy;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    @Column(nullable=false) private LocalDateTime updatedAt=LocalDateTime.now();
    @Version private Long version;

    public PatientOutreach() { }
    @PrePersist protected void beforeInsert(){ LocalDateTime now=LocalDateTime.now(); if(createdAt==null)createdAt=now; updatedAt=now; }
    @PreUpdate protected void beforeUpdate(){ updatedAt=LocalDateTime.now(); }
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getReferenceCode(){return referenceCode;} public void setReferenceCode(String v){referenceCode=v;}
    public DischargeChecklist getDischargeChecklist(){return dischargeChecklist;} public void setDischargeChecklist(DischargeChecklist v){dischargeChecklist=v;}
    public String getPatientReference(){return patientReference;} public void setPatientReference(String v){patientReference=v;}
    public String getPatientName(){return patientName;} public void setPatientName(String v){patientName=v;}
    public String getPurpose(){return purpose;} public void setPurpose(String v){purpose=v;}
    public String getChannel(){return channel;} public void setChannel(String v){channel=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getOutcome(){return outcome;} public void setOutcome(String v){outcome=v;}
    public String getPriority(){return priority;} public void setPriority(String v){priority=v;}
    public String getEscalationLevel(){return escalationLevel;} public void setEscalationLevel(String v){escalationLevel=v;}
    public String getAssignedTeam(){return assignedTeam;} public void setAssignedTeam(String v){assignedTeam=v;}
    public String getAssignedTo(){return assignedTo;} public void setAssignedTo(String v){assignedTo=v;}
    public String getContactNumber(){return contactNumber;} public void setContactNumber(String v){contactNumber=v;}
    public String getPreferredWindow(){return preferredWindow;} public void setPreferredWindow(String v){preferredWindow=v;}
    public boolean isConsentConfirmed(){return consentConfirmed;} public void setConsentConfirmed(boolean v){consentConfirmed=v;}
    public boolean isIdentityVerified(){return identityVerified;} public void setIdentityVerified(boolean v){identityVerified=v;}
    public boolean isInterpreterRequired(){return interpreterRequired;} public void setInterpreterRequired(boolean v){interpreterRequired=v;}
    public boolean isCaregiverContacted(){return caregiverContacted;} public void setCaregiverContacted(boolean v){caregiverContacted=v;}
    public String getConversationSummary(){return conversationSummary;} public void setConversationSummary(String v){conversationSummary=v;}
    public String getReportedSymptoms(){return reportedSymptoms;} public void setReportedSymptoms(String v){reportedSymptoms=v;}
    public String getMedicationConcerns(){return medicationConcerns;} public void setMedicationConcerns(String v){medicationConcerns=v;}
    public String getWoundConcerns(){return woundConcerns;} public void setWoundConcerns(String v){woundConcerns=v;}
    public String getMobilityNotes(){return mobilityNotes;} public void setMobilityNotes(String v){mobilityNotes=v;}
    public String getSocialSupportNotes(){return socialSupportNotes;} public void setSocialSupportNotes(String v){socialSupportNotes=v;}
    public String getPatientQuestions(){return patientQuestions;} public void setPatientQuestions(String v){patientQuestions=v;}
    public String getAdviceProvided(){return adviceProvided;} public void setAdviceProvided(String v){adviceProvided=v;}
    public String getEscalationNotes(){return escalationNotes;} public void setEscalationNotes(String v){escalationNotes=v;}
    public String getHandoffNotes(){return handoffNotes;} public void setHandoffNotes(String v){handoffNotes=v;}
    public String getClosureNotes(){return closureNotes;} public void setClosureNotes(String v){closureNotes=v;}
    public boolean isRedFlagSymptoms(){return redFlagSymptoms;} public void setRedFlagSymptoms(boolean v){redFlagSymptoms=v;}
    public boolean isBreathingConcern(){return breathingConcern;} public void setBreathingConcern(boolean v){breathingConcern=v;}
    public boolean isChestPainConcern(){return chestPainConcern;} public void setChestPainConcern(boolean v){chestPainConcern=v;}
    public boolean isNeurologicalConcern(){return neurologicalConcern;} public void setNeurologicalConcern(boolean v){neurologicalConcern=v;}
    public boolean isUncontrolledPain(){return uncontrolledPain;} public void setUncontrolledPain(boolean v){uncontrolledPain=v;}
    public boolean isMedicationAccessConcern(){return medicationAccessConcern;} public void setMedicationAccessConcern(boolean v){medicationAccessConcern=v;}
    public boolean isAppointmentConfirmed(){return appointmentConfirmed;} public void setAppointmentConfirmed(boolean v){appointmentConfirmed=v;}
    public boolean isWarningSignsUnderstood(){return warningSignsUnderstood;} public void setWarningSignsUnderstood(boolean v){warningSignsUnderstood=v;}
    public boolean isClinicianHandoffRequired(){return clinicianHandoffRequired;} public void setClinicianHandoffRequired(boolean v){clinicianHandoffRequired=v;}
    public boolean isSupervisorReviewRequired(){return supervisorReviewRequired;} public void setSupervisorReviewRequired(boolean v){supervisorReviewRequired=v;}
    public int getAttemptCount(){return attemptCount;} public void setAttemptCount(int v){attemptCount=v;}
    public LocalDate getNextContactDate(){return nextContactDate;} public void setNextContactDate(LocalDate v){nextContactDate=v;}
    public LocalDateTime getLastAttemptAt(){return lastAttemptAt;} public void setLastAttemptAt(LocalDateTime v){lastAttemptAt=v;}
    public LocalDateTime getClosedAt(){return closedAt;} public void setClosedAt(LocalDateTime v){closedAt=v;}
    public String getCreatedBy(){return createdBy;} public void setCreatedBy(String v){createdBy=v;}
    public String getLastUpdatedBy(){return lastUpdatedBy;} public void setLastUpdatedBy(String v){lastUpdatedBy=v;}
    public String getClosedBy(){return closedBy;} public void setClosedBy(String v){closedBy=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
    public Long getVersion(){return version;}
    public boolean isFinalised(){return "CLOSED".equals(status)||"CANCELLED".equals(status);}
    public boolean isOverdue(){return nextContactDate!=null&&nextContactDate.isBefore(LocalDate.now())&&!isFinalised();}
}
