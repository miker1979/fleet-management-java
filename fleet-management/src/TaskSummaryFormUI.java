import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TaskSummaryFormUI extends JDialog {

    private final FleetManager manager;
    private final Task task;
    private final TaskSummary summary;

    private JTextField foremanField;
    private JTextField scheduledDateField;
    private JTextField scheduledStartTimeField;
    private JTextField locationField;
    private JTextField jobTypeField;
    private JTextField barrierLengthField;

    private JSpinner actualStartTimeSpinner;
    private JSpinner finishTimeSpinner;

    private JSpinner barrierCountSpinner;
    private JSpinner absorbSetSpinner;
    private JTextField absorbStyleField;
    private JTextField absorbLengthField;

    private JSpinner workerCountSpinner;
    private JSpinner hoursWorkedSpinner;

    private JTextField driversOnJobField;
    private JTextField forkliftStatusField;

    private JTextArea delayNotesArea;
    private JTextArea breakdownNotesArea;
    private JTextArea equipmentIssueNotesArea;
    private JTextArea materialIssueNotesArea;
    private JTextArea safetyNotesArea;
    private JTextArea finalSummaryArea;

    public TaskSummaryFormUI(JFrame parent, FleetManager manager, Task task) {
        super(parent, "Task Summary - Task #" + task.getTaskId(), true);

        this.manager = manager;
        this.task = task;

        TaskSummary existing = manager.findTaskSummaryByTaskId(task.getTaskId());
        if (existing == null) {
            this.summary = new TaskSummary(task.getTaskId(), String.valueOf(task.getJobId()));
            prefillFromTask();
        } else {
            this.summary = existing;
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 760);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildScrollableForm(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadSummaryValues();
    }

    private void prefillFromTask() {
        summary.setForemanName(task.getForeman());
        summary.setScheduledDate(task.getStartDate());
        summary.setScheduledStartTime(task.getStartTime());
        summary.setLocation(task.getLocation());
        summary.setJobType(task.getJobType());

        if (task.getTcb() != null) {
            summary.setAbsorbLength(String.valueOf(task.getTcb().getLengthFeet()));
        }

        if (task.getTia() != null) {
            summary.setAbsorbStyle(task.getTia().getType());
            summary.setAbsorbSetCount(task.getTia().getSetCount());
        }

        summary.setBarrierCount(task.getLinearFeet());
        summary.setWorkerCount(task.getTotalAssignedPersonnelCount());
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(12, 12, 0, 12));

        JLabel title = new JLabel("Task Completion Summary");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel(
                "Task #" + task.getTaskId()
                        + " | Job #" + task.getJobId()
                        + " | " + safe(task.getJobType())
                        + " | " + safe(task.getLocation())
        );

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        return header;
    }

    private JScrollPane buildScrollableForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        foremanField = new JTextField();
        scheduledDateField = new JTextField();
        scheduledStartTimeField = new JTextField();
        locationField = new JTextField();
        jobTypeField = new JTextField();
        barrierLengthField = new JTextField();

        makeReadOnly(foremanField);
        makeReadOnly(scheduledDateField);
        makeReadOnly(scheduledStartTimeField);
        makeReadOnly(locationField);
        makeReadOnly(jobTypeField);
        makeReadOnly(barrierLengthField);

        actualStartTimeSpinner = createTimeSpinner();
        finishTimeSpinner = createTimeSpinner();

        barrierCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        absorbSetSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        absorbStyleField = new JTextField();
        absorbLengthField = new JTextField();

        workerCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        hoursWorkedSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, 0.25));

        driversOnJobField = new JTextField();
        forkliftStatusField = new JTextField();

        delayNotesArea = createTextArea(3);
        breakdownNotesArea = createTextArea(3);
        equipmentIssueNotesArea = createTextArea(3);
        materialIssueNotesArea = createTextArea(3);
        safetyNotesArea = createTextArea(3);
        finalSummaryArea = createTextArea(5);

        int row = 0;

        addSection(formPanel, gbc, row++, "Scheduled Task Info");
        addField(formPanel, gbc, row++, "Foreman:", foremanField);
        addField(formPanel, gbc, row++, "Scheduled Date:", scheduledDateField);
        addField(formPanel, gbc, row++, "Scheduled Start Time:", scheduledStartTimeField);
        addField(formPanel, gbc, row++, "Location:", locationField);
        addField(formPanel, gbc, row++, "Job Type:", jobTypeField);
        addField(formPanel, gbc, row++, "Barrier Length From Task:", barrierLengthField);

        addSection(formPanel, gbc, row++, "Actual Work Times");
        addField(formPanel, gbc, row++, "Actual Start Time (24hr):", actualStartTimeSpinner);
        addField(formPanel, gbc, row++, "Finish Time (24hr):", finishTimeSpinner);

        addSection(formPanel, gbc, row++, "Materials Completed");
        addField(formPanel, gbc, row++, "Barrier Count / Linear Feet:", barrierCountSpinner);
        addField(formPanel, gbc, row++, "Absorb Sets:", absorbSetSpinner);
        addField(formPanel, gbc, row++, "Absorb Style:", absorbStyleField);
        addField(formPanel, gbc, row++, "Absorb Length:", absorbLengthField);

        addSection(formPanel, gbc, row++, "Crew and Equipment");
        addField(formPanel, gbc, row++, "Worker Count:", workerCountSpinner);
        addField(formPanel, gbc, row++, "Hours Worked:", hoursWorkedSpinner);
        addField(formPanel, gbc, row++, "Drivers on Job:", driversOnJobField);
        addField(formPanel, gbc, row++, "Forklift Status:", forkliftStatusField);

        addSection(formPanel, gbc, row++, "Issues / Notes");
        addTextArea(formPanel, gbc, row++, "Delay Notes:", delayNotesArea);
        addTextArea(formPanel, gbc, row++, "Breakdown Notes:", breakdownNotesArea);
        addTextArea(formPanel, gbc, row++, "Equipment Issue Notes:", equipmentIssueNotesArea);
        addTextArea(formPanel, gbc, row++, "Material Issue Notes:", materialIssueNotesArea);
        addTextArea(formPanel, gbc, row++, "Safety Notes:", safetyNotesArea);
        addTextArea(formPanel, gbc, row++, "Final Summary:", finalSummaryArea);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalStrut(40), gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(860, 620));

        return scrollPane;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));

        JButton saveButton = new JButton("Save Summary");
        JButton closeButton = new JButton("Close");

        saveButton.addActionListener(e -> saveSummary());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private void loadSummaryValues() {
        foremanField.setText(summary.getForemanName());
        scheduledDateField.setText(summary.getScheduledDate());
        scheduledStartTimeField.setText(summary.getScheduledStartTime());
        locationField.setText(summary.getLocation());
        jobTypeField.setText(summary.getJobType());

        if (task.getTcb() != null) {
            barrierLengthField.setText(task.getTcb().getLengthFeet() + " ft");
        } else {
            barrierLengthField.setText("N/A");
        }

        absorbStyleField.setText(summary.getAbsorbStyle());
        absorbLengthField.setText(summary.getAbsorbLength());

        barrierCountSpinner.setValue(summary.getBarrierCount());
        absorbSetSpinner.setValue(summary.getAbsorbSetCount());
        workerCountSpinner.setValue(summary.getWorkerCount());
        hoursWorkedSpinner.setValue(summary.getHoursWorked());

        driversOnJobField.setText(summary.getDriversOnJob());
        forkliftStatusField.setText(summary.getForkliftStatus());

        delayNotesArea.setText(summary.getDelayNotes());
        breakdownNotesArea.setText(summary.getBreakdownNotes());
        equipmentIssueNotesArea.setText(summary.getEquipmentIssueNotes());
        materialIssueNotesArea.setText(summary.getMaterialIssueNotes());
        safetyNotesArea.setText(summary.getSafetyNotes());
        finalSummaryArea.setText(summary.getFinalSummary());
    }

    private void saveSummary() {
        summary.setActualStartTime(formatSpinnerTime(actualStartTimeSpinner));
        summary.setFinishTime(formatSpinnerTime(finishTimeSpinner));

        summary.setBarrierCount(parseInteger(barrierCountSpinner.getValue()));
        summary.setAbsorbSetCount(parseInteger(absorbSetSpinner.getValue()));
        summary.setAbsorbStyle(absorbStyleField.getText().trim());
        summary.setAbsorbLength(absorbLengthField.getText().trim());

        summary.setWorkerCount(parseInteger(workerCountSpinner.getValue()));
        summary.setHoursWorked(parseDouble(hoursWorkedSpinner.getValue()));

        summary.setDriversOnJob(driversOnJobField.getText().trim());
        summary.setForkliftStatus(forkliftStatusField.getText().trim());

        summary.setDelayNotes(delayNotesArea.getText().trim());
        summary.setBreakdownNotes(breakdownNotesArea.getText().trim());
        summary.setEquipmentIssueNotes(equipmentIssueNotesArea.getText().trim());
        summary.setMaterialIssueNotes(materialIssueNotesArea.getText().trim());
        summary.setSafetyNotes(safetyNotesArea.getText().trim());
        summary.setFinalSummary(finalSummaryArea.getText().trim());

        task.setStatus("Completed");

        manager.saveTaskSummary(summary);
        DataStore.save(manager);

        JOptionPane.showMessageDialog(this, "Task summary saved.");
        dispose();
    }

    private JSpinner createTimeSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        return spinner;
    }

    private String formatSpinnerTime(JSpinner spinner) {
        Date date = (Date) spinner.getValue();

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0)
                .withNano(0)
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private JTextArea createTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 25);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void addSection(JPanel panel, GridBagConstraints gbc, int row, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(45, 74, 140));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(label, gbc);

        gbc.gridwidth = 1;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(field, gbc);
    }

    private void addTextArea(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextArea area) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane areaScroll = new JScrollPane(area);
        areaScroll.setPreferredSize(new Dimension(500, 80));
        panel.add(areaScroll, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    private void makeReadOnly(JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(235, 235, 235));
    }

    private int parseInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}