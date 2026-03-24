import java.util.ArrayList;

public class FleetManager {

    private ArrayList<Employee> employees = new ArrayList<>();
    private ArrayList<Truck> trucks = new ArrayList<>();
    private ArrayList<Job> jobs = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();

    public void addEmployee(Employee emp) {
        employees.add(emp);
    }

    public void addTruck(Truck truck) {
        trucks.add(truck);
    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public ArrayList<Truck> getTrucks() {
        return trucks;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}