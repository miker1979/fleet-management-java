import javax.swing.*;
import java.awt.*;

public class EditEmployeeUI extends JFrame {

    private FleetManager manager;
    private Employee employee;

    public EditEmployeeUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Edit Employee");
        setSize(720, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        Font labelFont = new Font("SansSerif", Font.BOLD, 15);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 15);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // =========================
        // CORE FIELDS
        // =========================
        JTextField idField = new JTextField(String.valueOf(employee.getEmployeeId()));
        idField.setEditable(false);

        JComboBox<String> positionCombo = new JComboBox<>(new String[]{
                "Driver",
                "Foreman",
                "Mechanic",
                "Concrete Crew",
                "Operator",
                "Owner",
                "Admin",
                "Safety",
                "Office"
        });
        positionCombo.setSelectedItem(employee.getPosition());

        JTextField firstNameField = new JTextField(employee.getFirstName());
        JTextField lastNameField = new JTextField(employee.getLastName());

        JComboBox<String> departmentCombo = new JComboBox<>(new String[]{
                "Transportation",
                "Operations",
                "Mechanic",
                "Admin",
                "Safety"
        });
        departmentCombo.setSelectedItem(employee.getDepartment());

        JTextField addressField = new JTextField(employee.getAddress());
        JTextField phoneField = new JTextField(employee.getPhoneNumber());
        JTextField emailField = new JTextField(employee.getEmail());
        JTextField hireDateField = new JTextField(employee.getHireDate());
        JTextField payRateField = new JTextField(String.format("%.2f", employee.getPayRate()));

        JCheckBox activeBox = new JCheckBox("Active", employee.isActive());

        JComboBox<String> assignedTruckCombo = new JComboBox<>();
        assignedTruckCombo.addItem("None");
        for (Truck truck : manager.getTrucks()) {
            assignedTruckCombo.addItem(truck.getTruckID());
        }
        if (employee.getAssignedTruckId() != null && !employee.getAssignedTruckId().isEmpty()) {
            assignedTruckCombo.setSelectedItem(employee.getAssignedTruckId());
        } else {
            assignedTruckCombo.setSelectedItem("None");
        }

        // =========================
        // DRIVER SECTION
        // =========================
        JTextField licenseNumberField = new JTextField(employee.getDriverLicenseNumber());
        JTextField licenseClassField = new JTextField(employee.getLicenseClass());
        JTextField licenseExpField = new JTextField(employee.getLicenseExpirationDate());
        JTextField endorsementsField = new JTextField(employee.getEndorsements());
        JTextField dotExpField = new JTextField(employee.getDotPhysicalExpirationDate());

        // =========================
        // FORKLIFT SECTION
        // =========================
        JCheckBox forkliftBox = new JCheckBox("Forklift Certified", employee.isForkliftCertified());
        JTextField forkliftExpField = new JTextField(employee.getForkliftCertificationExpirationDate());

        // =========================
        // EMERGENCY CONTACT
        // =========================
        JTextField emergencyNameField = new JTextField(employee.getEmergencyContactName());
        JTextField emergencyPhoneField = new JTextField(employee.getEmergencyContactPhone());
        JTextField emergencyRelationField = new JTextField(employee.getEmergencyContactRelationship());

        Component[] fields = {
                idField, positionCombo, firstNameField, lastNameField,
                departmentCombo, addressField, phoneField, emailField,
                hireDateField, payRateField, assignedTruckCombo,
                licenseNumberField, licenseClassField, licenseExpField,
                endorsementsField, dotExpField,
                forkliftExpField,
                emergencyNameField, emergencyPhoneField, emergencyRelationField
        };

        for (Component c : fields) {
            c.setFont(fieldFont);
        }

        activeBox.setFont(fieldFont);
        forkliftBox.setFont(fieldFont);

