package com.healthcare.hospital.enterprise.phase4.servicelevelcase.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class ServiceLevelCasePolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:ServiceLevelCasePolicy.STATUS_ORDER) assertTrue(ServiceLevelCasePolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(ServiceLevelCasePolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,ServiceLevelCasePolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",ServiceLevelCasePolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",ServiceLevelCasePolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,ServiceLevelCasePolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(ServiceLevelCasePolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=ServiceLevelCasePolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),ServiceLevelCasePolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
