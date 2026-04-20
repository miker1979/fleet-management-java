import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

    private DefaultListModel<DriverOption> availableDriverListModel;
    private JList<DriverOption> availableDriverList;

    private DefaultListModel<DriverOption> assignedDriverListModel;
    private JList<DriverOption> assignedDriverList;

    private JLabel selectedTaskLabel;
    private JTextArea previewArea;

    public JobBoardUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Ghostline Logistics Tech - Dispatch Board");
        setSize(1500, 820);
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

        setupJobTable();
        setupTaskTable();

        JSplitPane leftSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(jobTable),
                new JScrollPane(taskTable)
        );
        leftSplit.setResizeWeight(0.35);

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftSplit,
                createDispatchPanel()
        );
        mainSplit.setResizeWeight(0.68);

        main.add(mainSplit, BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton refreshBtn = new JButton("Refresh");
        JButton ownerBtn = new JButton("Owner Portal");
        JButton backBtn = new JButton("Back");

        refreshBtn.addActionListener(e -> refreshAll());
        ownerBtn.addActionListener(e -> {
            dispose();
            new OwnerPortal(manager).setVisible(true);
        });
        backBtn.addActionListener(e -> {
            dispose();
            Main.showLoginScreen();
        });

        bottom.add(refreshBtn);
        bottom.add(ownerBtn);
        bottom.add(backBtn);

        main.add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createDispatchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(470, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Dispatch Panel"));

        selectedTaskLabel = new JLabel("Select a task");
        selectedTaskLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(selectedTaskLabel, BorderLayout.NORTH);

        availableDriverListModel = new DefaultListModel<>();
        availableDriverList = new JList<>(availableDriverListModel);
        availableDriverList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        assignedDriverListModel = new DefaultListModel<>();
        assignedDriverList = new JList<>(assignedDriverListModel);
        assignedDriverList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        previewArea = new JTextArea(8, 20);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);

        availableDriverList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview();
            }
        });

        assignedDriverList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && availableDriverList.isSelectionEmpty()) {
                updateAssignedPreviewOnly();
            }
        });

        JPanel listsPanel = new JPanel(new GridLayout(3, 1, 8, 8));

        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Drivers"));
        availablePanel.add(new JScrollPane(availableDriverList), BorderLayout.CENTER);

        JPanel assignedPanel = new JPanel(new BorderLayout());
        assignedPanel.setBorder(BorderFactory.createTitledBorder("Assigned Crew"));
        assignedPanel.add(new JScrollPane(assignedDriverList), BorderLayout.CENTER);

        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Equipment Preview"));
        previewPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);

        listsPanel.add(availablePanel);
        listsPanel.add(assignedPanel);
        listsPanel.add(previewPanel);

        panel.add(listsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 8, 8));

        JButton assignBtn = new JButton("Assign");
        JButton removeBtn = new JButton("Remove");
        JButton clearBtn = new JButton("Clear Dispatch");

        assignBtn.addActionListener(e -> assignDrivers());
        removeBtn.addActionListener(e -> removeDrivers());
        clearBtn.addActionListener(e -> clearDispatch());

        buttonPanel.add(assignBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(clearBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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

        jobTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadTasks();
                    clearDispatchPanel();
                }
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

        taskTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadDispatchPanelForSelectedTask();
                }
            }
        });
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
        tasks.sort(
                Comparator.comparing(Task::getStartDate, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Task::getStartTime, String.CASE_INSENSITIVE_ORDER)
                        .thenComparingInt(Task::getTaskId)
        );

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

    private void loadDispatchPanelForSelectedTask() {
        Task task = getSelectedTask();

        availableDriverListModel.clear();
        assignedDriverListModel.clear();
        previewArea.setText("");

        if (task == null) {
            selectedTaskLabel.setText("Select a task");
            return;
        }

        selectedTaskLabel.setText(
                "Task #" + task.getTaskId() +
                " | " + safe(task.getStartDate()) +
                " | " + safe(task.getStartTime()) + "-" + safe(task.getEndTime()) +
                " | " + safe(task.getLocation())
        );

        ArrayList<Employee> availableDrivers = getAvailableDriversForTask(task);
        for (Employee employee : availableDrivers) {
            availableDriverListModel.addElement(new DriverOption(employee));
        }

        if (task.getAssignedEmployeeIds() != null) {
            for (Integer employeeId : task.getAssignedEmployeeIds()) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null) {
                    assignedDriverListModel.addElement(new DriverOption(employee));
                }
            }
        }

        updateAssignedPreviewOnly();
    }

    private void assignDrivers() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        List<DriverOption> selected = availableDriverList.getSelectedValuesList();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one available driver.");
            return;
        }

        ArrayList<Integer> assignedIds = task.getAssignedEmployeeIds();
        if (assignedIds == null) {
            assignedIds = new ArrayList<>();
        }

        for (DriverOption option : selected) {
            int employeeId = option.employee.getEmployeeId();
            if (!assignedIds.contains(employeeId)) {
                assignedIds.add(employeeId);
            }
        }

        task.setAssignedEmployeeIds(assignedIds);
        updateTaskCrewNotesAndStatus(task);

        DataStore.save(manager);
        loadTasks();
        loadDispatchPanelForSelectedTask();
    }

    private void removeDrivers() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        List<DriverOption> selected = assignedDriverList.getSelectedValuesList();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one assigned driver to remove.");
            return;
        }

        for (DriverOption option : selected) {
            task.removeAssignedEmployeeId(option.employee.getEmployeeId());
        }

        updateTaskCrewNotesAndStatus(task);

        DataStore.save(manager);
        loadTasks();
        loadDispatchPanelForSelectedTask();
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
        loadDispatchPanelForSelectedTask();
    }

    private void updateTaskCrewNotesAndStatus(Task task) {
        ArrayList<String> assignedNames = new ArrayList<>();

        if (task.getAssignedEmployeeIds() != null) {
            for (Integer employeeId : task.getAssignedEmployeeIds()) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null) {
                    assignedNames.add(employee.getFullName());
                }
            }
        }

        if (!assignedNames.isEmpty()) {
            task.setNotes("Crew: " + String.join(", ", assignedNames));
            task.setStatus("Dispatched");
        } else {
            task.setNotes("");
            task.setStatus("Open");
        }
    }

    private void updatePreview() {
        List<DriverOption> selectedAvailable = availableDriverList.getSelectedValuesList();

        if (!selectedAvailable.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            for (DriverOption option : selectedAvailable) {
                Employee employee = option.employee;
                sb.append(employee.getFullName())
                  .append(" | Truck: ")
                  .append(displayValue(employee.getAssignedTruckId()))
                  .append(" | Trailer: ")
                  .append(displayValue(employee.getAssignedTrailerId()))
                  .append("\n");
            }

            previewArea.setText(sb.toString().trim());
            return;
        }

        updateAssignedPreviewOnly();
    }

    private void updateAssignedPreviewOnly() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < assignedDriverListModel.getSize(); i++) {
            Employee employee = assignedDriverListModel.get(i).employee;
            sb.append(employee.getFullName())
              .append(" | Truck: ")
              .append(displayValue(employee.getAssignedTruckId()))
              .append(" | Trailer: ")
              .append(displayValue(employee.getAssignedTrailerId()))
              .append("\n");
        }

        previewArea.setText(sb.toString().trim());
    }

    private void refreshAll() {
        loadJobs();
        loadTasks();
        loadDispatchPanelForSelectedTask();
    }

    private void clearDispatchPanel() {
        availableDriverListModel.clear();
        assignedDriverListModel.clear();
        previewArea.setText("");
        selectedTaskLabel.setText("Select a task");
    }

    private ArrayList<Employee> getAvailableDriversForTask(Task currentTask) {
        ArrayList<Employee> availableDrivers = new ArrayList<>();

        for (Employee employee : manager.getEmployees()) {
            String position = safe(employee.getPosition()).toLowerCase();

            if (!employee.isActive()) continue;
            if (!position.contains("driver")) continue;

            boolean alreadyAssignedToCurrentTask =
                    currentTask.getAssignedEmployeeIds() != null &&
                    currentTask.getAssignedEmployeeIds().contains(employee.getEmployeeId());

            if (alreadyAssignedToCurrentTask) {
                continue;
            }

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

    private String displayValue(String value) {
        String safeValue = safe(value);
        return safeValue.isEmpty() ? "None" : safeValue;
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