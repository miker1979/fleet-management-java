import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class CreateEquipmentUI extends JFrame {

    private final FleetManager manager;

    private JComboBox<String> equipmentTypeCombo;

    private JTextField unitIdField;
    private JTextField yearField;
    private JTextField makeField;
    private JTextField modelField;
    private JTextField vinField;
    private JTextField colorField;
    private JTextField engineModelField;
    private JTextField engineTypeField;
    private JTextField tireSizeField;
    private JTextField mileageField;

    private JComboBox<String> trailerTypeCombo;
    private JComboBox<String> trailerLengthCombo;

    private JLabel colorLabel;
    private JLabel engineModelLabel;
    private JLabel engineTypeLabel;
    private JLabel mileageLabel;
    private JLabel trailerTypeLabel;
    private JLabel trailerLengthLabel;

    public CreateEquipmentUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create Equipment");
        setSize(700, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        equipmentTypeCombo = new JComboBox<>(new String[]{"Truck", "Trailer"});
        equipmentTypeCombo.setFont(fieldFont);

        unitIdField = new JTextField();
        yearField = new JTextField();
        makeField = new JTextField();
        modelField = new JTextField();
        vinField = new JTextField();
        colorField = new JTextField();
        engineModelField = new JTextField();
        engineTypeField = new JTextField();
        tireSizeField = new JTextField();
        mileageField = new JTextField();

        trailerTypeCombo = new JComboBox<>(new String[]{
                "Dry Van",
                "Reefer",
                "Flatbed",
                "Step Deck"
        });

        trailerLengthCombo = new JComboBox<>(new String[]{
                "48'",
                "53'"
        });

        JComponent[] fields = {
                equipmentTypeCombo, unitIdField, yearField, makeField, modelField, vinField,
                colorField, engineModelField, engineTypeField, tireSizeField, mileageField,
                trailerTypeCombo, trailerLengthCombo
        };

        for (JComponent field : fields) {
            field.setFont(fieldFont);
        }

        ((AbstractDocument) yearField.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        ((AbstractDocument) mileageField.getDocument()).setDocumentFilter(new DigitOnlyFilter());

        int row = 0;

        addField(formPanel, gbc, row++, "Equipment Type:", equipmentTypeCombo, labelFont);
        addField(formPanel, gbc, row++, "Unit ID:", unitIdField, labelFont);
        addField(formPanel, gbc, row++, "Year:", yearField, labelFont);
        addField(formPanel, gbc, row++, "Make:", makeField, labelFont);
        addField(formPanel, gbc, row++, "Model:", modelField, labelFont);
        addField(formPanel, gbc, row++, "VIN:", vinField, labelFont);

        colorLabel = addField(formPanel, gbc, row++, "Color:", colorField, labelFont);
        engineModelLabel = addField(formPanel, gbc, row++, "Engine Model:", engineModelField, labelFont);
        engineTypeLabel = addField(formPanel, gbc, row++, "Engine Type:", engineTypeField, labelFont);
        addField(formPanel, gbc, row++, "Tire Size:", tireSizeField, labelFont);
        mileageLabel = addField(formPanel, gbc, row++, "Mileage:", mileageField, labelFont);

        trailerTypeLabel = addField(formPanel, gbc, row++, "Trailer Type:", trailerTypeCombo, labelFont);
        trailerLengthLabel = addField(formPanel, gbc, row++, "Trailer Length:", trailerLengthCombo, labelFont);

        equipmentTypeCombo.addActionListener(e -> updateFieldVisibility());
        updateFieldVisibility();

        JButton saveButton = new JButton("Save Equipment");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveEquipment());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel addField(JPanel panel, GridBagConstraints gbc, int row,
                            String labelText, JComponent field, Font labelFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);

        return label;
    }

    private void updateFieldVisibility() {
        boolean isTruck = "Truck".equals(equipmentTypeCombo.getSelectedItem());
        boolean isTrailer = "Trailer".equals(equipmentTypeCombo.getSelectedItem());

        colorLabel.setVisible(isTruck);
        colorField.setVisible(isTruck);

        engineModelLabel.setVisible(isTruck);
        engineModelField.setVisible(isTruck);

        engineTypeLabel.setVisible(isTruck);
        engineTypeField.setVisible(isTruck);

        mileageLabel.setVisible(isTruck);
        mileageField.setVisible(isTruck);

        trailerTypeLabel.setVisible(isTrailer);
        trailerTypeCombo.setVisible(isTrailer);

        trailerLengthLabel.setVisible(isTrailer);
        trailerLengthCombo.setVisible(isTrailer);

        revalidate();
        repaint();
    }

    private void saveEquipment() {
        try {
            String equipmentType = (String) equipmentTypeCombo.getSelectedItem();
            String unitId = unitIdField.getText().trim();
            String yearText = yearField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String vin = vinField.getText().trim();
            String tireSize = tireSizeField.getText().trim();

            if (unitId.isEmpty() || yearText.isEmpty() || make.isEmpty() || model.isEmpty()) {
                throw new IllegalArgumentException("Unit ID, year, make, and model are required.");
            }

            int year = Integer.parseInt(yearText);

            if ("Truck".equals(equipmentType)) {
                String color = colorField.getText().trim();
                String engineModel = engineModelField.getText().trim();
                String engineType = engineTypeField.getText().trim();
                String mileageText = mileageField.getText().trim();

                if (mileageText.isEmpty()) {
                    mileageText = "0";
                }

                int mileage = Integer.parseInt(mileageText);

                Truck truck = new Truck(
                        unitId,
                        year,
                        make,
                        model,
                        vin,
                        color,
                        engineModel,
                        engineType,
                        tireSize,
                        mileage
                );

                manager.addTruck(truck);

                JOptionPane.showMessageDialog(this, "Truck created successfully.");
            } else {
                String trailerType = (String) trailerTypeCombo.getSelectedItem();
                String trailerLength = (String) trailerLengthCombo.getSelectedItem();

                Trailer trailer = new Trailer(
                        unitId,
                        year,
                        make,
                        model,
                        trailerType,
                        trailerLength,
                        vin,
                        tireSize
                );

                manager.addTrailer(trailer);

                JOptionPane.showMessageDialog(this, "Trailer created successfully.");
            }

            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year and mileage must be valid numbers.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving equipment: " + ex.getMessage());
        }
    }

    private static class DigitOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null || text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}