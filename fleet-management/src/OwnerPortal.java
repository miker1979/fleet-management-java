import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
        JButton timeOffBtn = createSideButton("Time Off Manager");
        JButton editCompanyBtn = createSideButton("Edit Company Info");

        createEmployeeBtn.addActionListener(e -> openChildWindow(new CreateEmployeeUI(manager)));
        createEquipmentBtn.addActionListener(e -> openChildWindow(new CreateEquipmentUI(manager)));
        vehicleListBtn.addActionListener(e -> openChildWindow(new CompanyVehicleListUI(manager)));
        rosterBtn.addActionListener(e -> openChildWindow(new CompanyRosterUI(manager)));
        timeOffBtn.addActionListener(e -> openChildWindow(new ManagerTimeOffDashboardUI(manager)));
        editCompanyBtn.addActionListener(e -> new CompanySetupUI(manager, true).setVisible(true));

        sidebar.add(createEmployeeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createEquipmentBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(vehicleListBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(rosterBtn);
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
        panel.add(header, BorderLayout.NORTH);

        setupJobTable();
        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        actionPanel.setBackground(new Color(15, 15, 15));

        JButton createJobBtn = createStyledButton("Create Job", new Color(0, 255, 150));
        JButton editJobBtn = createStyledButton("Edit Job", new Color(0, 200, 255));
        JButton deleteJobBtn = createStyledButton("Delete Job", new Color(255, 100, 100));

        createJobBtn.addActionListener(e -> createJob());
        editJobBtn.addActionListener(e -> editSelectedJob());
        deleteJobBtn.addActionListener(e -> deleteSelectedJob());

        actionPanel.add(createJobBtn);
        actionPanel.add(editJobBtn);
        actionPanel.add(deleteJobBtn);

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
        };

        taskTable = new JTable(taskModel);
        styleTable(taskTable);
    }

    private void setupJobTable() {
        String[] columns = {
                "Job #", "Project", "Contractor", "Start Date",
                "End Date", "Location", "Status"
        };

        jobModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jobTable = new JTable(jobModel);
        styleTable(jobTable);
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(60, 60, 60));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

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
                    names.add(employee.getFullName());
                }
            }

            if (!names.isEmpty()) {
                return String.join(", ", names);
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
                    safe(j.getStartDate()),
                    safe(j.getEndDate()),
                    safe(j.getLocation()),
                    safe(j.getStatus())
            });
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
        JTextField startDateField = new JTextField(LocalDate.now().toString());
        JTextField endDateField = new JTextField(LocalDate.now().plusDays(30).toString());
        JTextField locationField = new JTextField();
        JTextField statusField = new JTextField("Active");
        JTextField managerField = new JTextField();
        JTextField dotField = new JTextField();
        JTextField barrierField = new JTextField();
        JTextField linearFeetField = new JTextField("0");
        JTextField notesField = new JTextField();

        if (existing != null) {
            jobNumberField.setText(String.valueOf(existing.getJobNumber()));
            jobNumberField.setEditable(false);
            contractorField.setText(safe(existing.getContractor()));
            projectField.setText(safe(existing.getProjectName()));
            startDateField.setText(safe(existing.getStartDate()));
            endDateField.setText(safe(existing.getEndDate()));
            locationField.setText(safe(existing.getLocation()));
            statusField.setText(safe(existing.getStatus()));
            managerField.setText(safe(existing.getProjectManager()));
            dotField.setText(safe(existing.getDotProjectNumber()));
            barrierField.setText(safe(existing.getBarrierType()));
            linearFeetField.setText(String.valueOf(existing.getTotalLinearFeet()));
            notesField.setText(safe(existing.getNotes()));
        } else {
            jobNumberField.setText(String.valueOf(getNextJobNumber()));
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
        addFormRow(panel, gbc, 3, "Start Date (yyyy-MM-dd):", startDateField);
        addFormRow(panel, gbc, 4, "End Date (yyyy-MM-dd):", endDateField);
        addFormRow(panel, gbc, 5, "Location:", locationField);
        addFormRow(panel, gbc, 6, "Status:", statusField);
        addFormRow(panel, gbc, 7, "Project Manager:", managerField);
        addFormRow(panel, gbc, 8, "DOT Project #:", dotField);
        addFormRow(panel, gbc, 9, "Barrier Type:", barrierField);
        addFormRow(panel, gbc, 10, "Total Linear Feet:", linearFeetField);
        addFormRow(panel, gbc, 11, "Notes:", notesField);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(650, 450));
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

            LocalDate.parse(startDateField.getText().trim());
            LocalDate.parse(endDateField.getText().trim());

            if (existing == null) {
                Job job = new Job(
                        jobNumber,
                        contractorField.getText().trim(),
                        projectField.getText().trim(),
                        startDateField.getText().trim(),
                        endDateField.getText().trim(),
                        locationField.getText().trim()
                );

                job.setStatus(statusField.getText().trim());
                job.setProjectManager(managerField.getText().trim());
                job.setDotProjectNumber(dotField.getText().trim());
                job.setBarrierType(barrierField.getText().trim());
                job.setTotalLinearFeet(linearFeet);
                job.setNotes(notesField.getText().trim());

                return job;
            } else {
                existing.setContractor(contractorField.getText().trim());
                existing.setProjectName(projectField.getText().trim());
                existing.setStartDate(startDateField.getText().trim());
                existing.setEndDate(endDateField.getText().trim());
                existing.setLocation(locationField.getText().trim());
                existing.setStatus(statusField.getText().trim());
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

        if (showDispatchDialog(selected)) {
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

    private boolean showDispatchDialog(Task task) {
        ArrayList<Employee> availableDrivers = getAvailableDriversForTask(task);
        DefaultListModel<DriverOption> driverListModel = new DefaultListModel<>();

        for (Employee employee : availableDrivers) {
            driverListModel.addElement(new DriverOption(employee));
        }

        JList<DriverOption> driverJList = new JList<>(driverListModel);
        driverJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane driverScrollPane = new JScrollPane(driverJList);
        driverScrollPane.setPreferredSize(new Dimension(280, 220));

        JTextArea assignmentPreview = new JTextArea(12, 35);
        assignmentPreview.setEditable(false);
        assignmentPreview.setLineWrap(true);
        assignmentPreview.setWrapStyleWord(true);
        JScrollPane previewScrollPane = new JScrollPane(assignmentPreview);

        if (task.getAssignedEmployeeIds() != null && !task.getAssignedEmployeeIds().isEmpty()) {
            ArrayList<Integer> indexes = new ArrayList<>();

            for (int i = 0; i < driverListModel.size(); i++) {
                DriverOption option = driverListModel.get(i);
                if (task.getAssignedEmployeeIds().contains(option.employee.getEmployeeId())) {
                    indexes.add(i);
                }
            }

            int[] selectedIndexes = new int[indexes.size()];
            for (int i = 0; i < indexes.size(); i++) {
                selectedIndexes[i] = indexes.get(i);
            }
            driverJList.setSelectedIndices(selectedIndexes);
        }

        Runnable updatePreview = () -> {
            List<DriverOption> selected = driverJList.getSelectedValuesList();
            StringBuilder sb = new StringBuilder();

            if (selected.isEmpty()) {
                sb.append("No drivers assigned yet.");
            } else {
                for (DriverOption option : selected) {
                    Employee employee = option.employee;
                    sb.append(employee.getFullName())
                      .append(" | Truck: ")
                      .append(safe(employee.getAssignedTruckId()).isEmpty() ? "None" : employee.getAssignedTruckId())
                      .append(" | Trailer: ")
                      .append(safe(employee.getAssignedTrailerId()).isEmpty() ? "None" : employee.getAssignedTrailerId())
                      .append("\n");
                }
            }

            assignmentPreview.setText(sb.toString());
        };

        driverJList.addListSelectionListener(e -> updatePreview.run());
        updatePreview.run();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        panel.add(new JLabel("Available Drivers for " + task.getStartDate() + " " + task.getStartTime() + "-" + task.getEndTime() + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        panel.add(new JLabel("Assigned Equipment Preview:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        panel.add(driverScrollPane, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        panel.add(previewScrollPane, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Dispatch Task #" + task.getTaskId(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        ArrayList<Integer> assignedIds = new ArrayList<>();
        for (DriverOption option : driverJList.getSelectedValuesList()) {
            assignedIds.add(option.employee.getEmployeeId());
        }

        task.setAssignedEmployeeIds(assignedIds);

        if (!assignedIds.isEmpty()) {
            ArrayList<String> names = new ArrayList<>();
            for (Integer employeeId : assignedIds) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null) {
                    names.add(employee.getFullName());
                }
            }
            task.setNotes("Crew: " + String.join(", ", names));
            task.setStatus("Dispatched");
        } else {
            task.setNotes("");
            task.setStatus("Open");
        }

        return true;
    }

    private ArrayList<Employee> getAvailableDriversForTask(Task currentTask) {
        ArrayList<Employee> availableDrivers = new ArrayList<>();

        for (Employee employee : manager.getEmployees()) {
            String position = safe(employee.getPosition()).toLowerCase();

            if (!employee.isActive()) continue;
            if (!position.contains("driver")) continue;

            boolean conflict = false;

            for (Task task : manager.getTasks()) {
                if (task == currentTask) continue;

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

            if (!dateA.equals(dateB)) return false;

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
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        int taskId = Integer.parseInt(String.valueOf(taskModel.getValueAt(row, 0)));

        for (Task task : manager.getTasks()) {
            if (task.getTaskId() == taskId) {
                return task;
            }
        }

        return null;
    }

    private Job getSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        int jobNumber = Integer.parseInt(String.valueOf(jobModel.getValueAt(row, 0)));

        for (Job job : manager.getJobs()) {
            if (job.getJobNumber() == jobNumber) {
                return job;
            }
        }

        return null;
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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private static class DriverOption {
        private final Employee employee;

        private DriverOption(Employee employee) {
            this.employee = employee;
        }

        @Override
        public String toString() {
            String truck = employee.getAssignedTruckId() == null || employee.getAssignedTruckId().isEmpty()
                    ? "No Truck"
                    : employee.getAssignedTruckId();

            String trailer = employee.getAssignedTrailerId() == null || employee.getAssignedTrailerId().isEmpty()
                    ? "No Trailer"
                    : employee.getAssignedTrailerId();

            return employee.getFullName() + " | Truck: " + truck + " | Trailer: " + trailer;
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