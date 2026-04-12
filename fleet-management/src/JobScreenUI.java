import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JobScreenUI extends JFrame {
    private FleetManager manager;

    private JComboBox<String> typeCombo;
    private JComboBox<String> contractorCombo;
    private JComboBox<String> foremanCombo;
    private JComboBox<String> statusCombo;
    private JComboBox<String> hourCombo;
    private JComboBox<String> minuteCombo;

    private JTextField dateField;
    private JTextField locationField;
    private JTextField truckField;

    public JobScreenUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Dispatch New Job");
        setSize(560, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Dispatch New Job");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] types = {"Barrier Install", "Barrier Removal", "Relocation", "Crash Absorber"};
        String[] contractors = {"Pulice", "Sundt", "Kiewit", "Howe Precast", "ADOT"};
        String[] foremen = {"Mike Robinson", "Foreman Joe", "Foreman Steve"};
        String[] statuses = {"Scheduled", "Waiting for Closure", "In Progress", "Completed"};

        String[] hours = {
                "0100", "0200", "0300", "0400", "0500", "0600",
                "0700", "0800", "0900", "1000", "1100", "1200",
                "1300", "1400", "1500", "1600", "1700", "1800",
                "1900", "2000", "2100", "2200", "2300", "0000"
        };

        String[] minutes = {"00", "15", "30", "45"};

        typeCombo = new JComboBox<>(types);
        contractorCombo = new JComboBox<>(contractors);
        foremanCombo = new JComboBox<>(foremen);
        statusCombo = new JComboBox<>(statuses);

        dateField = new JTextField("2026-04-10", 20);
        locationField = new JTextField(20);
        truckField = new JTextField("Truck 101, Truck 202", 20);

        hourCombo = new JComboBox<>(hours);
        minuteCombo = new JComboBox<>(minutes);

        int row = 0;
        addRow(formPanel, gbc, row++, "Job Type:", typeCombo);
        addRow(formPanel, gbc, row++, "Contractor:", contractorCombo);
        addRow(formPanel, gbc, row++, "Dispatch Date (YYYY-MM-DD):", dateField);
        addTimeRow(formPanel, gbc, row++, "Start Time:", hourCombo, minuteCombo);
        addRow(formPanel, gbc, row++, "Location:", locationField);
        addRow(formPanel, gbc, row++, "Foreman:", foremanCombo);
        addRow(formPanel, gbc, row++, "Assigned Trucks:", truckField);
        addRow(formPanel, gbc, row++, "Initial Status:", statusCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn = new JButton("Dispatch Job");

        saveBtn.setBackground(new Color(60, 90, 160));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        cancelBtn.setFocusPainted(false);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> saveAction());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void addTimeRow(JPanel panel, GridBagConstraints gbc, int row, String labelText,
                            JComboBox<String> hourCombo, JComboBox<String> minuteCombo) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        timePanel.setBackground(Color.WHITE);
        timePanel.add(hourCombo);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteCombo);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(timePanel, gbc);
    }

    private void saveAction() {
        String dispatchDate = dateField.getText().trim();
        String location = locationField.getText().trim();
        String assignedTrucks = truckField.getText().trim();
        String startTime = hourCombo.getSelectedItem() + ":" + minuteCombo.getSelectedItem();

        if (dispatchDate.isEmpty() || location.isEmpty() || assignedTrucks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        try {
            java.time.LocalDate.parse(dispatchDate);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dispatch date must be in YYYY-MM-DD format.");
            return;
        }

        Task newTask = new Task(
                manager.getTasks().size() + 1001,
                dispatchDate,
                startTime,
                (String) typeCombo.getSelectedItem(),
                (String) contractorCombo.getSelectedItem(),
                location,
                (String) foremanCombo.getSelectedItem(),
                assignedTrucks,
                (String) statusCombo.getSelectedItem()
        );

        manager.addTask(newTask);

        JOptionPane.showMessageDialog(
                this,
                "Job dispatched successfully.\nStart Time: " + startTime
        );

        dispose();
    }
}