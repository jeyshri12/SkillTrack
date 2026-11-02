package com.skilltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {

    Color purple = new Color(139, 92, 246);
    Color darkPurple = new Color(17, 17, 31);
    Color lightBg = new Color(25, 23, 43);

    public Login() {

        setTitle("SkillTrack | Student Portal");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(17, 17, 31));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(70, 40, 40, 40));

        JLabel logo = new JLabel("🚀  SkillTrack");
        logo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 34));
        logo.setForeground(Color.WHITE);

        JLabel heading = new JLabel(
           "<html><div style='line-height:1.4;'>Build Your Skills.<br>Shape Your Future.</div></html>"
        );
        heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
        heading.setForeground(new Color(248, 250, 252));

        JLabel description = new JLabel(
            "<html>Track your learning journey.<br><br>" +
            "Manage your goals and grow<br>" +
            "your skills with confidence.</html>"
        );

        description.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        description.setForeground(new Color(196, 181, 253));

        leftPanel.add(logo);
        leftPanel.add(Box.createVerticalStrut(85));
        leftPanel.add(heading);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(description);
        leftPanel.add(Box.createVerticalGlue());

        // Right panel
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(25, 23, 43));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(75, 55, 50, 55));

        JLabel welcome = new JLabel("Welcome Back!");
        welcome.setFont(new Font("Arial", Font.BOLD, 27));
        welcome.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(161, 161, 181));

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        usernameLabel.setForeground(Color.WHITE);

        RoundedTextField usernameField = new RoundedTextField();
        usernameField.setPreferredSize(new Dimension(300, 46));
        usernameField.setMaximumSize(new Dimension(300, 46));
        usernameField.setBackground(new Color(33, 31, 54));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 75, 125)),
                new EmptyBorder(8, 14, 8, 14)
            )
        );

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passwordLabel.setForeground(Color.WHITE);

        RoundedPasswordField passwordField = new RoundedPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 46));
        passwordField.setMaximumSize(new Dimension(300, 46));
        passwordField.setBackground(new Color(33, 31, 54));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 75, 125)),
                new EmptyBorder(8, 14, 8, 14)
            )
        );

        RoundedButton loginButton = new RoundedButton("Sign In");

        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(false);
        loginButton.setBackground(purple);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        );
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(300, 46));
        loginButton.setMaximumSize(new Dimension(300, 46));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

       loginButton.addActionListener(e -> {

       String username = usernameField.getText();
       String password = new String(passwordField.getPassword());

       String sql = "SELECT * FROM users WHERE username=? AND password_hash=?";

       try {
           Connection con = DBConnection.getConnection();

           if (con == null) {
              JOptionPane.showMessageDialog(this, "Database connection failed!");
              return;
           }

           PreparedStatement ps = con.prepareStatement(sql);

           ps.setString(1, username);
           ps.setString(2, password);

           ResultSet rs = ps.executeQuery();

            if (rs.next()) {
               int userId = rs.getInt("user_id");

               new Dashboard(userId);
               dispose();

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Incorrect username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE
                );
                usernameField.setText("");
                passwordField.setText("");
            }

           rs.close();
           ps.close();
           con.close();

       } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage());
       }
    });

        rightPanel.add(welcome);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(subtitle);
        rightPanel.add(Box.createVerticalStrut(35));
        rightPanel.add(usernameLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(usernameField);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(passwordLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(loginButton);
        JButton signupButton = new JButton("New user? Create Account");

        signupButton.setForeground(purple);
        signupButton.setBackground(new Color(25, 23, 43));
        signupButton.setFont(new Font("Arial", Font.BOLD, 13));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.setBorderPainted(false);
        signupButton.setFocusPainted(false);
        signupButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        signupButton.addActionListener(e -> new Registration());

        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(signupButton);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
        setVisible(true);
    }

    class RoundedButton extends JButton {

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2.dispose();
        super.paintComponent(g);
    }
}

    class RoundedTextField extends JTextField {

    public RoundedTextField() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(new Color(90, 75, 125));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        g2.dispose();
    }
}

    class RoundedPasswordField extends JPasswordField {

    public RoundedPasswordField() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(new Color(90, 75, 125));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();
    }
}

    public static void main(String[] args) {
        new Login();
    }
}
