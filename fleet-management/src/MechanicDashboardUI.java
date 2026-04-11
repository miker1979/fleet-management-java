import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MechanicDashboardUI extends JFrame {

    private FleetManager manager;
    private Employee mechanic;
    private DefaultListModel<String> repairListModel;
    private JList<String> repairList;
    private JTextArea detailArea;

    public MechanicDashboardUI(FleetManager manager, Employee mechanic) {
        this.manager = manager;
        this.mechanic = mechanic;

        setTitle("Mechanic Dashboard");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Mechanic Dashboard - " + mechanic.getFullName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBackground(new Color(245, 247, 250));

        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createTitledBorder("Mechanical Write-Ups"));

        repairListModel = new DefaultListModel<>();
        repairList = new JList<>(repairListModel);
        repairList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        repairList.setFont(new Font("Monospaced", Font.PLAIN, 13));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        refreshRepairList();

        repairList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedWriteUpDetails();
            }
        });

        listPanel.add(new JScrollPane(repairList), BorderLayout.CENTER);

        JPanel detailPanel = new JPanel(new BorderLayout(5, 5));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder("Write-Up Details"));
        detailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        centerPanel.add(listPanel);
        centerPanel.add(detailPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton newWriteUpButton = new JButton("New Write-Up");
        JButton editNotesButton = new JButton("Edit Notes");
        JButton markInRepairButton = new JButton("Mark In Repair");
        JButton markCompletedButton = new JButton("Mark Completed");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        newWriteUpButton.addActionListener(e -> openWriteUpForm());
        editNotesButton.addActionListener(e -> editNotes());
        markInRepairButton.addActionListener(e -> updateSelectedWriteUpStatus("In Repair"));
        markCompletedButton.addActionListener(e -> updateSelectedWriteUpStatus("Completed"));
        refreshButton.addActionListener(e -> refreshRepairList());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(newWriteUpButton);
        buttonPanel.add(editNotesButton);
        buttonPanel.add(markInRepairButton);
        buttonPanel.add(markCompletedButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public void refreshRepairList() {
        repairListModel.clear();

        java.util.List<MechanicalWriteUp> writeUps = manager.getMechanicalWriteUps();

        if (writeUps.isEmpty()) {
            repairListModel.addElement("No mechanical write-ups found.");
            detailArea.setText("");
            return;
        }

        for (MechanicalWriteUp writeUp : writeUps) {
            String line = "ID " + writeUp.getWriteUpId()
                    + " | " + writeUp.getTruckId()
                    + " | " + writeUp.getIssueType()
                    + " | " + writeUp.getRepairStatus();
            repairListModel.addElement(line);
        }

        repairList.setSelectedIndex(0);
    }

    private void showSelectedWriteUpDetails() {
        int index = repairList.getSelectedIndex();
        java.util.List<MechanicalWriteUp> writeUps = manager.getMechanicalWriteUps();

        if (index < 0 || writeUps.isEmpty() || index >= writeUps.size()) {
            detailArea.setText("");
            return;
        }

        MechanicalWriteUp writeUp = writeUps.get(index);
        Truck truck = manager.findTruckById(writeUp.getTruckId());

        String truckText = (truck != null)
                ? "Truck " + truck.getTruckID() + " - " + truck.getModel()
                : "Truck ID " + writeUp.getTruckId();

        detailArea.setText(
                "Write-Up ID: " + writeUp.getWriteUpId() + "\n" +
                "Truck: " + truckText + "\n" +
                "Date Reported: " + writeUp.getDateReported() + "\n" +
                "Issue: " + writeUp.getIssueType() + "\n" +
                "Priority: " + writeUp.getPriority() + "\n" +
                "Reported By: " + writeUp.getReportedBy() + "\n" +
                "Mechanic: " + writeUp.getAssignedMechanic() + "\n" +
                "Status: " + writeUp.getRepairStatus() + "\n" +
                "Out Of Service: " + (writeUp.isOutOfService() ? "Yes" : "No") + "\n" +
                "Safe To Drive: " + (writeUp.isSafeToDrive() ? "Yes" : "No") + "\n\n" +
                "Problem:\n" + writeUp.getProblemDescription() + "\n\n" +
                "Repair Notes:\n" + writeUp.getRepairNotes()
        );
    }

    private void openWriteUpForm() {
        new MechanicalWriteUpFormUI(manager, this, mechanic).setVisible(true);
    }

    private void editNotes() {
        int index = repairList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select a write-up first.");
            return;
        }

        MechanicalWriteUp writeUp = manager.getMechanicalWriteUps().get(index);

        String newNotes = JOptionPane.showInputDialog(
                this,
                "Edit Repair Notes:",
                writeUp.getRepairNotes()
        );

        if (newNotes != null) {
            writeUp.setRepairNotes(newNotes);
            refreshRepairList();
            showSelectedWriteUpDetails();
        }
    }

    private void updateSelectedWriteUpStatus(String newStatus) {
        int index = repairList.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select a write-up first.");
            return;
        }

        MechanicalWriteUp writeUp = manager.getMechanicalWriteUps().get(index);

        writeUp.setAssignedMechanic(mechanic.getFullName());
        writeUp.setRepairStatus(newStatus);

        Truck truck = manager.findTruckById(writeUp.getTruckId());

        if (truck != null) {
            if ("In Repair".equalsIgnoreCase(newStatus)) {
                writeUp.setOutOfService(true);
                writeUp.setSafeToDrive(false);
                truck.setDown(true, writeUp.getIssueType());
            }

            if ("Completed".equalsIgnoreCase(newStatus)) {
                writeUp.setOutOfService(false);
                writeUp.setSafeToDrive(true);
                truck.setDown(false, "Ready");
            }
        }

        refreshRepairList();
        repairList.setSelectedIndex(index);
        showSelectedWriteUpDetails();

        JOptionPane.showMessageDialog(this, "Write-up updated.");
    }
}