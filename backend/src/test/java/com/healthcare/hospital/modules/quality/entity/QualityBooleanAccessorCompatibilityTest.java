package com.healthcare.hospital.modules.quality.entity;

import com.healthcare.hospital.modules.quality.dto.CreateQualityCommentRequest;
import com.healthcare.hospital.modules.quality.dto.CreateQualityReviewRequest;
import com.healthcare.hospital.modules.quality.dto.QualityReviewUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Protects the JavaBean boolean accessor contract used by service logic,
 * Thymeleaf binding, JSON serialization, and validation workflows.
 */
class QualityBooleanAccessorCompatibilityTest {

    @Test
    void qualityReviewBooleanAliasesShareTheSameBackingValues() {
        QualityReview review = new QualityReview();

        review.setPatientSafetyImpact(true);
        review.setRequiresDisclosure(true);
        review.setRegulatoryNotification(true);
        review.setSentinelEvent(true);
        review.setPrivacySensitive(true);
        review.setFollowUpRequired(true);
        review.setEvidencePreserved(true);

        assertTrue(review.isPatientSafetyImpact());
        assertTrue(review.isRequiresDisclosure());
        assertTrue(review.isRegulatoryNotification());
        assertTrue(review.isSentinelEvent());
        assertTrue(review.isPrivacySensitive());
        assertTrue(review.isFollowUpRequired());
        assertTrue(review.isEvidencePreserved());

        review.setPatientSafetyImpact(false);
        review.setRequiresDisclosure(false);
        review.setRegulatoryNotification(false);
        review.setSentinelEvent(false);
        review.setPrivacySensitive(false);
        review.setFollowUpRequired(false);
        review.setEvidencePreserved(false);

        assertFalse(review.isPatientSafetyImpact());
        assertFalse(review.isRequiresDisclosure());
        assertFalse(review.isRegulatoryNotification());
        assertFalse(review.isSentinelEvent());
        assertFalse(review.isPrivacySensitive());
        assertFalse(review.isFollowUpRequired());
        assertFalse(review.isEvidencePreserved());
    }

    @Test
    void createReviewRequestSupportsBooleanAccessorsExpectedByService() {
        CreateQualityReviewRequest request = new CreateQualityReviewRequest();
        request.setPatientSafetyImpact(true);
        request.setRequiresDisclosure(true);
        request.setRegulatoryNotification(true);
        request.setSentinelEvent(true);
        request.setPrivacySensitive(true);
        request.setFollowUpRequired(true);
        request.setEvidencePreserved(true);

        assertTrue(request.isPatientSafetyImpact());
        assertTrue(request.isRequiresDisclosure());
        assertTrue(request.isRegulatoryNotification());
        assertTrue(request.isSentinelEvent());
        assertTrue(request.isPrivacySensitive());
        assertTrue(request.isFollowUpRequired());
        assertTrue(request.isEvidencePreserved());
    }

    @Test
    void updateAndCommentRequestsExposeBooleanProperties() {
        QualityReviewUpdateRequest update = new QualityReviewUpdateRequest();
        update.setEvidencePreserved(true);
        assertTrue(update.isEvidencePreserved());

        CreateQualityCommentRequest comment = new CreateQualityCommentRequest();
        comment.setActionRequired(true);
        assertTrue(comment.isActionRequired());
    }
}
