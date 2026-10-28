package com.healthcare.hospital.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Basic model contract tests for the ward and bed inventory. */
class WardBedTest {
    @Test
    void bedStartsAvailableAndCanStoreWardIdentity() {
        WardBed bed = new WardBed();
        bed.setWardName("General Ward");
        bed.setBedNumber("G-101");
        assertEquals("General Ward", bed.getWardName());
        assertEquals("G-101", bed.getBedNumber());
        assertNull(bed.getPatientName());
    }

    @Test
    void statusChangesAreReflectedByTheEntity() {
        WardBed bed = new WardBed();
        bed.setStatus("OCCUPIED");
        bed.setPatientName("Sample Patient");
        assertEquals("OCCUPIED", bed.getStatus());
        assertEquals("Sample Patient", bed.getPatientName());
    }
}
