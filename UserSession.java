import java.util.ArrayList;
import java.util.List;

public class UserSession {
    private int accountId;
    private String username;
    private String fullName;
    private List<String> roles;
    private List<String> permissions;

    public UserSession(int accountId, String username, String fullName) {
        this.accountId = accountId;
        this.username = username;
        this.fullName = fullName;
        this.roles = new ArrayList<>();
        this.permissions = new ArrayList<>();
    }

    // Getters
    public int getAccountId() { return accountId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public List<String> getRoles() { return roles; }
    public List<String> getPermissions() { return permissions; }

    // Setters for the lists
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }

    // RBAC Checkers (Used by your GUI to show/hide buttons)
    public boolean hasRole(String roleName) {
        return roles.contains(roleName);
    }

    public boolean hasPermission(String permissionName) {
        return permissions.contains(permissionName);
    }
}