package com.healthcare.hospital.enterprise.phase4.staffcredential.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class StaffCredentialPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:StaffCredentialPolicy.STATUS_ORDER) assertTrue(StaffCredentialPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(StaffCredentialPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,StaffCredentialPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",StaffCredentialPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",StaffCredentialPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,StaffCredentialPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(StaffCredentialPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=StaffCredentialPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),StaffCredentialPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
