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

        // Initialize the CardLayout container
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 1. Build the REAL Login Panel from your IntelliJ .form
        Login loginScreen = new Login(new Login.LoginListener() {
            @Override
            public void onLoginSuccess(UserSession session) {
                // When login succeeds, pass the session to the router
                handleLoginRouting(session);
            }
        });

        // Add the login screen to the card layout
        mainPanel.add(loginScreen.getPanel(), "LOGIN");

        add(mainPanel);

        // Start by showing the login screen
        cardLayout.show(mainPanel, "LOGIN");
    }

    /**
     * THE CONTROLLER: This decides where the user goes AFTER logging in.
     */
    private void handleLoginRouting(UserSession session) {

        // Check if they are ONLY a Customer (Note: getRoles() is used because roles is private)
        if (session.hasRole("CUSTOMER") && session.getRoles().size() == 1) {

            // Generate the Customer Dashboard WITH the real session data
            CustomerDashboard customerDash = new CustomerDashboard(session);
            mainPanel.add(customerDash.getPanel(), "CUSTOMER");

            // Switch the screen
            cardLayout.show(mainPanel, "CUSTOMER");

        } else {

            // Generate the Employee Dashboard WITH the real session data
            EmployeeDashboard employeeDash = new EmployeeDashboard(session);
            mainPanel.add(employeeDash.getPanel(), "EMPLOYEE");

            // Switch the screen
            cardLayout.show(mainPanel, "EMPLOYEE");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainAppFrame().setVisible(true));
    }
}