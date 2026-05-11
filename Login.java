import javax.swing.*;

public class Login {
    private JPanel panel;
    private JTextField idTextField;
    private JPasswordField pwTextField;
    private JButton loginButton, createLogin;
    private LoginListener listener;
    private DatabaseManager dbManager;

    public interface LoginListener {
        void onLoginSuccess(UserSession session);
        void onNavigateToRegister();
    }

    public Login(LoginListener listener) {
        this.listener = listener;
        this.dbManager = new DatabaseManager();
        loginButton.addActionListener(e -> attemptLogin());
        createLogin.addActionListener(e -> listener.onNavigateToRegister());
    }

    private void attemptLogin() {
        String username = idTextField.getText().trim();
        String password = new String(pwTextField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please enter both ID and Password.");
            return;
        }

        UserSession session = dbManager.authenticate(username, password);

        if (session != null) {
            if (session.isRequiresPasswordReset()) {
                String newPass = JOptionPane.showInputDialog(panel, "Admin requires a password reset. Enter new password:");
                if (newPass != null && !newPass.trim().isEmpty()) {
                    dbManager.updatePassword(session.getAccountId(), newPass.trim());
                    JOptionPane.showMessageDialog(panel, "Password updated. You have been logged in.");
                } else {
                    JOptionPane.showMessageDialog(panel, "Reset cancelled. Cannot log in.");
                    return;
                }
            }
            idTextField.setText(""); pwTextField.setText("");
            listener.onLoginSuccess(session);
        } else {
            JOptionPane.showMessageDialog(panel, "Invalid ID or Password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public JPanel getPanel() { return panel; }
    private void createUIComponents() { pwTextField = new JPasswordField(); }
}