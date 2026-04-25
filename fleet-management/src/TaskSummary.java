import java.io.Serializable;

public class TaskSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private int taskId;
    private String jobNumber;

    private String foremanName;
    private String scheduledDate;
    private String scheduledStartTime;
    private String actualStartTime;
    private String finishTime;
    private String location;
    private String jobType;

    private int barrierCount;
    private int absorbSetCount;
    private String absorbStyle;
    private String absorbLength;

    private int workerCount;
    private double hoursWorked;

    private String driversOnJob;
    private String forkliftStatus;

    private String delayNotes;
    private String breakdownNotes;
    private String equipmentIssueNotes;
    private String materialIssueNotes;
    private String safetyNotes;
    private String finalSummary;

    public TaskSummary(int taskId, String jobNumber) {
        this.taskId = taskId;
        this.jobNumber = jobNumber;

        this.foremanName = "";
        this.scheduledDate = "";
        this.scheduledStartTime = "";
        this.actualStartTime = "";
        this.finishTime = "";
        this.location = "";
        this.jobType = "";

        this.barrierCount = 0;
        this.absorbSetCount = 0;
        this.absorbStyle = "";
        this.absorbLength = "";

        this.workerCount = 0;
        this.hoursWorked = 0.0;

        this.driversOnJob = "";
        this.forkliftStatus = "";

        this.delayNotes = "";
        this.breakdownNotes = "";
        this.equipmentIssueNotes = "";
        this.materialIssueNotes = "";
        this.safetyNotes = "";
        this.finalSummary = "";
    }

    public double getPersonHours() {
        return workerCount * hoursWorked;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public int getJobId() {
    try {
        return Integer.parseInt(jobNumber);
    } catch (Exception e) {
        return 0; }
    }

    public String getForemanName() {
        return foremanName;
    }

    public void setForemanName(String foremanName) {
        this.foremanName = foremanName;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(String scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public String getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(String actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public int getBarrierCount() {
        return barrierCount;
    }

    public void setBarrierCount(int barrierCount) {
        this.barrierCount = barrierCount;
    }

    public int getAbsorbSetCount() {
        return absorbSetCount;
    }

    public void setAbsorbSetCount(int absorbSetCount) {
        this.absorbSetCount = absorbSetCount;
    }

    public String getAbsorbStyle() {
        return absorbStyle;
    }

    public void setAbsorbStyle(String absorbStyle) {
        this.absorbStyle = absorbStyle;
    }

    public String getAbsorbLength() {
        return absorbLength;
    }

    public void setAbsorbLength(String absorbLength) {
        this.absorbLength = absorbLength;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getDriversOnJob() {
        return driversOnJob;
    }

    public void setDriversOnJob(String driversOnJob) {
        this.driversOnJob = driversOnJob;
    }

    public String getForkliftStatus() {
        return forkliftStatus;
    }

    public void setForkliftStatus(String forkliftStatus) {
        this.forkliftStatus = forkliftStatus;
    }

    public String getDelayNotes() {
        return delayNotes;
    }

    public void setDelayNotes(String delayNotes) {
        this.delayNotes = delayNotes;
    }

    public String getBreakdownNotes() {
        return breakdownNotes;
    }

    public void setBreakdownNotes(String breakdownNotes) {
        this.breakdownNotes = breakdownNotes;
    }

    public String getEquipmentIssueNotes() {
        return equipmentIssueNotes;
    }

    public void setEquipmentIssueNotes(String equipmentIssueNotes) {
        this.equipmentIssueNotes = equipmentIssueNotes;
    }

    public String getMaterialIssueNotes() {
        return materialIssueNotes;
    }

    public void setMaterialIssueNotes(String materialIssueNotes) {
        this.materialIssueNotes = materialIssueNotes;
    }

    public String getSafetyNotes() {
        return safetyNotes;
    }

    public void setSafetyNotes(String safetyNotes) {
        this.safetyNotes = safetyNotes;
    }

    public String getFinalSummary() {
        return finalSummary;
    }

    public void setFinalSummary(String finalSummary) {
        this.finalSummary = finalSummary;
    }
}