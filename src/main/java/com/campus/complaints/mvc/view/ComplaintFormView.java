package com.campus.complaints.mvc.view;

import com.campus.complaints.component.CardPanel;
import com.campus.complaints.component.StyledButton;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.Priority;
import com.campus.complaints.util.AppTheme;
import com.campus.complaints.util.IconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Complaint submission form view in the MVC architecture.
 * Integrates with ComplaintBuilder via ComplaintController.
 */
public class ComplaintFormView extends JPanel {

    private final JTextField titleField;
    private final JTextArea descArea;
    private final JComboBox<ComplaintCategory> categoryCombo;
    private final JTextField locationField;
    private final JComboBox<Priority> priorityCombo;
    private final JLabel errorLabel;
    private final StyledButton submitBtn;
    private final StyledButton cancelBtn;

    private boolean isDuplicate = false;

    public ComplaintFormView() {
        setBackground(AppTheme.BG_PAGE);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // ---- Title ----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("Submit Complaint");
        title.setFont(AppTheme.FONT_LARGE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // ---- Form card ----
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Title field
        card.add(label("Complaint Title *"));
        card.add(Box.createVerticalStrut(4));
        titleField = textField("Brief title describing the issue");
        card.add(titleField);
        card.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // Category + Priority on same row
        JPanel row1 = new JPanel(new GridLayout(1, 2, AppTheme.SPACING_MD, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel catPanel = new JPanel(new BorderLayout(0, 4));
        catPanel.setOpaque(false);
        catPanel.add(label("Category *"), BorderLayout.NORTH);
        categoryCombo = new JComboBox<>(ComplaintCategory.values());
        styleCombo(categoryCombo);
        catPanel.add(categoryCombo, BorderLayout.CENTER);

        JPanel priPanel = new JPanel(new BorderLayout(0, 4));
        priPanel.setOpaque(false);
        priPanel.add(label("Priority *"), BorderLayout.NORTH);
        priorityCombo = new JComboBox<>(Priority.values());
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        styleCombo(priorityCombo);
        priPanel.add(priorityCombo, BorderLayout.CENTER);

        row1.add(catPanel);
        row1.add(priPanel);
        card.add(row1);
        card.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // Location field
        card.add(label("Location *"));
        card.add(Box.createVerticalStrut(4));
        locationField = textField("e.g., Computer Lab 2, Block A");
        card.add(locationField);
        card.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // Description area
        card.add(label("Description *"));
        card.add(Box.createVerticalStrut(4));
        descArea = new JTextArea(5, 0);
        descArea.setFont(AppTheme.FONT_BODY);
        descArea.setForeground(AppTheme.TEXT_PRIMARY);
        descArea.setBackground(AppTheme.BG_PAGE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1, true));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descScroll);
        card.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(AppTheme.SPACING_SM));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitBtn = new StyledButton("Submit Complaint", StyledButton.Variant.PRIMARY,
                IconFactory.check(IconFactory.SIZE_MD, Color.WHITE));
        cancelBtn = new StyledButton("Cancel", StyledButton.Variant.SECONDARY);

        btnRow.add(submitBtn);
        btnRow.add(cancelBtn);
        card.add(btnRow);

        JLabel note = new JLabel("  * Required fields");
        note.setFont(AppTheme.FONT_SMALL);
        note.setForeground(AppTheme.TEXT_MUTED);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(8));
        card.add(note);

        JScrollPane cardScroll = new JScrollPane(card);
        cardScroll.setOpaque(false);
        cardScroll.getViewport().setOpaque(false);
        cardScroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(Box.createVerticalStrut(AppTheme.SPACING_LG), BorderLayout.NORTH);
        center.add(cardScroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField textField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(AppTheme.FONT_BODY);
        tf.setForeground(AppTheme.TEXT_PRIMARY);
        tf.setBackground(AppTheme.BG_PAGE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private <T> void styleCombo(JComboBox<T> combo) {
        combo.setFont(AppTheme.FONT_BODY);
        combo.setBackground(AppTheme.BG_CARD);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public String getComplaintTitle()    { return titleField.getText().trim(); }
    public String getDescription()       { return descArea.getText().trim(); }
    public String getComplaintLocation() { return locationField.getText().trim(); }
    public ComplaintCategory getCategory() { return (ComplaintCategory) categoryCombo.getSelectedItem(); }
    public Priority getPriority()  { return (Priority) priorityCombo.getSelectedItem(); }
    public void setErrorMessage(String msg) { errorLabel.setText(msg); }
    public void clearError()       { errorLabel.setText(" "); }

    public void prefill(String title, String desc, ComplaintCategory cat,
                         Priority priority, String location) {
        titleField.setText(title);
        descArea.setText(desc);
        categoryCombo.setSelectedItem(cat);
        priorityCombo.setSelectedItem(priority);
        locationField.setText(location);
        isDuplicate = true;
    }

    public void clearForm() {
        titleField.setText("");
        descArea.setText("");
        locationField.setText("");
        categoryCombo.setSelectedIndex(0);
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        clearError();
        isDuplicate = false;
    }

    public boolean isDuplicate() { return isDuplicate; }

    public void addSubmitListener(ActionListener l) { submitBtn.addActionListener(l); }
    public void addCancelListener(ActionListener l) { cancelBtn.addActionListener(l); }
}
