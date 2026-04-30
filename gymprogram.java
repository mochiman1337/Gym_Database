import javax.swing.*;
import java.sql.*;
import java.awt.Color;
import java.awt.Font;

public class gymprogram {

  static int currentAccountId = 0;
  static String currentEmployeeType = "";

  public static void main(String[] args) {
    Database.connect();
    new SignIn();
  }

  static class SignIn extends JFrame {
    SignIn() {
      setTitle("Gym Sign In");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel title = new JLabel("Gym Management Portal");
      title.setBounds(0, 70, 1200, 50);
      title.setHorizontalAlignment(SwingConstants.CENTER);
      title.setFont(new Font("Arial", Font.BOLD, 30));
      title.setForeground(new Color(40, 40, 40));
      add(title);

      JLabel subtitle = new JLabel("Sign in to continue");
      subtitle.setBounds(0, 120, 1200, 30);
      subtitle.setHorizontalAlignment(SwingConstants.CENTER);
      subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
      subtitle.setForeground(Color.GRAY);
      add(subtitle);

      JLabel userLabel = new JLabel("Username:");
      userLabel.setBounds(400, 200, 120, 30);
      add(userLabel);

      JTextField usernameTF = new JTextField();
      usernameTF.setBounds(550, 200, 250, 40);
      add(usernameTF);

      JLabel passLabel = new JLabel("Password:");
      passLabel.setBounds(400, 260, 120, 30);
      add(passLabel);


      JPasswordField passwordPF = new JPasswordField();
      passwordPF.setBounds(550, 260, 250, 40);
      add(passwordPF);

      JLabel typeLabel = new JLabel("Account Type:");
      typeLabel.setBounds(400, 320, 120, 30);
      add(typeLabel);

      JComboBox<String> typeCB = new JComboBox<>(new String[]{"customer", "employee", "admin"});
      typeCB.setBounds(550, 320, 250, 40);
      add(typeCB);

      JButton signInButton = new JButton("Sign In");
      signInButton.setBounds(550, 390, 250, 40);
      signInButton.setBackground(new Color(70, 130, 180));
      signInButton.setForeground(Color.WHITE);
      signInButton.setFont(new Font("Arial", Font.BOLD, 16));
      add(signInButton);

      JButton createButton = new JButton("Create Customer Account");

      createButton.setBounds(0, 460, 1200, 40);
      createButton.setHorizontalAlignment(SwingConstants.CENTER);

      createButton.setBackground(new Color(173, 216, 230));
      createButton.setForeground(Color.BLACK);
      createButton.setFont(new Font("Arial", Font.PLAIN, 15));
      createButton.setFocusPainted(false);

      add(createButton);

      signInButton.addActionListener(e -> {
        try {
          String username = usernameTF.getText();
          String password = new String(passwordPF.getPassword());
          String accountType = typeCB.getSelectedItem().toString();

          Statement statement = Database.connection.createStatement();

          String sql = "SELECT * FROM Account WHERE username='" + username +
                  "' AND password='" + password + "'";

          ResultSet rs = statement.executeQuery(sql);

          if (rs.next()) {
            currentAccountId = rs.getInt("account_id");
            currentEmployeeType = rs.getString("employee_type");

            dispose();

            if (accountType.equals("customer")) {
              new CustomerMenu();
            } else if (accountType.equals("employee")) {
              new EmployeeMenu();
            } else {
              new AdminMenu();
            }

          } else {
            JOptionPane.showMessageDialog(this, "Invalid login");
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      createButton.addActionListener(e -> {
        dispose();
        new CustomerCreateAccount();
      });

      setVisible(true);
    }
  }

  static class CustomerCreateAccount extends JFrame {
    CustomerCreateAccount() {
      setTitle("Create Customer Account");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel userLabel = new JLabel("Username:");
      userLabel.setBounds(400, 160, 120, 30);
      add(userLabel);

      JTextField usernameTF = new JTextField();
      usernameTF.setBounds(550, 160, 250, 40);
      add(usernameTF);

      JLabel passLabel = new JLabel("Password:");
      passLabel.setBounds(400, 220, 120, 30);
      add(passLabel);

      JPasswordField passwordPF = new JPasswordField();
      passwordPF.setBounds(550, 220, 250, 40);
      add(passwordPF);

      JLabel nameLabel = new JLabel("Full Name:");
      nameLabel.setBounds(400, 280, 120, 30);
      add(nameLabel);

      JTextField fullNameTF = new JTextField();
      fullNameTF.setBounds(550, 280, 250, 40);
      add(fullNameTF);

      JLabel addressLabel = new JLabel("Address:");
      addressLabel.setBounds(400, 340, 120, 30);
      add(addressLabel);

      JTextField addressTF = new JTextField();
      addressTF.setBounds(550, 340, 250, 40);
      add(addressTF);

      JLabel contactLabel = new JLabel("Contact Info:");
      contactLabel.setBounds(400, 400, 120, 30);
      add(contactLabel);

      JTextField contactTF = new JTextField();
      contactTF.setBounds(550, 400, 250, 40);
      add(contactTF);

      JButton createButton = new JButton("Create Account");
      createButton.setBounds(550, 470, 250, 40);
      add(createButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(550, 530, 250, 40);
      add(backButton);

      createButton.addActionListener(e -> {
        try {
          Statement statement = Database.connection.createStatement();

          String sql = "INSERT INTO Account(username, password, full_name, address, contact_info) VALUES('"
                  + usernameTF.getText() + "','"
                  + new String(passwordPF.getPassword()) + "','"
                  + fullNameTF.getText() + "','"
                  + addressTF.getText() + "','"
                  + contactTF.getText() + "')";

          statement.executeUpdate(sql);

          JOptionPane.showMessageDialog(this, "Customer account created");

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new SignIn();
      });

      setVisible(true);
    }
  }

  static class CustomerMenu extends JFrame {
    CustomerMenu() {
      setTitle("Customer Menu");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JButton membershipButton = new JButton("Sign Up for Membership");
      membershipButton.setBounds(450, 180, 300, 50);
      add(membershipButton);

      JButton classButton = new JButton("Book Fitness Class");
      classButton.setBounds(450, 250, 300, 50);
      add(classButton);

      JButton trainerButton = new JButton("Book Trainer Appointment");
      trainerButton.setBounds(450, 320, 300, 50);
      add(trainerButton);

      JButton historyButton = new JButton("View History");
      historyButton.setBounds(450, 390, 300, 50);
      add(historyButton);

      JButton updateButton = new JButton("Update My Info");
      updateButton.setBounds(450, 460, 300, 50);
      add(updateButton);

      JButton logoutButton = new JButton("Logout");
      logoutButton.setBounds(450, 530, 300, 50);
      add(logoutButton);

      membershipButton.addActionListener(e -> {
        dispose();
        new MembershipSignUp();
      });

      classButton.addActionListener(e -> {
        dispose();
        new BookFitnessClass();
      });

      trainerButton.addActionListener(e -> {
        dispose();
        new BookTrainerAppointment();
      });

      historyButton.addActionListener(e -> {
        dispose();
        new CustomerHistory();
      });

      updateButton.addActionListener(e -> {
        dispose();
        new UpdateCustomerInfo();
      });

      logoutButton.addActionListener(e -> {
        dispose();
        new SignIn();
      });

      setVisible(true);
    }
  }

  static class MembershipSignUp extends JFrame {
    MembershipSignUp() {
      setTitle("Membership Sign Up");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel billingLabel = new JLabel("Billing Cycle:");
      billingLabel.setBounds(400, 250, 120, 30);
      add(billingLabel);

      JComboBox<String> billingCB = new JComboBox<>(new String[]{"monthly", "yearly"});
      billingCB.setBounds(550, 250, 250, 40);
      add(billingCB);

      JLabel startLabel = new JLabel("Start Date YYYY-MM-DD:");
      startLabel.setBounds(370, 310, 170, 30);
      add(startLabel);

      JTextField startDateTF = new JTextField();
      startDateTF.setBounds(550, 310, 250, 40);
      add(startDateTF);

      JButton saveButton = new JButton("Save Membership");
      saveButton.setBounds(550, 390, 250, 40);
      add(saveButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(550, 450, 250, 40);
      add(backButton);

      saveButton.addActionListener(e -> {
        try {
          Statement statement = Database.connection.createStatement();

          String sql = "INSERT INTO Membership(account_id, billing_cycle, start_date, status) VALUES("
                  + currentAccountId + ",'"
                  + billingCB.getSelectedItem().toString() + "','"
                  + startDateTF.getText() + "','active')";

          statement.executeUpdate(sql);

          JOptionPane.showMessageDialog(this, "Membership saved");

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new CustomerMenu();
      });

      setVisible(true);
    }
  }

  static class BookFitnessClass extends JFrame {
    BookFitnessClass() {
      setTitle("Book Fitness Class");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(250, 120, 700, 300);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT class_id, coach_id, class_type, class_time, status FROM Fitness_Class WHERE status='scheduled'");
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading classes: " + ex.getMessage());
        ex.printStackTrace();
      }

      JButton bookButton = new JButton("Book Selected Class");
      bookButton.setBounds(450, 470, 300, 40);
      add(bookButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(450, 530, 300, 40);
      add(backButton);

      bookButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a class first");
          } else {
            int classId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();

            String sql = "INSERT INTO Class_Booking(account_id, class_id, booking_time) VALUES("
                    + currentAccountId + "," + classId + ", NOW())";

            statement.executeUpdate(sql);

            JOptionPane.showMessageDialog(this, "Class booked");
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new CustomerMenu();
      });

      setVisible(true);
    }
  }

  static class BookTrainerAppointment extends JFrame {
    BookTrainerAppointment() {
      setTitle("Book Trainer Appointment");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable trainerTable = new JTable();
      JScrollPane scrollPane = new JScrollPane(trainerTable);
      scrollPane.setBounds(250, 100, 700, 200);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT account_id, full_name, employee_type FROM Account WHERE employee_type='trainer'");
        trainerTable.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading trainers: " + ex.getMessage());
        ex.printStackTrace();
      }

      JLabel timeLabel = new JLabel("Appointment Time YYYY-MM-DD HH:MM:SS:");
      timeLabel.setBounds(320, 340, 250, 30);
      add(timeLabel);

      JTextField timeTF = new JTextField();
      timeTF.setBounds(580, 340, 250, 40);
      add(timeTF);

      JButton requestButton = new JButton("Request Appointment");
      requestButton.setBounds(450, 430, 300, 40);
      add(requestButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(450, 490, 300, 40);
      add(backButton);

      requestButton.addActionListener(e -> {
        try {
          int row = trainerTable.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a trainer first");
          } else {
            int trainerId = Integer.parseInt(trainerTable.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();

            String sql = "INSERT INTO Trainer_Appointment(customer_id, trainer_id, appointment_time, status) VALUES("
                    + currentAccountId + "," + trainerId + ",'"
                    + timeTF.getText() + "','pending')";

            statement.executeUpdate(sql);

            JOptionPane.showMessageDialog(this, "Appointment requested");
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new CustomerMenu();
      });

      setVisible(true);
    }
  }

  static class CustomerHistory extends JFrame {
    CustomerHistory() {
      setTitle("Customer History");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel classLabel = new JLabel("Fitness Class History");
      classLabel.setBounds(250, 80, 200, 30);
      add(classLabel);

      JTable classTable = new JTable();
      JScrollPane classScroll = new JScrollPane(classTable);
      classScroll.setBounds(250, 120, 700, 200);
      add(classScroll);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT cb.booking_id, fc.class_type, fc.class_time, fc.status FROM Class_Booking cb JOIN Fitness_Class fc ON cb.class_id=fc.class_id WHERE cb.account_id=" + currentAccountId);
        classTable.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading class history: " + ex.getMessage());
        ex.printStackTrace();
      }

      JLabel trainerLabel = new JLabel("Trainer Appointment History");
      trainerLabel.setBounds(250, 340, 250, 30);
      add(trainerLabel);

      JTable appointmentTable = new JTable();
      JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
      appointmentScroll.setBounds(250, 380, 700, 200);
      add(appointmentScroll);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT appointment_id, trainer_id, appointment_time, status FROM Trainer_Appointment WHERE customer_id=" + currentAccountId);
        appointmentTable.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading appointment history: " + ex.getMessage());
        ex.printStackTrace();
      }

      JButton backButton = new JButton("Back");
      backButton.setBounds(450, 620, 300, 40);
      add(backButton);

      backButton.addActionListener(e -> {
        dispose();
        new CustomerMenu();
      });

      setVisible(true);
    }
  }

  static class UpdateCustomerInfo extends JFrame {
    UpdateCustomerInfo() {
      setTitle("Update Customer Info");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel nameLabel = new JLabel("Full Name:");
      nameLabel.setBounds(400, 240, 120, 30);
      add(nameLabel);

      JTextField nameTF = new JTextField();
      nameTF.setBounds(550, 240, 250, 40);
      add(nameTF);

      JLabel addressLabel = new JLabel("Address:");
      addressLabel.setBounds(400, 300, 120, 30);
      add(addressLabel);

      JTextField addressTF = new JTextField();
      addressTF.setBounds(550, 300, 250, 40);
      add(addressTF);

      JLabel contactLabel = new JLabel("Contact Info:");
      contactLabel.setBounds(400, 360, 120, 30);
      add(contactLabel);

      JTextField contactTF = new JTextField();
      contactTF.setBounds(550, 360, 250, 40);
      add(contactTF);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT full_name, address, contact_info FROM Account WHERE account_id=" + currentAccountId);

        if (rs.next()) {
          nameTF.setText(rs.getString("full_name"));
          addressTF.setText(rs.getString("address"));
          contactTF.setText(rs.getString("contact_info"));
        }

      } catch (Exception ex) {
        ex.printStackTrace();
      }

      JButton updateButton = new JButton("Update Info");
      updateButton.setBounds(550, 430, 250, 40);
      add(updateButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(550, 490, 250, 40);
      add(backButton);

      updateButton.addActionListener(e -> {
        try {
          Statement statement = Database.connection.createStatement();

          String sql = "UPDATE Account SET full_name='" + nameTF.getText()
                  + "', address='" + addressTF.getText()
                  + "', contact_info='" + contactTF.getText()
                  + "' WHERE account_id=" + currentAccountId;

          statement.executeUpdate(sql);

          JOptionPane.showMessageDialog(this, "Customer info updated");

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new CustomerMenu();
      });

      setVisible(true);
    }
  }

  static class EmployeeMenu extends JFrame {
    EmployeeMenu() {
      setTitle("Employee Menu");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JButton viewButton = new JButton("View Members");
      viewButton.setBounds(450, 180, 300, 50);
      add(viewButton);

      JButton cancelMembershipButton = new JButton("Cancel Membership");
      cancelMembershipButton.setBounds(450, 250, 300, 50);
      add(cancelMembershipButton);

      JButton createClassButton = new JButton("Create Fitness Class");
      createClassButton.setBounds(450, 320, 300, 50);
      add(createClassButton);

      JButton cancelClassButton = new JButton("Cancel Fitness Class");
      cancelClassButton.setBounds(450, 390, 300, 50);
      add(cancelClassButton);

      JButton appointmentsButton = new JButton("Trainer Appointments");
      appointmentsButton.setBounds(450, 460, 300, 50);
      add(appointmentsButton);

      JButton logoutButton = new JButton("Logout");
      logoutButton.setBounds(450, 530, 300, 50);
      add(logoutButton);

      viewButton.addActionListener(e -> {
        dispose();
        new ReceptionistViewMembers();
      });

      cancelMembershipButton.addActionListener(e -> {
        dispose();
        new CancelMembership();
      });

      createClassButton.addActionListener(e -> {
        dispose();
        new CreateFitnessClass();
      });

      cancelClassButton.addActionListener(e -> {
        dispose();
        new CancelFitnessClass();
      });

      appointmentsButton.addActionListener(e -> {
        dispose();
        new TrainerAppointments();
      });

      logoutButton.addActionListener(e -> {
        dispose();
        new SignIn();
      });

      setVisible(true);
    }
  }

  static class ReceptionistViewMembers extends JFrame {
    ReceptionistViewMembers() {
      setTitle("View Members");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(120, 100, 950, 450);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT a.account_id, a.username, a.full_name, a.address, a.contact_info, m.membership_id, m.billing_cycle, m.status AS membership_status FROM Account a LEFT JOIN Membership m ON a.account_id=m.account_id");
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading members: " + ex.getMessage());
        ex.printStackTrace();
      }

      JButton backButton = new JButton("Back");
      backButton.setBounds(450, 600, 300, 40);
      add(backButton);

      backButton.addActionListener(e -> {
        dispose();
        new EmployeeMenu();
      });

      setVisible(true);
    }
  }

  static class CancelMembership extends JFrame {
    CancelMembership() {
      setTitle("Cancel Membership");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(250, 120, 700, 300);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT membership_id, account_id, billing_cycle, start_date, status FROM Membership WHERE status='active'");
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading memberships: " + ex.getMessage());
        ex.printStackTrace();
      }

      JButton cancelButton = new JButton("Cancel Membership");
      cancelButton.setBounds(450, 470, 300, 40);
      add(cancelButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(450, 530, 300, 40);
      add(backButton);

      cancelButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a membership first");
          } else {
            int membershipId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("UPDATE Membership SET status='cancelled' WHERE membership_id=" + membershipId);

            JOptionPane.showMessageDialog(this, "Membership cancelled");

            ResultSet rs = statement.executeQuery("SELECT membership_id, account_id, billing_cycle, start_date, status FROM Membership WHERE status='active'");
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new EmployeeMenu();
      });

      setVisible(true);
    }
  }

  static class CreateFitnessClass extends JFrame {
    CreateFitnessClass() {
      setTitle("Create Fitness Class");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel typeLabel = new JLabel("Class Type:");
      typeLabel.setBounds(400, 240, 120, 30);
      add(typeLabel);

      JComboBox<String> typeCB = new JComboBox<>(new String[]{"yoga", "cardio", "strength", "pilates", "crossfit"});
      typeCB.setBounds(550, 240, 250, 40);
      add(typeCB);

      JLabel timeLabel = new JLabel("Class Time YYYY-MM-DD HH:MM:SS:");
      timeLabel.setBounds(320, 300, 230, 30);
      add(timeLabel);

      JTextField timeTF = new JTextField();
      timeTF.setBounds(550, 300, 250, 40);
      add(timeTF);

      JButton createButton = new JButton("Create Class");
      createButton.setBounds(550, 380, 250, 40);
      add(createButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(550, 440, 250, 40);
      add(backButton);

      createButton.addActionListener(e -> {
        try {
          Statement statement = Database.connection.createStatement();

          String sql = "INSERT INTO Fitness_Class(coach_id, class_type, class_time, status) VALUES("
                  + currentAccountId + ",'"
                  + typeCB.getSelectedItem().toString() + "','"
                  + timeTF.getText() + "','scheduled')";

          statement.executeUpdate(sql);

          JOptionPane.showMessageDialog(this, "Fitness class created");

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new EmployeeMenu();
      });

      setVisible(true);
    }
  }

  static class CancelFitnessClass extends JFrame {
    CancelFitnessClass() {
      setTitle("Cancel Fitness Class");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(250, 120, 700, 300);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT class_id, coach_id, class_type, class_time, status FROM Fitness_Class WHERE status='scheduled'");
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading classes: " + ex.getMessage());
        ex.printStackTrace();
      }

      JButton cancelButton = new JButton("Cancel Class");
      cancelButton.setBounds(450, 470, 300, 40);
      add(cancelButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(450, 530, 300, 40);
      add(backButton);

      cancelButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a class first");
          } else {
            int classId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("UPDATE Fitness_Class SET status='cancelled' WHERE class_id=" + classId);

            JOptionPane.showMessageDialog(this, "Class cancelled");

            ResultSet rs = statement.executeQuery("SELECT class_id, coach_id, class_type, class_time, status FROM Fitness_Class WHERE status='scheduled'");
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new EmployeeMenu();
      });

      setVisible(true);
    }
  }

  static class TrainerAppointments extends JFrame {
    TrainerAppointments() {
      setTitle("Trainer Appointments");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(200, 120, 800, 300);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT appointment_id, customer_id, trainer_id, appointment_time, status FROM Trainer_Appointment WHERE trainer_id=" + currentAccountId);
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage());
        ex.printStackTrace();
      }

      JButton acceptButton = new JButton("Accept");
      acceptButton.setBounds(250, 470, 160, 40);
      add(acceptButton);

      JButton declineButton = new JButton("Decline");
      declineButton.setBounds(430, 470, 160, 40);
      add(declineButton);

      JButton cancelButton = new JButton("Cancel");
      cancelButton.setBounds(610, 470, 160, 40);
      add(cancelButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(790, 470, 160, 40);
      add(backButton);

      acceptButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first");
          } else {
            int appointmentId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("UPDATE Trainer_Appointment SET status='accepted' WHERE appointment_id=" + appointmentId);

            JOptionPane.showMessageDialog(this, "Appointment accepted");

            ResultSet rs = statement.executeQuery("SELECT appointment_id, customer_id, trainer_id, appointment_time, status FROM Trainer_Appointment WHERE trainer_id=" + currentAccountId);
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      declineButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first");
          } else {
            int appointmentId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("UPDATE Trainer_Appointment SET status='declined' WHERE appointment_id=" + appointmentId);

            JOptionPane.showMessageDialog(this, "Appointment declined");

            ResultSet rs = statement.executeQuery("SELECT appointment_id, customer_id, trainer_id, appointment_time, status FROM Trainer_Appointment WHERE trainer_id=" + currentAccountId);
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      cancelButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first");
          } else {
            int appointmentId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("UPDATE Trainer_Appointment SET status='cancelled' WHERE appointment_id=" + appointmentId);

            JOptionPane.showMessageDialog(this, "Appointment cancelled");

            ResultSet rs = statement.executeQuery("SELECT appointment_id, customer_id, trainer_id, appointment_time, status FROM Trainer_Appointment WHERE trainer_id=" + currentAccountId);
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new EmployeeMenu();
      });

      setVisible(true);
    }
  }

