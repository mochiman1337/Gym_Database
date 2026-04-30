import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // Replace with your actual database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/GymDatabase";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Attempts to log the user in. Returns a UserSession if successful, or null if it fails.
     */
    public UserSession authenticate(String username, String enteredPassword) {
        UserSession session = null;

        // Query 1: Verify identity and password
        String authQuery = "SELECT account_id, full_name, password FROM Account WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement authStmt = conn.prepareStatement(authQuery)) {

            authStmt.setString(1, username);
            ResultSet rs = authStmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                // Note: For a production app, use BCrypt to hash/compare passwords!
                // For this school project, we are doing a direct string comparison.
                if (storedPassword.equals(enteredPassword)) {
                    int accountId = rs.getInt("account_id");
                    String fullName = rs.getString("full_name");

                    // Create the base session
                    session = new UserSession(accountId, username, fullName);

                    // Populate Roles and Permissions
                    session.setRoles(getUserRoles(conn, accountId));
                    session.setPermissions(getUserPermissions(conn, accountId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Login Error: " + e.getMessage());
        }
        return session; // Will be null if login failed
    }

    // Helper Method: Get Roles
    private List<String> getUserRoles(Connection conn, int accountId) throws SQLException {
        List<String> roles = new ArrayList<>();
        String roleQuery = "SELECT r.role_name FROM Role r " +
                "JOIN Account_Role ar ON r.role_id = ar.role_id " +
                "WHERE ar.account_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(roleQuery)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }
        }
        return roles;
    }

    // Helper Method: Get Permissions
    private List<String> getUserPermissions(Connection conn, int accountId) throws SQLException {
        List<String> permissions = new ArrayList<>();
        String permQuery = "SELECT rp.permission_name FROM Role_Permission rp " +
                "JOIN Account_Role ar ON rp.role_id = ar.role_id " +
                "WHERE ar.account_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(permQuery)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                permissions.add(rs.getString("permission_name"));
            }
        }
        return permissions;
    }
}