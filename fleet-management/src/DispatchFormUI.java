import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DispatchFormUI extends JDialog {

    private final FleetManager manager;
    private final Task task;

    private boolean saved = false;

    private JComboBox<String> foremanCombo;
    private JList<Employee> availableDriversList;
    private DefaultListModel<Employee> assignedCrewModel;
    private JList<Employee> assignedCrewList;

    private JComboBox<String> forkliftCombo;
    private JComboBox<Employee> equipmentDriverCombo;

    private DefaultListModel<String> supportEquipmentModel;
    private JList<String> supportEquipmentList;

    private JTextField absorberNameField;
    private JTextField absorberOriginField;

    private JComboBox<String> loadLocationCombo;
    private JTextField stagingLocationField;
    private JTextArea dispatchInstructionsArea;
    private JTextArea previewArea;

    private final ArrayList<String> selectedForklifts = new ArrayList<>();
    private final LinkedHashMap<Integer, ArrayList<String>> driverEquipmentMap = new LinkedHashMap<>();
    private final ArrayList<SupportAssignment> supportAssignments = new ArrayList<>();

    public DispatchFormUI(Frame parent, FleetManager manager, Task task) {
        super(parent, "Dispatch Task #" + task.getTaskId(), true);
        this.manager = manager;
        this.task = task;

        setSize(1225, 760);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadExistingTaskValues();
        refreshDriverCombo();
        refreshPreview();
    }

    public boolean wasSaved() {
        return saved;
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 4, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel title = new JLabel(
                "Dispatch Assignment - Task #" + task.getTaskId() + " | Job #" + task.getJobId(),
                SwingConstants.LEFT
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel(
                safe(task.getStartDate()) + "  " + safe(task.getStartTime()) + " - " + safe(task.getEndTime())
                        + "  |  " + safe(task.getJobType())
                        + "  |  " + safe(task.getLocation()),
                SwingConstants.LEFT
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(title);
        panel.add(subtitle);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        panel.add(buildCrewPanel());
        panel.add(buildEquipmentPanel());
        panel.add(buildPreviewPanel());

        return panel;
    }

    private JPanel buildCrewPanel() {
        JPanel panel = createSectionPanel("Crew Assignment");

        GridBagConstraints gbc = baseGbc();

        foremanCombo = new JComboBox<>();
        foremanCombo.addItem("");
        for (Employee employee : getForemen()) {
            foremanCombo.addItem(employee.getFullName());
        }
        foremanCombo.addActionListener(e -> refreshPreview());

        availableDriversList = new JList<>(buildAvailableDriversModel());
        availableDriversList.setVisibleRowCount(10);
        availableDriversList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        assignedCrewModel = new DefaultListModel<>();
        assignedCrewList = new JList<>(assignedCrewModel);
        assignedCrewList.setVisibleRowCount(10);
        assignedCrewList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        assignedCrewList.addListSelectionListener(this::handleCrewSelectionChange);

        JButton addDriverBtn = new JButton("Add Driver(s) >>");
        addDriverBtn.addActionListener(e -> addSelectedDrivers());

        JButton removeDriverBtn = new JButton("<< Remove Driver(s)");
        removeDriverBtn.addActionListener(e -> removeSelectedDrivers());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Foreman:"), gbc);

        gbc.gridy = 1;
        panel.add(foremanCombo, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        panel.add(new JLabel("Available Drivers:"), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel("Assigned Crew:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(availableDriversList), gbc);

        gbc.gridx = 1;
        panel.add(new JScrollPane(assignedCrewList), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addDriverBtn, gbc);

        gbc.gridx = 1;
        panel.add(removeDriverBtn, gbc);

        return panel;
    }

    private JPanel buildEquipmentPanel() {
        JPanel wrapper = new JPanel(new GridLayout(2, 1, 8, 8));

        JPanel dispatchPanel = createSectionPanel("Dispatch Details");
        GridBagConstraints gbc = baseGbc();

        loadLocationCombo = new JComboBox<>();
        populateLoadLocations();

        stagingLocationField = new JTextField();
        dispatchInstructionsArea = new JTextArea(5, 20);
        dispatchInstructionsArea.setLineWrap(true);
        dispatchInstructionsArea.setWrapStyleWord(true);

        loadLocationCombo.addActionListener(e -> refreshPreview());
        stagingLocationField.getDocument().addDocumentListener(new SimpleDocumentListener(this::refreshPreview));
        dispatchInstructionsArea.getDocument().addDocumentListener(new SimpleDocumentListener(this::refreshPreview));

        addFormRow(dispatchPanel, gbc, 0, "Load From:", loadLocationCombo);
        addFormRow(dispatchPanel, gbc, 1, "Stage At:", stagingLocationField);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        dispatchPanel.add(new JLabel("Dispatch Instructions:"), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dispatchPanel.add(new JScrollPane(dispatchInstructionsArea), gbc);

        JPanel equipmentPanel = createSectionPanel("Equipment Assignment");
        GridBagConstraints eg = baseGbc();

        forkliftCombo = new JComboBox<>();
        forkliftCombo.setEditable(false);
        forkliftCombo.addItem("");
        for (Forklift forklift : manager.getForklifts()) {
            forkliftCombo.addItem(forklift.getUnitId());
        }

        equipmentDriverCombo = new JComboBox<>();
        absorberNameField = new JTextField();
        absorberOriginField = new JTextField();

        supportEquipmentModel = new DefaultListModel<>();
        supportEquipmentList = new JList<>(supportEquipmentModel);
        supportEquipmentList.setVisibleRowCount(8);

        JButton assignForkliftBtn = new JButton("Assign Forklift");
        assignForkliftBtn.addActionListener(e -> assignForkliftToDriver());

        JButton addAbsorberBtn = new JButton("Add Absorber / Support Item");
        addAbsorberBtn.addActionListener(e -> addSupportEquipment());

        JButton removeSupportBtn = new JButton("Remove Selected Item");
        removeSupportBtn.addActionListener(e -> removeSelectedSupportItem());

        addFormRow(equipmentPanel, eg, 0, "Forklift:", forkliftCombo);
        addFormRow(equipmentPanel, eg, 1, "Assigned To:", equipmentDriverCombo);

        eg.gridx = 0;
        eg.gridy = 2;
        eg.gridwidth = 2;
        equipmentPanel.add(assignForkliftBtn, eg);

        addFormRow(equipmentPanel, eg, 3, "Support / Absorber:", absorberNameField);
        addFormRow(equipmentPanel, eg, 4, "Origin / From:", absorberOriginField);

        eg.gridx = 0;
        eg.gridy = 5;
        eg.gridwidth = 2;
        equipmentPanel.add(addAbsorberBtn, eg);

        eg.gridy = 6;
        eg.weighty = 1.0;
        eg.fill = GridBagConstraints.BOTH;
        equipmentPanel.add(new JScrollPane(supportEquipmentList), eg);

        eg.gridy = 7;
        eg.weighty = 0;
        eg.fill = GridBagConstraints.HORIZONTAL;
        equipmentPanel.add(removeSupportBtn, eg);

        wrapper.add(dispatchPanel);
        wrapper.add(equipmentPanel);

        return wrapper;
    }

    private JPanel buildPreviewPanel() {
        JPanel panel = createSectionPanel("Dispatch Preview");

        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(previewArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton(
                "Dispatched".equalsIgnoreCase(safe(task.getStatus())) ? "Update Dispatch" : "Save Dispatch"
        );
        saveBtn.addActionListener(e -> saveDispatch());

        panel.add(cancelBtn);
        panel.add(saveBtn);

        return panel;
    }

    private void populateLoadLocations() {
        loadLocationCombo.removeAllItems();
        loadLocationCombo.addItem("");
        loadLocationCombo.addItem("Main Yard");
        loadLocationCombo.addItem("Shop");
        loadLocationCombo.addItem("Office");

        for (Stockpile stockpile : manager.getStockpiles()) {
            if (stockpile != null) {
                loadLocationCombo.addItem(stockpile.toString());
            }
        }
    }

    private void loadExistingTaskValues() {
        if (!safe(task.getForeman()).isBlank()) {
            foremanCombo.setSelectedItem(task.getForeman());
        }

        if (task.getAssignedEmployeeIds() != null) {
            for (Integer employeeId : task.getAssignedEmployeeIds()) {
                Employee employee = manager.findEmployeeById(employeeId);
                if (employee != null && !containsEmployee(assignedCrewModel, employeeId)) {
                    assignedCrewModel.addElement(employee);
                }
            }
        }

        if (task.getAssignedForklifts() != null) {
            selectedForklifts.addAll(task.getAssignedForklifts());
        }

        if (task.getDriverEquipmentMap() != null) {
            for (Map.Entry<Integer, ArrayList<String>> entry : task.getDriverEquipmentMap().entrySet()) {
                driverEquipmentMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }

        if (task.getAbsorberAssignments() != null) {
            for (Task.AbsorberAssignment assignment : task.getAbsorberAssignments()) {
                supportAssignments.add(new SupportAssignment(
                        assignment.getSetName(),
                        assignment.getEmployeeId(),
                        assignment.getOrigin()
                ));
                supportEquipmentModel.addElement(formatSupportAssignment(
                        assignment.getSetName(),
                        assignment.getEmployeeId(),
                        assignment.getOrigin()
                ));
            }
        }

        loadLocationCombo.setSelectedItem(safe(task.getLoadLocation()));
        stagingLocationField.setText(safe(task.getStagingLocation()));
        dispatchInstructionsArea.setText(safe(task.getDispatchInstructions()));
    }

    private void addSelectedDrivers() {
        List<Employee> selected = availableDriversList.getSelectedValuesList();
        for (Employee employee : selected) {
            if (!containsEmployee(assignedCrewModel, employee.getEmployeeId())) {
                assignedCrewModel.addElement(employee);
            }
        }
        refreshDriverCombo();
        refreshPreview();
    }

    private void removeSelectedDrivers() {
        List<Employee> selected = assignedCrewList.getSelectedValuesList();
        for (Employee employee : selected) {
            removeEquipmentForDriver(employee.getEmployeeId());
            removeSupportAssignmentsForDriver(employee.getEmployeeId());
            removeFromModel(assignedCrewModel, employee.getEmployeeId());
        }
        refreshDriverCombo();
        rebuildSupportListModel();
        refreshPreview();
    }

    private void assignForkliftToDriver() {
        String forkliftId = safe((String) forkliftCombo.getSelectedItem()).trim();
        Employee employee = (Employee) equipmentDriverCombo.getSelectedItem();

        if (forkliftId.isBlank()) {
            JOptionPane.showMessageDialog(this, "Select a forklift first.");
            return;
        }

        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Select a crew member first.");
            return;
        }

        if (!selectedForklifts.contains(forkliftId)) {
            selectedForklifts.add(forkliftId);
        }

        driverEquipmentMap.putIfAbsent(employee.getEmployeeId(), new ArrayList<>());
        ArrayList<String> items = driverEquipmentMap.get(employee.getEmployeeId());
        String label = "Forklift " + forkliftId;
        if (!items.contains(label)) {
            items.add(label);
        }

        forkliftCombo.setSelectedItem("");
        refreshPreview();
    }

    private void addSupportEquipment() {
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Absorber", "Support"});
        JComboBox<String> styleBox = new JComboBox<>(new String[]{"New Style", "Old Style"});
        JComboBox<String> lengthBox = new JComboBox<>(new String[]{"20'", "12'"});
        JTextField qtyField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Style:"));
        panel.add(styleBox);
        panel.add(new JLabel("Length:"));
        panel.add(lengthBox);
        panel.add(new JLabel("Quantity / Sets:"));
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Support / Absorber",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
            return;
        }

        Employee employee = (Employee) equipmentDriverCombo.getSelectedItem();
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Select which crew member is bringing it.");
            return;
        }

        String itemName = qty + "x "
                + styleBox.getSelectedItem() + " "
                + lengthBox.getSelectedItem() + " "
                + typeBox.getSelectedItem();

        String origin = safe((String) loadLocationCombo.getSelectedItem()).trim();
        if (origin.isBlank()) {
            origin = "Not specified";
        }

        supportAssignments.add(new SupportAssignment(itemName, employee.getEmployeeId(), origin));
        supportEquipmentModel.addElement(formatSupportAssignment(itemName, employee.getEmployeeId(), origin));

        driverEquipmentMap.putIfAbsent(employee.getEmployeeId(), new ArrayList<>());
        driverEquipmentMap.get(employee.getEmployeeId()).add(itemName + " (From: " + origin + ")");

        refreshPreview();
    }

    private void removeSelectedSupportItem() {
        int index = supportEquipmentList.getSelectedIndex();
        if (index < 0) {
            return;
        }

        SupportAssignment removed = supportAssignments.remove(index);
        supportEquipmentModel.remove(index);

        ArrayList<String> equipment = driverEquipmentMap.get(removed.employeeId);
        if (equipment != null) {
            equipment.remove(removed.name + " (From: " + removed.origin + ")");
            if (equipment.isEmpty()) {
                driverEquipmentMap.remove(removed.employeeId);
            }
        }

        refreshPreview();
    }

    private void saveDispatch() {
        String foreman = safe((String) foremanCombo.getSelectedItem()).trim();

        if (foreman.isBlank()) {
            JOptionPane.showMessageDialog(this, "Select a foreman before saving dispatch.");
            return;
        }

        if (assignedCrewModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one driver before saving dispatch.");
            return;
        }

        ArrayList<Integer> assignedIds = new ArrayList<>();
        for (int i = 0; i < assignedCrewModel.size(); i++) {
            assignedIds.add(assignedCrewModel.get(i).getEmployeeId());
        }

        task.setForeman(foreman);
        task.setAssignedEmployeeIds(assignedIds);
        task.setAssignedForklifts(new ArrayList<>(selectedForklifts));
        task.setLoadLocation(safe((String) loadLocationCombo.getSelectedItem()));
        task.setStagingLocation(stagingLocationField.getText().trim());
        task.setDispatchInstructions(dispatchInstructionsArea.getText().trim());

        task.clearDriverEquipment();
        for (Map.Entry<Integer, ArrayList<String>> entry : driverEquipmentMap.entrySet()) {
            for (String item : entry.getValue()) {
                task.assignEquipmentToDriver(entry.getKey(), item);
            }
        }

        task.clearAbsorberAssignments();
        ArrayList<String> requiredEquipment = new ArrayList<>();
        for (SupportAssignment assignment : supportAssignments) {
            task.addAbsorberAssignment(assignment.name, assignment.employeeId, assignment.origin);
            requiredEquipment.add(assignment.name + " (From: " + assignment.origin + ")");
        }
        task.setRequiredEquipment(requiredEquipment);
        task.setRequiredEquipmentSummary(buildRequiredEquipmentSummary());

        task.setNotes(buildNotesSummary());
        task.setStatus("Dispatched");

        saved = true;
        dispose();
    }

    private void refreshDriverCombo() {
        equipmentDriverCombo.removeAllItems();
        for (int i = 0; i < assignedCrewModel.size(); i++) {
            equipmentDriverCombo.addItem(assignedCrewModel.get(i));
        }
    }

    private void refreshPreview() {
        StringBuilder sb = new StringBuilder();

        sb.append("FOREMAN\n");
        sb.append("-------\n");
        sb.append(safe((String) foremanCombo.getSelectedItem()).isBlank()
                ? "Not assigned"
                : foremanCombo.getSelectedItem()).append("\n\n");

        sb.append("CREW\n");
        sb.append("----\n");
        if (assignedCrewModel.isEmpty()) {
            sb.append("No drivers assigned.\n");
        } else {
            for (int i = 0; i < assignedCrewModel.size(); i++) {
                Employee employee = assignedCrewModel.get(i);
                sb.append("- ").append(employee.getFullName())
                        .append(" | Truck: ").append(blankAsNone(employee.getAssignedTruckId()))
                        .append(" | Trailer: ").append(blankAsNone(employee.getAssignedTrailerId()))
                        .append("\n");

                ArrayList<String> items = driverEquipmentMap.get(employee.getEmployeeId());
                if (items != null && !items.isEmpty()) {
                    for (String item : items) {
                        sb.append("    • Bringing: ").append(item);
                        if (item.startsWith("Forklift ")) {
                            String forkliftId = item.replace("Forklift ", "").trim();
                            sb.append(" | Pick up at: ").append(findForkliftLocation(forkliftId));
                        }
                        sb.append("\n");
                    }
                }
            }
        }

        String loadFrom = safe((String) loadLocationCombo.getSelectedItem());

        sb.append("\nDISPATCH LOCATIONS\n");
        sb.append("------------------\n");
        sb.append("Load From: ").append(blankAsNone(loadFrom)).append("\n");
        sb.append("Stage At : ").append(blankAsNone(stagingLocationField.getText())).append("\n");

        sb.append("\nFORKLIFTS\n");
        sb.append("---------\n");
        if (selectedForklifts.isEmpty()) {
            sb.append("No forklifts assigned.\n");
        } else {
            for (String forkliftId : selectedForklifts) {
                sb.append("- ").append(forkliftId)
                        .append(" (Pick up at: ").append(findForkliftLocation(forkliftId)).append(")")
                        .append("\n");
            }
        }

        sb.append("\nSUPPORT / ABSORBER ITEMS\n");
        sb.append("------------------------\n");
        if (supportAssignments.isEmpty()) {
            sb.append("No support items assigned.\n");
        } else {
            for (SupportAssignment assignment : supportAssignments) {
                sb.append("- ").append(formatSupportAssignment(
                        assignment.name,
                        assignment.employeeId,
                        assignment.origin
                )).append("\n");
            }
        }

        sb.append("\nINSTRUCTIONS\n");
        sb.append("------------\n");
        sb.append(blankAsNone(dispatchInstructionsArea.getText()));

        previewArea.setText(sb.toString());
        previewArea.setCaretPosition(0);
    }

    private void handleCrewSelectionChange(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            refreshPreview();
        }
    }

    private DefaultListModel<Employee> buildAvailableDriversModel() {
        DefaultListModel<Employee> model = new DefaultListModel<>();
        for (Employee employee : getAvailableDriversForTask()) {
            model.addElement(employee);
        }
        return model;
    }

    private ArrayList<Employee> getForemen() {
        ArrayList<Employee> result = new ArrayList<>();
        for (Employee employee : manager.getEmployees()) {
            if (employee == null || !employee.isActive()) {
                continue;
            }

            String position = safe(employee.getPosition()).toLowerCase();
            if (position.contains("foreman") || position.contains("supervisor")) {
                result.add(employee);
            }
        }

        result.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    private ArrayList<Employee> getAvailableDriversForTask() {
        ArrayList<Employee> result = new ArrayList<>();

        for (Employee employee : manager.getEmployees()) {
            if (employee == null || !employee.isActive()) {
                continue;
            }

            String position = safe(employee.getPosition()).toLowerCase();
            if (!position.contains("driver")) {
                continue;
            }

            if (!isDriverInConflict(employee.getEmployeeId())) {
                result.add(employee);
            }
        }

        result.sort(Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    private boolean isDriverInConflict(int employeeId) {
        for (Task otherTask : manager.getTasks()) {
            if (otherTask == null || otherTask == task) {
                continue;
            }

            if (otherTask.getAssignedEmployeeIds() != null
                    && otherTask.getAssignedEmployeeIds().contains(employeeId)
                    && isTimeConflict(task, otherTask)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeConflict(Task a, Task b) {
        try {
            LocalDate dateA = LocalDate.parse(a.getStartDate());
            LocalDate dateB = LocalDate.parse(b.getStartDate());

            if (!dateA.equals(dateB)) {
                return false;
            }

            LocalTime aStart = LocalTime.parse(a.getStartTime());
            LocalTime aEnd = LocalTime.parse(a.getEndTime());
            LocalTime bStart = LocalTime.parse(b.getStartTime());
            LocalTime bEnd = LocalTime.parse(b.getEndTime());

            return aStart.isBefore(bEnd) && aEnd.isAfter(bStart);
        } catch (Exception ex) {
            return true;
        }
    }

    private boolean containsEmployee(DefaultListModel<Employee> model, int employeeId) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).getEmployeeId() == employeeId) {
                return true;
            }
        }
        return false;
    }

    private void removeFromModel(DefaultListModel<Employee> model, int employeeId) {
        for (int i = model.size() - 1; i >= 0; i--) {
            if (model.get(i).getEmployeeId() == employeeId) {
                model.remove(i);
            }
        }
    }

    private void removeEquipmentForDriver(int employeeId) {
        driverEquipmentMap.remove(employeeId);
    }

    private void removeSupportAssignmentsForDriver(int employeeId) {
        supportAssignments.removeIf(a -> a.employeeId == employeeId);
    }

    private void rebuildSupportListModel() {
        supportEquipmentModel.clear();
        for (SupportAssignment assignment : supportAssignments) {
            supportEquipmentModel.addElement(
                    formatSupportAssignment(assignment.name, assignment.employeeId, assignment.origin)
            );
        }
    }

    private String buildNotesSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Crew: ");

        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < assignedCrewModel.size(); i++) {
            Employee employee = assignedCrewModel.get(i);
            names.add(employee.getFullName()
                    + " [Truck: " + blankAsNone(employee.getAssignedTruckId())
                    + ", Trailer: " + blankAsNone(employee.getAssignedTrailerId()) + "]");
        }

        sb.append(String.join(", ", names));

        String loadFrom = safe((String) loadLocationCombo.getSelectedItem());
        if (!loadFrom.isBlank()) {
            sb.append(" | Load From: ").append(loadFrom);
        }
        if (!safe(stagingLocationField.getText()).isBlank()) {
            sb.append(" | Stage At: ").append(stagingLocationField.getText().trim());
        }

        return sb.toString();
    }

    private String buildRequiredEquipmentSummary() {
        ArrayList<String> summary = new ArrayList<>();

        for (String forklift : selectedForklifts) {
            summary.add("Forklift " + forklift + " (Pick up at: " + findForkliftLocation(forklift) + ")");
        }

        for (SupportAssignment assignment : supportAssignments) {
            summary.add(assignment.name + " (From: " + assignment.origin + ")");
        }

        return String.join(", ", summary);
    }

    private String formatSupportAssignment(String name, int employeeId, String origin) {
        Employee employee = manager.findEmployeeById(employeeId);
        String driverName = employee == null ? "Unknown Driver" : employee.getFullName();
        return name + " | Assigned To: " + driverName + " | From: " + origin;
    }

    private String findForkliftLocation(String forkliftId) {
        for (Stockpile s : manager.getStockpiles()) {
            if (s.getForkliftIds().contains(forkliftId)) {
                return s.getName();
            }
        }
        return "Unknown Location";
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14)
        ));
        return panel;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private String blankAsNone(String value) {
        return safe(value).isBlank() ? "None" : value.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static class SupportAssignment {
        private final String name;
        private final int employeeId;
        private final String origin;

        private SupportAssignment(String name, int employeeId, String origin) {
            this.name = name;
            this.employeeId = employeeId;
            this.origin = origin;
        }
    }

    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable action;

        private SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            action.run();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            action.run();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            action.run();
        }
    }
}