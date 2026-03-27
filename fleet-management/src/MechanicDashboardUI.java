import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class MechanicDashboardUI extends JFrame {

    private FleetManager manager;

    private DefaultListModel<String> repairListModel;
    private JList<String> repairList;
    private JTextArea detailArea;

    public MechanicDashboardUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Mechanic Dashboard");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Mechanic Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBackground(new Color(245, 247, 250));

        // LEFT SIDE - WRITE-UP LIST
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createTitledBorder("Mechanical Write-Ups"));

        repairListModel = new DefaultListModel<>();
        repairList = new JList<>(repairListModel);
        repairList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        repairList.setFont(new Font("Monospaced", Font.PLAIN, 13));

        refreshRepairList();

        repairList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedWriteUpDetails();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(repairList);
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        // RIGHT SIDE - DETAILS
        JPanel detailPanel = new JPanel(new BorderLayout(5, 5));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder("Write-Up Details"));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane detailScrollPane = new JScrollPane(detailArea);
        detailPanel.add(detailScrollPane, BorderLayout.CENTER);

        centerPanel.add(listPanel);
        centerPanel.add(detailPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton newWriteUpButton = new JButton("New Write-Up");
        JButton markInRepairButton = new JButton("Mark In Repair");
        JButton markCompletedButton = new JButton("Mark Completed");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        newWriteUpButton.addActionListener(e -> openWriteUpForm());
        markInRepairButton.addActionListener(e -> updateSelectedWriteUpStatus("In Repair"));
        markCompletedButton.addActionListener(e -> updateSelectedWriteUpStatus("Completed"));
        refreshButton.addActionListener(e -> refreshRepairList());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(newWriteUpButton);
        buttonPanel.add(markInRepairButton);
        buttonPanel.add(markCompletedButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public void refreshRepairList() {
        repairListModel.clear();

        ArrayList<MechanicalWriteUp> writeUps = manager.getMechanicalWriteUps();

        if (writeUps.isEmpty()) {
            repairListModel.addElement("No mechanical write-ups found.");
            detailArea.setText("");
            return;
        }

        for (MechanicalWriteUp writeUp : writeUps) {
            String line =
                    "ID " + writeUp.getWriteUpId() +
                    " | Truck " + writeUp.getTruckId() +
                    " | " + writeUp.getIssueType() +
                    " | " + writeUp.getPriority() +
                    " | " + writeUp.getRepairStatus();

            repairListModel.addElement(line);
        }

        if (!writeUps.isEmpty()) {
            repairList.setSelectedIndex(0);
        }
    }

    private void showSelectedWriteUpDetails() {
        int index = repairList.getSelectedIndex();

        if (index < 0) {
            detailArea.setText("");
            return;
        }

        if (manager.getMechanicalWriteUps().isEmpty()) {
            detailArea.setText("");
            return;
        }

        if (index >= manager.getMechanicalWriteUps().size()) {
            detailArea.setText("");
            return;
        }

        MechanicalWriteUp writeUp = manager.getMechanicalWriteUps().get(index);
        Truck truck = manager.findTruckById(writeUp.getTruckId());

        String truckText;
        if (truck != null) {
            truckText = "Truck " + truck.getId() + " - " + truck.getModel();
        } else {
            truckText = "Truck ID " + writeUp.getTruckId();
        }

        detailArea.setText(
                "Write-Up ID: " + writeUp.getWriteUpId() + "\n" +
                "Truck: " + truckText + "\n" +
                "Date Reported: " + writeUp.getDateReported() + "\n" +
                "Issue: " + writeUp.getIssueType() + "\n" +
                "Priority: " + writeUp.getPriority() + "\n" +
                "Reported By: " + writeUp.getReportedBy() + "\n" +
                "Mechanic: " + writeUp.getAssignedMechanic() + "\n" +
                "Status: " + writeUp.getRepairStatus() + "\n" +
                "Estimated Cost: $" + writeUp.getEstimatedCost() + "\n" +
                "Out Of Service: " + (writeUp.isOutOfService() ? "Yes" : "No") + "\n" +
                "Safe To Drive: " + (writeUp.isSafeToDrive() ? "Yes" : "No") + "\n\n" +
                "Problem:\n" + writeUp.getProblemDescription() + "\n\n" +
                "Repair Notes:\n" + writeUp.getRepairNotes()
        );
    }

    private void openWriteUpForm() {
        MechanicalWriteUpFormUI form = new MechanicalWriteUpFormUI(manager, this);
        form.setVisible(true);
    }

    private void updateSelectedWriteUpStatus(String newStatus) {
        int index = repairList.getSelectedIndex();

        if (index < 0 || index >= manager.getMechanicalWriteUps().size()) {
            JOptionPane.showMessageDialog(this, "Please select a write-up first.");
            return;
        }

        MechanicalWriteUp writeUp = manager.getMechanicalWriteUps().get(index);
        writeUp.setStatus(newStatus);

        if ("Completed".equalsIgnoreCase(newStatus)) {
            Truck truck = manager.findTruckById(writeUp.getTruckId());
            if (truck != null) {
                truck.setAvailable(true);
            }
        }

        refreshRepairList();
        repairList.setSelectedIndex(index);
        showSelectedWriteUpDetails();

        JOptionPane.showMessageDialog(this, "Write-up status updated to " + newStatus + ".");
    }
}