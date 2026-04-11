public class Truck {
    private String truckID;
    private String model;
    private boolean isDown; 
    private String currentIssue;

    public Truck(String truckID, String model) {
        this.truckID = truckID;
        this.model = model;
        this.isDown = false;
        this.currentIssue = "Ready";
    }

    // Standard Getters
    public String getTruckID() { return truckID; }
    public String getModel() { return model; }
    public boolean isDown() { return isDown; }
    public String getCurrentIssue() { return currentIssue; }

    // Helper method to resolve common "cannot find symbol" errors in FleetManager
    public String getId() { return truckID; }

    // Setters for Maintenance Logic
    public void setDown(boolean down, String issue) {
        this.isDown = down;
        this.currentIssue = issue;
    }

    // This makes the truck appear correctly in JComboBoxes or Lists
    @Override
    public String toString() { 
        return truckID + " (" + model + ")"; 
    }
}