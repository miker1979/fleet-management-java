import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockpileManagerUI extends JFrame {

    private FleetManager manager;
    private JTable table;
    private DefaultTableModel model;

    public StockpileManagerUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Stockpile Manager");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
                "Name", "Location", "Barriers", "Last Updated"
        }, 0);

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete");
        JButton closeBtn = new JButton("Close");

        addBtn.addActionListener(e -> addStockpile());
        deleteBtn.addActionListener(e -> deleteStockpile());
        closeBtn.addActionListener(e -> dispose());

        buttons.add(addBtn);
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
                    s.getBarrierCount(),
                    s.getLastUpdatedTime()
            });
        }
    }

    private void addStockpile() {
        String name = JOptionPane.showInputDialog(this, "Stockpile Name:");
        String location = JOptionPane.showInputDialog(this, "Location:");
        String notes = JOptionPane.showInputDialog(this, "Notes:");

        if (name == null || name.isBlank()) return;

        manager.addStockpile(new Stockpile(name, location, notes));
        DataStore.save(manager);
        refresh();
    }

    private void deleteStockpile() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        manager.getStockpiles().remove(row);
        DataStore.save(manager);
        refresh();
    }
}