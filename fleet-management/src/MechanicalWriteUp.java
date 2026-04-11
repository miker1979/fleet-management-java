public class MechanicalWriteUp {
    private int writeUpId;
    private String truckId;
    private String dateReported;
    private String reportedBy;
    private String issueType;
    private String priority;
    private String problemDescription;
    private boolean safeToDrive;
    private boolean outOfService;
    private String assignedMechanic;
    private String repairNotes;
    private String repairStatus;
    private double estimatedCost;

    public MechanicalWriteUp(int writeUpId, String truckId, String dateReported,
                             String reportedBy, String issueType, String priority,
                             String problemDescription, boolean safeToDrive,
                             boolean outOfService, String assignedMechanic,
                             String repairNotes, String repairStatus, double estimatedCost) {
        this.writeUpId = writeUpId;
        this.truckId = truckId;
        this.dateReported = dateReported;
        this.reportedBy = reportedBy;
        this.issueType = issueType;
        this.priority = priority;
        this.problemDescription = problemDescription;
        this.safeToDrive = safeToDrive;
        this.outOfService = outOfService;
        this.assignedMechanic = assignedMechanic;
        this.repairNotes = repairNotes;
        this.repairStatus = repairStatus;
        this.estimatedCost = estimatedCost;
    }

    public int getWriteUpId() {
        return writeUpId;
    }

    public String getTruckId() {
        return truckId;
    }

    public String getDateReported() {
        return dateReported;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getPriority() {
        return priority;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public boolean isSafeToDrive() {
        return safeToDrive;
    }

    public boolean isOutOfService() {
        return outOfService;
    }

    public String getAssignedMechanic() {
        return assignedMechanic;
    }

    public String getRepairNotes() {
        return repairNotes;
    }

    public String getRepairStatus() {
        return repairStatus;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setRepairStatus(String repairStatus) {
        this.repairStatus = repairStatus;
    }

    public void setRepairNotes(String repairNotes) {
        this.repairNotes = repairNotes;
    }
}