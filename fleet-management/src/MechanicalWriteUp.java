public class MechanicalWriteUp {

    private int writeUpId;
    private int truckId;
    private String dateReported;
    private String reportedBy;
    private String category;
    private String severity;
    private String description;
    private boolean safeToDrive;
    private boolean outOfService;
    private String mechanicName;
    private String repairNotes;
    private String status;
    private double cost;

    public MechanicalWriteUp(int writeUpId, int truckId, String dateReported, String reportedBy,
                             String category, String severity, String description,
                             boolean safeToDrive, boolean outOfService,
                             String mechanicName, String repairNotes,
                             String status, double cost) {

        this.writeUpId = writeUpId;
        this.truckId = truckId;
        this.dateReported = dateReported;
        this.reportedBy = reportedBy;
        this.category = category;
        this.severity = severity;
        this.description = description;
        this.safeToDrive = safeToDrive;
        this.outOfService = outOfService;
        this.mechanicName = mechanicName;
        this.repairNotes = repairNotes;
        this.status = status;
        this.cost = cost;
    }

    public void displayWriteUp() {
        System.out.println("Write-Up ID: " + writeUpId);
        System.out.println("Truck ID: " + truckId);
        System.out.println("Date Reported: " + dateReported);
        System.out.println("Reported By: " + reportedBy);
        System.out.println("Category: " + category);
        System.out.println("Severity: " + severity);
        System.out.println("Description: " + description);
        System.out.println("Safe to Drive: " + safeToDrive);
        System.out.println("Out of Service: " + outOfService);
        System.out.println("Mechanic: " + mechanicName);
        System.out.println("Repair Notes: " + repairNotes);
        System.out.println("Status: " + status);
        System.out.println("Cost: $" + cost);
        System.out.println("---------------------------");
    }

    public int getTruckId() {
        return truckId;
    }

    public boolean isOutOfService() {
        return outOfService;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}