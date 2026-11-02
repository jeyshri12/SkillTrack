package com.skilltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyProfile extends JFrame {

    Color purple = new Color(167, 139, 250);
    Color darkPurple = new Color(25, 23, 43);
    Color background = new Color(17, 17, 31);

    int userId;

    public MyProfile(int userId) {

        this.userId = userId;

        setTitle("SkillTrack - My Profile");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(background);
        main.setBorder(new EmptyBorder(30, 35, 30, 35));

        JLabel heading = new JLabel("👤 My Profile");
        heading.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
            "View your personal and academic information"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitle);

        JPanel profilePanel = new JPanel();
        profilePanel.setBackground(new Color(33, 31, 54));
        profilePanel.setLayout(new GridLayout(2, 4, 15, 15));
        profilePanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        loadProfile(profilePanel);

        JButton backButton = new JButton("← Back to Dashboard");
        JButton editButton = new JButton("✏️  Edit Profile");

        editButton.setBackground(new Color(139, 92, 246));
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editProfile());

        backButton.setBackground(purple);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 13));
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            dispose();
            new Dashboard(userId);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(backButton);
        bottom.add(editButton);

        main.add(titlePanel, BorderLayout.NORTH);
        main.add(profilePanel, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);
        setVisible(true);
    }

    private void loadProfile(JPanel profilePanel) {

        String sql = "SELECT s.student_id, s.name, s.email, s.department, s.year, u.username " +
                     "FROM students s " + "JOIN users u ON s.user_id = u.user_id " + "WHERE s.user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                addDetail(profilePanel, "Student Name",
                    rs.getString("name"));

                addDetail(profilePanel, "Email Address",
                    rs.getString("email"));

                addDetail(profilePanel, "Department",
                    rs.getString("department"));

                addDetail(profilePanel, "Academic Year",
                    String.valueOf(rs.getInt("year")));

                addDetail(profilePanel, "Student ID", 
                    String.valueOf(rs.getInt("student_id")));
                addDetail(profilePanel, "Username", 
                    rs.getString("username"));

                int studentId = rs.getInt("student_id");

                int totalSkills = 0;
                int completedGoals = 0;

                String skillSql = "SELECT COUNT(*) FROM skills WHERE student_id = ?";
                try (PreparedStatement skillPs = con.prepareStatement(skillSql)) {
                    skillPs.setInt(1, studentId);
                    ResultSet skillRs = skillPs.executeQuery();

                    if (skillRs.next()) {
                        totalSkills = skillRs.getInt(1);
                    }
                }

                String goalSql = "SELECT COUNT(*) FROM goals WHERE student_id = ? AND status = 'Completed'";
                try (PreparedStatement goalPs = con.prepareStatement(goalSql)) {
                    goalPs.setInt(1, studentId);
                    ResultSet goalRs = goalPs.executeQuery();

                    if (goalRs.next()) {
                       completedGoals = goalRs.getInt(1);
                     }
                }

                addDetail(profilePanel, "Total Skills", String.valueOf(totalSkills));
                addDetail(profilePanel, "Completed Goals", String.valueOf(completedGoals));

            } else {
                profilePanel.add(new JLabel("Profile not found."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, "Unable to load profile information."
            );
        }
    }

    private void addDetail(JPanel panel, String title, String value) {

        JPanel detail = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.dispose();
            }
        };

        detail.setOpaque(false);
        detail.setBackground(new Color(42, 39, 66));
        detail.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                detail.setBackground(new Color(65, 58, 91));
                detail.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                detail.setBackground(new Color(42, 39, 66));
                detail.repaint();
            }
        });

        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setBorder(new EmptyBorder(18, 22, 18, 22));

        String emoji = "";

        switch (title) {
            case "Student Name":
                emoji = "👤";
                break;
            case "Email Address":
                emoji = "📧";
                break;
            case "Department":
                emoji = "🏢";
                break;
            case "Academic Year":
                emoji = "🎓";
                break;
            case "Student ID":
                emoji = "🪪";
                break;
            case "Username":
                emoji = "🔑";
                break;
            case "Total Skills":
                emoji = "💻";
                break;
            case "Completed Goals":
                emoji = "🏆";
                break;
        }

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(new Color(196, 181, 253));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(196, 181, 253));

        String displayValue = value;

        if (title.equals("Email Address")) {
            displayValue = "<html><div style='width:180px'>" + value + "</div></html>";
        }

        JLabel valueLabel = new JLabel(displayValue);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 17));
        valueLabel.setForeground(Color.WHITE);

        detail.add(iconLabel);
        detail.add(Box.createVerticalStrut(12));
        detail.add(titleLabel);
        detail.add(Box.createVerticalStrut(6));
        detail.add(valueLabel);

        int index = panel.getComponentCount();

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = index % 2;
        gbc.gridy = index / 2;

        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        detail.setPreferredSize(new Dimension(340, 165));

        panel.add(detail, gbc);
    }

    private void editProfile() {

    String name = JOptionPane.showInputDialog(
        this, "Enter Student Name:"
    );

    if (name == null || name.trim().isEmpty()) {
        return;
    }

    String email = JOptionPane.showInputDialog(
        this, "Enter Email Address:"
    );

    if (email == null || email.trim().isEmpty()) {
        return;
    }

    String department = JOptionPane.showInputDialog(
        this, "Enter Department:"
    );

    if (department == null || department.trim().isEmpty()) {
        return;
    }

    String yearText = JOptionPane.showInputDialog(
        this, "Enter Academic Year (1-5):"
    );

    if (yearText == null) {
        return;
    }

    try {
        int year = Integer.parseInt(yearText);

        if (year < 1 || year > 5) {
            JOptionPane.showMessageDialog(
                this, "Academic year must be between 1 and 5."
            );
            return;
        }

        String sql = "UPDATE students SET name=?, email=?, " +
                     "department=?, year=? WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setString(2, email.trim());
            ps.setString(3, department.trim());
            ps.setInt(4, year);
            ps.setInt(5, userId);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(
                    this, "Profile updated successfully!"
                );
            }
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this, "Please enter a valid academic year."
        );
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
            this, "Unable to update profile."
        );
    }
}
}