import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

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

        JLabel companyLogo = createImageLabel(95, 95, "/images/company_logo.png");
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

        JLabel appLogo = createImageLabel(180, 180, "/images/app_logo.png");
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

        cardPanel.add(createCard("Driver Portal", () -> {
            if (!manager.getEmployees().isEmpty()) {
                new EmployeeHomepageUI(manager, manager.getEmployees().get(0)).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No employees available for Driver Portal.");
            }
        }));

        cardPanel.add(createCard("Owner Portal", () -> {
            new OwnerPortal(manager).setVisible(true);
            dispose();
        }));

        cardPanel.add(createCard("Mechanic Portal", () -> {
            if (manager.getEmployees().size() > 1) {
                new MechanicDashboardUI(manager, manager.getEmployees().get(1)).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No mechanic employee available.");
            }
        }));

        cardPanel.add(createCard("Dashboard", () -> {
            new FleetTrackDashboard(manager).setVisible(true);
            dispose();
        }));

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

    private JLabel createImageLabel(int width, int height, String resourcePath) {
        URL url = getClass().getResource(resourcePath);

        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
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