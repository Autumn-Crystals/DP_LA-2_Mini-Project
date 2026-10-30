package com.campus.complaints.mvc.view;

import com.campus.complaints.component.CardPanel;
import com.campus.complaints.component.StyledButton;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.util.AppTheme;
import com.campus.complaints.util.IconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Student dashboard view in the MVC architecture.
 * Displays KPI cards and quick actions for reporting and tracking complaints.
 */
public class StudentDashboardView extends JPanel {

    private final JLabel totalLabel    = new JLabel("0");
    private final JLabel pendingLabel  = new JLabel("0");
    private final JLabel progressLabel = new JLabel("0");
    private final JLabel resolvedLabel = new JLabel("0");

    private final StyledButton submitBtn;
    private final StyledButton viewAllBtn;

    public StudentDashboardView() {
        setBackground(AppTheme.BG_PAGE);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // ---- Page header ----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("My Dashboard");
        title.setFont(AppTheme.FONT_LARGE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // ---- Content ----
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // Stat cards row
        JPanel stats = new JPanel(new GridLayout(1, 4, AppTheme.SPACING_MD, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);

        stats.add(buildStatCard("Total Submitted", totalLabel,
                AppTheme.PRIMARY, AppTheme.PRIMARY_LIGHT));
        stats.add(buildStatCard("Pending", pendingLabel,
                AppTheme.WARNING, AppTheme.WARNING_BG));
        stats.add(buildStatCard("In Progress", progressLabel,
                new Color(0x7C3AED), new Color(0xF5F3FF)));
        stats.add(buildStatCard("Resolved", resolvedLabel,
                AppTheme.SUCCESS, AppTheme.SUCCESS_BG));

        content.add(stats);
        content.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // Quick actions row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitBtn  = new StyledButton("Submit New Complaint", StyledButton.Variant.PRIMARY,
                IconFactory.submit(IconFactory.SIZE_MD, Color.WHITE));
        viewAllBtn = new StyledButton("View All My Complaints", StyledButton.Variant.SECONDARY,
                IconFactory.complaints(IconFactory.SIZE_MD, AppTheme.TEXT_PRIMARY));

        actions.add(submitBtn);
        actions.add(viewAllBtn);
        content.add(actions);
        content.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // Tips
        JLabel recentLabel = new JLabel("Quick Tips");
        recentLabel.setFont(AppTheme.FONT_SUBHEAD);
        recentLabel.setForeground(AppTheme.TEXT_PRIMARY);
        recentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(recentLabel);
        content.add(Box.createVerticalStrut(AppTheme.SPACING_SM));

        CardPanel tipsCard = new CardPanel();
        tipsCard.setLayout(new BoxLayout(tipsCard, BoxLayout.Y_AXIS));
        tipsCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        tipsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        tipsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        String[] tips = {
            "Use the 'Submit Complaint' button to report a new campus issue.",
            "Click 'Complaints' in the sidebar to view and track all your submissions.",
            "Use 'Duplicate' on an existing complaint to report the same issue in a different location.",
            "Click any complaint row to see its full details and current status."
        };
        for (String tip : tips) {
            JLabel tipLabel = new JLabel("  " + tip);
            tipLabel.setFont(AppTheme.FONT_BODY);
            tipLabel.setForeground(AppTheme.TEXT_SECONDARY);
            tipLabel.setIcon(IconFactory.check(IconFactory.SIZE_SM, AppTheme.SUCCESS));
            tipLabel.setIconTextGap(6);
            tipsCard.add(tipLabel);
            tipsCard.add(Box.createVerticalStrut(6));
        }
        content.add(tipsCard);
        content.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    private CardPanel buildStatCard(String label, JLabel valueLabel,
                                    Color valueColor, Color bgColor) {
        CardPanel card = new CardPanel();
        card.setBackground(bgColor);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(valueColor);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_BODY);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);

        card.add(valueLabel, BorderLayout.NORTH);
        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    public void updateStats(Map<ComplaintStatus, Integer> counts) {
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        totalLabel.setText(String.valueOf(total));
        pendingLabel.setText(String.valueOf(counts.getOrDefault(ComplaintStatus.PENDING, 0)));
        progressLabel.setText(String.valueOf(
                counts.getOrDefault(ComplaintStatus.IN_PROGRESS, 0) +
                counts.getOrDefault(ComplaintStatus.ASSIGNED, 0)));
        resolvedLabel.setText(String.valueOf(counts.getOrDefault(ComplaintStatus.RESOLVED, 0)));
    }

    public void addSubmitListener(ActionListener l)  { submitBtn.addActionListener(l); }
    public void addViewAllListener(ActionListener l) { viewAllBtn.addActionListener(l); }
}
