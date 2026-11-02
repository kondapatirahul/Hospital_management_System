package com.healthcare.hospital.enterprise.phase4.purchaseapproval.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class PurchaseApprovalPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:PurchaseApprovalPolicy.STATUS_ORDER) assertTrue(PurchaseApprovalPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(PurchaseApprovalPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,PurchaseApprovalPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",PurchaseApprovalPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",PurchaseApprovalPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,PurchaseApprovalPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(PurchaseApprovalPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=PurchaseApprovalPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),PurchaseApprovalPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
