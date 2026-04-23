import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stockpile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String location;
    private String notes;

    // Material inventory counts
    private int count20Ft;
    private int count12Ft;
    private int absorbSetCount;
    private int connector20FtCount;
    private int connector12FtCount;

    // Equipment staged at this stockpile by unique unit ID
    private ArrayList<String> forkliftIds;
    private ArrayList<String> gradallIds;

    private String lastUpdatedBy;
    private String lastUpdatedTime;

    public Stockpile(String name, String location, String notes) {
        this.name = name == null ? "" : name;
        this.location = location == null ? "" : location;
        this.notes = notes == null ? "" : notes;

        this.count20Ft = 0;
        this.count12Ft = 0;
        this.absorbSetCount = 0;
        this.connector20FtCount = 0;
        this.connector12FtCount = 0;

        this.forkliftIds = new ArrayList<>();
        this.gradallIds = new ArrayList<>();

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

    public int getCount20Ft() {
        return count20Ft;
    }

    public int getCount12Ft() {
        return count12Ft;
    }

    public int getAbsorbSetCount() {
        return absorbSetCount;
    }

    public int getConnector20FtCount() {
        return connector20FtCount;
    }

    public int getConnector12FtCount() {
        return connector12FtCount;
    }

    public List<String> getForkliftIds() {
        if (forkliftIds == null) {
            forkliftIds = new ArrayList<>();
        }
        return forkliftIds;
    }

    public List<String> getGradallIds() {
        if (gradallIds == null) {
            gradallIds = new ArrayList<>();
        }
        return gradallIds;
    }

    public int getForkliftCount() {
        return getForkliftIds().size();
    }

    public int getGradallCount() {
        return getGradallIds().size();
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    // Compatibility getter
    public int getBarrierCount() {
        return getTotalBarrierPieces();
    }

    public int getTotalBarrierPieces() {
        return count20Ft + count12Ft + connector20FtCount + connector12FtCount;
    }

    public int getTotalInventoryPieces() {
        return count20Ft + count12Ft + absorbSetCount + connector20FtCount + connector12FtCount;
    }

    public int getTotalLinearFeet() {
        return (count20Ft * 20)
                + (count12Ft * 12)
                + (connector20FtCount * 20)
                + (connector12FtCount * 12);
    }

    // ================= SETTERS =================

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public void setLocation(String location) {
        this.location = location == null ? "" : location;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
    }

    public void setCount20Ft(int count20Ft) {
        this.count20Ft = Math.max(0, count20Ft);
    }

    public void setCount12Ft(int count12Ft) {
        this.count12Ft = Math.max(0, count12Ft);
    }

    public void setAbsorbSetCount(int absorbSetCount) {
        this.absorbSetCount = Math.max(0, absorbSetCount);
    }

    public void setConnector20FtCount(int connector20FtCount) {
        this.connector20FtCount = Math.max(0, connector20FtCount);
    }

    public void setConnector12FtCount(int connector12FtCount) {
        this.connector12FtCount = Math.max(0, connector12FtCount);
    }

    // ================= CORE INVENTORY LOGIC =================

    public boolean updateCounts(int count20Ft,
                                int count12Ft,
                                int absorbSetCount,
                                int connector20FtCount,
                                int connector12FtCount,
                                String employeeName,
                                String timestamp) {

        if (count20Ft < 0 || count12Ft < 0 || absorbSetCount < 0
                || connector20FtCount < 0 || connector12FtCount < 0) {
            return false;
        }

        this.count20Ft = count20Ft;
        this.count12Ft = count12Ft;
        this.absorbSetCount = absorbSetCount;
        this.connector20FtCount = connector20FtCount;
        this.connector12FtCount = connector12FtCount;
        this.lastUpdatedBy = safeUser(employeeName);
        this.lastUpdatedTime = safeTime(timestamp);

        return true;
    }

    public void markUpdated(String employeeName, String timestamp) {
        this.lastUpdatedBy = safeUser(employeeName);
        this.lastUpdatedTime = safeTime(timestamp);
    }

    // Equipment at stockpile
    public boolean addForklift(String unitId, String employeeName, String timestamp) {
        if (unitId == null || unitId.isBlank()) {
            return false;
        }

        String trimmed = unitId.trim();
        if (getForkliftIds().contains(trimmed)) {
            return false;
        }

        getForkliftIds().add(trimmed);
        markUpdated(employeeName, timestamp);
        return true;
    }

    public boolean removeForklift(String unitId, String employeeName, String timestamp) {
        if (unitId == null || unitId.isBlank()) {
            return false;
        }

        boolean removed = getForkliftIds().remove(unitId.trim());
        if (removed) {
            markUpdated(employeeName, timestamp);
        }
        return removed;
    }

    public boolean addGradall(String unitId, String employeeName, String timestamp) {
        if (unitId == null || unitId.isBlank()) {
            return false;
        }

        String trimmed = unitId.trim();
        if (getGradallIds().contains(trimmed)) {
            return false;
        }

        getGradallIds().add(trimmed);
        markUpdated(employeeName, timestamp);
        return true;
    }

    public boolean removeGradall(String unitId, String employeeName, String timestamp) {
        if (unitId == null || unitId.isBlank()) {
            return false;
        }

        boolean removed = getGradallIds().remove(unitId.trim());
        if (removed) {
            markUpdated(employeeName, timestamp);
        }
        return removed;
    }

    // Backward compatibility
    public boolean setBarrierCount(int newCount, String employeeName, String timestamp) {
        if (newCount < 0) {
            return false;
        }

        this.count20Ft = newCount;
        this.count12Ft = 0;
        this.absorbSetCount = 0;
        this.connector20FtCount = 0;
        this.connector12FtCount = 0;
        this.lastUpdatedBy = safeUser(employeeName);
        this.lastUpdatedTime = safeTime(timestamp);

        return true;
    }

    public boolean updateBarrierCount(int newCount, String employeeName, String timestamp) {
        return setBarrierCount(newCount, employeeName, timestamp);
    }

    public boolean addBarriers(int quantity, String employeeName, String timestamp) {
        if (quantity <= 0) {
            return false;
        }

        this.count20Ft += quantity;
        this.lastUpdatedBy = safeUser(employeeName);
        this.lastUpdatedTime = safeTime(timestamp);
        return true;
    }

    public boolean removeBarriers(int quantity, String employeeName, String timestamp) {
        if (quantity <= 0 || quantity > getTotalBarrierPieces()) {
            return false;
        }

        int remaining = quantity;

        if (count20Ft >= remaining) {
            count20Ft -= remaining;
            remaining = 0;
        } else {
            remaining -= count20Ft;
            count20Ft = 0;
        }

        if (remaining > 0) {
            if (connector20FtCount >= remaining) {
                connector20FtCount -= remaining;
                remaining = 0;
            } else {
                remaining -= connector20FtCount;
                connector20FtCount = 0;
            }
        }

        if (remaining > 0) {
            if (count12Ft >= remaining) {
                count12Ft -= remaining;
                remaining = 0;
            } else {
                remaining -= count12Ft;
                count12Ft = 0;
            }
        }

        if (remaining > 0) {
            if (connector12FtCount >= remaining) {
                connector12FtCount -= remaining;
                remaining = 0;
            } else {
                return false;
            }
        }

        this.lastUpdatedBy = safeUser(employeeName);
        this.lastUpdatedTime = safeTime(timestamp);
        return true;
    }

    public boolean hasEnoughBarriers(int quantity) {
        return quantity > 0 && getTotalBarrierPieces() >= quantity;
    }

    // ================= HELPERS =================

    private String safeUser(String value) {
        return value == null || value.isBlank() ? "Unknown" : value.trim();
    }

    private String safeTime(String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }

    // ================= DISPLAY =================

    @Override
    public String toString() {
        return name + " (" + location + ") - Total Feet: " + getTotalLinearFeet();
    }
}