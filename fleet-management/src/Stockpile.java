public class Stockpile {

    private String name;
    private String location;
    private String notes;

    private int barrierCount;

    private String lastUpdatedBy;
    private String lastUpdatedTime;

    public Stockpile(String name, String location, String notes) {
        this.name = name;
        this.location = location;
        this.notes = notes;
        this.barrierCount = 0;
        this.lastUpdatedBy = "N/A";
        this.lastUpdatedTime = "N/A";
    }

    // ================= GETTERS =================

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

    public int getBarrierCount() {
        return barrierCount;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    // ================= SETTERS =================

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ================= CORE LOGIC =================

    public void updateBarrierCount(int newCount, String employeeName, String timestamp) {
        this.barrierCount = newCount;
        this.lastUpdatedBy = employeeName;
        this.lastUpdatedTime = timestamp;
    }

    // ================= DISPLAY =================

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}