package com.campus.complaints.component;

import com.campus.complaints.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Metric summary card displaying KPI numbers, titles, and accent colors.
 * Used across Student and Administrator dashboards.
 */
public class SummaryCard extends CardPanel {

    private final JLabel countLabel;
    private final JLabel titleLabel;
    private final JPanel accentBar;

    public SummaryCard(String title, String initialValue, Color accentColor) {
        super(AppTheme.CARD_RADIUS);
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(170, 100));

        accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(5, 50));
        accentBar.setBackground(accentColor);
        add(accentBar, BorderLayout.WEST);

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 4));
        content.setOpaque(false);

        countLabel = new JLabel(initialValue);
        countLabel.setFont(AppTheme.FONT_TITLE.deriveFont(26f));
        countLabel.setForeground(AppTheme.TEXT_PRIMARY);

        titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SECONDARY);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);

        content.add(countLabel);
        content.add(titleLabel);
        add(content, BorderLayout.CENTER);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorderColor(accentColor);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorderColor(AppTheme.BORDER);
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    public void setValue(int count) {
        countLabel.setText(String.valueOf(count));
        repaint();
    }

    public void setValue(String value) {
        countLabel.setText(value);
        repaint();
    }

    public String getValue() {
        return countLabel.getText();
    }
}