  static class AdminMenu extends JFrame {
    AdminMenu() {
      setTitle("Admin Menu");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JButton createEmployeeButton = new JButton("Create Employee Account");
      createEmployeeButton.setBounds(450, 160, 300, 50);
      add(createEmployeeButton);

      JButton manageCustomersButton = new JButton("Manage Customers");
      manageCustomersButton.setBounds(450, 230, 300, 50);
      add(manageCustomersButton);

      JButton manageEmployeesButton = new JButton("Manage Employees");
      manageEmployeesButton.setBounds(450, 300, 300, 50);
      add(manageEmployeesButton);

      JButton deleteDataButton = new JButton("Delete Gym Data");
      deleteDataButton.setBounds(450, 370, 300, 50);
      add(deleteDataButton);

      JButton resetPasswordButton = new JButton("Reset Password");
      resetPasswordButton.setBounds(450, 440, 300, 50);
      add(resetPasswordButton);

      JButton logoutButton = new JButton("Logout");
      logoutButton.setBounds(450, 510, 300, 50);
      add(logoutButton);

      createEmployeeButton.addActionListener(e -> {
        dispose();
        new CreateEmployeeAccount();
      });

      manageCustomersButton.addActionListener(e -> {
        dispose();
        new ManageCustomers();
      });

      manageEmployeesButton.addActionListener(e -> {
        dispose();
        new ManageEmployees();
      });

      deleteDataButton.addActionListener(e -> {
        dispose();
        new DeleteGymData();
      });

      resetPasswordButton.addActionListener(e -> {
        dispose();
        new ResetPassword();
      });

      logoutButton.addActionListener(e -> {
        dispose();
        new SignIn();
      });

      setVisible(true);
    }
  }

