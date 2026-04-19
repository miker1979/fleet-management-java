import java.io.Serializable;

public class Truck implements Serializable {
    private static final long serialVersionUID = 1L;

    private String truckID;

    // Basic Info
    private int year;
    private String make;
    private String model;
    private String vin;
    private String color;

    // Mechanical Info
    private String engineModel;
    private String engineType;
    private String tireSize;
    private int mileage;

    // Status + Assignment
    private String status; // Unused, In Use, Stored, Down, Out of Service
    private boolean isDown;
    private String currentIssue;
    private String assignedEmployeeName;

    private String notes;

    // Existing constructor
    public Truck(String truckID, String model) {
        this.truckID = truckID;
        this.model = model;
        this.status = "Unused";
        this.isDown = false;
        this.currentIssue = "Ready";
        this.assignedEmployeeName = "";
        this.notes = "";
    }

    // Full constructor
    public Truck(String truckID, int year, String make, String model,
                 String vin, String color, String engineModel,
                 String engineType, String tireSize, int mileage) {

        this.truckID = truckID;
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
        this.assignedEmployeeName = "";
        this.notes = "";
    }

    // Getters
    public String getTruckID() { return truckID; }
    public String getModel() { return model; }
    public boolean isDown() { return isDown; }
    public String getCurrentIssue() { return currentIssue; }

    public int getYear() { return year; }
    public String getMake() { return make; }
    public String getVin() { return vin; }
    public String getColor() { return color; }
    public String getEngineModel() { return engineModel; }
    public String getEngineType() { return engineType; }
    public String getTireSize() { return tireSize; }
    public int getMileage() { return mileage; }

    public String getStatus() { return status; }
    public String getAssignedEmployeeName() { return assignedEmployeeName; }
    public String getNotes() { return notes; }

    // Compatibility helper
    public String getId() { return truckID; }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedEmployeeName(String name) {
        this.assignedEmployeeName = name;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Maintenance logic
    public void setDown(boolean down, String issue) {
        this.isDown = down;
        this.currentIssue = issue;

        if (down) {
            this.status = "Down";
        } else {
            this.status = "Unused";
        }
    }

    // Dispatch helpers
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
    }

    public void markOutOfService() {
        this.status = "Out of Service";
        this.isDown = true;
    }

    @Override
    public String toString() {
        return truckID + " (" + model + ") - " + status;
    }
}