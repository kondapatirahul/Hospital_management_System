package com.healthcare.hospital.enterprise.phase4.expensereview.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class ExpenseReviewPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:ExpenseReviewPolicy.STATUS_ORDER) assertTrue(ExpenseReviewPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(ExpenseReviewPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,ExpenseReviewPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",ExpenseReviewPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",ExpenseReviewPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,ExpenseReviewPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(ExpenseReviewPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=ExpenseReviewPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),ExpenseReviewPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
