import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        FleetManager manager = new FleetManager();

        // =========================
        // TEST EMPLOYEE
        // =========================
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

        // =========================
        // TEST TRUCK
        // =========================
        Truck truck1 = new Truck(
            101,
            "Freightliner Cascadia",
            250000,
            true
        );
        manager.addTruck(truck1);

        // =========================
        // TEST JOB
        // =========================
        Job job1 = new Job(
            1,
            "I-10 Expansion Project",
            "Arizona DOT Contractor",
            "Phoenix, AZ",
            "01/01/2026",
            "12/31/2028",
            "In Progress",
            "Mike Robinson",
            "Major highway expansion project"
        );
        manager.addJob(job1);

        // =========================
        // TEST TASK
        // =========================
        Task task1 = new Task(
            1001,
            1,
            "Set Traffic Control Barriers",
            "Place barriers and warning signs in work zone",
            "Mike Robinson",
            "Freightliner Cascadia",
            "High",
            "01/15/2026",
            "01/16/2026",
            "Scheduled",
            "Coordinate with safety crew before lane closure"
        );
        manager.addTask(task1);

        // =========================
        // TEST MECHANICAL WRITE-UP
        // =========================
        MechanicalWriteUp writeUp1 = new MechanicalWriteUp(
            5001,
            101,
            "01/15/2026",
            "Mike Robinson",
            "Brakes",
            "High",
            "Brake pads worn down, reduced stopping power",
            false,
            true,
            "John Mechanic",
            "Replaced brake pads and inspected system",
            "In Repair",
            850.00
        );

        System.out.println("===== ORIGINAL RECORDS =====");
        truck1.displayTruck();
        job1.displayJob();
        task1.displayTask();
        writeUp1.displayWriteUp();

        System.out.println("===== CHECKING TRUCK AVAILABILITY =====");

        if (writeUp1.getTruckId() == truck1.getId() && writeUp1.isOutOfService()) {
            truck1.setAvailable(false);
            task1.setStatus("Delayed");
            task1.updateNotes("Task delayed because assigned truck is out of service for repairs.");

            System.out.println("Truck " + truck1.getModel() + " is OUT OF SERVICE.");
            System.out.println("Task '" + task1.getTaskName() + "' has been marked DELAYED.");
        } else {
            System.out.println("Truck is available. Task can continue as scheduled.");
        }

        System.out.println("===== UPDATED RECORDS =====");
        truck1.displayTruck();
        task1.displayTask();

        SwingUtilities.invokeLater(() -> {
            new FleetTrackDashboard(manager).setVisible(true);
        });
    }
}