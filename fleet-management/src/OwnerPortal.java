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
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        createEmployeeBtn.addActionListener(e -> openChildWindow(new CreateEmployeeUI(manager)));
        createEquipmentBtn.addActionListener(e -> openChildWindow(new CreateEquipmentUI(manager)));
        assignTruckBtn.addActionListener(e -> openChildWindow(new AssignTruckUI(manager)));
        rosterBtn.addActionListener(e -> openChildWindow(new CompanyRosterUI(manager)));
        timeOffBtn.addActionListener(e -> openChildWindow(new ManagerTimeOffDashboardUI(manager)));

        sidebar.add(createEmployeeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createEquipmentBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(assignTruckBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(rosterBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(timeOffBtn);

        return sidebar;
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
        JButton logoutBtn = createStyledButton("LOG OUT", new Color(255, 80, 80));

        addBtn.addActionListener(e -> openChildWindow(new JobScreenUI(manager)));

        logoutBtn.addActionListener(e -> {
            dispose();
            Main.showLoginScreen();
        });

        btnPanel.add(addBtn);
        btnPanel.add(logoutBtn);

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

        if (openTickets == 0) {
            maintenanceLabel.setText("All Equipment Ready");
        } else {
            maintenanceLabel.setText(openTickets + " Open Repair Ticket(s)");
        }
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