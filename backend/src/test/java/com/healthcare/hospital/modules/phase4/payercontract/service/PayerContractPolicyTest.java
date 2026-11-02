package com.healthcare.hospital.modules.phase4.payercontract.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class PayerContractPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:PayerContractPolicy.STATUS_ORDER) assertTrue(PayerContractPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(PayerContractPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,PayerContractPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",PayerContractPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",PayerContractPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,PayerContractPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(PayerContractPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=PayerContractPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),PayerContractPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
