import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
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
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        loadJobs();

        setVisible(true);
    }

    private void buildUI() {
        Color bg = new Color(12, 16, 26);
        Color accent = new Color(0, 229, 255);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(bg);
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(main);

        JLabel title = new JLabel("DISPATCH BOARD", SwingConstants.CENTER);
        title.setForeground(accent);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setBackground(bg);

        setupJobTable();
        setupTaskTable();

        center.add(new JScrollPane(jobTable));
        center.add(new JScrollPane(taskTable));

        main.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(bg);

        JButton refresh = new JButton("Refresh");
        JButton assignTruck = new JButton("Assign Truck");
        JButton clearTruck = new JButton("Clear Truck");
        JButton owner = new JButton("Owner Portal");
        JButton back = new JButton("Back");

        refresh.addActionListener(e -> loadJobs());
        assignTruck.addActionListener(e -> assignTruckToSelectedTask());
        clearTruck.addActionListener(e -> clearTruckFromSelectedTask());

        owner.addActionListener(e -> {
            dispose();
            new OwnerPortal(manager).setVisible(true);
        });

        back.addActionListener(e -> {
            dispose();
            Main.showLoginScreen();
        });

        bottom.add(refresh);
        bottom.add(assignTruck);
        bottom.add(clearTruck);
        bottom.add(owner);
        bottom.add(back);

        main.add(bottom, BorderLayout.SOUTH);
    }

    private void setupJobTable() {
        String[] cols = {"Job #", "Project", "Contractor", "Location", "Status"};

        jobModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        jobTable = new JTable(jobModel);
        jobTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jobTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadTasksForSelectedJob();
            }
        });
    }

    private void setupTaskTable() {
        String[] cols = {"Task #", "Date", "Time", "Type", "Foreman", "Truck", "Status"};

        taskModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        taskTable = new JTable(taskModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void loadJobs() {
        jobModel.setRowCount(0);

        List<Job> jobs = manager.getJobs();
        if (jobs == null) {
            taskModel.setRowCount(0);
            return;
        }

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

    private void loadTasksForSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            taskModel.setRowCount(0);
            return;
        }

        int jobId = Integer.parseInt(jobModel.getValueAt(row, 0).toString());
        taskModel.setRowCount(0);

        for (Task t : manager.getTasks()) {
            if (t.getJobId() == jobId) {
                taskModel.addRow(new Object[]{
                        t.getTaskId(),
                        safe(t.getStartDate()),
                        safe(t.getStartTime()),
                        safe(t.getJobType()),
                        safe(t.getForeman()),
                        safe(t.getAssignedTruck()),
                        safe(t.getStatus())
                });
            }
        }
    }

    private void assignTruckToSelectedTask() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        List<Truck> availableTrucks = getAvailableTrucks();
        if (availableTrucks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available trucks found.");
            return;
        }

        String[] truckOptions = new String[availableTrucks.size()];
        for (int i = 0; i < availableTrucks.size(); i++) {
            Truck truck = availableTrucks.get(i);
            truckOptions[i] = truck.getTruckID() + " | " + safe(truck.getStatus());
        }

        Object selection = JOptionPane.showInputDialog(
                this,
                "Select Truck:",
                "Assign Truck",
                JOptionPane.PLAIN_MESSAGE,
                null,
                truckOptions,
                truckOptions[0]
        );

        if (selection == null) {
            return;
        }

        String selectedText = selection.toString();
        String truckId = selectedText.split("\\|")[0].trim();

        task.setAssignedTruck(truckId);

        if (task.getStatus() == null || task.getStatus().trim().isEmpty() || task.getStatus().equalsIgnoreCase("Open")) {
            task.setStatus("Assigned");
        }

        Truck truck = findTruckById(truckId);
        if (truck != null) {
            String foreman = safe(task.getForeman());
            if (foreman.equals("N/A")) {
                foreman = "Task #" + task.getTaskId();
            }
            truck.markInUse(foreman);
        }

        loadTasksForSelectedJob();
        taskTable.clearSelection();
    }

    private void clearTruckFromSelectedTask() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        String truckId = task.getAssignedTruck();
        if (truckId == null || truckId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This task does not have an assigned truck.");
            return;
        }

        Truck truck = findTruckById(truckId);
        if (truck != null) {
            truck.markUnused();
        }

        task.setAssignedTruck("");
        if ("Assigned".equalsIgnoreCase(task.getStatus())) {
            task.setStatus("Open");
        }

        loadTasksForSelectedJob();
        taskTable.clearSelection();
    }

    private Task getSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        int taskId = Integer.parseInt(taskModel.getValueAt(row, 0).toString());

        for (Task task : manager.getTasks()) {
            if (task.getTaskId() == taskId) {
                return task;
            }
        }

        return null;
    }

    private List<Truck> getAvailableTrucks() {
        List<Truck> available = new ArrayList<>();

        for (Truck truck : manager.getTrucks()) {
            String status = safe(truck.getStatus());

            boolean down = truck.isDown();
            boolean inUse = status.equalsIgnoreCase("In Use");
            boolean outOfService = status.equalsIgnoreCase("Out of Service");

            if (!down && !inUse && !outOfService) {
                available.add(truck);
            }
        }

        return available;
    }

    private Truck findTruckById(String truckId) {
        for (Truck truck : manager.getTrucks()) {
            if (truck.getTruckID().equals(truckId)) {
                return truck;
            }
        }
        return null;
    }

    private String safe(String val) {
        return (val == null || val.trim().isEmpty()) ? "N/A" : val;
    }
}