package za.ac.medicare;

import java.util.List;
import java.util.Scanner;

/** Simple console menu; choose 0 to stop the program. */
public class Main {
    private final Scanner in = new Scanner(System.in);
    private final HospitalService hospital = new HospitalService();

    public static void main(String[] args) { new Main().run(); }

    private void run() {
        int choice;
        do {
            System.out.println("\n=== MediCare Hospital ===\n1 Register  2 Search  3 Update  4 Delete  5 List patients"
                    + "\n6 Allocate bed  7 Release bed  8 Ward layout  9 Available beds"
                    + "\n10 Occupied beds  11 Ward report  12 Sort surname  13 Sort ID  0 Exit");
            choice = number("Select: ");
            switch (choice) {
                case 1 -> register(); case 2 -> search(); case 3 -> update(); case 4 -> delete();
                case 5 -> listPatients(); case 6 -> allocate(); case 7 -> release(); case 8 -> layout();
                case 9 -> showBeds("Available", hospital.getAvailableBeds());
                case 10 -> showBeds("Occupied", hospital.getOccupiedBeds()); case 11 -> report();
                case 12 -> { hospital.sortBySurname(); System.out.println("Sorted by surname."); }
                case 13 -> { hospital.sortById(); System.out.println("Sorted by ID."); }
                case 0 -> System.out.println("Goodbye.");
                default -> System.out.println("Choose an option from 0 to 13.");
            }
        } while (choice != 0);
    }

    private void register() {
        try {
            int id = number("Patient ID: ");
            Patient p = patientDetails(id, category());
            System.out.println(hospital.registerPatient(p) ? "Patient registered." : "ID already exists.");
        } catch (IllegalArgumentException e) { System.out.println("Unable to register: " + e.getMessage()); }
    }
    private Patient patientDetails(int id, PatientCategory category) {
        String first = text("First name: "), last = text("Last name: ");
        int age = number("Age: ");
        String gender = text("Gender: "), condition = text("Medical condition: ");
        return category == PatientCategory.INPATIENT
                ? new Inpatient(id, first, last, age, gender, condition, "Ward 1")
                : new Patient(id, first, last, age, gender, condition, category);
    }
    private void search() {
        Patient p = hospital.findPatient(number("Patient ID: "));
        System.out.println(p == null ? "ID not found" : p.displayDetails());
    }
    private void update() {
        int id = number("Patient ID to update: ");
        if (hospital.findPatient(id) == null) { System.out.println("Unable to update patient details."); return; }
        try {
            Patient p = patientDetails(id, category());
            System.out.println(hospital.updatePatient(id, p.getFirstName(), p.getLastName(), p.getAge(),
                    p.getGender(), p.getMedicalCondition(), p.getCategory()) ? "Patient updated." : "Unable to update.");
        } catch (IllegalArgumentException e) { System.out.println("Unable to update: " + e.getMessage()); }
    }
    private void delete() {
        System.out.println(hospital.deletePatient(number("Patient ID to delete: ")) ? "Patient deleted" : "ID not found");
    }
    private void listPatients() {
        List<Patient> patients = hospital.getPatients();
        System.out.println("Registered patients (" + patients.size() + "):");
        if (patients.isEmpty()) System.out.println("None");
        for (Patient p : patients) System.out.println(p.displayDetails());
    }
    private void allocate() {
        System.out.println(hospital.allocateBed(number("Inpatient ID: "), text("Bed (B01-B20): "))
                ? "Bed successfully allocated" : "Unable to allocate bed");
    }
    private void release() {
        System.out.println(hospital.releaseBedByNumber(text("Bed to release: ")) ? "Bed released" : "Unable to release bed");
    }
    private void layout() {
        for (String[] row : hospital.getWardLayout()) {
            for (String bed : row) System.out.printf("%-21s", bed);
            System.out.println();
        }
    }
    private void showBeds(String heading, List<String> beds) {
        System.out.println(heading + " beds (" + beds.size() + "): " + (beds.isEmpty() ? "None" : String.join(", ", beds)));
    }
    private void report() {
        System.out.println("\n--- Ward report ---");
        listPatients(); showBeds("Available", hospital.getAvailableBeds()); showBeds("Occupied", hospital.getOccupiedBeds());
        System.out.println("Registered patients: " + hospital.getPatientCount());
        System.out.println("Occupied beds: " + hospital.getOccupiedBedCount());
        System.out.printf("Occupancy: %.1f%%%n", hospital.getOccupancyPercentage());
    }
    private PatientCategory category() {
        while (true) try {
            return PatientCategory.fromMenuChoice(number("Category 1 Inpatient, 2 Outpatient, 3 Emergency: "));
        } catch (IllegalArgumentException e) { System.out.println("Enter 1, 2, or 3."); }
    }
    private String text(String prompt) {
        while (true) { System.out.print(prompt); String s = in.nextLine().trim(); if (!s.isEmpty()) return s; }
    }
    private int number(String prompt) {
        while (true) try { System.out.print(prompt); return Integer.parseInt(in.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Enter a whole number."); }
    }
}
