public class Truck {
    private int id;
    private String model;
    private int mileage;
    private boolean available;
    private Driver driver;

    public Truck(int id, String model, int mileage, boolean available) {
        this.id = id;
        this.model = model;
        this.mileage = mileage;
        this.available = available;
        this.driver = null;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public Driver getDriver() {
        return driver;
    }

    // ASSIGN DRIVER
    public void assignDriver(Driver driver) {
        this.driver = driver;
        this.available = false;

        if (driver != null) {
            driver.setAssigned(true);
        }
    }

    // REMOVE DRIVER (useful later)
    public void removeDriver() {
        if (driver != null) {
            driver.setAssigned(false);
        }
        this.driver = null;
        this.available = true;
    }

    // DISPLAY
    public void displayTruck() {
        System.out.println("Truck ID: " + id);
        System.out.println("Model: " + model);
        System.out.println("Mileage: " + mileage);
        System.out.println("Available: " + available);

        if (driver != null) {
            System.out.println("Driver: " + driver.getName());
        } else {
            System.out.println("Driver: None");
        }

        System.out.println("----------------------");
    }
}