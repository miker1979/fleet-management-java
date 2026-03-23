import java.util.ArrayList;

public class FleetManager {

    private ArrayList<Truck> trucks;
    private ArrayList<Driver> drivers;
    private ArrayList<Employee> employees;

    public FleetManager() {
        trucks = new ArrayList<>();
        drivers = new ArrayList<>();
        employees = new ArrayList<>();
    }

    // =========================
    // TRUCK METHODS
    // =========================
    public void addTruck(Truck truck) {
        trucks.add(truck);
    }

    public ArrayList<Truck> getTrucks() {
        return trucks;
    }

    public Truck findTruckById(int id) {
        for (Truck truck : trucks) {
            if (truck.getId() == id) {
                return truck;
            }
        }
        return null;
    }

    public int getTotalTrucks() {
        return trucks.size();
    }

    public int getAvailableTrucks() {
        int count = 0;
        for (Truck truck : trucks) {
            if (truck.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public void displayAllTrucks() {
        if (trucks.isEmpty()) {
            System.out.println("No trucks in fleet.");
            return;
        }

        for (Truck truck : trucks) {
            truck.displayTruck();
        }
    }

    // =========================
    // DRIVER METHODS
    // =========================
    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public ArrayList<Driver> getDrivers() {
        return drivers;
    }

    public Driver findDriverById(int id) {
        for (Driver driver : drivers) {
            if (driver.getId() == id) {
                return driver;
            }
        }
        return null;
    }

    public void displayAllDrivers() {
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
            return;
        }

        for (Driver driver : drivers) {
            driver.displayDriver();
        }
    }

    // =========================
    // EMPLOYEE METHODS
    // =========================
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public Employee findEmployeeById(int id) {
        for (Employee employee : employees) {
            if (employee.getEmployeeId() == id) {
                return employee;
            }
        }
        return null;
    }

    public void displayAllEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        for (Employee employee : employees) {
            employee.displayEmployee();
        }
    }
}