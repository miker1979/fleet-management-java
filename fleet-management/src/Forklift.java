public class Forklift {

    private String unitId;

    // Basic Info
    private int year;
    private String make;
    private String model;
    private String vin;

    // Status
    private String status; // Unused, In Use, Stored, Down, Out of Service
    private boolean isDown;
    private String currentIssue;

    private String assignedOperator;
    private String notes;

    // 🔹 Constructor
    public Forklift(String unitId, int year, String make, String model, String vin) {
        this.unitId = unitId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.vin = vin;

        this.status = "Unused";
        this.isDown = false;
        this.currentIssue = "Ready";
        this.assignedOperator = "";
        this.notes = "";
    }

    // 🔹 Getters
    public String getUnitId() { return unitId; }
    public int getYear() { return year; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getVin() { return vin; }

    public String getStatus() { return status; }
    public boolean isDown() { return isDown; }
    public String getCurrentIssue() { return currentIssue; }
    public String getAssignedOperator() { return assignedOperator; }
    public String getNotes() { return notes; }

    // 🔹 Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedOperator(String operator) {
        this.assignedOperator = operator;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // 🔧 Maintenance logic (same idea as Truck)
    public void setDown(boolean down, String issue) {
        this.isDown = down;
        this.currentIssue = issue;

        if (down) {
            this.status = "Down";
        } else {
            this.status = "Unused";
        }
    }

    // 🔹 Status helpers
    public void markInUse(String operator) {
        this.status = "In Use";
        this.assignedOperator = operator;
    }

    public void markUnused() {
        this.status = "Unused";
        this.assignedOperator = "";
    }

    public void markStored() {
        this.status = "Stored";
    }

    public void markOutOfService() {
        this.status = "Out of Service";
        this.isDown = true;
    }

    // 🔹 Display
    @Override
    public String toString() {
        return unitId + " (" + make + " " + model + ") - " + status;
    }
}