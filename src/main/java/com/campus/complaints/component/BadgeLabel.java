package com.campus.complaints.component;

import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import com.campus.complaints.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Coloured badge label for status and priority display.
 * Uses rounded pill shape with background colour coding.
 */
public class BadgeLabel extends JLabel {

    private Color bgColor;
    private Color fgColor;
    private final int radius = 20;

    public BadgeLabel(String text, Color bg, Color fg) {
        super(text);
        this.bgColor = bg;
        this.fgColor = fg;
        setFont(AppTheme.FONT_BADGE);
        setForeground(fg);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
    }

    public static BadgeLabel forStatus(ComplaintStatus status) {
        Color bg, fg;
        switch (status) {
            case PENDING    -> { bg = AppTheme.STATUS_PENDING_BG;    fg = AppTheme.STATUS_PENDING_FG; }
            case ASSIGNED   -> { bg = AppTheme.STATUS_ASSIGNED_BG;   fg = AppTheme.STATUS_ASSIGNED_FG; }
            case IN_PROGRESS -> { bg = AppTheme.STATUS_INPROGRESS_BG; fg = AppTheme.STATUS_INPROGRESS_FG; }
            case RESOLVED   -> { bg = AppTheme.STATUS_RESOLVED_BG;   fg = AppTheme.STATUS_RESOLVED_FG; }
            case CANCELLED  -> { bg = AppTheme.STATUS_CANCELLED_BG;  fg = AppTheme.STATUS_CANCELLED_FG; }
            default         -> { bg = AppTheme.BORDER;               fg = AppTheme.TEXT_SECONDARY; }
        }
        return new BadgeLabel(status.getDisplayName(), bg, fg);
    }

    public static BadgeLabel forPriority(Priority priority) {
        Color bg, fg;
        switch (priority) {
            case LOW      -> { bg = AppTheme.PRIORITY_LOW_BG;  fg = AppTheme.PRIORITY_LOW_FG; }
            case MEDIUM   -> { bg = AppTheme.PRIORITY_MED_BG;  fg = AppTheme.PRIORITY_MED_FG; }
            case HIGH     -> { bg = AppTheme.PRIORITY_HIGH_BG; fg = AppTheme.PRIORITY_HIGH_FG; }
            case CRITICAL -> { bg = AppTheme.PRIORITY_CRIT_BG; fg = AppTheme.PRIORITY_CRIT_FG; }
            default       -> { bg = AppTheme.BORDER;           fg = AppTheme.TEXT_SECONDARY; }
        }
        return new BadgeLabel(priority.getDisplayName(), bg, fg);
    }

    public void update(Color bg, Color fg, String text) {
        this.bgColor = bg;
        this.fgColor = fg;
        setForeground(fg);
        setText(text);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
}
