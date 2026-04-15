import javax.swing.*;
import java.io.*;

public class CompanyFileManager {

    private static final String FILE_NAME = "company_info.txt";

    public static void saveCompany(Company company) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(nullToEmpty(company.getCompanyName()));
            writer.newLine();
            writer.write(nullToEmpty(company.getAddress()));
            writer.newLine();
            writer.write(nullToEmpty(company.getPhone()));
            writer.newLine();
            writer.write(nullToEmpty(company.getEmail()));
            writer.newLine();
            writer.write(nullToEmpty(company.getContactName()));
            writer.newLine();
            writer.write(nullToEmpty(company.getContactTitle()));
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error saving company information:\n" + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static Company loadCompany() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String companyName = safeReadLine(reader);
            String address = safeReadLine(reader);
            String phone = safeReadLine(reader);
            String email = safeReadLine(reader);
            String contactName = safeReadLine(reader);
            String contactTitle = safeReadLine(reader);

            return new Company(companyName, address, phone, email, contactName, contactTitle);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error loading company information:\n" + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    public static boolean companyFileExists() {
        return new File(FILE_NAME).exists();
    }

    private static String safeReadLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return line == null ? "" : line;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}