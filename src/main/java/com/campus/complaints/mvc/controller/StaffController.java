package com.campus.complaints.mvc.controller;

import com.campus.complaints.designpatterns.mediator.ComplaintMediator;
import com.campus.complaints.designpatterns.mediator.ComplaintMediatorImpl;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.MaintenanceStaff.AvailabilityStatus;
import com.campus.complaints.mvc.view.StaffManagementView;
import com.campus.complaints.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MVC Controller for maintenance technician staff operations:
 * listing staff, updating load/availability, adding new personnel, and skill configurations.
 */
public class StaffController {

    private static final Logger log = LoggerFactory.getLogger(StaffController.class);

    private final StaffService staffService;
    private final StaffManagementView view;
    private final ComplaintMediatorImpl mediator;

    public StaffController(StaffService staffService,
                           StaffManagementView view,
                           ComplaintMediatorImpl mediator) {
        this.staffService = staffService;
        this.view         = view;
        this.mediator     = mediator;

        attachListeners();
    }

    private void attachListeners() {
        if (view != null) {
            view.setOnAddNewListener(e -> showAddDialog());
            view.setOnEditListener(e -> {
                int row = Integer.parseInt(e.getActionCommand());
                MaintenanceStaff staff = view.getStaffAtRow(row);
                if (staff != null) showEditDialog(staff);
            });
        }
    }

    public void refreshList() {
        try {
            List<MaintenanceStaff> staff = staffService.getAllStaffWithLoad();
            if (view != null) view.loadStaff(staff);
        } catch (Exception e) {
            log.error("Failed to load staff: {}", e.getMessage());
            if (mediator != null) {
                mediator.showNotification("Failed to load staff list: " + e.getMessage(),
                        ComplaintMediator.NotificationType.ERROR);
            }
        }
    }

    private void showAddDialog() {
        JPanel form = buildStaffForm(null);
        int result = JOptionPane.showConfirmDialog(null, form,
                "Add New Staff Member", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name  = ((JTextField) getComponent(form, "nameField")).getText().trim();
        String email = ((JTextField) getComponent(form, "emailField")).getText().trim();
        String phone = ((JTextField) getComponent(form, "phoneField")).getText().trim();
        List<ComplaintCategory> skills = getSelectedSkills(form);

        try {
            staffService.addStaff(name, email, phone, skills);
            if (mediator != null) {
                mediator.showNotification("Staff member added: " + name, ComplaintMediator.NotificationType.SUCCESS);
            }
            refreshList();
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            log.error("Add staff failed: {}", e.getMessage());
            if (mediator != null) {
                mediator.showNotification("Failed to add staff: " + e.getMessage(),
                        ComplaintMediator.NotificationType.ERROR);
            }
        }
    }

    private void showEditDialog(MaintenanceStaff staff) {
        JPanel form = buildStaffForm(staff);
        int result = JOptionPane.showConfirmDialog(null, form,
                "Edit Staff: " + staff.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        staff.setName(((JTextField) getComponent(form, "nameField")).getText().trim());
        staff.setPhone(((JTextField) getComponent(form, "phoneField")).getText().trim());

        JComboBox<?> availCombo = (JComboBox<?>) getComponent(form, "availCombo");
        staff.setAvailabilityStatus((AvailabilityStatus) availCombo.getSelectedItem());
        staff.setSkills(getSelectedSkills(form));

        try {
            staffService.updateStaff(staff);
            staffService.updateSkills(staff.getId(), staff.getSkills());
            if (mediator != null) {
                mediator.showNotification("Staff updated: " + staff.getName(), ComplaintMediator.NotificationType.SUCCESS);
            }
            refreshList();
        } catch (Exception e) {
            log.error("Update staff failed: {}", e.getMessage());
            if (mediator != null) {
                mediator.showNotification("Update failed: " + e.getMessage(),
                        ComplaintMediator.NotificationType.ERROR);
            }
        }
    }

    private JPanel buildStaffForm(MaintenanceStaff staff) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 2, 3, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int y = 0;

        JTextField nameField = new JTextField(staff != null ? staff.getName() : "");
        nameField.setName("nameField");
        nameField.setPreferredSize(new Dimension(380, 30));

        JTextField emailField = new JTextField(staff != null ? staff.getEmail() : "");
        emailField.setName("emailField");
        emailField.setPreferredSize(new Dimension(380, 30));
        if (staff != null) emailField.setEnabled(false);

        JTextField phoneField = new JTextField(staff != null && staff.getPhone() != null ? staff.getPhone() : "");
        phoneField.setName("phoneField");
        phoneField.setPreferredSize(new Dimension(380, 30));

        JComboBox<AvailabilityStatus> availCombo = new JComboBox<>(AvailabilityStatus.values());
        availCombo.setName("availCombo");
        availCombo.setPreferredSize(new Dimension(380, 30));
        if (staff != null) availCombo.setSelectedItem(staff.getAvailabilityStatus());

        // Full Name
        gbc.gridy = y++;
        form.add(new JLabel("Full Name *"), gbc);
        gbc.gridy = y++;
        form.add(nameField, gbc);

        // Email
        gbc.gridy = y++;
        form.add(new JLabel("Email Address *" + (staff != null ? " (read-only)" : "")), gbc);
        gbc.gridy = y++;
        form.add(emailField, gbc);

        // Phone
        gbc.gridy = y++;
        form.add(new JLabel("Phone Number"), gbc);
        gbc.gridy = y++;
        form.add(phoneField, gbc);

        // Availability (when editing existing staff)
        if (staff != null) {
            gbc.gridy = y++;
            form.add(new JLabel("Availability Status"), gbc);
            gbc.gridy = y++;
            form.add(availCombo, gbc);
        }

        // Skills Header
        gbc.gridy = y++;
        gbc.insets = new Insets(10, 2, 4, 2);
        JLabel skillLabel = new JLabel("Assigned Skills (Category Expertise):");
        skillLabel.setFont(skillLabel.getFont().deriveFont(Font.BOLD));
        form.add(skillLabel, gbc);

        // Skills Checkbox Grid
        JPanel skillPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        skillPanel.setName("skillPanel");
        skillPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        for (ComplaintCategory cat : ComplaintCategory.values()) {
            JCheckBox cb = new JCheckBox(cat.getDisplayName());
            cb.putClientProperty("category", cat);
            cb.setFocusPainted(false);
            if (staff != null && staff.getSkills().contains(cat)) {
                cb.setSelected(true);
            }
            skillPanel.add(cb);
        }

        gbc.gridy = y++;
        gbc.insets = new Insets(2, 2, 4, 2);
        form.add(skillPanel, gbc);

        return form;
    }

    private List<ComplaintCategory> getSelectedSkills(JPanel form) {
        List<ComplaintCategory> selected = new ArrayList<>();
        JPanel skillPanel = (JPanel) getComponent(form, "skillPanel");
        if (skillPanel != null) {
            for (Component comp : skillPanel.getComponents()) {
                if (comp instanceof JCheckBox cb && cb.isSelected()) {
                    selected.add((ComplaintCategory) cb.getClientProperty("category"));
                }
            }
        }
        return selected;
    }

    private Component getComponent(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name.equals(c.getName())) return c;
            if (c instanceof Container sub) {
                Component found = getComponent(sub, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}
