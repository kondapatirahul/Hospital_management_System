package com.healthcare.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ShiftHandoverForm {
    @NotBlank @Size(max=100) private String department;
    @NotBlank @Size(max=30) private String shiftName;
    private LocalDateTime shiftDate=LocalDateTime.now();
    @NotBlank @Size(max=100) private String outgoingStaff;
    @NotBlank @Size(max=100) private String incomingStaff;
    @NotBlank @Size(max=3000) private String summary;
    @Size(max=3000) private String outstandingTasks;
    @Size(max=3000) private String patientWatchlist;
    @Size(max=2000) private String equipmentOrSupplyIssues;
    @Size(max=2000) private String escalationNotes;
    private boolean urgent;
    private boolean medicationCountVerified;
    private boolean controlledItemsVerified;
    private boolean patientBoardReviewed;
    private boolean safetyChecksComplete;
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
    public boolean isUrgent(){return urgent;} public void setUrgent(boolean v){urgent=v;}
    public boolean isMedicationCountVerified(){return medicationCountVerified;} public void setMedicationCountVerified(boolean v){medicationCountVerified=v;}
    public boolean isControlledItemsVerified(){return controlledItemsVerified;} public void setControlledItemsVerified(boolean v){controlledItemsVerified=v;}
    public boolean isPatientBoardReviewed(){return patientBoardReviewed;} public void setPatientBoardReviewed(boolean v){patientBoardReviewed=v;}
    public boolean isSafetyChecksComplete(){return safetyChecksComplete;} public void setSafetyChecksComplete(boolean v){safetyChecksComplete=v;}
}
