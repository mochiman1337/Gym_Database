import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/gym_db?serverTimezone=America/New_York";
    private static final String USER = "root";
    private static final String PASSWORD = "database1337";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public UserSession authenticate(String username, String enteredPassword) {
        String query = "SELECT account_id, full_name, password FROM Account WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getString("password").equals(enteredPassword)) {
                UserSession session = new UserSession(rs.getInt("account_id"), username, rs.getString("full_name"));
                session.setRoles(getUserRoles(conn, session.getAccountId()));
                session.setPermissions(getUserPermissions(conn, session.getAccountId()));
                return session;
            }
        } catch (SQLException e) { System.err.println("Auth Error: " + e.getMessage()); }
        return null;
    }

    private List<String> getUserRoles(Connection conn, int id) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.role_name FROM Role r JOIN Account_Role ar ON r.role_id = ar.role_id WHERE ar.account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) roles.add(rs.getString(1));
        }
        return roles;
    }

    private List<String> getUserPermissions(Connection conn, int id) throws SQLException {
        List<String> perms = new ArrayList<>();
        String sql = "SELECT p.permission_name FROM Account_Role ar JOIN Role_Permission rp ON ar.role_id = rp.role_id " +
                "JOIN Permission p ON rp.permission_id = p.permission_id WHERE ar.account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) perms.add(rs.getString(1));
        }
        return perms;
    }

    public List<String[]> getAllAccountsWithRoles() {
        List<String[]> list = new ArrayList<>();
        // Fetch exactly 5 columns to match headers: ID, Username, Name, Roles, Status
        String sql = "SELECT a.account_id, a.username, a.full_name, " +
                "GROUP_CONCAT(DISTINCT r.role_name SEPARATOR ', ') as roles, " +
                "COALESCE(m.status, 'N/A') as status " +
                "FROM Account a " +
                "LEFT JOIN Account_Role ar ON a.account_id = ar.account_id " +
                "LEFT JOIN Role r ON ar.role_id = r.role_id " +
                "LEFT JOIN Membership m ON a.account_id = m.account_id " +
                "GROUP BY a.account_id";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt(1)),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                });
            }
        } catch (SQLException e) { System.err.println("Load Members Error: " + e.getMessage()); }
        return list;
    }

    public List<String[]> getAllClasses() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT class_id, coach_id, 'GROUP' as type, class_type, class_time FROM Fitness_Class";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{String.valueOf(rs.getInt(1)), String.valueOf(rs.getInt(2)), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException e) { }
        return list;
    }

    public boolean createUser(String u, String p, String n, int rId) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("INSERT INTO Account (username, password, full_name) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, u); ps1.setString(2, p); ps1.setString(3, n); ps1.executeUpdate();
            ResultSet rs = ps1.getGeneratedKeys();
            if (rs.next()) {
                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO Account_Role (account_id, role_id) VALUES (?, ?)");
                ps2.setInt(1, rs.getInt(1)); ps2.setInt(2, rId);
                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException e) { }
        return false;
    }

    public boolean deleteUser(int id) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM Account WHERE account_id = ?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean cancelCustomerMembership(int id) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE Membership SET status = 'cancelled' WHERE account_id = ?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean bookClass(int accId, int classId) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO Class_Booking (account_id, class_id, booking_time) VALUES (?, ?, NOW())")) {
            ps.setInt(1, accId); ps.setInt(2, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean cancelBooking(int bId) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM Class_Booking WHERE booking_id = ?")) {
            ps.setInt(1, bId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<String[]> getAvailableClasses() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT class_id, class_type, class_time FROM Fitness_Class WHERE status = 'scheduled'";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2), rs.getString(3)});
            }
        } catch (SQLException e) { }
        return list;
    }

    public List<String[]> getCustomerBookings(int accId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT cb.booking_id, fc.class_type, fc.class_time, fc.status FROM Class_Booking cb " +
                "JOIN Fitness_Class fc ON cb.class_id = fc.class_id WHERE cb.account_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (SQLException e) { }
        return list;
    }

    public String[] getCustomerProfile(int id) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT full_name, address, contact_info FROM Account WHERE account_id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new String[]{rs.getString(1), rs.getString(2), rs.getString(3)};
        } catch (SQLException e) { }
        return new String[]{"", "", ""};
    }

    public boolean updateCustomerProfile(int id, String n, String a, String c) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE Account SET full_name = ?, address = ?, contact_info = ? WHERE account_id = ?")) {
            ps.setString(1, n); ps.setString(2, a); ps.setString(3, c); ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateMembershipLength(int accountId, int months) {
        String sql = "INSERT INTO Membership (account_id, status, start_date, billing_cycle) VALUES (?, 'active', CURDATE(), ?) " +
                "ON DUPLICATE KEY UPDATE status = 'active'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, months >= 12 ? "yearly" : "monthly");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}