import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        FleetManager manager = new FleetManager();

        // TEST EMPLOYEE (so your list isn't empty)
        Employee emp = new Employee(
            1,
            "Mike",
            "Robinson",
            "Fleet Manager",
            "Operations",
            "123 Main St",
            "555-1234",
            "mike@email.com",
            "2026-01-01",
            true,
            35.00
        );

        manager.addEmployee(emp);

        SwingUtilities.invokeLater(() -> {
            new FleetTrackDashboard(manager).setVisible(true);
        });
    }
}