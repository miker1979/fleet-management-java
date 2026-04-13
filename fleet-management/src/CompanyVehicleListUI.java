import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CompanyVehicleListUI extends JFrame {

    private FleetManager manager;
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
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleTable = new JTable(tableModel);

        // 🔥 Color coding
        vehicleTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String status = table.getValueAt(row, 4).toString();

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

        // 🔧 Buttons
        JPanel buttonPanel = new JPanel();

        JButton refreshBtn = new JButton("Refresh");
        JButton markInUseBtn = new JButton("Mark In Use");
        JButton markUnusedBtn = new JButton("Mark Unused");
        JButton markDownBtn = new JButton("Mark Down");
        JButton markStoredBtn = new JButton("Send to Storage");
        JButton closeBtn = new JButton("Close");

        refreshBtn.addActionListener(e -> refreshData());
        markInUseBtn.addActionListener(e -> markSelectedVehicleInUse());
        markUnusedBtn.addActionListener(e -> markSelectedVehicleUnused());
        markDownBtn.addActionListener(e -> markSelectedVehicleDown());
        markStoredBtn.addActionListener(e -> markSelectedVehicleStored());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(markInUseBtn);
        buttonPanel.add(markUnusedBtn);
        buttonPanel.add(markDownBtn);
        buttonPanel.add(markStoredBtn);
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
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

        String id = (String) tableModel.getValueAt(row, 0);

        for (Truck t : manager.getTrucks()) {
            if (t.getTruckID().equals(id)) {
                return t;
            }
        }

        return null;
    }

    private void markSelectedVehicleInUse() {
        Truck t = getSelectedTruck();
        if (t == null) return;

        String employee = JOptionPane.showInputDialog(
                this,
                "Enter employee name using this vehicle:"
        );

        if (employee != null && !employee.trim().isEmpty()) {
            t.markInUse(employee);
            refreshData();
        }
    }

    private void markSelectedVehicleUnused() {
        Truck t = getSelectedTruck();
        if (t == null) return;

        t.markUnused();
        refreshData();
    }

    private void markSelectedVehicleDown() {
        Truck t = getSelectedTruck();
        if (t == null) return;

        String issue = JOptionPane.showInputDialog(
                this,
                "Enter issue:"
        );

        if (issue != null) {
            t.setDown(true, issue);
            refreshData();
        }
    }

    private void markSelectedVehicleStored() {
        Truck t = getSelectedTruck();
        if (t == null) return;

        t.markStored();
        refreshData();
    }
}