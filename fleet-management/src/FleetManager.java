import java.util.ArrayList;

public class FleetManager {

    private ArrayList<Employee> employees;
    private ArrayList<Job> jobs;
    private ArrayList<Task> tasks;
    private ArrayList<Truck> trucks;
    private ArrayList<Forklift> forklifts;
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
        forklifts = new ArrayList<>();
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
        for (Task task : tasks) {
            if (task.getTaskId() == taskId) {
                return task;
            }
        }
        return null;
    }

    // ================= TRUCKS =================
    public void addTruck(Truck truck) {
        trucks.add(truck);
    }

    public ArrayList<Truck> getTrucks() {
        return trucks;
    }

    public Truck findTruckById(String truckId) {
        for (Truck t : trucks) {
            if (t.getTruckID().equals(truckId)) {
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

    public Forklift findForkliftById(String unitId) {
        for (Forklift forklift : forklifts) {
            if (forklift.getUnitId().equals(unitId)) {
                return forklift;
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

    public Stockpile findStockpileByName(String name) {
        for (Stockpile stockpile : stockpiles) {
            if (stockpile.getName().equalsIgnoreCase(name)) {
                return stockpile;
            }
        }
        return null;
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

    // ================= MECHANICAL WRITE-UPS =================
    public void addMechanicalWriteUp(MechanicalWriteUp writeUp) {
        mechanicalWriteUps.add(writeUp);
    }

    public ArrayList<MechanicalWriteUp> getMechanicalWriteUps() {
        return mechanicalWriteUps;
    }

    public int getNextWriteUpId() {
        return mechanicalWriteUps.size() + 1;
    }

    // ================= DVIR REPORTS =================
    public void addDvirReport(DVIRReport report) {
        dvirReports.add(report);
    }

    public ArrayList<DVIRReport> getDvirReports() {
        return dvirReports;
    }

    public DVIRReport findDvirReportById(int reportId) {
        for (DVIRReport report : dvirReports) {
            if (report.getReportId() == reportId) {
                return report;
            }
        }
        return null;
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