  static class CreateEmployeeAccount extends JFrame {
    CreateEmployeeAccount() {
      setTitle("Create Employee Account");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel userLabel = new JLabel("Username:");
      userLabel.setBounds(400, 180, 120, 30);
      add(userLabel);

      JTextField usernameTF = new JTextField();
      usernameTF.setBounds(550, 180, 250, 40);
      add(usernameTF);

      JLabel passLabel = new JLabel("Password:");
      passLabel.setBounds(400, 240, 120, 30);
      add(passLabel);

      JPasswordField passwordPF = new JPasswordField();
      passwordPF.setBounds(550, 240, 250, 40);
      add(passwordPF);

      JLabel nameLabel = new JLabel("Full Name:");
      nameLabel.setBounds(400, 300, 120, 30);
      add(nameLabel);

      JTextField fullNameTF = new JTextField();
      fullNameTF.setBounds(550, 300, 250, 40);
      add(fullNameTF);

      JLabel typeLabel = new JLabel("Employee Type:");
      typeLabel.setBounds(400, 360, 120, 30);
      add(typeLabel);

      JComboBox<String> typeCB = new JComboBox<>(new String[]{"receptionist", "fitness_coach", "trainer"});
      typeCB.setBounds(550, 360, 250, 40);
      add(typeCB);

      JButton createButton = new JButton("Create Employee");
      createButton.setBounds(550, 430, 250, 40);
      add(createButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(550, 490, 250, 40);
      add(backButton);

      createButton.addActionListener(e -> {
        try {
          Statement statement = Database.connection.createStatement();

          String sql = "INSERT INTO Account(username, password, full_name, employee_type) VALUES('"
                  + usernameTF.getText() + "','"
                  + new String(passwordPF.getPassword()) + "','"
                  + fullNameTF.getText() + "','"
                  + typeCB.getSelectedItem().toString() + "')";

          statement.executeUpdate(sql);

          JOptionPane.showMessageDialog(this, "Employee account created");

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new AdminMenu();
      });

      setVisible(true);
    }
  }

  static class ManageCustomers extends JFrame {
    ManageCustomers() {
      setTitle("Manage Customers");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(200, 100, 800, 300);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT account_id, username, full_name, address, contact_info FROM Account WHERE employee_type IS NULL");
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
        ex.printStackTrace();
      }

      JLabel nameLabel = new JLabel("New Full Name:");
      nameLabel.setBounds(300, 430, 120, 30);
      add(nameLabel);

      JTextField nameTF = new JTextField();
      nameTF.setBounds(430, 430, 250, 35);
      add(nameTF);

      JButton modifyButton = new JButton("Modify Name");
      modifyButton.setBounds(700, 430, 180, 35);
      add(modifyButton);

      JButton deleteButton = new JButton("Delete Selected");
      deleteButton.setBounds(400, 500, 180, 40);
      add(deleteButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(610, 500, 180, 40);
      add(backButton);

      modifyButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer first");
          } else {
            int accountId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("UPDATE Account SET full_name='" + nameTF.getText() + "' WHERE account_id=" + accountId);

            JOptionPane.showMessageDialog(this, "Customer modified");

            ResultSet rs = statement.executeQuery("SELECT account_id, username, full_name, address, contact_info FROM Account WHERE employee_type IS NULL");
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      deleteButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer first");
          } else {
            int accountId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("DELETE FROM Account WHERE account_id=" + accountId);

            JOptionPane.showMessageDialog(this, "Customer deleted");

            ResultSet rs = statement.executeQuery("SELECT account_id, username, full_name, address, contact_info FROM Account WHERE employee_type IS NULL");
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new AdminMenu();
      });

      setVisible(true);
    }
  }

