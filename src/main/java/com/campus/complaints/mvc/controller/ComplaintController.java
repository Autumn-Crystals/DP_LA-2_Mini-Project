package com.campus.complaints.mvc.controller;

import com.campus.complaints.designpatterns.mediator.ComplaintMediator;
import com.campus.complaints.designpatterns.mediator.ComplaintMediatorImpl;
import com.campus.complaints.designpatterns.strategy.LeastLoadedStaffStrategy;
import com.campus.complaints.designpatterns.strategy.RoundRobinAssignmentStrategy;
import com.campus.complaints.designpatterns.strategy.SkillBasedAssignmentStrategy;
import com.campus.complaints.designpatterns.strategy.StaffAssignmentStrategy;
import com.campus.complaints.designpatterns.template.WorkflowException;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.Priority;
import com.campus.complaints.model.User;
import com.campus.complaints.mvc.view.ComplaintDetailsView;
import com.campus.complaints.mvc.view.ComplaintFormView;
import com.campus.complaints.mvc.view.ComplaintManagementView;
import com.campus.complaints.service.AuthenticationService;
import com.campus.complaints.service.ComplaintService;
import com.campus.complaints.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

/**
 * MVC Controller for complaint operations: submission, listing, filtering,
 * automated staff assignment, resolution, memento undo, and prototype duplication.
 */
public class ComplaintController {

    private static final Logger log = LoggerFactory.getLogger(ComplaintController.class);

    private final ComplaintService complaintService;
    private final StaffService staffService;
    private final AuthenticationService authService;
    private final ComplaintMediatorImpl mediator;

    private final ComplaintManagementView listView;
    private final ComplaintDetailsView detailsView;
    private final ComplaintFormView formView;

    private Complaint selectedComplaint;

    private Runnable showListAction;
    private Runnable showFormAction;
    private Runnable showDetailsViewAction;

    public ComplaintController(ComplaintService complaintService,
                               StaffService staffService,
                               AuthenticationService authService,
                               ComplaintMediatorImpl mediator,
                               ComplaintManagementView listView,
                               ComplaintDetailsView detailsView,
                               ComplaintFormView formView) {
        this.complaintService = complaintService;
        this.staffService     = staffService;
        this.authService      = authService;
        this.mediator         = mediator;
        this.listView         = listView;
        this.detailsView      = detailsView;
        this.formView         = formView;

        attachListeners();
    }

    private void attachListeners() {
        if (listView != null) {
            listView.setOnViewListener(e -> {
                int row = Integer.parseInt(e.getActionCommand());
                Complaint c = listView.getComplaintAtRow(row);
                if (c != null) showDetails(c);
            });

            listView.setOnDuplicateListener(e -> {
                int row = Integer.parseInt(e.getActionCommand());
                Complaint c = listView.getComplaintAtRow(row);
                if (c != null) duplicateComplaint(c);
            });

            listView.addSearchListener(e -> refreshList());
        }

        if (formView != null) {
            formView.addSubmitListener(e -> handleSubmit());
            formView.addCancelListener(e -> navigateToList());
        }

        if (detailsView != null) {
            detailsView.addAssignListener(e -> handleAutoAssign());
            detailsView.addResolveListener(e -> handleResolve());
            detailsView.addUndoListener(e -> handleUndo());
            detailsView.addDuplicateListener(e -> {
                if (selectedComplaint != null) duplicateComplaint(selectedComplaint);
            });
            detailsView.addEditDescListener(e -> handleEditDescription());
            detailsView.addStatusChangeListener(e -> handleStatusChange());
        }
    }

    private void navigateToList() {
        if (formView != null) formView.clearForm();
        showList();
    }

    public void refreshList() {
        User user = authService.getCurrentUser();
        if (user == null || listView == null) return;

        List<Complaint> complaints;
        String query              = listView.getSearchQuery();
        ComplaintStatus status    = listView.getSelectedStatus();
        ComplaintCategory category = listView.getSelectedCategory();

        if (user.isAdmin()) {
            if (query != null && !query.isBlank()) {
                complaints = complaintService.searchComplaints(query);
            } else {
                complaints = complaintService.getAllComplaints();
            }
        } else {
            complaints = complaintService.getComplaintsByReporter(user.getId());
        }

        if (status != null) {
            final ComplaintStatus fs = status;
            complaints = complaints.stream().filter(c -> c.getStatus() == fs).toList();
        }
        if (category != null) {
            final ComplaintCategory fc = category;
            complaints = complaints.stream().filter(c -> c.getCategory() == fc).toList();
        }

        listView.loadComplaints(complaints);
    }

