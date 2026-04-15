import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyVehicleListUI extends JFrame {

    private final FleetManager manager;
    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    public CompanyVehicleListUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Company Vehicle List");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Company Vehicle List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
                "Unit ID",
                "Year",
                "Make",
                "Model",
                "Status",
                "Assigned To",
                "Issue"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleTable = new JTable(tableModel);

        vehicleTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    return c;
                }

                String status = String.valueOf(table.getValueAt(row, 4));

                switch (status) {
                    case "Down":
                        c.setForeground(Color.RED);
                        break;
                    case "Out of Service":
                        c.setForeground(Color.DARK_GRAY);
                        break;
                    case "Stored":
                        c.setForeground(new Color(120, 120, 120));
                        break;
                    case "In Use":
                        c.setForeground(new Color(0, 128, 0));
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        vehicleTable.setRowHeight(30);
        vehicleTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        vehicleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton refreshBtn = new JButton("Refresh");
        JButton assignBtn = new JButton("Assign Driver");
        JButton markUnusedBtn = new JButton("Mark Unused");
        JButton markDownBtn = new JButton("Mark Down");
        JButton markStoredBtn = new JButton("Send to Storage");
        JButton closeBtn = new JButton("Close");

        refreshBtn.addActionListener(e -> refreshData());
        assignBtn.addActionListener(e -> assignSelectedVehicle());
        markUnusedBtn.addActionListener(e -> markSelectedVehicleUnused());
        markDownBtn.addActionListener(e -> markSelectedVehicleDown());
        markStoredBtn.addActionListener(e -> markSelectedVehicleStored());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(assignBtn);
        buttonPanel.add(markUnusedBtn);
        buttonPanel.add(markDownBtn);
        buttonPanel.add(markStoredBtn);
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

    private void refreshData() {
        tableModel.setRowCount(0);

        for (Truck t : manager.getTrucks()) {
            String assigned = t.getAssignedEmployeeName();
            if (assigned == null || assigned.isEmpty()) {
                assigned = "None";
            }

            tableModel.addRow(new Object[]{
                    t.getTruckID(),
                    t.getYear(),
                    t.getMake(),
                    t.getModel(),
                    t.getStatus(),
                    assigned,
                    t.getCurrentIssue()
            });
        }
    }

    private Truck getSelectedTruck() {
        int row = vehicleTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle first.");
            return null;
        }

        String id = String.valueOf(tableModel.getValueAt(row, 0));

        for (Truck t : manager.getTrucks()) {
            if (t.getTruckID().equals(id)) {
                return t;
            }
        }

        return null;
    }

    private void assignSelectedVehicle() {
        Truck t = getSelectedTruck();
        if (t == null) {
            return;
        }

        List<Employee> drivers = new ArrayList<>();
        for (Employee e : manager.getEmployees()) {
            String position = e.getPosition();
            if (position != null && position.toLowerCase().contains("driver")) {
                drivers.add(e);
            }
        }

        if (drivers.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No drivers found. Create a driver first.",
                    "No Drivers",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object selection = JOptionPane.showInputDialog(
                this,
                "Select Driver:",
                "Assign Driver",
                JOptionPane.PLAIN_MESSAGE,
                null,
                drivers.toArray(),
                drivers.get(0)
        );

        if (!(selection instanceof Employee)) {
            return;
        }

        Employee employee = (Employee) selection;
        String fullName = employee.getFirstName() + " " + employee.getLastName();

        t.markInUse(fullName);

        if (employee.getAssignedTruckId() == null || employee.getAssignedTruckId().isEmpty()) {
            employee.setAssignedTruckId(t.getTruckID());
        } else {
            employee.setAssignedTruckId(t.getTruckID());
        }

        refreshData();
    }

    private void markSelectedVehicleUnused() {
        Truck t = getSelectedTruck();
        if (t == null) {
            return;
        }

        clearEmployeeAssignment(t.getTruckID());
        t.markUnused();
        refreshData();
    }

    private void markSelectedVehicleDown() {
        Truck t = getSelectedTruck();
        if (t == null) {
            return;
        }

        String issue = JOptionPane.showInputDialog(this, "Enter issue:");

        if (issue != null) {
            clearEmployeeAssignment(t.getTruckID());
            t.setDown(true, issue);
            refreshData();
        }
    }

    private void markSelectedVehicleStored() {
        Truck t = getSelectedTruck();
        if (t == null) {
            return;
        }

        clearEmployeeAssignment(t.getTruckID());
        t.markStored();
        refreshData();
    }

    private void clearEmployeeAssignment(String truckId) {
        for (Employee e : manager.getEmployees()) {
            String assignedTruckId = e.getAssignedTruckId();
            if (assignedTruckId != null && assignedTruckId.equals(truckId)) {
                e.setAssignedTruckId("");
            }
        }
    }
}