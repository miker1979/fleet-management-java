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

    // Assignment tracking
    private int assignedEmployeeId;
    private String assignedEmployeeName;

    private String notes;

    // Existing/simple constructor
    public Truck(String truckID, String model) {
        this.truckID = truckID;
        this.model = model;

        this.year = 0;
        this.make = "";
        this.vin = "";
        this.color = "";
        this.engineModel = "";
        this.engineType = "";
        this.tireSize = "";
        this.mileage = 0;

        this.status = "Unused";
        this.isDown = false;
        this.currentIssue = "Ready";
        this.assignedEmployeeId = 0;
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
        this.assignedEmployeeId = 0;
        this.assignedEmployeeName = "";
        this.notes = "";
    }

    // Getters
    public String getTruckID() {
        return truckID;
    }

    public String getModel() {
        return model;
    }

    public boolean isDown() {
        return isDown;
    }

    public String getCurrentIssue() {
        return currentIssue;
    }

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getVin() {
        return vin;
    }

    public String getColor() {
        return color;
    }

    public String getEngineModel() {
        return engineModel;
    }

    public String getEngineType() {
        return engineType;
    }

    public String getTireSize() {
        return tireSize;
    }

    public int getMileage() {
        return mileage;
    }

    public String getStatus() {
        return status;
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

    // Compatibility helper
    public String getId() {
        return truckID;
    }

    // Setters
    public void setTruckID(String truckID) {
        this.truckID = truckID;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setEngineModel(String engineModel) {
        this.engineModel = engineModel;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public void setTireSize(String tireSize) {
        this.tireSize = tireSize;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedEmployeeId(int assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public void setAssignedEmployeeName(String assignedEmployeeName) {
        this.assignedEmployeeName = assignedEmployeeName == null ? "" : assignedEmployeeName;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Maintenance logic
    public void setDown(boolean down, String issue) {
        this.isDown = down;
        this.currentIssue = (issue == null || issue.isBlank()) ? "Ready" : issue;

        if (down) {
            this.status = "Down";
        } else if (assignedEmployeeId > 0 || (assignedEmployeeName != null && !assignedEmployeeName.isBlank())) {
            this.status = "In Use";
        } else {
            this.status = "Unused";
        }
    }

    // Assignment helpers
    public void assignDriver(int employeeId, String employeeName) {
        this.assignedEmployeeId = employeeId;
        this.assignedEmployeeName = employeeName == null ? "" : employeeName;

        if (!isDown) {
            this.status = "In Use";
        }
    }

    public void clearAssignment() {
        this.assignedEmployeeId = 0;
        this.assignedEmployeeName = "";

        if (!isDown) {
            this.status = "Unused";
        }
    }

    // Dispatch helpers
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
    }

    public void markOutOfService() {
        this.status = "Out of Service";
        this.isDown = true;
    }

    @Override
    public String toString() {
        String makeModel = ((make == null ? "" : make) + " " + (model == null ? "" : model)).trim();
        if (makeModel.isEmpty()) {
            makeModel = model == null ? "" : model;
        }
        return truckID + " (" + makeModel + ") - " + status;
    }
}