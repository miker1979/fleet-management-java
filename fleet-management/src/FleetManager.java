import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class FleetManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Employee> employees;
    private ArrayList<Job> jobs;
    private ArrayList<Task> tasks;
    private ArrayList<Truck> trucks;
    private ArrayList<Trailer> trailers;
    private ArrayList<Forklift> forklifts;
    private ArrayList<Gradall> gradalls;
    private ArrayList<Stockpile> stockpiles;

    private ArrayList<DVIRReport> dvirReports;
    private ArrayList<MechanicalWriteUp> mechanicalWriteUps;
    private ArrayList<TimeOffRequest> timeOffRequests;

    private Company company;

    public FleetManager() {
        employees = new ArrayList<>();
        jobs = new ArrayList<>();
        tasks = new ArrayList<>();
        trucks = new ArrayList<>();
        trailers = new ArrayList<>();
        forklifts = new ArrayList<>();
        gradalls = new ArrayList<>();
        stockpiles = new ArrayList<>();

        dvirReports = new ArrayList<>();
        mechanicalWriteUps = new ArrayList<>();
        timeOffRequests = new ArrayList<>();
    }

    // ================= EMPLOYEES =================

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee e) {
        employees.add(e);
    }

    public Employee findEmployeeById(int id) {
        for (Employee e : employees) {
            if (e != null && e.getEmployeeId() == id) {
                return e;
            }
        }
        return null;
    }

    public Employee findEmployeeByName(String fullName) {
        if (fullName == null) {
            return null;
        }

        for (Employee e : employees) {
            if (e != null
                    && e.getFullName() != null
                    && e.getFullName().equalsIgnoreCase(fullName.trim())) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<Employee> getDriverEmployees() {
        ArrayList<Employee> result = new ArrayList<>();

        for (Employee e : employees) {
            if (e != null && safe(e.getPosition()).toLowerCase().contains("driver")) {
                result.add(e);
            }
        }

        result.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public ArrayList<Employee> getActiveDriverEmployees() {
        ArrayList<Employee> result = new ArrayList<>();

        for (Employee e : employees) {
            if (e == null || !e.isActive()) {
                continue;
            }

            String pos = safe(e.getPosition()).toLowerCase();
            if (pos.contains("driver")) {
                result.add(e);
            }
        }

        result.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public ArrayList<Employee> getForemen() {
        ArrayList<Employee> result = new ArrayList<>();

        for (Employee e : employees) {
            if (e != null) {
                String pos = safe(e.getPosition()).toLowerCase();
                if (pos.contains("foreman") || pos.contains("supervisor")) {
                    result.add(e);
                }
            }
        }

        result.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public Employee findEmployeeAssignedToTruck(String truckId) {
        if (truckId == null || truckId.isBlank()) {
            return null;
        }

        for (Employee e : employees) {
            if (e != null && truckId.equalsIgnoreCase(safe(e.getAssignedTruckId()))) {
                return e;
            }
        }
        return null;
    }

    public Employee findEmployeeAssignedToTrailer(String trailerId) {
        if (trailerId == null || trailerId.isBlank()) {
            return null;
        }

        for (Employee e : employees) {
            if (e != null && trailerId.equalsIgnoreCase(safe(e.getAssignedTrailerId()))) {
                return e;
            }
        }
        return null;
    }

    // ================= JOBS =================

    public void addJob(Job job) {
        jobs.add(job);
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public Job findJobByNumber(int jobNumber) {
        for (Job j : jobs) {
            if (j != null && j.getJobNumber() == jobNumber) {
                return j;
            }
        }
        return null;
    }

    // ================= TASKS =================

    public void addTask(Task task) {
        tasks.add(task);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public Task findTaskById(int id) {
        for (Task t : tasks) {
            if (t != null && t.getTaskId() == id) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Task> getTasksByJobId(int jobId) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t != null && t.getJobId() == jobId) {
                result.add(t);
            }
        }
        return result;
    }

    public int getTaskCountForJob(int jobId) {
        return getTasksByJobId(jobId).size();
    }

    public int getTaskCountByType(int jobId, String type) {
        int count = 0;
        for (Task t : tasks) {
            if (t != null
                    && t.getJobId() == jobId
                    && safe(t.getJobType()).equalsIgnoreCase(type)) {
                count++;
            }
        }
        return count;
    }

    public double getTotalManHoursForJob(int jobId) {
        double total = 0;

        for (Task t : tasks) {
            if (t == null || t.getJobId() != jobId) {
                continue;
            }

            int crew = t.getAssignedEmployeeIds() == null ? 0 : t.getAssignedEmployeeIds().size();

            try {
                int start = Integer.parseInt(t.getStartTime().replace(":", ""));
                int end = Integer.parseInt(t.getEndTime().replace(":", ""));
                double hours = (end - start) / 100.0;
                total += hours * crew;
            } catch (Exception ignored) {
            }
        }

        return total;
    }

    // ================= TRUCKS =================

    public void addTruck(Truck t) {
        trucks.add(t);
    }

    public ArrayList<Truck> getTrucks() {
        return trucks;
    }

    public Truck findTruckById(String id) {
        if (id == null) {
            return null;
        }

        for (Truck t : trucks) {
            if (t != null && safe(t.getTruckID()).equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Truck> getAssignableTrucks() {
        ArrayList<Truck> result = new ArrayList<>();

        for (Truck truck : trucks) {
            if (truck != null && !truck.isDown()) {
                result.add(truck);
            }
        }

        result.sort(Comparator.comparing(Truck::getTruckID, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public boolean assignDriverToTruck(int employeeId, String truckId) {
        Employee employee = findEmployeeById(employeeId);
        Truck truck = findTruckById(truckId);

        if (employee == null || truck == null) {
            return false;
        }

        if (truck.isDown()) {
            return false;
        }

        String position = safe(employee.getPosition()).toLowerCase();
        if (!position.contains("driver")) {
            return false;
        }

        String previousTruckId = safe(employee.getAssignedTruckId());
        if (!previousTruckId.isBlank()) {
            Truck previousTruck = findTruckById(previousTruckId);
            if (previousTruck != null) {
                previousTruck.clearAssignment();
            }
        }

        if (truck.getAssignedEmployeeId() > 0) {
            Employee previousEmployee = findEmployeeById(truck.getAssignedEmployeeId());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTruckId("");
            }
        } else if (!safe(truck.getAssignedEmployeeName()).isBlank()) {
            Employee previousEmployee = findEmployeeByName(truck.getAssignedEmployeeName());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTruckId("");
            }
        }

        for (Truck other : trucks) {
            if (other == null) {
                continue;
            }

            if (other.getAssignedEmployeeId() == employeeId) {
                other.clearAssignment();
            } else if (employee.getFullName() != null
                    && employee.getFullName().equalsIgnoreCase(safe(other.getAssignedEmployeeName()))) {
                other.clearAssignment();
            }
        }

        employee.setAssignedTruckId(truckId);
        truck.assignDriver(employee.getEmployeeId(), employee.getFullName());
        return true;
    }

    // ================= TRAILERS =================

    public void addTrailer(Trailer t) {
        trailers.add(t);
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public Trailer findTrailerById(String id) {
        if (id == null) {
            return null;
        }

        for (Trailer t : trailers) {
            if (t != null && safe(t.getTrailerId()).equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Trailer> getAssignableTrailers() {
        ArrayList<Trailer> result = new ArrayList<>();

        for (Trailer trailer : trailers) {
            if (trailer != null && !trailer.isDown()) {
                result.add(trailer);
            }
        }

        result.sort(Comparator.comparing(Trailer::getTrailerId, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public boolean assignDriverToTrailer(int employeeId, String trailerId) {
        Employee employee = findEmployeeById(employeeId);
        Trailer trailer = findTrailerById(trailerId);

        if (employee == null || trailer == null) {
            return false;
        }

        if (trailer.isDown()) {
            return false;
        }

        String position = safe(employee.getPosition()).toLowerCase();
        if (!position.contains("driver")) {
            return false;
        }

        String previousTrailerId = safe(employee.getAssignedTrailerId());
        if (!previousTrailerId.isBlank()) {
            Trailer previousTrailer = findTrailerById(previousTrailerId);
            if (previousTrailer != null) {
                previousTrailer.clearAssignment();
            }
        }

        if (trailer.getAssignedEmployeeId() > 0) {
            Employee previousEmployee = findEmployeeById(trailer.getAssignedEmployeeId());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTrailerId("");
            }
        } else if (!safe(trailer.getAssignedEmployeeName()).isBlank()) {
            Employee previousEmployee = findEmployeeByName(trailer.getAssignedEmployeeName());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTrailerId("");
            }
        }

        for (Trailer other : trailers) {
            if (other == null) {
                continue;
            }

            if (other.getAssignedEmployeeId() == employeeId) {
                other.clearAssignment();
            } else if (employee.getFullName() != null
                    && employee.getFullName().equalsIgnoreCase(safe(other.getAssignedEmployeeName()))) {
                other.clearAssignment();
            }
        }

        employee.setAssignedTrailerId(trailerId);
        trailer.assignDriver(employee.getEmployeeId(), employee.getFullName());
        return true;
    }

    public void clearTrailerAssignmentForEmployee(int employeeId) {
        Employee employee = findEmployeeById(employeeId);
        if (employee == null) {
            return;
        }

        String trailerId = safe(employee.getAssignedTrailerId());
        if (!trailerId.isBlank()) {
            Trailer trailer = findTrailerById(trailerId);
            if (trailer != null) {
                trailer.clearAssignment();
            }
        }

        employee.setAssignedTrailerId("");
    }

    // ================= FORKLIFTS =================

    public void addForklift(Forklift f) {
        forklifts.add(f);
    }

    public ArrayList<Forklift> getForklifts() {
        return forklifts;
    }

    public Forklift findForkliftById(String id) {
        if (id == null) {
            return null;
        }

        for (Forklift forklift : forklifts) {
            if (forklift != null
                    && forklift.getUnitId() != null
                    && forklift.getUnitId().equalsIgnoreCase(id)) {
                return forklift;
            }
        }
        return null;
    }

    public boolean isForkliftAssigned(String id) {
        for (Task t : tasks) {
            if (t != null
                    && t.getAssignedForklifts() != null
                    && t.getAssignedForklifts().contains(id)
                    && !safe(t.getStatus()).equalsIgnoreCase("Completed")) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Forklift> getAvailableForklifts() {
        ArrayList<Forklift> available = new ArrayList<>();
        for (Forklift f : forklifts) {
            if (f != null && !isForkliftAssigned(f.getUnitId())) {
                available.add(f);
            }
        }
        return available;
    }

    public boolean isForkliftInAnyStockpile(String unitId) {
        if (unitId == null || unitId.isBlank()) {
            return false;
        }

        for (Stockpile s : stockpiles) {
            if (s != null && s.getForkliftIds().contains(unitId.trim())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> addMultipleForkliftsToStockpile(String stockpileName,
                                                             ArrayList<String> forkliftIds,
                                                             String employeeName,
                                                             String timestamp) {

        ArrayList<String> failed = new ArrayList<>();

        Stockpile stockpile = findStockpileByName(stockpileName);
        if (stockpile == null) {
            failed.add("Invalid Stockpile");
            return failed;
        }

        for (String id : forkliftIds) {
            if (id == null || id.isBlank()) {
                continue;
            }

            String trimmed = id.trim();

            if (findForkliftById(trimmed) == null) {
                failed.add(trimmed + " (not found)");
                continue;
            }

            if (isForkliftInAnyStockpile(trimmed)) {
                failed.add(trimmed + " (already assigned)");
                continue;
            }

            stockpile.addForklift(trimmed, employeeName, timestamp);
        }

        return failed;
    }

    // ================= GRADALL =================

    public void addGradall(Gradall g) {
        gradalls.add(g);
    }

    public ArrayList<Gradall> getGradalls() {
        return gradalls;
    }

    public Gradall findGradallById(String id) {
        if (id == null) {
            return null;
        }

        for (Gradall gradall : gradalls) {
            if (gradall != null
                    && gradall.getUnitId() != null
                    && gradall.getUnitId().equalsIgnoreCase(id)) {
                return gradall;
            }
        }
        return null;
    }

    public boolean isGradallInAnyStockpile(String unitId) {
        if (unitId == null || unitId.isBlank()) {
            return false;
        }

        for (Stockpile s : stockpiles) {
            if (s != null && s.getGradallIds().contains(unitId.trim())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> addMultipleGradallsToStockpile(String stockpileName,
                                                            ArrayList<String> gradallIds,
                                                            String employeeName,
                                                            String timestamp) {

        ArrayList<String> failed = new ArrayList<>();

        Stockpile stockpile = findStockpileByName(stockpileName);
        if (stockpile == null) {
            failed.add("Invalid Stockpile");
            return failed;
        }

        for (String id : gradallIds) {
            if (id == null || id.isBlank()) {
                continue;
            }

            String trimmed = id.trim();

            if (findGradallById(trimmed) == null) {
                failed.add(trimmed + " (not found)");
                continue;
            }

            if (isGradallInAnyStockpile(trimmed)) {
                failed.add(trimmed + " (already assigned)");
                continue;
            }

            stockpile.addGradall(trimmed, employeeName, timestamp);
        }

        return failed;
    }

    // ================= STOCKPILES =================

    public void addStockpile(Stockpile stockpile) {
        stockpiles.add(stockpile);
    }

    public ArrayList<Stockpile> getStockpiles() {
        return stockpiles;
    }

    public Stockpile findStockpileByName(String name) {
        if (name == null) {
            return null;
        }

        for (Stockpile s : stockpiles) {
            if (s != null && s.getName() != null && s.getName().equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        return null;
    }

    public boolean addForkliftToStockpile(String stockpileName, String forkliftId, String employeeName, String timestamp) {
        Stockpile stockpile = findStockpileByName(stockpileName);

        if (stockpile == null || forkliftId == null || forkliftId.isBlank()) {
            return false;
        }

        String trimmedId = forkliftId.trim();

        if (findForkliftById(trimmedId) == null) {
            return false;
        }

        if (isForkliftInAnyStockpile(trimmedId)) {
            return false;
        }

        return stockpile.addForklift(trimmedId, employeeName, timestamp);
    }

    public boolean removeForkliftFromStockpile(String stockpileName, String forkliftId, String employeeName, String timestamp) {
        Stockpile stockpile = findStockpileByName(stockpileName);

        if (stockpile == null || forkliftId == null || forkliftId.isBlank()) {
            return false;
        }

        return stockpile.removeForklift(forkliftId.trim(), employeeName, timestamp);
    }

    public boolean addGradallToStockpile(String stockpileName, String gradallId, String employeeName, String timestamp) {
        Stockpile stockpile = findStockpileByName(stockpileName);

        if (stockpile == null || gradallId == null || gradallId.isBlank()) {
            return false;
        }

        String trimmedId = gradallId.trim();

        if (findGradallById(trimmedId) == null) {
            return false;
        }

        if (isGradallInAnyStockpile(trimmedId)) {
            return false;
        }

        return stockpile.addGradall(trimmedId, employeeName, timestamp);
    }

    public boolean removeGradallFromStockpile(String stockpileName, String gradallId, String employeeName, String timestamp) {
        Stockpile stockpile = findStockpileByName(stockpileName);

        if (stockpile == null || gradallId == null || gradallId.isBlank()) {
            return false;
        }

        return stockpile.removeGradall(gradallId.trim(), employeeName, timestamp);
    }

    public boolean dispatchBarriersToJob(String stockpileName,
                                         int jobNumber,
                                         int quantity,
                                         String employeeName,
                                         String timestamp) {

        if (quantity <= 0) {
            return false;
        }

        Stockpile stockpile = findStockpileByName(stockpileName);
        Job job = findJobByNumber(jobNumber);

        if (stockpile == null || job == null) {
            return false;
        }

        if (!stockpile.hasEnoughBarriers(quantity)) {
            return false;
        }

        boolean removed = stockpile.removeBarriers(quantity, employeeName, timestamp);
        if (!removed) {
            return false;
        }

        job.addBarriers(quantity, stockpileName);
        return true;
    }

    public boolean returnBarriersFromJob(String stockpileName,
                                         int jobNumber,
                                         int quantity,
                                         String employeeName,
                                         String timestamp) {

        if (quantity <= 0) {
            return false;
        }

        Stockpile stockpile = findStockpileByName(stockpileName);
        Job job = findJobByNumber(jobNumber);

        if (stockpile == null || job == null) {
            return false;
        }

        if (!job.removeBarriers(quantity)) {
            return false;
        }

        return stockpile.addBarriers(quantity, employeeName, timestamp);
    }

    public int getRemainingBarriersAtStockpile(String stockpileName) {
        Stockpile stockpile = findStockpileByName(stockpileName);
        return stockpile == null ? 0 : stockpile.getBarrierCount();
    }

    public int getBarriersAssignedToJob(int jobNumber) {
        Job job = findJobByNumber(jobNumber);
        return job == null ? 0 : job.getBarriersAssigned();
    }

    // ================= DVIR =================

    public ArrayList<DVIRReport> getDvirReports() {
        return dvirReports;
    }

    // ================= MECHANICAL =================

    public ArrayList<MechanicalWriteUp> getMechanicalWriteUps() {
        return mechanicalWriteUps;
    }

    public int getNextWriteUpId() {
        int max = 0;
        for (MechanicalWriteUp writeUp : mechanicalWriteUps) {
            if (writeUp != null) {
                max = Math.max(max, writeUp.getWriteUpId());
            }
        }
        return max + 1;
    }

    public void addMechanicalWriteUp(MechanicalWriteUp writeUp) {
        mechanicalWriteUps.add(writeUp);
    }

    // ================= TIME OFF =================

    public ArrayList<TimeOffRequest> getTimeOffRequests() {
        return timeOffRequests;
    }

    public int getNextTimeOffRequestId() {
        int max = 0;
        for (TimeOffRequest request : timeOffRequests) {
            if (request != null) {
                max = Math.max(max, request.getRequestId());
            }
        }
        return max + 1;
    }

    public void addTimeOffRequest(TimeOffRequest request) {
        timeOffRequests.add(request);
    }

    // ================= COMPANY =================

    public void setCompany(Company c) {
        this.company = c;
    }

    public Company getCompany() {
        return company;
    }

    // ================= UTIL =================

    private String safe(String v) {
        return v == null ? "" : v;
    }
}