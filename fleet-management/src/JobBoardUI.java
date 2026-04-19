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
        setSize(1200, 720);
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

        JButton assignBtn = new JButton("Assign Driver + Equipment");
        JButton clearBtn = new JButton("Clear Assignment");
        JButton refreshBtn = new JButton("Refresh");
        JButton ownerBtn = new JButton("Owner Portal");
        JButton backBtn = new JButton("Back");

        assignBtn.addActionListener(e -> assignFullDispatch());
        clearBtn.addActionListener(e -> clearAssignment());
        refreshBtn.addActionListener(e -> loadJobs());

        ownerBtn.addActionListener(e -> {
            dispose();
            new OwnerPortal(manager).setVisible(true);
        });

        backBtn.addActionListener(e -> {
            dispose();
            Main.showLoginScreen();
        });

        bottom.add(assignBtn);
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
        String[] cols = {"Task #", "Date", "Type", "Foreman", "Truck", "Trailer", "Status"};

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

        for (Job j : manager.getJobs()) {
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

        for (Task t : manager.getTasks()) {
            if (t.getJobId() == jobId) {
                taskModel.addRow(new Object[]{
                        t.getTaskId(),
                        safe(t.getStartDate()),
                        safe(t.getJobType()),
                        safe(t.getForeman()),
                        safe(t.getAssignedTruck()),
                        safe(t.getAssignedTrailer()),
                        safe(t.getStatus())
                });
            }
        }
    }

    private void assignFullDispatch() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        Employee driver = selectDriver();
        if (driver == null) {
            return;
        }

        Truck truck = selectTruck();
        if (truck == null) {
            return;
        }

        Trailer trailer = selectTrailer();
        if (trailer == null) {
            return;
        }

        clearExistingDriverAssignment(driver);

        task.setForeman(driver.getFullName());
        task.setAssignedTruck(truck.getTruckID());
        task.setAssignedTrailer(trailer.getTrailerId());
        task.setStatus("Assigned");

        driver.setAssignedTruckId(truck.getTruckID());
        driver.setAssignedTrailerId(trailer.getTrailerId());

        truck.markInUse(driver.getFullName());
        trailer.markInUse(driver.getFullName());

        loadTasks();
        taskTable.clearSelection();
    }

    private void clearAssignment() {
        Task task = getSelectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        String truckId = task.getAssignedTruck();
        String trailerId = task.getAssignedTrailer();
        String foremanName = task.getForeman();

        if (truckId != null && !truckId.trim().isEmpty()) {
            Truck truck = manager.findTruckById(truckId);
            if (truck != null) {
                truck.markUnused();
            }
        }

        if (trailerId != null && !trailerId.trim().isEmpty()) {
            Trailer trailer = manager.findTrailerById(trailerId);
            if (trailer != null) {
                trailer.markUnused();
            }
        }

        if (foremanName != null && !foremanName.trim().isEmpty()) {
            for (Employee employee : manager.getEmployees()) {
                if (employee.getFullName().equalsIgnoreCase(foremanName)) {
                    employee.setAssignedTruckId("");
                    employee.setAssignedTrailerId("");
                    break;
                }
            }
        }

        task.setAssignedTruck("");
        task.setAssignedTrailer("");
        task.setForeman("");
        task.setStatus("Open");

        loadTasks();
        taskTable.clearSelection();
    }

    private void clearExistingDriverAssignment(Employee driver) {
        String currentTruckId = driver.getAssignedTruckId();
        String currentTrailerId = driver.getAssignedTrailerId();

        if (currentTruckId != null && !currentTruckId.trim().isEmpty()) {
            Truck truck = manager.findTruckById(currentTruckId);
            if (truck != null) {
                truck.markUnused();
            }
        }

        if (currentTrailerId != null && !currentTrailerId.trim().isEmpty()) {
            Trailer trailer = manager.findTrailerById(currentTrailerId);
            if (trailer != null) {
                trailer.markUnused();
            }
        }

        for (Task task : manager.getTasks()) {
            if (driver.getFullName().equalsIgnoreCase(safe(task.getForeman()))) {
                task.setAssignedTruck("");
                task.setAssignedTrailer("");
                task.setForeman("");
                task.setStatus("Open");
            }
        }

        driver.setAssignedTruckId("");
        driver.setAssignedTrailerId("");
    }

    private Employee selectDriver() {
        List<Employee> drivers = new ArrayList<>();

        for (Employee employee : manager.getEmployees()) {
            String position = employee.getPosition();
            if (position != null && position.toLowerCase().contains("driver")) {
                drivers.add(employee);
            }
        }

        if (drivers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No drivers available.");
            return null;
        }

        String[] options = drivers.stream()
                .map(Employee::getFullName)
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Driver",
                "Driver",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == null) {
            return null;
        }

        for (Employee employee : drivers) {
            if (employee.getFullName().equals(selected)) {
                return employee;
            }
        }

        return null;
    }

    private Truck selectTruck() {
        List<Truck> available = new ArrayList<>();

        for (Truck truck : manager.getTrucks()) {
            String status = safe(truck.getStatus());
            if (!truck.isDown()
                    && !status.equalsIgnoreCase("In Use")
                    && !status.equalsIgnoreCase("Out of Service")) {
                available.add(truck);
            }
        }

        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No trucks available.");
            return null;
        }

        String[] options = available.stream()
                .map(t -> t.getTruckID() + " | " + safe(t.getStatus()))
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Truck",
                "Truck",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == null) {
            return null;
        }

        String truckId = selected.split("\\|")[0].trim();
        return manager.findTruckById(truckId);
    }

    private Trailer selectTrailer() {
        List<Trailer> available = new ArrayList<>();

        for (Trailer trailer : manager.getTrailers()) {
            String status = safe(trailer.getStatus());
            if (!trailer.isDown()
                    && !status.equalsIgnoreCase("In Use")
                    && !status.equalsIgnoreCase("Out of Service")) {
                available.add(trailer);
            }
        }

        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No trailers available.");
            return null;
        }

        String[] options = available.stream()
                .map(t -> t.getTrailerId() + " | " + safe(t.getTrailerType()) + " | " + safe(t.getTrailerLength()))
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Trailer",
                "Trailer",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == null) {
            return null;
        }

        String trailerId = selected.split("\\|")[0].trim();
        return manager.findTrailerById(trailerId);
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
}