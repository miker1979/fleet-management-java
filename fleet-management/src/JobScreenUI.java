import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JobScreenUI extends JFrame {

    private FleetManager manager;

    private JTextField contractorField;
    private JTextField locationField;
    private JTextField taskNameField;
    private JComboBox<String> statusCombo;
    private JTextField linearFeetField;
    private JComboBox<Truck> truckCombo;

    public JobScreenUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create New Job");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Create New Job");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(58, 93, 174));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        contractorField = new JTextField(20);
        locationField = new JTextField(20);
        taskNameField = new JTextField(20);
        linearFeetField = new JTextField(20);

        statusCombo = new JComboBox<>(new String[]{
                "Scheduled",
                "In Progress",
                "Delayed",
                "Completed"
        });

        truckCombo = new JComboBox<>();
        loadAvailableTrucks();

        int row = 0;

        addRow(formPanel, gbc, row++, "Contractor:", contractorField);
        addRow(formPanel, gbc, row++, "Location:", locationField);
        addRow(formPanel, gbc, row++, "Task Name:", taskNameField);
        addRow(formPanel, gbc, row++, "Status:", statusCombo);
        addRow(formPanel, gbc, row++, "Linear Feet:", linearFeetField);
        addRow(formPanel, gbc, row++, "Assign Truck:", truckCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton saveButton = new JButton("Save Job");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(58, 93, 174));
        saveButton.setForeground(Color.WHITE);

        saveButton.addActionListener(e -> saveJob());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void loadAvailableTrucks() {
        truckCombo.removeAllItems();

        for (Truck t : manager.getTrucks()) {
            if (t.isAvailable()) {
                truckCombo.addItem(t);
            }
        }
    }

    private void saveJob() {

        String contractor = contractorField.getText().trim();
        String location = locationField.getText().trim();
        String taskName = taskNameField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        String linearFeetText = linearFeetField.getText().trim();
        Truck truck = (Truck) truckCombo.getSelectedItem();

        if (contractor.isEmpty() || location.isEmpty() || taskName.isEmpty() || linearFeetText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill in all fields");
            return;
        }

        if (truck == null) {
            JOptionPane.showMessageDialog(this, "Select a truck");
            return;
        }

        int linearFeet;
        try {
            linearFeet = Integer.parseInt(linearFeetText);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Linear feet must be a number");
            return;
        }

        // SIMPLE WORKING TASK (matches your system)
        Task task = new Task(
                manager.getTasks().size() + 1,
                1,
                taskName,
                contractor + " - " + location,
                "Unassigned",
                truck.getModel(),
                "Normal",
                "Today",
                "Tomorrow",
                status,
                "",
                "Standard",
                linearFeet,
                "Day"
        );

        manager.addTask(task);

        // mark truck unavailable
        truck.setAvailable(false);

        JOptionPane.showMessageDialog(this, "Job Created");

        dispose();
    }
}