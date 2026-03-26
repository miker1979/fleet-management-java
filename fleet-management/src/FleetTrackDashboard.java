import javax.swing.*;
import java.awt.*;

public class FleetTrackDashboard extends JFrame {

    public FleetTrackDashboard() {
        setTitle("Fleet Management Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== MAIN PANEL =====
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ===== TITLE =====
        JLabel title = new JLabel("Fleet Management System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // ===== BUTTON PANEL (2 rows, 4 columns) =====
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        Font btnFont = new Font("Arial", Font.BOLD, 14);

        // ===== BUTTONS =====
        JButton btn1 = new JButton("Add Employee");
        JButton btn2 = new JButton("View Employees");
        JButton btn3 = new JButton("Add Truck");
        JButton btn4 = new JButton("View Trucks");

        JButton btn5 = new JButton("Create Job");
        JButton btn6 = new JButton("View Jobs");
        JButton btn7 = new JButton("Mechanic");
        JButton btn8 = new JButton("Coming Soon");

        // Apply font to all buttons
        JButton[] buttons = {btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8};
        for (JButton btn : buttons) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
        }

        // Add buttons to panel
        for (JButton btn : buttons) {
            buttonPanel.add(btn);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // ===== FOOTER =====
        JLabel footer = new JLabel("Fleet System v1.0", JLabel.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        // ===== ADD TO FRAME =====
        add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FleetTrackDashboard());
    }
}