import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CompanyVehicleListUI extends JFrame {

    private final FleetManager manager;
    private JTable equipmentTable;
    private DefaultTableModel tableModel;

    public CompanyVehicleListUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Company Equipment List");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Company Equipment List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
                "Equipment Type",
                "Unit ID",
                "Year",
                "Make",
                "Model",
                "Subtype",
                "Length",
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

        equipmentTable = new JTable(tableModel);

        equipmentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    return c;
                }

                String status = String.valueOf(table.getValueAt(row, 7));

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

        equipmentTable.setRowHeight(30);
        equipmentTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        equipmentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton refreshBtn = new JButton("Refresh");
        JButton markInUseBtn = new JButton("Mark In Use");
        JButton markUnusedBtn = new JButton("Mark Unused");
        JButton markDownBtn = new JButton("Mark Down");
        JButton markStoredBtn = new JButton("Send to Storage");
        JButton closeBtn = new JButton("Close");

        refreshBtn.addActionListener(e -> refreshData());
        markInUseBtn.addActionListener(e -> markSelectedEquipmentInUse());
        markUnusedBtn.addActionListener(e -> markSelectedEquipmentUnused());
        markDownBtn.addActionListener(e -> markSelectedEquipmentDown());
        markStoredBtn.addActionListener(e -> markSelectedEquipmentStored());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(markInUseBtn);
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
                    "Truck",
                    t.getTruckID(),
                    t.getYear(),
                    t.getMake(),
                    t.getModel(),
                    "",
                    "",
                    t.getStatus(),
                    assigned,
                    t.getCurrentIssue()
            });
        }

        for (Trailer t : manager.getTrailers()) {
            String assigned = t.getAssignedEmployeeName();
            if (assigned == null || assigned.isEmpty()) {
                assigned = "None";
            }

            tableModel.addRow(new Object[]{
                    "Trailer",
                    t.getTrailerId(),
                    t.getYear(),
                    t.getMake(),
                    t.getModel(),
                    t.getTrailerType(),
                    t.getTrailerLength(),
                    t.getStatus(),
                    assigned,
                    t.getCurrentIssue()
            });
        }
    }

    private String getSelectedEquipmentType() {
        int row = equipmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select equipment first.");
            return null;
        }

        return String.valueOf(tableModel.getValueAt(row, 0));
    }

    private String getSelectedEquipmentId() {
        int row = equipmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select equipment first.");
            return null;
        }

        return String.valueOf(tableModel.getValueAt(row, 1));
    }

    private void markSelectedEquipmentInUse() {
        String type = getSelectedEquipmentType();
        String id = getSelectedEquipmentId();
        if (type == null || id == null) {
            return;
        }

        String employee = JOptionPane.showInputDialog(
                this,
                "Enter employee name using this equipment:"
        );

        if (employee == null || employee.trim().isEmpty()) {
            return;
        }

        if (type.equals("Truck")) {
            Truck truck = manager.findTruckById(id);
            if (truck != null) {
                truck.markInUse(employee.trim());
            }
        } else {
            Trailer trailer = manager.findTrailerById(id);
            if (trailer != null) {
                trailer.markInUse(employee.trim());
            }
        }

        refreshData();
    }

    private void markSelectedEquipmentUnused() {
        String type = getSelectedEquipmentType();
        String id = getSelectedEquipmentId();
        if (type == null || id == null) {
            return;
        }

        if (type.equals("Truck")) {
            Truck truck = manager.findTruckById(id);
            if (truck != null) {
                truck.markUnused();
            }
        } else {
            Trailer trailer = manager.findTrailerById(id);
            if (trailer != null) {
                trailer.markUnused();
            }
        }

        refreshData();
    }

    private void markSelectedEquipmentDown() {
        String type = getSelectedEquipmentType();
        String id = getSelectedEquipmentId();
        if (type == null || id == null) {
            return;
        }

        String issue = JOptionPane.showInputDialog(this, "Enter issue:");
        if (issue == null) {
            return;
        }

        if (type.equals("Truck")) {
            Truck truck = manager.findTruckById(id);
            if (truck != null) {
                truck.setDown(true, issue);
            }
        } else {
            Trailer trailer = manager.findTrailerById(id);
            if (trailer != null) {
                trailer.setDown(true, issue);
            }
        }

        refreshData();
    }

    private void markSelectedEquipmentStored() {
        String type = getSelectedEquipmentType();
        String id = getSelectedEquipmentId();
        if (type == null || id == null) {
            return;
        }

        if (type.equals("Truck")) {
            Truck truck = manager.findTruckById(id);
            if (truck != null) {
                truck.markStored();
            }
        } else {
            Trailer trailer = manager.findTrailerById(id);
            if (trailer != null) {
                trailer.markStored();
            }
        }

        refreshData();
    }
}