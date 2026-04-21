import java.io.Serializable;

public class WorkTruck implements Serializable {
    private static final long serialVersionUID = 1L;

    private String unitId;

    // Basic Info
    private int year;
    private String make;
    private String model;
    private String vin;
    private String color;

    // Work Truck Specific
    private String bedType;     // Standard, Flatbed, Service Body
    private boolean hasToolbox;
    private boolean hasRack;

    // Status + Assignment
    private String status; // Unused, In Use, Stored, Down, Out of Service
    private boolean isDown;
    private String currentIssue;
    private String assignedEmployeeName;

    private String notes;

    public WorkTruck(String unitId, int year, String make, String model,
                     String vin, String color, String bedType) {

        this.unitId = unitId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.vin = vin;
        this.color = color;
        this.bedType = bedType;

        this.hasToolbox = false;
        this.hasRack = false;

        this.status = "Unused";
        this.isDown = false;
        this.currentIssue = "Ready";
        this.assignedEmployeeName = "";
        this.notes = "";
    }

    // Getters & Setters

    public String getUnitId() { return unitId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }

    public boolean isHasToolbox() { return hasToolbox; }
    public void setHasToolbox(boolean hasToolbox) { this.hasToolbox = hasToolbox; }

    public boolean isHasRack() { return hasRack; }
    public void setHasRack(boolean hasRack) { this.hasRack = hasRack; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isDown() { return isDown; }
    public void setDown(boolean down) { isDown = down; }

    public String getCurrentIssue() { return currentIssue; }
    public void setCurrentIssue(String currentIssue) { this.currentIssue = currentIssue; }

    public String getAssignedEmployeeName() { return assignedEmployeeName; }
    public void setAssignedEmployeeName(String name) { this.assignedEmployeeName = name; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}