import javax.swing.*;
import java.awt.*;

public class EvaluationFormUI extends JFrame {

    private FleetManager manager;

    public EvaluationFormUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Employee Evaluation Form");
        setSize(650, 750);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 2, 10, 10));

        // Fields
        JTextField empNameField = new JTextField();
        empNameField.setEditable(false);

        JTextField empIdField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Yearly"});
        JTextField dateField = new JTextField("2026-01-01");
        JTextField evaluatorField = new JTextField();

        // Ratings
        JComboBox<Integer> rating1 = createRatingBox();
        JComboBox<Integer> rating2 = createRatingBox();
        JComboBox<Integer> rating3 = createRatingBox();
        JComboBox<Integer> rating4 = createRatingBox();
        JComboBox<Integer> rating5 = createRatingBox();
        JComboBox<Integer> rating6 = createRatingBox();

        // Comments
        JTextField c1 = new JTextField();
        JTextField c2 = new JTextField();
        JTextField c3 = new JTextField();
        JTextField c4 = new JTextField();
        JTextField c5 = new JTextField();
        JTextField c6 = new JTextField();

        // Auto-fill employee name from ID
        empIdField.addActionListener(e -> {
            try {
                int id = Integer.parseInt(empIdField.getText().trim());
                Employee emp = manager.findEmployeeById(id);

                if (emp != null) {
                    empNameField.setText(emp.getFullName());
                } else {
                    empNameField.setText("Employee not found");
                }
            } catch (NumberFormatException ex) {
                empNameField.setText("Invalid ID");
            }
        });

        // UI Layout
        add(new JLabel("Employee Name:"));
        add(empNameField);

        add(new JLabel("Employee ID:"));
        add(empIdField);

        add(new JLabel("Evaluation Type:"));
        add(typeBox);

        add(new JLabel("Date:"));
        add(dateField);

        add(new JLabel("Evaluator:"));
        add(evaluatorField);

        add(new JLabel("Job Knowledge:"));
        add(rating1);
        add(new JLabel("Comment:"));
        add(c1);

        add(new JLabel("Work Quality:"));
        add(rating2);
        add(new JLabel("Comment:"));
        add(c2);

        add(new JLabel("Attendance:"));
        add(rating3);
        add(new JLabel("Comment:"));
        add(c3);

        add(new JLabel("Productivity:"));
        add(rating4);
        add(new JLabel("Comment:"));
        add(c4);

        add(new JLabel("Communication:"));
        add(rating5);
        add(new JLabel("Comment:"));
        add(c5);

        add(new JLabel("Dependability:"));
        add(rating6);
        add(new JLabel("Comment:"));
        add(c6);

        JButton saveBtn = new JButton("Save Evaluation");

        saveBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(empIdField.getText().trim());
                Employee emp = manager.findEmployeeById(empId);

                if (emp == null) {
                    JOptionPane.showMessageDialog(this, "Employee not found!");
                    return;
                }

                Evaluation eval = new Evaluation(
                        (int) (Math.random() * 10000),
                        empId,
                        (String) typeBox.getSelectedItem(),
                        dateField.getText().trim(),
                        evaluatorField.getText().trim(),
                        (int) rating1.getSelectedItem(),
                        (int) rating2.getSelectedItem(),
                        (int) rating3.getSelectedItem(),
                        (int) rating4.getSelectedItem(),
                        (int) rating5.getSelectedItem(),
                        (int) rating6.getSelectedItem()
                );

                eval.setComments(
                        c1.getText().trim(),
                        c2.getText().trim(),
                        c3.getText().trim(),
                        c4.getText().trim(),
                        c5.getText().trim(),
                        c6.getText().trim()
                );

                emp.addEvaluation(eval);
                JOptionPane.showMessageDialog(this, "Evaluation Saved!");

                // Optional: clear form after save
                empIdField.setText("");
                empNameField.setText("");
                evaluatorField.setText("");
                dateField.setText("2026-01-01");
                c1.setText("");
                c2.setText("");
                c3.setText("");
                c4.setText("");
                c5.setText("");
                c6.setText("");
                rating1.setSelectedIndex(0);
                rating2.setSelectedIndex(0);
                rating3.setSelectedIndex(0);
                rating4.setSelectedIndex(0);
                rating5.setSelectedIndex(0);
                rating6.setSelectedIndex(0);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving evaluation.");
            }
        });

        add(new JLabel(""));
        add(saveBtn);
    }

    private JComboBox<Integer> createRatingBox() {
        return new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
    }
}