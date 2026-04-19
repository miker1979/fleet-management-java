import java.io.Serializable;

public class Forklift implements Serializable {
    private static final long serialVersionUID = 1L;

    private String unitId;

    // Basic Info
    private int year;
    private String make;
    private String model;
    private String vin;

    // Added equipment detail fields
    private String color;
    private String engineModel;
    private String engineType;
    private String tireSize;
    private int mileage;

    // Status
    private String status; // Unused, In Use, Stored, Down, Out of Service
    private boolean isDown;
    private String currentIssue;

    private String assignedOperator;
    private String notes;

    public Forklift(String unitId, int year, String make, String model, String vin,
                    String color, String engineModel, String engineType,
                    String tireSize, int mileage) {
        this.unitId = unitId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.vin = vin;
        this.color = color;
        this.engineModel = engineModel;
        this.engineType = engineType;
        this.tireSize = tireSize;
        this.mileage = mileage;

        this.status = "Unused";
        this.isDown = false;
        this.currentIssue = "Ready";
        this.assignedOperator = "";
        this.notes = "";
    }

    // Getters
    public String getUnitId() { return unitId; }
    public int getYear() { return year; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getVin() { return vin; }
    public String getColor() { return color; }
    public String getEngineModel() { return engineModel; }
    public String getEngineType() { return engineType; }
    public String getTireSize() { return tireSize; }
    public int getMileage() { return mileage; }

    public String getStatus() { return status; }
    public boolean isDown() { return isDown; }
    public String getCurrentIssue() { return currentIssue; }
    public String getAssignedOperator() { return assignedOperator; }
    public String getNotes() { return notes; }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedOperator(String operator) {
        this.assignedOperator = operator;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public void setDown(boolean down, String issue) {
        this.isDown = down;
        this.currentIssue = issue;

        if (down) {
            this.status = "Down";
        } else {
            this.status = "Unused";
        }
    }

    // Status helpers
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

    @Override
    public String toString() {
        return unitId + " (" + make + " " + model + ") - " + status;
    }
}