import java.io.Serializable;

public class TCB implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int LENGTH_20 = 20;
    public static final int LENGTH_12 = 12;

    private int lengthFeet;

    // Straight run barrier pieces
    private int straightPieceCount;

    // Barrier pieces used as absorb barrier sections
    private int absorbPieceCount;

    // Total requested task footage
    private int requestedLinearFeet;

    public TCB(int lengthFeet, int straightPieceCount, int absorbPieceCount, int requestedLinearFeet) {
        this.lengthFeet = lengthFeet;
        this.straightPieceCount = straightPieceCount;
        this.absorbPieceCount = absorbPieceCount;
        this.requestedLinearFeet = requestedLinearFeet;
    }

    public int getLengthFeet() {
        return lengthFeet;
    }

    public void setLengthFeet(int lengthFeet) {
        this.lengthFeet = lengthFeet;
    }

    public int getStraightPieceCount() {
        return straightPieceCount;
    }

    public void setStraightPieceCount(int straightPieceCount) {
        this.straightPieceCount = straightPieceCount;
    }

    public int getAbsorbPieceCount() {
        return absorbPieceCount;
    }

    public void setAbsorbPieceCount(int absorbPieceCount) {
        this.absorbPieceCount = absorbPieceCount;
    }

    public int getRequestedLinearFeet() {
        return requestedLinearFeet;
    }

    public void setRequestedLinearFeet(int requestedLinearFeet) {
        this.requestedLinearFeet = requestedLinearFeet;
    }

    public int getAbsorbFootageUsed() {
        return absorbPieceCount * lengthFeet;
    }

    public int getRemainingStraightFootage() {
        int remaining = requestedLinearFeet - getAbsorbFootageUsed();
        return Math.max(remaining, 0);
    }

    public int getTotalPieceCount() {
        return straightPieceCount + absorbPieceCount;
    }

    public int getTotalBarrierFootageLoaded() {
        return getTotalPieceCount() * lengthFeet;
    }

    @Override
    public String toString() {
        return lengthFeet + " ft TCB" +
               " | Requested LF: " + requestedLinearFeet +
               " | Straight Pieces: " + straightPieceCount +
               " | Absorb Pieces: " + absorbPieceCount +
               " | Total Pieces: " + getTotalPieceCount() +
               " | Absorb LF Used: " + getAbsorbFootageUsed() +
               " | Remaining Straight LF: " + getRemainingStraightFootage();
    }
}