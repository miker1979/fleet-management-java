import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FleetManager manager = new FleetManager();
        boolean running = true;

        while (running) {
            System.out.println("\n--- Fleet Management System ---");
            System.out.println("1. Add Vehicle");
            System.out.println("2. View Fleet");
            System.out.println("3. Remove Vehicle");
            System.out.println("4. Assign Driver");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();

                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();

                    int year = readYear(scanner);

                    System.out.print("Enter driver name: ");
                    String driver = scanner.nextLine();

                    manager.addVehicle(make, model, year, driver);
                    break;

                case "2":
                    manager.viewFleet();
                    break;

                case "3":
                    int removeId = readId(scanner, "Enter vehicle ID to remove: ");
                    manager.removeVehicle(removeId);
                    break;

                case "4":
                    int assignId = readId(scanner, "Enter vehicle ID to assign driver: ");
                    System.out.print("Enter new driver name: ");
                    String newDriver = scanner.nextLine();
                    manager.assignDriver(assignId, newDriver);
                    break;

                case "5":
                    running = false;
                    System.out.println("Goodbye.");
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }

        scanner.close();
    }

    private static int readYear(Scanner scanner) {
        while (true) {
            System.out.print("Enter year: ");
            String input = scanner.nextLine();
            try {
                int year = Integer.parseInt(input);
                if (year >= 1900 && year <= 2100) {
                    return year;
                }
                System.out.println("Please enter a valid year between 1900 and 2100.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a numeric year.");
            }
        }
    }

    private static int readId(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID. Please enter a number.");
            }
        }
    }
}