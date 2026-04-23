import javax.swing.*;
import java.awt.Font;

public class Main {

    public static FleetManager manager;

    public static void main(String[] args) {
        try {
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeData();

        SwingUtilities.invokeLater(() -> {
            Company savedCompany = CompanyFileManager.loadCompany();
            if (savedCompany != null) {
                manager.setCompany(savedCompany);
            }

            new LoginUI(manager).setVisible(true);
        });
    }

    private static void initializeData() {
        manager = DataStore.load();
    }

    public static void showLoginScreen() {
        new LoginUI(manager).setVisible(true);
    }
}