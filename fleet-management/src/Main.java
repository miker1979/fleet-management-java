import javax.swing.*;

public class Main {

    public static FleetManager manager;

    public static void main(String[] args) {
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