    private void handleSubmit() {
        if (formView == null) return;
        formView.clearError();
        User user = authService.getCurrentUser();
        if (user == null) return;

        String title    = formView.getComplaintTitle();
        String desc     = formView.getDescription();
        String location = formView.getComplaintLocation();
        ComplaintCategory cat = formView.getCategory();
        Priority priority     = formView.getPriority();

        if (title.isBlank()) { formView.setErrorMessage("Title is required."); return; }
        if (desc.isBlank())  { formView.setErrorMessage("Description is required."); return; }
        if (location.isBlank()) { formView.setErrorMessage("Location is required."); return; }

        try {
            Complaint saved = complaintService.submitComplaint(
                    title, desc, cat, location, priority, user.getId());

            formView.clearForm();
            if (mediator != null) mediator.onComplaintSubmitted(saved);
            showList();

        } catch (IllegalStateException e) {
            formView.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Submit complaint failed: {}", e.getMessage());
            formView.setErrorMessage("Failed to submit complaint. Please check database connection.");
        }
    }

    public void duplicateComplaint(Complaint original) {
        Complaint copy = complaintService.duplicateComplaint(original.getId());

        if (formView != null) {
            formView.prefill(copy.getTitle(), copy.getDescription(),
                    copy.getCategory(), copy.getPriority(), copy.getLocation());
        }

        showForm();
        if (mediator != null) {
            mediator.showNotification(
                    "Complaint duplicated via Prototype Pattern — update Location/Description before submitting.",
                    ComplaintMediator.NotificationType.INFO);
        }
    }

