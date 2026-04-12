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

        manager.addJob(new Job(
                1001,
                "I-10 Broadway Barrier Project",
                "Pulice",
                "I-10 / Broadway",
                "2026-04-01",
                "2028-12-31",
                "Active",
                "Mike Robinson",
                "ADOT-2026-1001",
                "F-Shape",
                12000,
                "Long-term barrier installation project."
        ));

        manager.addTask(new Task(
                1001,
                1001,
                "2026-04-10",
                "06:00",
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