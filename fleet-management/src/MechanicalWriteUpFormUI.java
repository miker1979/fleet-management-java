import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MechanicalWriteUpFormUI extends JFrame {

    private FleetManager manager;
    private MechanicDashboardUI dashboard;
    private Employee currentEmployee;
    private boolean mechanicMode;

    private JComboBox<String> assetTypeCombo;
    private JComboBox<String> assetCombo;

    private JTextField dateField;
    private JTextField reportedByField;
    private JComboBox<String> issueTypeCombo;
    private JComboBox<String> priorityCombo;
    private JTextArea problemArea;
    private JTextField assignedMechanicField;
    private JTextArea repairNotesArea;
    private JComboBox<String> repairStatusCombo;
    private JCheckBox safeToDriveCheck;
    private JCheckBox outOfServiceCheck;

    public MechanicalWriteUpFormUI(FleetManager manager, MechanicDashboardUI dashboard, Employee currentEmployee) {
        this.manager = manager;
        this.dashboard = dashboard;
        this.currentEmployee = currentEmployee;
        this.mechanicMode = isMechanic(currentEmployee);

        setTitle("Mechanical Write-Up Form");
        setSize(720, mechanicMode ? 780 : 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // 🔥 NEW: Asset Type
        assetTypeCombo = new JComboBox<>(new String[]{
                "Truck",
                "Forklift"
        });

        assetCombo = new JComboBox<>();
        loadAssets("Truck");

        assetTypeCombo.addActionListener(e -> {
            String selectedType = (String) assetTypeCombo.getSelectedItem();
            loadAssets(selectedType);
        });

        dateField = new JTextField(20);
        dateField.setEditable(false);
        dateField.setText(getCurrentTimestamp24Hour());

        reportedByField = new JTextField(20);
        reportedByField.setEditable(false);
        if (currentEmployee != null) {
            reportedByField.setText(currentEmployee.getFullName());
        }

        issueTypeCombo = new JComboBox<>(new String[]{
                "Engine",
                "Brakes",
                "Tires",
                "Transmission",
                "Electrical",
                "Hydraulics",
                "Forks",
                "Lift System",
                "Other"
        });

        priorityCombo = new JComboBox<>(new String[]{
                "Low",
                "Medium",
                "High",
                "Critical"
        });

        problemArea = new JTextArea(4, 20);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);

        assignedMechanicField = new JTextField(20);
        assignedMechanicField.setEditable(false);
        assignedMechanicField.setText(mechanicMode && currentEmployee != null
                ? currentEmployee.getFullName()
                : "Unassigned");

        repairNotesArea = new JTextArea(4, 20);
        repairNotesArea.setLineWrap(true);
        repairNotesArea.setWrapStyleWord(true);

        repairStatusCombo = new JComboBox<>(new String[]{
                "Open",
                "In Repair",
                "Waiting On Parts",
                "Completed"
        });

        safeToDriveCheck = new JCheckBox("Safe To Drive");
        outOfServiceCheck = new JCheckBox("Out Of Service");

        // 🔧 FORM FIELDS
        addField(panel, gbc, row++, "Asset Type:", assetTypeCombo);
        addField(panel, gbc, row++, "Asset ID:", assetCombo);
        addField(panel, gbc, row++, "Date / Time Reported:", dateField);
        addField(panel, gbc, row++, "Reported By:", reportedByField);
        addField(panel, gbc, row++, "Issue Type:", issueTypeCombo);
        addField(panel, gbc, row++, "Problem Description:", new JScrollPane(problemArea));

        if (mechanicMode) {
            addField(panel, gbc, row++, "Priority:", priorityCombo);
            addField(panel, gbc, row++, "Assigned Mechanic:", assignedMechanicField);
            addField(panel, gbc, row++, "Repair Notes:", new JScrollPane(repairNotesArea));
            addField(panel, gbc, row++, "Repair Status:", repairStatusCombo);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;

            JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            checkPanel.add(safeToDriveCheck);
            checkPanel.add(outOfServiceCheck);

            panel.add(checkPanel, gbc);
            row++;
        }

        JButton saveButton = new JButton("Save Write-Up");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveWriteUp());
        cancelButton.addActionListener(e -> dispose());

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, gbc);

        add(new JScrollPane(panel));
    }

    private boolean isMechanic(Employee employee) {
        return employee != null &&
                employee.getPosition() != null &&
                employee.getPosition().toLowerCase().contains("mechanic");
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private String getCurrentTimestamp24Hour() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        return LocalDateTime.now().format(formatter);
    }

    // 🔥 LOAD TRUCKS + FORKLIFTS
    private void loadAssets(String type) {
        assetCombo.removeAllItems();

        if (type.equalsIgnoreCase("Truck")) {
            for (Truck t : manager.getTrucks()) {
                assetCombo.addItem(t.getTruckID());
            }
        }

        // 🔧 Future: add forklifts here when you create them
        if (type.equalsIgnoreCase("Forklift")) {
            if (manager.getForklifts() != null) {
                for (Forklift f : manager.getForklifts()) {
                    assetCombo.addItem(f.getUnitId());
                }
            }
        }
    }

    private void saveWriteUp() {
        try {
            String assetType = (String) assetTypeCombo.getSelectedItem();
            String assetId = (String) assetCombo.getSelectedItem();

            String dateReported = dateField.getText().trim();
            String reportedBy = reportedByField.getText().trim();
            String issueType = (String) issueTypeCombo.getSelectedItem();
            String problemDescription = problemArea.getText().trim();

            if (assetId == null || assetId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an asset.");
                return;
            }

            if (problemDescription.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a problem description.");
                return;
            }

            String priority = mechanicMode ? (String) priorityCombo.getSelectedItem() : "Pending";
            String assignedMechanic = mechanicMode ? assignedMechanicField.getText() : "Unassigned";
            String repairNotes = mechanicMode ? repairNotesArea.getText() : "";
            String repairStatus = mechanicMode ? (String) repairStatusCombo.getSelectedItem() : "Open";

            boolean safeToDrive = mechanicMode && safeToDriveCheck.isSelected();
            boolean outOfService = mechanicMode && outOfServiceCheck.isSelected();

            MechanicalWriteUp writeUp = new MechanicalWriteUp(
                    manager.getNextWriteUpId(),
                    assetId,
                    assetType,
                    dateReported,
                    reportedBy,
                    issueType,
                    priority,
                    problemDescription,
                    safeToDrive,
                    outOfService,
                    assignedMechanic,
                    repairNotes,
                    repairStatus
            );

            manager.addMechanicalWriteUp(writeUp);

            if (dashboard != null) {
                dashboard.refreshRepairList();
            }

            JOptionPane.showMessageDialog(this, "Write-up saved.");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}