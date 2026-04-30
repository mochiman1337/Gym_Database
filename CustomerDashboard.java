import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerDashboard {

    private JPanel customerPanel;
    private JTabbedPane tabbedPane;
    private JTable scheduleTable;
    private JButton bookButton;
    private JTable historyTable;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField contactField;
    private JButton saveProfileButton;
    private JButton logoutButton;

    // Backend Variables
    private UserSession session;
    private DatabaseManager dbManager;

    // The constructor requires the logged-in user's session
    public CustomerDashboard(UserSession session) {
        this.session = session;
        this.dbManager = new DatabaseManager();

        // 1. Initialize the data when the screen loads
        loadScheduleData();
        loadHistoryData();
        loadProfileData();

        // 2. Button Action: Book an Event
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptBooking();
            }
        });

        // 3. Button Action: Save Profile
        saveProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current window
                SwingUtilities.getWindowAncestor(getPanel()).dispose();
                // Open a fresh application window
                new MainAppFrame().setVisible(true);
            }
        });
    }

    // --- Core Methods ---

    private void loadScheduleData() {
        // Swing uses "Models" to put data into tables.
        // We define the column headers here.
        String[] columns = {"Event ID", "Type", "Name", "Time"};

        // In reality, you call dbManager to run a SELECT query on Scheduled_Event
        // String[][] data = dbManager.getAvailableEvents();

        // Mock Data for testing the GUI
        String[][] mockData = {
                {"1", "GROUP_CLASS", "Advanced Yoga", "April 18, 7:00 PM"},
                {"2", "1_ON_1_TRAINING", "Weightlifting", "April 19, 8:00 AM"}
        };

        // Create the model and apply it to the JTable
        DefaultTableModel model = new DefaultTableModel(mockData, columns);
        scheduleTable.setModel(model);
    }

    private void loadHistoryData() {
        String[] columns = {"Booking ID", "Event Name", "Time", "Status"};

        // You would query the database using the user's ID
        // String[][] data = dbManager.getCustomerBookings(session.getAccountId());

        String[][] mockData = {
                {"100", "Cardio Blast", "April 15, 6:00 PM", "Confirmed"}
        };

        historyTable.setModel(new DefaultTableModel(mockData, columns));
    }

    private void loadProfileData() {
        // Populate the text fields with the data from the session/database
        nameField.setText(session.getFullName());
        // addressField.setText(...)
    }

    private void attemptBooking() {
        // Get the row the user clicked on
        int selectedRow = scheduleTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(customerPanel, "Please select an event from the list to book.");
            return;
        }

        // Get the Event ID from the first column (index 0) of the selected row
        String eventId = (String) scheduleTable.getValueAt(selectedRow, 0);
        String eventName = (String) scheduleTable.getValueAt(selectedRow, 2);

        // Confirm the booking
        int choice = JOptionPane.showConfirmDialog(customerPanel,
                "Are you sure you want to book: " + eventName + "?",
                "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // Call DatabaseManager to INSERT into Booking table
            // boolean success = dbManager.bookEvent(session.getAccountId(), Integer.parseInt(eventId));

            JOptionPane.showMessageDialog(customerPanel, "Successfully booked " + eventName + "!");
            // Refresh the history table to show the new booking
            loadHistoryData();
        }
    }

    private void updateProfile() {
        String newName = nameField.getText();
        // Call DatabaseManager to UPDATE Account table
        JOptionPane.showMessageDialog(customerPanel, "Profile Updated Successfully!");
    }

    // Required by MainAppFrame to display this card
    public JPanel getPanel() {
        return customerPanel;
    }
}