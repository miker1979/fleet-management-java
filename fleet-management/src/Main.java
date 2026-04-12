import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FleetManager manager = new FleetManager();

        manager.addTruck(new Truck("T-101", "Freightliner"));
        manager.addTruck(new Truck("T-202", "Attenuator"));
        manager.addTruck(new Truck("T-303", "Service Truck"));

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
        mike.setAssignedTruckId("T-101");
        manager.addEmployee(mike);

        Employee charlie = new Employee(
                2,
                "Charlie",
                "Smith",
                "Mechanic",
                "Maintenance",
                "Phoenix",
                "555-5678",
                "charlie@email.com",
                "2026-01-10",
                true,
                32.0
        );
        manager.addEmployee(charlie);

        manager.addTask(new Task(
                1001,
                "2026-04-10",
                "0600:00",
                "Barrier Install",
                "Pulice",
                "I-10 / Broadway",
                "Mike Robinson",
                "T-101, T-202",
                "Scheduled"
        ));

        SwingUtilities.invokeLater(() -> {
            String[] options = {
                    "Driver Portal",
                    "Owner Portal",
                    "Mechanic Portal",
                    "Open All"
            };

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

            if (choice == 0 || choice == 3) {
                new EmployeeHomepageUI(manager, mike).setVisible(true);
            }

            if (choice == 1 || choice == 3) {
                new OwnerPortal(manager).setVisible(true);
            }

            if (choice == 2 || choice == 3) {
                new MechanicDashboardUI(manager, charlie).setVisible(true);
            }
        });
    }
}