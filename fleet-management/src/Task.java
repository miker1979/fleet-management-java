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

    // Company employees assigned to this task
    private ArrayList<Integer> assignedEmployeeIds;

    // Extra dispatch support
    private ArrayList<String> assignedOwnerOperators;
    private ArrayList<String> requiredEquipment;
    private ArrayList<String> assignedForklifts;

    private String loadLocation;
    private String stagingLocation;
    private String dispatchInstructions;
    private String requiredEquipmentSummary;

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
        this.assignedOwnerOperators = new ArrayList<>();
        this.requiredEquipment = new ArrayList<>();
        this.assignedForklifts = new ArrayList<>();

        this.loadLocation = "";
        this.stagingLocation = "";
        this.dispatchInstructions = "";
        this.requiredEquipmentSummary = "";
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

    public ArrayList<String> getAssignedOwnerOperators() {
        return assignedOwnerOperators;
    }

    public ArrayList<String> getRequiredEquipment() {
        return requiredEquipment;
    }

    public ArrayList<String> getAssignedForklifts() {
        return assignedForklifts;
    }

    public String getLoadLocation() {
        return loadLocation;
    }

    public String getStagingLocation() {
        return stagingLocation;
    }

    public String getDispatchInstructions() {
        return dispatchInstructions;
    }

    public String getRequiredEquipmentSummary() {
        return requiredEquipmentSummary;
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

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public void setAssignedOwnerOperators(ArrayList<String> assignedOwnerOperators) {
        if (assignedOwnerOperators == null) {
            this.assignedOwnerOperators = new ArrayList<>();
        } else {
            this.assignedOwnerOperators = assignedOwnerOperators;
        }
    }

    public void setRequiredEquipment(ArrayList<String> requiredEquipment) {
        if (requiredEquipment == null) {
            this.requiredEquipment = new ArrayList<>();
        } else {
            this.requiredEquipment = requiredEquipment;
        }
    }

    public void setAssignedForklifts(ArrayList<String> assignedForklifts) {
        if (assignedForklifts == null) {
            this.assignedForklifts = new ArrayList<>();
        } else {
            this.assignedForklifts = assignedForklifts;
        }
    }

    public void setLoadLocation(String loadLocation) {
        this.loadLocation = loadLocation;
    }

    public void setStagingLocation(String stagingLocation) {
        this.stagingLocation = stagingLocation;
    }

    public void setDispatchInstructions(String dispatchInstructions) {
        this.dispatchInstructions = dispatchInstructions;
    }

    public void setRequiredEquipmentSummary(String requiredEquipmentSummary) {
        this.requiredEquipmentSummary = requiredEquipmentSummary;
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

    public void addOwnerOperator(String ownerOperatorName) {
        if (assignedOwnerOperators == null) {
            assignedOwnerOperators = new ArrayList<>();
        }

        if (ownerOperatorName != null
                && !ownerOperatorName.trim().isEmpty()
                && !assignedOwnerOperators.contains(ownerOperatorName)) {
            assignedOwnerOperators.add(ownerOperatorName);
        }
    }

    public void removeOwnerOperator(String ownerOperatorName) {
        if (assignedOwnerOperators != null) {
            assignedOwnerOperators.remove(ownerOperatorName);
        }
    }

    public void clearOwnerOperators() {
        if (assignedOwnerOperators != null) {
            assignedOwnerOperators.clear();
        }
    }

    public void addRequiredEquipment(String equipment) {
        if (requiredEquipment == null) {
            requiredEquipment = new ArrayList<>();
        }

        if (equipment != null
                && !equipment.trim().isEmpty()
                && !requiredEquipment.contains(equipment)) {
            requiredEquipment.add(equipment);
        }
    }

    public void removeRequiredEquipment(String equipment) {
        if (requiredEquipment != null) {
            requiredEquipment.remove(equipment);
        }
    }

    public void clearRequiredEquipment() {
        if (requiredEquipment != null) {
            requiredEquipment.clear();
        }
    }

    public void addAssignedForklift(String forkliftId) {
        if (assignedForklifts == null) {
            assignedForklifts = new ArrayList<>();
        }

        if (forkliftId != null
                && !forkliftId.trim().isEmpty()
                && !assignedForklifts.contains(forkliftId)) {
            assignedForklifts.add(forkliftId);
        }
    }

    public void removeAssignedForklift(String forkliftId) {
        if (assignedForklifts != null) {
            assignedForklifts.remove(forkliftId);
        }
    }

    public void clearAssignedForklifts() {
        if (assignedForklifts != null) {
            assignedForklifts.clear();
        }
    }

    public int getTotalAssignedPersonnelCount() {
        int companyDrivers = assignedEmployeeIds != null ? assignedEmployeeIds.size() : 0;
        int ownerOperators = assignedOwnerOperators != null ? assignedOwnerOperators.size() : 0;
        return companyDrivers + ownerOperators;
    }

    public String getDriverSummary() {
        int companyDrivers = assignedEmployeeIds != null ? assignedEmployeeIds.size() : 0;
        int ownerOperators = assignedOwnerOperators != null ? assignedOwnerOperators.size() : 0;

        return "Company Drivers: " + companyDrivers + " | Owner Operators: " + ownerOperators;
    }

    public String getEquipmentSummary() {
        int forkliftCount = assignedForklifts != null ? assignedForklifts.size() : 0;
        int requiredCount = requiredEquipment != null ? requiredEquipment.size() : 0;

        return "Assigned Forklifts: " + forkliftCount + " | Required Equipment Items: " + requiredCount;
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
               " | Foreman: " + foreman +
               " | Status: " + status +
               " | Company Drivers: " + (assignedEmployeeIds != null ? assignedEmployeeIds.size() : 0) +
               " | Owner Ops: " + (assignedOwnerOperators != null ? assignedOwnerOperators.size() : 0) +
               " | Forklifts: " + (assignedForklifts != null ? assignedForklifts.size() : 0);
    }
}