import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustomerDashboard {
    private JPanel customerPanel, classSchedule, myBookings, myProfile;
    private JTabbedPane classScheduleTab;
    private JTable scheduleTable, historyTable, trainerTable;
    private JButton bookButton, cancelBookingButton, saveProfileButton, logoutButton, renewButton, bookTrainerButton;
    private JTextField nameField, addressField, contactField;

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
        if (bookTrainerButton != null) bookTrainerButton.addActionListener(e -> handleBookTrainer());

        logoutButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(getPanel()).dispose();
            new MainAppFrame().setVisible(true);
        });
    }

    private void handleBookTrainer() {
        int r = trainerTable.getSelectedRow();
        if (r != -1) {
            int trainerId = Integer.parseInt((String) trainerTable.getValueAt(r, 0));
            String time = JOptionPane.showInputDialog(customerPanel, "Enter time (YYYY-MM-DD HH:MM:SS):", "2026-06-01 10:00:00");
            if (time != null && !time.isEmpty() && dbManager.bookTrainer(session.getAccountId(), trainerId, time)) {
                JOptionPane.showMessageDialog(customerPanel, "Requested! Waiting for trainer approval.");
                loadHistory();
            }
        }
    }

    private void handleRenewal() {
        String[] opts = {"1 Month", "12 Months"};
        String choice = (String) JOptionPane.showInputDialog(customerPanel, "Select Duration:", "Renew", JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (choice != null) {
            if (dbManager.updateMembershipLength(session.getAccountId(), choice.contains("12") ? 12 : 1)) {
                JOptionPane.showMessageDialog(customerPanel, "Membership Updated!");
            }
        }
    }

    private void refreshUI() { loadSchedule(); loadTrainers(); loadHistory(); loadProfile(); }

    private void loadTrainers() {
        if (trainerTable == null) return;
        trainerTable.setModel(new DefaultTableModel(dbManager.getAvailableTrainers().toArray(new String[0][]), new String[]{"Trainer ID", "Name"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void loadSchedule() {
        scheduleTable.setModel(new DefaultTableModel(dbManager.getAvailableClasses().toArray(new String[0][]), new String[]{"ID", "Type", "Time"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void loadHistory() {
        historyTable.setModel(new DefaultTableModel(dbManager.getCustomerBookings(session.getAccountId()).toArray(new String[0][]), new String[]{"ID", "Event", "Time", "Status"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void loadProfile() {
        String[] info = dbManager.getCustomerProfile(session.getAccountId());
        nameField.setText(info[0]); addressField.setText(info[1]); contactField.setText(info[2]);
    }

    private void handleBook() {
        int r = scheduleTable.getSelectedRow();
        if (r != -1 && dbManager.bookClass(session.getAccountId(), Integer.parseInt((String) scheduleTable.getValueAt(r, 0)))) {
            JOptionPane.showMessageDialog(customerPanel, "Class Booked!"); loadHistory();
        }
    }

    private void handleCancel() {
        int r = historyTable.getSelectedRow();
        if (r != -1) {
            boolean isTrainer = ((String) historyTable.getValueAt(r, 1)).startsWith("Trainer");
            if (dbManager.cancelBooking(Integer.parseInt((String) historyTable.getValueAt(r, 0)), isTrainer)) {
                JOptionPane.showMessageDialog(customerPanel, "Cancelled."); loadHistory();
            }
        }
    }

    private void handleProfileUpdate() {
        if (dbManager.updateCustomerProfile(session.getAccountId(), nameField.getText(), addressField.getText(), contactField.getText())) JOptionPane.showMessageDialog(customerPanel, "Updated!");
    }

    public JPanel getPanel() { return customerPanel; }
}