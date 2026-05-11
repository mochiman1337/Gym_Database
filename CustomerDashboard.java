import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CustomerDashboard {
    private JPanel customerPanel;
    private JTabbedPane classScheduleTab; // FIXED: Missing binding
    private JPanel classSchedule;        // FIXED: Missing binding
    private JPanel myBookings;           // FIXED: Missing binding
    private JPanel myProfile;            // FIXED: Missing binding

    private JTable scheduleTable, historyTable;
    private JButton bookButton, cancelBookingButton, saveProfileButton, logoutButton;
    private JTextField nameField, addressField, contactField;
    private JButton renewButton;

    private UserSession session;
    private DatabaseManager dbManager;

    public CustomerDashboard(UserSession session) {
        this.session = session;
        this.dbManager = new DatabaseManager();

        refreshUI();

        bookButton.addActionListener(e -> handleBook());
        cancelBookingButton.addActionListener(e -> handleCancel());
        saveProfileButton.addActionListener(e -> handleProfileUpdate());
        if (renewButton != null) renewButton.addActionListener(e -> handleRenewal());

        logoutButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(getPanel()).dispose();
            new MainAppFrame().setVisible(true);
        });
    }

    private void handleRenewal() {
        String[] opts = {"1 Month", "12 Months"};
        String choice = (String) JOptionPane.showInputDialog(customerPanel, "Select Duration:", "Renew", JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (choice != null) {
            int m = choice.contains("12") ? 12 : 1;
            if (dbManager.updateMembershipLength(session.getAccountId(), m)) {
                JOptionPane.showMessageDialog(customerPanel, "Membership Updated!");
            }
        }
    }

    private void refreshUI() {
        loadSchedule(); loadHistory(); loadProfile();
    }

    private void loadSchedule() {
        String[] h = {"ID", "Type", "Time"};
        scheduleTable.setModel(new DefaultTableModel(dbManager.getAvailableClasses().toArray(new String[0][]), h) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void loadHistory() {
        String[] h = {"Booking ID", "Name", "Time", "Status"};
        historyTable.setModel(new DefaultTableModel(dbManager.getCustomerBookings(session.getAccountId()).toArray(new String[0][]), h) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void loadProfile() {
        String[] info = dbManager.getCustomerProfile(session.getAccountId());
        nameField.setText(info[0]); addressField.setText(info[1]); contactField.setText(info[2]);
    }

    private void handleBook() {
        int r = scheduleTable.getSelectedRow();
        if (r != -1) {
            int id = Integer.parseInt((String) scheduleTable.getValueAt(r, 0));
            if (dbManager.bookClass(session.getAccountId(), id)) {
                JOptionPane.showMessageDialog(customerPanel, "Booked!");
                loadHistory();
            }
        }
    }

    private void handleCancel() {
        int r = historyTable.getSelectedRow();
        if (r != -1) {
            int id = Integer.parseInt((String) historyTable.getValueAt(r, 0));
            if (dbManager.cancelBooking(id)) {
                JOptionPane.showMessageDialog(customerPanel, "Cancelled.");
                loadHistory();
            }
        }
    }

    private void handleProfileUpdate() {
        if (dbManager.updateCustomerProfile(session.getAccountId(), nameField.getText(), addressField.getText(), contactField.getText())) {
            JOptionPane.showMessageDialog(customerPanel, "Updated!");
        }
    }

    public JPanel getPanel() { return customerPanel; }
}