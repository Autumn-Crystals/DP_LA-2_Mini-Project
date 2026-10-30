package com.campus.complaints.component;

import com.campus.complaints.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Reusable rounded card panel with a subtle drop shadow.
 * Used for all dashboard cards, content areas, and form containers.
 */
public class CardPanel extends JPanel {

    private final int radius;
    private Color borderColor;

    public CardPanel() {
        this(AppTheme.CARD_RADIUS);
    }

    public CardPanel(int radius) {
        this.radius      = radius;
        this.borderColor = AppTheme.BORDER;
        setOpaque(false);
        setBackground(AppTheme.BG_CARD);
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Soft shadow
        for (int i = 3; i > 0; i--) {
            int alpha = 15 / i;
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fill(new RoundRectangle2D.Float(i, i + 1, w - i * 2, h - i * 2, radius, radius));
        }

        // Card background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 2, h - 2, radius, radius));

        // Border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 2, h - 2, radius, radius));

        g2.dispose();
        super.paintComponent(g);
    }
}
