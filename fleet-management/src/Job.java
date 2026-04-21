import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Job implements Serializable {
    private static final long serialVersionUID = 1L;

    // Core fields
    private int jobNumber;
    private String contractor;
    private String projectName;
    private String startDate;
    private String endDate;
    private String location;

    // Detail fields
    private String status;
    private String projectManager;
    private String dotProjectNumber;
    private String barrierType;
    private int totalLinearFeet;
    private String notes;

    public Job(int jobNumber, String contractor, String projectName,
               String startDate, String endDate, String location) {

        this.jobNumber = jobNumber;
        this.contractor = contractor == null ? "" : contractor;
        this.projectName = projectName == null ? "" : projectName;
        this.startDate = startDate == null ? "" : startDate;
        this.endDate = endDate == null ? "" : endDate;
        this.location = location == null ? "" : location;

        this.status = "Active";
        this.projectManager = "";
        this.dotProjectNumber = "";
        this.barrierType = "";
        this.totalLinearFeet = 0;
        this.notes = "";
    }

    public String getOpenDuration() {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate effectiveEnd;

            if ("Active".equalsIgnoreCase(status) || endDate == null || endDate.isBlank()) {
                effectiveEnd = LocalDate.now();
            } else {
                effectiveEnd = LocalDate.parse(endDate);
            }

            if (effectiveEnd.isBefore(start)) {
                return "Invalid dates";
            }

            Period period = Period.between(start, effectiveEnd);
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();
            int weeks = days / 7;

            StringBuilder sb = new StringBuilder();
            if (years > 0) {
                sb.append(years).append(years == 1 ? " year" : " years");
            }
            if (months > 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(months).append(months == 1 ? " month" : " months");
            }
            if (weeks > 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(weeks).append(weeks == 1 ? " week" : " weeks");
            }

            if (sb.length() == 0) {
                long daysOpen = ChronoUnit.DAYS.between(start, effectiveEnd);
                if (daysOpen <= 0) {
                    return "Today";
                }
                return daysOpen + (daysOpen == 1 ? " day" : " days");
            }

            return sb.toString();
        } catch (Exception e) {
            return "N/A";
        }
    }

    public int getTaskCount(List<Task> tasks) {
        return (int) tasks.stream()
                .filter(t -> t != null && t.getJobId() == jobNumber)
                .count();
    }

    public int getInstallCount(List<Task> tasks) {
        return (int) tasks.stream()
                .filter(t -> t != null && t.getJobId() == jobNumber)
                .filter(t -> "Install".equalsIgnoreCase(t.getJobType()))
                .count();
    }

    public int getRemoveCount(List<Task> tasks) {
        return (int) tasks.stream()
                .filter(t -> t != null && t.getJobId() == jobNumber)
                .filter(t -> "Remove".equalsIgnoreCase(t.getJobType()))
                .count();
    }

    public int getRelocateCount(List<Task> tasks) {
        return (int) tasks.stream()
                .filter(t -> t != null && t.getJobId() == jobNumber)
                .filter(t -> "Relocate".equalsIgnoreCase(t.getJobType()))
                .count();
    }

    public double getTotalManHours(List<Task> tasks) {
        double total = 0.0;

        for (Task task : tasks) {
            if (task == null || task.getJobId() != jobNumber) {
                continue;
            }

            int crewSize = task.getAssignedEmployeeIds() == null ? 0 : task.getAssignedEmployeeIds().size();
            if (crewSize == 0) {
                continue;
            }

            try {
                int start = Integer.parseInt(task.getStartTime().replace(":", ""));
                int end = Integer.parseInt(task.getEndTime().replace(":", ""));
                double hours = (end - start) / 100.0;
                if (hours > 0) {
                    total += hours * crewSize;
                }
            } catch (Exception ignored) {
                // Keep going if one task has bad time data
            }
        }

        return total;
    }

    public String getJobSummary(List<Task> tasks) {
        return "Job #" + jobNumber
                + "\nProject: " + projectName
                + "\nContractor: " + contractor
                + "\nLocation: " + location
                + "\nStatus: " + status
                + "\nOpen: " + getOpenDuration()
                + "\nTasks: " + getTaskCount(tasks)
                + "\nInstall: " + getInstallCount(tasks)
                + "\nRemove: " + getRemoveCount(tasks)
                + "\nRelocate: " + getRelocateCount(tasks)
                + "\nMan Hours: " + String.format("%.2f", getTotalManHours(tasks));
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public String getContractor() {
        return contractor;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public String getDotProjectNumber() {
        return dotProjectNumber;
    }

    public String getBarrierType() {
        return barrierType;
    }

    public int getTotalLinearFeet() {
        return totalLinearFeet;
    }

    public String getNotes() {
        return notes;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor == null ? "" : contractor;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? "" : projectName;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate == null ? "" : startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate == null ? "" : endDate;
    }

    public void setLocation(String location) {
        this.location = location == null ? "" : location;
    }

    public void setStatus(String status) {
        this.status = status == null ? "" : status;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager == null ? "" : projectManager;
    }

    public void setDotProjectNumber(String dotProjectNumber) {
        this.dotProjectNumber = dotProjectNumber == null ? "" : dotProjectNumber;
    }

    public void setBarrierType(String barrierType) {
        this.barrierType = barrierType == null ? "" : barrierType;
    }

    public void setTotalLinearFeet(int totalLinearFeet) {
        this.totalLinearFeet = totalLinearFeet;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
    }

    @Override
    public String toString() {
        return jobNumber + " | " + projectName + " | " + contractor;
    }
}
