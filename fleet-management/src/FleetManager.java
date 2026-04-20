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
            if (e.getId() == id) {
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
            if (e.getFullName() != null && e.getFullName().equalsIgnoreCase(fullName.trim())) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<Employee> getDriverEmployees() {
        ArrayList<Employee> drivers = new ArrayList<>();

        for (Employee e : employees) {
            if (e.getPosition() != null && e.getPosition().toLowerCase().contains("driver")) {
                drivers.add(e);
            }
        }

        return drivers;
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
            if (j.getJobNumber() == jobNumber) {
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
            if (t.getTaskId() == taskId) {
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
            if (t.getStartDate() != null && t.getStartDate().equals(date)) {
                result.add(t);
            }
        }

        return result;
    }

    public ArrayList<Task> getTasksSortedByTime(String date) {
        ArrayList<Task> result = getTasksByDate(date);

        result.sort(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(String::compareTo)));

        return result;
    }

    public ArrayList<Task> getTasksByStatus(String status) {
        ArrayList<Task> result = new ArrayList<>();

        for (Task t : tasks) {
            if (t.getStatus() != null && t.getStatus().equalsIgnoreCase(status)) {
                result.add(t);
            }
        }

        return result;
    }

    public ArrayList<Task> getTasksForEmployee(int employeeId) {
        ArrayList<Task> result = new ArrayList<>();

        for (Task t : tasks) {
            if (t.getAssignedEmployeeIds() != null && t.getAssignedEmployeeIds().contains(employeeId)) {
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
            if (t.getForeman() != null && t.getForeman().equalsIgnoreCase(foremanName.trim())) {
                result.add(t);
            }
        }

        return result;
    }

    public ArrayList<Employee> getAvailableDrivers(String date, String startTime) {
        ArrayList<Employee> available = new ArrayList<>();

        for (Employee e : employees) {
            if (e.getPosition() == null || !e.getPosition().toLowerCase().contains("driver")) {
                continue;
            }

            boolean assigned = false;

            for (Task t : tasks) {
                if (t.getStartDate() != null
                        && t.getStartDate().equals(date)
                        && t.getAssignedEmployeeIds() != null
                        && t.getAssignedEmployeeIds().contains(e.getId())) {
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
            if (t.getStartDate() != null
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
        for (Truck t : trucks) {
            if (t.getTruckID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    // ================= TRAILERS =================
    public void addTrailer(Trailer trailer) {
        trailers.add(trailer);
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public Trailer findTrailerById(String id) {
        for (Trailer t : trailers) {
            if (t.getTrailerId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    // ================= FORKLIFTS =================
    public void addForklift(Forklift forklift) {
        forklifts.add(forklift);
    }

    public ArrayList<Forklift> getForklifts() {
        return forklifts;
    }

    public Forklift findForkliftById(String id) {
        for (Forklift f : forklifts) {
            if (f.getUnitId().equals(id)) {
                return f;
            }
        }
        return null;
    }

    public ArrayList<Forklift> getAvailableForklifts() {
        ArrayList<Forklift> available = new ArrayList<>();

        for (Forklift forklift : forklifts) {
            boolean assigned = false;

            for (Task task : tasks) {
                if (task.getAssignedForklifts() != null
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
            if (task.getAssignedForklifts() != null
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
        for (Gradall g : gradalls) {
            if (g.getUnitId().equals(id)) {
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
}