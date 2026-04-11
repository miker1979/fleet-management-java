import javax.swing.*;
import java.awt.*;

public class JobScreenUI extends JFrame {
    private FleetManager manager;
    private JComboBox<String> typeCombo, contractorCombo, foremanCombo, statusCombo;
    private JTextField locationField, truckField;

    public JobScreenUI(FleetManager manager) {
        this.manager = manager;
        setTitle("Dispatch New Night Shift");
        setSize(400, 500);
        setLayout(new GridLayout(8, 2, 10, 10));
        setLocationRelativeTo(null);

        // Dropdown Data
        String[] types = {"Barrier Install", "Barrier Removal", "Relocation", "Crash Absorber"};
        String[] contractors = {"Pulice", "Sundt", "Kiewit", "Howe Precast", "ADOT"};
        String[] foremen = {"Mike Robinson", "Foreman Joe", "Foreman Steve"};
        String[] statuses = {"Scheduled", "Waiting for Closure", "In Progress", "Completed"};

        typeCombo = new JComboBox<>(types);
        contractorCombo = new JComboBox<>(contractors);
        foremanCombo = new JComboBox<>(foremen);
        statusCombo = new JComboBox<>(statuses);
        locationField = new JTextField();
        truckField = new JTextField("Truck 101, Truck 202"); // Multiple trucks allowed

        add(new JLabel(" Job Type:")); add(typeCombo);
        add(new JLabel(" Contractor:")); add(contractorCombo);
        add(new JLabel(" Location:")); add(locationField);
        add(new JLabel(" Foreman:")); add(foremanCombo);
        add(new JLabel(" Assigned Trucks:")); add(truckField);
        add(new JLabel(" Initial Status:")); add(statusCombo);

        JButton saveBtn = new JButton("DISPATCH JOB");
        saveBtn.addActionListener(e -> saveAction());
        add(new JLabel("")); add(saveBtn);
    }

    private void saveAction() {
        Task newTask = new Task(
            manager.getTasks().size() + 1001,
            "2026-04-10", // You can add a date picker later
            (String)typeCombo.getSelectedItem(),
            (String)contractorCombo.getSelectedItem(),
            locationField.getText(),
            (String)foremanCombo.getSelectedItem(),
            truckField.getText(),
            (String)statusCombo.getSelectedItem()
        );
        manager.addTask(newTask);
        JOptionPane.showMessageDialog(this, "Job Dispatched to Board");
        dispose();
    }
}