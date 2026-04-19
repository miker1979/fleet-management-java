import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class CreateEmployeeUI extends JFrame {

    private final FleetManager manager;

    public CreateEmployeeUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create Employee");
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

        JTextField idField = new JTextField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);

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

        JComboBox<String> departmentCombo = new JComboBox<>(new String[]{
                "Transportation",
                "Operations",
                "Mechanic",
                "Admin",
                "Safety"
        });

        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField hireDateField = new JTextField("2026-01-01", 20);
        JTextField payRateField = new JTextField(20);

        JComboBox<String> payTypeCombo = new JComboBox<>(new String[]{
                "Hourly",
                "Salary Per Year",
                "Salary Per Month",
                "Salary Per Week"
        });

        JCheckBox activeBox = new JCheckBox("Active", true);

        JTextField licenseNumberField = new JTextField(20);

        JComboBox<String> licenseClassCombo = new JComboBox<>(new String[]{
                "",
                "A",
                "B",
                "C"
        });

        JTextField licenseExpField = new JTextField("2028-08-06", 20);
        JTextField dotExpField = new JTextField("2026-10-31", 20);

        JCheckBox forkliftBox = new JCheckBox("Forklift Certified");
        JTextField forkliftExpField = new JTextField("2028-10-31", 20);

        JTextField emergencyNameField = new JTextField(20);
        JTextField emergencyPhoneField = new JTextField(20);
        JTextField emergencyRelationField = new JTextField(20);

        JCheckBox tBox = new JCheckBox("T - Double/Triple Trailers");
        JCheckBox pBox = new JCheckBox("P - Passenger");
        JCheckBox sBox = new JCheckBox("S - School Bus");
        JCheckBox nBox = new JCheckBox("N - Tank Vehicle");
        JCheckBox hBox = new JCheckBox("H - Hazardous Materials");
        JCheckBox xBox = new JCheckBox("X - Tank Vehicle / HazMat Combination");

        JPanel endorsementsPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        endorsementsPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        endorsementsPanel.add(tBox);
        endorsementsPanel.add(pBox);
        endorsementsPanel.add(sBox);
        endorsementsPanel.add(nBox);
        endorsementsPanel.add(hBox);
        endorsementsPanel.add(xBox);

        Component[] fields = {
                idField, firstNameField, lastNameField, positionCombo,
                departmentCombo, addressField, phoneField, emailField,
                hireDateField, payRateField, payTypeCombo,
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

        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        ((AbstractDocument) payRateField.getDocument()).setDocumentFilter(new DecimalFilter());

        installPhoneFormatter(phoneField);
        installPhoneFormatter(emergencyPhoneField);

        int row = 0;

        row = addField(formPanel, gbc, row, "Employee ID:", idField, labelFont);
        row = addField(formPanel, gbc, row, "Employee Type:", positionCombo, labelFont);
        row = addField(formPanel, gbc, row, "First Name:", firstNameField, labelFont);
        row = addField(formPanel, gbc, row, "Last Name:", lastNameField, labelFont);
        row = addField(formPanel, gbc, row, "Department:", departmentCombo, labelFont);
        row = addField(formPanel, gbc, row, "Address:", addressField, labelFont);
        row = addField(formPanel, gbc, row, "Phone Number:", phoneField, labelFont);
        row = addField(formPanel, gbc, row, "Email:", emailField, labelFont);
        row = addField(formPanel, gbc, row, "Hire Date (yyyy-MM-dd):", hireDateField, labelFont);
        row = addField(formPanel, gbc, row, "Pay Amount:", payRateField, labelFont);
        row = addField(formPanel, gbc, row, "Pay Type:", payTypeCombo, labelFont);
        row = addField(formPanel, gbc, row, "Status:", activeBox, labelFont);

        row = addSectionHeader(formPanel, gbc, row, "Driver Compliance", sectionFont);
        row = addField(formPanel, gbc, row, "License Number:", licenseNumberField, labelFont);
        row = addField(formPanel, gbc, row, "License Class:", licenseClassCombo, labelFont);
        row = addField(formPanel, gbc, row, "License Expiration (yyyy-MM-dd):", licenseExpField, labelFont);
        row = addField(formPanel, gbc, row, "Endorsements:", endorsementsPanel, labelFont);
        row = addField(formPanel, gbc, row, "DOT Physical Exp (yyyy-MM-dd):", dotExpField, labelFont);

        row = addSectionHeader(formPanel, gbc, row, "Equipment Certification", sectionFont);
        row = addField(formPanel, gbc, row, "Forklift Certified:", forkliftBox, labelFont);
        row = addField(formPanel, gbc, row, "Forklift Expiration (yyyy-MM-dd):", forkliftExpField, labelFont);

        row = addSectionHeader(formPanel, gbc, row, "Emergency Contact", sectionFont);
        row = addField(formPanel, gbc, row, "Contact Name:", emergencyNameField, labelFont);
        row = addField(formPanel, gbc, row, "Contact Phone:", emergencyPhoneField, labelFont);
        row = addField(formPanel, gbc, row, "Relationship:", emergencyRelationField, labelFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = new JButton("Save Employee");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setFont(fieldFont);
        cancelBtn.setFont(fieldFont);

        saveBtn.addActionListener(e -> {
            try {
                String idText = idField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String hireDate = hireDateField.getText().trim();
                String payText = payRateField.getText().trim();
                String licenseExp = licenseExpField.getText().trim();
                String dotExp = dotExpField.getText().trim();
                String forkliftExp = forkliftExpField.getText().trim();
                String emergencyName = emergencyNameField.getText().trim();
                String emergencyPhone = emergencyPhoneField.getText().trim();
                String emergencyRelation = emergencyRelationField.getText().trim();

                if (idText.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    throw new IllegalArgumentException("Employee ID, first name, and last name are required.");
                }

                if (payText.isEmpty()) {
                    throw new IllegalArgumentException("Pay Amount is required.");
                }

                if (phone.length() != 14) {
                    throw new IllegalArgumentException("Phone number must be in format (###) ###-####.");
                }

                if (!emergencyPhone.isEmpty() && emergencyPhone.length() != 14) {
                    throw new IllegalArgumentException("Emergency phone must be in format (###) ###-####.");
                }

                validateDate(hireDate, "Hire Date");
                validateDate(licenseExp, "License Expiration");

                if (!dotExp.isEmpty()) {
                    validateDate(dotExp, "DOT Physical Expiration");
                }

                if (forkliftBox.isSelected()) {
                    if (forkliftExp.isEmpty()) {
                        throw new IllegalArgumentException("Forklift expiration is required when forklift certified is checked.");
                    }
                    validateDate(forkliftExp, "Forklift Expiration");
                }

                int id = Integer.parseInt(idText);
                double payAmount = Double.parseDouble(payText);

                ArrayList<String> selectedEndorsements = new ArrayList<>();
                if (tBox.isSelected()) selectedEndorsements.add("T - Double/Triple Trailers");
                if (pBox.isSelected()) selectedEndorsements.add("P - Passenger");
                if (sBox.isSelected()) selectedEndorsements.add("S - School Bus");
                if (nBox.isSelected()) selectedEndorsements.add("N - Tank Vehicle");
                if (hBox.isSelected()) selectedEndorsements.add("H - Hazardous Materials");
                if (xBox.isSelected()) selectedEndorsements.add("X - Tank Vehicle / HazMat Combination");

                Employee employee = new Employee(
                        id,
                        firstName,
                        lastName,
                        (String) positionCombo.getSelectedItem(),
                        (String) departmentCombo.getSelectedItem(),
                        address,
                        phone,
                        email,
                        hireDate,
                        activeBox.isSelected(),
                        payAmount
                );

                employee.setDriverLicenseNumber(licenseNumberField.getText().trim());
                employee.setLicenseClass((String) licenseClassCombo.getSelectedItem());
                employee.setLicenseExpirationDate(licenseExp);
                employee.setEndorsements(selectedEndorsements);
                employee.setDotPhysicalExpirationDate(dotExp);
                employee.setForkliftCertified(forkliftBox.isSelected());
                employee.setForkliftCertificationExpirationDate(forkliftExp);
                employee.setEmergencyContactName(emergencyName);
                employee.setEmergencyContactPhone(emergencyPhone);
                employee.setEmergencyContactRelationship(emergencyRelation);

                manager.addEmployee(employee);
                DataStore.save(manager);

                JOptionPane.showMessageDialog(this, "Employee created successfully.");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Employee ID and Pay Amount must be valid numbers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
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

    private void validateDate(String dateText, String fieldName) {
        try {
            LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must use yyyy-MM-dd format.");
        }
    }

    private void installPhoneFormatter(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String digits = field.getText().replaceAll("\\D", "");

                    if (digits.length() > 10) {
                        digits = digits.substring(0, 10);
                    }

                    StringBuilder formatted = new StringBuilder();

                    if (digits.length() > 0) {
                        formatted.append("(");
                        formatted.append(digits, 0, Math.min(3, digits.length()));
                    }
                    if (digits.length() >= 4) {
                        formatted.append(") ");
                        formatted.append(digits, 3, Math.min(6, digits.length()));
                    }
                    if (digits.length() >= 7) {
                        formatted.append("-");
                        formatted.append(digits, 6, Math.min(10, digits.length()));
                    }

                    field.setText(formatted.toString());
                });
            }
        });
    }

    private static class DigitOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null || text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private static class DecimalFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) {
                return;
            }

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String future = current.substring(0, offset) + string + current.substring(offset);

            if (future.matches("\\d*(\\.\\d{0,2})?")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String future = current.substring(0, offset)
                    + (text == null ? "" : text)
                    + current.substring(offset + length);

            if (future.matches("\\d*(\\.\\d{0,2})?")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}