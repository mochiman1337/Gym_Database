import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {

    private JPanel panel;
    private JTextField idTextField;
    private JPasswordField pwTextField;
    private JButton loginButton;

    // Added to handle database connections and screen routing
    private LoginListener listener;
    private DatabaseManager dbManager;

    // Interface to communicate back to your MainAppFrame (CardLayout)
    public interface LoginListener {
        void onLoginSuccess(UserSession session);
    }

    // Constructor now requires the listener so it knows where to send the user
    public Login(LoginListener listener) {
        this.listener = listener;
        this.dbManager = new DatabaseManager();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = idTextField.getText();
        // JPasswordField requires getting the password as a char array and converting to String
        String password = new String(pwTextField.getPassword());

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please enter both ID and Password.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the database layer to authenticate
        UserSession session = dbManager.authenticate(username, password);

        if (session != null) {
            // Login Success!
            JOptionPane.showMessageDialog(panel, "Welcome, " + session.getFullName() + "!");

            // Clear the fields for security in case they log out later
            idTextField.setText("");
            pwTextField.setText("");

            // Tell the Main Frame to switch to the dashboard
            listener.onLoginSuccess(session);
        } else {
            // Login Failed
            JOptionPane.showMessageDialog(panel, "Invalid ID or Password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRED: This allows your MainAppFrame to grab this panel and display it
    public JPanel getPanel() {
        return panel;
    }

    // Since your .form file has custom-create="true" for pwTextField,
    // you must instantiate it here.
    private void createUIComponents() {
        pwTextField = new JPasswordField();
    }
}