  static class ManageEmployees extends JFrame {
    ManageEmployees() {
      setTitle("Manage Employees");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(200, 100, 800, 300);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT account_id, username, full_name, employee_type FROM Account WHERE employee_type IS NOT NULL");
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage());
        ex.printStackTrace();
      }

      JLabel nameLabel = new JLabel("New Full Name:");
      nameLabel.setBounds(300, 430, 120, 30);
      add(nameLabel);

      JTextField nameTF = new JTextField();
      nameTF.setBounds(430, 430, 250, 35);
      add(nameTF);

      JButton modifyButton = new JButton("Modify Name");
      modifyButton.setBounds(700, 430, 180, 35);
      add(modifyButton);

      JButton deleteButton = new JButton("Delete Selected");
      deleteButton.setBounds(400, 500, 180, 40);
      add(deleteButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(610, 500, 180, 40);
      add(backButton);

      modifyButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee first");
          } else {
            int accountId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("UPDATE Account SET full_name='" + nameTF.getText() + "' WHERE account_id=" + accountId);

            JOptionPane.showMessageDialog(this, "Employee modified");

            ResultSet rs = statement.executeQuery("SELECT account_id, username, full_name, employee_type FROM Account WHERE employee_type IS NOT NULL");
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      deleteButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee first");
          } else {
            int accountId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("DELETE FROM Account WHERE account_id=" + accountId);

            JOptionPane.showMessageDialog(this, "Employee deleted");

            ResultSet rs = statement.executeQuery("SELECT account_id, username, full_name, employee_type FROM Account WHERE employee_type IS NOT NULL");
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new AdminMenu();
      });

