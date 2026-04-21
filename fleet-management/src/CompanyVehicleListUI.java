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
                "Type", "Unit ID", "Year", "Make", "Model",
                "Subtype", "Length", "Status", "Assigned To", "Issue"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        equipmentTable = new JTable(tableModel);
        equipmentTable.setAutoCreateRowSorter(true);

        equipmentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                int modelRow = table.convertRowIndexToModel(row);
                String status = String.valueOf(tableModel.getValueAt(modelRow, 7));

                if (!isSelected) {
                    switch (status) {
                        case "Down" -> c.setForeground(Color.RED);
                        case "Out of Service" -> c.setForeground(Color.DARK_GRAY);
                        case "Stored" -> c.setForeground(Color.GRAY);
                        case "In Use" -> c.setForeground(new Color(0, 140, 0));
                        default -> c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton refreshBtn = new JButton("Refresh");
        JButton editBtn = new JButton("Edit Vehicle");
        JButton inUseBtn = new JButton("Mark In Use");
        JButton unusedBtn = new JButton("Mark Unused");
        JButton downBtn = new JButton("Mark Down");
        JButton storedBtn = new JButton("Store");
        JButton closeBtn = new JButton("Close");

        refreshBtn.addActionListener(e -> refreshData());
        editBtn.addActionListener(e -> editVehicle());
        inUseBtn.addActionListener(e -> markInUse());
        unusedBtn.addActionListener(e -> markUnused());
        downBtn.addActionListener(e -> markDown());
        storedBtn.addActionListener(e -> markStored());
        closeBtn.addActionListener(e -> dispose());

        buttons.add(refreshBtn);
        buttons.add(editBtn);
        buttons.add(inUseBtn);
        buttons.add(unusedBtn);
        buttons.add(downBtn);
        buttons.add(storedBtn);
        buttons.add(closeBtn);

        add(buttons, BorderLayout.SOUTH);

        refreshData();
    }

    private void refreshData() {
        tableModel.setRowCount(0);

        for (Truck t : manager.getTrucks()) {
            tableModel.addRow(new Object[]{
                    "Truck",
                    t.getTruckID(),
                    t.getYear(),
                    t.getMake(),
                    t.getModel(),
                    "",
                    "",
                    t.getStatus(),
                    safe(t.getAssignedEmployeeName(), "None"),
                    t.getCurrentIssue()
            });
        }

        for (Trailer t : manager.getTrailers()) {
            tableModel.addRow(new Object[]{
                    "Trailer",
                    t.getTrailerId(),
                    t.getYear(),
                    t.getMake(),
                    t.getModel(),
                    t.getTrailerType(),
                    t.getTrailerLength(),
                    t.getStatus(),
                    safe(t.getAssignedEmployeeName(), "None"),
                    t.getCurrentIssue()
            });
        }
    }

    private int getSelectedRow() {
        int viewRow = equipmentTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select equipment first.");
            return -1;
        }
        return equipmentTable.convertRowIndexToModel(viewRow);
    }

    private void markInUse() {
        int row = getSelectedRow();
        if (row == -1) return;

        String type = (String) tableModel.getValueAt(row, 0);
        String id = (String) tableModel.getValueAt(row, 1);

        Employee emp = (Employee) JOptionPane.showInputDialog(
                this,
                "Select employee:",
                "Assign Equipment",
                JOptionPane.PLAIN_MESSAGE,
                null,
                manager.getEmployees().toArray(),
                null
        );

        if (emp == null) return;

        if (type.equals("Truck")) {
            Truck t = manager.findTruckById(id);
            if (t != null) t.markInUse(emp.getFullName());
        } else {
            Trailer t = manager.findTrailerById(id);
            if (t != null) t.markInUse(emp.getFullName());
        }

        DataStore.save(manager);
        refreshData();
    }

    private void markUnused() {
        int row = getSelectedRow();
        if (row == -1) return;

        String type = (String) tableModel.getValueAt(row, 0);
        String id = (String) tableModel.getValueAt(row, 1);

        if (type.equals("Truck")) {
            Truck t = manager.findTruckById(id);
            if (t != null) t.markUnused();
        } else {
            Trailer t = manager.findTrailerById(id);
            if (t != null) t.markUnused();
        }

        DataStore.save(manager);
        refreshData();
    }

    private void markDown() {
        int row = getSelectedRow();
        if (row == -1) return;

        String issue = JOptionPane.showInputDialog(this, "Enter issue:");
        if (issue == null) return;

        String type = (String) tableModel.getValueAt(row, 0);
        String id = (String) tableModel.getValueAt(row, 1);

        if (type.equals("Truck")) {
            Truck t = manager.findTruckById(id);
            if (t != null) t.setDown(true, issue);
        } else {
            Trailer t = manager.findTrailerById(id);
            if (t != null) t.setDown(true, issue);
        }

        DataStore.save(manager);
        refreshData();
    }

    private void markStored() {
        int row = getSelectedRow();
        if (row == -1) return;

        String type = (String) tableModel.getValueAt(row, 0);
        String id = (String) tableModel.getValueAt(row, 1);

        if (type.equals("Truck")) {
            Truck t = manager.findTruckById(id);
            if (t != null) t.markStored();
        } else {
            Trailer t = manager.findTrailerById(id);
            if (t != null) t.markStored();
        }

        DataStore.save(manager);
        refreshData();
    }

    // 🔥 NEW (from your change list)
    private void editVehicle() {
        int row = getSelectedRow();
        if (row == -1) return;

        String type = (String) tableModel.getValueAt(row, 0);
        String id = (String) tableModel.getValueAt(row, 1);

        JOptionPane.showMessageDialog(
                this,
                "Edit Vehicle UI coming next (we’ll build this clean)."
        );
    }

    private String safe(String v, String fallback) {
        return (v == null || v.isBlank()) ? fallback : v;
    }
}