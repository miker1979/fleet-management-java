import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class EditEmployeeUI extends JFrame {

    private final FleetManager manager;
    private final Employee employee;

    public EditEmployeeUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Edit Employee");
        setSize(760, 900);
        setMinimumSize(new Dimension(620, 550));
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Font labelFont = new Font("SansSerif", Font.BOLD, 15);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 15);
        Font sectionFont = new Font("SansSerif", Font.BOLD, 17);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 100, 20));
        mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField(String.valueOf(employee.getEmployeeId()), 20);
        idField.setEditable(false);

        JComboBox<String> positionCombo = new JComboBox<>(new String[]{
                "Driver", "Foreman", "Mechanic", "Concrete Crew", "Operator", "Owner", "Admin", "Safety", "Office"
        });
        positionCombo.setSelectedItem(employee.getPosition());

        JTextField firstNameField = new JTextField(employee.getFirstName(), 20);
        JTextField lastNameField = new JTextField(employee.getLastName(), 20);

        JComboBox<String> departmentCombo = new JComboBox<>(new String[]{
                "Transportation", "Operations", "Mechanic", "Admin", "Safety"
        });
        departmentCombo.setSelectedItem(employee.getDepartment());

        JTextField addressField = new JTextField(employee.getAddress(), 20);
        JTextField phoneField = new JTextField(employee.getPhoneNumber(), 20);
        JTextField emailField = new JTextField(employee.getEmail(), 20);
        JTextField hireDateField = new JTextField(employee.getHireDate(), 20);
        JTextField payRateField = new JTextField(String.format("%.2f", employee.getPayRate()), 20);

        JCheckBox activeBox = new JCheckBox("Active", employee.isActive());
        JCheckBox forkliftBox = new JCheckBox("Forklift Certified", employee.isForkliftCertified());

        JTextField licenseNumberField = new JTextField(employee.getDriverLicenseNumber(), 20);
        JTextField licenseExpField = new JTextField(employee.getLicenseExpirationDate(), 20);
        JTextField dotExpField = new JTextField(employee.getDotPhysicalExpirationDate(), 20);
        JTextField forkliftExpField = new JTextField(employee.getForkliftCertificationExpirationDate(), 20);

        JComboBox<String> licenseClassCombo = new JComboBox<>(new String[]{
                "",
                "A",
                "B",
                "C"
        });
        licenseClassCombo.setSelectedItem(employee.getLicenseClass());

        JTextField emergencyNameField = new JTextField(employee.getEmergencyContactName(), 20);
        JTextField emergencyPhoneField = new JTextField(employee.getEmergencyContactPhone(), 20);
        JTextField emergencyRelationField = new JTextField(employee.getEmergencyContactRelationship(), 20);

        JCheckBox tBox = new JCheckBox("T - Double/Triple Trailers");
        JCheckBox pBox = new JCheckBox("P - Passenger");
        JCheckBox sBox = new JCheckBox("S - School Bus");
        JCheckBox nBox = new JCheckBox("N - Tank Vehicle");
        JCheckBox hBox = new JCheckBox("H - Hazardous Materials");
        JCheckBox xBox = new JCheckBox("X - Tank Vehicle / HazMat Combination");

        ArrayList<String> currentEndorsements = employee.getEndorsements();
        if (currentEndorsements != null) {
            if (currentEndorsements.contains("T - Double/Triple Trailers")) tBox.setSelected(true);
            if (currentEndorsements.contains("P - Passenger")) pBox.setSelected(true);
            if (currentEndorsements.contains("S - School Bus")) sBox.setSelected(true);
            if (currentEndorsements.contains("N - Tank Vehicle")) nBox.setSelected(true);
            if (currentEndorsements.contains("H - Hazardous Materials")) hBox.setSelected(true);
            if (currentEndorsements.contains("X - Tank Vehicle / HazMat Combination")) xBox.setSelected(true);
        }

        JPanel endorsementsPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        endorsementsPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        endorsementsPanel.add(tBox);
        endorsementsPanel.add(pBox);
        endorsementsPanel.add(sBox);
        endorsementsPanel.add(nBox);
        endorsementsPanel.add(hBox);
        endorsementsPanel.add(xBox);

        Component[] fields = {
                idField, positionCombo, firstNameField, lastNameField,
                departmentCombo, addressField, phoneField, emailField,
                hireDateField, payRateField,
                licenseNumberField, licenseClassCombo, licenseExpField,
                dotExpField, forkliftExpField,
                emergencyNameField, emergencyPhoneField, emergencyRelationField
        };

        for (Component c : fields) {
            c.setFont(fieldFont);
        }

        activeBox.setFont(fieldFont);
        forkliftBox.setFont(fieldFont);
        tBox.setFont(fieldFont);
        pBox.setFont(fieldFont);
        sBox.setFont(fieldFont);
        nBox.setFont(fieldFont);
        hBox.setFont(fieldFont);
        xBox.setFont(fieldFont);

        int row = 0;

        row = addField(formPanel, gbc, row, "Employee ID:", idField, labelFont);
        row = addField(formPanel, gbc, row, "Employee Type:", positionCombo, labelFont);
        row = addField(formPanel, gbc, row, "First Name:", firstNameField, labelFont);
        row = addField(formPanel, gbc, row, "Last Name:", lastNameField, labelFont);
        row = addField(formPanel, gbc, row, "Department:", departmentCombo, labelFont);
        row = addField(formPanel, gbc, row, "Address:", addressField, labelFont);
        row = addField(formPanel, gbc, row, "Phone Number:", phoneField, labelFont);
        row = addField(formPanel, gbc, row, "Email:", emailField, labelFont);
        row = addField(formPanel, gbc, row, "Hire Date:", hireDateField, labelFont);
        row = addField(formPanel, gbc, row, "Pay Rate:", payRateField, labelFont);
        row = addField(formPanel, gbc, row, "Status:", activeBox, labelFont);

        row = addSectionHeader(formPanel, gbc, row, "Driver Compliance", sectionFont);
        row = addField(formPanel, gbc, row, "License Number:", licenseNumberField, labelFont);
        row = addField(formPanel, gbc, row, "License Class:", licenseClassCombo, labelFont);
        row = addField(formPanel, gbc, row, "License Expiration:", licenseExpField, labelFont);
        row = addField(formPanel, gbc, row, "Endorsements:", endorsementsPanel, labelFont);
        row = addField(formPanel, gbc, row, "DOT Physical Exp:", dotExpField, labelFont);

        row = addSectionHeader(formPanel, gbc, row, "Equipment Certification", sectionFont);
        row = addField(formPanel, gbc, row, "Forklift Certified:", forkliftBox, labelFont);
        row = addField(formPanel, gbc, row, "Forklift Expiration:", forkliftExpField, labelFont);

        row = addSectionHeader(formPanel, gbc, row, "Emergency Contact", sectionFont);
        row = addField(formPanel, gbc, row, "Contact Name:", emergencyNameField, labelFont);
        row = addField(formPanel, gbc, row, "Contact Phone:", emergencyPhoneField, labelFont);
        row = addField(formPanel, gbc, row, "Relationship:", emergencyRelationField, labelFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setFont(fieldFont);
        cancelBtn.setFont(fieldFont);

        saveBtn.addActionListener(e -> {
            try {
                employee.setPosition((String) positionCombo.getSelectedItem());
                employee.setDepartment((String) departmentCombo.getSelectedItem());
                employee.setFirstName(firstNameField.getText().trim());
                employee.setLastName(lastNameField.getText().trim());
                employee.setAddress(addressField.getText().trim());
                employee.setPhoneNumber(phoneField.getText().trim());
                employee.setEmail(emailField.getText().trim());
                employee.setHireDate(hireDateField.getText().trim());
                employee.setActive(activeBox.isSelected());
                employee.setPayRate(Double.parseDouble(payRateField.getText().trim()));

                ArrayList<String> selectedEndorsements = new ArrayList<>();
                if (tBox.isSelected()) selectedEndorsements.add("T - Double/Triple Trailers");
                if (pBox.isSelected()) selectedEndorsements.add("P - Passenger");
                if (sBox.isSelected()) selectedEndorsements.add("S - School Bus");
                if (nBox.isSelected()) selectedEndorsements.add("N - Tank Vehicle");
                if (hBox.isSelected()) selectedEndorsements.add("H - Hazardous Materials");
                if (xBox.isSelected()) selectedEndorsements.add("X - Tank Vehicle / HazMat Combination");

                employee.setDriverLicenseNumber(licenseNumberField.getText().trim());
                employee.setLicenseClass((String) licenseClassCombo.getSelectedItem());
                employee.setLicenseExpirationDate(licenseExpField.getText().trim());
                employee.setEndorsements(selectedEndorsements);
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

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        formPanel.revalidate();
        mainPanel.revalidate();

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field, Font labelFont) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        panel.add(field, gbc);

        return row + 1;
    }

    private int addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String text, Font sectionFont) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel(text);
        header.setFont(sectionFont);
        header.setBorder(BorderFactory.createEmptyBorder(18, 0, 6, 0));
        panel.add(header, gbc);

        gbc.gridwidth = 1;
        return row + 1;
    }
}