      setVisible(true);
    }
  }

  static class DeleteGymData extends JFrame {
    DeleteGymData() {
      setTitle("Delete Gym Data");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JComboBox<String> dataTypeCB = new JComboBox<>(new String[]{
              "Membership",
              "Fitness_Class",
              "Class_Booking",
              "Trainer_Appointment"
      });

      dataTypeCB.setBounds(475, 70, 250, 40);
      add(dataTypeCB);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(200, 130, 800, 300);
      add(scrollPane);

      JButton loadButton = new JButton("Load Data");
      loadButton.setBounds(350, 480, 160, 40);
      add(loadButton);

      JButton deleteButton = new JButton("Delete Selected");
      deleteButton.setBounds(530, 480, 160, 40);
      add(deleteButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(710, 480, 160, 40);
      add(backButton);

      loadButton.addActionListener(e -> {
        try {
          String tableName = dataTypeCB.getSelectedItem().toString();

          Statement statement = Database.connection.createStatement();
          ResultSet rs = statement.executeQuery("SELECT * FROM " + tableName);

          table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      deleteButton.addActionListener(e -> {
        try {
          String tableName = dataTypeCB.getSelectedItem().toString();
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
          } else {
            int selectedId = Integer.parseInt(table.getValueAt(row, 0).toString());
            String idColumn;

            if (tableName.equals("Membership")) {
              idColumn = "membership_id";
            } else if (tableName.equals("Fitness_Class")) {
              idColumn = "class_id";
            } else if (tableName.equals("Class_Booking")) {
              idColumn = "booking_id";
            } else {
              idColumn = "appointment_id";
            }

            Statement statement = Database.connection.createStatement();
            statement.executeUpdate("DELETE FROM " + tableName + " WHERE " + idColumn + "=" + selectedId);

            JOptionPane.showMessageDialog(this, "Selected record deleted");

            ResultSet rs = statement.executeQuery("SELECT * FROM " + tableName);
            table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new AdminMenu();
      });

      setVisible(true);
    }
  }

  static class ResetPassword extends JFrame {
    ResetPassword() {
      setTitle("Reset Password");
      setBounds(0, 0, 1200, 800);
      setLayout(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      scrollPane.setBounds(250, 100, 700, 250);
      add(scrollPane);

      try {
        Statement statement = Database.connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT account_id, username, full_name, employee_type FROM Account");
        table.setModel(net.proteanit.sql.DbUtils.resultSetToTableModel(rs));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage());
        ex.printStackTrace();
      }

      JLabel passLabel = new JLabel("New Password:");
      passLabel.setBounds(400, 390, 120, 30);
      add(passLabel);

      JPasswordField passPF = new JPasswordField();
      passPF.setBounds(550, 390, 250, 40);
      add(passPF);

      JButton resetButton = new JButton("Reset Password");
      resetButton.setBounds(550, 460, 250, 40);
      add(resetButton);

      JButton backButton = new JButton("Back");
      backButton.setBounds(550, 520, 250, 40);
      add(backButton);

      resetButton.addActionListener(e -> {
        try {
          int row = table.getSelectedRow();

          if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an account first");
          } else {
            int accountId = Integer.parseInt(table.getValueAt(row, 0).toString());

            Statement statement = Database.connection.createStatement();

            statement.executeUpdate("UPDATE Account SET password='" +
                    new String(passPF.getPassword()) + "' WHERE account_id=" + accountId);

            JOptionPane.showMessageDialog(this, "Password reset");
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
          ex.printStackTrace();
        }
      });

      backButton.addActionListener(e -> {
        dispose();
        new AdminMenu();
      });

      setVisible(true);
    }
  }
}
