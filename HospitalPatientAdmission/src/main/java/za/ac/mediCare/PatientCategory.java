package za.ac.medicare;

/** The three patient categories supported by the ward system. */
public enum PatientCategory {
    INPATIENT, OUTPATIENT, EMERGENCY;

    public static PatientCategory fromMenuChoice(int choice) {
        return switch (choice) {
            case 1 -> INPATIENT;
            case 2 -> OUTPATIENT;
            case 3 -> EMERGENCY;
            default -> throw new IllegalArgumentException("Choose 1, 2, or 3.");
        };
    }
}
