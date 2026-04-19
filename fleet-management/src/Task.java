import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private int taskId;
    private int jobId;

    private String startDate;
    private String startTime;
    private String endTime;

    private String jobType;
    private String contractor;
    private String location;
    private String foreman;
    private String status;
    private String notes;
    private int linearFeet;

    private TCB tcb;
    private TIA tia;

    // Company-level dispatch: store assigned employees by ID.
    // Truck/trailer follow automatically from Employee roster.
    private ArrayList<Integer> assignedEmployeeIds;

    public Task(int taskId, int jobId, String startDate, String startTime, String endTime,
                String jobType, String contractor, String location, String foreman,
                String status, int linearFeet, TCB tcb, TIA tia) {

        this.taskId = taskId;
        this.jobId = jobId;

        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;

        this.jobType = jobType;
        this.contractor = contractor;
        this.location = location;
        this.foreman = foreman;
        this.status = status;
        this.linearFeet = linearFeet;

        this.tcb = tcb;
        this.tia = tia;

        this.notes = "No field notes yet.";
        this.assignedEmployeeIds = new ArrayList<>();
    }

    public int getTaskId() {
        return taskId;
    }

    public int getJobId() {
        return jobId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getJobType() {
        return jobType;
    }

    public String getContractor() {
        return contractor;
    }

    public String getLocation() {
        return location;
    }

    public String getForeman() {
        return foreman;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public int getLinearFeet() {
        return linearFeet;
    }

    public TCB getTcb() {
        return tcb;
    }

    public TIA getTia() {
        return tia;
    }

    public ArrayList<Integer> getAssignedEmployeeIds() {
        return assignedEmployeeIds;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setForeman(String foreman) {
        this.foreman = foreman;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setLinearFeet(int linearFeet) {
        this.linearFeet = linearFeet;
    }

    public void setTcb(TCB tcb) {
        this.tcb = tcb;
    }

    public void setTia(TIA tia) {
        this.tia = tia;
    }

    public void setAssignedEmployeeIds(ArrayList<Integer> assignedEmployeeIds) {
        if (assignedEmployeeIds == null) {
            this.assignedEmployeeIds = new ArrayList<>();
        } else {
            this.assignedEmployeeIds = assignedEmployeeIds;
        }
    }

    public void addAssignedEmployeeId(int employeeId) {
        if (assignedEmployeeIds == null) {
            assignedEmployeeIds = new ArrayList<>();
        }

        if (!assignedEmployeeIds.contains(employeeId)) {
            assignedEmployeeIds.add(employeeId);
        }
    }

    public void removeAssignedEmployeeId(int employeeId) {
        if (assignedEmployeeIds != null) {
            assignedEmployeeIds.remove(Integer.valueOf(employeeId));
        }
    }

    public void clearAssignedEmployees() {
        if (assignedEmployeeIds != null) {
            assignedEmployeeIds.clear();
        }
    }

    public boolean isEmployeeAssigned(int employeeId) {
        return assignedEmployeeIds != null && assignedEmployeeIds.contains(employeeId);
    }

    @Override
    public String toString() {
        return "Task #" + taskId +
               " | Job #" + jobId +
               " | " + contractor +
               " | " + location +
               " | " + startDate + " " + startTime + "-" + endTime +
               " | Type: " + jobType +
               " | LF: " + linearFeet +
               " | TCB: " + (tcb != null ? tcb.toString() : "None") +
               " | TIA: " + (tia != null ? tia.toString() : "None") +
               " | Assigned Employees: " + (assignedEmployeeIds != null ? assignedEmployeeIds.size() : 0);
    }
}