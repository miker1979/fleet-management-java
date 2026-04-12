import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AssignTruckUI extends JFrame {

    private FleetManager manager;

    private JComboBox<String> employeeCombo;
    private JComboBox<String> truckCombo;
    private JLabel currentTruckLabel;

    public AssignTruckUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Assign Truck to Employee");
        setSize(560, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Assign Truck");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        employeeCombo = new JComboBox<>();
        for (Employee emp : manager.getEmployees()) {
            employeeCombo.addItem(emp.getEmployeeId() + " - " + emp.getFullName());
        }

        truckCombo = new JComboBox<>();
        truckCombo.addItem("None");
        for (Truck truck : manager.getTrucks()) {
            truckCombo.addItem(truck.getTruckID());
        }

        currentTruckLabel = new JLabel("None");
        currentTruckLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        employeeCombo.addActionListener(e -> loadSelectedEmployeeAssignment());

        formPanel.add(new JLabel("Employee:"));
        formPanel.add(employeeCombo);
        formPanel.add(new JLabel("Current Assignment:"));
        formPanel.add(currentTruckLabel);
        formPanel.add(new JLabel("Select Truck:"));
        formPanel.add(truckCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton assignBtn = new JButton("Assign Truck");
        JButton clearBtn = new JButton("Clear Assignment");
        JButton closeBtn = new JButton("Close");

        assignBtn.setBackground(new Color(60, 90, 160));
        assignBtn.setForeground(Color.WHITE);
        assignBtn.setFocusPainted(false);

        clearBtn.setFocusPainted(false);
        closeBtn.setFocusPainted(false);

        assignBtn.addActionListener(e -> assignTruck());
        clearBtn.addActionListener(e -> clearTruckAssignment());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadSelectedEmployeeAssignment();
    }

    private void loadSelectedEmployeeAssignment() {
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            currentTruckLabel.setText("None");
            truckCombo.setSelectedItem("None");
            return;
        }

        String assignedTruck = employee.getAssignedTruckId();
        if (assignedTruck == null || assignedTruck.trim().isEmpty()) {
            currentTruckLabel.setText("None");
            truckCombo.setSelectedItem("None");
        } else {
            currentTruckLabel.setText(assignedTruck);
            truckCombo.setSelectedItem(assignedTruck);
        }
    }

    private Employee getSelectedEmployee() {
        String selectedEmployee = (String) employeeCombo.getSelectedItem();

        if (selectedEmployee == null) {
            return null;
        }

        int employeeId = Integer.parseInt(selectedEmployee.split(" - ")[0]);
        return manager.findEmployeeById(employeeId);
    }

    private void assignTruck() {
        Employee employee = getSelectedEmployee();
        String selectedTruck = (String) truckCombo.getSelectedItem();

        if (employee == null || selectedTruck == null || selectedTruck.equals("None")) {
            JOptionPane.showMessageDialog(this, "Select an employee and a truck.");
            return;
        }

        Employee alreadyAssigned = findEmployeeAssignedToTruck(selectedTruck);

        if (alreadyAssigned != null && alreadyAssigned.getEmployeeId() != employee.getEmployeeId()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Truck " + selectedTruck + " is already assigned to " + alreadyAssigned.getFullName() + "."
            );
            return;
        }

        employee.setAssignedTruckId(selectedTruck);
        currentTruckLabel.setText(selectedTruck);

        JOptionPane.showMessageDialog(
                this,
                "Truck " + selectedTruck + " assigned to " + employee.getFullName() + "."
        );
    }

    private void clearTruckAssignment() {
        Employee employee = getSelectedEmployee();

        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        employee.setAssignedTruckId("");
        currentTruckLabel.setText("None");
        truckCombo.setSelectedItem("None");

        JOptionPane.showMessageDialog(
                this,
                "Truck assignment cleared for " + employee.getFullName() + "."
        );
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
}