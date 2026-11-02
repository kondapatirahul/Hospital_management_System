package com.healthcare.hospital.modules.phase4.revenueplan.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class RevenuePlanPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:RevenuePlanPolicy.STATUS_ORDER) assertTrue(RevenuePlanPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(RevenuePlanPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,RevenuePlanPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",RevenuePlanPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",RevenuePlanPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,RevenuePlanPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(RevenuePlanPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=RevenuePlanPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),RevenuePlanPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
