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
 * Administrator dashboard view in the MVC architecture.
 * Displays KPI summary cards and quick navigation buttons.
 */
public class AdminDashboardView extends JPanel {

    private final JLabel totalLabel      = statLabel("0");
    private final JLabel pendingLabel    = statLabel("0");
    private final JLabel assignedLabel   = statLabel("0");
    private final JLabel progressLabel   = statLabel("0");
    private final JLabel resolvedLabel   = statLabel("0");

    private final StyledButton manageBtn;
    private final StyledButton staffBtn;

    public AdminDashboardView() {
        setBackground(AppTheme.BG_PAGE);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Title
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(AppTheme.FONT_LARGE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // Stats row (5 cards)
        JPanel stats = new JPanel(new GridLayout(1, 5, AppTheme.SPACING_MD, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);

        stats.add(buildStatCard("Total Complaints", totalLabel,
                AppTheme.PRIMARY, AppTheme.PRIMARY_LIGHT,
                IconFactory.complaints(24, AppTheme.PRIMARY)));
        stats.add(buildStatCard("Pending", pendingLabel,
                AppTheme.WARNING, AppTheme.WARNING_BG,
                IconFactory.warning(24, AppTheme.WARNING)));
        stats.add(buildStatCard("Assigned", assignedLabel,
                AppTheme.INFO, AppTheme.INFO_BG,
                IconFactory.assign(24, AppTheme.INFO)));
        stats.add(buildStatCard("In Progress", progressLabel,
                new Color(0x7C3AED), new Color(0xF5F3FF),
                IconFactory.edit(24, new Color(0x7C3AED))));
        stats.add(buildStatCard("Resolved", resolvedLabel,
                AppTheme.SUCCESS, AppTheme.SUCCESS_BG,
                IconFactory.check(24, AppTheme.SUCCESS)));

        content.add(stats);
        content.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // Quick actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        manageBtn = new StyledButton("Manage Complaints", StyledButton.Variant.PRIMARY,
                IconFactory.complaints(IconFactory.SIZE_MD, Color.WHITE));
        staffBtn  = new StyledButton("Manage Staff", StyledButton.Variant.SECONDARY,
                IconFactory.staff(IconFactory.SIZE_MD, AppTheme.TEXT_PRIMARY));

        actions.add(manageBtn);
        actions.add(staffBtn);
        content.add(actions);
        content.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // Info card
        CardPanel infoCard = new CardPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel infoTitle = new JLabel("Design Pattern Reference");
        infoTitle.setFont(AppTheme.FONT_SUBHEAD);
        infoTitle.setForeground(AppTheme.TEXT_PRIMARY);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(infoTitle);
        infoCard.add(Box.createVerticalStrut(8));

        String[][] patterns = {
            {"Builder", "ComplaintBuilder — complaint construction and validation"},
            {"Prototype", "ComplaintCloner / clone() — Duplicate Complaint action"},
            {"Strategy", "LeastLoaded / SkillBased / RoundRobin — staff assignment algorithms"},
            {"Template Method", "GeneralMaintenanceWorkflow / ITSupportWorkflow — resolve flow"},
            {"Mediator", "ComplaintMediatorImpl — coordinates view updates and notifications"},
            {"Memento", "ComplaintHistory + ComplaintMemento — Undo Last Change snapshot restoration"}
        };

        for (String[] p : patterns) {
            JLabel lbl = new JLabel("<html><b>" + p[0] + ":</b> <span style='color:#64748B'>" + p[1] + "</span></html>");
            lbl.setFont(AppTheme.FONT_SMALL);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoCard.add(lbl);
            infoCard.add(Box.createVerticalStrut(4));
        }

        content.add(infoCard);
        content.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    private CardPanel buildStatCard(String label, JLabel value, Color vc, Color bg, Icon icon) {
        CardPanel card = new CardPanel();
        card.setBackground(bg);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(value, BorderLayout.WEST);
        JLabel iconLabel = new JLabel(icon);
        top.add(iconLabel, BorderLayout.EAST);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_BODY);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);

        card.add(top, BorderLayout.NORTH);
        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    private JLabel statLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 34));
        l.setForeground(AppTheme.TEXT_PRIMARY);
        return l;
    }

    public void updateStats(Map<ComplaintStatus, Integer> counts) {
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        totalLabel.setText(String.valueOf(total));
        pendingLabel.setText(String.valueOf(counts.getOrDefault(ComplaintStatus.PENDING, 0)));
        assignedLabel.setText(String.valueOf(counts.getOrDefault(ComplaintStatus.ASSIGNED, 0)));
        progressLabel.setText(String.valueOf(counts.getOrDefault(ComplaintStatus.IN_PROGRESS, 0)));
        resolvedLabel.setText(String.valueOf(counts.getOrDefault(ComplaintStatus.RESOLVED, 0)));
        revalidate();
        repaint();
    }

    public void addManageListener(ActionListener l) { manageBtn.addActionListener(l); }
    public void addStaffListener(ActionListener l)  { staffBtn.addActionListener(l); }
}
