package com.campus.complaints.component;

import com.campus.complaints.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Reusable styled button with hover and press states.
 * Supports primary, secondary, danger, and ghost variants.
 */
public class StyledButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, DANGER, GHOST, SUCCESS }

    private Variant variant;
    private boolean hovered  = false;
    private boolean pressed  = false;
    private final int radius;

    public StyledButton(String text) {
        this(text, Variant.PRIMARY);
    }

    public StyledButton(String text, Variant variant) {
        this(text, variant, null);
    }

    public StyledButton(String text, Variant variant, Icon icon) {
        super(text);
        this.variant = variant;
        this.radius  = AppTheme.BTN_RADIUS;
        if (icon != null) setIcon(icon);

        setFont(AppTheme.FONT_BODY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIconTextGap(6);

        int px = 14, py = 7;
        setBorder(BorderFactory.createEmptyBorder(py, px, py, px));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; pressed = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
            @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
        });
    }

    public void setVariant(Variant v) { this.variant = v; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius);

        Color bg, fg, border;
        switch (variant) {
            case PRIMARY -> {
                bg     = pressed ? AppTheme.PRIMARY_HOVER : (hovered ? AppTheme.PRIMARY_HOVER : AppTheme.PRIMARY);
                fg     = Color.WHITE;
                border = bg;
            }
            case SECONDARY -> {
                bg     = pressed ? AppTheme.BORDER : (hovered ? new Color(0xF1F5F9) : AppTheme.BG_CARD);
                fg     = AppTheme.TEXT_PRIMARY;
                border = AppTheme.BORDER;
            }
            case DANGER -> {
                bg     = pressed ? new Color(0xB91C1C) : (hovered ? new Color(0xB91C1C) : AppTheme.ERROR);
                fg     = Color.WHITE;
                border = bg;
            }
            case GHOST -> {
                bg     = hovered ? AppTheme.PRIMARY_LIGHT : new Color(0,0,0,0);
                fg     = AppTheme.PRIMARY;
                border = new Color(0,0,0,0);
            }
            case SUCCESS -> {
                bg     = pressed ? new Color(0x15803D) : (hovered ? new Color(0x15803D) : AppTheme.SUCCESS);
                fg     = Color.WHITE;
                border = bg;
            }
            default -> {
                bg = AppTheme.BG_CARD; fg = AppTheme.TEXT_PRIMARY; border = AppTheme.BORDER;
            }
        }

        if (!isEnabled()) {
            bg = new Color(0xE2E8F0);
            fg = AppTheme.TEXT_MUTED;
        }

        g2.setColor(bg);
        g2.fill(shape);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(shape);
        g2.dispose();

        setForeground(fg);
        super.paintComponent(g);
    }
}
