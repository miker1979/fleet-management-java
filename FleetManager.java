import java.io.*;
import java.util.ArrayList;

public class FleetManager {
    private ArrayList<Vehicle> fleet = new ArrayList<>();
    private int nextId = 1;
    private final String FILE_NAME = "fleet.txt";

    public FleetManager() {
        loadFromFile();
    }

    public void addVehicle(String make, String model, int year, String driver) {
        Vehicle vehicle = new Vehicle(nextId, make, model, year, driver);
        fleet.add(vehicle);
        nextId++;
        saveToFile();
        System.out.println("Vehicle added successfully.");
    }

    public void viewFleet() {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles in fleet.");
            return;
        }

        System.out.println("\n--- Fleet Vehicles ---");
        for (Vehicle vehicle : fleet) {
            System.out.println(vehicle);
        }
    }

    public void removeVehicle(int id) {
        for (int i = 0; i < fleet.size(); i++) {
            if (fleet.get(i).getId() == id) {
                fleet.remove(i);
                saveToFile();
                System.out.println("Vehicle removed successfully.");
                return;
            }
        }
        System.out.println("Vehicle ID not found.");
    }

    public void assignDriver(int id, String driver) {
        for (Vehicle vehicle : fleet) {
            if (vehicle.getId() == id) {
                vehicle.setDriver(driver);
                saveToFile();
                System.out.println("Driver assigned successfully.");
                return;
            }
        }
        System.out.println("Vehicle ID not found.");
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Vehicle vehicle : fleet) {
                writer.println(
                    vehicle.getId() + "," +
                    vehicle.getMake() + "," +
                    vehicle.getModel() + "," +
                    vehicle.getYear() + "," +
                    vehicle.getDriver()
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving fleet data.");
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            int maxId = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String make = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    String driver = parts[4];

                    fleet.add(new Vehicle(id, make, model, year, driver));

                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }

            nextId = maxId + 1;

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading fleet data.");
        }
    }
}