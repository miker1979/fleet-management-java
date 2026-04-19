import java.io.Serializable;

public class Gradall implements Serializable {
    private static final long serialVersionUID = 1L;

    private String unitId;
    private int year;
    private String make;
    private String model;
    private String vin;
    private String color;
    private String engineModel;
    private String engineType;
    private String tireSize;
    private int hours;   // 🔥 important: NOT mileage

    public Gradall(String unitId, int year, String make, String model,
                   String vin, String color, String engineModel,
                   String engineType, String tireSize, int hours) {

        this.unitId = unitId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.vin = vin;
        this.color = color;
        this.engineModel = engineModel;
        this.engineType = engineType;
        this.tireSize = tireSize;
        this.hours = hours;
    }

    public String getUnitId() { return unitId; }
    public int getYear() { return year; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getVin() { return vin; }
    public String getColor() { return color; }
    public String getEngineModel() { return engineModel; }
    public String getEngineType() { return engineType; }
    public String getTireSize() { return tireSize; }
    public int getHours() { return hours; }

    @Override
    public String toString() {
        return "Gradall " + unitId + " | " + make + " " + model + " | Hours: " + hours;
    }
}