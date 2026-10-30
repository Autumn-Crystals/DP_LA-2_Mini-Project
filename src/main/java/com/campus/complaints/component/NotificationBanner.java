package com.campus.complaints.component;

import com.campus.complaints.designpatterns.mediator.ComplaintMediator;
import com.campus.complaints.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Notification banner displayed at the top of content area.
 * Shows success, error, warning, and info messages.
 * Auto-dismisses after a configurable delay.
 */
public class NotificationBanner extends JPanel {

    private final JLabel iconLabel;
    private final JLabel messageLabel;
    private Timer dismissTimer;

    public NotificationBanner() {
        setLayout(new BorderLayout(8, 0));
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setVisible(false);

        iconLabel = new JLabel();
        iconLabel.setFont(AppTheme.FONT_BODY);

        messageLabel = new JLabel();
        messageLabel.setFont(AppTheme.FONT_BODY);

        add(iconLabel,    BorderLayout.WEST);
        add(messageLabel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("x");
        closeBtn.setFont(AppTheme.FONT_SMALL);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dismiss());
        add(closeBtn, BorderLayout.EAST);
    }

    public void show(String message, ComplaintMediator.NotificationType type) {
        if (dismissTimer != null && dismissTimer.isRunning()) dismissTimer.stop();

        messageLabel.setText(message);
        Color bg, fg;
        Icon icon;
        switch (type) {
            case SUCCESS -> {
                bg = AppTheme.SUCCESS_BG; fg = AppTheme.SUCCESS;
                icon = com.campus.complaints.util.IconFactory.check(AppTheme.SPACING_MD, fg);
            }
            case ERROR -> {
                bg = AppTheme.ERROR_BG; fg = AppTheme.ERROR;
                icon = com.campus.complaints.util.IconFactory.error(AppTheme.SPACING_MD, fg);
            }
            case WARNING -> {
                bg = AppTheme.WARNING_BG; fg = AppTheme.WARNING;
                icon = com.campus.complaints.util.IconFactory.warning(AppTheme.SPACING_MD, fg);
            }
            default -> {
                bg = AppTheme.INFO_BG; fg = AppTheme.INFO;
                icon = com.campus.complaints.util.IconFactory.info(AppTheme.SPACING_MD, fg);
            }
        }

        setBackground(bg);
        messageLabel.setForeground(fg);
        iconLabel.setIcon(icon);
        setVisible(true);
        revalidate();
        repaint();

        // Auto-dismiss after 4 seconds
        dismissTimer = new Timer(4000, e -> dismiss());
        dismissTimer.setRepeats(false);
        dismissTimer.start();
    }

    public void dismiss() {
        if (dismissTimer != null) dismissTimer.stop();
        setVisible(false);
    }
}
