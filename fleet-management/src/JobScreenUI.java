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
        gbc.anchor = GridBagConstraints.WEST;

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

        addFormRow(formPanel, gbc, row++, "Contractor:", contractorField);
        addFormRow(formPanel, gbc, row++, "Location:", locationField);
        addFormRow(formPanel, gbc, row++, "Task Name:", taskNameField);
        addFormRow(formPanel, gbc, row++, "Status:", statusCombo);
        addFormRow(formPanel, gbc, row++, "Linear Feet Installed:", linearFeetField);
        addFormRow(formPanel, gbc, row++, "Assign Truck:", truckCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton saveButton = new JButton("Save Job");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(58, 93, 174));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        cancelButton.setFocusPainted(false);

        saveButton.addActionListener(e -> saveJob());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void loadAvailableTrucks() {
        truckCombo.removeAllItems();

        for (Truck truck : manager.getTrucks()) {
            if (truck.isAvailable()) {
                truckCombo.addItem(truck);
            }
        }
    }

    private void saveJob() {

        String contractor = contractorField.getText().trim();
        String location = locationField.getText().trim();
        String taskName = taskNameField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        String linearFeetText = linearFeetField.getText().trim();
        Truck selectedTruck = (Truck) truckCombo.getSelectedItem();

        if (contractor.isEmpty() || location.isEmpty() || taskName.isEmpty() || linearFeetText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (selectedTruck == null) {
            JOptionPane.showMessageDialog(this, "Please assign an available truck.");
            return;
        }

        int linearFeetInstalled;
        try {
            linearFeetInstalled = Integer.parseInt(linearFeetText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Linear Feet Installed must be a number.");
            return;
        }

        // CREATE TASK
        Task newTask = new Task(taskName, status, linearFeetInstalled, contractor, location, selectedTruck);
        manager.addTask(newTask);

        // ASSIGN TRUCK TO JOB (IMPORTANT FIX)
        selectedTruck.assignToJob(1); // placeholder job ID for now

        JOptionPane.showMessageDialog(this, "Job created successfully.");

        // OPTIONAL: refresh available trucks if reopened
        dispose();
    }
}