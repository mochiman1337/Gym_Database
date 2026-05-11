import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class EmployeeDashboard {
    private JPanel employeePanel;
    private JLabel welcomeLabel;
    private JTabbedPane employeeTabs;
    private JPanel scheduleTab, membersTab, appointmentsTab;
    private JTable masterScheduleTable, membersTable, appointmentsTable;
    private JButton createClassButton, cancelClassButton, cancelMembershipButton;
    private JButton resetPasswordButton, logoutButton, createUserButton, deleteUserButton;
    private JButton acceptApptButton, declineApptButton, viewMemberDetailsButton;
    private JTextField memberSearchField;

    private UserSession session;
    private DatabaseManager dbManager;
    private TableRowSorter<DefaultTableModel> memberSorter;

    public EmployeeDashboard(UserSession session) {
        this.session = session;
        this.dbManager = new DatabaseManager();
        welcomeLabel.setText("Employee Portal - Logged in as: " + session.getFullName());

        applyRBACPermissions();

        if (session.hasPermission("CREATE_EVENT") || session.hasPermission("FULL_ACCESS")) loadMasterSchedule();
        if (session.hasPermission("MANAGE_MEMBERSHIPS") || session.hasPermission("FULL_ACCESS")) {
            loadMembersList();
            setupMemberFilter();
        }
        if (session.hasPermission("MANAGE_APPOINTMENTS") || session.hasPermission("FULL_ACCESS")) loadAppointments();

        if (createClassButton != null) createClassButton.addActionListener(e -> showCreateClassDialog());
        if (cancelClassButton != null) cancelClassButton.addActionListener(e -> handleCancelClass());
        if (createUserButton != null) createUserButton.addActionListener(e -> showRegisterUserDialog());
        if (deleteUserButton != null) deleteUserButton.addActionListener(e -> handleDeleteUser());
        if (cancelMembershipButton != null) cancelMembershipButton.addActionListener(e -> handleCancelMembership());
        if (resetPasswordButton != null) resetPasswordButton.addActionListener(e -> handleForceReset());
        if (viewMemberDetailsButton != null) viewMemberDetailsButton.addActionListener(e -> handleViewDetails());
        if (acceptApptButton != null) acceptApptButton.addActionListener(e -> handleAppointmentStatus("accepted"));
        if (declineApptButton != null) declineApptButton.addActionListener(e -> handleAppointmentStatus("declined"));

        logoutButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(getPanel()).dispose();
            new MainAppFrame().setVisible(true);
        });
    }

    private void showCreateClassDialog() {
        JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField type = new JTextField();
        JTextField time = new JTextField("2026-06-01 18:00:00");
        p.add(new JLabel("Class Name:"));
        p.add(type);
        p.add(new JLabel("Time (YYYY-MM-DD HH:MM:SS):"));
        p.add(time);

        if (JOptionPane.showConfirmDialog(employeePanel, p, "Schedule New Class", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (dbManager.createClass(session.getAccountId(), type.getText().trim(), time.getText().trim())) {
                JOptionPane.showMessageDialog(employeePanel, "Class Scheduled!");
                loadMasterSchedule();
            } else {
                JOptionPane.showMessageDialog(employeePanel, "Error scheduling class. Check format.");
            }
        }
    }

    private void handleCancelClass() {
        int r = masterScheduleTable.getSelectedRow();
        if (r != -1) {
            int classId = Integer.parseInt((String) masterScheduleTable.getValueAt(r, 0));
            if (dbManager.cancelClass(classId)) {
                JOptionPane.showMessageDialog(employeePanel, "Class Cancelled.");
                loadMasterSchedule();
            }
        } else {
            JOptionPane.showMessageDialog(employeePanel, "Select a class to cancel.");
        }
    }

    private void handleForceReset() {
        int r = membersTable.getSelectedRow();
        if (r != -1) {
            String user = (String) membersTable.getModel().getValueAt(membersTable.convertRowIndexToModel(r), 1);
            if (dbManager.flagForPasswordReset(user))
                JOptionPane.showMessageDialog(employeePanel, "User flagged for reset.");
        }
    }

    private void handleViewDetails() {
        int r = membersTable.getSelectedRow();
        if (r != -1) {
            int modelRow = membersTable.convertRowIndexToModel(r);
            int id = Integer.parseInt((String) membersTable.getModel().getValueAt(modelRow, 0));
            String name = (String) membersTable.getModel().getValueAt(modelRow, 2);
            JTable detailsTable = new JTable(new DefaultTableModel(dbManager.getMemberDetails(id).toArray(new String[0][]), new String[]{"Type", "Event Info", "Time", "Status"}));
            JOptionPane.showMessageDialog(employeePanel, new JScrollPane(detailsTable), "History for " + name, JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void handleAppointmentStatus(String newStatus) {
        int r = appointmentsTable.getSelectedRow();
        if (r != -1) {
            int appId = Integer.parseInt((String) appointmentsTable.getValueAt(r, 0));
            if (dbManager.updateAppointmentStatus(appId, newStatus)) {
                JOptionPane.showMessageDialog(employeePanel, "Appointment " + newStatus + ".");
                loadAppointments();
            }
        }
    }

    private void setupMemberFilter() {
        if (membersTable == null || membersTable.getModel() == null) return;
        memberSorter = new TableRowSorter<>((DefaultTableModel) membersTable.getModel());
        membersTable.setRowSorter(memberSorter);
        memberSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                memberSorter.setRowFilter(memberSearchField.getText().trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + memberSearchField.getText()));
            }
        });
    }

    private void applyRBACPermissions() {
        boolean isSch = session.hasPermission("CREATE_EVENT") || session.hasPermission("FULL_ACCESS");
        boolean isStaff = session.hasPermission("MANAGE_MEMBERSHIPS") || session.hasPermission("FULL_ACCESS");
        boolean isTrainer = session.hasPermission("MANAGE_APPOINTMENTS") || session.hasPermission("FULL_ACCESS");
        boolean isAdm = session.hasPermission("FULL_ACCESS");

        if (createClassButton != null) createClassButton.setVisible(isSch);
        if (cancelClassButton != null) cancelClassButton.setVisible(isSch);
        if (cancelMembershipButton != null) cancelMembershipButton.setVisible(isStaff);
        if (viewMemberDetailsButton != null) viewMemberDetailsButton.setVisible(isStaff);
        if (acceptApptButton != null) acceptApptButton.setVisible(isTrainer);
        if (declineApptButton != null) declineApptButton.setVisible(isTrainer);
        if (resetPasswordButton != null) resetPasswordButton.setVisible(isAdm);
        if (deleteUserButton != null) deleteUserButton.setVisible(isAdm);
        if (createUserButton != null) createUserButton.setVisible(isAdm);

        if (!isSch && scheduleTab != null) employeeTabs.remove(scheduleTab);
        if (!isStaff && membersTab != null) employeeTabs.remove(membersTab);
        if (!isTrainer && appointmentsTab != null) employeeTabs.remove(appointmentsTab);
    }

    private void loadAppointments() {
        if (appointmentsTable == null) return;
        appointmentsTable.setModel(new DefaultTableModel(dbManager.getStaffAppointments(session.getAccountId(), session.hasRole("ADMIN")).toArray(new String[0][]), new String[]{"Appt ID", "Customer", "Time", "Status"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
    }

    private void loadMembersList() {
        if (membersTable == null) return;
        membersTable.setModel(new DefaultTableModel(dbManager.getAllAccountsWithRoles().toArray(new String[0][]), new String[]{"ID", "Username", "Name", "Roles", "Status"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        if (memberSorter != null) memberSorter.setModel((DefaultTableModel) membersTable.getModel());
    }

    private void handleCancelMembership() {
        int r = membersTable.getSelectedRow();
        if (r != -1) {
            int id = Integer.parseInt((String) membersTable.getModel().getValueAt(membersTable.convertRowIndexToModel(r), 0));
            if (dbManager.cancelCustomerMembership(id)) loadMembersList();
        }
    }

    private void showRegisterUserDialog() {
        JPanel p = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField u = new JTextField(), ps = new JTextField(), n = new JTextField();
        JComboBox<String> r = new JComboBox<>(new String[]{"CUSTOMER", "EMPLOYEE", "ADMIN", "COACH", "TRAINER"});
        p.add(new JLabel("Username:"));
        p.add(u);
        p.add(new JLabel("Password:"));
        p.add(ps);
        p.add(new JLabel("Full Name:"));
        p.add(n);
        p.add(new JLabel("Role:"));
        p.add(r);

        if (JOptionPane.showConfirmDialog(employeePanel, p, "Register", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int[] roleIds = {1, 2, 3, 4, 5};
            if (dbManager.createUser(u.getText(), ps.getText(), n.getText(), roleIds[r.getSelectedIndex()])) {
                loadMembersList();
            } else {
                JOptionPane.showMessageDialog(employeePanel, "Creation failed. Username taken.");
            }
        }
    }

    private void handleDeleteUser() {
        int r = membersTable.getSelectedRow();
        if (r != -1) {
            int id = Integer.parseInt((String) membersTable.getModel().getValueAt(membersTable.convertRowIndexToModel(r), 0));
            if (id != session.getAccountId() && dbManager.deleteUser(id)) loadMembersList();
        }
    }

    private void loadMasterSchedule() {
        if (masterScheduleTable == null) return;
        masterScheduleTable.setModel(new DefaultTableModel(dbManager.getAllClasses().toArray(new String[0][]), new String[]{"ID", "Coach ID", "Type", "Name", "Time"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
    }

    public JPanel getPanel() {
        return employeePanel;
    }
}