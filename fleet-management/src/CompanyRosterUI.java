import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CompanyRosterUI extends JFrame {

    private FleetManager manager;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public CompanyRosterUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Company Roster");
        setSize(1250, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 🔥 BIGGER TITLE
        JLabel titleLabel = new JLabel("Company Roster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 🔥 BIGGER FILTER
        filterCombo = new JComboBox<>(new String[]{
                "Show All",
                "Active Only",
                "Inactive Only"
        });
        filterCombo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        filterCombo.addActionListener(e -> refreshData());

        JPanel filterPanel = new JPanel();

        JLabel filterLabel = new JLabel("Filter: ");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        filterPanel.add(filterLabel);
        filterPanel.add(filterCombo);

        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Name", "Position", "Department",
                "Phone", "Email", "Status",
                "Truck", "Trailer"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setAutoCreateRowSorter(true);

        // 🔥 BIGGER TABLE TEXT
        employeeTable.setRowHeight(42);
        employeeTable.setFont(new Font("SansSerif", Font.PLAIN, 18));
        employeeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 17));

        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                int modelRow = table.convertRowIndexToModel(row);
                String status = tableModel.getValueAt(modelRow, 6).toString();

                if (!isSelected) {
                    c.setForeground(status.equalsIgnoreCase("Inactive") ? Color.GRAY : Color.BLACK);
                }
                return c;
            }
        });

        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton assignBtn = new JButton("Assign Truck");
        JButton clearBtn = new JButton("Clear Equipment");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton portalBtn = new JButton("Open Portal");
        JButton closeBtn = new JButton("Close");

        // 🔥 BIGGER BUTTON TEXT
        Font buttonFont = new Font("SansSerif", Font.BOLD, 15);
        assignBtn.setFont(buttonFont);
        clearBtn.setFont(buttonFont);
        editBtn.setFont(buttonFont);
        deleteBtn.setFont(buttonFont);
        portalBtn.setFont(buttonFont);
        closeBtn.setFont(buttonFont);

        assignBtn.addActionListener(e -> openAssignTruckUI());
        clearBtn.addActionListener(e -> clearEquipment());
        editBtn.addActionListener(e -> editEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
        portalBtn.addActionListener(e -> openPortal());
        closeBtn.addActionListener(e -> dispose());

        buttons.add(assignBtn);
        buttons.add(clearBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(portalBtn);
        buttons.add(closeBtn);

        add(buttons, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        String filter = (String) filterCombo.getSelectedItem();

        for (Employee e : manager.getEmployees()) {
            boolean active = e.isActive();

            if ("Active Only".equals(filter) && !active) continue;
            if ("Inactive Only".equals(filter) && active) continue;

            tableModel.addRow(new Object[]{
                    e.getEmployeeId(),
                    e.getFullName(),
                    e.getPosition(),
                    e.getDepartment(),
                    e.getPhoneNumber(),
                    e.getEmail(),
                    active ? "Active" : "Inactive",
                    safe(e.getAssignedTruckId(), "None"),
                    safe(e.getAssignedTrailerId(), "None")
            });
        }
    }

    private Employee getSelectedEmployee() {
        int viewRow = employeeTable.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee.");
            return null;
        }

        int row = employeeTable.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(row, 0);

        return manager.findEmployeeById(id);
    }

    private void openAssignTruckUI() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        if (!emp.isActive()) {
            JOptionPane.showMessageDialog(this, "Inactive employee.");
            return;
        }

        new AssignTruckUI(manager, this).setVisible(true);
    }

    private void clearEquipment() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        emp.setAssignedTruckId("");
        emp.setAssignedTrailerId("");

        DataStore.save(manager);
        refreshData();
    }

    private void editEmployee() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        new EditEmployeeUI(manager, emp).setVisible(true);
    }

    private void deleteEmployee() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        for (Task t : manager.getTasks()) {
            if (t.getAssignedEmployeeIds() != null &&
                t.getAssignedEmployeeIds().contains(emp.getEmployeeId()) &&
                !"Completed".equalsIgnoreCase(t.getStatus())) {

                JOptionPane.showMessageDialog(this, "Employee is on active task.");
                return;
            }
        }

        manager.getEmployees().remove(emp);
        DataStore.save(manager);
        refreshData();
    }

    private void openPortal() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        if (!emp.isActive()) {
            JOptionPane.showMessageDialog(this, "Inactive employee.");
            return;
        }

        if ("mechanic".equalsIgnoreCase(emp.getPosition())) {
            new MechanicDashboardUI(manager, emp).setVisible(true);
        } else {
            new EmployeeHomepageUI(manager, emp).setVisible(true);
        }
    }

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}