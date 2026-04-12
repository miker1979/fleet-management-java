public class Task {
    private int taskId;
    private int jobId; // 🔥 LINK TO JOB

    private String startDate;
    private String startTime;

    private String jobType;
    private String contractor;
    private String location;
    private String foreman;
    private String assignedTruck;
    private String status;
    private String notes;

    public Task(int taskId, int jobId, String startDate, String startTime, String jobType, String contractor,
                String location, String foreman, String assignedTruck, String status) {

        this.taskId = taskId;
        this.jobId = jobId; // 🔥 SET JOB LINK

        this.startDate = startDate;
        this.startTime = startTime;

        this.jobType = jobType;
        this.contractor = contractor;
        this.location = location;
        this.foreman = foreman;
        this.assignedTruck = assignedTruck;
        this.status = status;

        this.notes = "No field notes yet.";
    }

    // --- Getters ---
    public int getTaskId() { return taskId; }
    public int getJobId() { return jobId; } // 🔥 NEW

    public String getStartDate() { return startDate; }
    public String getStartTime() { return startTime; }

    public String getJobType() { return jobType; }
    public String getContractor() { return contractor; }
    public String getLocation() { return location; }
    public String getForeman() { return foreman; }
    public String getAssignedTruck() { return assignedTruck; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    // --- Setters ---
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setAssignedTruck(String assignedTruck) { this.assignedTruck = assignedTruck; }
    public void setForeman(String foreman) { this.foreman = foreman; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    // --- Debug Helper ---
    @Override
    public String toString() {
        return "Task #" + taskId +
               " | Job #" + jobId +
               " | " + contractor +
               " | " + location +
               " | " + startDate + " " + startTime;
    }
}