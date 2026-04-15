import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CompanySetupUI extends JFrame {

    private final FleetManager manager;
    private final boolean editMode;

    private JTextField companyNameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField contactNameField;
    private JTextField contactTitleField;

    public CompanySetupUI(FleetManager manager, boolean editMode) {
        this.manager = manager;
        this.editMode = editMode;

        setTitle(editMode ? "Edit Company Information" : "First-Time Company Setup");
        setSize(700, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(editMode ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(242, 244, 247));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel headingLabel = new JLabel(editMode ? "Edit Company Information" : "Company Setup");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subheadingLabel = new JLabel(
                editMode
                        ? "Update the company information used throughout the system."
                        : "Enter your company information before using FleetTrack."
        );
        subheadingLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subheadingLabel.setForeground(new Color(90, 90, 90));
        subheadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(242, 244, 247));
        headerPanel.add(headingLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subheadingLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 215, 215)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        companyNameField = new JTextField(25);
        addressField = new JTextField(25);
        phoneField = new JTextField(25);
        emailField = new JTextField(25);
        contactNameField = new JTextField(25);
        contactTitleField = new JTextField(25);

        addFormRow(formPanel, gbc, 0, "Company Name:", companyNameField);
        addFormRow(formPanel, gbc, 1, "Address:", addressField);
        addFormRow(formPanel, gbc, 2, "Phone:", phoneField);
        addFormRow(formPanel, gbc, 3, "Email:", emailField);
        addFormRow(formPanel, gbc, 4, "Primary Contact:", contactNameField);
        addFormRow(formPanel, gbc, 5, "Contact Title:", contactTitleField);

        loadExistingCompanyData();

        JButton saveButton = new JButton(editMode ? "Save Changes" : "Save and Continue");
        saveButton.setFont(new Font("Arial", Font.BOLD, 15));
        saveButton.setPreferredSize(new Dimension(180, 42));
        saveButton.addActionListener(e -> saveCompanyInfo());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 15));
        cancelButton.setPreferredSize(new Dimension(120, 42));
        cancelButton.addActionListener(e -> cancelAction());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(242, 244, 247));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(new Color(242, 244, 247));
        centerWrapper.setBorder(new EmptyBorder(20, 40, 20, 40));
        centerWrapper.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(300, 34));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(textField, gbc);
    }

    private void loadExistingCompanyData() {
        Company existingCompany = manager.getCompany();

        if (existingCompany == null) {
            existingCompany = CompanyFileManager.loadCompany();
            if (existingCompany != null) {
                manager.setCompany(existingCompany);
            }
        }

        if (existingCompany != null) {
            companyNameField.setText(existingCompany.getCompanyName());
            addressField.setText(existingCompany.getAddress());
            phoneField.setText(existingCompany.getPhone());
            emailField.setText(existingCompany.getEmail());
            contactNameField.setText(existingCompany.getContactName());
            contactTitleField.setText(existingCompany.getContactTitle());
        }
    }

    private void saveCompanyInfo() {
        String companyName = companyNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String contactName = contactNameField.getText().trim();
        String contactTitle = contactTitleField.getText().trim();

        if (companyName.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty() || contactName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Company company = new Company(companyName, address, phone, email, contactName, contactTitle);
        manager.setCompany(company);
        CompanyFileManager.saveCompany(company);

        JOptionPane.showMessageDialog(
                this,
                editMode ? "Company information updated successfully." : "Company information saved successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();

        if (!editMode) {
            new LoginUI(manager).setVisible(true);
        }
    }

    private void cancelAction() {
        dispose();

        if (!editMode) {
            new LoginUI(manager).setVisible(true);
        }
    }
}