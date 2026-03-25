import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class FleetTrackDashboard extends JFrame {

    private FleetManager manager;

    public FleetTrackDashboard(FleetManager manager) {
        this.manager = manager;

        setTitle("FleetTrack Pro");
        setSize(1100, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color bgColor = new Color(245, 247, 250);
        Color blue = new Color(58, 93, 174);
        Color lightBlue = new Color(232, 240, 254);
        Color redTint = new Color(252, 235, 235);
        Color yellowBtn = new Color(230, 201, 92);

        getContentPane().setBackground(bgColor);

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(blue);
        navBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel logo = new JLabel("FleetTrack Pro");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel navLinks = new JLabel("Jobs   Fleet Status   Maintenance   |   Foreman Joe");
        navLinks.setForeground(Color.WHITE);
        navLinks.setFont(new Font("SansSerif", Font.PLAIN, 14));

        navBar.add(logo, BorderLayout.WEST);
        navBar.add(navLinks, BorderLayout.EAST);

        add(navBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel header = new JLabel("Today's Operations Summary");
        header.setFont(new Font("SansSerif", Font.BOLD, 30));
        header.setForeground(new Color(45, 67, 126));
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(bgColor);

        JPanel leftColumn = new JPanel();
        leftColumn.setBackground(bgColor);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setPreferredSize(new Dimension(320, 650));

        JPanel vehicleCard = createCardPanel(lightBlue, new Color(91, 141, 239));
        vehicleCard.setLayout(new BoxLayout(vehicleCard, BoxLayout.Y_AXIS));

        JLabel vehicleTitle = createSectionTitle("Vehicle Status", new Color(58, 93, 174));

        int total = manager.getTrucks().size();
        int availableCount = 0;
        int outOfServiceCount = 0;

        for (Truck truck : manager.getTrucks()) {
            if (truck.isAvailable()) {
                availableCount++;
            } else {
                outOfServiceCount++;
            }
        }

        JLabel available = createInfoLabel(
                availableCount + " / " + total + " Available",
                new Color(34, 139, 34),
                true
        );

        JLabel inUse = createInfoLabel(
                "Active Tasks: " + manager.getTasks().size(),
                Color.DARK_GRAY,
                false
        );

        JLabel maintenance = createInfoLabel(
                outOfServiceCount + " Out of Service",
                new Color(178, 34, 34),
                false
        );

        vehicleCard.add(vehicleTitle);
        vehicleCard.add(Box.createVerticalStrut(10));
        vehicleCard.add(available);
        vehicleCard.add(Box.createVerticalStrut(6));
        vehicleCard.add(inUse);
        vehicleCard.add(Box.createVerticalStrut(6));
        vehicleCard.add(maintenance);

        JPanel repairCard = createCardPanel(redTint, new Color(205, 92, 92));
        repairCard.setLayout(new BoxLayout(repairCard, BoxLayout.Y_AXIS));

        JLabel repairTitle = createSectionTitle("Open Repair Tickets", new Color(180, 70, 70));

        int ticketCount = 0;
        for (Truck truck : manager.getTrucks()) {
            if (!truck.isAvailable()) {
                ticketCount++;
            }
        }

        JLabel ticket1 = createInfoLabel(
                "Open Issues: " + ticketCount,
                new Color(178, 34, 34),
                true
        );

        int totalLFInstalled = 0;
        for (Task task : manager.getTasks()) {
            totalLFInstalled += task.getLinearFeetInstalled();
        }

        JLabel ticket2 = createInfoLabel(
                "Today's Barrier Output: " + totalLFInstalled + " LF",
                new Color(184, 134, 11),
                false
        );

        JLabel viewAll = createInfoLabel("View All Tickets →", new Color(58, 93, 174), false);

        repairCard.add(repairTitle);
        repairCard.add(Box.createVerticalStrut(10));
        repairCard.add(ticket1);
        repairCard.add(Box.createVerticalStrut(6));
        repairCard.add(ticket2);
        repairCard.add(Box.createVerticalStrut(10));
        repairCard.add(viewAll);

        JPanel quickActionsPanel = new JPanel();
        quickActionsPanel.setBackground(bgColor);
        quickActionsPanel.setLayout(new BoxLayout(quickActionsPanel, BoxLayout.Y_AXIS));
        quickActionsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel quickTitle = new JLabel("Quick Actions");
        quickTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        quickTitle.setForeground(new Color(45, 67, 126));
        quickTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createJobBtn = new JButton("+ Create New Job");
        styleButton(createJobBtn, blue, Color.WHITE);

        JButton timesheetBtn = new JButton("Review Timesheets");
        styleButton(timesheetBtn, yellowBtn, Color.BLACK);

        JButton jobSheetBtn = new JButton("Job Sheets");
        styleButton(jobSheetBtn, new Color(100, 149, 237), Color.WHITE);

        JButton employeesBtn = new JButton("Employees");
        styleButton(employeesBtn, new Color(46, 139, 87), Color.WHITE);

        createJobBtn.addActionListener(e -> new JobScreenUI(manager).setVisible(true));

        timesheetBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Timesheets screen coming next.");
        });

        jobSheetBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Job Sheets screen coming next.");
        });

        employeesBtn.addActionListener(e -> {
            new EmployeeScreenUI(manager).setVisible(true);
        });

        quickActionsPanel.add(quickTitle);
        quickActionsPanel.add(Box.createVerticalStrut(12));
        quickActionsPanel.add(createJobBtn);
        quickActionsPanel.add(Box.createVerticalStrut(15));
        quickActionsPanel.add(timesheetBtn);
        quickActionsPanel.add(Box.createVerticalStrut(15));
        quickActionsPanel.add(jobSheetBtn);
        quickActionsPanel.add(Box.createVerticalStrut(15));
        quickActionsPanel.add(employeesBtn);
        quickActionsPanel.add(Box.createVerticalStrut(20));

        leftColumn.add(vehicleCard);
        leftColumn.add(Box.createVerticalStrut(20));
        leftColumn.add(repairCard);
        leftColumn.add(Box.createVerticalStrut(20));
        leftColumn.add(quickActionsPanel);

        JPanel rightColumn = new JPanel();
        rightColumn.setBackground(bgColor);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));

        JPanel mapCard = createCardPanel(Color.WHITE, new Color(210, 220, 235));
        mapCard.setLayout(new BorderLayout());
        mapCard.setPreferredSize(new Dimension(700, 320));
        mapCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JLabel mapTitle = new JLabel("Live Fleet Map View");
        mapTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        mapTitle.setForeground(new Color(58, 93, 174));
        mapTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel mapPlaceholder = new JPanel(new GridBagLayout());
        mapPlaceholder.setBackground(new Color(245, 248, 252));
        mapPlaceholder.setBorder(new LineBorder(new Color(220, 225, 235), 1, true));
        mapPlaceholder.setPreferredSize(new Dimension(650, 220));

        JLabel mapText = new JLabel("[Placeholder for Interactive Map - Java API will place data here]");
        mapText.setFont(new Font("SansSerif", Font.PLAIN, 18));
        mapText.setForeground(Color.GRAY);

        mapPlaceholder.add(mapText);

        JPanel mapInner = new JPanel(new BorderLayout());
        mapInner.setOpaque(false);
        mapInner.add(mapTitle, BorderLayout.NORTH);
        mapInner.add(mapPlaceholder, BorderLayout.CENTER);

        mapCard.add(mapInner, BorderLayout.CENTER);

        JPanel jobsCard = createCardPanel(Color.WHITE, new Color(210, 220, 235));
        jobsCard.setLayout(new BoxLayout(jobsCard, BoxLayout.Y_AXIS));

        JLabel jobsTitle = new JLabel("Active Job Assignments");
        jobsTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        jobsTitle.setForeground(new Color(58, 93, 174));

        jobsCard.add(jobsTitle);
        jobsCard.add(Box.createVerticalStrut(15));

        if (manager.getTasks().isEmpty()) {
            JLabel noTasks = createInfoLabel("No active tasks available", Color.DARK_GRAY, false);
            jobsCard.add(noTasks);
        } else {
            for (Task task : manager.getTasks()) {
                JLabel taskLabel = createInfoLabel(
                        "Task: " + task.getTaskName() + " → " + task.getStatus(),
                        Color.DARK_GRAY,
                        false
                );
                jobsCard.add(taskLabel);
                jobsCard.add(Box.createVerticalStrut(8));
            }
        }

        rightColumn.add(mapCard);
        rightColumn.add(Box.createVerticalStrut(20));
        rightColumn.add(jobsCard);

        contentPanel.add(leftColumn, BorderLayout.WEST);
        contentPanel.add(rightColumn, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createCardPanel(Color bg, Color borderColor) {
        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        return panel;
    }

    private JLabel createSectionTitle(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(color);
        return label;
    }

    private JLabel createInfoLabel(String text, Color color, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 15));
        return label;
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(240, 45));
        button.setMaximumSize(new Dimension(240, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}