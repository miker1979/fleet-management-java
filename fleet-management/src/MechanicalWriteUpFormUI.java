import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MechanicalWriteUpFormUI extends JFrame {

    private FleetManager manager;
    private MechanicDashboardUI parentDashboard;

    private JComboBox<Truck> truckCombo;
    private JTextField dateField;
    private JTextField reportedByField;
    private JComboBox<String> issueTypeCombo;
    private JComboBox<String> priorityCombo;
    private JTextArea problemArea;
    private JCheckBox immediateSafetyCheck;
    private JCheckBox outOfServiceCheck;
    private JTextField mechanicField;
    private JTextArea repairNotesArea;
    private JComboBox<String> statusCombo;
    private JTextField estimatedCostField;

    public MechanicalWriteUpFormUI(FleetManager manager, MechanicDashboardUI parentDashboard) {
        this.manager = manager;
        this.parentDashboard = parentDashboard;

        setTitle("New Mechanical Write-Up");
        setSize(650, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Create Mechanical Write-Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        truckCombo = new JComboBox<>();
        loadTrucks();

        dateField = new JTextField("2026-01-15", 20);
        reportedByField = new JTextField("Mike Robinson", 20);

        issueTypeCombo = new JComboBox<>(new String[]{
                "Brakes",
                "Flat Tire",
                "Oil Leak",
                "Engine",
                "Hydraulics",
                "Lights",
                "Inspection",
                "Other"
        });

        priorityCombo = new JComboBox<>(new String[]{
                "Low",
                "Medium",
                "High"
        });

        problemArea = new JTextArea(4, 20);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);

        immediateSafetyCheck = new JCheckBox("Immediate Safety Issue");
        immediateSafetyCheck.setBackground(Color.WHITE);

        outOfServiceCheck = new JCheckBox("Mark Truck Out of Service");
        outOfServiceCheck.setBackground(Color.WHITE);

        mechanicField = new JTextField("", 20);

        repairNotesArea = new JTextArea(4, 20);
        repairNotesArea.setLineWrap(true);
        repairNotesArea.setWrapStyleWord(true);

        statusCombo = new JComboBox<>(new String[]{
                "Waiting",
                "In Repair",
                "Completed"
        });

        estimatedCostField = new JTextField("0.00", 20);

        int row = 0;
        addRow(formPanel, gbc, row++, "Truck:", truckCombo);
        addRow(formPanel, gbc, row++, "Date:", dateField);
        addRow(formPanel, gbc, row++, "Reported By:", reportedByField);
        addRow(formPanel, gbc, row++, "Issue Type:", issueTypeCombo);
        addRow(formPanel, gbc, row++, "Priority:", priorityCombo);
        addRow(formPanel, gbc, row++, "Problem Description:", new JScrollPane(problemArea));
        addRow(formPanel, gbc, row++, "Safety Flag:", immediateSafetyCheck);
        addRow(formPanel, gbc, row++, "Truck Status:", outOfServiceCheck);
        addRow(formPanel, gbc, row++, "Assigned Mechanic:", mechanicField);
        addRow(formPanel, gbc, row++, "Repair Notes:", new JScrollPane(repairNotesArea));
        addRow(formPanel, gbc, row++, "Repair Status:", statusCombo);
        addRow(formPanel, gbc, row++, "Estimated Cost:", estimatedCostField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Write-Up");

        saveButton.setBackground(new Color(60, 90, 160));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveWriteUp());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

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

    private void loadTrucks() {
        truckCombo.removeAllItems();

        for (Truck truck : manager.getTrucks()) {
            truckCombo.addItem(truck);
        }
    }

    private void saveWriteUp() {
        Truck selectedTruck = (Truck) truckCombo.getSelectedItem();
        String date = dateField.getText().trim();
        String reportedBy = reportedByField.getText().trim();
        String issueType = (String) issueTypeCombo.getSelectedItem();
        String priority = (String) priorityCombo.getSelectedItem();
        String problemDescription = problemArea.getText().trim();
        boolean immediateSafetyIssue = immediateSafetyCheck.isSelected();
        boolean outOfService = outOfServiceCheck.isSelected();
        String assignedMechanic = mechanicField.getText().trim();
        String repairNotes = repairNotesArea.getText().trim();
        String repairStatus = (String) statusCombo.getSelectedItem();
        String estimatedCostText = estimatedCostField.getText().trim();

        if (selectedTruck == null || date.isEmpty() || reportedBy.isEmpty() || problemDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        double estimatedCost;
        try {
            estimatedCost = Double.parseDouble(estimatedCostText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Estimated cost must be a valid number.");
            return;
        }

        int writeUpId = manager.getNextWriteUpId();

        MechanicalWriteUp writeUp = new MechanicalWriteUp(
                writeUpId,
                selectedTruck.getId(),
                date,
                reportedBy,
                issueType,
                priority,
                problemDescription,
                immediateSafetyIssue,
                outOfService,
                assignedMechanic,
                repairNotes,
                repairStatus,
                estimatedCost
        );

        manager.addMechanicalWriteUp(writeUp);

        if (outOfService) {
            selectedTruck.setAvailable(false);
        }

        JOptionPane.showMessageDialog(this, "Mechanical write-up saved successfully.");

        if (parentDashboard != null) {
            parentDashboard.refreshRepairList();
        }

        dispose();
    }
}