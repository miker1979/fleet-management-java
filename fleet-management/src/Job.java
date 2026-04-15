public class Job {

    // 🔹 CORE JOB BOARD FIELDS (PRIMARY)
    private int jobNumber;
    private String contractor;
    private String projectName;
    private String startDate;
    private String endDate;
    private String location;

    // 🔹 OPTIONAL / FUTURE DETAIL FIELDS
    private String status;
    private String projectManager;
    private String dotProjectNumber;
    private String barrierType;
    private int totalLinearFeet;
    private String notes;

    // 🔥 CLEAN PRIMARY CONSTRUCTOR (Job Board Focused)
    public Job(int jobNumber, String contractor, String projectName,
               String startDate, String endDate, String location) {

        this.jobNumber = jobNumber;
        this.contractor = contractor;
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;

        // defaults
        this.status = "Active";
        this.projectManager = "";
        this.dotProjectNumber = "";
        this.barrierType = "";
        this.totalLinearFeet = 0;
        this.notes = "";
    }

    // 🔹 GETTERS (Core)
    public int getJobNumber() { return jobNumber; }
    public String getContractor() { return contractor; }
    public String getProjectName() { return projectName; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getLocation() { return location; }

    // 🔹 GETTERS (Optional)
    public String getStatus() { return status; }
    public String getProjectManager() { return projectManager; }
    public String getDotProjectNumber() { return dotProjectNumber; }
    public String getBarrierType() { return barrierType; }
    public int getTotalLinearFeet() { return totalLinearFeet; }
    public String getNotes() { return notes; }

    // 🔹 SETTERS (Core editable)
    public void setContractor(String contractor) { this.contractor = contractor; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setLocation(String location) { this.location = location; }

    // 🔹 SETTERS (Optional)
    public void setStatus(String status) { this.status = status; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }
    public void setDotProjectNumber(String dotProjectNumber) { this.dotProjectNumber = dotProjectNumber; }
    public void setBarrierType(String barrierType) { this.barrierType = barrierType; }
    public void setTotalLinearFeet(int totalLinearFeet) { this.totalLinearFeet = totalLinearFeet; }
    public void setNotes(String notes) { this.notes = notes; }

    // 🔥 CLEAN DISPLAY (Job Board Style)
    public void displayJob() {
        System.out.println("Job #: " + jobNumber);
        System.out.println("Contractor: " + contractor);
        System.out.println("Project: " + projectName);
        System.out.println("Start: " + startDate + " | End: " + endDate);
        System.out.println("Location: " + location);
        System.out.println("---------------------------");
    }

    @Override
    public String toString() {
        return jobNumber + " | " + projectName + " | " + contractor;
    }
}