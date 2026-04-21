import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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

    // Core crew
    private ArrayList<Integer> assignedEmployeeIds;
    private ArrayList<String> assignedOwnerOperators;

    // Equipment
    private ArrayList<String> assignedForklifts;
    private ArrayList<String> requiredEquipment;

    // Driver -> equipment responsibility map
    private HashMap<Integer, ArrayList<String>> driverEquipmentMap;

    // Absorber/support assignments
    private ArrayList<AbsorberAssignment> absorberAssignments;

    // Dispatch details
    private String loadLocation;
    private String stagingLocation;
    private String dispatchInstructions;
    private String requiredEquipmentSummary;

    public Task(int taskId, int jobId, String startDate, String startTime, String endTime,
                String jobType, String contractor, String location, String foreman,
                String status, int linearFeet, TCB tcb, TIA tia) {

        this.taskId = taskId;
        this.jobId = jobId;
        this.startDate = safe(startDate);
        this.startTime = safe(startTime);
        this.endTime = safe(endTime);
        this.jobType = safe(jobType);
        this.contractor = safe(contractor);
        this.location = safe(location);
        this.foreman = safe(foreman);
        this.status = safe(status);
        this.linearFeet = linearFeet;
        this.tcb = tcb;
        this.tia = tia;

        this.notes = "";
        this.assignedEmployeeIds = new ArrayList<>();
        this.assignedOwnerOperators = new ArrayList<>();
        this.assignedForklifts = new ArrayList<>();
        this.requiredEquipment = new ArrayList<>();
        this.driverEquipmentMap = new HashMap<>();
        this.absorberAssignments = new ArrayList<>();

        this.loadLocation = "";
        this.stagingLocation = "";
        this.dispatchInstructions = "";
        this.requiredEquipmentSummary = "";
    }

    // ================= GETTERS =================

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

    public ArrayList<String> getAssignedForklifts() {
        return assignedForklifts;
    }

    public ArrayList<String> getRequiredEquipment() {
        return requiredEquipment;
    }

    public HashMap<Integer, ArrayList<String>> getDriverEquipmentMap() {
        return driverEquipmentMap;
    }

    public ArrayList<String> getEquipmentForDriver(int employeeId) {
        return driverEquipmentMap.getOrDefault(employeeId, new ArrayList<>());
    }

    public ArrayList<AbsorberAssignment> getAbsorberAssignments() {
        return absorberAssignments;
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

    // ================= SETTERS =================

    public void setStartDate(String startDate) {
        this.startDate = safe(startDate);
    }

    public void setStartTime(String startTime) {
        this.startTime = safe(startTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = safe(endTime);
    }

    public void setJobType(String jobType) {
        this.jobType = safe(jobType);
    }

    public void setContractor(String contractor) {
        this.contractor = safe(contractor);
    }

    public void setLocation(String location) {
        this.location = safe(location);
    }

    public void setForeman(String foreman) {
        this.foreman = safe(foreman);
    }

    public void setStatus(String status) {
        this.status = safe(status);
    }

    public void setNotes(String notes) {
        this.notes = safe(notes);
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
        this.assignedEmployeeIds = assignedEmployeeIds == null
                ? new ArrayList<>()
                : new ArrayList<>(assignedEmployeeIds);
    }

    public void setAssignedOwnerOperators(ArrayList<String> assignedOwnerOperators) {
        this.assignedOwnerOperators = assignedOwnerOperators == null
                ? new ArrayList<>()
                : new ArrayList<>(assignedOwnerOperators);
    }

    public void setAssignedForklifts(ArrayList<String> assignedForklifts) {
        this.assignedForklifts = assignedForklifts == null
                ? new ArrayList<>()
                : new ArrayList<>(assignedForklifts);
    }

    public void setRequiredEquipment(ArrayList<String> requiredEquipment) {
        this.requiredEquipment = requiredEquipment == null
                ? new ArrayList<>()
                : new ArrayList<>(requiredEquipment);
    }

    public void setDriverEquipmentMap(HashMap<Integer, ArrayList<String>> driverEquipmentMap) {
        if (driverEquipmentMap == null) {
            this.driverEquipmentMap = new HashMap<>();
            return;
        }

        this.driverEquipmentMap = new HashMap<>();
        for (Integer key : driverEquipmentMap.keySet()) {
            ArrayList<String> value = driverEquipmentMap.get(key);
            this.driverEquipmentMap.put(key, value == null ? new ArrayList<>() : new ArrayList<>(value));
        }
    }

    public void setAbsorberAssignments(ArrayList<AbsorberAssignment> absorberAssignments) {
        this.absorberAssignments = absorberAssignments == null
                ? new ArrayList<>()
                : new ArrayList<>(absorberAssignments);
    }

    public void setLoadLocation(String loadLocation) {
        this.loadLocation = safe(loadLocation);
    }

    public void setStagingLocation(String stagingLocation) {
        this.stagingLocation = safe(stagingLocation);
    }

    public void setDispatchInstructions(String dispatchInstructions) {
        this.dispatchInstructions = safe(dispatchInstructions);
    }

    public void setRequiredEquipmentSummary(String requiredEquipmentSummary) {
        this.requiredEquipmentSummary = safe(requiredEquipmentSummary);
    }

    // ================= CREW HELPERS =================

    public void addAssignedEmployeeId(int employeeId) {
        if (!assignedEmployeeIds.contains(employeeId)) {
            assignedEmployeeIds.add(employeeId);
        }
    }

    public void removeAssignedEmployeeId(int employeeId) {
        assignedEmployeeIds.remove(Integer.valueOf(employeeId));
        driverEquipmentMap.remove(employeeId);
    }

    public void clearAssignedEmployees() {
        assignedEmployeeIds.clear();
        driverEquipmentMap.clear();
    }

    public boolean isEmployeeAssigned(int employeeId) {
        return assignedEmployeeIds.contains(employeeId);
    }

    public void addOwnerOperator(String ownerOperatorName) {
        String value = safe(ownerOperatorName).trim();
        if (!value.isEmpty() && !assignedOwnerOperators.contains(value)) {
            assignedOwnerOperators.add(value);
        }
    }

    public void removeOwnerOperator(String ownerOperatorName) {
        assignedOwnerOperators.remove(ownerOperatorName);
    }

    public void clearOwnerOperators() {
        assignedOwnerOperators.clear();
    }

    // ================= EQUIPMENT HELPERS =================

    public void addRequiredEquipment(String equipment) {
        String value = safe(equipment).trim();
        if (!value.isEmpty() && !requiredEquipment.contains(value)) {
            requiredEquipment.add(value);
        }
    }

    public void removeRequiredEquipment(String equipment) {
        requiredEquipment.remove(equipment);
    }

    public void clearRequiredEquipment() {
        requiredEquipment.clear();
    }

    public void addAssignedForklift(String forkliftId) {
        String value = safe(forkliftId).trim();
        if (!value.isEmpty() && !assignedForklifts.contains(value)) {
            assignedForklifts.add(value);
        }
    }

    public void removeAssignedForklift(String forkliftId) {
        assignedForklifts.remove(forkliftId);
    }

    public void clearAssignedForklifts() {
        assignedForklifts.clear();
    }

    public void assignEquipmentToDriver(int employeeId, String equipment) {
        String value = safe(equipment).trim();
        if (value.isEmpty()) {
            return;
        }

        driverEquipmentMap.putIfAbsent(employeeId, new ArrayList<>());
        ArrayList<String> items = driverEquipmentMap.get(employeeId);

        if (!items.contains(value)) {
            items.add(value);
        }
    }

    public void removeEquipmentFromDriver(int employeeId, String equipment) {
        ArrayList<String> items = driverEquipmentMap.get(employeeId);
        if (items == null) {
            return;
        }

        items.remove(equipment);

        if (items.isEmpty()) {
            driverEquipmentMap.remove(employeeId);
        }
    }

    public void clearDriverEquipment() {
        driverEquipmentMap.clear();
    }

    public void addAbsorberAssignment(String setName, int employeeId, String origin) {
        String cleanSetName = safe(setName).trim();
        String cleanOrigin = safe(origin).trim();

        if (cleanSetName.isEmpty()) {
            return;
        }

        absorberAssignments.add(new AbsorberAssignment(cleanSetName, employeeId, cleanOrigin));
    }

    public void clearAbsorberAssignments() {
        absorberAssignments.clear();
    }

    // ================= SUMMARY METHODS =================

    public int getTotalAssignedPersonnelCount() {
        return assignedEmployeeIds.size() + assignedOwnerOperators.size();
    }

    public String getDriverSummary() {
        return "Company Drivers: " + assignedEmployeeIds.size()
                + " | Owner Operators: " + assignedOwnerOperators.size();
    }

    public String getEquipmentSummary() {
        return "Assigned Forklifts: " + assignedForklifts.size()
                + " | Required Equipment Items: " + requiredEquipment.size()
                + " | Absorber Assignments: " + absorberAssignments.size();
    }

    public String getDispatchSummary() {
        StringBuilder sb = new StringBuilder();

        sb.append("Foreman: ").append(safe(foreman).isEmpty() ? "Unassigned" : foreman).append("\n");
        sb.append("Status: ").append(safe(status)).append("\n");

        if (!safe(loadLocation).isEmpty()) {
            sb.append("Load From: ").append(loadLocation).append("\n");
        }
        if (!safe(stagingLocation).isEmpty()) {
            sb.append("Stage At: ").append(stagingLocation).append("\n");
        }
        if (!safe(dispatchInstructions).isEmpty()) {
            sb.append("Instructions: ").append(dispatchInstructions).append("\n");
        }

        sb.append("\nCrew:\n");
        if (assignedEmployeeIds.isEmpty() && assignedOwnerOperators.isEmpty()) {
            sb.append("- No crew assigned\n");
        } else {
            for (Integer employeeId : assignedEmployeeIds) {
                sb.append("- Employee ID ").append(employeeId);
                ArrayList<String> equipment = driverEquipmentMap.get(employeeId);
                if (equipment != null && !equipment.isEmpty()) {
                    sb.append(" -> ").append(String.join(", ", equipment));
                }
                sb.append("\n");
            }

            for (String ownerOperator : assignedOwnerOperators) {
                sb.append("- Owner Operator: ").append(ownerOperator).append("\n");
            }
        }

        sb.append("\nForklifts:\n");
        if (assignedForklifts.isEmpty()) {
            sb.append("- None\n");
        } else {
            for (String forkliftId : assignedForklifts) {
                sb.append("- ").append(forkliftId).append("\n");
            }
        }

        sb.append("\nAbsorber Sets:\n");
        if (absorberAssignments.isEmpty()) {
            sb.append("- None\n");
        } else {
            for (AbsorberAssignment assignment : absorberAssignments) {
                sb.append("- ").append(assignment).append("\n");
            }
        }

        if (!requiredEquipment.isEmpty()) {
            sb.append("\nRequired Equipment:\n");
            for (String item : requiredEquipment) {
                sb.append("- ").append(item).append("\n");
            }
        }

        if (!safe(requiredEquipmentSummary).isEmpty()) {
            sb.append("\nEquipment Summary: ").append(requiredEquipmentSummary).append("\n");
        }

        return sb.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String toString() {
        return "Task #" + taskId
                + " | Job #" + jobId
                + " | " + contractor
                + " | " + location
                + " | " + startDate + " " + startTime + "-" + endTime
                + " | Type: " + jobType
                + " | LF: " + linearFeet
                + " | Foreman: " + foreman
                + " | Status: " + status
                + " | Company Drivers: " + assignedEmployeeIds.size()
                + " | Owner Ops: " + assignedOwnerOperators.size()
                + " | Forklifts: " + assignedForklifts.size();
    }

    public static class AbsorberAssignment implements Serializable {
        private static final long serialVersionUID = 1L;

        private String setName;
        private int employeeId;
        private String origin;

        public AbsorberAssignment(String setName, int employeeId, String origin) {
            this.setName = setName == null ? "" : setName;
            this.employeeId = employeeId;
            this.origin = origin == null ? "" : origin;
        }

        public String getSetName() {
            return setName;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public String getOrigin() {
            return origin;
        }

        public void setSetName(String setName) {
            this.setName = setName == null ? "" : setName;
        }

        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }

        public void setOrigin(String origin) {
            this.origin = origin == null ? "" : origin;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(setName);
            sb.append(" (Driver ID: ").append(employeeId);
            if (!origin.isBlank()) {
                sb.append(", From: ").append(origin);
            }
            sb.append(")");
            return sb.toString();
        }
    }
}