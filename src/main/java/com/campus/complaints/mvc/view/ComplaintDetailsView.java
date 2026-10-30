package com.campus.complaints.mvc.view;

import com.campus.complaints.component.BadgeLabel;
import com.campus.complaints.component.CardPanel;
import com.campus.complaints.component.StyledButton;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;
import com.campus.complaints.util.AppTheme;
import com.campus.complaints.util.IconFactory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

/**
 * Detailed complaint view panel in the MVC architecture.
 * Displays all complaint fields and exposes operations:
 * Assign Staff, Change Status, Resolve, Duplicate, and Undo.
 */
public class ComplaintDetailsView extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private Complaint currentComplaint;
    private final boolean isAdmin;

    // Detail labels
    private final JLabel idLabel         = detail();
    private final JLabel titleLabel      = detail();
    private final JLabel categoryLabel   = detail();
    private final JLabel locationLabel   = detail();
    private final JLabel priorityLabel   = detail();
    private final JLabel statusLabel     = detail();
    private final JLabel reporterLabel   = detail();
    private final JLabel staffLabel      = detail();
    private final JLabel createdLabel    = detail();
    private final JLabel updatedLabel    = detail();
    private final JLabel resolvedLabel   = detail();
    private final JTextArea descArea     = new JTextArea();
    private final JTextArea resolutionArea = new JTextArea();

    // Badges
    private final BadgeLabel statusBadge   = BadgeLabel.forStatus(ComplaintStatus.PENDING);
    private final BadgeLabel priorityBadge = BadgeLabel.forPriority(Priority.MEDIUM);

    // Action buttons (admin)
    private final StyledButton assignBtn;
    private final StyledButton resolveBtn;
    private final StyledButton undoBtn;
    private final StyledButton duplicateBtn;
    private final StyledButton editDescBtn;

    // Status change combo (admin)
    private final JComboBox<ComplaintStatus> statusCombo;

    public ComplaintDetailsView(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setBackground(AppTheme.BG_PAGE);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // ---- Title bar ----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("Complaint Details");
        title.setFont(AppTheme.FONT_LARGE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // ---- Detail card ----
        CardPanel detailCard = new CardPanel();
        detailCard.setLayout(new BorderLayout(0, 0));
        detailCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card header
        JPanel cardHeader = new JPanel(new BorderLayout(12, 0));
        cardHeader.setBackground(new Color(0xF8FAFC));
        cardHeader.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));
        titleLabel.setFont(AppTheme.FONT_SUBHEAD);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        cardHeader.add(titleLabel, BorderLayout.WEST);
        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        badges.setOpaque(false);
        badges.add(priorityBadge);
        badges.add(statusBadge);
        cardHeader.add(badges, BorderLayout.EAST);
        detailCard.add(cardHeader, BorderLayout.NORTH);

        // Grid of fields
        JPanel grid = new JPanel(new GridLayout(0, 2, 0, 0));
        grid.setBackground(AppTheme.BG_CARD);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        addField(grid, "Complaint ID", idLabel);
        addField(grid, "Category", categoryLabel);
        addField(grid, "Location", locationLabel);
        addField(grid, "Priority", priorityLabel);
        addField(grid, "Status", statusLabel);
        addField(grid, "Reporter", reporterLabel);
        addField(grid, "Assigned Staff", staffLabel);
        addField(grid, "Submitted", createdLabel);
        addField(grid, "Last Updated", updatedLabel);
        addField(grid, "Resolved At", resolvedLabel);

        detailCard.add(grid, BorderLayout.CENTER);

        // Description
        JPanel descSection = new JPanel(new BorderLayout(0, 4));
        descSection.setBackground(AppTheme.BG_CARD);
        descSection.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(AppTheme.FONT_SUBHEAD);
        descLabel.setForeground(AppTheme.TEXT_SECONDARY);
        descArea.setFont(AppTheme.FONT_BODY);
        descArea.setForeground(AppTheme.TEXT_PRIMARY);
        descArea.setBackground(AppTheme.BG_PAGE);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        descArea.setRows(3);

        descSection.add(descLabel, BorderLayout.NORTH);
        descSection.add(new JScrollPane(descArea) {{
            setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
            setPreferredSize(new Dimension(0, 80));
        }}, BorderLayout.CENTER);

        // Resolution notes
        JPanel resSection = new JPanel(new BorderLayout(0, 4));
        resSection.setBackground(AppTheme.BG_CARD);
        resSection.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 20));

        JLabel resLabel = new JLabel("Resolution Notes");
        resLabel.setFont(AppTheme.FONT_SUBHEAD);
        resLabel.setForeground(AppTheme.TEXT_SECONDARY);
        resolutionArea.setFont(AppTheme.FONT_BODY);
        resolutionArea.setForeground(AppTheme.TEXT_SECONDARY);
        resolutionArea.setBackground(AppTheme.BG_PAGE);
        resolutionArea.setEditable(false);
        resolutionArea.setLineWrap(true);
        resolutionArea.setWrapStyleWord(true);
        resolutionArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        resolutionArea.setRows(2);

        resSection.add(resLabel, BorderLayout.NORTH);
        resSection.add(new JScrollPane(resolutionArea) {{
            setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
            setPreferredSize(new Dimension(0, 60));
        }}, BorderLayout.CENTER);

        JPanel bottomSection = new JPanel(new BorderLayout(0, 0));
        bottomSection.setBackground(AppTheme.BG_CARD);
        bottomSection.add(descSection, BorderLayout.NORTH);
        bottomSection.add(resSection,  BorderLayout.CENTER);
        detailCard.add(bottomSection, BorderLayout.SOUTH);

        content.add(detailCard);
        content.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // ---- Actions card ----
        CardPanel actionsCard = new CardPanel();
        actionsCard.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 12));
        actionsCard.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        actionsCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        duplicateBtn = new StyledButton("Duplicate", StyledButton.Variant.SECONDARY,
                IconFactory.duplicate(IconFactory.SIZE_MD, AppTheme.TEXT_PRIMARY));
        actionsCard.add(duplicateBtn);

        if (isAdmin) {
            assignBtn   = new StyledButton("Assign Staff", StyledButton.Variant.PRIMARY,
                    IconFactory.assign(IconFactory.SIZE_MD, Color.WHITE));
            resolveBtn  = new StyledButton("Resolve Complaint", StyledButton.Variant.SUCCESS,
                    IconFactory.resolve(IconFactory.SIZE_MD, Color.WHITE));
            undoBtn     = new StyledButton("Undo Last Change", StyledButton.Variant.GHOST,
                    IconFactory.undo(IconFactory.SIZE_MD, AppTheme.PRIMARY));
            editDescBtn = new StyledButton("Edit Description", StyledButton.Variant.GHOST,
                    IconFactory.edit(IconFactory.SIZE_MD, AppTheme.TEXT_SECONDARY));

            statusCombo = new JComboBox<>(ComplaintStatus.values());
            statusCombo.setFont(AppTheme.FONT_BODY);
            statusCombo.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list,
                        Object val, int idx, boolean sel, boolean focus) {
                    super.getListCellRendererComponent(list, val, idx, sel, focus);
                    if (val instanceof ComplaintStatus cs) setText(cs.getDisplayName());
                    return this;
                }
            });

            actionsCard.add(assignBtn);
            actionsCard.add(new JLabel("  Change Status:"));
            actionsCard.add(statusCombo);
            actionsCard.add(resolveBtn);
            actionsCard.add(undoBtn);
            actionsCard.add(editDescBtn);
        } else {
            assignBtn   = null;
            resolveBtn  = null;
            undoBtn     = null;
            editDescBtn = null;
            statusCombo = null;
        }

        content.add(actionsCard);

        add(new JScrollPane(content) {{
            setOpaque(false);
            getViewport().setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
        }}, BorderLayout.CENTER);
    }

    private void addField(JPanel grid, String label, JLabel value) {
        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setBackground(AppTheme.BG_CARD);
        cell.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 1, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        cell.add(lbl,   BorderLayout.NORTH);
        cell.add(value, BorderLayout.CENTER);
        grid.add(cell);
    }

    private JLabel detail() {
        JLabel l = new JLabel("-");
        l.setFont(AppTheme.FONT_BODY);
        l.setForeground(AppTheme.TEXT_PRIMARY);
        return l;
    }

    public void loadComplaint(Complaint complaint, String reporterName, String staffName) {
        this.currentComplaint = complaint;
        idLabel.setText("#" + complaint.getId());
        titleLabel.setText(complaint.getTitle());
        categoryLabel.setText(complaint.getCategory().getDisplayName());
        locationLabel.setText(complaint.getLocation());
        priorityLabel.setText(complaint.getPriority().getDisplayName());
        statusLabel.setText(complaint.getStatus().getDisplayName());
        reporterLabel.setText(reporterName != null ? reporterName : "-");
        staffLabel.setText(staffName != null ? staffName : "Not assigned");
        createdLabel.setText(complaint.getCreatedAt() != null
                ? complaint.getCreatedAt().format(FMT) : "-");
        updatedLabel.setText(complaint.getUpdatedAt() != null
                ? complaint.getUpdatedAt().format(FMT) : "-");
        resolvedLabel.setText(complaint.getResolvedAt() != null
                ? complaint.getResolvedAt().format(FMT) : "-");
        descArea.setText(complaint.getDescription());
        resolutionArea.setText(complaint.getResolutionNotes() != null
                ? complaint.getResolutionNotes() : "");

        updateBadge(statusBadge, BadgeLabel.forStatus(complaint.getStatus()));
        updateBadge(priorityBadge, BadgeLabel.forPriority(complaint.getPriority()));

        if (statusCombo != null) statusCombo.setSelectedItem(complaint.getStatus());
        if (undoBtn != null)     undoBtn.setEnabled(true);

        revalidate();
        repaint();
    }

    private void updateBadge(BadgeLabel target, BadgeLabel source) {
        target.setText(source.getText());
        source.getParent();
    }

    public void setUndoEnabled(boolean enabled) {
        if (undoBtn != null) undoBtn.setEnabled(enabled);
    }

    public Complaint getCurrentComplaint() { return currentComplaint; }
    public ComplaintStatus getSelectedStatus() {
        return statusCombo != null ? (ComplaintStatus) statusCombo.getSelectedItem() : null;
    }

    public void addAssignListener(ActionListener l)    { if (assignBtn   != null) assignBtn.addActionListener(l); }
    public void addResolveListener(ActionListener l)   { if (resolveBtn  != null) resolveBtn.addActionListener(l); }
    public void addUndoListener(ActionListener l)      { if (undoBtn     != null) undoBtn.addActionListener(l); }
    public void addDuplicateListener(ActionListener l) { duplicateBtn.addActionListener(l); }
    public void addEditDescListener(ActionListener l)  { if (editDescBtn != null) editDescBtn.addActionListener(l); }
    public void addStatusChangeListener(ActionListener l) {
        if (statusCombo != null) statusCombo.addActionListener(l);
    }
}
