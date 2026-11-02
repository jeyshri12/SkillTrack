
package com.skilltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Registration extends JFrame {

    Color purple = new Color(139, 92, 246);
    Color darkPurple = new Color(17, 17, 31);
    Color background = new Color(17, 17, 31);

    JTextField nameField, emailField, departmentField, yearField, usernameField;
    JPasswordField passwordField;

    public Registration() {

        setTitle("SkillTrack - Create Account");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(17, 17, 31));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 45, 25, 45));

        JLabel title = new JLabel("Create Your Account");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(196, 181, 253));

        JLabel subtitle = new JLabel("Join SkillTrack and track your growth");
        subtitle.setForeground(new Color(161, 161, 181));

        nameField = new JTextField();
        emailField = new JTextField();
        departmentField = new JTextField();
        yearField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        addField(panel, "Student Name", nameField);
        addField(panel, "Email Address", emailField);
        addField(panel, "Department", departmentField);
        addField(panel, "Year of Study", yearField);
        addField(panel, "Username", usernameField);
        addField(panel, "Password", passwordField);

        JButton registerButton = new JButton("Create Account");

        registerButton.setBackground(new Color(139, 92, 246));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerButton.setMaximumSize(new Dimension(400, 46));
        registerButton.setPreferredSize(new Dimension(400, 46));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        registerButton.addActionListener(e -> registerStudent());

        panel.add(Box.createVerticalStrut(20));
        panel.add(registerButton);

        add(panel, BorderLayout.CENTER);

        JPanel heading = new JPanel();
        heading.setBackground(new Color(17, 17, 31));
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBorder(new EmptyBorder(25, 45, 0, 45));
        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(subtitle);

        add(heading, BorderLayout.NORTH);

        setVisible(true);
    }

    private void addField(JPanel panel, String label, JTextField field) {

    JLabel text = new JLabel(label);
    text.setFont(new Font("Segoe UI", Font.BOLD, 13));
    text.setForeground(new Color(196, 181, 253));
    text.setAlignmentX(Component.LEFT_ALIGNMENT);

    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    field.setForeground(Color.WHITE);
    field.setBackground(new Color(33, 31, 54));
    field.setCaretColor(Color.WHITE);
    field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 70, 110), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));

    field.setMaximumSize(new Dimension(400, 38));
    field.setPreferredSize(new Dimension(400, 38));
    field.setAlignmentX(Component.LEFT_ALIGNMENT);

    panel.add(Box.createVerticalStrut(12));
    panel.add(text);
    panel.add(Box.createVerticalStrut(5));
    panel.add(field);
}

    private void registerStudent() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentField.getText().trim();
        String yearText = yearField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() ||
            department.isEmpty() || yearText.isEmpty() ||
            username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        int year;

        try {
            year = Integer.parseInt(yearText);

            if (year < 1 || year > 5) {
                JOptionPane.showMessageDialog(this,
                    "Enter a valid study year (1 to 5).");
                return;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Year must be a number.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email!");
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this,
                "Password must contain at least 8 characters.");
            return;
        }

        String userSQL =
            "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        String studentSQL =
            "INSERT INTO students (user_id, name, email, department, year) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(this,
                    "Database connection failed!");
                return;
            }

            con.setAutoCommit(false);

            try {
                int userId;

                try (PreparedStatement ps = con.prepareStatement(
                        userSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, "Student");

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        userId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(studentSQL)) {

                    ps.setInt(1, userId);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setString(4, department);
                    ps.setInt(5, year);

                    ps.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(this,
                    "Account created successfully! You can now log in.");

                dispose();

            } catch (SQLException ex) {
                con.rollback();

                if (ex.getErrorCode() == 1062) {
                    JOptionPane.showMessageDialog(this,
                        "Username or email already exists!");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Registration failed: " + ex.getMessage());
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Registration();
    }
}
