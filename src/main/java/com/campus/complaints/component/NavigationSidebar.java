package com.campus.complaints.component;

import com.campus.complaints.model.User;
import com.campus.complaints.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Reusable vertical navigation sidebar providing quick switching between application views.
 */
public class NavigationSidebar extends JPanel {

    public record NavItem(String id, String label, Icon icon) {}

    private final JPanel itemsPanel;
    private final List<JButton> buttons = new ArrayList<>();
    private final Consumer<String> onNavigate;
    private String activeId;

    public NavigationSidebar(String title, User currentUser, Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setBackground(AppTheme.PRIMARY_DARK);
        setPreferredSize(new Dimension(220, 600));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 15f));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Nav items container
        itemsPanel = new JPanel();
        itemsPanel.setOpaque(false);
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        add(itemsPanel, BorderLayout.CENTER);

        // Footer / user section
        JPanel footerPanel = new JPanel(new BorderLayout(5, 5));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        if (currentUser != null) {
            JPanel userBox = new JPanel(new GridLayout(2, 1));
            userBox.setOpaque(false);
            JLabel nameLabel = new JLabel(currentUser.getName());
            nameLabel.setFont(AppTheme.FONT_REGULAR.deriveFont(Font.BOLD, 12f));
            nameLabel.setForeground(Color.WHITE);

            JLabel roleLabel = new JLabel(currentUser.getRole().name());
            roleLabel.setFont(AppTheme.FONT_SECONDARY.deriveFont(10f));
            roleLabel.setForeground(new Color(200, 210, 230));

            userBox.add(nameLabel);
            userBox.add(roleLabel);
            footerPanel.add(userBox, BorderLayout.CENTER);
        }

        if (onLogout != null) {
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.setFont(AppTheme.FONT_SECONDARY.deriveFont(Font.BOLD, 11f));
            logoutBtn.setForeground(Color.WHITE);
            logoutBtn.setBackground(new Color(192, 57, 43));
            logoutBtn.setFocusPainted(false);
            logoutBtn.setBorderPainted(false);
            logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            logoutBtn.addActionListener(e -> onLogout.run());
            footerPanel.add(logoutBtn, BorderLayout.SOUTH);
        }

        add(footerPanel, BorderLayout.SOUTH);
    }

    public void addNavigationItem(String id, String label, Icon icon) {
        JButton btn = new JButton(label, icon);
        btn.setFont(AppTheme.FONT_REGULAR);
        btn.setForeground(Color.WHITE);
        btn.setBackground(AppTheme.PRIMARY_DARK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(196, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        btn.addActionListener(e -> {
            setActiveItem(id);
            if (onNavigate != null) {
                onNavigate.accept(id);
            }
        });

        buttons.add(btn);
        itemsPanel.add(btn);
        itemsPanel.add(Box.createVerticalStrut(6));

        if (activeId == null) {
            setActiveItem(id);
        }
    }

    public void setActiveItem(String id) {
        this.activeId = id;
        for (JButton b : buttons) {
            if (b.getText().equalsIgnoreCase(id) || b.getActionCommand().equals(id)) {
                b.setBackground(AppTheme.PRIMARY);
                b.setFont(AppTheme.FONT_REGULAR.deriveFont(Font.BOLD));
            } else {
                b.setBackground(AppTheme.PRIMARY_DARK);
                b.setFont(AppTheme.FONT_REGULAR);
            }
        }
    }
}
