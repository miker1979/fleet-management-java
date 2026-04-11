import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeHomepageUI extends JFrame {

    private FleetManager manager;
    private Employee employee;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private Timer autoRefreshTimer;
    private JTextArea repairStatusArea;
    private LocalDate selectedDate = LocalDate.now();
    private JButton selectedButton;

    public EmployeeHomepageUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Employee Portal - " + employee.getFullName());
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(new Color(240, 242, 245));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(45, 74, 140));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel nameLabel = new JLabel(employee.getFullName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        JLabel infoLabel = new JLabel(
                "Role: " + employee.getPosition() +
                " | Truck: " + getAssignedTruckDisplay() +
                " | " + LocalDate.now()
        );
        infoLabel.setForeground(new Color(200, 210, 230));
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        header.add(nameLabel);
        header.add(infoLabel);
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));

        JPanel calendarStrip = new JPanel(new GridLayout(1, 7, 5, 5));
        calendarStrip.setBackground(new Color(240, 242, 245));
        calendarStrip.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd");

        for (int i = 0; i < 7; i++) {
            LocalDate day = LocalDate.now().plusDays(i);

            JButton dayBtn = new JButton(formatter.format(day));
            dayBtn.setFocusPainted(false);
            dayBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            dayBtn.setBorder(BorderFactory.createLineBorder(new Color(140, 160, 180)));
            dayBtn.setBackground(new Color(245, 245, 245));
            dayBtn.setForeground(Color.BLACK);

            if (day.equals(selectedDate)) {
                dayBtn.setBackground(new Color(70, 130, 180));
                dayBtn.setForeground(Color.WHITE);
                selectedButton = dayBtn;
            }

            dayBtn.addActionListener(e -> {
                selectedDate = day;

                if (selectedButton != null) {
                    selectedButton.setBackground(new Color(245, 245, 245));
                    selectedButton.setForeground(Color.BLACK);
                }

                dayBtn.setBackground(new Color(70, 130, 180));
                dayBtn.setForeground(Color.WHITE);
                selectedButton = dayBtn;

                refreshTableData();
            });

            calendarStrip.add(dayBtn);
        }

        mainPanel.add(calendarStrip, BorderLayout.NORTH);

        String[] columns = {"Date", "Job Type", "Contractor", "Location", "Status", "Truck"};
        tableModel = new DefaultTableModel(columns, 0);
        scheduleTable = new JTable(tableModel);

        setupTableAesthetics();
        refreshTableData();

        JScrollPane tableScroll = new JScrollPane(scheduleTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Daily Schedule"));
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        repairStatusArea = new JTextArea(5, 20);
        repairStatusArea.setEditable(false);
        repairStatusArea.setLineWrap(true);
        repairStatusArea.setWrapStyleWord(true);
        repairStatusArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        refreshRepairStatus();

        JScrollPane repairScroll = new JScrollPane(repairStatusArea);
        repairScroll.setBorder(BorderFactory.createTitledBorder("Truck Status"));
        mainPanel.add(repairScroll, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

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
        scheduleTable.setRowHeight(45);
        scheduleTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        scheduleTable.getTableHeader().setBackground(new Color(230, 230, 230));
        scheduleTable.setSelectionBackground(new Color(173, 216, 230));
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);

        List<Task> allTasks = manager.getTasks();

        for (Task t : allTasks) {
            if (t.getForeman().equalsIgnoreCase(employee.getFullName())
                    && t.getStartDate().equalsIgnoreCase(selectedDate.toString())) {

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
            repairStatusArea.setText("Truck " + assignedTruckId + " is clear. No active issues.");
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
                        "Job at " + t.getLocation() + " has been CANCELED.",
                        "Dispatch Update",
                        JOptionPane.WARNING_MESSAGE);

                t.setStatus("Canceled (Acknowledged)");
            }
        }
    }
}