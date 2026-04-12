import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JobScreenUI extends JFrame {
    private FleetManager manager;

    private JComboBox<String> jobCombo; // 🔥 NEW
    private JComboBox<String> typeCombo;
    private JComboBox<String> contractorCombo;
    private JComboBox<String> foremanCombo;
    private JComboBox<String> statusCombo;
    private JComboBox<String> hourCombo;
    private JComboBox<String> minuteCombo;

    private JTextField dateField;
    private JTextField locationField;
    private JTextField truckField;

    public JobScreenUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Dispatch Task");
        setSize(560, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Dispatch Task");
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

        String[] types = {"Barrier Install", "Barrier Removal", "Relocation", "Crash Absorber"};
        String[] contractors = {"Pulice", "Sundt", "Kiewit", "Howe Precast", "ADOT"};
        String[] foremen = {"Mike Robinson", "Foreman Joe", "Foreman Steve"};
        String[] statuses = {"Scheduled", "Waiting for Closure", "In Progress", "Completed"};

        String[] hours = {
                "0100","0200","0300","0400","0500","0600",
                "0700","0800","0900","1000","1100","1200",
                "1300","1400","1500","1600","1700","1800",
                "1900","2000","2100","2200","2300","0000"
        };

        String[] minutes = {"00","15","30","45"};

        // 🔥 BUILD JOB DROPDOWN
        jobCombo = new JComboBox<>();
        for (Job job : manager.getJobs()) {
            jobCombo.addItem(job.getJobId() + " - " + job.getJobName());
        }

        typeCombo = new JComboBox<>(types);
        contractorCombo = new JComboBox<>(contractors);
        foremanCombo = new JComboBox<>(foremen);
        statusCombo = new JComboBox<>(statuses);

        dateField = new JTextField("2026-04-10", 20);
        locationField = new JTextField(20);
        truckField = new JTextField("Truck 101, Truck 202", 20);

        hourCombo = new JComboBox<>(hours);
        minuteCombo = new JComboBox<>(minutes);

        int row = 0;
        addRow(formPanel, gbc, row++, "Job:", jobCombo); // 🔥 NEW ROW
        addRow(formPanel, gbc, row++, "Task Type:", typeCombo);
        addRow(formPanel, gbc, row++, "Contractor:", contractorCombo);
        addRow(formPanel, gbc, row++, "Dispatch Date (YYYY-MM-DD):", dateField);
        addTimeRow(formPanel, gbc, row++, "Start Time:", hourCombo, minuteCombo);
        addRow(formPanel, gbc, row++, "Location:", locationField);
        addRow(formPanel, gbc, row++, "Foreman:", foremanCombo);
        addRow(formPanel, gbc, row++, "Assigned Trucks:", truckField);
        addRow(formPanel, gbc, row++, "Initial Status:", statusCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn = new JButton("Dispatch Task");

        saveBtn.setBackground(new Color(60, 90, 160));
        saveBtn.setForeground(Color.WHITE);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> saveAction());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addTimeRow(JPanel panel, GridBagConstraints gbc, int row, String labelText,
                            JComboBox<String> hourCombo, JComboBox<String> minuteCombo) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        timePanel.setBackground(Color.WHITE);
        timePanel.add(hourCombo);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteCombo);

        gbc.gridx = 1;
        panel.add(timePanel, gbc);
    }

    private void saveAction() {
        String selectedJob = (String) jobCombo.getSelectedItem();

        if (selectedJob == null) {
            JOptionPane.showMessageDialog(this, "No jobs available. Create a job first.");
            return;
        }

        int jobId = Integer.parseInt(selectedJob.split(" - ")[0]); // 🔥 extract ID

        String dispatchDate = dateField.getText().trim();
        String location = locationField.getText().trim();
        String assignedTrucks = truckField.getText().trim();

        String hourRaw = (String) hourCombo.getSelectedItem();
        String minute = (String) minuteCombo.getSelectedItem();
        String startTime = hourRaw.substring(0, 2) + ":" + minute;

        Task newTask = new Task(
                manager.getTasks().size() + 1001,
                jobId, // 🔥 LINKED
                dispatchDate,
                startTime,
                (String) typeCombo.getSelectedItem(),
                (String) contractorCombo.getSelectedItem(),
                location,
                (String) foremanCombo.getSelectedItem(),
                assignedTrucks,
                (String) statusCombo.getSelectedItem()
        );

        manager.addTask(newTask);

        JOptionPane.showMessageDialog(this, "Task dispatched successfully.");
        dispose();
    }
}