        // =========================
        // BUILD FORM
        // =========================
        addField(formPanel, "Employee ID:", idField, labelFont);
        addField(formPanel, "Employee Type:", positionCombo, labelFont);
        addField(formPanel, "First Name:", firstNameField, labelFont);
        addField(formPanel, "Last Name:", lastNameField, labelFont);
        addField(formPanel, "Department:", departmentCombo, labelFont);
        addField(formPanel, "Address:", addressField, labelFont);
        addField(formPanel, "Phone Number:", phoneField, labelFont);
        addField(formPanel, "Email:", emailField, labelFont);
        addField(formPanel, "Hire Date:", hireDateField, labelFont);
        addField(formPanel, "Pay Rate:", payRateField, labelFont);
        addField(formPanel, "Assigned Truck:", assignedTruckCombo, labelFont);

        addSectionHeader(formPanel, "Driver Compliance");
        addField(formPanel, "License Number:", licenseNumberField, labelFont);
        addField(formPanel, "License Class:", licenseClassField, labelFont);
        addField(formPanel, "License Expiration:", licenseExpField, labelFont);
        addField(formPanel, "Endorsements:", endorsementsField, labelFont);
        addField(formPanel, "DOT Physical Exp:", dotExpField, labelFont);

        addSectionHeader(formPanel, "Equipment Certification");
        formPanel.add(new JLabel(""));
        formPanel.add(forkliftBox);
        addField(formPanel, "Forklift Expiration:", forkliftExpField, labelFont);

        addSectionHeader(formPanel, "Emergency Contact");
        addField(formPanel, "Contact Name:", emergencyNameField, labelFont);
        addField(formPanel, "Contact Phone:", emergencyPhoneField, labelFont);
        addField(formPanel, "Relationship:", emergencyRelationField, labelFont);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(labelFont);
        formPanel.add(statusLabel);
        formPanel.add(activeBox);

        // =========================
        // BUTTONS
        // =========================
        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setFont(labelFont);
        cancelBtn.setFont(labelFont);

        saveBtn.addActionListener(e -> {
            try {
                employee.setPosition((String) positionCombo.getSelectedItem());
                employee.setDepartment((String) departmentCombo.getSelectedItem());
                employee.setAddress(addressField.getText().trim());
                employee.setPhoneNumber(phoneField.getText().trim());
                employee.setEmail(emailField.getText().trim());
                employee.setActive(activeBox.isSelected());
                employee.setPayRate(Double.parseDouble(payRateField.getText().trim()));

                String assignedTruck = (String) assignedTruckCombo.getSelectedItem();
                if (assignedTruck != null && !assignedTruck.equalsIgnoreCase("None")) {
                    employee.setAssignedTruckId(assignedTruck);
                } else {
                    employee.setAssignedTruckId("");
                }

                employee.setDriverLicenseNumber(licenseNumberField.getText().trim());
                employee.setLicenseClass(licenseClassField.getText().trim());
                employee.setLicenseExpirationDate(licenseExpField.getText().trim());
                employee.setEndorsements(endorsementsField.getText().trim());
                employee.setDotPhysicalExpirationDate(dotExpField.getText().trim());

                employee.setForkliftCertified(forkliftBox.isSelected());
                employee.setForkliftCertificationExpirationDate(forkliftExpField.getText().trim());

                employee.setEmergencyContactName(emergencyNameField.getText().trim());
                employee.setEmergencyContactPhone(emergencyPhoneField.getText().trim());
                employee.setEmergencyContactRelationship(emergencyRelationField.getText().trim());

                JOptionPane.showMessageDialog(this, "Employee updated successfully.");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Pay rate must be a valid number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating employee: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveBtn);
        bottomPanel.add(cancelBtn);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, String label, Component field, Font font) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(font);
        panel.add(lbl);
        panel.add(field);
    }

    private void addSectionHeader(JPanel panel, String text) {
        panel.add(new JLabel(" "));
        JLabel header = new JLabel("---- " + text + " ----");
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(header);
    }
}