    private void handleAutoAssign() {
        if (selectedComplaint == null) return;
        User admin = authService.getCurrentUser();
        if (admin == null || !admin.isAdmin()) {
            if (mediator != null) mediator.showNotification("Access denied.", ComplaintMediator.NotificationType.ERROR);
            return;
        }

        String[] labels = {"Least-Loaded Staff", "Skill-Based Assignment", "Round-Robin Assignment"};
        JComboBox<String> strategyCombo = new JComboBox<>(labels);
        JPanel panel = new JPanel(new java.awt.BorderLayout(0, 8));
        panel.add(new JLabel("Select Strategy Pattern algorithm:"), java.awt.BorderLayout.NORTH);
        panel.add(strategyCombo, java.awt.BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Auto-Assign Staff (Strategy Pattern)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        int idx = strategyCombo.getSelectedIndex();
        StaffAssignmentStrategy strategy = switch (idx) {
            case 1 -> new SkillBasedAssignmentStrategy();
            case 2 -> new RoundRobinAssignmentStrategy();
            default -> new LeastLoadedStaffStrategy();
        };
        complaintService.setAssignmentStrategy(strategy);

        try {
            Optional<MaintenanceStaff> assigned =
                    complaintService.autoAssignStaff(selectedComplaint, admin.getId());

            if (assigned.isPresent()) {
                if (mediator != null) mediator.onStaffAssigned(selectedComplaint, assigned.get());
                showDetails(selectedComplaint);
            } else {
                if (mediator != null) {
                    mediator.showNotification(
                            "No eligible staff found using " + strategy.getStrategyName() + ".",
                            ComplaintMediator.NotificationType.WARNING);
                }
            }
        } catch (Exception e) {
            log.error("Auto-assign failed: {}", e.getMessage());
            if (mediator != null) {
                mediator.showNotification("Assignment failed: " + e.getMessage(),
                        ComplaintMediator.NotificationType.ERROR);
            }
        }
    }

    private void handleResolve() {
        if (selectedComplaint == null) return;
        User admin = authService.getCurrentUser();
        if (admin == null || !admin.isAdmin()) return;

        JTextArea notesArea = new JTextArea(5, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(notesArea);

        JPanel panel = new JPanel(new java.awt.BorderLayout(0, 8));
        panel.add(new JLabel("Enter resolution notes (required for Template Method workflow):"), java.awt.BorderLayout.NORTH);
        panel.add(scroll, java.awt.BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Resolve Complaint #" + selectedComplaint.getId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String notes = notesArea.getText().trim();

        try {
            complaintService.resolveComplaint(selectedComplaint, notes, admin.getId());
            if (mediator != null) mediator.onComplaintStatusChanged(selectedComplaint);
            showDetails(selectedComplaint);

        } catch (WorkflowException e) {
            if (mediator != null) {
                mediator.showNotification("Resolution failed: " + e.getMessage(),
                        ComplaintMediator.NotificationType.ERROR);
            }
        } catch (Exception e) {
            log.error("Resolve complaint failed: {}", e.getMessage());
            if (mediator != null) {
                mediator.showNotification("Error resolving complaint: " + e.getMessage(),
                        ComplaintMediator.NotificationType.ERROR);
            }
        }
    }

    private void handleUndo() {
        if (selectedComplaint == null) return;
        User admin = authService.getCurrentUser();
        if (admin == null || !admin.isAdmin()) return;

        int confirm = JOptionPane.showConfirmDialog(null,
                "Undo the last state change to complaint #" + selectedComplaint.getId() + "?\n" +
                "Restores snapshot via Memento Pattern.",
                "Confirm Undo (Memento Pattern)", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean undone = complaintService.undoLastChange(selectedComplaint, admin.getId());
        if (undone) {
            if (mediator != null) mediator.onUndoPerformed(selectedComplaint);
            updateUndoButton();
            showDetails(selectedComplaint);
        } else {
            if (mediator != null) {
                mediator.showNotification("No previous state to restore for this complaint.",
                        ComplaintMediator.NotificationType.WARNING);
            }
            if (detailsView != null) detailsView.setUndoEnabled(false);
        }
    }

    private void handleStatusChange() {
        if (selectedComplaint == null || detailsView == null) return;
        ComplaintStatus newStatus = detailsView.getSelectedStatus();
        if (newStatus == null || newStatus == selectedComplaint.getStatus()) return;

        User admin = authService.getCurrentUser();
        if (admin == null || !admin.isAdmin()) return;

        if (!selectedComplaint.getStatus().canTransitionTo(newStatus)) {
            if (mediator != null) {
                mediator.showNotification(
                        "Invalid transition: " + selectedComplaint.getStatus().getDisplayName() +
                        " → " + newStatus.getDisplayName(),
                        ComplaintMediator.NotificationType.ERROR);
            }
            return;
        }

        try {
            complaintService.updateStatus(selectedComplaint, newStatus, admin.getId());
            if (mediator != null) mediator.onComplaintStatusChanged(selectedComplaint);
            updateUndoButton();
            showDetails(selectedComplaint);
        } catch (IllegalStateException e) {
            if (mediator != null) mediator.showNotification(e.getMessage(), ComplaintMediator.NotificationType.ERROR);
        }
    }

    private void handleEditDescription() {
        if (selectedComplaint == null) return;
        User admin = authService.getCurrentUser();
        if (admin == null || !admin.isAdmin()) return;

        JTextArea area = new JTextArea(selectedComplaint.getDescription(), 5, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        int r = JOptionPane.showConfirmDialog(null, new JScrollPane(area),
                "Edit Description", JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        String newDesc = area.getText().trim();
        if (newDesc.isBlank()) {
            if (mediator != null) mediator.showNotification("Description cannot be empty.", ComplaintMediator.NotificationType.ERROR);
            return;
        }

        complaintService.updateDescription(selectedComplaint, newDesc, admin.getId());
        if (mediator != null) mediator.onComplaintStatusChanged(selectedComplaint);
        updateUndoButton();
        showDetails(selectedComplaint);
    }

    private void showDetails(Complaint complaint) {
        this.selectedComplaint = complaint;
        if (detailsView == null) return;

        String reporterName = "User #" + complaint.getReporterId();
        String staffName    = null;
        if (complaint.getAssignedStaffId() != null) {
            staffName = staffService.findById(complaint.getAssignedStaffId())
                    .map(MaintenanceStaff::getName)
                    .orElse("Staff #" + complaint.getAssignedStaffId());
        }

        detailsView.loadComplaint(complaint, reporterName, staffName);
        updateUndoButton();
        showDetailsView();
    }

    private void updateUndoButton() {
        if (detailsView != null && selectedComplaint != null) {
            detailsView.setUndoEnabled(complaintService.canUndo(selectedComplaint.getId()));
        }
    }

    public void setShowListAction(Runnable r)        { this.showListAction        = r; }
    public void setShowFormAction(Runnable r)        { this.showFormAction        = r; }
    public void setShowDetailsViewAction(Runnable r) { this.showDetailsViewAction = r; }

    private void showList()        { if (showListAction        != null) showListAction.run(); }
    private void showForm()        { if (showFormAction        != null) showFormAction.run(); }
    private void showDetailsView() { if (showDetailsViewAction != null) showDetailsViewAction.run(); }
}
