public class Task {

    private int taskId;
    private int jobId;
    private int employeeId; // Added for ID-based filtering
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

    public Task(int taskId, int jobId, int employeeId, String taskName, String description,
                String assignedEmployee, String assignedTruck, String priority,
                String startDate, String dueDate, String status, String notes,
                String barrierType, int linearFeetInstalled, String workShift) {

        this.taskId = taskId;
        this.jobId = jobId;
        this.employeeId = employeeId; // Assigned here
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

    // Overloaded constructor for JobScreenUI
    public Task(String taskName, String status, int linearFeetInstalled,
                String contractor, String location, Truck assignedTruck) {

        this.taskId = 0;
        this.jobId = 0;
        this.employeeId = 0; // Default to 0 for unassigned
        this.taskName = taskName;
        this.description = "Contractor: " + contractor + " | Location: " + location;
        this.assignedEmployee = "Unassigned";
        this.assignedTruck = assignedTruck.toString();
        this.priority = "Normal";
        this.startDate = "TBD";
        this.dueDate = "TBD";
        this.status = status;
        this.notes = "";
        this.barrierType = "Unknown";
        this.linearFeetInstalled = linearFeetInstalled;
        this.workShift = "Day";
    }

    public void displayTask() {
        System.out.println("Task ID: " + taskId);
        System.out.println("Job ID: " + jobId);
        System.out.println("Employee ID: " + employeeId);
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

    // Added getter to fix UI error
    public int getEmployeeId() {
        return employeeId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignedEmployee() {
        return assignedEmployee;
    }

    public String getAssignedTruck() {
        return assignedTruck;
    }

    public String getPriority() {
        return priority;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getBarrierType() {
        return barrierType;
    }

    public int getLinearFeetInstalled() {
        return linearFeetInstalled;
    }

    public String getWorkShift() {
        return workShift;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }
}