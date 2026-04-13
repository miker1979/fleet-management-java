import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreateEmployeeUI extends JFrame {

    private FleetManager manager;

    public CreateEmployeeUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create Employee");
        setSize(760, 950);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        Font labelFont = new Font("SansSerif", Font.BOLD, 15);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 15);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();

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

        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField hireDateField = new JTextField("2026-01-01");
        JTextField payRateField = new JTextField();

        JComboBox<String> payTypeCombo = new JComboBox<>(new String[]{
                "Hourly",
                "Salary Per Year",
                "Salary Per Month",
                "Salary Per Week"
        });

        JCheckBox activeBox = new JCheckBox("Active", true);

        JComboBox<String> assignedTruckCombo = new JComboBox<>();
        assignedTruckCombo.addItem("None");
        for (Truck truck : manager.getTrucks()) {
            assignedTruckCombo.addItem(truck.getTruckID());
        }

        JTextField licenseNumberField = new JTextField();

        JComboBox<String> licenseClassCombo = new JComboBox<>(new String[]{
                "",
                "A",
                "B",
                "C"
        });

        JTextField licenseExpField = new JTextField("2028-08-06");

        JComboBox<String> endorsementsCombo = new JComboBox<>(new String[]{
                "",
                "T - Double/Triple Trailers",
                "P - Passenger",
                "S - School Bus",
                "N - Tank Vehicle",
                "H - Hazardous Materials",
                "X - Tank Vehicle / HazMat Combination"
        });

        JTextField dotExpField = new JTextField("2026-10-31");

        JCheckBox forkliftBox = new JCheckBox("Forklift Certified");
        JTextField forkliftExpField = new JTextField("2028-10-31");

        JTextField emergencyNameField = new JTextField();
        JTextField emergencyPhoneField = new JTextField();
        JTextField emergencyRelationField = new JTextField();

        Component[] fields = {
                idField, firstNameField, lastNameField, positionCombo,
                departmentCombo, addressField, phoneField, emailField,
                hireDateField, payRateField, payTypeCombo, assignedTruckCombo,
                licenseNumberField, licenseClassCombo, licenseExpField,
                endorsementsCombo, dotExpField,
                forkliftExpField,
                emergencyNameField, emergencyPhoneField, emergencyRelationField
        };

        for (Component c : fields) {
            c.setFont(fieldFont);
        }

        forkliftBox.setFont(fieldFont);
        activeBox.setFont(fieldFont);

        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        ((AbstractDocument) payRateField.getDocument()).setDocumentFilter(new DecimalFilter());

        installPhoneFormatter(phoneField);
        installPhoneFormatter(emergencyPhoneField);

        addField(formPanel, "Employee ID:", idField, labelFont);
        addField(formPanel, "Employee Type:", positionCombo, labelFont);
        addField(formPanel, "First Name:", firstNameField, labelFont);
        addField(formPanel, "Last Name:", lastNameField, labelFont);
        addField(formPanel, "Department:", departmentCombo, labelFont);
        addField(formPanel, "Address:", addressField, labelFont);
        addField(formPanel, "Phone Number:", phoneField, labelFont);
        addField(formPanel, "Email:", emailField, labelFont);
        addField(formPanel, "Hire Date (yyyy-MM-dd):", hireDateField, labelFont);
        addField(formPanel, "Pay Amount:", payRateField, labelFont);
        addField(formPanel, "Pay Type:", payTypeCombo, labelFont);
        addField(formPanel, "Assigned Truck:", assignedTruckCombo, labelFont);

        addSectionHeader(formPanel, "Driver Compliance");
        addField(formPanel, "License Number:", licenseNumberField, labelFont);
        addField(formPanel, "License Class:", licenseClassCombo, labelFont);
        addField(formPanel, "License Expiration (yyyy-MM-dd):", licenseExpField, labelFont);
        addField(formPanel, "Endorsements:", endorsementsCombo, labelFont);
        addField(formPanel, "DOT Physical Exp (yyyy-MM-dd):", dotExpField, labelFont);

        addSectionHeader(formPanel, "Equipment Certification");
        formPanel.add(new JLabel(""));
        formPanel.add(forkliftBox);
        addField(formPanel, "Forklift Expiration (yyyy-MM-dd):", forkliftExpField, labelFont);

        addSectionHeader(formPanel, "Emergency Contact");
        addField(formPanel, "Contact Name:", emergencyNameField, labelFont);
        addField(formPanel, "Contact Phone:", emergencyPhoneField, labelFont);
        addField(formPanel, "Relationship:", emergencyRelationField, labelFont);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(labelFont);
        formPanel.add(statusLabel);
        formPanel.add(activeBox);

        JButton saveBtn = new JButton("Save Employee");
        JButton cancelBtn = new JButton("Cancel");

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

                if (forkliftBox.isSelected() && !forkliftExp.isEmpty()) {
                    validateDate(forkliftExp, "Forklift Expiration");
                }

                int id = Integer.parseInt(idText);
                double payAmount = Double.parseDouble(payText);

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

                String truck = (String) assignedTruckCombo.getSelectedItem();
                if (truck != null && !truck.equals("None")) {
                    employee.setAssignedTruckId(truck);
                }

                employee.setDriverLicenseNumber(licenseNumberField.getText().trim());
                employee.setLicenseClass((String) licenseClassCombo.getSelectedItem());
                employee.setLicenseExpirationDate(licenseExp);

                String selectedEndorsement = (String) endorsementsCombo.getSelectedItem();
                if (selectedEndorsement != null) {
                    employee.setEndorsements(selectedEndorsement);
                } else {
                    employee.setEndorsements("");
                }

                employee.setDotPhysicalExpirationDate(dotExp);

                employee.setForkliftCertified(forkliftBox.isSelected());
                employee.setForkliftCertificationExpirationDate(forkliftExp);

                employee.setEmergencyContactName(emergencyName);
                employee.setEmergencyContactPhone(emergencyPhone);
                employee.setEmergencyContactRelationship(emergencyRelation);

                manager.addEmployee(employee);

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
            String future = current.substring(0, offset) + (text == null ? "" : text)
                    + current.substring(offset + length);

            if (future.matches("\\d*(\\.\\d{0,2})?")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}