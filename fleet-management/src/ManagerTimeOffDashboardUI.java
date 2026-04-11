import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ManagerTimeOffDashboardUI extends JFrame {

    private FleetManager manager;
    private DefaultListModel<String> requestListModel;
    private JList<String> requestList;
    private JTextArea detailArea;

    public ManagerTimeOffDashboardUI(FleetManager manager) {
        this.manager = manager;

        setTitle("Manager Time Off Dashboard");
        setSize(900, 550);
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

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBackground(new Color(245, 247, 250));

        // LEFT SIDE - REQUEST LIST
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createTitledBorder("Submitted Requests"));

        requestListModel = new DefaultListModel<>();
        requestList = new JList<>(requestListModel);
        requestList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestList.setFont(new Font("Monospaced", Font.PLAIN, 13));

        refreshRequestList();

        requestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedRequestDetails();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(requestList);
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        // RIGHT SIDE - DETAILS
        JPanel detailPanel = new JPanel(new BorderLayout(5, 5));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder("Request Details"));

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

        JButton approveButton = new JButton("Approve");
        JButton denyButton = new JButton("Deny");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        approveButton.addActionListener(e -> updateSelectedRequestStatus("Approved"));
        denyButton.addActionListener(e -> updateSelectedRequestStatus("Denied"));
        refreshButton.addActionListener(e -> refreshRequestList());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(approveButton);
        buttonPanel.add(denyButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public void refreshRequestList() {
        requestListModel.clear();

        // FIXED: Using java.util.List to avoid AWT conflict and type mismatch
        java.util.List<TimeOffRequest> requests = manager.getTimeOffRequests();

        if (requests.isEmpty()) {
            requestListModel.addElement("No time off requests found.");
            detailArea.setText("");
            return;
        }

        for (TimeOffRequest request : requests) {
            String line = "ID " + request.getRequestId() +
                          " | " + request.getEmployeeName() +
                          " | " + request.getStartDate() +
                          " to " + request.getEndDate() +
                          " | " + request.getStatus();
            requestListModel.addElement(line);
        }

        if (!requests.isEmpty()) {
            requestList.setSelectedIndex(0);
        }
    }

    private void showSelectedRequestDetails() {
        int index = requestList.getSelectedIndex();
        java.util.List<TimeOffRequest> requests = manager.getTimeOffRequests();

        if (index < 0 || requests.isEmpty() || index >= requests.size()) {
            detailArea.setText("");
            return;
        }

        TimeOffRequest request = requests.get(index);

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
        int index = requestList.getSelectedIndex();
        java.util.List<TimeOffRequest> requests = manager.getTimeOffRequests();

        if (index < 0 || index >= requests.size()) {
            JOptionPane.showMessageDialog(this, "Please select a request first.");
            return;
        }

        TimeOffRequest request = requests.get(index);
        request.setStatus(newStatus);

        refreshRequestList();
        requestList.setSelectedIndex(index);
        showSelectedRequestDetails();
        JOptionPane.showMessageDialog(this, "Request " + request.getRequestId() + " marked as " + newStatus + ".");
    }
}