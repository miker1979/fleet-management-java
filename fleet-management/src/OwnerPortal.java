import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OwnerPortal extends JFrame {
    private FleetManager manager;
    private JTable jobBoard;
    private DefaultTableModel model;
    private JLabel availableTrucksLabel;
    private JLabel maintenanceLabel;

    public OwnerPortal(FleetManager manager) {
        this.manager = manager;

        setTitle("FleetTrack Pro - Owner Portal");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(15, 15, 15));
        setLayout(new BorderLayout(15, 15));

        add(createSidebar(), BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setOpaque(false);

        JPanel boardPanel = new JPanel(new BorderLayout());
        boardPanel.setOpaque(false);

        JLabel header = new JLabel(" LIVE OPERATIONS BOARD", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 28));
        header.setForeground(new Color(0, 255, 255));
        boardPanel.add(header, BorderLayout.NORTH);

        setupTable();
        boardPanel.add(new JScrollPane(jobBoard), BorderLayout.CENTER);
        centerPanel.add(boardPanel);

        JPanel mapPlaceholder = new JPanel(new BorderLayout());
        mapPlaceholder.setBackground(Color.WHITE);
        mapPlaceholder.setBorder(BorderFactory.createTitledBorder("Live Fleet Map View"));

        JLabel mapText = new JLabel("[Map Placeholder]", SwingConstants.CENTER);
        mapPlaceholder.add(mapText, BorderLayout.CENTER);

        centerPanel.add(mapPlaceholder);
        add(centerPanel, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(300, 0));
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
        JButton assignTruckBtn = createSideButton("Assign Truck");
        JButton rosterBtn = createSideButton("Company Roster");
        JButton timeOffBtn = createSideButton("Time Off Manager");
        JButton timesheetBtn = createSideButton("Review Timesheets");
        JButton jobSheetsBtn = createSideButton("Job Sheets");
        JButton maintenanceBtn = createSideButton("Maintenance");
        JButton reportsBtn = createSideButton("Reports");

        createEmployeeBtn.addActionListener(e ->
                new CreateEmployeeUI(manager).setVisible(true)
        );

        createEquipmentBtn.addActionListener(e ->
                new CreateEquipmentUI(manager).setVisible(true)
        );

        assignTruckBtn.addActionListener(e ->
                new AssignTruckUI(manager).setVisible(true)
        );

        rosterBtn.addActionListener(e ->
                new CompanyRosterUI(manager).setVisible(true)
        );

        timeOffBtn.addActionListener(e ->
                new ManagerTimeOffDashboardUI(manager).setVisible(true)
        );

        timesheetBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Timesheets coming soon")
        );

        jobSheetsBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Job Sheets coming soon")
        );

        maintenanceBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Maintenance dashboard coming soon")
        );

        reportsBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Reports coming soon")
        );

        sidebar.add(createEmployeeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createEquipmentBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(assignTruckBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(rosterBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        sidebar.add(timeOffBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(timesheetBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(jobSheetsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(maintenanceBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(reportsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        return sidebar;
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
        box.setMaximumSize(new Dimension(280, 120));
        return box;
    }

    private JButton createSideButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(270, 45));
        btn.setBackground(new Color(45, 45, 45));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void setupTable() {
        String[] columns = {
                "Task #", "Job #", "Date", "Time", "Task Type",
                "Contractor", "Location", "Foreman", "Assigned Equipment", "Status"
        };

        model = new DefaultTableModel(columns, 0);
        jobBoard = new JTable(model);

        jobBoard.setBackground(new Color(30, 30, 30));
        jobBoard.setForeground(Color.WHITE);
        jobBoard.setRowHeight(45);
        jobBoard.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JTableHeader header = jobBoard.getTableHeader();
        header.setBackground(new Color(45, 45, 45));
        header.setForeground(new Color(0, 255, 255));
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    private JPanel createBottomPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        btnPanel.setBackground(new Color(15, 15, 15));

        JButton addBtn = createStyledButton("+ DISPATCH TASK", new Color(0, 255, 150));
        JButton syncBtn = createStyledButton("SYNC SYSTEM", Color.LIGHT_GRAY);

        addBtn.addActionListener(e -> new JobScreenUI(manager).setVisible(true));
        syncBtn.addActionListener(e -> refreshData());

        btnPanel.add(addBtn);
        btnPanel.add(syncBtn);

        return btnPanel;
    }

    private JButton createStyledButton(String text, Color borderColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(borderColor);
        btn.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        btn.setFocusPainted(false);
        return btn;
    }

    public void refreshData() {
        model.setRowCount(0);

        List<Task> sortedTasks = new ArrayList<>(manager.getTasks());
        sortedTasks.sort(
                Comparator.comparing(this::parseTaskDate)
                        .thenComparing(this::parseTaskTime)
        );

        for (Task t : sortedTasks) {
            model.addRow(new Object[]{
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

        int total = manager.getTrucks().size();
        int down = (int) manager.getTrucks().stream().filter(Truck::isDown).count();
        availableTrucksLabel.setText((total - down) + " / " + total + " Available");
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
            String raw = task.getStartTime();

            if (raw == null || raw.isEmpty()) {
                return LocalTime.MAX;
            }

            return LocalTime.parse(raw);
        } catch (Exception e) {
            return LocalTime.MAX;
        }
    }
}