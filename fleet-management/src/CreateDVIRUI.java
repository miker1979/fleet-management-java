import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CreateDVIRUI extends JFrame {

    private final FleetManager manager;
    private final Employee employee;

    private JTextField carrierField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField driverField;
    private JTextField truckIdField;
    private JTextField odometerField;
    private JTextField trailerIdField;
    private JTextArea remarksArea;
    private JTextField mechanicSignatureField;
    private JTextField mechanicDateField;
    private JTextField driverSignatureField;

    private JCheckBox satisfactoryBox;
    private JCheckBox defectsCorrectedBox;
    private JCheckBox safeToOperateBox;

    private final List<JCheckBox> truckDefectBoxes = new ArrayList<>();
    private final List<JCheckBox> trailerDefectBoxes = new ArrayList<>();

    public CreateDVIRUI(FleetManager manager, Employee employee) {
        this.manager = manager;
        this.employee = employee;

        setTitle("Driver Vehicle Inspection Report");
        setSize(1100, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("DRIVER VEHICLE INSPECTION REPORT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(buildHeaderPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(buildDefectPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(buildRemarksPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(buildStatusPanel());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton saveButton = new JButton("Save DVIR");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveDVIR());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Inspection Header"));

        carrierField = new JTextField(getCompanyName());
        dateField = new JTextField(LocalDate.now().toString());
        timeField = new JTextField(LocalTime.now().withSecond(0).withNano(0).toString());
        driverField = new JTextField(getDriverName());
        truckIdField = new JTextField(getAssignedTruckId());
        odometerField = new JTextField();
        trailerIdField = new JTextField(getAssignedTrailerId());
        remarksArea = new JTextArea(6, 50);
        mechanicSignatureField = new JTextField();
        mechanicDateField = new JTextField();
        driverSignatureField = new JTextField(getDriverName());

        ((AbstractDocument) odometerField.getDocument()).setDocumentFilter(new DigitOnlyFilter());

        addField(panel, "Carrier:", carrierField);
        addField(panel, "Date (yyyy-MM-dd):", dateField);
        addField(panel, "Time (HH:mm):", timeField);
        addField(panel, "Driver Name:", driverField);
        addField(panel, "Truck ID:", truckIdField);
        addField(panel, "Odometer Reading:", odometerField);
        addField(panel, "Trailer ID:", trailerIdField);
        addField(panel, "Mechanic Signature:", mechanicSignatureField);
        addField(panel, "Mechanic Date (yyyy-MM-dd):", mechanicDateField);
        addField(panel, "Driver Signature:", driverSignatureField);

        return panel;
    }

    private JPanel buildDefectPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 15, 15));
        wrapper.setBorder(BorderFactory.createTitledBorder("Inspection Items"));

        JPanel truckPanel = new JPanel(new GridLayout(0, 2));
        truckPanel.setBorder(BorderFactory.createTitledBorder("Truck / Tractor"));

        String[] truckItems = {
                "Air Compressor", "Air Lines",
                "Battery", "Belts and Hoses",
                "Body", "Brake Accessories",
                "Brakes, Parking", "Brakes, Service",
                "Clutch", "Coupling Devices",
                "Defroster/Heater", "Drive Line",
                "Engine", "Exhaust",
                "Fifth Wheel", "Fluid Levels",
                "Frame and Assembly", "Front Axle",
                "Fuel Tanks", "Horn",
                "Lights", "Head - Stop",
                "Tail - Dash", "Turn Indicators",
                "Mirrors", "Muffler",
                "Oil Pressure", "Radiator",
                "Rear End", "Reflectors",
                "Safety Equipment", "Fire Extinguisher",
                "Flags - Flares - Fuses", "Reflective Triangles",
                "Spare Bulbs and Fuses", "Starter",
                "Steering", "Suspension System",
                "Tire Chains", "Tires",
                "Transmission", "Trip Recorder",
                "Wheels and Rims", "Windows",
                "Windshield Wipers", "Other"
        };

        for (String item : truckItems) {
            JCheckBox box = new JCheckBox(item);
            truckDefectBoxes.add(box);
            truckPanel.add(box);
        }

        JPanel trailerPanel = new JPanel(new GridLayout(0, 2));
        trailerPanel.setBorder(BorderFactory.createTitledBorder("Trailer"));

        String[] trailerItems = {
                "Brake Connections", "Brakes",
                "Coupling Devices", "Coupling (King) Pin",
                "Doors", "Hitch",
                "Landing Gear", "Lights - All",
                "Reflectors/Reflective Tape", "Roof",
                "Suspension System", "Tarpaulin",
                "Tires", "Wheels and Rims",
                "Other"
        };

        for (String item : trailerItems) {
            JCheckBox box = new JCheckBox(item);
            trailerDefectBoxes.add(box);
            trailerPanel.add(box);
        }

        wrapper.add(new JScrollPane(truckPanel));
        wrapper.add(new JScrollPane(trailerPanel));

        return wrapper;
    }

    private JPanel buildRemarksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Remarks"));

        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(remarksArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Inspection Status"));

        satisfactoryBox = new JCheckBox("Condition of the above vehicle is satisfactory");
        defectsCorrectedBox = new JCheckBox("Above defects corrected");
        safeToOperateBox = new JCheckBox("Above defects need not be corrected for safe operation");

        panel.add(satisfactoryBox);
        panel.add(defectsCorrectedBox);
        panel.add(safeToOperateBox);

        return panel;
    }

    private void saveDVIR() {
        try {
            String carrier = carrierField.getText().trim();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String driverName = driverField.getText().trim();
            String truckId = truckIdField.getText().trim();
            String odometerText = odometerField.getText().trim();
            String trailerId = trailerIdField.getText().trim();
            String remarks = remarksArea.getText().trim();
            String mechanicSignature = mechanicSignatureField.getText().trim();
            String mechanicDate = mechanicDateField.getText().trim();
            String driverSignature = driverSignatureField.getText().trim();

            if (carrier.isEmpty() || date.isEmpty() || time.isEmpty() || driverName.isEmpty() || truckId.isEmpty()) {
                throw new IllegalArgumentException("Carrier, date, time, driver name, and truck ID are required.");
            }

            if (odometerText.isEmpty()) {
                throw new IllegalArgumentException("Odometer reading is required.");
            }

            LocalDate.parse(date);
            LocalTime.parse(time);

            if (!mechanicDate.isEmpty()) {
                LocalDate.parse(mechanicDate);
            }

            int odometer = Integer.parseInt(odometerText);

            Truck truck = manager.findTruckById(truckId);
            if (truck == null) {
                throw new IllegalArgumentException("Truck ID does not exist in fleet.");
            }

            List<String> truckDefects = getSelectedDefects(truckDefectBoxes);
            List<String> trailerDefects = getSelectedDefects(trailerDefectBoxes);

            int nextId = getNextReportId();

            DVIRReport report = new DVIRReport(
                    nextId,
                    carrier,
                    date,
                    time,
                    driverName,
                    truckId,
                    odometer,
                    trailerId,
                    truckDefects,
                    trailerDefects,
                    remarks,
                    satisfactoryBox.isSelected(),
                    defectsCorrectedBox.isSelected(),
                    safeToOperateBox.isSelected(),
                    mechanicSignature,
                    mechanicDate,
                    driverSignature
            );

            manager.getDvirReports().add(report);

            if (!truckDefects.isEmpty()) {
                truck.setDown(true, "DVIR Report #" + nextId + " - Defects Reported");
            }

            DataStore.save(manager);

            JOptionPane.showMessageDialog(
                    this,
                    truckDefects.isEmpty() && trailerDefects.isEmpty()
                            ? "DVIR saved successfully."
                            : "DVIR saved. Defects reported -> truck flagged for maintenance.",
                    "DVIR Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Odometer must be a valid number.");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Date/time format invalid. Use yyyy-MM-dd and HH:mm.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving DVIR: " + ex.getMessage());
        }
    }

    private List<String> getSelectedDefects(List<JCheckBox> boxes) {
        List<String> defects = new ArrayList<>();
        for (JCheckBox box : boxes) {
            if (box.isSelected()) {
                defects.add(box.getText());
            }
        }
        return defects;
    }

    private int getNextReportId() {
        int max = 0;
        for (DVIRReport report : manager.getDvirReports()) {
            max = Math.max(max, report.getReportId());
        }
        return max + 1;
    }

    private String getCompanyName() {
        if (manager.getCompany() != null && manager.getCompany().getCompanyName() != null) {
            return manager.getCompany().getCompanyName();
        }
        return "Ghostline Logistics Tech LLC";
    }

    private String getDriverName() {
        if (employee == null) {
            return "";
        }
        return employee.getFirstName() + " " + employee.getLastName();
    }

    private String getAssignedTruckId() {
        if (employee == null || employee.getAssignedTruckId() == null) {
            return "";
        }
        return employee.getAssignedTruckId();
    }

    private String getAssignedTrailerId() {
        if (employee == null || employee.getAssignedTrailerId() == null) {
            return "";
        }
        return employee.getAssignedTrailerId();
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(label);
        panel.add(field);
    }

    private static class DigitOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null || text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    
    }
}