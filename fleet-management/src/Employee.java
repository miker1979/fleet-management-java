import java.util.ArrayList;

public class Employee {

    private int employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private String address;
    private String phoneNumber;
    private String email;
    private String hireDate;
    private boolean active;
    private double payRate;
    private String assignedTruckId;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;

    // Driver Compliance
    private String driverLicenseNumber;
    private String licenseClass;
    private String licenseExpirationDate;
    private String endorsements;
    private String dotPhysicalExpirationDate;

    // Equipment / Certification
    private boolean forkliftCertified;
    private String forkliftCertificationExpirationDate;

    private ArrayList<Evaluation> evaluations;

    public Employee(int employeeId, String firstName, String lastName, String position,
                    String department, String address, String phoneNumber,
                    String email, String hireDate, boolean active, double payRate) {

        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.department = department;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.hireDate = hireDate;
        this.active = active;
        this.payRate = payRate;
        this.assignedTruckId = "";

        this.emergencyContactName = "";
        this.emergencyContactPhone = "";
        this.emergencyContactRelationship = "";

        this.driverLicenseNumber = "";
        this.licenseClass = "";
        this.licenseExpirationDate = "";
        this.endorsements = "";
        this.dotPhysicalExpirationDate = "";

        this.forkliftCertified = false;
        this.forkliftCertificationExpirationDate = "";

        this.evaluations = new ArrayList<>();
    }

    // =========================
    // GETTERS
    // =========================
    public int getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPosition() {
        return position;
    }

    public String getDepartment() {
        return department;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getHireDate() {
        return hireDate;
    }

    public boolean isActive() {
        return active;
    }

    public double getPayRate() {
        return payRate;
    }

    public String getAssignedTruckId() {
        return assignedTruckId;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public String getEmergencyContactRelationship() {
        return emergencyContactRelationship;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public String getLicenseClass() {
        return licenseClass;
    }

    public String getLicenseExpirationDate() {
        return licenseExpirationDate;
    }

    public String getEndorsements() {
        return endorsements;
    }

    public String getDotPhysicalExpirationDate() {
        return dotPhysicalExpirationDate;
    }

    public boolean isForkliftCertified() {
        return forkliftCertified;
    }

    public String getForkliftCertificationExpirationDate() {
        return forkliftCertificationExpirationDate;
    }

    public ArrayList<Evaluation> getEvaluations() {
        return evaluations;
    }

    // =========================
    // SETTERS
    // =========================
    public void setPosition(String position) {
        this.position = position;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPayRate(double payRate) {
        this.payRate = payRate;
    }

    public void setAssignedTruckId(String assignedTruckId) {
        this.assignedTruckId = assignedTruckId;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public void setEmergencyContactRelationship(String emergencyContactRelationship) {
        this.emergencyContactRelationship = emergencyContactRelationship;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public void setLicenseClass(String licenseClass) {
        this.licenseClass = licenseClass;
    }

    public void setLicenseExpirationDate(String licenseExpirationDate) {
        this.licenseExpirationDate = licenseExpirationDate;
    }

    public void setEndorsements(String endorsements) {
        this.endorsements = endorsements;
    }

    public void setDotPhysicalExpirationDate(String dotPhysicalExpirationDate) {
        this.dotPhysicalExpirationDate = dotPhysicalExpirationDate;
    }

    public void setForkliftCertified(boolean forkliftCertified) {
        this.forkliftCertified = forkliftCertified;
    }

    public void setForkliftCertificationExpirationDate(String forkliftCertificationExpirationDate) {
        this.forkliftCertificationExpirationDate = forkliftCertificationExpirationDate;
    }

    public void setFirstName(String firstName) {
    this.firstName = firstName;
}

public void setLastName(String lastName) {
    this.lastName = lastName;
}

public void setHireDate(String hireDate) {
    this.hireDate = hireDate;
}

    // =========================
    // EVALUATION METHODS
    // =========================
    public void addEvaluation(Evaluation evaluation) {
        evaluations.add(evaluation);
    }

    public void displayAllEvaluations() {
        if (evaluations.isEmpty()) {
            System.out.println("No evaluations found for " + getFullName());
            return;
        }

        System.out.println("Evaluations for " + getFullName() + ":");
        for (Evaluation evaluation : evaluations) {
            evaluation.displayEvaluation();
        }
    }

    public double getAverageEvaluationScore() {
        if (evaluations.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Evaluation evaluation : evaluations) {
            total += evaluation.getOverallScore();
        }

        return total / evaluations.size();
    }

    public void displayEmployee() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + getFullName());
        System.out.println("Position: " + position);
        System.out.println("Department: " + department);
        System.out.println("Address: " + address);
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Email: " + email);
        System.out.println("Hire Date: " + hireDate);
        System.out.println("Status: " + (active ? "Active" : "Inactive"));
        System.out.println("Pay Rate: $" + payRate);
        System.out.println("Assigned Truck: " + (assignedTruckId == null || assignedTruckId.isEmpty() ? "None" : assignedTruckId));

        System.out.println("Emergency Contact: " +
                (emergencyContactName == null || emergencyContactName.isEmpty() ? "None" : emergencyContactName));
        System.out.println("Emergency Contact Phone: " +
                (emergencyContactPhone == null || emergencyContactPhone.isEmpty() ? "None" : emergencyContactPhone));
        System.out.println("Emergency Contact Relationship: " +
                (emergencyContactRelationship == null || emergencyContactRelationship.isEmpty() ? "None" : emergencyContactRelationship));

        System.out.println("Driver License Number: " +
                (driverLicenseNumber == null || driverLicenseNumber.isEmpty() ? "N/A" : driverLicenseNumber));
        System.out.println("License Class: " +
                (licenseClass == null || licenseClass.isEmpty() ? "N/A" : licenseClass));
        System.out.println("License Expiration: " +
                (licenseExpirationDate == null || licenseExpirationDate.isEmpty() ? "N/A" : licenseExpirationDate));
        System.out.println("Endorsements: " +
                (endorsements == null || endorsements.isEmpty() ? "N/A" : endorsements));
        System.out.println("DOT Physical Expiration: " +
                (dotPhysicalExpirationDate == null || dotPhysicalExpirationDate.isEmpty() ? "N/A" : dotPhysicalExpirationDate));

        System.out.println("Forklift Certified: " + (forkliftCertified ? "Yes" : "No"));
        System.out.println("Forklift Certification Expiration: " +
                (forkliftCertificationExpirationDate == null || forkliftCertificationExpirationDate.isEmpty() ? "N/A" : forkliftCertificationExpirationDate));

        System.out.println("Average Evaluation Score: " + getAverageEvaluationScore());
        System.out.println("-----------------------------");
    }
}