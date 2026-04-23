import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockpileManagerUI extends JFrame {

    private final FleetManager manager;
    private JTable table;
    private DefaultTableModel model;

    public StockpileManagerUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Stockpile Manager");
        setSize(1550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
                "Name",
                "Location",
                "20' Count",
                "12' Count",
                "Absorb Sets",
                "20' Connectors",
                "12' Connectors",
                "Forklifts",
                "Forklift IDs",
                "Gradalls",
                "Gradall IDs",
                "Barrier Pieces",
                "Linear Feet",
                "Last Updated By",
                "Last Updated Time"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(26);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton addBtn = new JButton("Add");
        JButton updateCountBtn = new JButton("Update Count");
        JButton addForkliftBtn = new JButton("Add Forklift");
        JButton removeForkliftBtn = new JButton("Remove Forklift");
        JButton addGradallBtn = new JButton("Add Gradall");
        JButton removeGradallBtn = new JButton("Remove Gradall");
        JButton deleteBtn = new JButton("Delete");
        JButton closeBtn = new JButton("Close");

        addBtn.addActionListener(e -> addStockpile());
        updateCountBtn.addActionListener(e -> updateBarrierCount());
        addForkliftBtn.addActionListener(e -> addForklift());
        removeForkliftBtn.addActionListener(e -> removeForklift());
        addGradallBtn.addActionListener(e -> addGradall());
        removeGradallBtn.addActionListener(e -> removeGradall());
        deleteBtn.addActionListener(e -> deleteStockpile());
        closeBtn.addActionListener(e -> dispose());

        buttons.add(addBtn);
        buttons.add(updateCountBtn);
        buttons.add(addForkliftBtn);
        buttons.add(removeForkliftBtn);
        buttons.add(addGradallBtn);
        buttons.add(removeGradallBtn);
        buttons.add(deleteBtn);
        buttons.add(closeBtn);

        add(buttons, BorderLayout.SOUTH);

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);

        for (Stockpile s : manager.getStockpiles()) {
            model.addRow(new Object[]{
                    s.getName(),
                    s.getLocation(),
                    s.getCount20Ft(),
                    s.getCount12Ft(),
                    s.getAbsorbSetCount(),
                    s.getConnector20FtCount(),
                    s.getConnector12FtCount(),
                    s.getForkliftCount(),
                    String.join(", ", s.getForkliftIds()),
                    s.getGradallCount(),
                    String.join(", ", s.getGradallIds()),
                    s.getTotalBarrierPieces(),
                    s.getTotalLinearFeet(),
                    s.getLastUpdatedBy(),
                    s.getLastUpdatedTime()
            });
        }
    }

    private Stockpile getSelectedStockpile() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a stockpile first.");
            return null;
        }

        int modelRow = table.convertRowIndexToModel(row);
        return manager.getStockpiles().get(modelRow);
    }

    private String nowStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void addStockpile() {
        String name = JOptionPane.showInputDialog(this, "Stockpile Name:");
        if (name == null || name.isBlank()) {
            return;
        }

        String location = JOptionPane.showInputDialog(this, "Location:");
        if (location == null) {
            location = "";
        }

        String notes = JOptionPane.showInputDialog(this, "Notes:");
        if (notes == null) {
            notes = "";
        }

        manager.addStockpile(new Stockpile(name.trim(), location.trim(), notes.trim()));
        DataStore.save(manager);
        refresh();
    }

    private void updateBarrierCount() {
        Stockpile selected = getSelectedStockpile();
        if (selected == null) {
            return;
        }

        JTextField count20Field = new JTextField(String.valueOf(selected.getCount20Ft()));
        JTextField count12Field = new JTextField(String.valueOf(selected.getCount12Ft()));
        JTextField absorbSetField = new JTextField(String.valueOf(selected.getAbsorbSetCount()));
        JTextField connector20Field = new JTextField(String.valueOf(selected.getConnector20FtCount()));
        JTextField connector12Field = new JTextField(String.valueOf(selected.getConnector12FtCount()));
        JTextField updatedByField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("20' Barrier Count:"));
        panel.add(count20Field);

        panel.add(new JLabel("12' Barrier Count:"));
        panel.add(count12Field);

        panel.add(new JLabel("Absorb Set Count:"));
        panel.add(absorbSetField);

        panel.add(new JLabel("20' Connector Count:"));
        panel.add(connector20Field);

        panel.add(new JLabel("12' Connector Count:"));
        panel.add(connector12Field);

        panel.add(new JLabel("Updated By:"));
        panel.add(updatedByField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Stockpile Counts",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int count20 = Integer.parseInt(count20Field.getText().trim());
            int count12 = Integer.parseInt(count12Field.getText().trim());
            int absorbSets = Integer.parseInt(absorbSetField.getText().trim());
            int connector20 = Integer.parseInt(connector20Field.getText().trim());
            int connector12 = Integer.parseInt(connector12Field.getText().trim());

            if (count20 < 0 || count12 < 0 || absorbSets < 0 || connector20 < 0 || connector12 < 0) {
                JOptionPane.showMessageDialog(this, "Counts cannot be negative.");
                return;
            }

            String updatedBy = updatedByField.getText().trim();
            if (updatedBy.isBlank()) {
                updatedBy = "Unknown";
            }

            boolean updated = selected.updateCounts(
                    count20,
                    count12,
                    absorbSets,
                    connector20,
                    connector12,
                    updatedBy,
                    nowStamp()
            );

            if (!updated) {
                JOptionPane.showMessageDialog(this, "Unable to update stockpile.");
                return;
            }

            DataStore.save(manager);
            refresh();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid whole numbers only.");
        }
    }

    private void addForklift() {
        Stockpile selected = getSelectedStockpile();
        if (selected == null) {
            return;
        }

        String unitId = JOptionPane.showInputDialog(this, "Forklift Unit ID:");
        if (unitId == null || unitId.isBlank()) {
            return;
        }

        String updatedBy = JOptionPane.showInputDialog(this, "Updated By:");
        if (updatedBy == null || updatedBy.isBlank()) {
            updatedBy = "Unknown";
        }

        boolean success = manager.addForkliftToStockpile(
                selected.getName(),
                unitId.trim(),
                updatedBy,
                nowStamp()
        );

        if (!success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add forklift.\nMake sure the unit exists in Equipment and is not already at another stockpile."
            );
            return;
        }

        DataStore.save(manager);
        refresh();
    }

    private void removeForklift() {
        Stockpile selected = getSelectedStockpile();
        if (selected == null) {
            return;
        }

        if (selected.getForkliftIds().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No forklifts at this stockpile.");
            return;
        }

        Object choice = JOptionPane.showInputDialog(
                this,
                "Select forklift to remove:",
                "Remove Forklift",
                JOptionPane.PLAIN_MESSAGE,
                null,
                selected.getForkliftIds().toArray(),
                selected.getForkliftIds().get(0)
        );

        if (choice == null) {
            return;
        }

        String updatedBy = JOptionPane.showInputDialog(this, "Updated By:");
        if (updatedBy == null || updatedBy.isBlank()) {
            updatedBy = "Unknown";
        }

        boolean success = manager.removeForkliftFromStockpile(
                selected.getName(),
                choice.toString(),
                updatedBy,
                nowStamp()
        );

        if (!success) {
            JOptionPane.showMessageDialog(this, "Unable to remove forklift.");
            return;
        }

        DataStore.save(manager);
        refresh();
    }

    private void addGradall() {
        Stockpile selected = getSelectedStockpile();
        if (selected == null) {
            return;
        }

        String unitId = JOptionPane.showInputDialog(this, "Gradall Unit ID:");
        if (unitId == null || unitId.isBlank()) {
            return;
        }

        String updatedBy = JOptionPane.showInputDialog(this, "Updated By:");
        if (updatedBy == null || updatedBy.isBlank()) {
            updatedBy = "Unknown";
        }

        boolean success = manager.addGradallToStockpile(
                selected.getName(),
                unitId.trim(),
                updatedBy,
                nowStamp()
        );

        if (!success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add gradall.\nMake sure the unit exists in Equipment and is not already at another stockpile."
            );
            return;
        }

        DataStore.save(manager);
        refresh();
    }

    private void removeGradall() {
        Stockpile selected = getSelectedStockpile();
        if (selected == null) {
            return;
        }

        if (selected.getGradallIds().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No gradalls at this stockpile.");
            return;
        }

        Object choice = JOptionPane.showInputDialog(
                this,
                "Select gradall to remove:",
                "Remove Gradall",
                JOptionPane.PLAIN_MESSAGE,
                null,
                selected.getGradallIds().toArray(),
                selected.getGradallIds().get(0)
        );

        if (choice == null) {
            return;
        }

        String updatedBy = JOptionPane.showInputDialog(this, "Updated By:");
        if (updatedBy == null || updatedBy.isBlank()) {
            updatedBy = "Unknown";
        }

        boolean success = manager.removeGradallFromStockpile(
                selected.getName(),
                choice.toString(),
                updatedBy,
                nowStamp()
        );

        if (!success) {
            JOptionPane.showMessageDialog(this, "Unable to remove gradall.");
            return;
        }

        DataStore.save(manager);
        refresh();
    }

    private void deleteStockpile() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected stockpile?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        manager.getStockpiles().remove(modelRow);

        DataStore.save(manager);
        refresh();
    }
}