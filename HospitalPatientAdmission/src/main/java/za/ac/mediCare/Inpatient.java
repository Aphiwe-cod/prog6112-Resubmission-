package za.ac.medicare;

/** Inpatients inherit common patient data and add ward and bed details. */
public class Inpatient extends Patient {
    private final String wardNumber;
    private String bedNumber;

    public Inpatient(int patientId, String firstName, String lastName, int age,
                     String gender, String medicalCondition, String wardNumber) {
        super(patientId, firstName, lastName, age, gender, medicalCondition, PatientCategory.INPATIENT);
        if (wardNumber == null || wardNumber.isBlank()) throw new IllegalArgumentException("Ward number is required.");
        this.wardNumber = wardNumber.trim();
        this.bedNumber = "Not allocated";
    }

    public String getWardNumber() { return wardNumber; }
    public String getBedNumber() { return bedNumber; }
    public void setBed(String bedNumber) {
        if (bedNumber == null || bedNumber.isBlank()) throw new IllegalArgumentException("Bed number is required.");
        this.bedNumber = bedNumber.trim();
    }
    public boolean hasBed() { return !"Not allocated".equals(bedNumber); }

    @Override public String displayDetails() {
        return super.displayDetails() + String.format(" | Ward: %s | Bed: %s", wardNumber, bedNumber);
    }
}
