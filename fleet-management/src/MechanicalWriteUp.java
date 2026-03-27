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

    public int getWriteUpId() {
        return writeUpId;
    }

    public int getTruckId() {
        return truckId;
    }

    public String getDateReported() {
        return dateReported;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public String getCategory() {
        return category;
    }

    public String getIssueType() {
        return category;
    }

    public String getSeverity() {
        return severity;
    }

    public String getPriority() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getProblemDescription() {
        return description;
    }

    public boolean isSafeToDrive() {
        return safeToDrive;
    }

    public boolean isOutOfService() {
        return outOfService;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public String getAssignedMechanic() {
        return mechanicName;
    }

    public String getRepairNotes() {
        return repairNotes;
    }

    public String getStatus() {
        return status;
    }

    public String getRepairStatus() {
        return status;
    }

    public double getCost() {
        return cost;
    }

    public double getEstimatedCost() {
        return cost;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRepairNotes(String repairNotes) {
        this.repairNotes = repairNotes;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public void setOutOfService(boolean outOfService) {
        this.outOfService = outOfService;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}