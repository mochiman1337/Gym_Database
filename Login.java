import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JPanel panel;
    private JTextField idTextField;
    private JPasswordField pwTextField;
    private JButton loginButton;
    private JButton createLogin;

    private LoginListener listener;
    private DatabaseManager dbManager;

    public interface LoginListener {
        void onLoginSuccess(UserSession session);

        void onNavigateToRegister();
    }

    public Login(LoginListener listener) {
        this.listener = listener;
        this.dbManager = new DatabaseManager();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Go to Register screen
        createLogin.addActionListener(e -> listener.onNavigateToRegister());
    }

    private void attemptLogin() {
        String username = idTextField.getText();
        String password = new String(pwTextField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please enter both ID and Password.");
            return;
        }

        UserSession session = dbManager.authenticate(username, password);

        if (session != null) {
            idTextField.setText("");
            pwTextField.setText("");
            listener.onLoginSuccess(session);
        } else {
            JOptionPane.showMessageDialog(panel, "Invalid ID or Password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createUIComponents() {
        pwTextField = new JPasswordField();
    }
}