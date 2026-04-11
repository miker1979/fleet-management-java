import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FleetManager manager = new FleetManager();

        // Populate Fleet
        manager.addTruck(new Truck("T-101", "Freightliner"));
        manager.addTruck(new Truck("T-202", "Attenuator"));
        manager.addTruck(new Truck("T-303", "Service Truck"));

        // Mike Robinson Profile
        Employee mike = new Employee(
                1,
                "Mike",
                "Robinson",
                "Foreman",
                "Ops",
                "Maricopa",
                "555-1234",
                "mike@email.com",
                "2026-01-01",
                true,
                35.0
        );

        // Add employee to manager
        manager.addEmployee(mike);

        // Initial Task
        manager.addTask(new Task(
                1001,
                "2026-04-10",
                "Barrier Install",
                "Pulice",
                "I-10 / Broadway",
                "Mike Robinson",
                "T-101, T-202",
                "Scheduled"
        ));

        SwingUtilities.invokeLater(() -> {
            String[] options = {"Driver Portal", "Owner Portal", "Open Both"};

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Access Level:",
                    "FleetTrack Login",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0 || choice == 2) {
                new EmployeeHomepageUI(manager, mike).setVisible(true);
            }

            if (choice == 1 || choice == 2) {
                new OwnerPortal(manager).setVisible(true);
            }
        });
    }
}