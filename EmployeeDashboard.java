import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeDashboard {
    // Linked by IntelliJ UI Designer
    private JPanel employeePanel;
    private JLabel welcomeLabel;
    private JTabbedPane employeeTabs;

    // Tab Panels
    private JPanel scheduleTab;
    private JPanel membersTab;
    private JPanel adminTab;

    // Components
    private JTable masterScheduleTable;
    private JButton createClassButton;
    private JButton cancelClassButton;
    private JTable membersTable;
    private JButton cancelMembershipButton;
    private JButton createEmployeeButton;
    private JButton resetPasswordButton;
    private JButton deleteUserButton;
    private JButton logoutButton;

    // Backend Variables
    private UserSession session;
    private DatabaseManager dbManager;

    public EmployeeDashboard(UserSession session) {
        this.session = session;
        this.dbManager = new DatabaseManager();

        welcomeLabel.setText("Employee Portal - Logged in as: " + session.getFullName());

        // 1. Lock down the GUI based on Database Permissions
        applyRBACPermissions();

        // 2. Load the data into the tables
        loadMasterSchedule();
        loadMembersList();

        // 3. Action Listeners (Using Pop-ups as discussed!)
        createClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Using a pop-up dialog instead of a whole new form
                String className = JOptionPane.showInputDialog(employeePanel, "Enter Name for New Class:");
                if (className != null && !className.trim().isEmpty()) {
                    // dbManager.createEvent(session.getAccountId(), className, ...);
                    JOptionPane.showMessageDialog(employeePanel, "Successfully scheduled: " + className);
                }
            }
        });

        resetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String targetUsername = JOptionPane.showInputDialog(employeePanel, "Enter Username to force reset:");
                if (targetUsername != null) {
                    // dbManager.flagForPasswordReset(targetUsername);
                    JOptionPane.showMessageDialog(employeePanel, targetUsername + " will be forced to reset password on next login.");
                }
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

    /**
     * THE CORE SECURITY METHOD
     * This checks the session permissions and hides GUI elements the user shouldn't see.
     */
    private void applyRBACPermissions() {
        // Assume they have NO access to start
        createClassButton.setVisible(false);
        cancelClassButton.setVisible(false);
        cancelMembershipButton.setVisible(false);

        // 1. Coach / Admin Permissions (Classes)
        if (session.hasPermission("CREATE_EVENT")) {
            createClassButton.setVisible(true);
            cancelClassButton.setVisible(true);
        } else {
            // If they can't do anything with classes, remove the tab entirely
            employeeTabs.remove(scheduleTab);
        }

        // 2. Receptionist / Admin Permissions (Memberships)
        if (session.hasPermission("MANAGE_MEMBERSHIPS")) {
            cancelMembershipButton.setVisible(true);
        } else {
            employeeTabs.remove(membersTab);
        }

        // 3. Admin Only Permissions
        if (!session.hasPermission("FULL_ACCESS")) {
            // If they are not an admin, completely remove the Admin Tools tab
            employeeTabs.remove(adminTab);
        }
    }

    // --- Data Loading Methods ---

    private void loadMasterSchedule() {
        String[] columns = {"Event ID", "Host ID", "Type", "Name", "Time"};
        String[][] mockData = {
                {"1", "3", "GROUP_CLASS", "Advanced Yoga", "April 18, 7:00 PM"},
                {"2", "3", "1_ON_1", "Personal Training", "April 19, 8:00 AM"}
        };
        masterScheduleTable.setModel(new DefaultTableModel(mockData, columns));
    }

    private void loadMembersList() {
        String[] columns = {"Account ID", "Username", "Name", "Cycle", "Status"};
        String[][] mockData = {
                {"1", "Skater12", "John Doe", "Monthly", "Active"}
        };
        membersTable.setModel(new DefaultTableModel(mockData, columns));
    }

    // Required by MainAppFrame
    public JPanel getPanel() {
        return employeePanel;
    }
}