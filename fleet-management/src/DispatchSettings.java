public class DispatchSettings {

    private boolean autoDispatchEnabled;
    private boolean allowCustomDateRange;
    private boolean useCompanyDefaultCycle;

    private String defaultPresetName;      // "Monday-Sunday", "Wednesday-Tuesday", "Next 7 Days", "Custom"
    private int cycleLengthDays;           // 7, 5, 14, etc.
    private int cycleStartDayOfWeek;       // 1=Monday ... 7=Sunday
    private boolean reserveEmergencyCapacity;
    private boolean previewBeforeSave;
    private boolean autoAssignTrucks;

    public DispatchSettings() {
        this.autoDispatchEnabled = false;
        this.allowCustomDateRange = true;
        this.useCompanyDefaultCycle = true;
        this.defaultPresetName = "Monday-Sunday";
        this.cycleLengthDays = 7;
        this.cycleStartDayOfWeek = 1;
        this.reserveEmergencyCapacity = false;
        this.previewBeforeSave = true;
        this.autoAssignTrucks = true;
    }

    public boolean isAutoDispatchEnabled() {
        return autoDispatchEnabled;
    }

    public void setAutoDispatchEnabled(boolean autoDispatchEnabled) {
        this.autoDispatchEnabled = autoDispatchEnabled;
    }

    public boolean isAllowCustomDateRange() {
        return allowCustomDateRange;
    }

    public void setAllowCustomDateRange(boolean allowCustomDateRange) {
        this.allowCustomDateRange = allowCustomDateRange;
    }

    public boolean isUseCompanyDefaultCycle() {
        return useCompanyDefaultCycle;
    }

    public void setUseCompanyDefaultCycle(boolean useCompanyDefaultCycle) {
        this.useCompanyDefaultCycle = useCompanyDefaultCycle;
    }

    public String getDefaultPresetName() {
        return defaultPresetName;
    }

    public void setDefaultPresetName(String defaultPresetName) {
        this.defaultPresetName = defaultPresetName;
    }

    public int getCycleLengthDays() {
        return cycleLengthDays;
    }

    public void setCycleLengthDays(int cycleLengthDays) {
        this.cycleLengthDays = cycleLengthDays;
    }

    public int getCycleStartDayOfWeek() {
        return cycleStartDayOfWeek;
    }

    public void setCycleStartDayOfWeek(int cycleStartDayOfWeek) {
        this.cycleStartDayOfWeek = cycleStartDayOfWeek;
    }

    public boolean isReserveEmergencyCapacity() {
        return reserveEmergencyCapacity;
    }

    public void setReserveEmergencyCapacity(boolean reserveEmergencyCapacity) {
        this.reserveEmergencyCapacity = reserveEmergencyCapacity;
    }

    public boolean isPreviewBeforeSave() {
        return previewBeforeSave;
    }

    public void setPreviewBeforeSave(boolean previewBeforeSave) {
        this.previewBeforeSave = previewBeforeSave;
    }

    public boolean isAutoAssignTrucks() {
        return autoAssignTrucks;
    }

    public void setAutoAssignTrucks(boolean autoAssignTrucks) {
        this.autoAssignTrucks = autoAssignTrucks;
    }
}