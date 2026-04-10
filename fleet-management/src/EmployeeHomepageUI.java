import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeHomepageUI extends JFrame {

    private FleetManager manager;
    private Employee employee;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private Timer autoRefreshTimer; // The "Heartbeat"

    public EmployeeHomepageUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Employee Portal - " + employee.getFullName());
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== HEADER =====
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(45, 74, 140));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeMsg = new JLabel("Welcome, " + employee.getFullName());
        welcomeMsg.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeMsg.setForeground(Color.WHITE);

        JLabel statsMsg = new JLabel("Position: " + employee.getPosition() + " | Dept: " + employee.getDepartment());
        statsMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        statsMsg.setForeground(new Color(200, 210, 230));

        header.add(welcomeMsg);
        header.add(statsMsg);
        add(header, BorderLayout.NORTH);

        // ===== CENTER: THE CALENDAR/SCHEDULE TABLE =====
        String[] columns = {"Date", "Task Name", "Priority", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0); 
        scheduleTable = new JTable(tableModel);
        
        scheduleTable.setRowHeight(30);
        scheduleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        scheduleTable.setFont(new Font("Arial", Font.PLAIN, 13));
        
        refreshTableData(); 

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Live Assigned Schedule (Auto-Updating)"));
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM: ACTIONS =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton closeBtn = new JButton("Close Portal");
        closeBtn.addActionListener(e -> {
            autoRefreshTimer.stop(); // Stop the heartbeat when window closes
            dispose();
        });

        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        // ===== THE "REAL-TIME" HEARTBEAT =====
        // Checks for updates every 3 seconds (3000 milliseconds)
        autoRefreshTimer = new Timer(3000, e -> {
            checkForUrgentNotifications();
            refreshTableData();
        });
        autoRefreshTimer.start();
    }

    private void refreshTableData() {
        tableModel.setRowCount(0); 
        List<Task> allTasks = manager.getTasks();
        
        for (Task t : allTasks) {
            if (t.getEmployeeId() == employee.getEmployeeId()) {
                Object[] row = {
                    t.getStartDate(),
                    t.getTaskName(),
                    t.getPriority(),
                    t.getStatus(),
                    t.getNotes()
                };
                tableModel.addRow(row);
            }
        }
    }

    /**
     * Logic to trigger a popup if a job status is changed to "Canceled"
     */
    private void checkForUrgentNotifications() {
        for (Task t : manager.getTasks()) {
            if (t.getEmployeeId() == employee.getEmployeeId() && t.getStatus().equalsIgnoreCase("Canceled")) {
                // In a real app, you'd flag this so the popup only shows once
                JOptionPane.showMessageDialog(this, 
                    "ALERT: The task '" + t.getTaskName() + "' has been CANCELED by dispatch.", 
                    "Urgent Schedule Change", 
                    JOptionPane.WARNING_MESSAGE);
                
                // Temporary fix: Change status to 'Acknowledged' so the popup doesn't loop
                t.setStatus("Canceled (Viewed)"); 
            }
        }
    }
}