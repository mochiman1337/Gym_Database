import javax.swing.*;
import java.awt.*;

public class MainAppFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainAppFrame() {
        setTitle("Gym Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Log-in Screen
        Login loginScreen = new Login(new Login.LoginListener() {
            @Override
            public void onLoginSuccess(UserSession session) {
                handleLoginRouting(session);
            }

            @Override
            public void onNavigateToRegister() {
                cardLayout.show(mainPanel, "REGISTER");
            }
        });

        // Register Screen: Creating new user
        Register registerScreen = new Register(new Register.RegisterListener() {
            @Override
            public void onRegistrationComplete() {
                cardLayout.show(mainPanel, "LOGIN"); // Returns to Login after creation
            }

            @Override
            public void onCancel() {
                cardLayout.show(mainPanel, "LOGIN"); // Returns to Login Page
            }
        });

        mainPanel.add(loginScreen.getPanel(), "LOGIN");
        mainPanel.add(registerScreen.getPanel(), "REGISTER");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void handleLoginRouting(UserSession session) {
        if (session.hasRole("CUSTOMER") && session.getRoles().size() == 1) {
            CustomerDashboard customerDash = new CustomerDashboard(session);
            mainPanel.add(customerDash.getPanel(), "CUSTOMER");
            cardLayout.show(mainPanel, "CUSTOMER");
        } else {
            EmployeeDashboard employeeDash = new EmployeeDashboard(session);
            mainPanel.add(employeeDash.getPanel(), "EMPLOYEE");
            cardLayout.show(mainPanel, "EMPLOYEE");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainAppFrame().setVisible(true));
    }
}