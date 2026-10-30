package com.campus.complaints.mvc.view;

import com.campus.complaints.component.CardPanel;
import com.campus.complaints.component.StyledButton;
import com.campus.complaints.util.AppTheme;
import com.campus.complaints.util.IconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Login screen view in the MVC architecture.
 * Clean, centered card layout with email and password fields.
 */
public class LoginView extends JPanel {

    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final StyledButton loginBtn;
    private final JLabel errorLabel;

    public LoginView() {
        setBackground(AppTheme.BG_PAGE);
        setLayout(new GridBagLayout());

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(420, 480));

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(36, 32, 36, 32));

        JLabel logo = new JLabel("CampusDesk");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setIcon(IconFactory.complaints(24, Color.WHITE));
        logo.setIconTextGap(10);
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("Complaint & Maintenance System");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(new Color(0xBFDBFE));
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerText.add(logo);
        headerText.add(Box.createVerticalStrut(6));
        headerText.add(sub);

        header.add(headerText, BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        // ---- Form ----
        JPanel form = new JPanel();
        form.setBackground(AppTheme.BG_CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(32, 32, 24, 32));

        JLabel signInLabel = new JLabel("Sign In");
        signInLabel.setFont(AppTheme.FONT_HEADING);
        signInLabel.setForeground(AppTheme.TEXT_PRIMARY);
        signInLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(signInLabel);
        form.add(Box.createVerticalStrut(AppTheme.SPACING_LG));

        // Email field
        form.add(fieldLabel("Email Address"));
        form.add(Box.createVerticalStrut(4));
        emailField = createTextField("admin@campus.edu");
        form.add(emailField);
        form.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // Password field
        form.add(fieldLabel("Password"));
        form.add(Box.createVerticalStrut(4));
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordField.setText("admin123");
        form.add(passwordField);
        form.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(AppTheme.SPACING_SM));

        // Login button
        loginBtn = new StyledButton("Sign In", StyledButton.Variant.PRIMARY);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(AppTheme.SPACING_MD));

        // Demo hint
        JLabel hint = new JLabel("<html><center><span style='color:#64748B;font-size:10px;'>" +
                "Admin: admin@campus.edu (admin123)<br>Students: student1@campus.edu / student2@campus.edu (student123)</span></center></html>");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(hint);

        card.add(form, BorderLayout.CENTER);
        add(card, new GridBagConstraints());
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        styleTextField(tf);
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(AppTheme.FONT_BODY);
        tf.setForeground(AppTheme.TEXT_PRIMARY);
        tf.setBackground(AppTheme.BG_PAGE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // ---- Public API ----

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText(" ");
    }

    public void addLoginListener(ActionListener listener) {
        loginBtn.addActionListener(listener);
        emailField.addActionListener(listener);
        passwordField.addActionListener(listener);
    }
}
