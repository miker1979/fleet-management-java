public class Driver {
    private int id;
    private String name;
    private String license;
    private boolean assigned;

    public Driver(int id, String name, String license) {
        this.id = id;
        this.name = name;
        this.license = license;
        this.assigned = false;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLicense() {
        return license;
    }

    public boolean isAssigned() {
        return assigned;
    }

    // SETTERS
    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    // DISPLAY
    public void displayDriver() {
        System.out.println("Driver ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("License: " + license);
        System.out.println("Assigned: " + (assigned ? "Yes" : "No"));
        System.out.println("----------------------");
    }
}