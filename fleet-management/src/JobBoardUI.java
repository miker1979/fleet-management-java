import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JobBoardUI extends JFrame {

    private final FleetManager manager;

    private JTable jobTable;
    private JTable taskTable;

    private DefaultTableModel jobModel;
    private DefaultTableModel taskModel;

    public JobBoardUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Ghostline Logistics Tech - Dispatch Board");
        setSize(1350, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        loadJobs();

        setVisible(true);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(main);

        JLabel title = new JLabel("DISPATCH BOARD", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));

        setupJobTable();
        setupTaskTable();

        center.add(new JScrollPane(jobTable));
        center.add(new JScrollPane(taskTable));

        main.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton dispatchBtn = new JButton("Dispatch Driver(s)");
        JButton clearBtn = new JButton("Clear Dispatch");
        JButton refreshBtn = new JButton("Refresh");
        JButton ownerBtn = new JButton("Owner Portal");
        JButton backBtn = new JButton("Back");

        dispatchBtn.addActionListener(e -> dispatchSelectedTask());
        clearBtn.addActionListener(e -> clearDispatch());
        refreshBtn.addActionListener(e -> loadJobs());

        ownerBtn.addActionListener(e -> {
            dispose();
            new OwnerPortal(manager).setVisible(true);
        });

        backBtn.addActionListener(e -> {
            dispose();
            Main.showLoginScreen();
        });

        bottom.add(dispatchBtn);
        bottom.add(clearBtn);
        bottom.add(refreshBtn);
        bottom.add(ownerBtn);
        bottom.add(backBtn);

        main.add(bottom, BorderLayout.SOUTH);
    }

    private void setupJobTable() {
        String[] cols = {"Job #", "Project", "Contractor", "Location", "Status"};

        jobModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jobTable = new JTable(jobModel);
        jobTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jobTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadTasks();
            }
        });
    }

    private void setupTaskTable() {
        String[] cols = {
                "Task #", "Date", "Start", "End", "Type", "Foreman", "Crew", "Truck(s)", "Trailer(s)", "Status"
        };

        taskModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(taskModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void loadJobs() {
        jobModel.setRowCount(0);

        List<Job> jobs = new ArrayList<>(manager.getJobs());
        jobs.sort(Comparator.comparingInt(Job::getJobNumber));

        for (Job j : jobs) {
            jobModel.addRow(new Object[]{
                    j.getJobNumber(),
                    safe(j.getProjectName()),
                    safe(j.getContractor()),
                    safe(j.getLocation()),
                    safe(j.getStatus())
            });
        }

        taskModel.setRowCount(0);
    }

    private void loadTasks() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            taskModel.setRowCount(0);
            return;
        }

        int jobId = (int) jobModel.getValueAt(row, 0);
        taskModel.setRowCount(0);

        List<Task> tasks = new ArrayList<>(manager.getTasks());
        tasks.sort(Comparator.comparingInt(Task::getTaskId));

        for (Task t : tasks) {
            if (t.getJobId() == jobId) {
                taskModel.addRow(new Object[]{
                        t.getTaskId(),
                        safe(t.getStartDate()),
                        safe(t.getStartTime()),
                        safe(t.getEndTime()),
                        safe(t.getJobType()),
                        safe(t.getForeman()),
                        buildCrewDisplay(t),
                        buildTruckDisplay(t),
                        buildTrailerDisplay(t),
                        safe(t.getStatus())
                });
            }
        }
    }

    private void dispatchSelectedTask() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        if (showDispatchDialog(task)) {
            DataStore.save(manager);
            loadTasks();
            taskTable.clearSelection();
        }
    }

    private void clearDispatch() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        task.clearAssignedEmployees();
        task.setNotes("");
        task.setStatus("Open");

        DataStore.save(manager);
        loadTasks();
        taskTable.clearSelection();
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
        driverScrollPane.setPreferredSize(new Dimension(320, 240));

        JTextArea previewArea = new JTextArea(12, 38);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        JScrollPane previewScrollPane = new JScrollPane(previewArea);

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
                sb.append("No drivers selected.");
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

            previewArea.setText(sb.toString());
        };

        driverJList.addListSelectionListener(e -> updatePreview.run());
        updatePreview.run();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.weighty = 0;
        panel.add(new JLabel("Available Drivers for " + task.getStartDate() + " " + task.getStartTime() + "-" + task.getEndTime() + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.55;
        gbc.weighty = 0;
        panel.add(new JLabel("Equipment Preview:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.45;
        gbc.weighty = 1.0;
        panel.add(driverScrollPane, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.55;
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
        ArrayList<String> assignedNames = new ArrayList<>();

        for (DriverOption option : driverJList.getSelectedValuesList()) {
            assignedIds.add(option.employee.getEmployeeId());
            assignedNames.add(option.employee.getFullName());
        }

        task.setAssignedEmployeeIds(assignedIds);

        if (!assignedNames.isEmpty()) {
            task.setNotes("Crew: " + String.join(", ", assignedNames));
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

    private String buildCrewDisplay(Task task) {
        ArrayList<String> names = new ArrayList<>();

        if (task.getAssignedEmployeeIds() != null) {
            for (Integer employeeId : task.getAssignedEmployeeIds()) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null) {
                    names.add(employee.getFullName());
                }
            }
        }

        if (!names.isEmpty()) {
            return String.join(", ", names);
        }

        String notes = safe(task.getNotes());
        if (notes.startsWith("Crew:")) {
            return notes.substring("Crew:".length()).trim();
        }

        return "";
    }

    private String buildTruckDisplay(Task task) {
        ArrayList<String> trucks = new ArrayList<>();

        if (task.getAssignedEmployeeIds() != null) {
            for (Integer employeeId : task.getAssignedEmployeeIds()) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null) {
                    String truckId = safe(employee.getAssignedTruckId());
                    if (!truckId.isEmpty() && !trucks.contains(truckId)) {
                        trucks.add(truckId);
                    }
                }
            }
        }

        return trucks.isEmpty() ? "" : String.join(", ", trucks);
    }

    private String buildTrailerDisplay(Task task) {
        ArrayList<String> trailers = new ArrayList<>();

        if (task.getAssignedEmployeeIds() != null) {
            for (Integer employeeId : task.getAssignedEmployeeIds()) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null) {
                    String trailerId = safe(employee.getAssignedTrailerId());
                    if (!trailerId.isEmpty() && !trailers.contains(trailerId)) {
                        trailers.add(trailerId);
                    }
                }
            }
        }

        return trailers.isEmpty() ? "" : String.join(", ", trailers);
    }

    private Task getSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        int id = (int) taskModel.getValueAt(row, 0);

        for (Task task : manager.getTasks()) {
            if (task.getTaskId() == id) {
                return task;
            }
        }

        return null;
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "" : value;
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
}