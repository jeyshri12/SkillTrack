package com.skilltrack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Skills extends JFrame {

    private DefaultTableModel model;
    private JTable table; 
    
    Color purple = new Color(91, 63, 180);
    Color darkPurple = new Color(48, 35, 90);
    Color background = new Color(247, 246, 251);

    int userId;
    int studentId;

    JTextField skillField;
    JComboBox<String> categoryBox;
    JComboBox<String> levelBox;
    JComboBox<String> targetLevelBox;
    JSpinner progressSpinner;

    public Skills(int userId) {

        this.userId = userId;
        this.studentId = getStudentId();

        setTitle("SkillTrack - My Skills");

        JButton dashboardButton = new JButton("←  Back to Dashboard") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setColor(purple);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        dashboardButton.setPreferredSize(new Dimension(190, 36));
        dashboardButton.setFont(new Font("Arial", Font.BOLD, 12));
        dashboardButton.setForeground(Color.WHITE);
        dashboardButton.setBackground(purple);
        dashboardButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        dashboardButton.setContentAreaFilled(false);
        dashboardButton.setBorderPainted(false);
        dashboardButton.setOpaque(false);
        dashboardButton.setFocusPainted(false);
        dashboardButton.setBorder(new EmptyBorder(5, 12, 5, 12));
        dashboardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        dashboardButton.addActionListener(e -> {
            dispose();
            new Dashboard(userId);
        });

        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(new Color(17, 17, 31));
        main.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel heading = new JLabel("💻 My Skills");
        heading.setFont(new Font("Seoge UI Emoji", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage and track your skills");
        subtitle.setForeground(new Color(161, 161, 181));

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setOpaque(false);

        JPanel titleSection = new JPanel();
        titleSection.setOpaque(false);
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));

        titleSection.add(heading);
        titleSection.add(Box.createVerticalStrut(8));
        titleSection.add(subtitle);

        headingPanel.add(titleSection, BorderLayout.WEST);
        headingPanel.add(dashboardButton, BorderLayout.EAST);

        // Input form
        JPanel form = new JPanel(new GridLayout(2, 5, 12, 12));
        form.setBackground(new Color(33, 31, 54));
        form.setBorder(new EmptyBorder(25, 20, 25, 20));

        skillField = new JTextField();
        skillField.setBackground(new Color(42, 39, 66));
        skillField.setForeground(Color.WHITE);
        skillField.setCaretColor(Color.WHITE);
        skillField.setFont(new Font("Arial", Font.PLAIN, 14));
        skillField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(196, 181, 253), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));

        categoryBox = new JComboBox<>(new String[]{
            "Programming", "Database", "Web Development",
            "Communication", "Other"
        });

        levelBox = new JComboBox<>(new String[]{
            "Beginner", "Intermediate", "Advanced"
        });

        targetLevelBox = new JComboBox<>(new String[]{
            "Beginner", "Intermediate", "Advanced"
        });

        JLabel skillNameLabel = new JLabel("Skill Name");
        JLabel categoryLabel = new JLabel("Category");
        JLabel levelLabel = new JLabel("Skill Level");
        JLabel progressLabel = new JLabel("Progress (%)");
        JLabel targetLabel = new JLabel("Target Level");

        JLabel[] labels = {
           skillNameLabel, categoryLabel, levelLabel, progressLabel, targetLabel
        };

        for (JLabel label : labels) {
           label.setForeground(new Color(196, 181, 253));
           label.setFont(new Font("Arial", Font.BOLD, 13));
           form.add(label);
        }

        JComboBox<?>[] dropdowns = {
            categoryBox, levelBox, targetLevelBox
        };

        for (JComboBox<?> dropdown : dropdowns) {
            dropdown.setBackground(new Color(42, 39, 66));
            dropdown.setForeground(Color.WHITE);
            dropdown.setFont(new Font("Arial", Font.PLAIN, 13));
            dropdown.setBorder(BorderFactory.createLineBorder(new Color(196, 181, 253), 1));
            dropdown.setFocusable(false);
        }

        for (JComboBox<?> dropdown : dropdowns) {
            dropdown.setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {
                   JButton arrow = new JButton("▼");
                   arrow.setBackground(new Color(42, 39, 66));
                   arrow.setForeground(new Color(196, 181, 253));
                   arrow.setBorder(BorderFactory.createEmptyBorder());
                   arrow.setFocusPainted(false);
                   return arrow;
                }
            });
        }

        progressSpinner = new JSpinner(
            new SpinnerNumberModel(0, 0, 100, 5)
        );

        progressSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        progressSpinner.setBackground(new Color(42, 39, 66));
        progressSpinner.setForeground(Color.WHITE);

        JComponent spinnerEditor = progressSpinner.getEditor();

        if (spinnerEditor instanceof JSpinner.DefaultEditor) {
           JTextField spinnerField = ((JSpinner.DefaultEditor) spinnerEditor).getTextField();

           spinnerField.setBackground(new Color(42, 39, 66));
           spinnerField.setForeground(Color.WHITE);
           spinnerField.setCaretColor(Color.WHITE);
           spinnerField.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }

        form.add(skillField);
        form.add(categoryBox);
        form.add(levelBox);
        form.add(progressSpinner);
        form.add(targetLevelBox);

        // Buttons
        JButton addButton = createButton("Add Skill");
        JButton updateButton = createButton("Update Skill");
        JButton deleteButton = createButton("Delete Skill");
        JButton refreshButton = createButton("Refresh");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setOpaque(false);

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        // Table
        model = new DefaultTableModel(
            new String[]{
                "Skill ID", "Skill Name", "Category",
                "Level", "Progress (%)", "Target Level"
            }, 0
        ) {
             @Override
             public boolean isCellEditable(int row, int column) {
             return false;
          }
        };

        table = new JTable(model);

        table.setBackground(new Color(33, 31, 54));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(65, 58, 91));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(65, 58, 91));
        table.setRowHeight(32);

        table.getTableHeader().setBackground(new Color(25, 23, 43));
        table.getTableHeader().setForeground(new Color(196, 181, 253));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(new Color(17, 17, 31));
        scrollPane.getViewport().setBackground(new Color(17, 17, 31));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);

        JPanel upper = new JPanel(new BorderLayout(10, 10));
        upper.setOpaque(false);

        upper.add(form, BorderLayout.NORTH);
        upper.add(buttons, BorderLayout.SOUTH);

        center.add(upper, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        main.add(headingPanel, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);

        add(main);

        addButton.addActionListener(e -> addSkill());
        updateButton.addActionListener(e -> updateSkill());
        deleteButton.addActionListener(e -> deleteSkill());
        refreshButton.addActionListener(e -> loadSkills());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();

                if (row >= 0) {
                    skillField.setText(model.getValueAt(row, 1).toString());
                    categoryBox.setSelectedItem(model.getValueAt(row, 2));
                    levelBox.setSelectedItem(model.getValueAt(row, 3));
                    progressSpinner.setValue(
                        Integer.parseInt(model.getValueAt(row, 4).toString())
                    );

                targetLevelBox.setSelectedItem(model.getValueAt(row, 5));
                }
            }
        });

        if (studentId > 0) {
            loadSkills();
        } else {
            JOptionPane.showMessageDialog(this,
                "Student profile not found!");
        }

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

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setBackground(new Color(139, 92, 246));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(167, 139, 250));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(139, 92, 246));
            }
        });

        return button;
    }

    private void addSkill() {

    if (studentId <= 0) return;

    String skill = skillField.getText().trim();

    if (skill.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Enter a skill name!");
        return;
    }

    try (Connection con = DBConnection.getConnection()) {

        // Check whether the skill already exists
        String checkSql = "SELECT COUNT(*) FROM skills " +
            "WHERE student_id = ? AND LOWER(TRIM(skill_name)) = LOWER(?)";

        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {

            checkPs.setInt(1, studentId);
            checkPs.setString(2, skill);

            ResultSet rs = checkPs.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                    "This skill already exists!");
                return;
            }
        }

        // Insert the new skill
        String sql = "INSERT INTO skills " +
        "(student_id, skill_name, category, skill_level, progress_percentage, target_level) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, skill);
            ps.setString(3, categoryBox.getSelectedItem().toString());
            ps.setString(4, levelBox.getSelectedItem().toString());
            ps.setInt(5, (Integer) progressSpinner.getValue());
            ps.setString(6, targetLevelBox.getSelectedItem().toString());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Skill added successfully!");

            clearFields();
            loadSkills();
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error: " + e.getMessage());
    }
}

    private void loadSkills() {

        model.setRowCount(0);

        String sql = "SELECT * FROM skills WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("skill_id"),
                    rs.getString("skill_name"),
                    rs.getString("category"),
                    rs.getString("skill_level"),
                    rs.getInt("progress_percentage"),
                    rs.getString("target_level")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading skills: " + e.getMessage());
        }
    }

    private void updateSkill() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Select a skill to update!");
            return;
        }

        String sql = "UPDATE skills SET skill_name=?, category=?, " +
            "skill_level=?, progress_percentage=?, target_level=? " +
            "WHERE skill_id=? AND student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, skillField.getText().trim());
            ps.setString(2, categoryBox.getSelectedItem().toString());
            ps.setString(3, levelBox.getSelectedItem().toString());
            ps.setInt(4, (Integer) progressSpinner.getValue());
            ps.setString(5, targetLevelBox.getSelectedItem().toString());
            ps.setInt(6, (Integer) model.getValueAt(row, 0));
            ps.setInt(7, studentId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Skill updated successfully!");

            clearFields();
            loadSkills();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
        }
    }

    private void deleteSkill() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Select a skill to delete!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this, "Delete this skill?",
            "Confirm", JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM skills WHERE skill_id=? AND student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, (Integer) model.getValueAt(row, 0));
            ps.setInt(2, studentId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Skill deleted successfully!");

            clearFields();
            loadSkills();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        skillField.setText("");
        categoryBox.setSelectedIndex(0);
        levelBox.setSelectedIndex(0);
        targetLevelBox.setSelectedIndex(0);
        progressSpinner.setValue(0);
        table.clearSelection();
    }

    public static void main(String[] args) {
        new Skills(1);
    }
}
