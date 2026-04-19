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
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Company Roster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.CENTER);

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
                "Assigned Truck",
                "Assigned Trailer"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);

        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String status = table.getValueAt(row, 6).toString();

                if (!isSelected) {
                    if (status.equalsIgnoreCase("Inactive")) {
                        c.setForeground(Color.GRAY);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
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

        JButton assignBtn = new JButton("Assign Equipment");
        JButton clearBtn = new JButton("Clear Equipment");
        JButton editBtn = new JButton("Edit Employee");
        JButton deleteBtn = new JButton("Delete Employee");
        JButton openPortalBtn = new JButton("Open Portal");
        JButton closeBtn = new JButton("Close");

        assignBtn.addActionListener(e -> assignEquipment());
        clearBtn.addActionListener(e -> clearEquipment());
        editBtn.addActionListener(e -> editSelectedEmployee());
        deleteBtn.addActionListener(e -> deleteSelectedEmployee());
        openPortalBtn.addActionListener(e -> openSelectedEmployeePortal());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(openPortalBtn);
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
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

            if ("Active Only".equals(filter) && !isActive) continue;
            if ("Inactive Only".equals(filter) && isActive) continue;

            String status = isActive ? "Active" : "Inactive";

            String assignedTruck = safe(employee.getAssignedTruckId());
            if (assignedTruck.isEmpty()) {
                assignedTruck = "None";
            }

            String assignedTrailer = safe(employee.getAssignedTrailerId());
            if (assignedTrailer.isEmpty()) {
                assignedTrailer = "None";
            }

            tableModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPosition(),
                    employee.getDepartment(),
                    employee.getPhoneNumber(),
                    employee.getEmail(),
                    status,
                    assignedTruck,
                    assignedTrailer
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

    private void assignEquipment() {
        try {
            Employee emp = getSelectedEmployee();
            if (emp == null) return;

            if (!emp.isActive()) {
                JOptionPane.showMessageDialog(this, "Cannot assign equipment to an inactive employee.");
                return;
            }

            JComboBox<String> truckBox = new JComboBox<>();
            JComboBox<String> trailerBox = new JComboBox<>();

            truckBox.addItem("None");
            for (Truck truck : manager.getTrucks()) {
                String truckId = safe(truck.getTruckID());
                if (!truckId.isEmpty()) {
                    truckBox.addItem(truckId);
                }
            }

            trailerBox.addItem("None");
            for (Trailer trailer : manager.getTrailers()) {
                String trailerId = safe(trailer.getTrailerId());
                if (!trailerId.isEmpty()) {
                    trailerBox.addItem(trailerId);
                }
            }

            if (!safe(emp.getAssignedTruckId()).isEmpty()) {
                truckBox.setSelectedItem(emp.getAssignedTruckId());
            }

            if (!safe(emp.getAssignedTrailerId()).isEmpty()) {
                trailerBox.setSelectedItem(emp.getAssignedTrailerId());
            }

            JPanel panel = new JPanel(new GridLayout(4, 1, 8, 8));
            panel.add(new JLabel("Assign Truck:"));
            panel.add(truckBox);
            panel.add(new JLabel("Assign Trailer:"));
            panel.add(trailerBox);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Assign Equipment to " + emp.getFullName(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String selectedTruck = safe((String) truckBox.getSelectedItem());
            String selectedTrailer = safe((String) trailerBox.getSelectedItem());

            if ("None".equalsIgnoreCase(selectedTruck)) {
                selectedTruck = "";
            }

            if ("None".equalsIgnoreCase(selectedTrailer)) {
                selectedTrailer = "";
            }

            for (Employee other : manager.getEmployees()) {
                if (other == emp) continue;

                if (!selectedTruck.isEmpty() &&
                        selectedTruck.equalsIgnoreCase(safe(other.getAssignedTruckId()))) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Truck " + selectedTruck + " is already assigned to " + other.getFullName() + "."
                    );
                    return;
                }

                if (!selectedTrailer.isEmpty() &&
                        selectedTrailer.equalsIgnoreCase(safe(other.getAssignedTrailerId()))) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Trailer " + selectedTrailer + " is already assigned to " + other.getFullName() + "."
                    );
                    return;
                }
            }

            emp.setAssignedTruckId(selectedTruck);
            emp.setAssignedTrailerId(selectedTrailer);

            DataStore.save(manager);
            refreshData();

            JOptionPane.showMessageDialog(this, "Equipment assignment saved.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Assign Equipment failed: " + ex.getClass().getSimpleName() + " - " + ex.getMessage()
            );
        }
    }

    private void clearEquipment() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Clear truck and trailer assignment for " + emp.getFullName() + "?",
                "Clear Equipment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        emp.setAssignedTruckId("");
        emp.setAssignedTrailerId("");

        DataStore.save(manager);
        refreshData();
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
            JOptionPane.showMessageDialog(this, "Employee is tied to an active task.");
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
        DataStore.save(manager);
        refreshData();
    }

    private boolean isEmployeeAssignedToActiveTask(Employee emp) {
        for (Task t : manager.getTasks()) {
            if (t.getAssignedEmployeeIds() != null &&
                    t.getAssignedEmployeeIds().contains(emp.getEmployeeId()) &&
                    !safe(t.getStatus()).equalsIgnoreCase("Completed") &&
                    !safe(t.getStatus()).equalsIgnoreCase("Canceled")) {
                return true;
            }
        }
        return false;
    }

    private void openSelectedEmployeePortal() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        if (!emp.isActive()) {
            JOptionPane.showMessageDialog(this, "Inactive employees cannot access the portal.");
            return;
        }

        if ("mechanic".equalsIgnoreCase(emp.getPosition())) {
            new MechanicDashboardUI(manager, emp).setVisible(true);
        } else {
            new EmployeeHomepageUI(manager, emp).setVisible(true);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}