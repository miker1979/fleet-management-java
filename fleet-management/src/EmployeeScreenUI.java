import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EmployeeScreenUI extends JFrame {

    private FleetManager manager;
    private JList<String> employeeList;
    private DefaultListModel<String> listModel;
    private JTextArea detailArea;

    public EmployeeScreenUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Employee Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Employees", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // LEFT: Employee List
        listModel = new DefaultListModel<>();
        employeeList = new JList<>(listModel);
        employeeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(employeeList);
        listScroll.setPreferredSize(new Dimension(250, 400));
        add(listScroll, BorderLayout.WEST);

        // RIGHT: Employee Details
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane detailScroll = new JScrollPane(detailArea);
        add(detailScroll, BorderLayout.CENTER);

        // BOTTOM: Buttons
        JPanel buttonPanel = new JPanel();

        JButton viewEvalBtn = new JButton("View Evaluations");
        JButton addEvalBtn = new JButton("Add Evaluation");
        JButton addEmployeeBtn = new JButton("Create Employee");
        JButton refreshBtn = new JButton("Refresh");

        buttonPanel.add(viewEvalBtn);
        buttonPanel.add(addEvalBtn);
        buttonPanel.add(addEmployeeBtn);
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load employees when screen opens
        loadEmployees();

        // Click employee in list -> show details
        employeeList.addListSelectionListener(e -> showEmployeeDetails());

        // View evaluations button
        viewEvalBtn.addActionListener(e -> showEvaluations());

        // Add evaluation button
        addEvalBtn.addActionListener(e -> {
            new EvaluationFormUI(manager).setVisible(true);
        });

        // Create employee button
        addEmployeeBtn.addActionListener(e -> {
            new CreateEmployeeUI(manager, this).setVisible(true);
        });

        // Refresh button
        refreshBtn.addActionListener(e -> {
            refreshEmployeeList();
        });
    }

    private void loadEmployees() {
        listModel.clear();

        ArrayList<Employee> employees = manager.getEmployees();

        if (employees.isEmpty()) {
            listModel.addElement("No employees found");
            return;
        }

        for (Employee emp : employees) {
            listModel.addElement(emp.getEmployeeId() + " - " + emp.getFullName());
        }
    }

    public void refreshEmployeeList() {
        loadEmployees();
        detailArea.setText("");
    }

    private Employee getSelectedEmployee() {
        int index = employeeList.getSelectedIndex();

        if (index == -1) {
            return null;
        }

        if (manager.getEmployees().isEmpty()) {
            return null;
        }

        if (index >= manager.getEmployees().size()) {
            return null;
        }

        return manager.getEmployees().get(index);
    }

    private void showEmployeeDetails() {
        Employee emp = getSelectedEmployee();

        if (emp == null) {
            detailArea.setText("");
            return;
        }

        String info = "";
        info += "ID: " + emp.getEmployeeId() + "\n";
        info += "Name: " + emp.getFullName() + "\n";
        info += "Position: " + emp.getPosition() + "\n";
        info += "Department: " + emp.getDepartment() + "\n";
        info += "Address: " + emp.getAddress() + "\n";
        info += "Phone: " + emp.getPhoneNumber() + "\n";
        info += "Email: " + emp.getEmail() + "\n";
        info += "Hire Date: " + emp.getHireDate() + "\n";
        info += "Status: " + (emp.isActive() ? "Active" : "Inactive") + "\n";
        info += "Pay Rate: $" + emp.getPayRate() + "\n";
        info += "Average Evaluation Score: " + emp.getAverageEvaluationScore() + "\n";

        detailArea.setText(info);
    }

    private void showEvaluations() {
        Employee emp = getSelectedEmployee();

        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (emp.getEvaluations().isEmpty()) {
            sb.append("No evaluations found for ").append(emp.getFullName()).append(".");
        } else {
            sb.append("Evaluations for ").append(emp.getFullName()).append(":\n\n");

            for (Evaluation eval : emp.getEvaluations()) {
                sb.append("Type: ").append(eval.getEvaluationType()).append("\n");
                sb.append("Date: ").append(eval.getEvaluationDate()).append("\n");
                sb.append("Evaluator: ").append(eval.getEvaluatorName()).append("\n");
                sb.append("Overall Score: ").append(eval.getOverallScore()).append("\n");
                sb.append("-----------------------------\n");
            }
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }
}