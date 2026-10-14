package za.ac.medicare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HospitalServiceTest {
    private HospitalService service;
    @BeforeEach void setUp() { service = new HospitalService(); }

    private Inpatient inpatient(int id, String surname) {
        return new Inpatient(id, "Alex", surname, 30, "Other", "Observation", "Ward 1");
    }
    private Patient outpatient(int id) {
        return new Patient(id, "Sam", "Jones", 25, "Female", "Check-up", PatientCategory.OUTPATIENT);
    }

    @Test void registersAndSearchesPatientAndRejectsDuplicateId() {
        Patient p = outpatient(10);
        assertTrue(service.registerPatient(p));
        assertSame(p, service.findPatient(10));
        assertFalse(service.registerPatient(outpatient(10)));
        assertNull(service.findPatient(99));
    }

    @Test void updatesPatientDetailsAndCategory() {
        assertTrue(service.registerPatient(outpatient(10)));
        assertTrue(service.updatePatient(10, "Taylor", "Smith", 26, "Female", "Review", PatientCategory.INPATIENT));
        Patient changed = service.findPatient(10);
        assertInstanceOf(Inpatient.class, changed);
        assertEquals("Taylor", changed.getFirstName());
        assertEquals("Smith", changed.getLastName());
        assertEquals(26, changed.getAge());
        assertFalse(service.updatePatient(999, "A", "B", 20, "X", "Y", PatientCategory.OUTPATIENT));
    }

    @Test void deletesPatientAndReleasesTheirBed() {
        assertTrue(service.registerPatient(inpatient(1, "Brown")));
        assertTrue(service.allocateBed(1, "B05"));
        assertTrue(service.deletePatient(1));
        assertNull(service.findPatient(1));
        assertTrue(service.getAvailableBeds().contains("B05"));
        assertFalse(service.deletePatient(1));
    }

    @Test void allocatesAndReleasesBedsWithValidation() {
        service.registerPatient(inpatient(1, "Brown"));
        service.registerPatient(inpatient(2, "Green"));
        service.registerPatient(outpatient(3));
        assertTrue(service.allocateBed(1, "b05"));
        assertEquals("B05", ((Inpatient) service.findPatient(1)).getBedNumber());
        assertFalse(service.allocateBed(2, "B05"));
        assertFalse(service.allocateBed(1, "B06"));
        assertFalse(service.allocateBed(3, "B06"));
        assertFalse(service.allocateBed(2, "B21"));
        assertTrue(service.releaseBedByNumber("B05"));
        assertEquals("Not allocated", ((Inpatient) service.findPatient(1)).getBedNumber());
        assertFalse(service.releaseBedByNumber("B05"));
    }

    @Test void preventsAllocationWhenAllTwentyBedsAreOccupied() {
        for (int i = 1; i <= 21; i++) service.registerPatient(inpatient(i, "Patient" + i));
        for (int i = 1; i <= 20; i++) assertTrue(service.allocateBed(i, String.format("B%02d", i)));
        assertFalse(service.allocateBed(21, "B01"));
        assertEquals(20, service.getOccupiedBedCount());
        assertEquals(100.0, service.getOccupancyPercentage());
    }

    @Test void sortsBySurnameAndByPatientId() {
        service.registerPatient(outpatient(20));
        service.registerPatient(new Patient(3, "Jo", "Adams", 20, "X", "Y", PatientCategory.EMERGENCY));
        service.registerPatient(new Patient(8, "Lee", "Brown", 20, "X", "Y", PatientCategory.OUTPATIENT));
        service.sortBySurname();
        assertEquals("Adams", service.getPatients().get(0).getLastName());
        assertEquals("Brown", service.getPatients().get(1).getLastName());
        service.sortById();
        assertEquals(3, service.getPatients().get(0).getPatientId());
        assertEquals(8, service.getPatients().get(1).getPatientId());
        assertEquals(20, service.getPatients().get(2).getPatientId());
    }

    @Test void wardStartsWithTwentyAvailableBedsAndUsesTwoDimensionalLayout() {
        assertEquals(20, service.getAvailableBeds().size());
        assertEquals(0, service.getOccupiedBeds().size());
        assertEquals(4, service.getWardLayout().length);
        assertEquals(5, service.getWardLayout()[0].length);
        assertEquals(0.0, service.getOccupancyPercentage());
    }
}
