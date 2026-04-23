
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OwnerPortal extends JFrame {

    private final FleetManager manager;

    private JTable taskTable;
    private JTable jobTable;

    private DefaultTableModel taskModel;
    private DefaultTableModel jobModel;

    private JLabel availableTrucksLabel;
    private JLabel maintenanceLabel;

    public OwnerPortal(FleetManager manager) {
        this.manager = manager;

        setTitle("FleetTrack Pro - Owner Portal");
        setSize(1500, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(15, 15, 15));
        setLayout(new BorderLayout(15, 15));

        add(createSidebar(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                refreshData();
            }
        });

        refreshData();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBackground(new Color(25, 25, 25));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JPanel statusBox = createStatusBox("Vehicle Status", new Color(0, 150, 255));
        availableTrucksLabel = new JLabel("Scanning...");
        availableTrucksLabel.setForeground(new Color(0, 255, 100));
        statusBox.add(availableTrucksLabel);
        sidebar.add(statusBox);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel repairBox = createStatusBox("Open Repair Tickets", Color.ORANGE);
        maintenanceLabel = new JLabel("All Equipment Ready");
        maintenanceLabel.setForeground(Color.WHITE);
        repairBox.add(maintenanceLabel);
        sidebar.add(repairBox);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton createEmployeeBtn = createSideButton("Create Employee");
        JButton createEquipmentBtn = createSideButton("Create Equipment");
        JButton vehicleListBtn = createSideButton("Vehicle List");
        JButton rosterBtn = createSideButton("Company Roster");
        JButton assignDriverVehicleBtn = createSideButton("Assign Driver To Vehicle");
        JButton timeOffBtn = createSideButton("Time Off Manager");
        JButton editCompanyBtn = createSideButton("Edit Company Info");
        JButton stockpileManagerBtn = createSideButton("Stockpile Manager");

        createEmployeeBtn.addActionListener(e -> openChildWindow(new CreateEmployeeUI(manager)));
        createEquipmentBtn.addActionListener(e -> openChildWindow(new CreateEquipmentUI(manager)));
        vehicleListBtn.addActionListener(e -> openChildWindow(new CompanyVehicleListUI(manager)));
        rosterBtn.addActionListener(e -> openChildWindow(new CompanyRosterUI(manager)));
        assignDriverVehicleBtn.addActionListener(e -> showAssignDriverVehicleDialog());
        timeOffBtn.addActionListener(e -> openChildWindow(new ManagerTimeOffDashboardUI(manager)));
        editCompanyBtn.addActionListener(e -> new CompanySetupUI(manager, true).setVisible(true));
        stockpileManagerBtn.addActionListener(e -> {
    try {
        System.out.println("Opening Stockpile Manager...");
        StockpileManagerUI ui = new StockpileManagerUI(manager);
        ui.setVisible(true);
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "ERROR: " + ex.getMessage());
    }
});

        sidebar.add(createEmployeeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createEquipmentBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(vehicleListBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(stockpileManagerBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(rosterBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(assignDriverVehicleBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(timeOffBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(editCompanyBtn);

        return sidebar;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 12, 12));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 15));

        centerPanel.add(createTaskPanel());
        centerPanel.add(createJobPanel());

        return centerPanel;
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel header = new JLabel("LIVE OPERATIONS BOARD", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 28));
        header.setForeground(new Color(0, 255, 255));
        panel.add(header, BorderLayout.NORTH);

        setupTaskTable();
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        actionPanel.setBackground(new Color(15, 15, 15));

        JButton createTaskBtn = createStyledButton("Create Task", new Color(0, 255, 150));
        JButton editTaskBtn = createStyledButton("Edit Task", new Color(0, 200, 255));
        JButton dispatchTaskBtn = createStyledButton("Dispatch Task", new Color(255, 215, 0));
        JButton deleteTaskBtn = createStyledButton("Delete Task", new Color(255, 100, 100));

        createTaskBtn.addActionListener(e -> createTask());
        editTaskBtn.addActionListener(e -> editSelectedTask());
        dispatchTaskBtn.addActionListener(e -> dispatchSelectedTask());
        deleteTaskBtn.addActionListener(e -> deleteSelectedTask());

        actionPanel.add(createTaskBtn);
        actionPanel.add(editTaskBtn);
        actionPanel.add(dispatchTaskBtn);
        actionPanel.add(deleteTaskBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createJobPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel header = new JLabel("JOB MANAGEMENT", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setForeground(new Color(255, 215, 0));
        JButton dispatchBarriersBtn = createStyledButton("Dispatch Barriers", new Color(0, 255, 150));
        panel.add(header, BorderLayout.NORTH);

        setupJobTable();
        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        actionPanel.setBackground(new Color(15, 15, 15));

        JButton createJobBtn = createStyledButton("Create Job", new Color(0, 255, 150));
        JButton editJobBtn = createStyledButton("Edit Job", new Color(0, 200, 255));
        JButton deleteJobBtn = createStyledButton("Delete Job", new Color(255, 100, 100));
        JButton jobStatsBtn = createStyledButton("Job Dashboard", new Color(255, 215, 0));

        createJobBtn.addActionListener(e -> createJob());
        editJobBtn.addActionListener(e -> editSelectedJob());
        deleteJobBtn.addActionListener(e -> deleteSelectedJob());
        jobStatsBtn.addActionListener(e -> showSelectedJobDashboard());
        dispatchBarriersBtn.addActionListener(e -> showDispatchBarriersDialog());

        actionPanel.add(createJobBtn);
        actionPanel.add(editJobBtn);
        actionPanel.add(deleteJobBtn);
        actionPanel.add(jobStatsBtn);
        actionPanel.add(dispatchBarriersBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        bottomPanel.setBackground(new Color(15, 15, 15));

        JButton logoutBtn = createStyledButton("LOG OUT", new Color(255, 80, 80));
        logoutBtn.addActionListener(e -> {
            dispose();
            Main.showLoginScreen();
        });

        bottomPanel.add(logoutBtn);
        return bottomPanel;
    }

    private void openChildWindow(JFrame window) {
        window.setVisible(true);
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refreshData();
            }
        });
    }

    private JPanel createStatusBox(String title, Color accent) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(35, 35, 35));
        box.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accent),
                title,
                0,
                0,
                new Font("SansSerif", Font.BOLD, 12),
                accent
        ));
        box.setMaximumSize(new Dimension(290, 120));
        return box;
    }

    private JButton createSideButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(290, 48));
        btn.setBackground(new Color(45, 45, 45));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        return btn;
    }

    private JButton createStyledButton(String text, Color borderColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(borderColor);
        btn.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        return btn;
    }

    private void setupTaskTable() {
        String[] columns = {
                "Task #", "Job #", "Date", "Start", "End", "Task Type",
                "Contractor", "Location", "Foreman", "Crew", "Linear Feet", "Status"
        };

        taskModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0, 1, 10 -> Integer.class;
                    default -> String.class;
                };
            }
        };

        taskTable = new JTable(taskModel);
        styleTable(taskTable);
    }

    private void setupJobTable() {
        String[] columns = {
                "Job #", "Project", "Contractor", "Start Date",
                "End Date", "Open Time", "Location", "Status"
        };

        jobModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : String.class;
            }
        };

        jobTable = new JTable(jobModel);
        styleTable(jobTable);
        jobTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    showSelectedJobDashboard();
                }
            }
        });
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(60, 60, 60));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(45, 45, 45));
        header.setForeground(new Color(0, 255, 255));
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
    }

    public void refreshData() {
        refreshTaskTable();
        refreshJobTable();

        int total = manager.getTrucks().size();
        int down = (int) manager.getTrucks().stream().filter(Truck::isDown).count();
        availableTrucksLabel.setText((total - down) + " / " + total + " Available");

        int openTickets = 0;
        if (manager.getMechanicalWriteUps() != null) {
            for (MechanicalWriteUp writeUp : manager.getMechanicalWriteUps()) {
                String status = writeUp.getRepairStatus();
                if (status == null ||
                        (!status.equalsIgnoreCase("Completed")
                                && !status.equalsIgnoreCase("Closed")
                                && !status.equalsIgnoreCase("Resolved"))) {
                    openTickets++;
                }
            }
        }

        maintenanceLabel.setText(openTickets == 0
                ? "All Equipment Ready"
                : openTickets + " Open Repair Ticket(s)");
    }

    private void refreshTaskTable() {
        taskModel.setRowCount(0);

        List<Task> sortedTasks = new ArrayList<>(manager.getTasks());
        sortedTasks.sort(
                Comparator.comparing(this::parseTaskDate)
                        .thenComparing(this::parseTaskTime)
        );

        for (Task t : sortedTasks) {
            taskModel.addRow(new Object[]{
                    t.getTaskId(),
                    t.getJobId(),
                    t.getStartDate(),
                    t.getStartTime(),
                    t.getEndTime(),
                    t.getJobType(),
                    t.getContractor(),
                    t.getLocation(),
                    t.getForeman(),
                    buildCrewDisplay(t),
                    t.getLinearFeet(),
                    t.getStatus()
            });
        }
    }

    private String buildCrewDisplay(Task task) {
        if (task.getAssignedEmployeeIds() != null && !task.getAssignedEmployeeIds().isEmpty()) {
            ArrayList<String> names = new ArrayList<>();

            for (Integer employeeId : task.getAssignedEmployeeIds()) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null) {
                    String truck = safe(employee.getAssignedTruckId()).isEmpty() ? "No Truck" : employee.getAssignedTruckId();
                    String trailer = safe(employee.getAssignedTrailerId()).isEmpty() ? "No Trailer" : employee.getAssignedTrailerId();
                    names.add(employee.getFullName() + " [Truck: " + truck + ", Trailer: " + trailer + "]");
                }
            }

            if (!names.isEmpty()) {
                return String.join(" | ", names);
            }
        }

        String notes = safe(task.getNotes());
        if (notes.startsWith("Crew:")) {
            return notes.substring("Crew:".length()).trim();
        }

        return notes;
    }

    private void refreshJobTable() {
        jobModel.setRowCount(0);

        List<Job> jobs = manager.getJobs();
        if (jobs == null) {
            return;
        }

        jobs.sort(Comparator.comparingInt(Job::getJobNumber));

        for (Job j : jobs) {
            jobModel.addRow(new Object[]{
                    j.getJobNumber(),
                    safe(j.getProjectName()),
                    safe(j.getContractor()),
                    formatMonthYearDisplay(safe(j.getStartDate())),
                    formatMonthYearDisplay(safe(j.getEndDate())),
                    buildJobOpenTime(j),
                    safe(j.getLocation()),
                    safe(j.getStatus())
            });
        }
    }

    private String buildJobOpenTime(Job job) {
        try {
            LocalDate start = LocalDate.parse(job.getStartDate());
            LocalDate end = "Active".equalsIgnoreCase(safe(job.getStatus()))
                    ? LocalDate.now()
                    : LocalDate.parse(job.getEndDate());

            if (end.isBefore(start)) {
                return "Invalid";
            }

            long totalMonths = ChronoUnit.MONTHS.between(start.withDayOfMonth(1), end.withDayOfMonth(1));
            long years = totalMonths / 12;
            long months = totalMonths % 12;
            long extraDays = ChronoUnit.DAYS.between(end.withDayOfMonth(1), end);
            long weeks = Math.max(0, extraDays / 7);

            StringBuilder sb = new StringBuilder();
            if (years > 0) {
                sb.append(years).append(years == 1 ? " year" : " years");
            }
            if (months > 0) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(months).append(months == 1 ? " month" : " months");
            }
            if (years == 0 && weeks > 0) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(weeks).append(weeks == 1 ? " week" : " weeks");
            }
            return sb.length() == 0 ? "Less than 1 month" : sb.toString();
        } catch (Exception e) {
            return "N/A";
        }
    }

    private void createJob() {
        Job job = showJobDialog(null);
        if (job != null) {
            manager.getJobs().add(job);
            DataStore.save(manager);
            refreshData();
        }
    }

    private void editSelectedJob() {
        Job selected = getSelectedJob();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a job first.");
            return;
        }

        if (showJobDialog(selected) != null) {
            DataStore.save(manager);
            refreshData();
        }
    }

    private void deleteSelectedJob() {
        Job selected = getSelectedJob();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a job first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected job?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            manager.getJobs().remove(selected);

            List<Task> tasksToRemove = new ArrayList<>();
            for (Task task : manager.getTasks()) {
                if (task.getJobId() == selected.getJobNumber()) {
                    tasksToRemove.add(task);
                }
            }
            manager.getTasks().removeAll(tasksToRemove);

            DataStore.save(manager);
            refreshData();
        }
    }

    private Job showJobDialog(Job existing) {
        JTextField jobNumberField = new JTextField();
        JTextField contractorField = new JTextField();
        JTextField projectField = new JTextField();
        JComboBox<String> startMonthBox = buildMonthCombo();
        JComboBox<Integer> startYearBox = buildYearCombo();
        JComboBox<String> endMonthBox = buildMonthCombo();
        JComboBox<Integer> endYearBox = buildYearCombo();
        JTextField locationField = new JTextField();
        JComboBox<String> statusField = new JComboBox<>(new String[]{"Active", "Open", "Completed", "Closed", "On Hold"});
        JTextField managerField = new JTextField();
        JTextField dotField = new JTextField();
        JTextField barrierField = new JTextField();
        JTextField linearFeetField = new JTextField("0");
        JTextField notesField = new JTextField();

        LocalDate defaultStart = LocalDate.now().withDayOfMonth(1);
        LocalDate defaultEnd = defaultStart.plusMonths(1).withDayOfMonth(defaultStart.plusMonths(1).lengthOfMonth());

        if (existing != null) {
            jobNumberField.setText(String.valueOf(existing.getJobNumber()));
            jobNumberField.setEditable(false);
            contractorField.setText(safe(existing.getContractor()));
            projectField.setText(safe(existing.getProjectName()));
            locationField.setText(safe(existing.getLocation()));
            statusField.setSelectedItem(safe(existing.getStatus()).isEmpty() ? "Active" : safe(existing.getStatus()));
            managerField.setText(safe(existing.getProjectManager()));
            dotField.setText(safe(existing.getDotProjectNumber()));
            barrierField.setText(safe(existing.getBarrierType()));
            linearFeetField.setText(String.valueOf(existing.getTotalLinearFeet()));
            notesField.setText(safe(existing.getNotes()));

            LocalDate start = parseJobDateOrDefault(existing.getStartDate(), defaultStart);
            LocalDate end = parseJobDateOrDefault(existing.getEndDate(), defaultEnd);
            startMonthBox.setSelectedIndex(start.getMonthValue() - 1);
            startYearBox.setSelectedItem(start.getYear());
            endMonthBox.setSelectedIndex(end.getMonthValue() - 1);
            endYearBox.setSelectedItem(end.getYear());
        } else {
            jobNumberField.setText(String.valueOf(getNextJobNumber()));
            startMonthBox.setSelectedIndex(defaultStart.getMonthValue() - 1);
            startYearBox.setSelectedItem(defaultStart.getYear());
            endMonthBox.setSelectedIndex(defaultEnd.getMonthValue() - 1);
            endYearBox.setSelectedItem(defaultEnd.getYear());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(panel, gbc, 0, "Job #:", jobNumberField);
        addFormRow(panel, gbc, 1, "Contractor:", contractorField);
        addFormRow(panel, gbc, 2, "Project Name:", projectField);
        addFormRow(panel, gbc, 3, "Start (Month / Year):", buildTwoFieldPanel(startMonthBox, startYearBox));
        addFormRow(panel, gbc, 4, "End (Month / Year):", buildTwoFieldPanel(endMonthBox, endYearBox));
        addFormRow(panel, gbc, 5, "Location:", locationField);
        addFormRow(panel, gbc, 6, "Status:", statusField);
        addFormRow(panel, gbc, 7, "Project Manager:", managerField);
        addFormRow(panel, gbc, 8, "DOT Project #:", dotField);
        addFormRow(panel, gbc, 9, "Barrier Type:", barrierField);
        addFormRow(panel, gbc, 10, "Total Linear Feet:", linearFeetField);
        addFormRow(panel, gbc, 11, "Notes:", notesField);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(700, 460));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                existing == null ? "Create Job" : "Edit Job",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            int jobNumber = Integer.parseInt(jobNumberField.getText().trim());
            int linearFeet = parseIntSafe(linearFeetField.getText().trim(), 0);

            LocalDate startDate = LocalDate.of(
                    (Integer) startYearBox.getSelectedItem(),
                    startMonthBox.getSelectedIndex() + 1,
                    1
            );

            YearMonth endYearMonth = YearMonth.of(
                    (Integer) endYearBox.getSelectedItem(),
                    endMonthBox.getSelectedIndex() + 1
            );
            LocalDate endDate = endYearMonth.atEndOfMonth();

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
                return null;
            }

            if (existing == null) {
                Job job = new Job(
                        jobNumber,
                        contractorField.getText().trim(),
                        projectField.getText().trim(),
                        startDate.toString(),
                        endDate.toString(),
                        locationField.getText().trim()
                );

                job.setStatus(String.valueOf(statusField.getSelectedItem()).trim());
                job.setProjectManager(managerField.getText().trim());
                job.setDotProjectNumber(dotField.getText().trim());
                job.setBarrierType(barrierField.getText().trim());
                job.setTotalLinearFeet(linearFeet);
                job.setNotes(notesField.getText().trim());

                return job;
            } else {
                existing.setContractor(contractorField.getText().trim());
                existing.setProjectName(projectField.getText().trim());
                existing.setStartDate(startDate.toString());
                existing.setEndDate(endDate.toString());
                existing.setLocation(locationField.getText().trim());
                existing.setStatus(String.valueOf(statusField.getSelectedItem()).trim());
                existing.setProjectManager(managerField.getText().trim());
                existing.setDotProjectNumber(dotField.getText().trim());
                existing.setBarrierType(barrierField.getText().trim());
                existing.setTotalLinearFeet(linearFeet);
                existing.setNotes(notesField.getText().trim());

                return existing;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid job data: " + ex.getMessage());
            return null;
        }
    }

    private void createTask() {
        Task task = showTaskDialog(null);
        if (task != null) {
            manager.getTasks().add(task);
            DataStore.save(manager);
            refreshData();
        }
    }

    private void editSelectedTask() {
        Task selected = getSelectedTask();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        Task edited = showTaskDialog(selected);
        if (edited != null) {
            DataStore.save(manager);
            refreshData();
        }
    }

    private void dispatchSelectedTask() {
        Task selected = getSelectedTask();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        DispatchFormUI dialog = new DispatchFormUI(this, manager, selected);
        dialog.setVisible(true);

        if (dialog.wasSaved()) {
            DataStore.save(manager);
            refreshData();
        }
    }

    private void deleteSelectedTask() {
        Task selected = getSelectedTask();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            manager.getTasks().remove(selected);
            DataStore.save(manager);
            refreshData();
        }
    }

    private Task showTaskDialog(Task existing) {
        JTextField taskIdField = new JTextField();
        JComboBox<String> jobIdCombo = new JComboBox<>();

        JTextField dateField = new JTextField(LocalDate.now().toString());

        JSpinner startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startEditor);

        java.util.Calendar startCal = java.util.Calendar.getInstance();
        startCal.set(java.util.Calendar.HOUR_OF_DAY, 7);
        startCal.set(java.util.Calendar.MINUTE, 0);
        startCal.set(java.util.Calendar.SECOND, 0);
        startCal.set(java.util.Calendar.MILLISECOND, 0);
        startTimeSpinner.setValue(startCal.getTime());

        JSpinner endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endEditor);

        java.util.Calendar endCal = java.util.Calendar.getInstance();
        endCal.set(java.util.Calendar.HOUR_OF_DAY, 15);
        endCal.set(java.util.Calendar.MINUTE, 0);
        endCal.set(java.util.Calendar.SECOND, 0);
        endCal.set(java.util.Calendar.MILLISECOND, 0);
        endTimeSpinner.setValue(endCal.getTime());

        JComboBox<String> taskTypeField = new JComboBox<>(new String[]{
                "Install",
                "Remove",
                "Relocate"
        });

        JTextField contractorField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField linearFeetField = new JTextField("0");

        JComboBox<String> tcbLengthBox = new JComboBox<>(new String[]{"20", "12"});
        JComboBox<String> tiaTypeBox = new JComboBox<>(new String[]{
                TIA.NONE,
                TIA.NEW_STYLE,
                TIA.OLD_STYLE
        });
        JTextField tiaSetField = new JTextField("0");

        JLabel straightPiecesLabel = new JLabel("0");
        JLabel absorbPiecesLabel = new JLabel("0");
        JLabel totalPiecesLabel = new JLabel("0");
        JLabel plasticUnitsLabel = new JLabel("0");

        JComboBox<String> foremanField = new JComboBox<>();
        for (Employee employee : manager.getEmployees()) {
            String position = employee.getPosition();
            if (position != null && position.toLowerCase().contains("foreman")) {
                foremanField.addItem(employee.getFullName());
            }
        }

        JTextField statusField = new JTextField("Open");
        JTextField notesField = new JTextField();

        for (Job job : manager.getJobs()) {
            jobIdCombo.addItem(String.valueOf(job.getJobNumber()));
        }

        contractorField.setEditable(false);

        Runnable calc = () -> updateBarrierCalculator(
                linearFeetField,
                tcbLengthBox,
                tiaSetField,
                tiaTypeBox,
                straightPiecesLabel,
                absorbPiecesLabel,
                totalPiecesLabel,
                plasticUnitsLabel
        );

        linearFeetField.getDocument().addDocumentListener(new SimpleDocumentListener(calc));
        tiaSetField.getDocument().addDocumentListener(new SimpleDocumentListener(calc));
        tcbLengthBox.addActionListener(e -> calc.run());
        tiaTypeBox.addActionListener(e -> calc.run());

        jobIdCombo.addActionListener(e -> updateTaskJobLinkedFields(jobIdCombo, contractorField, locationField));

        if (jobIdCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Create a job before creating a task.");
            return null;
        }

        if (existing != null) {
            taskIdField.setText(String.valueOf(existing.getTaskId()));
            taskIdField.setEditable(false);
            jobIdCombo.setSelectedItem(String.valueOf(existing.getJobId()));
            dateField.setText(safe(existing.getStartDate()));
            linearFeetField.setText(String.valueOf(existing.getLinearFeet()));

            if (existing.getTcb() != null) {
                tcbLengthBox.setSelectedItem(String.valueOf(existing.getTcb().getLengthFeet()));
            }

            if (existing.getTia() != null) {
                tiaTypeBox.setSelectedItem(existing.getTia().getType());
                tiaSetField.setText(String.valueOf(existing.getTia().getSetCount()));
            }

            try {
                LocalTime existingStart = LocalTime.parse(safe(existing.getStartTime()));
                java.util.Calendar editStartCal = java.util.Calendar.getInstance();
                editStartCal.set(java.util.Calendar.HOUR_OF_DAY, existingStart.getHour());
                editStartCal.set(java.util.Calendar.MINUTE, existingStart.getMinute());
                editStartCal.set(java.util.Calendar.SECOND, 0);
                editStartCal.set(java.util.Calendar.MILLISECOND, 0);
                startTimeSpinner.setValue(editStartCal.getTime());
            } catch (Exception ignored) {
            }

            try {
                LocalTime existingEnd = LocalTime.parse(safe(existing.getEndTime()));
                java.util.Calendar editEndCal = java.util.Calendar.getInstance();
                editEndCal.set(java.util.Calendar.HOUR_OF_DAY, existingEnd.getHour());
                editEndCal.set(java.util.Calendar.MINUTE, existingEnd.getMinute());
                editEndCal.set(java.util.Calendar.SECOND, 0);
                editEndCal.set(java.util.Calendar.MILLISECOND, 0);
                endTimeSpinner.setValue(editEndCal.getTime());
            } catch (Exception ignored) {
            }

            taskTypeField.setSelectedItem(safe(existing.getJobType()));
            contractorField.setText(safe(existing.getContractor()));
            locationField.setText(safe(existing.getLocation()));
            foremanField.setSelectedItem(safe(existing.getForeman()));
            statusField.setText(safe(existing.getStatus()));
            notesField.setText(safe(existing.getNotes()));
        } else {
            taskIdField.setText(String.valueOf(getNextTaskId()));
            updateTaskJobLinkedFields(jobIdCombo, contractorField, locationField);
        }

        calc.run();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(panel, gbc, 0, "Task #:", taskIdField);
        addFormRow(panel, gbc, 1, "Job #:", jobIdCombo);
        addFormRow(panel, gbc, 2, "Date (yyyy-MM-dd):", dateField);
        addFormRow(panel, gbc, 3, "Start Time (24hr HH:mm):", startTimeSpinner);
        addFormRow(panel, gbc, 4, "End Time (24hr HH:mm):", endTimeSpinner);
        addFormRow(panel, gbc, 5, "Task Type:", taskTypeField);
        addFormRow(panel, gbc, 6, "Contractor:", contractorField);
        addFormRow(panel, gbc, 7, "Location:", locationField);
        addFormRow(panel, gbc, 8, "Foreman:", foremanField);
        addFormRow(panel, gbc, 9, "Linear Feet:", linearFeetField);
        addFormRow(panel, gbc, 10, "TCB Length:", tcbLengthBox);
        addFormRow(panel, gbc, 11, "TIA Type:", tiaTypeBox);
        addFormRow(panel, gbc, 12, "TIA Sets:", tiaSetField);
        addFormRow(panel, gbc, 13, "Straight Pieces:", straightPiecesLabel);
        addFormRow(panel, gbc, 14, "Absorb Pieces:", absorbPiecesLabel);
        addFormRow(panel, gbc, 15, "Total Pieces:", totalPiecesLabel);
        addFormRow(panel, gbc, 16, "Plastic Units:", plasticUnitsLabel);
        addFormRow(panel, gbc, 17, "Status:", statusField);
        addFormRow(panel, gbc, 18, "Notes:", notesField);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(720, 650));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                existing == null ? "Create Task" : "Edit Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            int taskId = Integer.parseInt(taskIdField.getText().trim());
            int jobId = Integer.parseInt(String.valueOf(jobIdCombo.getSelectedItem()));
            int linearFeet = parseIntSafe(linearFeetField.getText().trim(), 0);

            int tcbLength = Integer.parseInt(String.valueOf(tcbLengthBox.getSelectedItem()));
            int tiaSets = parseIntSafe(tiaSetField.getText().trim(), 0);
            String tiaType = String.valueOf(tiaTypeBox.getSelectedItem());

            int straightPieces = parseIntSafe(straightPiecesLabel.getText(), 0);
            int absorbPieces = parseIntSafe(absorbPiecesLabel.getText(), 0);

            TCB tcb = new TCB(tcbLength, straightPieces, absorbPieces, linearFeet);
            TIA tia = new TIA(tiaType, tiaSets);

            LocalDate.parse(dateField.getText().trim());

            Date startSpinnerDate = (Date) startTimeSpinner.getValue();
            LocalTime startTime = startSpinnerDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .withSecond(0)
                    .withNano(0);

            Date endSpinnerDate = (Date) endTimeSpinner.getValue();
            LocalTime endTime = endSpinnerDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .withSecond(0)
                    .withNano(0);

            String startTimeText = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            String endTimeText = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            String foreman = String.valueOf(foremanField.getSelectedItem()).trim();

            if (existing == null) {
                Task newTask = new Task(
                        taskId,
                        jobId,
                        dateField.getText().trim(),
                        startTimeText,
                        endTimeText,
                        String.valueOf(taskTypeField.getSelectedItem()).trim(),
                        contractorField.getText().trim(),
                        locationField.getText().trim(),
                        foreman,
                        statusField.getText().trim(),
                        linearFeet,
                        tcb,
                        tia
                );

                newTask.setNotes(notesField.getText().trim());
                return newTask;
            } else {
                ArrayList<Integer> existingAssignedIds = new ArrayList<>();
                if (existing.getAssignedEmployeeIds() != null) {
                    existingAssignedIds.addAll(existing.getAssignedEmployeeIds());
                }

                Task rebuiltTask = new Task(
                        taskId,
                        jobId,
                        dateField.getText().trim(),
                        startTimeText,
                        endTimeText,
                        String.valueOf(taskTypeField.getSelectedItem()).trim(),
                        contractorField.getText().trim(),
                        locationField.getText().trim(),
                        foreman,
                        statusField.getText().trim(),
                        linearFeet,
                        tcb,
                        tia
                );

                rebuiltTask.setNotes(notesField.getText().trim());
                rebuiltTask.setAssignedEmployeeIds(existingAssignedIds);

                int index = manager.getTasks().indexOf(existing);
                if (index >= 0) {
                    manager.getTasks().set(index, rebuiltTask);
                }

                return rebuiltTask;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid task data: " + ex.getMessage());
            return null;
        }
    }

    private void updateBarrierCalculator(
            JTextField linearFeetField,
            JComboBox<String> tcbLengthBox,
            JTextField tiaSetField,
            JComboBox<String> tiaTypeBox,
            JLabel straightPiecesLabel,
            JLabel absorbPiecesLabel,
            JLabel totalPiecesLabel,
            JLabel plasticUnitsLabel
    ) {
        try {
            int linearFeet = parseIntSafe(linearFeetField.getText().trim(), 0);
            int length = Integer.parseInt((String) tcbLengthBox.getSelectedItem());
            int sets = parseIntSafe(tiaSetField.getText().trim(), 0);

            String type = (String) tiaTypeBox.getSelectedItem();
            if (TIA.NONE.equals(type)) {
                sets = 0;
            }

            int absorbPieces = sets;
            int absorbFootage = absorbPieces * length;

            int remainingFeet = Math.max(linearFeet - absorbFootage, 0);
            int straightPieces = (int) Math.ceil((double) remainingFeet / length);
            int totalPieces = straightPieces + absorbPieces;

            int plasticUnits = 0;
            if (TIA.NEW_STYLE.equals(type)) {
                plasticUnits = sets * 3;
            } else if (TIA.OLD_STYLE.equals(type)) {
                plasticUnits = sets * 10;
            }

            straightPiecesLabel.setText(String.valueOf(straightPieces));
            absorbPiecesLabel.setText(String.valueOf(absorbPieces));
            totalPiecesLabel.setText(String.valueOf(totalPieces));
            plasticUnitsLabel.setText(String.valueOf(plasticUnits));
        } catch (Exception e) {
            straightPiecesLabel.setText("0");
            absorbPiecesLabel.setText("0");
            totalPiecesLabel.setText("0");
            plasticUnitsLabel.setText("0");
        }
    }

    private void showAssignDriverVehicleDialog() {
        ArrayList<Employee> drivers = manager.getActiveDriverEmployees();
        ArrayList<Truck> trucks = manager.getAssignableTrucks();
        ArrayList<Trailer> trailers = manager.getAssignableTrailers();

        if (drivers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active drivers found in the company roster.");
            return;
        }

        if (trucks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignable trucks found.");
            return;
        }

        JComboBox<Employee> driverCombo = new JComboBox<>(drivers.toArray(new Employee[0]));
        JComboBox<Truck> truckCombo = new JComboBox<>(trucks.toArray(new Truck[0]));

        ArrayList<Object> trailerOptions = new ArrayList<>();
        trailerOptions.add("None");
        trailerOptions.addAll(trailers);
        JComboBox<Object> trailerCombo = new JComboBox<>(trailerOptions.toArray());

        JTextArea previewArea = new JTextArea(8, 30);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);

        Runnable refreshPreview = () -> {
            Employee selectedDriver = (Employee) driverCombo.getSelectedItem();
            Truck selectedTruck = (Truck) truckCombo.getSelectedItem();
            Object trailerSelection = trailerCombo.getSelectedItem();

            StringBuilder sb = new StringBuilder();

            if (selectedDriver != null) {
                sb.append("Driver: ").append(selectedDriver.getFullName()).append("\n");
                sb.append("Current Truck: ")
                        .append(safe(selectedDriver.getAssignedTruckId()).isEmpty() ? "None" : selectedDriver.getAssignedTruckId())
                        .append("\n");
                sb.append("Current Trailer: ")
                        .append(safe(selectedDriver.getAssignedTrailerId()).isEmpty() ? "None" : selectedDriver.getAssignedTrailerId())
                        .append("\n\n");
            }

            if (selectedTruck != null) {
                Employee truckOwner = manager.findEmployeeAssignedToTruck(selectedTruck.getTruckID());
                sb.append("Selected Truck: ").append(selectedTruck.getTruckID()).append("\n");
                sb.append("Truck Currently Assigned To: ")
                        .append(truckOwner == null ? "Nobody" : truckOwner.getFullName())
                        .append("\n\n");
            }

            if (trailerSelection instanceof Trailer) {
                Trailer trailer = (Trailer) trailerSelection;
                Employee trailerOwner = manager.findEmployeeAssignedToTrailer(trailer.getTrailerId());
                sb.append("Selected Trailer: ").append(trailer.getTrailerId()).append("\n");
                sb.append("Trailer Currently Assigned To: ")
                        .append(trailerOwner == null ? "Nobody" : trailerOwner.getFullName());
            } else {
                sb.append("Selected Trailer: None");
            }

            previewArea.setText(sb.toString());
        };

        driverCombo.addActionListener(e -> refreshPreview.run());
        truckCombo.addActionListener(e -> refreshPreview.run());
        trailerCombo.addActionListener(e -> refreshPreview.run());
        refreshPreview.run();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(panel, gbc, 0, "Driver:", driverCombo);
        addFormRow(panel, gbc, 1, "Truck:", truckCombo);
        addFormRow(panel, gbc, 2, "Trailer:", trailerCombo);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(previewArea), gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Assign Driver To Vehicle",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Employee selectedDriver = (Employee) driverCombo.getSelectedItem();
        Truck selectedTruck = (Truck) truckCombo.getSelectedItem();
        Object trailerSelection = trailerCombo.getSelectedItem();

        if (selectedDriver == null || selectedTruck == null) {
            JOptionPane.showMessageDialog(this, "Please select a driver and truck.");
            return;
        }

        boolean truckAssigned = manager.assignDriverToTruck(
                selectedDriver.getEmployeeId(),
                selectedTruck.getTruckID()
        );

        if (!truckAssigned) {
            JOptionPane.showMessageDialog(this, "Truck assignment failed.");
            return;
        }

        if (trailerSelection instanceof Trailer) {
            Trailer selectedTrailer = (Trailer) trailerSelection;
            boolean trailerAssigned = manager.assignDriverToTrailer(
                    selectedDriver.getEmployeeId(),
                    selectedTrailer.getTrailerId()
            );

            if (!trailerAssigned) {
                JOptionPane.showMessageDialog(this, "Truck assigned, but trailer assignment failed.");
                DataStore.save(manager);
                refreshData();
                return;
            }
        } else {
            manager.clearTrailerAssignmentForEmployee(selectedDriver.getEmployeeId());
        }

        DataStore.save(manager);
        refreshData();
        JOptionPane.showMessageDialog(this, "Driver vehicle assignment saved.");
    }

    private ArrayList<Employee> getAvailableDriversForTask(Task currentTask) {
        ArrayList<Employee> availableDrivers = new ArrayList<>();

        for (Employee employee : manager.getEmployees()) {
            if (employee == null) {
                continue;
            }

            String position = safe(employee.getPosition()).toLowerCase(Locale.ENGLISH);

            if (!employee.isActive()) {
                continue;
            }
            if (!position.contains("driver")) {
                continue;
            }

            boolean conflict = false;

            for (Task task : manager.getTasks()) {
                if (task == null || task == currentTask) {
                    continue;
                }

                if (task.getAssignedEmployeeIds() != null &&
                        task.getAssignedEmployeeIds().contains(employee.getEmployeeId())) {

                    if (isTimeConflict(currentTask, task)) {
                        conflict = true;
                        break;
                    }
                }
            }

            if (!conflict) {
                availableDrivers.add(employee);
            }
        }

        availableDrivers.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return availableDrivers;
    }

    private boolean isTimeConflict(Task a, Task b) {
        try {
            LocalDate dateA = LocalDate.parse(a.getStartDate());
            LocalDate dateB = LocalDate.parse(b.getStartDate());

            if (!dateA.equals(dateB)) {
                return false;
            }

            LocalTime aStart = LocalTime.parse(a.getStartTime());
            LocalTime aEnd = LocalTime.parse(a.getEndTime());

            LocalTime bStart = LocalTime.parse(b.getStartTime());
            LocalTime bEnd = LocalTime.parse(b.getEndTime());

            return aStart.isBefore(bEnd) && aEnd.isAfter(bStart);
        } catch (Exception e) {
            return true;
        }
    }

    private void updateTaskJobLinkedFields(JComboBox<String> jobIdCombo, JTextField contractorField, JTextField locationField) {
        String selectedJobText = (String) jobIdCombo.getSelectedItem();
        if (selectedJobText == null) {
            contractorField.setText("");
            locationField.setText("");
            return;
        }

        try {
            int selectedJobNumber = Integer.parseInt(selectedJobText);
            Job selectedJob = manager.findJobByNumber(selectedJobNumber);
            if (selectedJob != null) {
                contractorField.setText(safe(selectedJob.getContractor()));
                locationField.setText(safe(selectedJob.getLocation()));
            } else {
                contractorField.setText("");
                locationField.setText("");
            }
        } catch (NumberFormatException e) {
            contractorField.setText("");
            locationField.setText("");
        }
    }

    private Task getSelectedTask() {
        int viewRow = taskTable.getSelectedRow();
        if (viewRow == -1) {
            return null;
        }

        int row = taskTable.convertRowIndexToModel(viewRow);
        int taskId = Integer.parseInt(String.valueOf(taskModel.getValueAt(row, 0)));

        for (Task task : manager.getTasks()) {
            if (task.getTaskId() == taskId) {
                return task;
            }
        }

        return null;
    }

    private Job getSelectedJob() {
        int viewRow = jobTable.getSelectedRow();
        if (viewRow == -1) {
            return null;
        }

        int row = jobTable.convertRowIndexToModel(viewRow);
        int jobNumber = Integer.parseInt(String.valueOf(jobModel.getValueAt(row, 0)));

        for (Job job : manager.getJobs()) {
            if (job.getJobNumber() == jobNumber) {
                return job;
            }
        }

        return null;
    }

    private void showSelectedJobDashboard() {
        Job selected = getSelectedJob();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a job first.");
            return;
        }

        new JobDashboardUI(manager, selected).setVisible(true);
    }

    private int getNextJobNumber() {
        int max = 0;
        for (Job job : manager.getJobs()) {
            max = Math.max(max, job.getJobNumber());
        }
        return max + 1;
    }

    private int getNextTaskId() {
        int max = 0;
        for (Task task : manager.getTasks()) {
            max = Math.max(max, task.getTaskId());
        }
        return max + 1;
    }

    private int parseIntSafe(String text, int fallback) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return fallback;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private LocalDate parseTaskDate(Task task) {
        try {
            return LocalDate.parse(task.getStartDate());
        } catch (Exception e) {
            return LocalDate.MAX;
        }
    }

    private LocalTime parseTaskTime(Task task) {
        try {
            return LocalTime.parse(task.getStartTime());
        } catch (Exception e) {
            return LocalTime.MAX;
        }
    }

    private JComboBox<String> buildMonthCombo() {
        JComboBox<String> comboBox = new JComboBox<>();
        for (int month = 1; month <= 12; month++) {
            comboBox.addItem(java.time.Month.of(month).getDisplayName(TextStyle.FULL, Locale.US));
        }
        return comboBox;
    }

    private JComboBox<Integer> buildYearCombo() {
        JComboBox<Integer> comboBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 2; year <= currentYear + 10; year++) {
            comboBox.addItem(year);
        }
        return comboBox;
    }

    private JPanel buildTwoFieldPanel(JComponent left, JComponent right) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setOpaque(false);
        panel.add(left);
        panel.add(right);
        return panel;
    }

    private LocalDate parseJobDateOrDefault(String value, LocalDate fallback) {
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    private String formatMonthYearDisplay(String isoDate) {
        try {
            LocalDate date = LocalDate.parse(isoDate);
            return date.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " " + date.getYear();
        } catch (Exception e) {
            return isoDate;
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }


    private void showDispatchBarriersDialog() {

    Job selectedJob = getSelectedJob();

    if (selectedJob == null) {
       JOptionPane.showMessageDialog(this, "Select a job first.");
        return;
    }

    if (manager.getStockpiles().isEmpty()) {
        JOptionPane.showMessageDialog(this, "No stockpiles available.");
        return;
    }

    JComboBox<String> stockpileBox = new JComboBox<>();
    for (Stockpile s : manager.getStockpiles()) {
        stockpileBox.addItem(s.getName());
    }

    JTextField quantityField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    panel.add(new JLabel("Stockpile:"));
    panel.add(stockpileBox);
    panel.add(new JLabel("Quantity:"));
    panel.add(quantityField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Dispatch Barriers",
            JOptionPane.OK_CANCEL_OPTION
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    try {
        String stockpileName = (String) stockpileBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText().trim());

        boolean success = manager.dispatchBarriersToJob(
                stockpileName,
                selectedJob.getJobNumber(),
                quantity,
                "Dispatcher",
                LocalDate.now().toString()
        );

        if (!success) {
            JOptionPane.showMessageDialog(this, "Dispatch failed. Check inventory.");
            return;
        }

        DataStore.save(manager);
        refreshData();

        JOptionPane.showMessageDialog(this,
                "Dispatched " + quantity + " barriers to Job #" + selectedJob.getJobNumber());

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid quantity.");
    }
}
    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable action;

        public SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            action.run();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            action.run();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            action.run();
        }
    }
}
