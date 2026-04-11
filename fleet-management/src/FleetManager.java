import java.util.ArrayList;
import java.util.List;

public class FleetManager {

    private ArrayList<Employee> employees;
    private ArrayList<Truck> trucks;
    private ArrayList<Job> jobs;
    private ArrayList<Task> tasks;
    private ArrayList<MechanicalWriteUp> mechanicalWriteUps;
    private ArrayList<Evaluation> evaluations;
    private ArrayList<TimeOffRequest> timeOffRequests;

    public FleetManager() {
        employees = new ArrayList<>();
        trucks = new ArrayList<>();
        jobs = new ArrayList<>();
        tasks = new ArrayList<>();
        mechanicalWriteUps = new ArrayList<>();
        evaluations = new ArrayList<>();
        timeOffRequests = new ArrayList<>();
    }

    // ===== EMPLOYEES =====
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public Employee findEmployeeById(int employeeId) {
        for (Employee employee : employees) {
            if (employee.getEmployeeId() == employeeId) {
                return employee;
            }
        }
        return null;
    }

    // ===== TRUCKS =====
    public void addTruck(Truck truck) {
        trucks.add(truck);
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public Truck findTruckById(String truckId) {
        for (Truck truck : trucks) {
            if (truck.getTruckID().equalsIgnoreCase(truckId)) {
                return truck;
            }
        }
        return null;
    }

    // ===== JOBS =====
    public void addJob(Job job) {
        jobs.add(job);
    }

    public List<Job> getJobs() {
        return jobs;
    }

    // ===== TASKS =====
    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // ===== MECHANICAL WRITE-UPS =====
    public void addMechanicalWriteUp(MechanicalWriteUp writeUp) {
        mechanicalWriteUps.add(writeUp);
    }

    public List<MechanicalWriteUp> getMechanicalWriteUps() {
        return mechanicalWriteUps;
    }

    public int getNextWriteUpId() {
        return 5000 + mechanicalWriteUps.size() + 1;
    }

    // ===== EVALUATIONS =====
    public void addEvaluation(Evaluation evaluation) {
        evaluations.add(evaluation);
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public int getNextEvaluationId() {
        return 1000 + evaluations.size() + 1;
    }

    // ===== TIME OFF REQUESTS =====
    public void addTimeOffRequest(TimeOffRequest request) {
        timeOffRequests.add(request);
    }

    public List<TimeOffRequest> getTimeOffRequests() {
        return timeOffRequests;
    }

    public int getNextTimeOffRequestId() {
        return 8000 + timeOffRequests.size() + 1;
    }
}