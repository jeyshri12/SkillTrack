package com.skilltrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class Goals extends JFrame {

    Color purple = new Color(91, 63, 180);
    Color darkPurple = new Color(48, 35, 90);
    Color background = new Color(247, 246, 251);

    int userId;
    int studentId;

    JTextField goalField;
    JTextField dateField;
    JComboBox<String> statusBox;
    JTable table;
    DefaultTableModel model;

    public Goals(int userId) {

        this.userId = userId;
        this.studentId = getStudentId();

        setTitle("SkillTrack - My Goals");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(new Color(17, 17, 31));
        main.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel heading = new JLabel("🎯 My Goals");
        heading.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
            "Set your learning goals and track your progress"
        );
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitle);
        
        JButton backButton = new JButton("←  Back to Dashboard") {
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

        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setForeground(Color.WHITE);

        backButton.addActionListener(e -> {
           dispose();
           new Dashboard(userId);
        });

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setOpaque(false);
        headingPanel.add(titlePanel, BorderLayout.WEST);
        headingPanel.add(backButton, BorderLayout.EAST);

        JPanel form = new JPanel(new GridLayout(2, 3, 12, 12));
        form.setBackground(new Color(33, 31, 54));
        form.setBorder(new EmptyBorder(25, 20, 25, 20));

        goalField = new JTextField();
        dateField = new JTextField();

        JTextField[] fields = {goalField, dateField};

        for (JTextField field : fields) {
            field.setBackground(new Color(42, 39, 66));
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
            field.setFont(new Font("Arial", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(196, 181, 253), 1),
                new EmptyBorder(8, 10, 8, 10)
            ));
        }

        statusBox = new JComboBox<>(new String[]{
            "Pending", "In Progress", "Completed"
        });

        statusBox.setBackground(new Color(139, 92, 246));
        statusBox.setForeground(Color.WHITE);
        statusBox.setFont(new Font("Arial", Font.PLAIN, 13));
        statusBox.setBorder(BorderFactory.createLineBorder(
            new Color(196, 181, 253), 1
        ));
        statusBox.setFocusable(false);

        JLabel goalNameLabel = new JLabel("Goal Name");
        JLabel dateLabel = new JLabel("Target Date (YYYY-MM-DD)");
        JLabel statusLabel = new JLabel("Status");

        JLabel[] labels = {goalNameLabel, dateLabel, statusLabel};

        for (JLabel label : labels) {
           label.setForeground(new Color(196, 181, 253));
           label.setFont(new Font("Arial", Font.BOLD, 13));
           form.add(label);
        }

        form.add(goalField);
        form.add(dateField);
        form.add(statusBox);

        JButton addButton = createButton("Add Goal");
        JButton updateButton = createButton("Update Goal");
        JButton deleteButton = createButton("Delete Goal");
        JButton refreshButton = createButton("Refresh");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setOpaque(false);
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        JPanel upper = new JPanel(new BorderLayout(10, 10));
        upper.setOpaque(false);
        upper.add(form, BorderLayout.NORTH);
        upper.add(buttons, BorderLayout.SOUTH);

        model = new DefaultTableModel(
            new String[]{"Goal ID", "Goal Name", "Target Date", "Status"}, 0
        ) {
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
        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(new Color(17, 17, 31));
        scrollPane.getViewport().setBackground(new Color(17, 17, 31));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);
        center.add(upper, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        main.add(headingPanel, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);

        add(main);

        addButton.addActionListener(e -> addGoal());
        updateButton.addActionListener(e -> updateGoal());
        deleteButton.addActionListener(e -> deleteGoal());
        refreshButton.addActionListener(e -> loadGoals());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();

                if (row >= 0) {
                    goalField.setText(model.getValueAt(row, 1).toString());

                    Object date = model.getValueAt(row, 2);
                    dateField.setText(date == null ? "" : date.toString());

                    statusBox.setSelectedItem(
                        model.getValueAt(row, 3).toString()
                    );
                }
            }
        });

        if (studentId > 0) {
            loadGoals();
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

    private void addGoal() {

        String name = goalField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a goal name!");
            return;
        }

        java.sql.Date targetDate = null;

        try {
            if (!dateField.getText().trim().isEmpty()) {
                targetDate = java.sql.Date.valueOf(
                    LocalDate.parse(dateField.getText().trim())
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this, "Enter the date in YYYY-MM-DD format!"
            );
            return;
        }

        String sql = "INSERT INTO goals " +
            "(student_id, goal_name, target_date, status) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, name);
            ps.setDate(3, targetDate);
            ps.setString(4, statusBox.getSelectedItem().toString());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Goal added successfully!");

            clearFields();
            loadGoals();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadGoals() {

        model.setRowCount(0);

        String sql = "SELECT goal_id, goal_name, target_date, status " +
                     "FROM goals WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("goal_id"),
                    rs.getString("goal_name"),
                    rs.getDate("target_date"),
                    rs.getString("status")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this, "Error loading goals: " + e.getMessage()
            );
        }
    }

    private void updateGoal() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a goal to update!");
            return;
        }

        String name = goalField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a goal name!");
            return;
        }

        java.sql.Date targetDate = null;

        try {
            if (!dateField.getText().trim().isEmpty()) {
                targetDate = java.sql.Date.valueOf(
                    LocalDate.parse(dateField.getText().trim())
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this, "Enter the date in YYYY-MM-DD format!"
            );
            return;
        }

        String sql = "UPDATE goals SET goal_name=?, target_date=?, status=? " +
                     "WHERE goal_id=? AND student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDate(2, targetDate);
            ps.setString(3, statusBox.getSelectedItem().toString());
            ps.setInt(4, (Integer) model.getValueAt(row, 0));
            ps.setInt(5, studentId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Goal updated successfully!");

            clearFields();
            loadGoals();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteGoal() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a goal to delete!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this, "Delete this goal?", "Confirm",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM goals WHERE goal_id=? AND student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, (Integer) model.getValueAt(row, 0));
            ps.setInt(2, studentId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Goal deleted successfully!");

            clearFields();
            loadGoals();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        goalField.setText("");
        dateField.setText("");
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }
}