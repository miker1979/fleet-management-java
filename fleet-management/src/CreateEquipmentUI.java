import javax.swing.*;
import java.awt.*;

public class CreateEquipmentUI extends JFrame {

    private FleetManager manager;

    public CreateEquipmentUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Create Equipment");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField truckIdField = new JTextField();
        JTextField modelField = new JTextField();

        formPanel.add(new JLabel("Truck / Equipment ID:"));
        formPanel.add(truckIdField);

        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelField);

        JButton saveBtn = new JButton("Save Equipment");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String truckId = truckIdField.getText().trim().toUpperCase();
                String model = modelField.getText().trim();

                // ✅ Basic validation
                if (truckId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Truck ID is required.");
                    truckIdField.requestFocus();
                    return;
                }

                if (model.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Model is required.");
                    modelField.requestFocus();
                    return;
                }

                // ✅ Duplicate check
                for (Truck existing : manager.getTrucks()) {
                    if (existing.getTruckID().equalsIgnoreCase(truckId)) {
                        JOptionPane.showMessageDialog(this, "Truck ID already exists.");
                        truckIdField.requestFocus();
                        return;
                    }
                }

                // ✅ Create and save
                Truck truck = new Truck(truckId, model);
                manager.addTruck(truck);

                JOptionPane.showMessageDialog(this, "Equipment created successfully!");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating equipment: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveBtn);
        bottomPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}