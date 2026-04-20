import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class EmployeeHomepageUI extends JFrame {

    private final FleetManager manager;
    private final Employee employee;

    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private Timer autoRefreshTimer;
    private JTextArea repairStatusArea;

    private LocalDate selectedDate = LocalDate.now();
    private YearMonth currentMonth = YearMonth.now();

    private JPanel calendarContentPanel;
    private JLabel monthLabel;
    private JToggleButton selectedDayOnlyToggle;

    public EmployeeHomepageUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Employee Portal - " + employee.getFullName());
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(new Color(240, 242, 245));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(45, 74, 140));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel nameLabel = new JLabel(employee.getFullName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        JLabel infoLabel = new JLabel(
                "Role: " + safeString(employee.getPosition()) +
                " | Truck: " + getAssignedTruckDisplay() +
                " | Trailer: " + getAssignedTrailerDisplay() +
                " | Phone: " + safeString(employee.getPhoneNumber())
        );
        infoLabel.setForeground(new Color(200, 210, 230));
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        header.add(nameLabel);
        header.add(infoLabel);
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel calendarWrapper = new JPanel(new BorderLayout(8, 8));
        calendarWrapper.setBackground(Color.WHITE);
        calendarWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel calendarTopBar = new JPanel(new BorderLayout());
        calendarTopBar.setBackground(Color.WHITE);

        JButton prevBtn = new JButton("←");
        JButton nextBtn = new JButton("→");
        styleFlatButton(prevBtn);
        styleFlatButton(nextBtn);

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        monthLabel.setForeground(new Color(60, 60, 60));

        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            rebuildCalendar();
        });

        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            rebuildCalendar();
        });

        calendarTopBar.add(prevBtn, BorderLayout.WEST);
        calendarTopBar.add(monthLabel, BorderLayout.CENTER);
        calendarTopBar.add(nextBtn, BorderLayout.EAST);

        calendarContentPanel = new JPanel(new BorderLayout());
        calendarContentPanel.setBackground(Color.WHITE);

        calendarWrapper.add(calendarTopBar, BorderLayout.NORTH);
        calendarWrapper.add(calendarContentPanel, BorderLayout.CENTER);

        mainPanel.add(calendarWrapper, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(new Color(240, 242, 245));

        JPanel gridTopBar = new JPanel(new BorderLayout());
        gridTopBar.setBackground(new Color(240, 242, 245));

        JLabel gridTitle = new JLabel("My Dispatches");
        gridTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        selectedDayOnlyToggle = new JToggleButton("Show Selected Day Only");
        selectedDayOnlyToggle.setFocusPainted(false);
        selectedDayOnlyToggle.addActionListener(e -> refreshTableData());

        gridTopBar.add(gridTitle, BorderLayout.WEST);
        gridTopBar.add(selectedDayOnlyToggle, BorderLayout.EAST);

        String[] columns = {
                "Date",
                "Time",
                "Job Type",
                "Contractor",
                "Location",
                "Foreman",
                "Forklift(s)",
                "Instructions",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        setupTableAesthetics();

        JScrollPane tableScroll = new JScrollPane(scheduleTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));

        tablePanel.add(gridTopBar, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        repairStatusArea = new JTextArea(5, 20);
        repairStatusArea.setEditable(false);
        repairStatusArea.setLineWrap(true);
        repairStatusArea.setWrapStyleWord(true);
        repairStatusArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        repairStatusArea.setBackground(Color.WHITE);

        JScrollPane repairScroll = new JScrollPane(repairStatusArea);
        repairScroll.setBorder(BorderFactory.createTitledBorder("Truck Status"));
        repairScroll.setPreferredSize(new Dimension(0, 150));

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBackground(new Color(240, 242, 245));
        bottomPanel.add(tablePanel, BorderLayout.CENTER);
        bottomPanel.add(repairScroll, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton timeOffBtn = new JButton("Request Time Off");
        JButton writeUpBtn = new JButton("Report Mechanical Issue");
        JButton refreshBtn = new JButton("Manual Sync");
        JButton closeBtn = new JButton("Log Out");

        timeOffBtn.addActionListener(e -> openTimeOffRequestForm());
        writeUpBtn.addActionListener(e -> new MechanicalWriteUpFormUI(manager, null, employee).setVisible(true));
        refreshBtn.addActionListener(e -> {
            rebuildCalendar();
            refreshTableData();
            refreshRepairStatus();
        });

        closeBtn.addActionListener(e -> {
            if (autoRefreshTimer != null) {
                autoRefreshTimer.stop();
            }
            dispose();
            Main.showLoginScreen();
        });

        footer.add(timeOffBtn);
        footer.add(writeUpBtn);
        footer.add(refreshBtn);
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);

        rebuildCalendar();
        refreshTableData();
        refreshRepairStatus();

        autoRefreshTimer = new Timer(3000, e -> {
            checkForUrgentNotifications();
            rebuildCalendar();
            refreshTableData();
            refreshRepairStatus();
        });
        autoRefreshTimer.start();
    }

    private void rebuildCalendar() {
        calendarContentPanel.removeAll();

        monthLabel.setText(
                currentMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) +
                        " " + currentMonth.getYear()
        );

        JPanel calendarGrid = new JPanel(new GridLayout(0, 7, 6, 6));
        calendarGrid.setBackground(Color.WHITE);

        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayHeader : dayHeaders) {
            JLabel headerLabel = new JLabel(dayHeader, SwingConstants.CENTER);
            headerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            headerLabel.setForeground(new Color(150, 150, 150));
            calendarGrid.add(headerLabel);
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int firstDayColumn = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();

        for (int i = 0; i < firstDayColumn; i++) {
            calendarGrid.add(createEmptyCalendarCell());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            calendarGrid.add(createDateButton(date));
        }

        int totalCellsUsed = 7 + firstDayColumn + daysInMonth;
        int remainder = totalCellsUsed % 7;
        if (remainder != 0) {
            int blanksNeeded = 7 - remainder;
            for (int i = 0; i < blanksNeeded; i++) {
                calendarGrid.add(createEmptyCalendarCell());
            }
        }

        calendarContentPanel.add(calendarGrid, BorderLayout.CENTER);
        calendarContentPanel.revalidate();
        calendarContentPanel.repaint();
    }

    private Component createEmptyCalendarCell() {
        JPanel empty = new JPanel();
        empty.setBackground(Color.WHITE);
        return empty;
    }

    private JButton createDateButton(LocalDate date) {
        boolean isToday = date.equals(LocalDate.now());
        boolean isSelected = date.equals(selectedDate);
        boolean hasJob = hasJobOnDate(date);
        boolean hasMaintenance = hasMaintenanceOnDate(date);
        boolean hasTimeOff = hasTimeOffOnDate(date);

        String indicators = buildIndicatorHtml(hasJob, hasMaintenance, hasTimeOff);

        JButton btn = new JButton(
                "<html><center>" +
                        "<div style='font-size:16px; font-weight:bold;'>" + date.getDayOfMonth() + "</div>" +
                        "<div style='font-size:10px; margin-top:4px;'>" + indicators + "</div>" +
                        "</center></html>"
        );

        btn.setFocusPainted(false);
        btn.setMargin(new Insets(6, 6, 6, 6));
        btn.setPreferredSize(new Dimension(70, 58));
        btn.setOpaque(true);

        if (isSelected) {
            btn.setBackground(new Color(29, 53, 87));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(29, 53, 87), 2));
        } else if (isToday) {
            btn.setBackground(new Color(224, 239, 252));
            btn.setForeground(new Color(30, 30, 30));
            btn.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        } else {
            btn.setBackground(new Color(248, 250, 252));
            btn.setForeground(new Color(45, 45, 45));
            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230), 1));
        }

        btn.addActionListener(e -> {
            selectedDate = date;
            refreshTableData();
            rebuildCalendar();
        });

        return btn;
    }

    private String buildIndicatorHtml(boolean hasJob, boolean hasMaintenance, boolean hasTimeOff) {
        StringBuilder sb = new StringBuilder();

        sb.append(hasJob
                ? "<span style='color:#2a9d8f;'>J</span>"
                : "<span style='color:#d0d7de;'>J</span>");

        sb.append("&nbsp;");

        sb.append(hasMaintenance
                ? "<span style='color:#e76f51;'>M</span>"
                : "<span style='color:#d0d7de;'>M</span>");

        sb.append("&nbsp;");

        sb.append(hasTimeOff
                ? "<span style='color:#457b9d;'>T</span>"
                : "<span style='color:#d0d7de;'>T</span>");

        return sb.toString();
    }

    private void styleFlatButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private String getAssignedTruckDisplay() {
        if (employee.getAssignedTruckId() == null || employee.getAssignedTruckId().isEmpty()) {
            return "None";
        }
        return employee.getAssignedTruckId();
    }

    private String getAssignedTrailerDisplay() {
        if (employee.getAssignedTrailerId() == null || employee.getAssignedTrailerId().isEmpty()) {
            return "None";
        }
        return employee.getAssignedTrailerId();
    }

    private void setupTableAesthetics() {
        scheduleTable.setRowHeight(42);
        scheduleTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        scheduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        scheduleTable.getTableHeader().setBackground(new Color(230, 230, 230));
        scheduleTable.setSelectionBackground(new Color(173, 216, 230));
        scheduleTable.setGridColor(new Color(220, 220, 220));
        scheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(95);
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(220);
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(320);
        scheduleTable.getColumnModel().getColumn(8).setPreferredWidth(120);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);

        List<Task> tasks = getEmployeeTasksSorted();

        for (Task task : tasks) {
            LocalDate taskDate = parseDateSafe(task.getStartDate());

            boolean includeRow;
            if (selectedDayOnlyToggle.isSelected()) {
                includeRow = taskDate != null && taskDate.equals(selectedDate);
            } else {
                includeRow = taskDate == null || !taskDate.isBefore(LocalDate.now());
            }

            if (!includeRow) {
                continue;
            }

            String forkliftText = buildForkliftText(task);
            String instructions = safeString(task.getDispatchInstructions()).trim();
            if (instructions.isEmpty()) {
                instructions = "-";
            }

            String timeText = safeString(task.getStartTime()) + " - " + safeString(task.getEndTime());

            tableModel.addRow(new Object[]{
                    safeString(task.getStartDate()),
                    timeText.trim().equals("-") ? "" : timeText,
                    safeString(task.getJobType()),
                    safeString(task.getContractor()),
                    safeString(task.getLocation()),
                    safeString(task.getForeman()),
                    forkliftText,
                    instructions,
                    safeString(task.getStatus())
            });
        }
    }

    private List<Task> getEmployeeTasksSorted() {
        ArrayList<Task> employeeTasks = new ArrayList<>();

        for (Task task : manager.getTasks()) {
            if (task.getAssignedEmployeeIds() != null &&
                    task.getAssignedEmployeeIds().contains(employee.getEmployeeId())) {
                employeeTasks.add(task);
            }
        }

        employeeTasks.sort(
                Comparator.comparing(
                                Task::getStartDate,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                        )
                        .thenComparing(
                                Task::getStartTime,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                        )
                        .thenComparingInt(Task::getTaskId)
        );

        return employeeTasks;
    }

    private String buildForkliftText(Task task) {
        if (task.getAssignedForklifts() == null || task.getAssignedForklifts().isEmpty()) {
            return "None";
        }
        return String.join(", ", task.getAssignedForklifts());
    }

    private LocalDate parseDateSafe(String dateText) {
        try {
            return LocalDate.parse(dateText);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasJobOnDate(LocalDate date) {
        for (Task task : manager.getTasks()) {
            if (task.getAssignedEmployeeIds() != null &&
                    task.getAssignedEmployeeIds().contains(employee.getEmployeeId())) {

                LocalDate taskDate = parseDateSafe(task.getStartDate());
                if (taskDate != null && taskDate.equals(date)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasMaintenanceOnDate(LocalDate date) {
        String assignedTruckId = employee.getAssignedTruckId();

        if (assignedTruckId == null || assignedTruckId.isEmpty()) {
            return false;
        }

        for (MechanicalWriteUp writeUp : manager.getMechanicalWriteUps()) {
            if (assignedTruckId.equalsIgnoreCase(writeUp.getTruckId())) {
                String status = safeString(writeUp.getRepairStatus()).toLowerCase();
                if (!status.contains("complete") && !status.contains("closed") && !status.contains("resolved")) {
                    YearMonth calendarMonth = YearMonth.from(date);
                    if (calendarMonth.equals(currentMonth)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasTimeOffOnDate(LocalDate date) {
        try {
            Method getRequestsMethod = manager.getClass().getMethod("getTimeOffRequests");
            Object requestsObject = getRequestsMethod.invoke(manager);

            if (requestsObject instanceof List<?>) {
                List<?> requests = (List<?>) requestsObject;

                for (Object request : requests) {
                    String employeeName = invokeStringMethod(request, "getEmployeeName");
                    if (employeeName == null) {
                        employeeName = invokeStringMethod(request, "getFullName");
                    }
                    if (employeeName == null) {
                        employeeName = invokeStringMethod(request, "getEmployee");
                    }

                    if (employeeName != null && employeeName.equalsIgnoreCase(employee.getFullName())) {
                        String status = invokeStringMethod(request, "getStatus");
                        if (status == null || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Approved")) {
                            LocalDate startDate = invokeDateMethod(request, "getStartDate");
                            LocalDate endDate = invokeDateMethod(request, "getEndDate");

                            if (startDate == null) {
                                startDate = invokeDateMethod(request, "getRequestDate");
                            }
                            if (endDate == null) {
                                endDate = startDate;
                            }

                            if (startDate != null && endDate != null) {
                                if ((!date.isBefore(startDate)) && (!date.isAfter(endDate))) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private String invokeStringMethod(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            Object result = method.invoke(target);
            return result == null ? null : result.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate invokeDateMethod(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            Object result = method.invoke(target);

            if (result instanceof LocalDate) {
                return (LocalDate) result;
            }

            if (result != null) {
                return LocalDate.parse(result.toString());
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private void refreshRepairStatus() {
        String assignedTruckId = employee.getAssignedTruckId();

        if (assignedTruckId == null || assignedTruckId.isEmpty()) {
            repairStatusArea.setText("No truck assigned.");
            return;
        }

        MechanicalWriteUp latest = null;

        for (MechanicalWriteUp writeUp : manager.getMechanicalWriteUps()) {
            if (assignedTruckId.equalsIgnoreCase(writeUp.getTruckId())) {
                latest = writeUp;
            }
        }

        if (latest == null) {
            repairStatusArea.setText("Truck " + assignedTruckId + " is clear. No active issues.");
            return;
        }

        repairStatusArea.setText(
                "Truck: " + latest.getTruckId() + "\n" +
                        "Status: " + safeString(latest.getRepairStatus()) + "\n" +
                        "Assigned Mechanic: " + safeString(latest.getAssignedMechanic()) + "\n" +
                        "Priority: " + safeString(latest.getPriority()) + "\n" +
                        "Safe To Drive: " + (latest.isSafeToDrive() ? "Yes" : "No") + "\n" +
                        "Out Of Service: " + (latest.isOutOfService() ? "Yes" : "No") + "\n\n" +
                        "Problem:\n" + safeString(latest.getProblemDescription()) + "\n\n" +
                        "Repair Notes:\n" + safeString(latest.getRepairNotes())
        );
    }

    private void openTimeOffRequestForm() {
        try {
            Class<?> uiClass = Class.forName("TimeOffRequestFormUI");
            Constructor<?>[] constructors = uiClass.getConstructors();

            for (Constructor<?> constructor : constructors) {
                Class<?>[] params = constructor.getParameterTypes();

                if (params.length == 3 &&
                        params[0].isAssignableFrom(FleetManager.class) &&
                        params[1].isAssignableFrom(Employee.class) &&
                        params[2].isAssignableFrom(LocalDate.class)) {

                    Object ui = constructor.newInstance(manager, employee, selectedDate);
                    if (ui instanceof JFrame) {
                        ((JFrame) ui).setVisible(true);
                        return;
                    }
                }

                if (params.length == 2 &&
                        params[0].isAssignableFrom(FleetManager.class) &&
                        params[1].isAssignableFrom(Employee.class)) {

                    Object ui = constructor.newInstance(manager, employee);
                    if (ui instanceof JFrame) {
                        ((JFrame) ui).setVisible(true);
                        return;
                    }
                }

                if (params.length == 1 && params[0].isAssignableFrom(Employee.class)) {
                    Object ui = constructor.newInstance(employee);
                    if (ui instanceof JFrame) {
                        ((JFrame) ui).setVisible(true);
                        return;
                    }
                }

                if (params.length == 1 && params[0].isAssignableFrom(FleetManager.class)) {
                    Object ui = constructor.newInstance(manager);
                    if (ui instanceof JFrame) {
                        ((JFrame) ui).setVisible(true);
                        return;
                    }
                }

                if (params.length == 0) {
                    Object ui = constructor.newInstance();
                    if (ui instanceof JFrame) {
                        ((JFrame) ui).setVisible(true);
                        return;
                    }
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Time-off form exists but could not be opened with the current constructor setup.",
                    "Time Off Request",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "TimeOffRequestFormUI was not found in the project.",
                    "Time Off Request",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open the time-off request form.",
                    "Time Off Request",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void checkForUrgentNotifications() {
        for (Task task : manager.getTasks()) {
            if (task.getAssignedEmployeeIds() != null &&
                    task.getAssignedEmployeeIds().contains(employee.getEmployeeId()) &&
                    safeString(task.getStatus()).equalsIgnoreCase("Canceled")) {

                JOptionPane.showMessageDialog(
                        this,
                        "Job at " + safeString(task.getLocation()) + " has been CANCELED.",
                        "Dispatch Update",
                        JOptionPane.WARNING_MESSAGE
                );

                task.setStatus("Canceled (Acknowledged)");
            }
        }
    }
}