import java.io.Serializable;

public class Trailer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String trailerId;

    // Basic Info
    private int year;
    private String make;
    private String model;
    private String trailerType;   // Dry Van, Reefer, Flatbed, Step Deck
    private String trailerLength; // 48', 53'
    private String vin;
    private String tireSize;

    // Status + Assignment
    private String status; // Unused, In Use, Stored, Down, Out of Service
    private boolean isDown;
    private String currentIssue;
    private int assignedEmployeeId;
    private String assignedEmployeeName;

    private String notes;

    public Trailer(String trailerId, int year, String make, String model,
                   String trailerType, String trailerLength,
                   String vin, String tireSize) {

        this.trailerId = trailerId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.trailerType = trailerType;
        this.trailerLength = trailerLength;
        this.vin = vin;
        this.tireSize = tireSize;

        this.status = "Unused";
        this.isDown = false;
        this.currentIssue = "Ready";
        this.assignedEmployeeId = 0;
        this.assignedEmployeeName = "";
        this.notes = "";
    }

    public String getTrailerId() {
        return trailerId;
    }

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getTrailerType() {
        return trailerType;
    }

    public String getTrailerLength() {
        return trailerLength;
    }

    public String getVin() {
        return vin;
    }

    public String getTireSize() {
        return tireSize;
    }

    public String getStatus() {
        return status;
    }

    public boolean isDown() {
        return isDown;
    }

    public String getCurrentIssue() {
        return currentIssue;
    }

    public int getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public String getAssignedEmployeeName() {
        return assignedEmployeeName;
    }

    public String getNotes() {
        return notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedEmployeeName(String name) {
        this.assignedEmployeeName = name == null ? "" : name;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDown(boolean down, String issue) {
        this.isDown = down;
        this.currentIssue = issue == null ? "" : issue;

        if (down) {
            this.status = "Down";
            this.assignedEmployeeId = 0;
            this.assignedEmployeeName = "";
        } else {
            this.status = "Unused";
            if (this.currentIssue.isBlank()) {
                this.currentIssue = "Ready";
            }
        }
    }

    public void assignDriver(int employeeId, String employeeName) {
        this.assignedEmployeeId = employeeId;
        this.assignedEmployeeName = employeeName == null ? "" : employeeName;
        this.status = "In Use";
    }

    public void clearAssignment() {
        this.assignedEmployeeId = 0;
        this.assignedEmployeeName = "";

        if (!isDown) {
            this.status = "Unused";
        }
    }

    public void markInUse(String employeeName) {
        this.status = "In Use";
        this.assignedEmployeeName = employeeName == null ? "" : employeeName;
    }

    public void markUnused() {
        this.status = "Unused";
        this.assignedEmployeeId = 0;
        this.assignedEmployeeName = "";
    }

    public void markStored() {
        this.status = "Stored";
        this.assignedEmployeeId = 0;
        this.assignedEmployeeName = "";
    }

    public void markOutOfService() {
        this.status = "Out of Service";
        this.isDown = true;
        this.currentIssue = "Out of Service";
        this.assignedEmployeeId = 0;
        this.assignedEmployeeName = "";
    }

    @Override
    public String toString() {
        return trailerId + " | " + trailerType + " | " + trailerLength + " | " + status;
    }
}