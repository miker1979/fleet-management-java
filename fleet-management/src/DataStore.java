import javax.swing.JOptionPane;
import java.io.*;

public class DataStore {

    private static final String FILE_NAME = "fleettrack_data.ser";

    public static void save(FleetManager manager) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(manager);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving data: " + e.getMessage());
        }
    }

    public static FleetManager load() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No save file found. Starting fresh.");
            return new FleetManager();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            System.out.println("Data loaded successfully.");
            return (FleetManager) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data. Starting fresh.");
            return new FleetManager();
        }
    }
}