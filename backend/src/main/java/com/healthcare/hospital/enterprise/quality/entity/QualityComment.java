package com.healthcare.hospital.enterprise.quality.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Append-only review discussion entry; sensitive content is never rendered as raw HTML. */
@Entity
@Table(name="clinical_quality_comments", indexes={
    @Index(name="idx_quality_comment_case", columnList="review_id,created_at")
})
public class QualityComment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="review_id", nullable=false)
    private QualityReview review;
    @Column(nullable=false, length=100) private String authorUsername;
    @Column(nullable=false, length=24) private String visibility="INTERNAL";
    @Column(nullable=false, length=24) private String commentType="NOTE";
    @Column(nullable=false, columnDefinition="TEXT") private String body;
    @Column(nullable=false) private boolean actionRequired;
    @Column(length=100) private String assignedTo;
    @Column(name="follow_up_date") private java.time.LocalDate followUpDate;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public QualityComment() { }
    public Long getId() { return id; }
    public void setId(Long v) { id=v; }
    public QualityReview getReview() { return review; }
    public void setReview(QualityReview v) { review=v; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String v) { authorUsername=v; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String v) { visibility=v; }
    public String getCommentType() { return commentType; }
    public void setCommentType(String v) { commentType=v; }
    public String getBody() { return body; }
    public void setBody(String v) { body=v; }
    public boolean isActionRequired() { return actionRequired; }
    public void setActionRequired(boolean v) { actionRequired=v; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String v) { assignedTo=v; }
    public java.time.LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(java.time.LocalDate v) { followUpDate=v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { createdAt=v; }
}
