import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EvaluationFormUI extends JFrame {

    private FleetManager manager;

    public EvaluationFormUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Employee Evaluation Form");
        setSize(700, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // =========================
        // HEADER
        // =========================
        JLabel header = new JLabel("Employee Evaluation");
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(header);
        mainPanel.add(Box.createVerticalStrut(20));

        // =========================
        // EMPLOYEE INFO
        // =========================
        JTextField empNameField = new JTextField();
        empNameField.setEditable(false);

        JTextField empIdField = new JTextField();
        JTextField evaluatorField = new JTextField();
        JTextField dateField = new JTextField("2026-01-01");

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Yearly"});

        empIdField.addActionListener(e -> {
            try {
                int id = Integer.parseInt(empIdField.getText().trim());
                Employee emp = manager.findEmployeeById(id);

                if (emp != null) {
                    empNameField.setText(emp.getFullName());
                } else {
                    empNameField.setText("Employee not found");
                }
            } catch (Exception ex) {
                empNameField.setText("Invalid ID");
            }
        });

        mainPanel.add(createField("Employee ID", empIdField));
        mainPanel.add(createField("Employee Name", empNameField));
        mainPanel.add(createField("Evaluator", evaluatorField));
        mainPanel.add(createField("Date", dateField));
        mainPanel.add(createField("Evaluation Type", typeBox));

        mainPanel.add(Box.createVerticalStrut(20));

        // =========================
        // RATINGS
        // =========================
        mainPanel.add(createSectionLabel("Performance Ratings"));

        JComboBox<Integer> r1 = createRatingBox();
        JComboBox<Integer> r2 = createRatingBox();
        JComboBox<Integer> r3 = createRatingBox();
        JComboBox<Integer> r4 = createRatingBox();
        JComboBox<Integer> r5 = createRatingBox();
        JComboBox<Integer> r6 = createRatingBox();

        JTextField c1 = new JTextField();
        JTextField c2 = new JTextField();
        JTextField c3 = new JTextField();
        JTextField c4 = new JTextField();
        JTextField c5 = new JTextField();
        JTextField c6 = new JTextField();

        mainPanel.add(createRatingRow("Job Knowledge", r1, c1));
        mainPanel.add(createRatingRow("Work Quality", r2, c2));
        mainPanel.add(createRatingRow("Attendance", r3, c3));
        mainPanel.add(createRatingRow("Productivity", r4, c4));
        mainPanel.add(createRatingRow("Communication", r5, c5));
        mainPanel.add(createRatingRow("Dependability", r6, c6));

        mainPanel.add(Box.createVerticalStrut(20));

        // =========================
        // SAVE BUTTON
        // =========================
        JButton saveBtn = new JButton("Save Evaluation");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(empIdField.getText().trim());
                Employee emp = manager.findEmployeeById(empId);

                if (emp == null) {
                    JOptionPane.showMessageDialog(this, "Employee not found!");
                    return;
                }

                Evaluation eval = new Evaluation(
                        (int)(Math.random() * 10000),
                        empId,
                        (String) typeBox.getSelectedItem(),
                        dateField.getText().trim(),
                        evaluatorField.getText().trim(),
                        (int) r1.getSelectedItem(),
                        (int) r2.getSelectedItem(),
                        (int) r3.getSelectedItem(),
                        (int) r4.getSelectedItem(),
                        (int) r5.getSelectedItem(),
                        (int) r6.getSelectedItem()
                );

                eval.setComments(
                        c1.getText(),
                        c2.getText(),
                        c3.getText(),
                        c4.getText(),
                        c5.getText(),
                        c6.getText()
                );

                emp.addEvaluation(eval);

                JOptionPane.showMessageDialog(this, "Evaluation Saved!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving evaluation.");
            }
        });

        mainPanel.add(saveBtn);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);
    }

    // =========================
    // HELPERS
    // =========================
    private JPanel createField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRatingRow(String label, JComboBox<Integer> rating, JTextField comment) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        panel.add(new JLabel(label));
        panel.add(rating);
        panel.add(new JLabel("Comment"));
        panel.add(comment);

        return panel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        return label;
    }

    private JComboBox<Integer> createRatingBox() {
        return new JComboBox<>(new Integer[]{1,2,3,4,5});
    }
}