import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeHomepageUI extends JFrame {

    private FleetManager manager;
    private Employee employee;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private Timer autoRefreshTimer;
    private JTextArea repairStatusArea;

    public EmployeeHomepageUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Foreman Portal - " + employee.getFullName());
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 242, 245));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(45, 74, 140));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeMsg = new JLabel("Foreman: " + employee.getFullName());
        welcomeMsg.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeMsg.setForeground(Color.WHITE);

        JLabel statsMsg = new JLabel(
                "Assigned Truck: " + getAssignedTruckDisplay() + " | Phone: " + employee.getPhoneNumber()
        );
        statsMsg.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsMsg.setForeground(new Color(200, 210, 230));

        header.add(welcomeMsg);
        header.add(statsMsg);
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBackground(new Color(240, 242, 245));

        String[] columns = {"Date", "Job Type", "Contractor", "Location", "Status", "Trucks"};
        tableModel = new DefaultTableModel(columns, 0);
        scheduleTable = new JTable(tableModel);

        setupTableAesthetics();
        refreshTableData();

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Active Dispatch (Auto-Refreshing)"));
        centerPanel.add(scrollPane);

        repairStatusArea = new JTextArea();
        repairStatusArea.setEditable(false);
        repairStatusArea.setLineWrap(true);
        repairStatusArea.setWrapStyleWord(true);
        repairStatusArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        refreshRepairStatus();

        JScrollPane repairScrollPane = new JScrollPane(repairStatusArea);
        repairScrollPane.setBorder(BorderFactory.createTitledBorder("My Truck Repair Status"));
        centerPanel.add(repairScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton writeUpBtn = new JButton("Report Mechanical Issue");
        JButton refreshBtn = new JButton("Manual Sync");
        JButton closeBtn = new JButton("Log Out");

        writeUpBtn.addActionListener(e -> {
            new MechanicalWriteUpFormUI(manager, null, employee).setVisible(true);
        });

        refreshBtn.addActionListener(e -> {
            refreshTableData();
            refreshRepairStatus();
        });

        closeBtn.addActionListener(e -> {
            autoRefreshTimer.stop();
            dispose();
        });

        footer.add(writeUpBtn);
        footer.add(refreshBtn);
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);

        autoRefreshTimer = new Timer(3000, e -> {
            checkForUrgentNotifications();
            refreshTableData();
            refreshRepairStatus();
        });

        autoRefreshTimer.start();
    }

    private String getAssignedTruckDisplay() {
        if (employee.getAssignedTruckId() == null || employee.getAssignedTruckId().isEmpty()) {
            return "None";
        }
        return employee.getAssignedTruckId();
    }

    private void setupTableAesthetics() {
        scheduleTable.setRowHeight(40);
        scheduleTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        scheduleTable.getTableHeader().setBackground(new Color(230, 230, 230));
        scheduleTable.setSelectionBackground(new Color(173, 216, 230));
    }

    private void refreshTableData() {
        int selectedRow = scheduleTable.getSelectedRow();
        tableModel.setRowCount(0);

        List<Task> allTasks = manager.getTasks();

        for (Task t : allTasks) {
            if (t.getForeman().equalsIgnoreCase(employee.getFullName())) {
                Object[] row = {
                        t.getStartDate(),
                        t.getJobType(),
                        t.getContractor(),
                        t.getLocation(),
                        t.getStatus(),
                        t.getAssignedTruck()
                };
                tableModel.addRow(row);
            }
        }

        if (selectedRow != -1 && selectedRow < tableModel.getRowCount()) {
            scheduleTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void refreshRepairStatus() {
        String assignedTruckId = employee.getAssignedTruckId();

        if (assignedTruckId == null || assignedTruckId.isEmpty()) {
            repairStatusArea.setText("No truck assigned.");
            return;
        }

        MechanicalWriteUp latest = null;

        for (MechanicalWriteUp writeUp : manager.getMechanicalWriteUps()) {
            if (assignedTruckId.equalsIgnoreCase(writeUp.getTruckId())) {
                latest = writeUp;
            }
        }

        if (latest == null) {
            repairStatusArea.setText("No active or recent write-up found for truck " + assignedTruckId + ".");
            return;
        }

        repairStatusArea.setText(
                "Truck: " + latest.getTruckId() + "\n" +
                "Status: " + latest.getRepairStatus() + "\n" +
                "Assigned Mechanic: " + latest.getAssignedMechanic() + "\n" +
                "Priority: " + latest.getPriority() + "\n" +
                "Safe To Drive: " + (latest.isSafeToDrive() ? "Yes" : "No") + "\n" +
                "Out Of Service: " + (latest.isOutOfService() ? "Yes" : "No") + "\n\n" +
                "Problem:\n" + latest.getProblemDescription() + "\n\n" +
                "Repair Notes:\n" + latest.getRepairNotes()
        );
    }

    private void checkForUrgentNotifications() {
        for (Task t : manager.getTasks()) {
            if (t.getForeman().equalsIgnoreCase(employee.getFullName())
                    && t.getStatus().equalsIgnoreCase("Canceled")) {

                JOptionPane.showMessageDialog(this,
                        "ATTENTION: Job at " + t.getLocation() + " for " + t.getContractor() + " has been CANCELED.",
                        "Dispatch Update",
                        JOptionPane.WARNING_MESSAGE);

                t.setStatus("Canceled (Acknowledged)");
            }
        }
    }
}