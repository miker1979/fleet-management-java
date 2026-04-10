import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TimeOffRequestFormUI extends JFrame {

    private FleetManager manager;

    private JTextField employeeIdField;
    private JTextField employeeNameField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> requestTypeCombo;
    private JTextArea reasonArea;

    public TimeOffRequestFormUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Time Off Request");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Submit Time Off Request");
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

        employeeIdField = new JTextField(20);
        employeeNameField = new JTextField(20);
        employeeNameField.setEditable(false);

        startDateField = new JTextField("2026-03-26", 20);
        endDateField = new JTextField("2026-03-27", 20);

        requestTypeCombo = new JComboBox<>(new String[]{
                "Vacation",
                "Sick",
                "Personal",
                "Unpaid"
        });

        reasonArea = new JTextArea(5, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        employeeIdField.addActionListener(e -> fillEmployeeName());

        int row = 0;
        addRow(formPanel, gbc, row++, "Employee ID:", employeeIdField);
        addRow(formPanel, gbc, row++, "Employee Name:", employeeNameField);
        addRow(formPanel, gbc, row++, "Start Date:", startDateField);
        addRow(formPanel, gbc, row++, "End Date:", endDateField);
        addRow(formPanel, gbc, row++, "Request Type:", requestTypeCombo);
        addRow(formPanel, gbc, row++, "Reason:", new JScrollPane(reasonArea));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton cancelButton = new JButton("Cancel");
        JButton submitButton = new JButton("Submit Request");

        submitButton.setBackground(new Color(60, 90, 160));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);

        cancelButton.addActionListener(e -> dispose());
        submitButton.addActionListener(e -> submitRequest());

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

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

    private void fillEmployeeName() {
        try {
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            Employee employee = manager.findEmployeeById(employeeId);

            if (employee != null) {
                employeeNameField.setText(employee.getFullName());
            } else {
                employeeNameField.setText("Employee not found");
            }
        } catch (NumberFormatException ex) {
            employeeNameField.setText("Invalid ID");
        }
    }

    private void submitRequest() {
        String employeeIdText = employeeIdField.getText().trim();
        String employeeName = employeeNameField.getText().trim();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        String requestType = (String) requestTypeCombo.getSelectedItem();
        String reason = reasonArea.getText().trim();

        if (employeeIdText.isEmpty() || employeeName.isEmpty() ||
                startDate.isEmpty() || endDate.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        if (employeeName.equals("Employee not found") || employeeName.equals("Invalid ID")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid employee ID.");
            return;
        }

        int employeeId;
        try {
            employeeId = Integer.parseInt(employeeIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a valid number.");
            return;
        }

        int requestId = manager.getNextTimeOffRequestId();

        TimeOffRequest request = new TimeOffRequest(
                requestId,
                employeeId,
                employeeName,
                startDate,
                endDate,
                requestType,
                reason,
                "Pending"
        );

        manager.addTimeOffRequest(request);

        JOptionPane.showMessageDialog(this, "Time off request submitted successfully.");

        request.displayRequest();

        dispose();
    }
}