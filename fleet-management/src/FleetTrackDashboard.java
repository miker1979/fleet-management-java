import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FleetTrackDashboard extends JFrame {

    private final FleetManager manager;

    private JLabel jobsCountLabel;
    private JLabel trucksCountLabel;
    private JLabel forkliftsCountLabel;
    private JLabel dateTimeLabel;

    private JTextArea equipmentAlertsArea;
    private JTextArea activeJobLocationsArea;
    private JTextArea safetyMessageArea;

    public FleetTrackDashboard(FleetManager manager) {
        this.manager = manager;

        setTitle("FleetTrack Pro - Global Dashboard");
        setSize(1550, 980);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);

        refreshDashboardCounts();
        startClock();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(37, 65, 126));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel leftHeader = new JPanel();
        leftHeader.setOpaque(false);
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));

        JLabel companyLabel = new JLabel("Ghostline Logistics Tech LLC");
        companyLabel.setFont(new Font("Arial", Font.BOLD, 28));
        companyLabel.setForeground(Color.WHITE);

        jobsCountLabel = new JLabel("Active Jobs: 0");
        jobsCountLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        jobsCountLabel.setForeground(Color.WHITE);

        trucksCountLabel = new JLabel("Trucks: 0");
        trucksCountLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        trucksCountLabel.setForeground(Color.WHITE);

        forkliftsCountLabel = new JLabel("Forklifts: 0");
        forkliftsCountLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        forkliftsCountLabel.setForeground(Color.WHITE);

        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        statsRow.setOpaque(false);
        statsRow.add(jobsCountLabel);
        statsRow.add(trucksCountLabel);
        statsRow.add(forkliftsCountLabel);

        leftHeader.add(companyLabel);
        leftHeader.add(Box.createVerticalStrut(10));
        leftHeader.add(statsRow);

        JPanel rightHeader = new JPanel();
        rightHeader.setOpaque(false);
        rightHeader.setLayout(new BoxLayout(rightHeader, BoxLayout.Y_AXIS));

        JLabel dashboardLabel = new JLabel("GLOBAL DASHBOARD");
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dashboardLabel.setForeground(new Color(214, 228, 255));
        dashboardLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dateTimeLabel.setForeground(Color.WHITE);
        dateTimeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightHeader.add(dashboardLabel);
        rightHeader.add(Box.createVerticalStrut(10));
        rightHeader.add(dateTimeLabel);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(234, 238, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topCardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        topCardsPanel.setOpaque(false);

        topCardsPanel.add(createEquipmentStatusCard());
        topCardsPanel.add(createYardOperationsCard());
        topCardsPanel.add(createSafetyWeatherCard());

        JPanel lowerSectionPanel = new JPanel(new BorderLayout(20, 0));
        lowerSectionPanel.setOpaque(false);

        lowerSectionPanel.add(createFleetMapPanel(), BorderLayout.CENTER);
        lowerSectionPanel.add(createQuickActionsPanel(), BorderLayout.EAST);

        mainPanel.add(topCardsPanel, BorderLayout.NORTH);
        mainPanel.add(lowerSectionPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createEquipmentStatusCard() {
        JPanel card = createCardPanel();

        JLabel trucksReadyLabel = new JLabel("Trucks: 0 Ready / 0 Down");
        trucksReadyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        trucksReadyLabel.setForeground(new Color(20, 148, 32));

        JLabel forkliftsReadyLabel = new JLabel("Forklifts: 0 Ready / 0 Down");
        forkliftsReadyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        forkliftsReadyLabel.setForeground(new Color(20, 148, 32));

        JLabel alertsLabel = new JLabel("Equipment Alerts");
        alertsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        alertsLabel.setForeground(new Color(35, 42, 60));

        equipmentAlertsArea = createInfoTextArea(
                "No equipment in system yet.\nAdd trucks or forklifts in Owner Portal to begin operations."
        );

        card.add(trucksReadyLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(forkliftsReadyLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(alertsLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(new JScrollPane(equipmentAlertsArea));

        return card;
    }

    private JPanel createYardOperationsCard() {
        JPanel card = createCardPanel();

        JLabel yardEquipmentLabel = new JLabel("Yard Equipment Ready: 0");
        yardEquipmentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        yardEquipmentLabel.setForeground(new Color(35, 42, 60));

        JLabel forkliftsAvailableLabel = new JLabel("Forklifts Available at Yard: 0");
        forkliftsAvailableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        forkliftsAvailableLabel.setForeground(new Color(35, 42, 60));

        JLabel activeJobsLabel = new JLabel("Active Job Locations");
        activeJobsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        activeJobsLabel.setForeground(new Color(35, 42, 60));

        activeJobLocationsArea = createInfoTextArea(
                "No active job locations in system."
        );

        card.add(yardEquipmentLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(forkliftsAvailableLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(activeJobsLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(new JScrollPane(activeJobLocationsArea));

        return card;
    }

    private JPanel createSafetyWeatherCard() {
        JPanel card = createCardPanel();

        JLabel weatherLabel = new JLabel("Weather Status: Green - Normal Operations");
        weatherLabel.setFont(new Font("Arial", Font.BOLD, 18));
        weatherLabel.setForeground(new Color(20, 148, 32));

        JLabel managerLabel = new JLabel("On-Call Manager: Not Assigned");
        managerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        managerLabel.setForeground(new Color(35, 42, 60));

        JLabel safetyLabel = new JLabel("Safety Message of the Day");
        safetyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        safetyLabel.setForeground(new Color(35, 42, 60));

        safetyMessageArea = createInfoTextArea(
                "Stay alert, inspect your equipment, and report any issues before dispatch. " +
                "Maintain safe yard speeds and verify load security before rolling."
        );

        card.add(weatherLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(managerLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(safetyLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(new JScrollPane(safetyMessageArea));

        return card;
    }

    private JPanel createFleetMapPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 213, 224)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Live Fleet Map / Visual Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(37, 65, 126));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel descriptionLabel = new JLabel("This area is reserved for future live fleet mapping.");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionLabel.setForeground(Color.BLACK);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JTextArea usesArea = new JTextArea();
        usesArea.setEditable(false);
        usesArea.setFont(new Font("Arial", Font.PLAIN, 16));
        usesArea.setBackground(Color.WHITE);
        usesArea.setLineWrap(true);
        usesArea.setWrapStyleWord(true);
        usesArea.setText(
                "Planned uses:\n" +
                "• Yard vs. field equipment visibility\n" +
                "• Job site locations\n" +
                "• Dispatch and route awareness\n" +
                "• Weather and safety overlays"
        );
        usesArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descriptionLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(usesArea);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(360, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 213, 224)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(37, 65, 126));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton ownerPortalButton = createDashboardButton("Owner Portal");
        JButton employeePortalButton = createDashboardButton("Employee Portal");
        JButton mechanicPortalButton = createDashboardButton("Mechanic Portal");
        JButton stockpileButton = createDashboardButton("Stockpiles");
        JButton logoutButton = createDashboardButton("Log Out");

        ownerPortalButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Owner Portal screen coming soon.")
        );

        employeePortalButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Employee Portal screen coming soon.")
        );

        mechanicPortalButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Mechanic Portal screen coming soon.")
        );

        stockpileButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Stockpile screen coming soon.")
        );

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginUI(manager).setVisible(true);
            }
        });

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(ownerPortalButton);
        panel.add(Box.createVerticalStrut(14));
        panel.add(employeePortalButton);
        panel.add(Box.createVerticalStrut(14));
        panel.add(mechanicPortalButton);
        panel.add(Box.createVerticalStrut(14));
        panel.add(stockpileButton);
        panel.add(Box.createVerticalStrut(14));
        panel.add(logoutButton);

        return panel;
    }

    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setPreferredSize(new Dimension(300, 48));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(225, 233, 243));
        button.setForeground(new Color(35, 42, 60));
        button.setBorder(BorderFactory.createLineBorder(new Color(140, 160, 185)));
        return button;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 213, 224)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    private JTextArea createInfoTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(248, 250, 252));
        area.setBorder(BorderFactory.createLineBorder(new Color(140, 160, 185)));
        return area;
    }

    private void refreshDashboardCounts() {
        if (manager != null) {
            jobsCountLabel.setText("Active Jobs: " + manager.getJobs().size());
            trucksCountLabel.setText("Trucks: " + manager.getTrucks().size());
            forkliftsCountLabel.setText("Forklifts: " + manager.getForklifts().size());
        }
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy  |  HH:mm");
            dateTimeLabel.setText(LocalDateTime.now().format(formatter));
        });
        timer.start();
    }
}