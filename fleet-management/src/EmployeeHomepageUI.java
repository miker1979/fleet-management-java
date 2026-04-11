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

    public EmployeeHomepageUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Foreman Portal - " + employee.getFullName());
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 242, 245));

        // ===== HEADER: DRIVER/FOREMAN INFO =====
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(45, 74, 140)); // Deep Blue
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeMsg = new JLabel("Foreman: " + employee.getFullName());
        welcomeMsg.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeMsg.setForeground(Color.WHITE);

        JLabel statsMsg = new JLabel("Assigned Area: I-10 Logistics | Phone: " + employee.getPhoneNumber());
        statsMsg.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsMsg.setForeground(new Color(200, 210, 230));

        header.add(welcomeMsg);
        header.add(statsMsg);
        add(header, BorderLayout.NORTH);

        // ===== CENTER: THE BOARD VIEW =====
        // UPDATED: Added Contractor column to match the new Task structure
        String[] columns = {"Date", "Job Type", "Contractor", "Location", "Status", "Trucks"};
        tableModel = new DefaultTableModel(columns, 0); 
        scheduleTable = new JTable(tableModel);
        
        setupTableAesthetics();
        refreshTableData(); 

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Active Dispatch (Auto-Refreshing)"));
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM: ACTIONS =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton refreshBtn = new JButton("Manual Sync");
        JButton closeBtn = new JButton("Log Out");

        refreshBtn.addActionListener(e -> refreshTableData());
        closeBtn.addActionListener(e -> {
            autoRefreshTimer.stop(); 
            dispose();
        });

        footer.add(refreshBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        // ===== REAL-TIME SYNC =====
        autoRefreshTimer = new Timer(3000, e -> {
            checkForUrgentNotifications();
            refreshTableData();
        });
        autoRefreshTimer.start();
    }

    private void setupTableAesthetics() {
        scheduleTable.setRowHeight(40); // Taller rows for readability in the field
        scheduleTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        scheduleTable.getTableHeader().setBackground(new Color(230, 230, 230));
        scheduleTable.setSelectionBackground(new Color(173, 216, 230));
    }

    private void refreshTableData() {
        // Prevent table from flickering if data hasn't changed (Optional)
        int selectedRow = scheduleTable.getSelectedRow();
        tableModel.setRowCount(0); 
        
        List<Task> allTasks = manager.getTasks();
        
        for (Task t : allTasks) {
            // Updated to check Foreman name since we are using names for dispatch now
            if (t.getForeman().equalsIgnoreCase(employee.getFullName())) {
                Object[] row = {
                    t.getStartDate(),
                    t.getJobType(),
                    t.getContractor(), // NEW: Pulice, Sundt, etc.
                    t.getLocation(),
                    t.getStatus(),
                    t.getAssignedTruck() // Shows the equipment list
                };
                tableModel.addRow(row);
            }
        }
        
        // Restore selection if applicable
        if (selectedRow != -1 && selectedRow < tableModel.getRowCount()) {
            scheduleTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void checkForUrgentNotifications() {
        for (Task t : manager.getTasks()) {
            // Notify Foreman if dispatch cancels a job
            if (t.getForeman().equalsIgnoreCase(employee.getFullName()) && t.getStatus().equalsIgnoreCase("Canceled")) {
                
                JOptionPane.showMessageDialog(this, 
                    "ATTENTION: Job at " + t.getLocation() + " for " + t.getContractor() + " has been CANCELED.", 
                    "Dispatch Update", 
                    JOptionPane.WARNING_MESSAGE);
                
                t.setStatus("Canceled (Acknowledged)"); 
            }
        }
    }
}