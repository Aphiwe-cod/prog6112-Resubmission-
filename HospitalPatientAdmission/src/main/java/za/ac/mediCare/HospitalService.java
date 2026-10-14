package za.ac.medicare;

import java.util.ArrayList;
import java.util.List;

/** Holds patients and the ward's 4-by-5 bed layout in memory. */
public class HospitalService {
    public static final int ROWS = 4, COLUMNS = 5, BED_COUNT = ROWS * COLUMNS;
    private final ArrayList<Patient> patients = new ArrayList<>();
    private final String[][] beds = new String[ROWS][COLUMNS];
    private final int[][] occupants = new int[ROWS][COLUMNS]; // 0 means empty

    public HospitalService() {
        int n = 1;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLUMNS; c++)
            beds[r][c] = String.format("B%02d", n++);
    }
    public boolean registerPatient(Patient p) {
        if (p == null || findPatient(p.getPatientId()) != null) return false;
        patients.add(p); return true;
    }
    public Patient findPatient(int id) {
        for (Patient p : patients) if (p.getPatientId() == id) return p;
        return null;
    }
    public boolean updatePatient(int id, String first, String last, int age, String gender,
                                 String condition, PatientCategory category) {
        Patient old = findPatient(id);
        if (old == null || category == null) return false;
        try {
            if (old instanceof Inpatient i && category == PatientCategory.INPATIENT) {
                i.setFirstName(first); i.setLastName(last); i.setAge(age);
                i.setGender(gender); i.setMedicalCondition(condition);
                return true;
            }
            String ward = old instanceof Inpatient i ? i.getWardNumber() : "Ward 1";
            Patient replacement = category == PatientCategory.INPATIENT
                    ? new Inpatient(id, first, last, age, gender, condition, ward)
                    : new Patient(id, first, last, age, gender, condition, category);
            if (old instanceof Inpatient i && i.hasBed()) releaseBed(id);
            patients.set(patients.indexOf(old), replacement);
            return true;
        } catch (IllegalArgumentException e) { return false; }
    }
    public boolean deletePatient(int id) {
        Patient p = findPatient(id);
        if (p == null) return false;
        if (p instanceof Inpatient i && i.hasBed()) releaseBed(id);
        return patients.remove(p);
    }
    public boolean allocateBed(int id, String bed) {
        Patient p = findPatient(id);
        int[] cell = cell(bed);
        if (!(p instanceof Inpatient i) || i.hasBed() || cell == null || occupants[cell[0]][cell[1]] != 0) return false;
        occupants[cell[0]][cell[1]] = id; i.setBed(beds[cell[0]][cell[1]]); return true;
    }
    public boolean releaseBed(int id) {
        Patient p = findPatient(id);
        if (!(p instanceof Inpatient i) || !i.hasBed()) return false;
        int[] cell = cell(i.getBedNumber());
        if (cell == null || occupants[cell[0]][cell[1]] != id) return false;
        occupants[cell[0]][cell[1]] = 0; i.setBed("Not allocated"); return true;
    }
    public boolean releaseBedByNumber(String bed) {
        int[] cell = cell(bed);
        return cell != null && occupants[cell[0]][cell[1]] != 0 && releaseBed(occupants[cell[0]][cell[1]]);
    }
    public List<Patient> getPatients() { return new ArrayList<>(patients); }
    public int getPatientCount() { return patients.size(); }
    public int getOccupiedBedCount() {
        int count = 0;
        for (int[] row : occupants) for (int id : row) if (id != 0) count++;
        return count;
    }
    public double getOccupancyPercentage() { return getOccupiedBedCount() * 100.0 / BED_COUNT; }
    public List<String> getAvailableBeds() { return bedList(false); }
    public List<String> getOccupiedBeds() { return bedList(true); }
    public String[][] getWardLayout() {
        String[][] view = new String[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLUMNS; c++)
            view[r][c] = occupants[r][c] == 0 ? beds[r][c] + " [Available]" : beds[r][c] + " [ID " + occupants[r][c] + "]";
        return view;
    }
    public void sortBySurname() {
        for (int end = patients.size() - 1; end > 0; end--) for (int j = 0; j < end; j++)
            if (patients.get(j).getLastName().compareToIgnoreCase(patients.get(j + 1).getLastName()) > 0) swap(j);
    }
    public void sortById() {
        for (int end = patients.size() - 1; end > 0; end--) for (int j = 0; j < end; j++)
            if (patients.get(j).getPatientId() > patients.get(j + 1).getPatientId()) swap(j);
    }
    private void swap(int j) { Patient p = patients.get(j); patients.set(j, patients.get(j + 1)); patients.set(j + 1, p); }
    private List<String> bedList(boolean occupied) {
        List<String> list = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLUMNS; c++)
            if ((occupants[r][c] != 0) == occupied) list.add(beds[r][c]);
        return list;
    }
    private int[] cell(String bed) {
        if (bed == null) return null;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLUMNS; c++)
            if (beds[r][c].equalsIgnoreCase(bed.trim())) return new int[]{r, c};
        return null;
    }
}
