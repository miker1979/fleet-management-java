import java.io.Serializable;
import java.util.ArrayList;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

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

    // Equipment Assignment
    private String assignedTruckId;
    private String assignedTrailerId;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;

    // Driver Compliance
    private String driverLicenseNumber;
    private String licenseClass;
    private String licenseExpirationDate;
    private ArrayList<String> endorsements;
    private String dotPhysicalExpirationDate;

    // Equipment / Certification
    private boolean forkliftCertified;
    private String forkliftCertificationExpirationDate;

    private ArrayList<Evaluation> evaluations;

    public Employee(int employeeId, String firstName, String lastName, String position,
                    String department, String address, String phoneNumber,
                    String email, String hireDate, boolean active, double payRate) {

        this.employeeId = employeeId;
        this.firstName = safe(firstName);
        this.lastName = safe(lastName);
        this.position = safe(position);
        this.department = safe(department);
        this.address = safe(address);
        this.phoneNumber = safe(phoneNumber);
        this.email = safe(email);
        this.hireDate = safe(hireDate);
        this.active = active;
        this.payRate = payRate;

        this.assignedTruckId = "";
        this.assignedTrailerId = "";

        this.emergencyContactName = "";
        this.emergencyContactPhone = "";
        this.emergencyContactRelationship = "";

        this.driverLicenseNumber = "";
        this.licenseClass = "";
        this.licenseExpirationDate = "";
        this.endorsements = new ArrayList<>();
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

    public int getId() {
        return employeeId;
    }

    public String getFirstName() {
        return safe(firstName);
    }

    public String getLastName() {
        return safe(lastName);
    }

    public String getFullName() {
        return (safe(firstName) + " " + safe(lastName)).trim();
    }

    public String getPosition() {
        return safe(position);
    }

    public String getDepartment() {
        return safe(department);
    }

    public String getAddress() {
        return safe(address);
    }

    public String getPhoneNumber() {
        return safe(phoneNumber);
    }

    public String getEmail() {
        return safe(email);
    }

    public String getHireDate() {
        return safe(hireDate);
    }

    public boolean isActive() {
        return active;
    }

    public double getPayRate() {
        return payRate;
    }

    public String getAssignedTruckId() {
        return safe(assignedTruckId);
    }

    public String getAssignedTrailerId() {
        return safe(assignedTrailerId);
    }

    public String getEmergencyContactName() {
        return safe(emergencyContactName);
    }

    public String getEmergencyContactPhone() {
        return safe(emergencyContactPhone);
    }

    public String getEmergencyContactRelationship() {
        return safe(emergencyContactRelationship);
    }

    public String getDriverLicenseNumber() {
        return safe(driverLicenseNumber);
    }

    public String getLicenseClass() {
        return safe(licenseClass);
    }

    public String getLicenseExpirationDate() {
        return safe(licenseExpirationDate);
    }

    public ArrayList<String> getEndorsements() {
        return endorsements;
    }

    public String getDotPhysicalExpirationDate() {
        return safe(dotPhysicalExpirationDate);
    }

    public boolean isForkliftCertified() {
        return forkliftCertified;
    }

    public String getForkliftCertificationExpirationDate() {
        return safe(forkliftCertificationExpirationDate);
    }

    public ArrayList<Evaluation> getEvaluations() {
        return evaluations;
    }

    // =========================
    // SETTERS
    // =========================
    public void setFirstName(String firstName) {
        this.firstName = safe(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName = safe(lastName);
    }

    public void setPosition(String position) {
        this.position = safe(position);
    }

    public void setDepartment(String department) {
        this.department = safe(department);
    }

    public void setAddress(String address) {
        this.address = safe(address);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = safe(phoneNumber);
    }

    public void setEmail(String email) {
        this.email = safe(email);
    }

    public void setHireDate(String hireDate) {
        this.hireDate = safe(hireDate);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPayRate(double payRate) {
        this.payRate = payRate;
    }

    public void setAssignedTruckId(String assignedTruckId) {
        this.assignedTruckId = safe(assignedTruckId);
    }

    public void setAssignedTrailerId(String assignedTrailerId) {
        this.assignedTrailerId = safe(assignedTrailerId);
    }

    public void setEmergencyContactName(String name) {
        this.emergencyContactName = safe(name);
    }

    public void setEmergencyContactPhone(String phone) {
        this.emergencyContactPhone = safe(phone);
    }

    public void setEmergencyContactRelationship(String relation) {
        this.emergencyContactRelationship = safe(relation);
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = safe(driverLicenseNumber);
    }

    public void setLicenseClass(String licenseClass) {
        this.licenseClass = safe(licenseClass);
    }

    public void setLicenseExpirationDate(String licenseExpirationDate) {
        this.licenseExpirationDate = safe(licenseExpirationDate);
    }

    public void setEndorsements(ArrayList<String> endorsements) {
        this.endorsements = (endorsements == null) ? new ArrayList<>() : endorsements;
    }

    public void setDotPhysicalExpirationDate(String dotPhysicalExpirationDate) {
        this.dotPhysicalExpirationDate = safe(dotPhysicalExpirationDate);
    }

    public void setForkliftCertified(boolean forkliftCertified) {
        this.forkliftCertified = forkliftCertified;
    }

    public void setForkliftCertificationExpirationDate(String date) {
        this.forkliftCertificationExpirationDate = safe(date);
    }

    // =========================
    // EVALUATION METHODS
    // =========================
    public void addEvaluation(Evaluation evaluation) {
        if (evaluation != null) {
            evaluations.add(evaluation);
        }
    }

    public void displayAllEvaluations() {
        if (evaluations.isEmpty()) {
            System.out.println("No evaluations found for " + getFullName());
            return;
        }

        System.out.println("Evaluations for " + getFullName() + ":");
        for (Evaluation evaluation : evaluations) {
            if (evaluation != null) {
                evaluation.displayEvaluation();
            }
        }
    }

    public double getAverageEvaluationScore() {
        if (evaluations.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        int count = 0;

        for (Evaluation evaluation : evaluations) {
            if (evaluation != null) {
                total += evaluation.getOverallScore();
                count++;
            }
        }

        return count == 0 ? 0.0 : total / count;
    }

    public void displayEmployee() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + getFullName());
        System.out.println("Position: " + getPosition());
        System.out.println("Department: " + getDepartment());
        System.out.println("Address: " + getAddress());
        System.out.println("Phone: " + getPhoneNumber());
        System.out.println("Email: " + getEmail());
        System.out.println("Hire Date: " + getHireDate());
        System.out.println("Status: " + (active ? "Active" : "Inactive"));
        System.out.println("Pay Rate: $" + payRate);

        System.out.println("Assigned Truck: " +
                (getAssignedTruckId().isEmpty() ? "None" : getAssignedTruckId()));
        System.out.println("Assigned Trailer: " +
                (getAssignedTrailerId().isEmpty() ? "None" : getAssignedTrailerId()));

        System.out.println("Emergency Contact: " +
                (getEmergencyContactName().isEmpty() ? "None" : getEmergencyContactName()));
        System.out.println("Emergency Contact Phone: " +
                (getEmergencyContactPhone().isEmpty() ? "None" : getEmergencyContactPhone()));
        System.out.println("Emergency Contact Relationship: " +
                (getEmergencyContactRelationship().isEmpty() ? "None" : getEmergencyContactRelationship()));

        System.out.println("Driver License Number: " +
                (getDriverLicenseNumber().isEmpty() ? "N/A" : getDriverLicenseNumber()));
        System.out.println("License Class: " +
                (getLicenseClass().isEmpty() ? "N/A" : getLicenseClass()));
        System.out.println("License Expiration: " +
                (getLicenseExpirationDate().isEmpty() ? "N/A" : getLicenseExpirationDate()));
        System.out.println("Endorsements: " +
                (endorsements == null || endorsements.isEmpty() ? "N/A" : String.join(", ", endorsements)));
        System.out.println("DOT Physical Expiration: " +
                (getDotPhysicalExpirationDate().isEmpty() ? "N/A" : getDotPhysicalExpirationDate()));

        System.out.println("Forklift Certified: " + (forkliftCertified ? "Yes" : "No"));
        System.out.println("Forklift Certification Expiration: " +
                (getForkliftCertificationExpirationDate().isEmpty() ? "N/A" : getForkliftCertificationExpirationDate()));

        System.out.println("Average Evaluation Score: " + getAverageEvaluationScore());
        System.out.println("-----------------------------");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String toString() {
        return getFullName() + " - " + getPosition();
    }
}