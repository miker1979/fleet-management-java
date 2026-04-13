import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CompanyRosterUI extends JFrame {

    private FleetManager manager;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public CompanyRosterUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Company Roster");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Company Roster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 🔥 FILTER DROPDOWN
        filterCombo = new JComboBox<>(new String[]{
                "Show All",
                "Active Only",
                "Inactive Only"
        });

        filterCombo.addActionListener(e -> refreshData());

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter: "));
        filterPanel.add(filterCombo);

        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "ID",
                "Name",
                "Position",
                "Department",
                "Phone",
                "Email",
                "Status",
                "Assigned Truck"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);

        // 🔥 COLOR INACTIVE ROWS
        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String status = table.getValueAt(row, 6).toString();

                if (status.equalsIgnoreCase("Inactive")) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        employeeTable.setRowHeight(32);
        employeeTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        employeeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton editBtn = new JButton("Edit Employee");
        JButton deleteBtn = new JButton("Delete Employee");
        JButton openPortalBtn = new JButton("Open Portal");
        JButton closeBtn = new JButton("Close");

        editBtn.addActionListener(e -> editSelectedEmployee());
        deleteBtn.addActionListener(e -> deleteSelectedEmployee());
        openPortalBtn.addActionListener(e -> openSelectedEmployeePortal());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(openPortalBtn);
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent e) {
                refreshData();
            }
        });

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        String filter = (String) filterCombo.getSelectedItem();

        for (Employee employee : manager.getEmployees()) {

            boolean isActive = employee.isActive();

            // 🔥 FILTER LOGIC
            if (filter.equals("Active Only") && !isActive) continue;
            if (filter.equals("Inactive Only") && isActive) continue;

            String status = isActive ? "Active" : "Inactive";
            String assignedTruck = employee.getAssignedTruckId();

            if (assignedTruck == null || assignedTruck.trim().isEmpty()) {
                assignedTruck = "None";
            }

            tableModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPosition(),
                    employee.getDepartment(),
                    employee.getPhoneNumber(),
                    employee.getEmail(),
                    status,
                    assignedTruck
            });
        }
    }

    private Employee getSelectedEmployee() {
        int row = employeeTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return null;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        return manager.findEmployeeById(id);
    }

    private void editSelectedEmployee() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        new EditEmployeeUI(manager, emp).setVisible(true);
    }

    private void deleteSelectedEmployee() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        if (isEmployeeAssignedToActiveTask(emp)) {
            JOptionPane.showMessageDialog(this,
                    "Employee is tied to an active task.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete " + emp.getFullName() + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        manager.getEmployees().remove(emp);
        refreshData();
    }

    private boolean isEmployeeAssignedToActiveTask(Employee emp) {
        for (Task t : manager.getTasks()) {
            if (t.getForeman() != null &&
                    t.getForeman().equalsIgnoreCase(emp.getFullName()) &&
                    !t.getStatus().equalsIgnoreCase("Completed") &&
                    !t.getStatus().equalsIgnoreCase("Canceled")) {
                return true;
            }
        }
        return false;
    }

    private void openSelectedEmployeePortal() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        // 🔥 BLOCK INACTIVE EMPLOYEES
        if (!emp.isActive()) {
            JOptionPane.showMessageDialog(this,
                    "Inactive employees cannot access the portal.");
            return;
        }

        if ("mechanic".equalsIgnoreCase(emp.getPosition())) {
            new MechanicDashboardUI(manager, emp).setVisible(true);
        } else {
            new EmployeeHomepageUI(manager, emp).setVisible(true);
        }
    }
}