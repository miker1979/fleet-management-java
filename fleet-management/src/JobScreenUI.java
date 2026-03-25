import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JobScreenUI extends JFrame {

    private FleetManager manager;

    public JobScreenUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Job Management");
        setSize(750, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Job Management Screen");
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(header);
        mainPanel.add(Box.createVerticalStrut(20));

        JTextField jobIdField = new JTextField();
        JTextField jobNameField = new JTextField();
        JTextField contractingCompanyField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField startDateField = new JTextField("2026-01-01");
        JTextField completionDateField = new JTextField("2028-12-31");
        JTextField projectManagerField = new JTextField();
        JTextField dotProjectField = new JTextField();
        JTextField totalLFField = new JTextField();

        JComboBox<String> statusBox = new JComboBox<>(new String[]{
                "Planned", "In Progress", "On Hold", "Completed", "Cancelled"
        });

        JComboBox<String> barrierTypeBox = new JComboBox<>(new String[]{
                "AZDOT F-Shape",
                "Temporary Concrete Barrier",
                "Water Barrier",
                "Guardrail",
                "Crash Cushion"
        });

        JTextArea notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        mainPanel.add(createField("Job ID", jobIdField));
        mainPanel.add(createField("Job Name", jobNameField));
        mainPanel.add(createField("Contracting Company", contractingCompanyField));
        mainPanel.add(createField("Location", locationField));
        mainPanel.add(createField("Start Date", startDateField));
        mainPanel.add(createField("Estimated Completion Date", completionDateField));
        mainPanel.add(createField("Project Manager", projectManagerField));
        mainPanel.add(createField("DOT Project Number", dotProjectField));
        mainPanel.add(createField("Barrier Type", barrierTypeBox));
        mainPanel.add(createField("Total Linear Feet", totalLFField));
        mainPanel.add(createField("Status", statusBox));

        JPanel notesPanel = new JPanel(new BorderLayout(5, 5));
        notesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        notesPanel.setBackground(Color.WHITE);

        JLabel notesLabel = new JLabel("Notes");
        notesLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        notesPanel.add(notesLabel, BorderLayout.NORTH);
        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        mainPanel.add(notesPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save Job");
        JButton clearBtn = new JButton("Clear Form");
        JButton closeBtn = new JButton("Close");

        saveBtn.setPreferredSize(new Dimension(140, 40));
        clearBtn.setPreferredSize(new Dimension(140, 40));
        closeBtn.setPreferredSize(new Dimension(140, 40));

        saveBtn.addActionListener(e -> {
            try {
                int jobId = Integer.parseInt(jobIdField.getText().trim());
                int totalLF = Integer.parseInt(totalLFField.getText().trim());

                Job job = new Job(
                        jobId,
                        jobNameField.getText().trim(),
                        contractingCompanyField.getText().trim(),
                        locationField.getText().trim(),
                        startDateField.getText().trim(),
                        completionDateField.getText().trim(),
                        (String) statusBox.getSelectedItem(),
                        projectManagerField.getText().trim(),
                        dotProjectField.getText().trim(),
                        (String) barrierTypeBox.getSelectedItem(),
                        totalLF,
                        notesArea.getText().trim()
                );

                manager.addJob(job);
                JOptionPane.showMessageDialog(this, "Job saved successfully!");
                job.displayJob();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Job ID and Total Linear Feet must be numbers.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving job.");
            }
        });

        clearBtn.addActionListener(e -> {
            jobIdField.setText("");
            jobNameField.setText("");
            contractingCompanyField.setText("");
            locationField.setText("");
            startDateField.setText("2026-01-01");
            completionDateField.setText("2028-12-31");
            projectManagerField.setText("");
            dotProjectField.setText("");
            totalLFField.setText("");
            statusBox.setSelectedIndex(0);
            barrierTypeBox.setSelectedIndex(0);
            notesArea.setText("");
        });

        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);
    }

    private JPanel createField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }
}