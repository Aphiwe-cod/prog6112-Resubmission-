package za.ac.medicare;

import java.util.Objects;

/** Base patient record. Attributes are private and exposed through accessors. */
public class Patient {
    private final int patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String medicalCondition;
    private PatientCategory category;

    public Patient(int patientId, String firstName, String lastName, int age,
                   String gender, String medicalCondition, PatientCategory category) {
        if (patientId <= 0) throw new IllegalArgumentException("Patient ID must be positive.");
        if (age < 0 || age > 130) throw new IllegalArgumentException("Age must be between 0 and 130.");
        this.patientId = patientId;
        setFirstName(firstName); setLastName(lastName); setAge(age); setGender(gender);
        setMedicalCondition(medicalCondition); setCategory(category);
    }

    public int getPatientId() { return patientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String value) { firstName = required(value, "First name"); }
    public String getLastName() { return lastName; }
    public void setLastName(String value) { lastName = required(value, "Last name"); }
    public int getAge() { return age; }
    public void setAge(int value) {
        if (value < 0 || value > 130) throw new IllegalArgumentException("Age must be between 0 and 130.");
        age = value;
    }
    public String getGender() { return gender; }
    public void setGender(String value) { gender = required(value, "Gender"); }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String value) { medicalCondition = required(value, "Medical condition"); }
    public PatientCategory getCategory() { return category; }
    public void setCategory(PatientCategory value) { category = Objects.requireNonNull(value, "Category is required."); }

    public String displayDetails() {
        return String.format("ID: %d | Name: %s %s | Age: %d | Gender: %s | Condition: %s | Category: %s",
                patientId, firstName, lastName, age, gender, medicalCondition, category);
    }

    private static String required(String value, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " cannot be blank.");
        return value.trim();
    }
}
