import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class EmployeeDashboard {
    private JPanel employeePanel;
    private JLabel welcomeLabel;
    private JTabbedPane employeeTabs;
    private JPanel scheduleTab, membersTab;
    private JTable masterScheduleTable, membersTable;
    private JButton createClassButton, cancelClassButton, cancelMembershipButton;
    private JButton resetPasswordButton, logoutButton, createUserButton, deleteUserButton;
    private JTextField memberSearchField;

    private UserSession session;
    private DatabaseManager dbManager;
    private TableRowSorter<DefaultTableModel> memberSorter;

    public EmployeeDashboard(UserSession session) {
        this.session = session;
        this.dbManager = new DatabaseManager();

        welcomeLabel.setText("Employee Portal - Logged in as: " + session.getFullName());

        applyRBACPermissions();
        loadMasterSchedule();
        loadMembersList();
        setupMemberFilter();

        createUserButton.addActionListener(e -> showRegisterUserDialog());
        deleteUserButton.addActionListener(e -> handleDeleteUser());
        cancelMembershipButton.addActionListener(e -> handleCancelMembership());
        logoutButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(getPanel()).dispose();
            new MainAppFrame().setVisible(true);
        });
    }

    private void setupMemberFilter() {
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
                String t = memberSearchField.getText();
                memberSorter.setRowFilter(t.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
            }
        });
    }

    private void applyRBACPermissions() {
        boolean canSch = session.hasPermission("CREATE_EVENT") || session.hasPermission("FULL_ACCESS");
        boolean isStaffOrAdmin = session.hasPermission("MANAGE_MEMBERSHIPS") || session.hasPermission("FULL_ACCESS");
        boolean isAdm = session.hasPermission("FULL_ACCESS");

        createClassButton.setVisible(canSch);
        cancelClassButton.setVisible(canSch);
        cancelMembershipButton.setVisible(isStaffOrAdmin);
        resetPasswordButton.setVisible(isAdm);
        deleteUserButton.setVisible(isAdm);
        createUserButton.setVisible(isAdm);
        if (!canSch) employeeTabs.remove(scheduleTab);
    }

    private void loadMembersList() {
        // Headers MUST match EXACTLY!: ID, Username, Name, Roles, Status
        String[] headers = {"ID", "Username", "Name", "Roles", "Status"};
        List<String[]> data = dbManager.getAllAccountsWithRoles();

        DefaultTableModel model = new DefaultTableModel(data.toArray(new String[0][]), headers) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        membersTable.setModel(model);
        if (memberSorter != null) memberSorter.setModel(model);
    }

    private void handleCancelMembership() {
        int r = membersTable.getSelectedRow();
        if (r != -1) {
            int modelRow = membersTable.convertRowIndexToModel(r);
            int id = Integer.parseInt((String) membersTable.getModel().getValueAt(modelRow, 0));
            if (dbManager.cancelCustomerMembership(id)) {
                JOptionPane.showMessageDialog(employeePanel, "Membership status: Cancelled.");
                loadMembersList();
            }
        }
    }

    private void showRegisterUserDialog() {
        JPanel p = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField u = new JTextField(), ps = new JTextField(), n = new JTextField();
        JComboBox<String> r = new JComboBox<>(new String[]{"CUSTOMER (1)", "EMPLOYEE (2)", "ADMIN (3)"});
        p.add(new JLabel("Username:"));
        p.add(u);
        p.add(new JLabel("Password:"));
        p.add(ps);
        p.add(new JLabel("Full Name:"));
        p.add(n);
        p.add(new JLabel("Role:"));
        p.add(r);
        if (JOptionPane.showConfirmDialog(employeePanel, p, "Register", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (dbManager.createUser(u.getText(), ps.getText(), n.getText(), r.getSelectedIndex() + 1))
                loadMembersList();
        }
    }

    private void handleDeleteUser() {
        int r = membersTable.getSelectedRow();
        if (r != -1) {
            int modelRow = membersTable.convertRowIndexToModel(r);
            int id = Integer.parseInt((String) membersTable.getModel().getValueAt(modelRow, 0));
            if (id != session.getAccountId() && dbManager.deleteUser(id)) loadMembersList();
        }
    }

    private void loadMasterSchedule() {
        String[] h = {"ID", "Coach", "Type", "Name", "Time"};
        masterScheduleTable.setModel(new DefaultTableModel(dbManager.getAllClasses().toArray(new String[0][]), h) {
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