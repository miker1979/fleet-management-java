import javax.swing.*;
import java.awt.*;

public class JobScreenUI extends JFrame {

    private FleetManager manager;

    private JTextField jobIdField;
    private JTextField jobNameField;
    private JTextField contractorField;
    private JTextField locationField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField statusField;
    private JTextField projectManagerField;
    private JTextField dotProjectNumberField;
    private JTextField barrierTypeField;
    private JTextField totalLinearFeetField;
    private JTextArea notesArea;

    public JobScreenUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create New Job");
        setSize(700, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Create New Job", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        jobIdField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Job ID:", jobIdField);

        jobNameField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Job Name:", jobNameField);

        contractorField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Contractor:", contractorField);

        locationField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Location:", locationField);

        startDateField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Start Date:", startDateField);

        endDateField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Estimated Completion:", endDateField);

        statusField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Status:", statusField);

        projectManagerField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Project Manager:", projectManagerField);

        dotProjectNumberField = new JTextField();
        addFormRow(formPanel, gbc, row++, "DOT Project Number:", dotProjectNumberField);

        barrierTypeField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Barrier Type:", barrierTypeField);

        totalLinearFeetField = new JTextField();
        addFormRow(formPanel, gbc, row++, "Total Linear Feet:", totalLinearFeetField);

        notesArea = new JTextArea(5, 20);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        addFormRow(formPanel, gbc, row++, "Notes:", notesScrollPane);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save Job");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveJob());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void saveJob() {
        try {
            int jobId = Integer.parseInt(jobIdField.getText().trim());
            String jobName = jobNameField.getText().trim();
            String contractor = contractorField.getText().trim();
            String location = locationField.getText().trim();
            String startDate = startDateField.getText().trim();
            String estimatedCompletion = endDateField.getText().trim();
            String status = statusField.getText().trim();
            String projectManager = projectManagerField.getText().trim();
            String dotProjectNumber = dotProjectNumberField.getText().trim();
            String barrierType = barrierTypeField.getText().trim();
            int totalLinearFeet = Integer.parseInt(totalLinearFeetField.getText().trim());
            String notes = notesArea.getText().trim();

            Job job = new Job(
                    jobId,
                    jobName,
                    contractor,
                    location,
                    startDate,
                    estimatedCompletion,
                    status,
                    projectManager,
                    dotProjectNumber,
                    barrierType,
                    totalLinearFeet,
                    notes
            );

            manager.addJob(job);

            JOptionPane.showMessageDialog(this, "Job saved successfully.");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Job ID and Total Linear Feet must be numbers.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving job: " + ex.getMessage());
        }
    }
}