package com.skilltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends JFrame {

    Color purple = new Color(167, 139, 250);       // Lavender accent
    Color darkPurple = new Color(25, 23, 43);      // Sidebar
    Color background = new Color(17, 17, 31);     // Main background

    public Dashboard(int userId) {

        setTitle("SkillTrack - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(darkPurple);
        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 20, 20));

        JLabel logo = new JLabel("SkillTrack");
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setForeground(new Color(196, 181, 253));

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(50));

        String[] menuItems = {
            "Dashboard", "My Profile", "My Skills",
            "My Goals", "Reports", "Skill Gap Analysis", "Logout"
        };

        for (String item : menuItems) {
            String icon = "";

            switch (item) {
                case "Dashboard": icon = "🏠"; break;
                case "My Profile": icon = "👤"; break;
                case "My Skills": icon = "💻"; break;
                case "My Goals": icon = "🎯"; break;
                case "Reports": icon = "📊"; break;
                case "Skill Gap Analysis": icon = "🔍"; break;
                case "Logout": icon = "🚪"; break;
            }

            JButton button = new JButton(icon + "     " + item);
            
            if (item.equals("My Skills")) {
                button.addActionListener(e -> {
                   new Skills(userId);
                   dispose();
                });
            }

            if (item.equals("My Goals")) {
                button.addActionListener(e -> {
                   new Goals(userId);
                   dispose();
                });
            }

            if (item.equals("Reports")) {
                button.addActionListener(e -> {
                   new Reports(userId);
                   dispose();
                });
            }

            if (item.equals("Skill Gap Analysis")) {
                button.addActionListener(e -> {
                new SkillGapAnalysis(userId);
                    dispose();
                });
            }

            if (item.equals("My Profile")) {
                button.addActionListener(e -> {
                   new MyProfile(userId);
                   dispose();
                });
            }

            if (item.equals("Logout")) {
                button.addActionListener(e -> {
                   dispose();
                   new Login();
                });
            }
            button.setFont(new Font("Seoge UI Emoji", Font.PLAIN, 15));
            button.setForeground(new Color(196, 181, 253));
            button.setBackground(darkPurple);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setMaximumSize(new Dimension(180, 45));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(new Color(65, 58, 91));
                }
                @Override 
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(darkPurple);
                }
            });

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(12));
        }

        // Main content
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(background);
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);

        JLabel welcome = new JLabel("Welcome, " + getStudentName(userId) + "! Your learning journey starts here.");
        welcome.setFont(new Font("Arial", Font.PLAIN, 15));
        welcome.setForeground(new Color(196, 181, 253));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(welcome);

        top.add(titlePanel, BorderLayout.WEST);

        // Overview cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 25, 25));
        cards.setOpaque(false);

        int totalSkills = getTotalSkillCount(userId);

        cards.add(createCard("Total Skills",
        String.valueOf(totalSkills), "Skills being learned"));

        int completedGoals = getCompletedGoalCount(userId);
        cards.add(createCard("Completed Goals",String.valueOf(completedGoals), "Goals achieved"));

        int overallProgress = getOverallProgress(userId);

        cards.add(createCard("Overall Progress",overallProgress + "%", "Average skill progress"));
        
        // Recent activity
        JPanel activity = new JPanel(new BorderLayout(10, 15));
        activity.setBackground(Color.WHITE);
        activity.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(65, 58, 91), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel activityTitle = new JLabel("My Learning Overview");
        activityTitle.setFont(new Font("Arial", Font.BOLD, 20));
        activityTitle.setForeground(Color.WHITE);

        activity.setBackground(new Color(33, 31, 54));

        JPanel skillList = getSkillProgressPanel(userId);

        activity.add(activityTitle, BorderLayout.NORTH);
        activity.add(skillList, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(20, 30));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(activity, BorderLayout.CENTER);

        content.add(top, BorderLayout.NORTH);
        content.add(center, BorderLayout.CENTER);

        main.add(sidebar, BorderLayout.WEST);
        main.add(content, BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    private String getStudentName(int userId) {

    String sql = "SELECT name FROM students WHERE user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("name");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "Student";
}
    private int getTotalSkillCount(int userId) {

    String sql = "SELECT COUNT(*) FROM skills s " +
                 "JOIN students st ON s.student_id = st.student_id " +
                 "WHERE st.user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}
    
private int getOverallProgress(int userId) {

    String sql = "SELECT AVG(s.progress_percentage) " +
                 "FROM skills s " +
                 "JOIN students st ON s.student_id = st.student_id " +
                 "WHERE st.user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return (int) Math.round(rs.getDouble(1));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}	

private int getCompletedGoalCount(int userId) {

    String sql = "SELECT COUNT(*) FROM goals g " +
                 "JOIN students st ON g.student_id = st.student_id " +
                 "WHERE st.user_id = ? AND LOWER(g.status) = 'completed'";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}	

    private JPanel createCard(String title, String value, String subtitle) {

        JPanel card = new JPanel();
        card.setBackground(new Color(33, 31, 54));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(65, 58, 91), 1),
            new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(196, 181, 253));
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(new Color(167, 139, 250));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(new Color(161, 161, 181));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitleLabel);

        return card;
    }

    private JPanel createProgress(String skill, int progress) {

        JPanel panel = new JPanel(new BorderLayout(10, 8));
        panel.setBackground(new Color(33, 31, 54));

        JLabel name = new JLabel(skill);
        name.setFont(new Font("Arial", Font.BOLD, 14));
        name.setForeground(Color.WHITE);

        JLabel percentage = new JLabel(progress + "%");
        percentage.setForeground(new Color(196, 181, 253));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(progress);
        bar.setStringPainted(false);
        bar.setForeground(new Color(167, 139, 250));
        bar.setBackground(new Color(65, 58, 91));

        JPanel labels = new JPanel(new BorderLayout());
        labels.setBackground(new Color(33, 31, 54));
        labels.add(name, BorderLayout.WEST);
        labels.add(percentage, BorderLayout.EAST);

        panel.add(labels, BorderLayout.NORTH);
        panel.add(bar, BorderLayout.CENTER);

        return panel;
    } 

    private JPanel getSkillProgressPanel(int userId) {

    JPanel skillList = new JPanel();
    skillList.setLayout(new BoxLayout(skillList, BoxLayout.Y_AXIS));
    skillList.setBackground(new Color(33, 31, 54));

    String sql = "SELECT s.skill_name, s.progress_percentage " +
                 "FROM skills s " +
                 "JOIN students st ON s.student_id = st.student_id " +
                 "WHERE st.user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            String skillName = rs.getString("skill_name");
            int progress = rs.getInt("progress_percentage");

            skillList.add(createProgress(skillName, progress));
            skillList.add(Box.createVerticalStrut(20));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return skillList;
}

    public static void main(String[] args) {
        new Dashboard(1);
    }
}

