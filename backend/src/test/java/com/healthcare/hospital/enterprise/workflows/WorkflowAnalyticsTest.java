package com.healthcare.hospital.enterprise.workflows;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
class WorkflowAnalyticsTest {
    @Test void normalizesStatusKeys() { assertEquals("IN_PROGRESS",WorkflowAnalytics.key("in progress")); }
    @Test void nullTextBecomesEmpty() { assertEquals("",WorkflowAnalytics.text(null)); }
    @Test void containsIgnoresCase() { assertTrue(WorkflowAnalytics.contains("Ward Nurse","NURSE")); }
    @Test void containsAcceptsEmptyQuery() { assertTrue(WorkflowAnalytics.contains("abc"," ")); }
    @Test void containsRejectsMissingValueForQuery() { assertFalse(WorkflowAnalytics.contains(null,"x")); }
    @Test void safeCollectionFiltersNulls() { assertEquals(List.of("a"),WorkflowAnalytics.safe(java.util.Arrays.asList("a",null))); }
    @Test void safeCollectionAcceptsNull() { assertTrue(WorkflowAnalytics.safe(null).isEmpty()); }
    @Test void dateRangeIncludesBoundaries() {
        assertTrue(WorkflowAnalytics.between(LocalDate.of(2026,1,1),LocalDate.of(2026,1,1),LocalDate.of(2026,1,1)));
    }
    @Test void dateRangeRejectsEarlierDate() {
        assertFalse(WorkflowAnalytics.between(LocalDate.of(2025,12,31),LocalDate.of(2026,1,1),null));
    }
    @Test void overdueExcludesCompleted() {
        assertFalse(WorkflowAnalytics.overdue(LocalDateTime.now().minusHours(1),LocalDateTime.now(),"COMPLETED"));
    }
    @Test void overdueDetectsOpenPastDue() {
        assertTrue(WorkflowAnalytics.overdue(LocalDateTime.now().minusHours(1),LocalDateTime.now(),"OPEN"));
    }
    @Test void snapshotCopiesMutableCollections() {
        var statuses=new java.util.ArrayList<>(List.of("OPEN","OPEN"));
        var snapshot=WorkflowAnalytics.snapshot("TEST",statuses,2,2,0,List.of());
        statuses.clear();
        assertEquals(2,snapshot.statusCounts().get("OPEN"));
    }
    @Test void snapshotRetainsCounters() {
        var snapshot=WorkflowAnalytics.snapshot("PATIENT",List.of("A","B"),9,8,1,List.of("review"));
        assertEquals(9,snapshot.total()); assertEquals(8,snapshot.active()); assertEquals(1,snapshot.attention());
    }
}
