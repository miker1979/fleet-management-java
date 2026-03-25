public class Task {

    private int taskId;
    private int jobId;
    private String taskName;
    private String description;
    private String assignedEmployee;
    private String assignedTruck;
    private String priority;
    private String startDate;
    private String dueDate;
    private String status;
    private String notes;
    private String barrierType;
    private int linearFeetInstalled;
    private String workShift;

    public Task(int taskId, int jobId, String taskName, String description,
                String assignedEmployee, String assignedTruck, String priority,
                String startDate, String dueDate, String status, String notes,
                String barrierType, int linearFeetInstalled, String workShift) {

        this.taskId = taskId;
        this.jobId = jobId;
        this.taskName = taskName;
        this.description = description;
        this.assignedEmployee = assignedEmployee;
        this.assignedTruck = assignedTruck;
        this.priority = priority;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = status;
        this.notes = notes;
        this.barrierType = barrierType;
        this.linearFeetInstalled = linearFeetInstalled;
        this.workShift = workShift;
    }

    public void displayTask() {
        System.out.println("Task ID: " + taskId);
        System.out.println("Job ID: " + jobId);
        System.out.println("Task Name: " + taskName);
        System.out.println("Description: " + description);
        System.out.println("Assigned Employee: " + assignedEmployee);
        System.out.println("Assigned Truck: " + assignedTruck);
        System.out.println("Priority: " + priority);
        System.out.println("Start Date: " + startDate);
        System.out.println("Due Date: " + dueDate);
        System.out.println("Status: " + status);
        System.out.println("Notes: " + notes);
        System.out.println("Barrier Type: " + barrierType);
        System.out.println("Linear Feet Installed: " + linearFeetInstalled);
        System.out.println("Work Shift: " + workShift);
        System.out.println("---------------------------");
    }

    public int getTaskId() {
        return taskId;
    }

    public int getJobId() {
        return jobId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getAssignedTruck() {
        return assignedTruck;
    }

    public String getStatus() {
        return status;
    }

    public int getLinearFeetInstalled() {
        return linearFeetInstalled;
    }

    public String getBarrierType() {
        return barrierType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }
}