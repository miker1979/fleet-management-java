import java.time.DayOfWeek;
import java.time.LocalDate;

public class DispatchWindowBuilder {

    public static DispatchWindow buildMondaySundayNextWeek(LocalDate today) {
        LocalDate nextMonday = today.with(DayOfWeek.MONDAY);
        if (!nextMonday.isAfter(today)) {
            nextMonday = nextMonday.plusWeeks(1);
        }

        LocalDate nextSunday = nextMonday.plusDays(6);
        return new DispatchWindow(nextMonday, nextSunday, "Next Monday-Sunday");
    }

    public static DispatchWindow buildCompanyCycle(LocalDate today, int startDayOfWeek, int cycleLengthDays, String label) {
        DayOfWeek day = convertDay(startDayOfWeek);

        LocalDate cycleStart = today.with(day);
        if (!cycleStart.isAfter(today)) {
            cycleStart = cycleStart.plusWeeks(1);
        }

        LocalDate cycleEnd = cycleStart.plusDays(cycleLengthDays - 1);
        return new DispatchWindow(cycleStart, cycleEnd, label);
    }

    public static DispatchWindow buildNextDays(LocalDate today, int numberOfDays) {
        LocalDate start = today.plusDays(1);
        LocalDate end = start.plusDays(numberOfDays - 1);
        return new DispatchWindow(start, end, "Next " + numberOfDays + " Days");
    }

    public static DispatchWindow buildCustom(LocalDate start, LocalDate end) {
        return new DispatchWindow(start, end, "Custom Range");
    }

    private static DayOfWeek convertDay(int dayNumber) {
        switch (dayNumber) {
            case 1: return DayOfWeek.MONDAY;
            case 2: return DayOfWeek.TUESDAY;
            case 3: return DayOfWeek.WEDNESDAY;
            case 4: return DayOfWeek.THURSDAY;
            case 5: return DayOfWeek.FRIDAY;
            case 6: return DayOfWeek.SATURDAY;
            case 7: return DayOfWeek.SUNDAY;
            default: return DayOfWeek.MONDAY;
        }
    }
}