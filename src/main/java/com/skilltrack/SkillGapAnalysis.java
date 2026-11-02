package com.skilltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SkillGapAnalysis extends JFrame {

    private int userId;
    private int studentId;
    private JLabel totalSkillsLabel;
    private JLabel improvementLabel;
    private JLabel reachedTargetLabel;

    public SkillGapAnalysis(int userId) {
        this.userId = userId;
        this.studentId = getStudentId();

        if (studentId == -1) {
            JOptionPane.showMessageDialog(this, "Student record not found!");
            return;
        }

        setTitle("SkillTrack - Skill Gap Analysis");
        setSize(950, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.setBackground(new Color(17, 17, 31));
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        JLabel heading = new JLabel("🔍 Skill Gap Analysis");
        heading.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        heading.setHorizontalAlignment(SwingConstants.LEADING);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Understand your current skills and identify areas to improve."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(new Color(161, 161, 181));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(2, 1, 0, 10));
        headerPanel.setBackground(new Color(17, 17, 31));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitle);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        String[] columns = {
            "Skill Name","Current Level","Target Level","Progress (%)","Skill Gap","Recommended Action"
        };

        DefaultTableModel gapModel = new DefaultTableModel(columns, 0) {
           @Override
           public boolean isCellEditable(int row, int column) {
               return false;
           }
        };

        JTable gapTable = new JTable(gapModel);

        gapTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        gapTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        gapTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        gapTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        gapTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        gapTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        gapTable.getColumnModel().getColumn(5).setPreferredWidth(280);
        
        gapTable.setBackground(new Color(33, 31, 54));
        gapTable.setForeground(Color.WHITE);
        gapTable.setSelectionBackground(new Color(65, 58, 91));
        gapTable.setSelectionForeground(Color.WHITE);
        gapTable.setGridColor(new Color(65, 58, 91));
        gapTable.setRowHeight(35); 
        gapTable.setFont(new Font("Arial", Font.PLAIN, 13));

        gapTable.getTableHeader().setBackground(new Color(25, 23, 43));
        gapTable.getTableHeader().setForeground(new Color(196, 181, 253));
        gapTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        JPanel totalCard = new JPanel();
        totalCard.setLayout(new BoxLayout(totalCard, BoxLayout.Y_AXIS));
        totalCard.setBackground(new Color(33, 31, 54));
        totalCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel totalTitle = new JLabel("Total Skills Analysed");

        totalTitle.setForeground(new Color(196, 181, 253));
        totalTitle.setFont(new Font("Arial", Font.BOLD, 14));

        totalSkillsLabel = new JLabel("0");
        totalSkillsLabel.setFont(new Font("Arial", Font.BOLD, 26));
        totalSkillsLabel.setForeground(Color.WHITE);

        totalCard.add(totalTitle);
        totalCard.add(Box.createVerticalStrut(10));
        totalCard.add(totalSkillsLabel);

        summaryPanel.add(totalCard);

        JPanel improvementCard = new JPanel();

        improvementCard.setLayout(new BoxLayout(improvementCard, BoxLayout.Y_AXIS));
        improvementCard.setBackground(new Color(33, 31, 54));
        improvementCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel improvementTitle = new JLabel("Needs Improvement");

        improvementTitle.setForeground(new Color(196, 181, 253));
        improvementTitle.setFont(new Font("Arial", Font.BOLD, 14));

        improvementLabel = new JLabel("0");
        improvementLabel.setFont(new Font("Arial", Font.BOLD, 26));
        improvementLabel.setForeground(Color.WHITE);

        improvementCard.add(improvementTitle);
        improvementCard.add(Box.createVerticalStrut(10));
        improvementCard.add(improvementLabel);

        summaryPanel.add(improvementCard);

        JPanel reachedCard = new JPanel();
        reachedCard.setLayout(new BoxLayout(reachedCard, BoxLayout.Y_AXIS));
        reachedCard.setBackground(new Color(33, 31, 54));
        reachedCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel reachedTitle = new JLabel("Target Reached");

        reachedTitle.setForeground(new Color(196, 181, 253));
        reachedTitle.setFont(new Font("Arial", Font.BOLD, 14));

        reachedTargetLabel = new JLabel("0");
        reachedTargetLabel.setFont(new Font("Arial", Font.BOLD, 26));
        reachedTargetLabel.setForeground(Color.WHITE);

        reachedCard.add(reachedTitle);
        reachedCard.add(Box.createVerticalStrut(10));
        reachedCard.add(reachedTargetLabel);

        summaryPanel.add(reachedCard);
        summaryPanel.setBackground(new Color(17, 17, 31));

        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        JScrollPane scrollPane = new JScrollPane(gapTable);
        scrollPane.setPreferredSize(new Dimension(880, 300));
        scrollPane.setBackground(new Color(17, 17, 31));
        scrollPane.getViewport().setBackground(new Color(17, 17, 31));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(scrollPane);
        loadGapData(gapModel);
        mainPanel.add(Box.createVerticalStrut(30));

        JButton backButton = new JButton("←  Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(167, 139, 250));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);

        backButton.addActionListener(e -> {
            dispose();
            new Dashboard(userId);
        });

        mainPanel.add(backButton);

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

        return -1;
    }

    private void loadGapData(DefaultTableModel gapModel) {

        String sql = "SELECT skill_name, skill_level, target_level, " +
                     "progress_percentage FROM skills WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            gapModel.setRowCount(0);
            int totalSkills = 0;
            int needsImprovement = 0;
            int targetReached = 0;

            while (rs.next()) {

               totalSkills++;
               String skillName = rs.getString("skill_name");
               String currentLevel = rs.getString("skill_level");
               String targetLevel = rs.getString("target_level");
               int progress = rs.getInt("progress_percentage");

               int currentRank = getLevelRank(currentLevel);
               int targetRank = getLevelRank(targetLevel);

               String gap = currentRank >= targetRank
                    ? "Target Reached"
                    : "Needs Improvement";

                String recommendation;

                if (currentRank >= targetRank) {
                    recommendation = "Maintain your skill level";
                } else if (currentLevel.equals("Beginner")) {
                    recommendation = "Practise basics and build small projects";
                } else if (currentLevel.equals("Intermediate")) {
                    recommendation = "Work on advanced concepts and projects";
                } else {
                    recommendation = "Continue improving your skills";
                }    
                
                if (gap.equals("Target Reached")) {
                    targetReached++;
                } else {
                    needsImprovement++;
                }

                gapModel.addRow(new Object[]{
                  skillName,
                  currentLevel,
                  targetLevel,
                  progress,
                  gap,
                  recommendation
                });
            }

            totalSkillsLabel.setText(String.valueOf(totalSkills));
            improvementLabel.setText(String.valueOf(needsImprovement));
            reachedTargetLabel.setText(String.valueOf(targetReached));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, "Unable to load skill gap data."
            );
        }
    }

    private int getLevelRank(String level) {

        if (level == null) {
            return 0;
        }

        switch (level) {
            case "Beginner":
                return 1;
            case "Intermediate":
                return 2;
            case "Advanced":
                return 3;
            default:
                return 0;
        }
    }
}
