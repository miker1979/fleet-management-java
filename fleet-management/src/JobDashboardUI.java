import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class JobDashboardUI extends JFrame {

    private final FleetManager manager;
    private final Job job;

    private DefaultTableModel taskModel;

    public JobDashboardUI(FleetManager manager, Job job) {
        this.manager = manager;
        this.job = job;

        setTitle("Job Dashboard - Job #" + job.getJobNumber());
        setSize(1500, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(15, 15, 15));
        setLayout(new BorderLayout(12, 12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        refreshDashboard();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(15, 15, 15));
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel title = new JLabel("ACTIVE JOB DASHBOARD");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.YELLOW);

        JLabel subtitle = new JLabel("Job #" + job.getJobNumber() + " - " + safe(job.getProjectName()));
        subtitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        subtitle.setForeground(Color.WHITE);

        header.add(title);
        header.add(subtitle);

        return header;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBackground(new Color(15, 15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JPanel top = new JPanel(new GridLayout(1, 3, 12, 12));
        top.setBackground(new Color(15, 15, 15));

        top.add(buildJobInfoPanel());
        top.add(buildStatsPanel());
        top.add(buildNotesPanel());

        main.add(top, BorderLayout.NORTH);
        main.add(buildTaskTablePanel(), BorderLayout.CENTER);

        return main;
    }

    private JPanel buildJobInfoPanel() {
        JPanel panel = createBoxPanel("Job Information");

        addInfo(panel, "Job #:", String.valueOf(job.getJobNumber()));
        addInfo(panel, "Project:", safe(job.getProjectName()));
        addInfo(panel, "Contractor:", safe(job.getContractor()));
        addInfo(panel, "Location:", safe(job.getLocation()));
        addInfo(panel, "Status:", safe(job.getStatus()));
        addInfo(panel, "Open Time:", job.getOpenDuration());
        addInfo(panel, "Start:", safe(job.getStartDate()));
        addInfo(panel, "End:", safe(job.getEndDate()));

        return panel;
    }

    private JPanel buildStatsPanel() {
        JPanel panel = createBoxPanel("Job Stats");

        ArrayList<Task> tasks = manager.getTasksByJobId(job.getJobNumber());
        ArrayList<TaskSummary> summaries = manager.getTaskSummariesByJobId(job.getJobNumber());

        int totalTasks = tasks.size();
        int installs = countTasksByType(tasks, "Install");
        int removes = countTasksByType(tasks, "Remove");
        int relocates = countTasksByType(tasks, "Relocate");

        int completedTasks = countTasksByStatus(tasks, "Completed");
        int dispatchedTasks = countDispatchedOrCompleted(tasks);

        int totalBarrier = 0;
        int totalAbsorbs = 0;
        double totalPersonHours = 0.0;

        for (TaskSummary summary : summaries) {
            totalBarrier += summary.getBarrierCount();
            totalAbsorbs += summary.getAbsorbSetCount();
            totalPersonHours += summary.getPersonHours();
        }

        addInfo(panel, "Total Tasks:", String.valueOf(totalTasks));
        addInfo(panel, "Installs:", String.valueOf(installs));
        addInfo(panel, "Removes:", String.valueOf(removes));
        addInfo(panel, "Relocates:", String.valueOf(relocates));
        addInfo(panel, "Dispatched Tasks:", String.valueOf(dispatchedTasks));
        addInfo(panel, "Completed Tasks:", String.valueOf(completedTasks));
        addInfo(panel, "Barrier Total:", String.valueOf(totalBarrier));
        addInfo(panel, "Absorb Sets:", String.valueOf(totalAbsorbs));
        addInfo(panel, "Person-Hours:", String.format("%.2f", totalPersonHours));

        return panel;
    }

    private JPanel buildNotesPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(new Color(25, 25, 25));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Job Notes / Summary",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE
        ));

        JTextArea notesArea = new JTextArea();
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(new Color(20, 20, 20));
        notesArea.setForeground(Color.WHITE);
        notesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        StringBuilder sb = new StringBuilder();

        if (safe(job.getNotes()).isBlank()) {
            sb.append("No notes entered.\n\n");
        } else {
            sb.append(job.getNotes()).append("\n\n");
        }

        ArrayList<TaskSummary> summaries = manager.getTaskSummariesByJobId(job.getJobNumber());

        sb.append("Completed Summaries: ").append(summaries.size()).append("\n");

        int totalBarrier = 0;
        int totalAbsorbs = 0;
        double totalPersonHours = 0.0;

        for (TaskSummary summary : summaries) {
            totalBarrier += summary.getBarrierCount();
            totalAbsorbs += summary.getAbsorbSetCount();
            totalPersonHours += summary.getPersonHours();

            if (!safe(summary.getDelayNotes()).isBlank()) {
                sb.append("\nDelay: ").append(summary.getDelayNotes());
            }
            if (!safe(summary.getBreakdownNotes()).isBlank()) {
                sb.append("\nBreakdown: ").append(summary.getBreakdownNotes());
            }
            if (!safe(summary.getEquipmentIssueNotes()).isBlank()) {
                sb.append("\nEquipment Issue: ").append(summary.getEquipmentIssueNotes());
            }
            if (!safe(summary.getMaterialIssueNotes()).isBlank()) {
                sb.append("\nMaterial Issue: ").append(summary.getMaterialIssueNotes());
            }
            if (!safe(summary.getSafetyNotes()).isBlank()) {
                sb.append("\nSafety Note: ").append(summary.getSafetyNotes());
            }
        }

        sb.append("\n\nBarrier Total: ").append(totalBarrier);
        sb.append("\nAbsorb Sets: ").append(totalAbsorbs);
        sb.append("\nPerson-Hours: ").append(String.format("%.2f", totalPersonHours));

        notesArea.setText(sb.toString());

        panel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildTaskTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 15, 15));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Tasks On This Job",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE
        ));

        String[] columns = {
            "Task #",
            "Date",
             "Start",
            "End",
             "Type",
             "Foreman",
             "Crew Count",
             "Task Linear Feet",
            "Summary Barrier",
            "Absorbs",
            "Person-Hours",
             "Status"
};

        taskModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(taskModel);
        styleTable(table);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void refreshDashboard() {
        taskModel.setRowCount(0);

        ArrayList<Task> tasks = manager.getTasksByJobId(job.getJobNumber());

        tasks.sort((a, b) -> safe(a.getStartDate()).compareToIgnoreCase(safe(b.getStartDate())));

        for (Task task : tasks) {
            TaskSummary summary = manager.findTaskSummaryByTaskId(task.getTaskId());

            int summaryBarrier = summary == null ? 0 : summary.getBarrierCount();
            int absorbs = summary == null ? 0 : summary.getAbsorbSetCount();
            double personHours = summary == null ? 0.0 : summary.getPersonHours();

            taskModel.addRow(new Object[]{
                    task.getTaskId(),
                    safe(task.getStartDate()),
                    safe(task.getStartTime()),
                    safe(task.getEndTime()),
                    safe(task.getJobType()),
                    safe(task.getForeman()),
                    task.getTotalAssignedPersonnelCount(),
                    task.getLinearFeet(),
                    summaryBarrier,
                    absorbs,
                    String.format("%.2f", personHours),
                    safe(task.getStatus()),
                    buildEquipmentDisplay(task)
            });
        }
    }

    private JPanel createBoxPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 8));
        panel.setBackground(new Color(25, 25, 25));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title,
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE
        ));
        return panel;
    }

    private void addInfo(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setForeground(Color.LIGHT_GRAY);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setForeground(Color.WHITE);
        valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(60, 60, 60));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(45, 45, 45));
        header.setForeground(Color.CYAN);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private int countTasksByType(ArrayList<Task> tasks, String type) {
        int count = 0;

        for (Task task : tasks) {
            if (task != null && safe(task.getJobType()).equalsIgnoreCase(type)) {
                count++;
            }
        }

        return count;
    }

    private int countTasksByStatus(ArrayList<Task> tasks, String status) {
        int count = 0;

        for (Task task : tasks) {
            if (task != null && safe(task.getStatus()).equalsIgnoreCase(status)) {
                count++;
            }
        }

        return count;
    }

    private int countDispatchedOrCompleted(ArrayList<Task> tasks) {
        int count = 0;

        for (Task task : tasks) {
            String status = safe(task.getStatus());

            if (status.equalsIgnoreCase("Dispatched")
                    || status.equalsIgnoreCase("Completed")) {
                count++;
            }
        }

        return count;
    }

    private String buildEquipmentDisplay(Task task) {
        int forkliftCount = task.getAssignedForklifts() == null ? 0 : task.getAssignedForklifts().size();
        int equipmentCount = task.getRequiredEquipment() == null ? 0 : task.getRequiredEquipment().size();

        return "Forklifts: " + forkliftCount + " | Equipment: " + equipmentCount;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}