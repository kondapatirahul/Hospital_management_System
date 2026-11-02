package com.healthcare.hospital.enterprise.phase4.budgettransfer.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class BudgetTransferPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:BudgetTransferPolicy.STATUS_ORDER) assertTrue(BudgetTransferPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(BudgetTransferPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,BudgetTransferPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",BudgetTransferPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",BudgetTransferPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,BudgetTransferPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(BudgetTransferPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=BudgetTransferPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),BudgetTransferPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
