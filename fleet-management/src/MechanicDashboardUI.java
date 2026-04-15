import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MechanicDashboardUI extends JFrame {

    private FleetManager manager;
    private Employee mechanic;

    private JTable repairTable;
    private DefaultTableModel tableModel;
    private JTextArea detailArea;

    private List<MechanicalWriteUp> displayedWriteUps;

    public MechanicDashboardUI(FleetManager manager, Employee mechanic) {
        this.manager = manager;
        this.mechanic = mechanic;
        this.displayedWriteUps = new ArrayList<>();

        setTitle("Mechanic Dashboard");
        setSize(1200, 650);
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

        String[] columns = {
                "ID",
                "Date Reported",
                "Priority",
                "Asset Type",
                "Asset ID",
                "Issue",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        repairTable = new JTable(tableModel);
        repairTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        repairTable.setRowHeight(28);
        repairTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        repairTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        repairTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedWriteUpDetails();
            }
        });

        listPanel.add(new JScrollPane(repairTable), BorderLayout.CENTER);

        JPanel detailPanel = new JPanel(new BorderLayout(5, 5));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder("Write-Up Details"));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

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
        JButton closeButton = new JButton("Log Out");

        newWriteUpButton.addActionListener(e -> openWriteUpForm());
        editNotesButton.addActionListener(e -> editNotes());
        markInRepairButton.addActionListener(e -> updateSelectedWriteUpStatus("In Repair"));
        markCompletedButton.addActionListener(e -> updateSelectedWriteUpStatus("Completed"));

        closeButton.addActionListener(e -> {
            dispose();
            Main.showLoginScreen();
        });

        buttonPanel.add(newWriteUpButton);
        buttonPanel.add(editNotesButton);
        buttonPanel.add(markInRepairButton);
        buttonPanel.add(markCompletedButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                refreshRepairList();
            }
        });

        refreshRepairList();
    }

    public void refreshRepairList() {
        tableModel.setRowCount(0);
        displayedWriteUps.clear();

        List<MechanicalWriteUp> writeUps = new ArrayList<>(manager.getMechanicalWriteUps());

        if (writeUps.isEmpty()) {
            detailArea.setText("");
            return;
        }

        writeUps.sort(
                Comparator.comparingInt((MechanicalWriteUp w) -> getPriorityRank(w.getPriority()))
                        .thenComparing((MechanicalWriteUp w) -> parseDateTime(w.getDateReported()), Comparator.reverseOrder())
        );

        displayedWriteUps.addAll(writeUps);

        for (MechanicalWriteUp writeUp : displayedWriteUps) {
            tableModel.addRow(new Object[]{
                    writeUp.getWriteUpId(),
                    writeUp.getDateReported(),
                    writeUp.getPriority(),
                    getAssetTypeSafe(writeUp),
                    getAssetIdSafe(writeUp),
                    writeUp.getIssueType(),
                    writeUp.getRepairStatus()
            });
        }

        if (!displayedWriteUps.isEmpty()) {
            repairTable.setRowSelectionInterval(0, 0);
            showSelectedWriteUpDetails();
        }
    }

    private int getPriorityRank(String priority) {
        if (priority == null) return 99;

        switch (priority.trim().toLowerCase()) {
            case "critical":
                return 1;
            case "high":
                return 2;
            case "medium":
                return 3;
            case "low":
                return 4;
            case "pending mechanic review":
            case "pending":
                return 5;
            default:
                return 99;
        }
    }

    private LocalDateTime parseDateTime(String dateText) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return LocalDateTime.MIN;
        }

        String[] patterns = {
                "MM/dd/yyyy HH:mm",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                if (pattern.equals("yyyy-MM-dd")) {
                    return java.time.LocalDate.parse(dateText, formatter).atStartOfDay();
                }
                return LocalDateTime.parse(dateText, formatter);
            } catch (Exception ignored) {
            }
        }

        return LocalDateTime.MIN;
    }

    private MechanicalWriteUp getSelectedWriteUp() {
        int index = repairTable.getSelectedRow();

        if (index < 0 || index >= displayedWriteUps.size()) {
            return null;
        }

        return displayedWriteUps.get(index);
    }

    private void showSelectedWriteUpDetails() {
        MechanicalWriteUp writeUp = getSelectedWriteUp();

        if (writeUp == null) {
            detailArea.setText("");
            return;
        }

        String assetType = getAssetTypeSafe(writeUp);
        String assetId = getAssetIdSafe(writeUp);
        String assetDescription = buildAssetDescription(assetType, assetId);

        detailArea.setText(
                "Write-Up ID: " + writeUp.getWriteUpId() + "\n" +
                "Asset Type: " + assetType + "\n" +
                "Asset: " + assetDescription + "\n" +
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

    private String getAssetTypeSafe(MechanicalWriteUp writeUp) {
        String assetType = writeUp.getAssetType();
        if (assetType == null || assetType.trim().isEmpty()) {
            return "Truck";
        }
        return assetType;
    }

    private String getAssetIdSafe(MechanicalWriteUp writeUp) {
        String assetId = writeUp.getAssetId();
        if (assetId == null || assetId.trim().isEmpty()) {
            return writeUp.getTruckId();
        }
        return assetId;
    }

    private String buildAssetDescription(String assetType, String assetId) {
        if (assetType.equalsIgnoreCase("Forklift")) {
            Forklift forklift = manager.findForkliftById(assetId);
            if (forklift != null) {
                return forklift.getUnitId() + " - " + forklift.getMake() + " " + forklift.getModel();
            }
            return assetId;
        }

        Truck truck = manager.findTruckById(assetId);
        if (truck != null) {
            return truck.getTruckID() + " - " + truck.getModel();
        }

        return assetId;
    }

    private void openWriteUpForm() {
        new MechanicalWriteUpFormUI(manager, this, mechanic).setVisible(true);
    }

    private void editNotes() {
        MechanicalWriteUp writeUp = getSelectedWriteUp();

        if (writeUp == null) {
            JOptionPane.showMessageDialog(this, "Select a write-up first.");
            return;
        }

        String newNotes = JOptionPane.showInputDialog(
                this,
                "Edit Repair Notes:",
                writeUp.getRepairNotes()
        );

        if (newNotes != null) {
            writeUp.setRepairNotes(newNotes);
            refreshRepairList();
        }
    }

    private void updateSelectedWriteUpStatus(String newStatus) {
        MechanicalWriteUp writeUp = getSelectedWriteUp();

        if (writeUp == null) {
            JOptionPane.showMessageDialog(this, "Select a write-up first.");
            return;
        }

        writeUp.setAssignedMechanic(mechanic.getFullName());
        writeUp.setRepairStatus(newStatus);

        String assetType = getAssetTypeSafe(writeUp);
        String assetId = getAssetIdSafe(writeUp);

        if (assetType.equalsIgnoreCase("Forklift")) {
            Forklift forklift = manager.findForkliftById(assetId);

            if (forklift != null) {
                if ("In Repair".equalsIgnoreCase(newStatus)) {
                    writeUp.setOutOfService(true);
                    writeUp.setSafeToDrive(false);
                    forklift.setDown(true, writeUp.getIssueType());
                }

                if ("Completed".equalsIgnoreCase(newStatus)) {
                    writeUp.setOutOfService(false);
                    writeUp.setSafeToDrive(true);
                    forklift.setDown(false, "Ready");
                }
            }
        } else {
            Truck truck = manager.findTruckById(assetId);

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
        }

        refreshRepairList();
        JOptionPane.showMessageDialog(this, "Write-up updated.");
    }
}