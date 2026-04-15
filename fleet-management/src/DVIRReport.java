import java.util.ArrayList;
import java.util.List;

public class DVIRReport {

    private int reportId;

    private String carrier;
    private String date;
    private String time;
    private String driverName;

    private String truckId;
    private int odometerReading;
    private String trailerId;

    private List<String> truckDefects;
    private List<String> trailerDefects;

    private String remarks;

    private boolean satisfactory;
    private boolean defectsCorrected;
    private boolean safeToOperate;

    private String mechanicSignature;
    private String mechanicDate;
    private String driverSignature;

    public DVIRReport(int reportId, String carrier, String date, String time,
                      String driverName, String truckId, int odometerReading,
                      String trailerId, List<String> truckDefects,
                      List<String> trailerDefects, String remarks,
                      boolean satisfactory, boolean defectsCorrected,
                      boolean safeToOperate, String mechanicSignature,
                      String mechanicDate, String driverSignature) {

        this.reportId = reportId;
        this.carrier = carrier;
        this.date = date;
        this.time = time;
        this.driverName = driverName;
        this.truckId = truckId;
        this.odometerReading = odometerReading;
        this.trailerId = trailerId;
        this.truckDefects = truckDefects != null ? new ArrayList<>(truckDefects) : new ArrayList<>();
        this.trailerDefects = trailerDefects != null ? new ArrayList<>(trailerDefects) : new ArrayList<>();
        this.remarks = remarks;
        this.satisfactory = satisfactory;
        this.defectsCorrected = defectsCorrected;
        this.safeToOperate = safeToOperate;
        this.mechanicSignature = mechanicSignature;
        this.mechanicDate = mechanicDate;
        this.driverSignature = driverSignature;
    }

    public int getReportId() {
        return reportId;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getTruckId() {
        return truckId;
    }

    public int getOdometerReading() {
        return odometerReading;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public List<String> getTruckDefects() {
        return truckDefects;
    }

    public List<String> getTrailerDefects() {
        return trailerDefects;
    }

    public String getRemarks() {
        return remarks;
    }

    public boolean isSatisfactory() {
        return satisfactory;
    }

    public boolean isDefectsCorrected() {
        return defectsCorrected;
    }

    public boolean isSafeToOperate() {
        return safeToOperate;
    }

    public String getMechanicSignature() {
        return mechanicSignature;
    }

    public String getMechanicDate() {
        return mechanicDate;
    }

    public String getDriverSignature() {
        return driverSignature;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setSatisfactory(boolean satisfactory) {
        this.satisfactory = satisfactory;
    }

    public void setDefectsCorrected(boolean defectsCorrected) {
        this.defectsCorrected = defectsCorrected;
    }

    public void setSafeToOperate(boolean safeToOperate) {
        this.safeToOperate = safeToOperate;
    }

    public void setMechanicSignature(String mechanicSignature) {
        this.mechanicSignature = mechanicSignature;
    }

    public void setMechanicDate(String mechanicDate) {
        this.mechanicDate = mechanicDate;
    }

    public void setDriverSignature(String driverSignature) {
        this.driverSignature = driverSignature;
    }

    @Override
    public String toString() {
        return "DVIR #" + reportId + " | " + date + " | Driver: " + driverName
                + " | Truck: " + truckId + " | Trailer: " + trailerId;
    }
}