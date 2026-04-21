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
    private ArrayList<TimeOffRequest> timeOffRequests;
    private ArrayList<MechanicalWriteUp> mechanicalWriteUps;
    private ArrayList<Stockpile> stockpiles;
    private ArrayList<DVIRReport> dvirReports;

    private Company company;

    public FleetManager() {
        employees = new ArrayList<>();
        jobs = new ArrayList<>();
        tasks = new ArrayList<>();
        trucks = new ArrayList<>();
        trailers = new ArrayList<>();
        forklifts = new ArrayList<>();
        gradalls = new ArrayList<>();
        timeOffRequests = new ArrayList<>();
        mechanicalWriteUps = new ArrayList<>();
        stockpiles = new ArrayList<>();
        dvirReports = new ArrayList<>();
    }

    // ================= EMPLOYEES =================
    public void addEmployee(Employee e) {
        employees.add(e);
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
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
        ArrayList<Employee> drivers = new ArrayList<>();

        for (Employee e : employees) {
            if (e == null) {
                continue;
            }

            String position = e.getPosition() == null ? "" : e.getPosition().toLowerCase();
            if (position.contains("driver")) {
                drivers.add(e);
            }
        }

        drivers.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return drivers;
    }

    public ArrayList<Employee> getActiveDriverEmployees() {
        ArrayList<Employee> drivers = new ArrayList<>();

        for (Employee e : employees) {
            if (e == null) {
                continue;
            }

            if (!e.isActive()) {
                continue;
            }

            String position = e.getPosition() == null ? "" : e.getPosition().toLowerCase();
            if (position.contains("driver")) {
                drivers.add(e);
            }
        }

        drivers.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return drivers;
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

    public Task findTaskById(int taskId) {
        for (Task t : tasks) {
            if (t != null && t.getTaskId() == taskId) {
                return t;
            }
        }
        return null;
    }

    public void removeTaskById(int taskId) {
        Task task = findTaskById(taskId);
        if (task != null) {
            tasks.remove(task);
        }
    }

    public ArrayList<Task> getTasksByDate(String date) {
        ArrayList<Task> result = new ArrayList<>();

        for (Task t : tasks) {
            if (t != null && t.getStartDate() != null && t.getStartDate().equals(date)) {
                result.add(t);
            }
        }

        return result;
    }

    public ArrayList<Task> getTasksSortedByTime(String date) {
        ArrayList<Task> result = getTasksByDate(date);

        result.sort(Comparator.comparing(
                Task::getStartTime,
                Comparator.nullsLast(String::compareTo)
        ));

        return result;
    }

    public ArrayList<Task> getTasksByStatus(String status) {
        ArrayList<Task> result = new ArrayList<>();

        for (Task t : tasks) {
            if (t != null && t.getStatus() != null && t.getStatus().equalsIgnoreCase(status)) {
                result.add(t);
            }
        }

        return result;
    }

    public ArrayList<Task> getTasksForEmployee(int employeeId) {
        ArrayList<Task> result = new ArrayList<>();

        for (Task t : tasks) {
            if (t != null
                    && t.getAssignedEmployeeIds() != null
                    && t.getAssignedEmployeeIds().contains(employeeId)) {
                result.add(t);
            }
        }

        return result;
    }

    public ArrayList<Task> getTasksForEmployeeSorted(int employeeId) {
        ArrayList<Task> result = getTasksForEmployee(employeeId);

        result.sort((t1, t2) -> {
            String d1 = t1.getStartDate() == null ? "" : t1.getStartDate();
            String d2 = t2.getStartDate() == null ? "" : t2.getStartDate();

            int dateCompare = d1.compareTo(d2);
            if (dateCompare != 0) {
                return dateCompare;
            }

            String s1 = t1.getStartTime() == null ? "" : t1.getStartTime();
            String s2 = t2.getStartTime() == null ? "" : t2.getStartTime();

            return s1.compareTo(s2);
        });

        return result;
    }

    public ArrayList<Task> getTasksForForeman(String foremanName) {
        ArrayList<Task> result = new ArrayList<>();

        if (foremanName == null) {
            return result;
        }

        for (Task t : tasks) {
            if (t != null
                    && t.getForeman() != null
                    && t.getForeman().equalsIgnoreCase(foremanName.trim())) {
                result.add(t);
            }
        }

        return result;
    }

    public ArrayList<Employee> getAvailableDrivers(String date, String startTime) {
        ArrayList<Employee> available = new ArrayList<>();

        for (Employee e : employees) {
            if (e == null) {
                continue;
            }

            String position = e.getPosition() == null ? "" : e.getPosition().toLowerCase();
            if (!position.contains("driver")) {
                continue;
            }

            boolean assigned = false;

            for (Task t : tasks) {
                if (t != null
                        && t.getStartDate() != null
                        && t.getStartDate().equals(date)
                        && t.getAssignedEmployeeIds() != null
                        && t.getAssignedEmployeeIds().contains(e.getEmployeeId())) {
                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                available.add(e);
            }
        }

        return available;
    }

    public boolean isEmployeeAssignedToAnyTaskOnDate(int employeeId, String date) {
        for (Task t : tasks) {
            if (t != null
                    && t.getStartDate() != null
                    && t.getStartDate().equals(date)
                    && t.getAssignedEmployeeIds() != null
                    && t.getAssignedEmployeeIds().contains(employeeId)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<Task> getGlobalLoadBoardTasks() {
        ArrayList<Task> result = new ArrayList<>(tasks);

        result.sort((t1, t2) -> {
            String d1 = t1.getStartDate() == null ? "" : t1.getStartDate();
            String d2 = t2.getStartDate() == null ? "" : t2.getStartDate();

            int dateCompare = d1.compareTo(d2);
            if (dateCompare != 0) {
                return dateCompare;
            }

            String s1 = t1.getStartTime() == null ? "" : t1.getStartTime();
            String s2 = t2.getStartTime() == null ? "" : t2.getStartTime();

            int timeCompare = s1.compareTo(s2);
            if (timeCompare != 0) {
                return timeCompare;
            }

            String c1 = t1.getContractor() == null ? "" : t1.getContractor();
            String c2 = t2.getContractor() == null ? "" : t2.getContractor();

            return c1.compareTo(c2);
        });

        return result;
    }

    // ================= TRUCKS =================
    public void addTruck(Truck truck) {
        trucks.add(truck);
    }

    public ArrayList<Truck> getTrucks() {
        return trucks;
    }

    public Truck findTruckById(String id) {
        if (id == null) {
            return null;
        }

        for (Truck t : trucks) {
            if (t != null && t.getTruckID() != null && t.getTruckID().equalsIgnoreCase(id)) {
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
        Truck selectedTruck = findTruckById(truckId);

        if (employee == null || selectedTruck == null) {
            return false;
        }

        String position = employee.getPosition() == null ? "" : employee.getPosition().toLowerCase();
        if (!position.contains("driver")) {
            return false;
        }

        if (selectedTruck.isDown()) {
            return false;
        }

        String previousTruckId = safe(employee.getAssignedTruckId());
        if (!previousTruckId.isBlank()) {
            Truck previousTruck = findTruckById(previousTruckId);
            if (previousTruck != null) {
                previousTruck.clearAssignment();
            }
        }

        if (selectedTruck.getAssignedEmployeeId() > 0) {
            Employee previousEmployee = findEmployeeById(selectedTruck.getAssignedEmployeeId());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTruckId("");
            }
        } else if (!safe(selectedTruck.getAssignedEmployeeName()).isBlank()) {
            Employee previousEmployee = findEmployeeByName(selectedTruck.getAssignedEmployeeName());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTruckId("");
            }
        }

        for (Truck truck : trucks) {
            if (truck == null) {
                continue;
            }

            if (truck.getAssignedEmployeeId() == employeeId) {
                truck.clearAssignment();
            } else if (employee.getFullName() != null
                    && employee.getFullName().equalsIgnoreCase(safe(truck.getAssignedEmployeeName()))) {
                truck.clearAssignment();
            }
        }

        selectedTruck.assignDriver(employee.getEmployeeId(), employee.getFullName());
        employee.setAssignedTruckId(selectedTruck.getTruckID());

        return true;
    }

    public boolean unassignDriverFromTruck(String truckId) {
        Truck truck = findTruckById(truckId);
        if (truck == null) {
            return false;
        }

        if (truck.getAssignedEmployeeId() > 0) {
            Employee employee = findEmployeeById(truck.getAssignedEmployeeId());
            if (employee != null) {
                employee.setAssignedTruckId("");
            }
        } else if (!safe(truck.getAssignedEmployeeName()).isBlank()) {
            Employee employee = findEmployeeByName(truck.getAssignedEmployeeName());
            if (employee != null) {
                employee.setAssignedTruckId("");
            }
        }

        truck.clearAssignment();
        return true;
    }

    // ================= TRAILERS =================
    public void addTrailer(Trailer trailer) {
        trailers.add(trailer);
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public Trailer findTrailerById(String id) {
        if (id == null) {
            return null;
        }

        for (Trailer t : trailers) {
            if (t != null && t.getTrailerId() != null && t.getTrailerId().equalsIgnoreCase(id)) {
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
        Trailer selectedTrailer = findTrailerById(trailerId);

        if (employee == null || selectedTrailer == null) {
            return false;
        }

        String position = employee.getPosition() == null ? "" : employee.getPosition().toLowerCase();
        if (!position.contains("driver")) {
            return false;
        }

        if (selectedTrailer.isDown()) {
            return false;
        }

        String previousTrailerId = safe(employee.getAssignedTrailerId());
        if (!previousTrailerId.isBlank()) {
            Trailer previousTrailer = findTrailerById(previousTrailerId);
            if (previousTrailer != null) {
                previousTrailer.clearAssignment();
            }
        }

        if (selectedTrailer.getAssignedEmployeeId() > 0) {
            Employee previousEmployee = findEmployeeById(selectedTrailer.getAssignedEmployeeId());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTrailerId("");
            }
        } else if (!safe(selectedTrailer.getAssignedEmployeeName()).isBlank()) {
            Employee previousEmployee = findEmployeeByName(selectedTrailer.getAssignedEmployeeName());
            if (previousEmployee != null) {
                previousEmployee.setAssignedTrailerId("");
            }
        }

        for (Trailer trailer : trailers) {
            if (trailer == null) {
                continue;
            }

            if (trailer.getAssignedEmployeeId() == employeeId) {
                trailer.clearAssignment();
            } else if (employee.getFullName() != null
                    && employee.getFullName().equalsIgnoreCase(safe(trailer.getAssignedEmployeeName()))) {
                trailer.clearAssignment();
            }
        }

        selectedTrailer.assignDriver(employee.getEmployeeId(), employee.getFullName());
        employee.setAssignedTrailerId(selectedTrailer.getTrailerId());

        return true;
    }

    public boolean unassignDriverFromTrailer(String trailerId) {
        Trailer trailer = findTrailerById(trailerId);
        if (trailer == null) {
            return false;
        }

        if (trailer.getAssignedEmployeeId() > 0) {
            Employee employee = findEmployeeById(trailer.getAssignedEmployeeId());
            if (employee != null) {
                employee.setAssignedTrailerId("");
            }
        } else if (!safe(trailer.getAssignedEmployeeName()).isBlank()) {
            Employee employee = findEmployeeByName(trailer.getAssignedEmployeeName());
            if (employee != null) {
                employee.setAssignedTrailerId("");
            }
        }

        trailer.clearAssignment();
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
    public void addForklift(Forklift forklift) {
        forklifts.add(forklift);
    }

    public ArrayList<Forklift> getForklifts() {
        return forklifts;
    }

    public Forklift findForkliftById(String id) {
        if (id == null) {
            return null;
        }

        for (Forklift f : forklifts) {
            if (f != null && f.getUnitId() != null && f.getUnitId().equalsIgnoreCase(id)) {
                return f;
            }
        }
        return null;
    }

    public ArrayList<Forklift> getAvailableForklifts() {
        ArrayList<Forklift> available = new ArrayList<>();

        for (Forklift forklift : forklifts) {
            if (forklift == null) {
                continue;
            }

            boolean assigned = false;

            for (Task task : tasks) {
                if (task != null
                        && task.getAssignedForklifts() != null
                        && task.getAssignedForklifts().contains(forklift.getUnitId())
                        && task.getStatus() != null
                        && !task.getStatus().equalsIgnoreCase("Completed")) {
                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                available.add(forklift);
            }
        }

        return available;
    }

    public boolean isForkliftAssigned(String forkliftId) {
        if (forkliftId == null) {
            return false;
        }

        for (Task task : tasks) {
            if (task != null
                    && task.getAssignedForklifts() != null
                    && task.getAssignedForklifts().contains(forkliftId)
                    && task.getStatus() != null
                    && !task.getStatus().equalsIgnoreCase("Completed")) {
                return true;
            }
        }

        return false;
    }

    // ================= GRADALLS =================
    public void addGradall(Gradall gradall) {
        gradalls.add(gradall);
    }

    public ArrayList<Gradall> getGradalls() {
        return gradalls;
    }

    public Gradall findGradallById(String id) {
        if (id == null) {
            return null;
        }

        for (Gradall g : gradalls) {
            if (g != null && g.getUnitId() != null && g.getUnitId().equalsIgnoreCase(id)) {
                return g;
            }
        }
        return null;
    }

    // ================= STOCKPILES =================
    public void addStockpile(Stockpile stockpile) {
        stockpiles.add(stockpile);
    }

    public ArrayList<Stockpile> getStockpiles() {
        return stockpiles;
    }

    // ================= TIME OFF =================
    public void addTimeOffRequest(TimeOffRequest request) {
        timeOffRequests.add(request);
    }

    public ArrayList<TimeOffRequest> getTimeOffRequests() {
        return timeOffRequests;
    }

    public int getNextTimeOffRequestId() {
        return timeOffRequests.size() + 1;
    }

    // ================= MECHANICAL =================
    public void addMechanicalWriteUp(MechanicalWriteUp writeUp) {
        mechanicalWriteUps.add(writeUp);
    }

    public ArrayList<MechanicalWriteUp> getMechanicalWriteUps() {
        return mechanicalWriteUps;
    }

    public int getNextWriteUpId() {
        return mechanicalWriteUps.size() + 1;
    }

    // ================= DVIR =================
    public void addDvirReport(DVIRReport report) {
        dvirReports.add(report);
    }

    public ArrayList<DVIRReport> getDvirReports() {
        return dvirReports;
    }

    public int getNextDvirReportId() {
        return dvirReports.size() + 1;
    }

    // ================= COMPANY =================
    public void setCompany(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    // ================= HELPERS =================
    private String safe(String value) {
        return value == null ? "" : value;
    }
}