import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreateEmployeeUI extends JFrame {

    private FleetManager manager;

    public CreateEmployeeUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create Employee");
        setSize(700, 760);
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
                "Owner",
                "Admin",
                "Safety",
                "Office"
        });

        JComboBox<String> departmentCombo = new JComboBox<>(new String[]{
                "Transportation",
                "Foreman",
                "Mechanic",
                "Admin",
                "Operations",
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

        Component[] fields = {
                idField, firstNameField, lastNameField, positionCombo,
                departmentCombo, addressField, phoneField, emailField,
                hireDateField, payRateField, payTypeCombo, assignedTruckCombo
        };

        for (Component c : fields) {
            c.setFont(fieldFont);
        }

        activeBox.setFont(fieldFont);

        addField(formPanel, "Employee ID:", idField, labelFont);
        addField(formPanel, "First Name:", firstNameField, labelFont);
        addField(formPanel, "Last Name:", lastNameField, labelFont);
        addField(formPanel, "Position:", positionCombo, labelFont);
        addField(formPanel, "Department:", departmentCombo, labelFont);
        addField(formPanel, "Address:", addressField, labelFont);
        addField(formPanel, "Phone Number:", phoneField, labelFont);
        addField(formPanel, "Email:", emailField, labelFont);
        addField(formPanel, "Hire Date:", hireDateField, labelFont);
        addField(formPanel, "Pay Amount:", payRateField, labelFont);
        addField(formPanel, "Pay Type:", payTypeCombo, labelFont);
        addField(formPanel, "Assigned Truck:", assignedTruckCombo, labelFont);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(labelFont);
        formPanel.add(statusLabel);
        formPanel.add(activeBox);

        phoneField.addCaretListener(e -> {
            String current = phoneField.getText();
            String formatted = formatPhoneNumber(current);
            if (!current.equals(formatted)) {
                SwingUtilities.invokeLater(() -> {
                    phoneField.setText(formatted);
                    phoneField.setCaretPosition(phoneField.getText().length());
                });
            }
        });

        payRateField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                try {
                    String text = payRateField.getText().trim().replace("$", "").replace(",", "");

                    if (!text.isEmpty()) {
                        double value = Double.parseDouble(text);
                        payRateField.setText(String.format("%.2f", value));
                    }

                } catch (Exception ex) {
                    payRateField.setText("");
                }
            }
        });

        JButton saveBtn = new JButton("Save Employee");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setFont(labelFont);
        cancelBtn.setFont(labelFont);

        saveBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String position = (String) positionCombo.getSelectedItem();
                String department = (String) departmentCombo.getSelectedItem();
                String address = addressField.getText().trim();
                String phone = formatPhoneNumber(phoneField.getText().trim());
                String email = emailField.getText().trim();
                String hireDate = hireDateField.getText().trim();
                String payType = (String) payTypeCombo.getSelectedItem();
                String assignedTruck = (String) assignedTruckCombo.getSelectedItem();
                boolean active = activeBox.isSelected();

                if (firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "First and last name are required.");
                    return;
                }

                for (Employee existing : manager.getEmployees()) {
                    if (existing.getEmployeeId() == id) {
                        JOptionPane.showMessageDialog(this, "Employee ID already exists.");
                        return;
                    }
                }

                String digitsOnly = phone.replaceAll("\\D", "");
                if (digitsOnly.length() != 10) {
                    JOptionPane.showMessageDialog(this, "Phone number must have 10 digits.");
                    return;
                }

                String payText = payRateField.getText().trim().replace("$", "").replace(",", "");
                if (payText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Pay amount is required.");
                    return;
                }

                double payRate = new BigDecimal(payText)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

                if (assignedTruck != null && !assignedTruck.equalsIgnoreCase("None")) {
                    Employee alreadyAssigned = findEmployeeAssignedToTruck(assignedTruck);
                    if (alreadyAssigned != null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Truck " + assignedTruck + " is already assigned to " + alreadyAssigned.getFullName() + "."
                        );
                        return;
                    }
                }

                Employee employee = new Employee(
                        id,
                        firstName,
                        lastName,
                        position,
                        department,
                        address,
                        phone,
                        email,
                        hireDate,
                        active,
                        payRate
                );

                if (assignedTruck != null && !assignedTruck.equalsIgnoreCase("None")) {
                    employee.setAssignedTruckId(assignedTruck);
                }

                manager.addEmployee(employee);

                JOptionPane.showMessageDialog(
                        this,
                        "Employee created successfully.\nPay: " + formatPayDisplay(payRate, payType)
                );
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Employee ID and pay amount must be valid numbers.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating employee: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveBtn);
        bottomPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, String label, Component field, Font font) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(font);
        panel.add(lbl);
        panel.add(field);
    }

    private String formatPhoneNumber(String phone) {
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() > 10) {
            digits = digits.substring(0, 10);
        }

        if (digits.length() <= 3) {
            return digits;
        } else if (digits.length() <= 6) {
            return digits.substring(0, 3) + "-" + digits.substring(3);
        } else {
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        }
    }

    private Employee findEmployeeAssignedToTruck(String truckId) {
        for (Employee employee : manager.getEmployees()) {
            if (employee.getAssignedTruckId() != null &&
                    employee.getAssignedTruckId().equalsIgnoreCase(truckId)) {
                return employee;
            }
        }
        return null;
    }

    private String formatPayDisplay(double payRate, String payType) {
        String amount = String.format("$%,.2f", payRate);

        if ("Hourly".equalsIgnoreCase(payType)) {
            return amount + " per hour";
        } else if ("Salary Per Year".equalsIgnoreCase(payType)) {
            return amount + " per year";
        } else if ("Salary Per Month".equalsIgnoreCase(payType)) {
            return amount + " per month";
        } else if ("Salary Per Week".equalsIgnoreCase(payType)) {
            return amount + " per week";
        }

        return amount;
    }
}