import java.io.Serializable;

public class TIA implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String NONE = "None";
    public static final String NEW_STYLE = "New Style (3 Each)";
    public static final String OLD_STYLE = "Old Style (10 Each)";

    private String type;
    private int setCount;

    public TIA(String type, int setCount) {
        this.type = type;
        this.setCount = setCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSetCount() {
        return setCount;
    }

    public void setSetCount(int setCount) {
        this.setCount = setCount;
    }

    public int getPlasticUnitCount() {
        if (type == null || type.equalsIgnoreCase(NONE)) {
            return 0;
        }

        if (type.equalsIgnoreCase(NEW_STYLE)) {
            return setCount * 3;
        }

        if (type.equalsIgnoreCase(OLD_STYLE)) {
            return setCount * 10;
        }

        return 0;
    }

    public boolean isNone() {
        return type == null || type.equalsIgnoreCase(NONE) || setCount <= 0;
    }

    @Override
    public String toString() {
        if (isNone()) {
            return "No TIA";
        }

        return type +
               " | Sets: " + setCount +
               " | Plastic Units: " + getPlasticUnitCount();
    }
}