import javax.swing.*;
import java.awt.*;

public class CreateEmployeeUI extends JFrame {

    private FleetManager manager;

    public CreateEmployeeUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create Employee");
        setSize(550, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField departmentField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField hireDateField = new JTextField("2026-01-01");
        JTextField payRateField = new JTextField();
        JCheckBox activeBox = new JCheckBox("Active", true);

        formPanel.add(new JLabel("Employee ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Position:"));
        formPanel.add(positionField);

        formPanel.add(new JLabel("Department:"));
        formPanel.add(departmentField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Hire Date:"));
        formPanel.add(hireDateField);

        formPanel.add(new JLabel("Pay Rate:"));
        formPanel.add(payRateField);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(activeBox);

        JButton saveBtn = new JButton("Save Employee");

        saveBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String position = positionField.getText().trim();
                String department = departmentField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String hireDate = hireDateField.getText().trim();
                double payRate = Double.parseDouble(payRateField.getText().trim());
                boolean active = activeBox.isSelected();

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

                manager.addEmployee(employee);

                JOptionPane.showMessageDialog(this, "Employee created successfully!");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating employee. Check your input.");
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveBtn);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}