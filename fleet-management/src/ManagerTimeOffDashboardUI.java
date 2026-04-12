import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManagerTimeOffDashboardUI extends JFrame {

    private FleetManager manager;
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private JTextArea detailArea;

    public ManagerTimeOffDashboardUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Manager Time Off Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("Time Off Request Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {
                "ID", "Employee", "Start", "End", "Type", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTable = new JTable(tableModel);
        requestTable.setRowHeight(40);
        requestTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        requestTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        requestTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane tableScroll = new JScrollPane(requestTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Submitted Requests"));

        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // ===== DETAILS =====
        detailArea = new JTextArea(6, 20);
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Request Details"));

        mainPanel.add(detailScroll, BorderLayout.SOUTH);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton approveButton = new JButton("Approve");
        JButton denyButton = new JButton("Deny");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        approveButton.addActionListener(e -> updateSelectedRequestStatus("Approved"));
        denyButton.addActionListener(e -> updateSelectedRequestStatus("Denied"));
        refreshButton.addActionListener(e -> refreshTable());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(approveButton);
        buttonPanel.add(denyButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        refreshTable();

        requestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedRequestDetails();
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        List<TimeOffRequest> requests = manager.getTimeOffRequests();

        if (requests.isEmpty()) {
            detailArea.setText("No time off requests found.");
            return;
        }

        for (TimeOffRequest request : requests) {
            Object[] row = {
                    request.getRequestId(),
                    request.getEmployeeName(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getRequestType(),
                    request.getStatus()
            };

            tableModel.addRow(row);
        }

        requestTable.setRowSelectionInterval(0, 0);
    }

    private void showSelectedRequestDetails() {
        int row = requestTable.getSelectedRow();
        List<TimeOffRequest> requests = manager.getTimeOffRequests();

        if (row < 0 || row >= requests.size()) {
            detailArea.setText("");
            return;
        }

        TimeOffRequest request = requests.get(row);

        detailArea.setText(
                "Request ID: " + request.getRequestId() + "\n" +
                "Employee ID: " + request.getEmployeeId() + "\n" +
                "Employee Name: " + request.getEmployeeName() + "\n" +
                "Start Date: " + request.getStartDate() + "\n" +
                "End Date: " + request.getEndDate() + "\n" +
                "Request Type: " + request.getRequestType() + "\n" +
                "Status: " + request.getStatus() + "\n\n" +
                "Reason:\n" + request.getReason()
        );
    }

    private void updateSelectedRequestStatus(String newStatus) {
        int row = requestTable.getSelectedRow();
        List<TimeOffRequest> requests = manager.getTimeOffRequests();

        if (row < 0 || row >= requests.size()) {
            JOptionPane.showMessageDialog(this, "Please select a request first.");
            return;
        }

        TimeOffRequest request = requests.get(row);
        request.setStatus(newStatus);

        refreshTable();
        requestTable.setRowSelectionInterval(row, row);
        showSelectedRequestDetails();

        JOptionPane.showMessageDialog(this,
                "Request " + request.getRequestId() + " marked as " + newStatus + ".");
    }
}