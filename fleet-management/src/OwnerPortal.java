import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
        JButton deleteTaskBtn = createStyledButton("Delete Task", new Color(255, 100, 100));

        createTaskBtn.addActionListener(e -> createTask());
        editTaskBtn.addActionListener(e -> editSelectedTask());
        deleteTaskBtn.addActionListener(e -> deleteSelectedTask());

        actionPanel.add(createTaskBtn);
        actionPanel.add(editTaskBtn);
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
                "Task #", "Job #", "Date", "Time", "Task Type",
                "Contractor", "Location", "Foreman", "Assigned Equipment", "Status"
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
                    t.getJobType(),
                    t.getContractor(),
                    t.getLocation(),
                    t.getForeman(),
                    t.getAssignedTruck(),
                    t.getStatus()
            });
        }
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

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Job #:"));
        panel.add(jobNumberField);
        panel.add(new JLabel("Contractor:"));
        panel.add(contractorField);
        panel.add(new JLabel("Project Name:"));
        panel.add(projectField);
        panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (yyyy-MM-dd):"));
        panel.add(endDateField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Project Manager:"));
        panel.add(managerField);
        panel.add(new JLabel("DOT Project #:"));
        panel.add(dotField);
        panel.add(new JLabel("Barrier Type:"));
        panel.add(barrierField);
        panel.add(new JLabel("Total Linear Feet:"));
        panel.add(linearFeetField);
        panel.add(new JLabel("Notes:"));
        panel.add(notesField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
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
            refreshData();
        }
    }

    private Task showTaskDialog(Task existing) {
        JTextField taskIdField = new JTextField();
        JComboBox<String> jobIdCombo = new JComboBox<>();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField timeField = new JTextField("07:00");
        JTextField taskTypeField = new JTextField();
        JTextField contractorField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField foremanField = new JTextField();
        JTextField assignedTruckField = new JTextField();
        JTextField statusField = new JTextField("Open");
        JTextField notesField = new JTextField();

        for (Job job : manager.getJobs()) {
            jobIdCombo.addItem(String.valueOf(job.getJobNumber()));
        }

        if (jobIdCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Create a job before creating a task.");
            return null;
        }

        if (existing != null) {
            taskIdField.setText(String.valueOf(existing.getTaskId()));
            taskIdField.setEditable(false);
            jobIdCombo.setSelectedItem(String.valueOf(existing.getJobId()));
            dateField.setText(safe(existing.getStartDate()));
            timeField.setText(safe(existing.getStartTime()));
            taskTypeField.setText(safe(existing.getJobType()));
            contractorField.setText(safe(existing.getContractor()));
            locationField.setText(safe(existing.getLocation()));
            foremanField.setText(safe(existing.getForeman()));
            assignedTruckField.setText(safe(existing.getAssignedTruck()));
            statusField.setText(safe(existing.getStatus()));
            notesField.setText(safe(existing.getNotes()));
        } else {
            taskIdField.setText(String.valueOf(getNextTaskId()));
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Task #:"));
        panel.add(taskIdField);
        panel.add(new JLabel("Job #:"));
        panel.add(jobIdCombo);
        panel.add(new JLabel("Date (yyyy-MM-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:mm):"));
        panel.add(timeField);
        panel.add(new JLabel("Task Type:"));
        panel.add(taskTypeField);
        panel.add(new JLabel("Contractor:"));
        panel.add(contractorField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Foreman:"));
        panel.add(foremanField);
        panel.add(new JLabel("Assigned Equipment:"));
        panel.add(assignedTruckField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Notes:"));
        panel.add(notesField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
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

            LocalDate.parse(dateField.getText().trim());
            LocalTime.parse(timeField.getText().trim());

            Task rebuiltTask = new Task(
                    taskId,
                    jobId,
                    dateField.getText().trim(),
                    timeField.getText().trim(),
                    taskTypeField.getText().trim(),
                    contractorField.getText().trim(),
                    locationField.getText().trim(),
                    foremanField.getText().trim(),
                    assignedTruckField.getText().trim(),
                    statusField.getText().trim()
            );

            rebuiltTask.setNotes(notesField.getText().trim());

            if (existing == null) {
                return rebuiltTask;
            }

            int index = manager.getTasks().indexOf(existing);
            if (index >= 0) {
                manager.getTasks().set(index, rebuiltTask);
            }

            return rebuiltTask;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid task data: " + ex.getMessage());
            return null;
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
}