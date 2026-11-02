package com.healthcare.hospital.enterprise.phase4.assetregister.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class AssetRegisterPolicyTest {
 @Test void recognizesEveryConfiguredStatus() { for(String status:AssetRegisterPolicy.STATUS_ORDER) assertTrue(AssetRegisterPolicy.isKnownStatus(status)); }
 @Test void rejectsUnknownStatus() { assertFalse(AssetRegisterPolicy.isKnownStatus("NOT_A_STATUS")); }
 @Test void amountNeverReturnsNegative() { assertEquals(BigDecimal.ZERO,AssetRegisterPolicy.safeAmount(new BigDecimal("-9.00"))); }
 @Test void dateUrgencyDistinguishesOverdueAndNearDue() { assertEquals("OVERDUE",AssetRegisterPolicy.urgency(LocalDate.now().minusDays(1))); assertEquals("CRITICAL",AssetRegisterPolicy.urgency(LocalDate.now().plusDays(1))); }
 @Test void percentageProtectsAgainstZeroDenominator() { assertEquals(BigDecimal.ZERO,AssetRegisterPolicy.percentage(BigDecimal.TEN,BigDecimal.ZERO)); }
 @Test void requiredFieldsAreReported() { assertTrue(AssetRegisterPolicy.validateRequired("",null,null).size()>=2); }
 @Test void classifyReturnsReviewSignals() { var result=AssetRegisterPolicy.classify(BigDecimal.TEN,LocalDate.now().minusDays(1),AssetRegisterPolicy.STATUS_ORDER.get(0),false); assertEquals("OVERDUE",result.get("urgency")); assertEquals(Boolean.TRUE,result.get("statusKnown")); }
}
