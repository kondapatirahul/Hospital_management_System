package com.healthcare.hospital.modules.phase4.complianceattestation.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class ComplianceAttestationPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:ComplianceAttestationPolicy.STATUS_ORDER) assertTrue(ComplianceAttestationPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(ComplianceAttestationPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,ComplianceAttestationPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",ComplianceAttestationPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",ComplianceAttestationPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,ComplianceAttestationPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(ComplianceAttestationPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=ComplianceAttestationPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),ComplianceAttestationPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
