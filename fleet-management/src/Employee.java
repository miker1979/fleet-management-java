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
        System.out.println("Average Evaluation Score: " + getAverageEvaluationScore());
        System.out.println("-----------------------------");
    }
}