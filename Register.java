import javax.swing.*;

public class Register {
    private JPanel registerPanel;
    private JTextField nameField, userField, passField, addrField, contactField;
    private JRadioButton monthlyButton, yearlyButton;
    private JButton submitButton, backButton;

    private RegisterListener listener;
    private DatabaseManager dbManager;

    public interface RegisterListener {
        void onRegistrationComplete();
        void onCancel();
    }

    public Register(RegisterListener listener) {
        this.listener = listener;
        this.dbManager = new DatabaseManager();

        // Group radio buttons so only one can be selected
        ButtonGroup group = new ButtonGroup();
        group.add(monthlyButton);
        group.add(yearlyButton);
        monthlyButton.setSelected(true);

        submitButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> listener.onCancel());
    }

    private void handleRegistration() {
        String fullName = nameField.getText().trim();
        String username = userField.getText().trim();
        String password = passField.getText().trim();
        String address = addrField.getText().trim();
        String contact = contactField.getText().trim();
        int months = yearlyButton.isSelected() ? 12 : 1;

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(registerPanel, "Name, Username, and Password are required.");
            return;
        }

        // 1. Create the Account (Role 1 = CUSTOMER)
        if (dbManager.createUser(username, password, fullName, 1)) {
            // 2. Fetch the new ID to set profile and membership
            UserSession temp = dbManager.authenticate(username, password);
            if (temp != null) {
                dbManager.updateCustomerProfile(temp.getAccountId(), fullName, address, contact);
                dbManager.updateMembershipLength(temp.getAccountId(), months);
            }
            JOptionPane.showMessageDialog(registerPanel, "Account Created Successfully!");
            listener.onRegistrationComplete();
        } else {
            JOptionPane.showMessageDialog(registerPanel, "Error: Username may be taken.");
        }
    }

    public JPanel getPanel() { return registerPanel; }
}