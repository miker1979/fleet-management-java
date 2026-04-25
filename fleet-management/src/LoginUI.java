import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LoginUI extends JFrame {

    private final FleetManager manager;

    public LoginUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Ghostline FleetTrack");
        setSize(980, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(242, 244, 247));
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(242, 244, 247));

        JPanel topHeaderPanel = new JPanel(new BorderLayout());
        topHeaderPanel.setBackground(new Color(242, 244, 247));
        topHeaderPanel.setMaximumSize(new Dimension(920, 120));
        topHeaderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel companyLogo = createImageLabel(95, 95, "images/company_logo.png");
        companyLogo.setBorder(new EmptyBorder(0, 0, 0, 15));

        JLabel companyLabel = new JLabel("Ghostline Logistics Tech LLC");
        companyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        companyLabel.setForeground(new Color(70, 70, 70));
        companyLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeaderPanel.setBackground(new Color(242, 244, 247));
        leftHeaderPanel.add(companyLogo);
        leftHeaderPanel.add(companyLabel);

        topHeaderPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(900, 1));
        separator.setForeground(new Color(210, 210, 210));

        JLabel appLogo = createImageLabel(180, 180, "images/app_logo.png");
        appLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("FleetTrack");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(59, 95, 173));

        JLabel subtitleLabel = new JLabel("Fleet Management System");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(110, 110, 110));

        JPanel cardPanel = new JPanel(new GridLayout(2, 2, 18, 18));
        cardPanel.setBackground(new Color(242, 244, 247));
        cardPanel.setBorder(new EmptyBorder(24, 20, 18, 20));
        cardPanel.setMaximumSize(new Dimension(920, 220));

        cardPanel.add(createCard("Employee Portal", this::openDriverPortal));
        cardPanel.add(createCard("Owner Portal", this::openOwnerPortal));
        cardPanel.add(createCard("Mechanic Portal", this::openMechanicPortal));
        cardPanel.add(createCard("Global Dashboard", this::openGlobalDashboard));

        JButton exitButton = new JButton("Exit Program");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setPreferredSize(new Dimension(220, 46));
        exitButton.setMaximumSize(new Dimension(220, 46));
        exitButton.setFocusPainted(false);
        exitButton.setFont(new Font("Arial", Font.BOLD, 15));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> System.exit(0));

        JLabel footerLabel = new JLabel("© 2026 Ghostline Logistics Tech LLC. All rights reserved.");
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        footerLabel.setForeground(new Color(100, 100, 100));

        contentPanel.add(topHeaderPanel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(appLogo);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(18));
        contentPanel.add(cardPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(exitButton);
        contentPanel.add(Box.createVerticalStrut(26));
        contentPanel.add(footerLabel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void openOwnerPortal() {
        try {
            OwnerPortal ownerPortal = new OwnerPortal(manager);
            ownerPortal.setVisible(true);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Could not open Owner Portal.\n\n" + ex.getMessage(),
                    "Owner Portal Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openGlobalDashboard() {
        try {
            FleetTrackDashboard dashboard = new FleetTrackDashboard(manager);
            dashboard.setVisible(true);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Could not open Global Dashboard.\n\n" + ex.getMessage(),
                    "Dashboard Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openDriverPortal() {
        List<Employee> drivers = new ArrayList<>();

        for (Employee e : manager.getEmployees()) {
            if (e.getPosition() != null &&
                e.getPosition().toLowerCase().contains("driver")) {
                drivers.add(e);
            }
        }

        if (drivers.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No drivers have been created yet.\nPlease create a driver in the Owner Portal first.",
                    "No Drivers Found",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (drivers.size() == 1) {
            new EmployeeHomepageUI(manager, drivers.get(0)).setVisible(true);
            dispose();
            return;
        }

        Object selection = JOptionPane.showInputDialog(
                this,
                "Select Driver:",
                "Driver Login",
                JOptionPane.PLAIN_MESSAGE,
                null,
                drivers.toArray(),
                drivers.get(0)
        );

        if (selection instanceof Employee) {
            new EmployeeHomepageUI(manager, (Employee) selection).setVisible(true);
            dispose();
        }
    }

    private void openMechanicPortal() {
        List<Employee> mechanics = new ArrayList<>();

        for (Employee e : manager.getEmployees()) {
            if (e.getPosition() != null &&
                e.getPosition().toLowerCase().contains("mechanic")) {
                mechanics.add(e);
            }
        }

        if (mechanics.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No mechanics have been created yet.\nPlease create a mechanic in the Owner Portal first.",
                    "No Mechanics Found",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (mechanics.size() == 1) {
            new MechanicDashboardUI(manager, mechanics.get(0)).setVisible(true);
            dispose();
            return;
        }

        Object selection = JOptionPane.showInputDialog(
                this,
                "Select Mechanic:",
                "Mechanic Login",
                JOptionPane.PLAIN_MESSAGE,
                null,
                mechanics.toArray(),
                mechanics.get(0)
        );

        if (selection instanceof Employee) {
            new MechanicDashboardUI(manager, (Employee) selection).setVisible(true);
            dispose();
        }
    }

    private JPanel createCard(String title, Runnable action) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(74, 102, 178));
        card.setPreferredSize(new Dimension(400, 100));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(title);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 17));
        card.add(label);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(64, 90, 160));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(74, 102, 178));
            }
        });

        return card;
    }

    private JLabel createImageLabel(int width, int height, String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);

        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

            JLabel label = new JLabel(new ImageIcon(scaled));
            label.setPreferredSize(new Dimension(width, height));
            label.setMaximumSize(new Dimension(width, height));
            label.setMinimumSize(new Dimension(width, height));
            return label;
        }

        JLabel fallback = new JLabel("Image Not Found", SwingConstants.CENTER);
        fallback.setPreferredSize(new Dimension(width, height));
        fallback.setMaximumSize(new Dimension(width, height));
        fallback.setMinimumSize(new Dimension(width, height));
        fallback.setOpaque(true);
        fallback.setBackground(new Color(220, 220, 220));
        fallback.setForeground(Color.DARK_GRAY);
        fallback.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return fallback;
    }
}