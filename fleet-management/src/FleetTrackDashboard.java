import javax.swing.*;
import java.awt.*;

public class FleetTrackDashboard extends JFrame {

    private FleetManager manager;

    public FleetTrackDashboard(FleetManager manager) {
        this.manager = manager;

        setTitle("FleetTrack Pro");
        setSize(1100, 850);
        setMinimumSize(new Dimension(1100, 850));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 90, 160));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel title = new JLabel("FleetTrack Pro");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JLabel nav = new JLabel("Jobs   Fleet Status   Maintenance   |   Foreman Joe");
        nav.setForeground(Color.WHITE);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        header.add(title, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== MAIN PANEL =====
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(380, 0));
        left.setOpaque(false);

        JLabel summaryTitle = new JLabel("Today's Operations Summary");
        summaryTitle.setFont(new Font("Arial", Font.BOLD, 36));
        summaryTitle.setForeground(new Color(45, 74, 140));
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(summaryTitle);
        left.add(Box.createVerticalStrut(20));

        JPanel vehicleCard = new JPanel();
        vehicleCard.setLayout(new BoxLayout(vehicleCard, BoxLayout.Y_AXIS));
        vehicleCard.setBorder(BorderFactory.createTitledBorder("Vehicle Status"));
        vehicleCard.setBackground(new Color(235, 242, 252));
        vehicleCard.setMaximumSize(new Dimension(370, 140));
        vehicleCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel available = new JLabel("18 / 20 Available");
        available.setForeground(new Color(34, 139, 34));
        available.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel inUse = new JLabel("1 in Use (Job #405)");
        inUse.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel maintenance = new JLabel("1 in Maintenance (Ticket #003)");
        maintenance.setForeground(new Color(178, 34, 34));
        maintenance.setFont(new Font("Arial", Font.PLAIN, 14));

        vehicleCard.add(Box.createVerticalStrut(10));
        vehicleCard.add(available);
        vehicleCard.add(Box.createVerticalStrut(10));
        vehicleCard.add(inUse);
        vehicleCard.add(Box.createVerticalStrut(10));
        vehicleCard.add(maintenance);
        vehicleCard.add(Box.createVerticalStrut(10));

        JLabel quickActionsLabel = new JLabel("Quick Actions");
        quickActionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        quickActionsLabel.setForeground(new Color(45, 74, 140));
        quickActionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionsWrapper = new JPanel(new BorderLayout());
        actionsWrapper.setOpaque(false);
        actionsWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsWrapper.setMaximumSize(new Dimension(370, 240));

        JPanel actions = new JPanel(new GridLayout(4, 2, 10, 10));
        actions.setOpaque(false);

        JButton btn1 = new JButton("+ Create New Job");
        JButton btn2 = new JButton("Review Timesheets");
        JButton btn3 = new JButton("Job Sheets");
        JButton btn4 = new JButton("Employee Portal");
        JButton btn5 = new JButton("Time Off Manager");
        JButton btn6 = new JButton("View Tasks");
        JButton btn7 = new JButton("Maintenance");
        JButton btn8 = new JButton("Reports");

        JButton[] buttons = {btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8};

        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(170, 40));
        }

        actions.add(btn1);
        actions.add(btn5);
        actions.add(btn2);
        actions.add(btn6);
        actions.add(btn3);
        actions.add(btn7);
        actions.add(btn4);
        actions.add(btn8);

        actionsWrapper.add(actions, BorderLayout.NORTH);

        // ===== BUTTON ACTIONS =====
        btn1.addActionListener(e -> new JobScreenUI(manager).setVisible(true));

        btn4.addActionListener(e -> {
            if (manager.getEmployees().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No employees found.");
                return;
            }

            Employee emp = manager.getEmployees().get(0);
            new EmployeeHomepageUI(manager, emp).setVisible(true);
        });

        btn5.addActionListener(e -> new ManagerTimeOffDashboardUI(manager).setVisible(true));

        // 🔥 FIX HERE
        btn7.addActionListener(e -> {
            Employee mechanic = null;

            for (Employee emp : manager.getEmployees()) {
                if (emp.getPosition() != null &&
                        emp.getPosition().toLowerCase().contains("mechanic")) {
                    mechanic = emp;
                    break;
                }
            }

            if (mechanic == null) {
                JOptionPane.showMessageDialog(this, "No mechanic found.");
                return;
            }

            new MechanicDashboardUI(manager, mechanic).setVisible(true);
        });

        left.add(vehicleCard);
        left.add(Box.createVerticalStrut(25));
        left.add(quickActionsLabel);
        left.add(Box.createVerticalStrut(10));
        left.add(actionsWrapper);

        JPanel rightSide = new JPanel(new BorderLayout(15, 15));
        rightSide.setOpaque(false);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Live Fleet Map View"));
        center.setBackground(Color.WHITE);

        JLabel mapPlaceholder = new JLabel(
                "[Placeholder for Interactive Map]",
                JLabel.CENTER
        );

        center.add(mapPlaceholder, BorderLayout.CENTER);

        rightSide.add(center, BorderLayout.CENTER);

        main.add(left, BorderLayout.WEST);
        main.add(rightSide, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);
    }
}