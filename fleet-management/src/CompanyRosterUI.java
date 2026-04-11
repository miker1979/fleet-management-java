import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CompanyRosterUI extends JFrame {

    private FleetManager manager;

    private JTable employeeTable;
    private JTable truckTable;

    private DefaultTableModel employeeModel;
    private DefaultTableModel truckModel;

    public CompanyRosterUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Company Roster");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Company Roster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // ===== EMPLOYEE SECTION =====
        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.setBorder(BorderFactory.createTitledBorder("Employees"));

        String[] employeeColumns = {
                "Employee ID", "Name", "Position", "Department",
                "Phone", "Email", "Assigned Truck", "Status"
        };

        employeeModel = new DefaultTableModel(employeeColumns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        employeeTable = new JTable(employeeModel);
        employeeTable.setRowHeight(28);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        employeePanel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // ===== TRUCK SECTION =====
        JPanel truckPanel = new JPanel(new BorderLayout());
        truckPanel.setBorder(BorderFactory.createTitledBorder("Fleet"));

        String[] truckColumns = {
                "Truck ID", "Model", "Status", "Current Issue", "Assigned Employee"
        };

        truckModel = new DefaultTableModel(truckColumns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        truckTable = new JTable(truckModel);
        truckTable.setRowHeight(28);

        truckTable.setDefaultRenderer(Object.class, new TruckStatusRenderer());

        truckPanel.add(new JScrollPane(truckTable), BorderLayout.CENTER);

        centerPanel.add(employeePanel);
        centerPanel.add(truckPanel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton openPortalBtn = new JButton("Open Portal");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        openPortalBtn.addActionListener(e -> openSelectedEmployeePortal());
        refreshBtn.addActionListener(e -> refreshRoster());
        closeBtn.addActionListener(e -> dispose());

        bottomPanel.add(openPortalBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(closeBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        refreshRoster();
    }

    private void refreshRoster() {
        employeeModel.setRowCount(0);
        truckModel.setRowCount(0);

        for (Employee employee : manager.getEmployees()) {
            String assignedTruck = employee.getAssignedTruckId();
            if (assignedTruck == null || assignedTruck.isEmpty()) assignedTruck = "None";

            employeeModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPosition(),
                    employee.getDepartment(),
                    employee.getPhoneNumber(),
                    employee.getEmail(),
                    assignedTruck,
                    employee.isActive() ? "Active" : "Inactive"
            });
        }

        for (Truck truck : manager.getTrucks()) {
            String assignedEmployee = findAssignedEmployeeForTruck(truck.getTruckID());

            truckModel.addRow(new Object[]{
                    truck.getTruckID(),
                    truck.getModel(),
                    truck.isDown() ? "Down / Maintenance" : "Available",
                    truck.getCurrentIssue(),
                    assignedEmployee
            });
        }
    }

    private String findAssignedEmployeeForTruck(String truckId) {
        StringBuilder names = new StringBuilder();

        for (Employee employee : manager.getEmployees()) {
            if (employee.getAssignedTruckId() != null &&
                    employee.getAssignedTruckId().equalsIgnoreCase(truckId)) {

                if (names.length() > 0) {
                    names.append(", ");
                }

                names.append(employee.getFullName());
            }
        }

        if (names.length() == 0) {
            return "Unassigned";
        }

        return names.toString();
    }

    private void openSelectedEmployeePortal() {
        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        int employeeId = Integer.parseInt(employeeTable.getValueAt(selectedRow, 0).toString());
        Employee emp = findEmployeeById(employeeId);

        if (emp == null || !emp.isActive()) return;

        String role = emp.getPosition();

        if (role.equalsIgnoreCase("Driver") || role.equalsIgnoreCase("Foreman")) {
            new EmployeeHomepageUI(manager, emp).setVisible(true);
        } else if (role.equalsIgnoreCase("Mechanic")) {
            new MechanicDashboardUI(manager, emp).setVisible(true);
        } else if (role.equalsIgnoreCase("Owner")) {
            new OwnerPortal(manager).setVisible(true);
        }
    }

    private Employee findEmployeeById(int id) {
        for (Employee e : manager.getEmployees()) {
            if (e.getEmployeeId() == id) return e;
        }
        return null;
    }

    class TruckStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String status = table.getValueAt(row, 2).toString();
            String assigned = table.getValueAt(row, 4).toString();

            if (assigned.contains(",")) {
                c.setBackground(new Color(255, 200, 120)); // ORANGE
            } else if (status.contains("Down")) {
                c.setBackground(new Color(255, 120, 120)); // RED
            } else {
                c.setBackground(new Color(170, 255, 170)); // GREEN
            }

            if (isSelected) {
                c.setBackground(new Color(100, 150, 255));
            }

            return c;
        }
    }
}