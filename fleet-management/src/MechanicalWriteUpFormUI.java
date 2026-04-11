import javax.swing.*;
import java.awt.*;

public class MechanicalWriteUpFormUI extends JFrame {

    private FleetManager manager;
    private MechanicDashboardUI dashboard;

    private JComboBox<String> truckCombo;
    private JTextField dateField;
    private JTextField reportedByField;
    private JComboBox<String> issueTypeCombo;
    private JComboBox<String> priorityCombo;
    private JTextArea problemArea;
    private JCheckBox safeToDriveCheck;
    private JCheckBox outOfServiceCheck;
    private JTextField assignedMechanicField;
    private JTextArea repairNotesArea;
    private JComboBox<String> repairStatusCombo;
    private JTextField estimatedCostField;

    public MechanicalWriteUpFormUI(FleetManager manager, MechanicDashboardUI dashboard) {
        this.manager = manager;
        this.dashboard = dashboard;

        setTitle("Mechanical Write-Up Form");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        truckCombo = new JComboBox<>();
        loadTrucks();

        dateField = new JTextField(15);
        reportedByField = new JTextField(15);

        issueTypeCombo = new JComboBox<>(new String[] {
                "Engine",
                "Brakes",
                "Tires",
                "Transmission",
                "Electrical",
                "Suspension",
                "Trailer",
                "Other"
        });

        priorityCombo = new JComboBox<>(new String[] {
                "Low",
                "Medium",
                "High",
                "Critical"
        });

        problemArea = new JTextArea(4, 20);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);

        safeToDriveCheck = new JCheckBox("Safe To Drive");
        outOfServiceCheck = new JCheckBox("Out Of Service");

        assignedMechanicField = new JTextField(15);

        repairNotesArea = new JTextArea(4, 20);
        repairNotesArea.setLineWrap(true);
        repairNotesArea.setWrapStyleWord(true);

        repairStatusCombo = new JComboBox<>(new String[] {
                "Open",
                "In Repair",
                "Waiting On Parts",
                "Completed"
        });

        estimatedCostField = new JTextField(15);

        addField(panel, gbc, row++, "Truck ID:", truckCombo);
        addField(panel, gbc, row++, "Date Reported:", dateField);
        addField(panel, gbc, row++, "Reported By:", reportedByField);
        addField(panel, gbc, row++, "Issue Type:", issueTypeCombo);
        addField(panel, gbc, row++, "Priority:", priorityCombo);
        addField(panel, gbc, row++, "Problem Description:", new JScrollPane(problemArea));
        addField(panel, gbc, row++, "Assigned Mechanic:", assignedMechanicField);
        addField(panel, gbc, row++, "Repair Notes:", new JScrollPane(repairNotesArea));
        addField(panel, gbc, row++, "Repair Status:", repairStatusCombo);
        addField(panel, gbc, row++, "Estimated Cost:", estimatedCostField);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkPanel.add(safeToDriveCheck);
        checkPanel.add(outOfServiceCheck);
        panel.add(checkPanel, gbc);

        row++;

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

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void loadTrucks() {
        truckCombo.removeAllItems();

        for (Truck truck : manager.getTrucks()) {
            truckCombo.addItem(truck.getTruckID());
        }
    }

    private void saveWriteUp() {
        try {
            String truckId = (String) truckCombo.getSelectedItem();
            String dateReported = dateField.getText().trim();
            String reportedBy = reportedByField.getText().trim();
            String issueType = (String) issueTypeCombo.getSelectedItem();
            String priority = (String) priorityCombo.getSelectedItem();
            String problemDescription = problemArea.getText().trim();
            boolean safeToDrive = safeToDriveCheck.isSelected();
            boolean outOfService = outOfServiceCheck.isSelected();
            String assignedMechanic = assignedMechanicField.getText().trim();
            String repairNotes = repairNotesArea.getText().trim();
            String repairStatus = (String) repairStatusCombo.getSelectedItem();

            double estimatedCost = 0.0;
            String costText = estimatedCostField.getText().trim();
            if (!costText.isEmpty()) {
                estimatedCost = Double.parseDouble(costText);
            }

            if (truckId == null || truckId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a truck.");
                return;
            }

            if (dateReported.isEmpty() || reportedBy.isEmpty() || problemDescription.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
                return;
            }

            MechanicalWriteUp writeUp = new MechanicalWriteUp(
                    manager.getNextWriteUpId(),
                    truckId,
                    dateReported,
                    reportedBy,
                    issueType,
                    priority,
                    problemDescription,
                    safeToDrive,
                    outOfService,
                    assignedMechanic,
                    repairNotes,
                    repairStatus,
                    estimatedCost
            );

            manager.addMechanicalWriteUp(writeUp);
            dashboard.refreshRepairList();

            JOptionPane.showMessageDialog(this, "Mechanical write-up saved successfully.");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Estimated cost must be a valid number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving write-up: " + ex.getMessage());
        }
    }
}