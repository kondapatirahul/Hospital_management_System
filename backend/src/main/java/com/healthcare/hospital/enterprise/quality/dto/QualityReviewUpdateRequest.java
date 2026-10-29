package com.healthcare.hospital.enterprise.quality.dto;

import jakarta.validation.constraints.Size;

/** Editable investigation fields; status transitions are deliberately separate endpoints. */
public class QualityReviewUpdateRequest {
    @Size(max=5000) private String immediateContainment;
    @Size(max=5000) private String contributingFactors;
    @Size(max=5000) private String rootCause;
    @Size(max=5000) private String correctivePlan;
    @Size(max=5000) private String preventionPlan;
    @Size(max=5000) private String learningSummary;
    @Size(max=100) private String ownerUsername;
    @Size(max=100) private String reviewerUsername;
    @Size(max=4000) private String closureSummary;
    private java.time.LocalDate dueDate;
    private boolean evidencePreserved;
    public String getImmediateContainment() { return immediateContainment; }
    public void setImmediateContainment(String value) { this.immediateContainment=value; }
    public String getContributingFactors() { return contributingFactors; }
    public void setContributingFactors(String value) { this.contributingFactors=value; }
    public String getRootCause() { return rootCause; }
    public void setRootCause(String value) { this.rootCause=value; }
    public String getCorrectivePlan() { return correctivePlan; }
    public void setCorrectivePlan(String value) { this.correctivePlan=value; }
    public String getPreventionPlan() { return preventionPlan; }
    public void setPreventionPlan(String value) { this.preventionPlan=value; }
    public String getLearningSummary() { return learningSummary; }
    public void setLearningSummary(String value) { this.learningSummary=value; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String value) { this.ownerUsername=value; }
    public String getReviewerUsername() { return reviewerUsername; }
    public void setReviewerUsername(String value) { this.reviewerUsername=value; }
    public String getClosureSummary() { return closureSummary; }
    public void setClosureSummary(String value) { this.closureSummary=value; }
    public java.time.LocalDate getDueDate() { return dueDate; }
    public void setDueDate(java.time.LocalDate value) { this.dueDate=value; }
    public boolean getEvidencePreserved() { return evidencePreserved; }
    public boolean isEvidencePreserved() { return evidencePreserved; }
    public void setEvidencePreserved(boolean value) { this.evidencePreserved=value; }
}
