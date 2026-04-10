import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        FleetManager manager = new FleetManager();

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

        TimeOffRequest request1 = new TimeOffRequest(
            8001,
            1,
            "Mike Robinson",
            "2026-04-01",
            "2026-04-03",
            "Vacation",
            "Family trip",
            "Pending"
        );
        manager.addTimeOffRequest(request1);

        Truck truck1 = new Truck(
            101,
            "Freightliner Cascadia",
            250000,
            true
        );
        truck1.assignToJob(1);
        manager.addTruck(truck1);

        Job job1 = new Job(
            1,
            "I-10 Barrier Installation Phase 2",
            "Howe Precast",
            "Phoenix, AZ",
            "2026-01-01",
            "2028-12-31",
            "In Progress",
            "Mike Robinson",
            "AZDOT-3478",
            "AZDOT F-Shape",
            120000,
            "Major barrier installation project for highway expansion."
        );
        manager.addJob(job1);

        Task task1 = new Task(
            1001,
            1,
            1,
            "Set Traffic Control Barriers",
            "Install 1200 LF of concrete barrier along EB lanes",
            "Mike Robinson",
            "Freightliner Cascadia",
            "High",
            "2026-01-15",
            "2026-01-16",
            "Scheduled",
            "Night work with lane closure",
            "AZDOT F-Shape",
            1200,
            "Night"
        );
        manager.addTask(task1);

        MechanicalWriteUp writeUp1 = new MechanicalWriteUp(
            5001,
            101,
            "2026-01-15",
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
        manager.addMechanicalWriteUp(writeUp1);

        if (writeUp1.getTruckId() == truck1.getId() && writeUp1.isOutOfService()) {
            truck1.setAvailable(false);
            task1.setStatus("Delayed");
            task1.updateNotes("Task delayed because assigned truck is out of service for repairs.");
        }

        SwingUtilities.invokeLater(() -> {
            new FleetTrackDashboard(manager).setVisible(true);
        });

        // ============================================================
        // DISPATCH SIMULATION THREAD
        // This will wait 10 seconds, then cancel the task automatically
        // ============================================================
        new Thread(() -> {
            try {
                Thread.sleep(10000); // Wait 10 seconds after the app starts
                task1.setStatus("Canceled");
                System.out.println(">>> DISPATCH SYSTEM: Task 1001 has been CANCELED in the background.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}