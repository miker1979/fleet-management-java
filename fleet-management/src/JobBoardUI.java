import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
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

    private DefaultListModel<String> availableForkliftListModel;
    private JList<String> availableForkliftList;

    private DefaultListModel<String> assignedForkliftListModel;
    private JList<String> assignedForkliftList;

    private JLabel selectedTaskLabel;
    private JTextArea previewArea;
    private JTextArea instructionsArea;

    public JobBoardUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Ghostline Logistics Tech - Dispatch Board");
        setSize(1600, 900);
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
        leftSplit.setResizeWeight(0.32);

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftSplit,
                createDispatchPanel()
        );
        mainSplit.setResizeWeight(0.64);

        main.add(mainSplit, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));

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
        panel.setPreferredSize(new Dimension(560, 0));
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

        availableForkliftListModel = new DefaultListModel<>();
        availableForkliftList = new JList<>(availableForkliftListModel);
        availableForkliftList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        assignedForkliftListModel = new DefaultListModel<>();
        assignedForkliftList = new JList<>(assignedForkliftListModel);
        assignedForkliftList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        previewArea = new JTextArea(9, 20);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);

        instructionsArea = new JTextArea(5, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);

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

        availableForkliftList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview();
            }
        });

        JPanel listsPanel = new JPanel(new GridLayout(6, 1, 8, 8));

        JPanel availableDriverPanel = new JPanel(new BorderLayout());
        availableDriverPanel.setBorder(BorderFactory.createTitledBorder("Available Drivers"));
        availableDriverPanel.add(new JScrollPane(availableDriverList), BorderLayout.CENTER);

        JPanel assignedDriverPanel = new JPanel(new BorderLayout());
        assignedDriverPanel.setBorder(BorderFactory.createTitledBorder("Assigned Crew"));
        assignedDriverPanel.add(new JScrollPane(assignedDriverList), BorderLayout.CENTER);

        JPanel availableForkliftPanel = new JPanel(new BorderLayout());
        availableForkliftPanel.setBorder(BorderFactory.createTitledBorder("Available Forklifts"));
        availableForkliftPanel.add(new JScrollPane(availableForkliftList), BorderLayout.CENTER);

        JPanel assignedForkliftPanel = new JPanel(new BorderLayout());
        assignedForkliftPanel.setBorder(BorderFactory.createTitledBorder("Assigned Forklifts"));
        assignedForkliftPanel.add(new JScrollPane(assignedForkliftList), BorderLayout.CENTER);

        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBorder(BorderFactory.createTitledBorder("Dispatch Instructions"));
        instructionsPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);

        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Equipment / Assignment Preview"));
        previewPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);

        listsPanel.add(availableDriverPanel);
        listsPanel.add(assignedDriverPanel);
        listsPanel.add(availableForkliftPanel);
        listsPanel.add(assignedForkliftPanel);
        listsPanel.add(instructionsPanel);
        listsPanel.add(previewPanel);

        panel.add(listsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));

        JButton assignBtn = new JButton("Assign Selected");
        JButton removeDriverBtn = new JButton("Remove Driver(s)");
        JButton removeForkliftBtn = new JButton("Remove Forklift(s)");
        JButton clearBtn = new JButton("Clear Dispatch");

        assignBtn.addActionListener(e -> assignSelections());
        removeDriverBtn.addActionListener(e -> removeDrivers());
        removeForkliftBtn.addActionListener(e -> removeForklifts());
        clearBtn.addActionListener(e -> clearDispatch());

        buttonPanel.add(assignBtn);
        buttonPanel.add(removeDriverBtn);
        buttonPanel.add(removeForkliftBtn);
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
        jobTable.setRowHeight(28);

        jobTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                loadTasks();
                clearDispatchPanel();
            }
        });
    }

    private void setupTaskTable() {
        String[] cols = {
                "Task #", "Date", "Start", "End", "Type", "Foreman", "Crew",
                "Truck(s)", "Trailer(s)", "Forklift(s)", "Status"
        };

        taskModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(taskModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(28);

        taskTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                loadDispatchPanelForSelectedTask();
            }
        });
    }

    private void loadJobs() {
        jobModel.setRowCount(0);

        List<Job> jobs = new ArrayList<>(manager.getJobs());
        jobs.sort(Comparator.comparingInt(Job::getJobNumber));

        for (Job job : jobs) {
            jobModel.addRow(new Object[]{
                    job.getJobNumber(),
                    safe(job.getProjectName()),
                    safe(job.getContractor()),
                    safe(job.getLocation()),
                    safe(job.getStatus())
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
                Comparator.comparing(Task::getStartDate, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(Task::getStartTime, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparingInt(Task::getTaskId)
        );

        for (Task task : tasks) {
            if (task.getJobId() == jobId) {
                taskModel.addRow(new Object[]{
                        task.getTaskId(),
                        safe(task.getStartDate()),
                        safe(task.getStartTime()),
                        safe(task.getEndTime()),
                        safe(task.getJobType()),
                        safe(task.getForeman()),
                        buildCrewDisplay(task),
                        buildTruckDisplay(task),
                        buildTrailerDisplay(task),
                        buildForkliftDisplay(task),
                        safe(task.getStatus())
                });
            }
        }
    }

    private void loadDispatchPanelForSelectedTask() {
        Task task = getSelectedTask();

        availableDriverListModel.clear();
        assignedDriverListModel.clear();
        availableForkliftListModel.clear();
        assignedForkliftListModel.clear();
        previewArea.setText("");
        instructionsArea.setText("");

        if (task == null) {
            selectedTaskLabel.setText("Select a task");
            return;
        }

        selectedTaskLabel.setText(
                "Task #" + task.getTaskId()
                        + " | " + safe(task.getStartDate())
                        + " | " + safe(task.getStartTime()) + "-" + safe(task.getEndTime())
                        + " | " + safe(task.getLocation())
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

        ArrayList<String> assignedForklifts = task.getAssignedForklifts() == null
                ? new ArrayList<>()
                : new ArrayList<>(task.getAssignedForklifts());

        for (Forklift forklift : manager.getForklifts()) {
            String unitId = forklift.getUnitId();
            boolean assignedElsewhere = manager.isForkliftAssigned(unitId) && !assignedForklifts.contains(unitId);

            if (!assignedElsewhere) {
                if (assignedForklifts.contains(unitId)) {
                    assignedForkliftListModel.addElement(unitId);
                } else {
                    availableForkliftListModel.addElement(unitId);
                }
            }
        }

        instructionsArea.setText(safe(task.getDispatchInstructions()));
        updateAssignedPreviewOnly();
    }

    private void assignSelections() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        List<DriverOption> selectedDrivers = availableDriverList.getSelectedValuesList();
        List<String> selectedForklifts = availableForkliftList.getSelectedValuesList();

        if (selectedDrivers.isEmpty() && selectedForklifts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one driver or forklift.");
            return;
        }

        ArrayList<Integer> assignedIds = task.getAssignedEmployeeIds();
        if (assignedIds == null) {
            assignedIds = new ArrayList<>();
        }

        for (DriverOption option : selectedDrivers) {
            int employeeId = option.employee.getEmployeeId();
            if (!assignedIds.contains(employeeId)) {
                assignedIds.add(employeeId);
            }
        }

        task.setAssignedEmployeeIds(assignedIds);

        for (String forkliftId : selectedForklifts) {
            task.addAssignedForklift(forkliftId);
        }

        task.setDispatchInstructions(instructionsArea.getText().trim());
        updateTaskStatus(task);

        DataStore.save(manager);
        loadTasks();
        reselectTask(task.getTaskId());
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

        task.setDispatchInstructions(instructionsArea.getText().trim());
        updateTaskStatus(task);

        DataStore.save(manager);
        loadTasks();
        reselectTask(task.getTaskId());
        loadDispatchPanelForSelectedTask();
    }

    private void removeForklifts() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        List<String> selected = assignedForkliftList.getSelectedValuesList();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one assigned forklift to remove.");
            return;
        }

        for (String forkliftId : selected) {
            task.removeAssignedForklift(forkliftId);
        }

        task.setDispatchInstructions(instructionsArea.getText().trim());
        updateTaskStatus(task);

        DataStore.save(manager);
        loadTasks();
        reselectTask(task.getTaskId());
        loadDispatchPanelForSelectedTask();
    }

    private void clearDispatch() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        task.clearAssignedEmployees();
        task.clearAssignedForklifts();
        task.setDispatchInstructions("");
        task.setStatus("Open");

        DataStore.save(manager);
        loadTasks();
        reselectTask(task.getTaskId());
        loadDispatchPanelForSelectedTask();
    }

    private void updateTaskStatus(Task task) {
        boolean hasDrivers = task.getAssignedEmployeeIds() != null && !task.getAssignedEmployeeIds().isEmpty();
        boolean hasForklifts = task.getAssignedForklifts() != null && !task.getAssignedForklifts().isEmpty();
        boolean hasInstructions = task.getDispatchInstructions() != null && !task.getDispatchInstructions().trim().isEmpty();

        if (hasDrivers || hasForklifts || hasInstructions) {
            task.setStatus("Dispatched");
        } else {
            task.setStatus("Open");
        }
    }

    private void updatePreview() {
        Task task = getSelectedTask();
        if (task == null) {
            previewArea.setText("");
            return;
        }

        List<DriverOption> selectedAvailableDrivers = availableDriverList.getSelectedValuesList();
        List<String> selectedAvailableForklifts = availableForkliftList.getSelectedValuesList();

        StringBuilder sb = new StringBuilder();

        if (!selectedAvailableDrivers.isEmpty()) {
            sb.append("Selected Drivers:\n");
            for (DriverOption option : selectedAvailableDrivers) {
                Employee employee = option.employee;
                sb.append("- ")
                  .append(employee.getFullName())
                  .append(" | Truck: ")
                  .append(displayValue(employee.getAssignedTruckId()))
                  .append(" | Trailer: ")
                  .append(displayValue(employee.getAssignedTrailerId()))
                  .append("\n");
            }
            sb.append("\n");
        }

        if (!selectedAvailableForklifts.isEmpty()) {
            sb.append("Selected Forklifts:\n");
            for (String forkliftId : selectedAvailableForklifts) {
                sb.append("- ").append(forkliftId).append("\n");
            }
            sb.append("\n");
        }

        sb.append("Currently Assigned Drivers:\n");
        appendAssignedDrivers(sb);

        sb.append("\nCurrently Assigned Forklifts:\n");
        appendAssignedForklifts(sb, task);

        previewArea.setText(sb.toString().trim());
    }

    private void updateAssignedPreviewOnly() {
        Task task = getSelectedTask();
        if (task == null) {
            previewArea.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Currently Assigned Drivers:\n");
        appendAssignedDrivers(sb);

        sb.append("\nCurrently Assigned Forklifts:\n");
        appendAssignedForklifts(sb, task);

        if (task.getDispatchInstructions() != null && !task.getDispatchInstructions().trim().isEmpty()) {
            sb.append("\nInstructions:\n").append(task.getDispatchInstructions().trim());
        }

        previewArea.setText(sb.toString().trim());
    }

    private void appendAssignedDrivers(StringBuilder sb) {
        if (assignedDriverListModel.isEmpty()) {
            sb.append("- None\n");
            return;
        }

        for (int i = 0; i < assignedDriverListModel.size(); i++) {
            Employee employee = assignedDriverListModel.get(i).employee;
            sb.append("- ")
              .append(employee.getFullName())
              .append(" | Truck: ")
              .append(displayValue(employee.getAssignedTruckId()))
              .append(" | Trailer: ")
              .append(displayValue(employee.getAssignedTrailerId()))
              .append("\n");
        }
    }

    private void appendAssignedForklifts(StringBuilder sb, Task task) {
        ArrayList<String> forklifts = task.getAssignedForklifts();
        if (forklifts == null || forklifts.isEmpty()) {
            sb.append("- None\n");
            return;
        }

        for (String forkliftId : forklifts) {
            sb.append("- ").append(forkliftId).append("\n");
        }
    }

    private void refreshAll() {
        int selectedJobRow = jobTable.getSelectedRow();
        Integer selectedJobNumber = null;
        Integer selectedTaskId = null;

        if (selectedJobRow != -1) {
            selectedJobNumber = (Integer) jobModel.getValueAt(selectedJobRow, 0);
        }

        Task selectedTask = getSelectedTask();
        if (selectedTask != null) {
            selectedTaskId = selectedTask.getTaskId();
        }

        loadJobs();

        if (selectedJobNumber != null) {
            for (int i = 0; i < jobModel.getRowCount(); i++) {
                if (((Integer) jobModel.getValueAt(i, 0)).equals(selectedJobNumber)) {
                    jobTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
            loadTasks();
        }

        if (selectedTaskId != null) {
            reselectTask(selectedTaskId);
            loadDispatchPanelForSelectedTask();
        } else {
            clearDispatchPanel();
        }
    }

    private void reselectTask(int taskId) {
        for (int i = 0; i < taskModel.getRowCount(); i++) {
            Integer tableTaskId = (Integer) taskModel.getValueAt(i, 0);
            if (tableTaskId == taskId) {
                taskTable.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private void clearDispatchPanel() {
        availableDriverListModel.clear();
        assignedDriverListModel.clear();
        availableForkliftListModel.clear();
        assignedForkliftListModel.clear();
        previewArea.setText("");
        instructionsArea.setText("");
        selectedTaskLabel.setText("Select a task");
    }

    private ArrayList<Employee> getAvailableDriversForTask(Task currentTask) {
        ArrayList<Employee> availableDrivers = new ArrayList<>();

        for (Employee employee : manager.getEmployees()) {
            String position = safe(employee.getPosition()).toLowerCase();

            if (!employee.isActive()) {
                continue;
            }

            if (!position.contains("driver")) {
                continue;
            }

            boolean alreadyAssignedToCurrentTask =
                    currentTask.getAssignedEmployeeIds() != null
                            && currentTask.getAssignedEmployeeIds().contains(employee.getEmployeeId());

            if (alreadyAssignedToCurrentTask) {
                continue;
            }

            boolean conflict = false;

            for (Task task : manager.getTasks()) {
                if (task == currentTask) {
                    continue;
                }

                if (task.getAssignedEmployeeIds() != null
                        && task.getAssignedEmployeeIds().contains(employee.getEmployeeId())) {

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

            boolean aCrossesMidnight = !aEnd.isAfter(aStart);
            boolean bCrossesMidnight = !bEnd.isAfter(bStart);

            if (aCrossesMidnight) {
                aEnd = aEnd.plusHours(24);
            }
            if (bCrossesMidnight) {
                bEnd = bEnd.plusHours(24);
            }

            LocalTime compareAStart = aStart;
            LocalTime compareBStart = bStart;

            if (aCrossesMidnight && bStart.isBefore(aStart)) {
                compareBStart = bStart.plusHours(24);
                bEnd = bEnd.plusHours(24);
            }

            if (bCrossesMidnight && aStart.isBefore(bStart)) {
                compareAStart = aStart.plusHours(24);
                aEnd = aEnd.plusHours(24);
            }

            return compareAStart.isBefore(bEnd) && aEnd.isAfter(compareBStart);
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

        return names.isEmpty() ? "" : String.join(", ", names);
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

    private String buildForkliftDisplay(Task task) {
        ArrayList<String> forklifts = task.getAssignedForklifts();
        return (forklifts == null || forklifts.isEmpty()) ? "" : String.join(", ", forklifts);
    }

    private Task getSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        int taskId = (int) taskModel.getValueAt(row, 0);
        return manager.findTaskById(taskId);
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "" : value.trim();
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