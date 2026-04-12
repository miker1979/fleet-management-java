import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CompanyRosterUI extends JFrame {

    private FleetManager manager;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public CompanyRosterUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Company Roster");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Company Roster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

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
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(32);
        employeeTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        employeeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));

        JButton refreshBtn = new JButton("Refresh");
        JButton editBtn = new JButton("Edit Employee");
        JButton deleteBtn = new JButton("Delete Employee");
        JButton openPortalBtn = new JButton("Open Portal");
        JButton closeBtn = new JButton("Close");

        refreshBtn.addActionListener(e -> refreshData());

        editBtn.addActionListener(e -> editSelectedEmployee());

        deleteBtn.addActionListener(e -> deleteSelectedEmployee());

        openPortalBtn.addActionListener(e -> openSelectedEmployeePortal());

        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(openPortalBtn);
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        for (Employee employee : manager.getEmployees()) {
            String status = employee.isActive() ? "Active" : "Inactive";
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
        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.");
            return null;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        return manager.findEmployeeById(employeeId);
    }

    private void editSelectedEmployee() {
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            return;
        }

        EditEmployeeUI editUI = new EditEmployeeUI(manager, employee);
        editUI.setVisible(true);

        editUI.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refreshData();
            }
        });
    }

    private void deleteSelectedEmployee() {
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            return;
        }

        if (isEmployeeAssignedToActiveTask(employee)) {
            JOptionPane.showMessageDialog(
                    this,
                    "This employee is tied to an active task and cannot be deleted right now."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete " + employee.getFullName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (employee.getAssignedTruckId() != null && !employee.getAssignedTruckId().trim().isEmpty()) {
            employee.setAssignedTruckId("");
        }

        manager.getEmployees().remove(employee);
        refreshData();

        JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
    }

    private boolean isEmployeeAssignedToActiveTask(Employee employee) {
        for (Task task : manager.getTasks()) {
            if (task.getForeman() != null
                    && task.getForeman().equalsIgnoreCase(employee.getFullName())
                    && task.getStatus() != null
                    && !task.getStatus().equalsIgnoreCase("Completed")
                    && !task.getStatus().equalsIgnoreCase("Canceled")) {
                return true;
            }
        }
        return false;
    }

    private void openSelectedEmployeePortal() {
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            return;
        }

        String position = employee.getPosition() == null ? "" : employee.getPosition().trim().toLowerCase();

        if (position.equals("mechanic")) {
            new MechanicDashboardUI(manager, employee).setVisible(true);
            return;
        }

        new EmployeeHomepageUI(manager, employee).setVisible(true);
    }
}