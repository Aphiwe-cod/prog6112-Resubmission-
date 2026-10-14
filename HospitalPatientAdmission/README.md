# MediCare Hospital Patient Admission System

A menu-driven Java console application for the Programming 1B Practical Assignment 1, implemented against the supplied assignment brief and walkthrough.

## Requirements

- Java 17 or later
- Maven 3.8+

## Build, test, and run

From this directory:

```bash
mvn test
mvn exec:java
```

The main menu follows the walkthrough numbering: `0` exits; `1` registers; `2` searches; `3` updates; `4` deletes; `5` displays patients; `6` allocates a bed; `7` releases a bed; `8` displays the ward layout; `9` and `10` show available/occupied beds; `11` prints the ward report; `12` and `13` bubble-sort by surname and ID.

## Design notes

- `PatientCategory` is an enum. `Patient` is the encapsulated base class; `Inpatient` extends it, calls `super(...)`, and overrides `displayDetails()`.
- `HospitalService` keeps records in an `ArrayList` and models the 4 × 5 bed plan with two-dimensional arrays. The cells record the patient ID for occupied beds; `0` denotes an empty bed.
- Only an inpatient can be allocated one bed, and a bed may have only one patient. Deleting an inpatient or changing their category releases their bed.
- Patient IDs must be positive and unique; age and required text fields are validated.
- JUnit tests cover CRUD operations, duplicate IDs, bed assignment/release, full capacity, invalid allocations, and both sort orders.
- Data is in memory and is cleared when the program exits, consistent with the assignment assumptions.

## Suggested walkthrough

Start the program, register one inpatient and one outpatient, test duplicate registration and patient search, allocate a bed such as `B05`, inspect the layout and report, then release the bed and delete the inpatient. Finally test options 12 and 13 and exit with 0.

Review the code and make sure you can explain the design and tests before submitting. Follow your institution's rules for acknowledging assistance and for individual work.
