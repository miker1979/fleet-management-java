import java.time.LocalDate;

public class DispatchWindow {

    private LocalDate startDate;
    private LocalDate endDate;
    private String label;

    public DispatchWindow(LocalDate startDate, LocalDate endDate, String label) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.label = label;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getLabel() {
        return label;
    }
}