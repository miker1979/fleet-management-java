public class Trailer {

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
        this.assignedEmployeeName = "";
        this.notes = "";
    }

    // 🔹 Getters
    public String getTrailerId() { return trailerId; }
    public int getYear() { return year; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getTrailerType() { return trailerType; }
    public String getTrailerLength() { return trailerLength; }
    public String getVin() { return vin; }
    public String getTireSize() { return tireSize; }

    public String getStatus() { return status; }
    public boolean isDown() { return isDown; }
    public String getCurrentIssue() { return currentIssue; }
    public String getAssignedEmployeeName() { return assignedEmployeeName; }
    public String getNotes() { return notes; }

    // 🔹 Setters
    public void setStatus(String status) { this.status = status; }
    public void setAssignedEmployeeName(String name) { this.assignedEmployeeName = name; }
    public void setNotes(String notes) { this.notes = notes; }

    // 🔹 Maintenance logic
    public void setDown(boolean down, String issue) {
        this.isDown = down;
        this.currentIssue = issue;
        this.status = down ? "Down" : "Unused";
    }

    // 🔹 Dispatch helpers
    public void markInUse(String employeeName) {
        this.status = "In Use";
        this.assignedEmployeeName = employeeName;
    }

    public void markUnused() {
        this.status = "Unused";
        this.assignedEmployeeName = "";
    }

    public void markStored() {
        this.status = "Stored";
        this.assignedEmployeeName = "";
    }

    public void markOutOfService() {
        this.status = "Out of Service";
        this.isDown = true;
        this.assignedEmployeeName = "";
    }

    @Override
    public String toString() {
        return trailerId + " | " + trailerType + " | " + trailerLength;
    }
}