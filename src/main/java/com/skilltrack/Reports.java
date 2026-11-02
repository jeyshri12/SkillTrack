
package com.skilltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Reports extends JFrame {

    private int userId;
    private int studentId;

    public Reports(int userId) {
        this.userId = userId;
        this.studentId = getStudentId();

        setTitle("SkillTrack - My Reports");
        setSize(950, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setBackground(new Color(17, 17, 31));
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(17, 17, 31));

        JLabel heading = new JLabel("📊 My Reports");
        heading.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
           "Track your skills and learning progress."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(new Color(161, 161, 181));

        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitle);

        JPanel gridPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        gridPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        gridPanel.setBackground(new Color(17, 17, 31));

        gridPanel.add(createReportCard( "Total Skills", String.valueOf(getTotalSkills()), "💻"));

        gridPanel.add(createReportCard("Average Skill Progress", getAverageProgress() + "%", "📈"));

        gridPanel.add(createReportCard("Total Goals", String.valueOf(getTotalGoals()), "🎯"));

        gridPanel.add(createReportCard("Completed Goals", String.valueOf(getCompletedGoals()), "🏆"));

        gridPanel.add(createReportCard("Skills to Improve", String.valueOf(getSkillsToImprove()), "📚"));

        gridPanel.add(createReportCard("Skills at Target", String.valueOf(getSkillsAtTarget()), "⭐"));

        gridPanel.add(createReportCard("Pending Goals", String.valueOf(getPendingGoals()), "⏳"));

        gridPanel.add(createReportCard("In Progress Goals", String.valueOf(getInProgressGoals()), "🚀"));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("←  Back to Dashboard");
        backButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        backButton.setBackground(new Color(95, 65, 160));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            dispose();
            new Dashboard(userId);
        });

        mainPanel.add(backButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private int getStudentId() {
        String sql = "SELECT student_id FROM students WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("student_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int getTotalSkills() {
        return getCount(
            "SELECT COUNT(*) FROM skills WHERE student_id = ?"
        );
    }

    private int getTotalGoals() {
        return getCount(
            "SELECT COUNT(*) FROM goals WHERE student_id = ?"
        );
    }

    private int getCompletedGoals() {
        String sql = "SELECT COUNT(*) FROM goals " +
                     "WHERE student_id = ? AND LOWER(status) = 'completed'";

        return getCount(sql);
    }

    private int getSkillsToImprove() {
        return getCount(
            "SELECT COUNT(*) FROM skills WHERE student_id = ? AND skill_level <> target_level"
        );
    }

    private int getSkillsAtTarget() {
        return getCount(
            "SELECT COUNT(*) FROM skills WHERE student_id = ? AND skill_level = target_level"
        );
    }

    private int getPendingGoals() {
        return getCount(
            "SELECT COUNT(*) FROM goals WHERE student_id = ? AND status = 'Pending'"
        );
    }

    private int getInProgressGoals() {
        return getCount(
           "SELECT COUNT(*) FROM goals WHERE student_id = ? AND status = 'In Progress'"
        );
    }

    private int getCount(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int getAverageProgress() {
        String sql = "SELECT AVG(progress_percentage) " +
                     "FROM skills WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return (int) Math.round(rs.getDouble(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private JPanel createReportCard(String title, String value, String icon) {

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

    JLabel iconLabel = new JLabel(icon);
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
    iconLabel.setForeground(new Color(196, 181, 253));

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
    titleLabel.setForeground(new Color(196, 181, 253));

    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(new Font("Arial", Font.BOLD, 17));
    valueLabel.setForeground(Color.WHITE);

    detail.add(iconLabel);
    detail.add(Box.createVerticalStrut(12));
    detail.add(titleLabel);
    detail.add(Box.createVerticalStrut(6));
    detail.add(valueLabel);

    detail.setPreferredSize(new Dimension(200, 165));

    return detail;
}
}
