public class Vehicle {
    private int id;
    private String make;
    private String model;
    private int year;
    private String driver;

    public Vehicle(int id, String make, String model, int year, String driver) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.driver = driver;
    }

    public int getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Override
    public String toString() {
        return "ID: " + id +
               " | Year: " + year +
               " | Make: " + make +
               " | Model: " + model +
               " | Driver: " + driver;
    }
}