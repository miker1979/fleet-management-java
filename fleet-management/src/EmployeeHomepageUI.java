import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        buildHeader();
        buildMainPanel();
        buildFooter();

        rebuildCalendar();
        refreshTableData();
    }

    private void buildHeader() {
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
    }

    private void buildMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        mainPanel.add(buildCalendarPanel(), BorderLayout.NORTH);
        mainPanel.add(buildScheduleTablePanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel buildCalendarPanel() {
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

        return calendarWrapper;
    }

    private JPanel buildScheduleTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(new Color(240, 242, 245));

        JPanel gridTopBar = new JPanel(new BorderLayout());
        gridTopBar.setBackground(new Color(240, 242, 245));

        JLabel gridTitle = new JLabel("My Assigned Tasks");
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

        return tablePanel;
    }

    private void buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton refreshBtn = new JButton("Manual Refresh");
        JButton closeBtn = new JButton("Close");

        refreshBtn.addActionListener(e -> {
            rebuildCalendar();
            refreshTableData();
        });

        closeBtn.addActionListener(e -> dispose());

        footer.add(refreshBtn);
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private void rebuildCalendar() {
        calendarContentPanel.removeAll();

        monthLabel.setText(
                currentMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        + " " + currentMonth.getYear()
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

        String jobIndicator = hasJob
                ? "<span style='color:#2a9d8f;'>J</span>"
                : "<span style='color:#d0d7de;'>J</span>";

        JButton btn = new JButton(
                "<html><center>" +
                        "<div style='font-size:16px; font-weight:bold;'>" + date.getDayOfMonth() + "</div>" +
                        "<div style='font-size:10px; margin-top:4px;'>" + jobIndicator + "</div>" +
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

            String timeText = safeString(task.getStartTime());

            if (!safeString(task.getEndTime()).isEmpty()) {
                timeText += " - " + safeString(task.getEndTime());
            }

            tableModel.addRow(new Object[]{
                    safeString(task.getStartDate()),
                    timeText,
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
            if (task.getAssignedEmployeeIds() != null
                    && task.getAssignedEmployeeIds().contains(employee.getEmployeeId())) {
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

    private boolean hasJobOnDate(LocalDate date) {
        for (Task task : manager.getTasks()) {
            if (task.getAssignedEmployeeIds() != null
                    && task.getAssignedEmployeeIds().contains(employee.getEmployeeId())) {

                LocalDate taskDate = parseDateSafe(task.getStartDate());

                if (taskDate != null && taskDate.equals(date)) {
                    return true;
                }
            }
        }

        return false;
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
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(220);
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(320);
        scheduleTable.getColumnModel().getColumn(8).setPreferredWidth(120);
    }

    private void styleFlatButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}