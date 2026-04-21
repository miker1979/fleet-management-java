import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class JobDashboardUI extends JFrame {

    private final FleetManager manager;
    private final Job job;

    private JLabel jobNumberValue;
    private JLabel projectValue;
    private JLabel contractorValue;
    private JLabel locationValue;
    private JLabel statusValue;
    private JLabel durationValue;
    private JLabel startValue;
    private JLabel endValue;

    private JLabel totalTasksValue;
    private JLabel installsValue;
    private JLabel removesValue;
    private JLabel relocatesValue;
    private JLabel manHoursValue;
    private JLabel totalFeetValue;

    private JTextArea notesArea;
    private JTextArea summaryArea;

    private JTable taskTable;
    private DefaultTableModel taskTableModel;

    public JobDashboardUI(FleetManager manager, Job job) {
        this.manager = manager;
        this.job = job;

        setTitle("Job Dashboard - Job #" + job.getJobNumber());
        setSize(1325, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(18, 18, 18));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadData();
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel title = new JLabel("ACTIVE JOB DASHBOARD");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(255, 215, 0));

        JLabel subtitle = new JLabel("Job #" + job.getJobNumber() + " - " + safe(job.getProjectName()));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(Color.WHITE);

        panel.add(title, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JPanel topGrid = new JPanel(new GridLayout(1, 3, 12, 12));
        topGrid.setOpaque(false);
        topGrid.add(buildJobInfoPanel());
        topGrid.add(buildStatsPanel());
        topGrid.add(buildSummaryPanel());

        center.add(topGrid, BorderLayout.NORTH);
        center.add(buildTasksPanel(), BorderLayout.CENTER);

        return center;
    }

    private JPanel buildJobInfoPanel() {
        JPanel panel = createSectionPanel("Job Information");
        panel.setLayout(new GridBagLayout());

        jobNumberValue = createValueLabel();
        projectValue = createValueLabel();
        contractorValue = createValueLabel();
        locationValue = createValueLabel();
        statusValue = createValueLabel();
        durationValue = createValueLabel();
        startValue = createValueLabel();
        endValue = createValueLabel();

        GridBagConstraints gbc = baseGbc();
        addRow(panel, gbc, 0, "Job #:", jobNumberValue);
        addRow(panel, gbc, 1, "Project:", projectValue);
        addRow(panel, gbc, 2, "Contractor:", contractorValue);
        addRow(panel, gbc, 3, "Location:", locationValue);
        addRow(panel, gbc, 4, "Status:", statusValue);
        addRow(panel, gbc, 5, "Open Time:", durationValue);
        addRow(panel, gbc, 6, "Start:", startValue);
        addRow(panel, gbc, 7, "End:", endValue);

        return panel;
    }

    private JPanel buildStatsPanel() {
        JPanel panel = createSectionPanel("Job Stats");
        panel.setLayout(new GridBagLayout());

        totalTasksValue = createHighlightLabel(new Color(0, 255, 255));
        installsValue = createHighlightLabel(new Color(0, 255, 150));
        removesValue = createHighlightLabel(new Color(255, 120, 120));
        relocatesValue = createHighlightLabel(new Color(255, 215, 0));
        manHoursValue = createHighlightLabel(new Color(255, 255, 255));
        totalFeetValue = createHighlightLabel(new Color(180, 220, 255));

        GridBagConstraints gbc = baseGbc();
        addRow(panel, gbc, 0, "Total Tasks:", totalTasksValue);
        addRow(panel, gbc, 1, "Installs:", installsValue);
        addRow(panel, gbc, 2, "Removes:", removesValue);
        addRow(panel, gbc, 3, "Relocates:", relocatesValue);
        addRow(panel, gbc, 4, "Man-Hours:", manHoursValue);
        addRow(panel, gbc, 5, "Linear Feet:", totalFeetValue);

        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = createSectionPanel("Job Notes / Summary");
        panel.setLayout(new BorderLayout(8, 8));

        notesArea = new JTextArea();
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        notesArea.setBackground(new Color(28, 28, 28));
        notesArea.setForeground(Color.WHITE);

        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        summaryArea.setBackground(new Color(22, 22, 22));
        summaryArea.setForeground(new Color(220, 220, 220));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(notesArea),
                new JScrollPane(summaryArea)
        );
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTasksPanel() {
        JPanel panel = createSectionPanel("Tasks On This Job");
        panel.setLayout(new BorderLayout(8, 8));

        String[] columns = {
                "Task #", "Date", "Start", "End", "Type", "Foreman", "Crew Count",
                "Linear Feet", "Status", "Equipment"
        };

        taskTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(taskTableModel);
        taskTable.setAutoCreateRowSorter(true);
        taskTable.setRowHeight(28);
        taskTable.setBackground(new Color(30, 30, 30));
        taskTable.setForeground(Color.WHITE);
        taskTable.setSelectionBackground(new Color(65, 65, 65));
        taskTable.setSelectionForeground(Color.WHITE);
        taskTable.getTableHeader().setBackground(new Color(45, 45, 45));
        taskTable.getTableHeader().setForeground(new Color(0, 255, 255));
        taskTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        panel.setBackground(new Color(18, 18, 18));

        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        styleButton(refreshBtn, new Color(0, 200, 255));
        styleButton(closeBtn, new Color(255, 100, 100));

        refreshBtn.addActionListener(e -> loadData());
        closeBtn.addActionListener(e -> dispose());

        panel.add(refreshBtn);
        panel.add(closeBtn);

        return panel;
    }

    private void loadData() {
        ArrayList<Task> jobTasks = manager.getTasksByJobId(job.getJobNumber());

        jobNumberValue.setText(String.valueOf(job.getJobNumber()));
        projectValue.setText(safe(job.getProjectName()));
        contractorValue.setText(safe(job.getContractor()));
        locationValue.setText(safe(job.getLocation()));
        statusValue.setText(safe(job.getStatus()));
        durationValue.setText(safe(job.getOpenDuration()));
        startValue.setText(safe(job.getStartDate()));
        endValue.setText(safe(job.getEndDate()));

        totalTasksValue.setText(String.valueOf(manager.getTaskCountForJob(job.getJobNumber())));
        installsValue.setText(String.valueOf(manager.getTaskCountByType(job.getJobNumber(), "Install")));
        removesValue.setText(String.valueOf(manager.getTaskCountByType(job.getJobNumber(), "Remove")));
        relocatesValue.setText(String.valueOf(manager.getTaskCountByType(job.getJobNumber(), "Relocate")));
        manHoursValue.setText(formatDouble(manager.getTotalManHoursForJob(job.getJobNumber())));
        totalFeetValue.setText(String.valueOf(job.getTotalLinearFeet()));

        notesArea.setText(safe(job.getNotes()).isBlank() ? "No notes entered." : job.getNotes());
        summaryArea.setText(buildSummary(jobTasks));

        taskTableModel.setRowCount(0);
        for (Task task : jobTasks) {
            taskTableModel.addRow(new Object[]{
                    task.getTaskId(),
                    safe(task.getStartDate()),
                    safe(task.getStartTime()),
                    safe(task.getEndTime()),
                    safe(task.getJobType()),
                    safe(task.getForeman()),
                    getCrewCount(task),
                    task.getLinearFeet(),
                    safe(task.getStatus()),
                    buildEquipmentCell(task)
            });
        }
    }

    private String buildSummary(ArrayList<Task> jobTasks) {
        StringBuilder sb = new StringBuilder();

        sb.append("Open Time: ").append(job.getOpenDuration()).append("\n");
        sb.append("Tasks: ").append(manager.getTaskCountForJob(job.getJobNumber())).append("\n");
        sb.append("Installs: ").append(manager.getTaskCountByType(job.getJobNumber(), "Install")).append("\n");
        sb.append("Removes: ").append(manager.getTaskCountByType(job.getJobNumber(), "Remove")).append("\n");
        sb.append("Relocates: ").append(manager.getTaskCountByType(job.getJobNumber(), "Relocate")).append("\n");
        sb.append("Man-Hours: ").append(formatDouble(manager.getTotalManHoursForJob(job.getJobNumber()))).append("\n");

        int dispatchedCount = 0;
        int completedCount = 0;
        int forkliftTasks = 0;

        for (Task task : jobTasks) {
            if ("Dispatched".equalsIgnoreCase(safe(task.getStatus()))) {
                dispatchedCount++;
            }
            if ("Completed".equalsIgnoreCase(safe(task.getStatus()))) {
                completedCount++;
            }
            if (task.getAssignedForklifts() != null && !task.getAssignedForklifts().isEmpty()) {
                forkliftTasks++;
            }
        }

        sb.append("Dispatched Tasks: ").append(dispatchedCount).append("\n");
        sb.append("Completed Tasks: ").append(completedCount).append("\n");
        sb.append("Tasks Using Forklifts: ").append(forkliftTasks).append("\n");

        return sb.toString();
    }

    private int getCrewCount(Task task) {
        int companyCrew = task.getAssignedEmployeeIds() == null ? 0 : task.getAssignedEmployeeIds().size();
        int ownerOps = task.getAssignedOwnerOperators() == null ? 0 : task.getAssignedOwnerOperators().size();
        return companyCrew + ownerOps;
    }

    private String buildEquipmentCell(Task task) {
        ArrayList<String> parts = new ArrayList<>();

        if (task.getAssignedForklifts() != null && !task.getAssignedForklifts().isEmpty()) {
            parts.add("Forklifts: " + task.getAssignedForklifts().size());
        }

        if (task.getRequiredEquipment() != null && !task.getRequiredEquipment().isEmpty()) {
            parts.add("Support: " + task.getRequiredEquipment().size());
        }

        if (parts.isEmpty()) {
            return "None";
        }

        return String.join(" | ", parts);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(24, 24, 24));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE
        ));
        return panel;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JLabel valueLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(createLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(valueLabel, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(190, 190, 190));
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    private JLabel createHighlightLabel(Color color) {
        JLabel label = new JLabel();
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        return label;
    }

    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(140, 40));
        button.setBackground(new Color(30, 30, 30));
        button.setForeground(color);
        button.setBorder(BorderFactory.createLineBorder(color, 2));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private String formatDouble(double value) {
        return new DecimalFormat("0.##").format(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}