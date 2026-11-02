package com.healthcare.hospital.modules.phase4.auditfinding.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class AuditFindingPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:AuditFindingPolicy.STATUS_ORDER) assertTrue(AuditFindingPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(AuditFindingPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,AuditFindingPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",AuditFindingPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",AuditFindingPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,AuditFindingPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(AuditFindingPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=AuditFindingPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),AuditFindingPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
