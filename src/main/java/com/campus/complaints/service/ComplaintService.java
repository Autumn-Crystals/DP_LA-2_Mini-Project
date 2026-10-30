package com.campus.complaints.service;

import com.campus.complaints.dao.ComplaintDAO;
import com.campus.complaints.dao.ComplaintHistoryDAO;
import com.campus.complaints.dao.MaintenanceStaffDAO;
import com.campus.complaints.dao.StaffDAO;
import com.campus.complaints.dao.impl.ComplaintDAOImpl;
import com.campus.complaints.dao.impl.MaintenanceStaffDAOImpl;
import com.campus.complaints.designpatterns.builder.ComplaintBuilder;
import com.campus.complaints.designpatterns.memento.ComplaintHistory;
import com.campus.complaints.designpatterns.memento.ComplaintMemento;
import com.campus.complaints.designpatterns.prototype.ComplaintCloner;
import com.campus.complaints.designpatterns.strategy.LeastLoadedStaffStrategy;
import com.campus.complaints.designpatterns.strategy.StaffAssignmentStrategy;
import com.campus.complaints.designpatterns.template.ComplaintWorkflow;
import com.campus.complaints.designpatterns.template.GeneralMaintenanceWorkflow;
import com.campus.complaints.designpatterns.template.ITSupportWorkflow;
import com.campus.complaints.designpatterns.template.WorkflowException;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Core business logic service for complaint operations.
 *
 * <p>Coordinates the following GoF design patterns:
 * <ul>
 *   <li><b>Builder Pattern</b>: {@link ComplaintBuilder} for validating and constructing Complaints.</li>
 *   <li><b>Prototype Pattern</b>: {@link ComplaintCloner} and {@link Complaint#clone()} for duplicating complaints.</li>
 *   <li><b>Strategy Pattern</b>: {@link StaffAssignmentStrategy} for runtime algorithm selection.</li>
 *   <li><b>Template Method Pattern</b>: {@link ComplaintWorkflow} for category-specific resolution.</li>
 *   <li><b>Memento Pattern</b>: {@link ComplaintMemento} and {@link ComplaintHistory} for undo functionality.</li>
 * </ul>
 */
public class ComplaintService {

    private static final Logger log = LoggerFactory.getLogger(ComplaintService.class);

    private final ComplaintDAO complaintDAO;
    private final MaintenanceStaffDAO staffDAO;
    private final ComplaintHistoryDAO historyDAO;

    // Memento caretaker: per-complaint in-memory history stacks
    private final Map<Integer, ComplaintHistory> historyMap = new HashMap<>();

    // Current strategy (runtime-selectable)
    private StaffAssignmentStrategy assignmentStrategy = new LeastLoadedStaffStrategy();

    public ComplaintService() {
        this.complaintDAO = new ComplaintDAOImpl();
        this.staffDAO     = new MaintenanceStaffDAOImpl();
        this.historyDAO   = new ComplaintHistoryDAO();
    }

    public ComplaintService(ComplaintDAO complaintDAO, MaintenanceStaffDAO staffDAO,
                            ComplaintHistoryDAO historyDAO) {
        this.complaintDAO = complaintDAO;
        this.staffDAO     = staffDAO;
        this.historyDAO   = historyDAO;
    }

    // =====================================================================
    // Strategy Pattern — runtime strategy selection
    // =====================================================================

    public void setAssignmentStrategy(StaffAssignmentStrategy strategy) {
        this.assignmentStrategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
        log.info("Assignment strategy set to: {}", strategy.getStrategyName());
    }

    public StaffAssignmentStrategy getAssignmentStrategy() {
        return assignmentStrategy;
    }

    // =====================================================================
    // Complaint CRUD & Builder Pattern
    // =====================================================================

    /**
     * Submits a new complaint using the dedicated Builder Pattern.
     * Validates required fields and persists to the underlying store.
     */
    public Complaint submitComplaint(String title, String description,
                                     ComplaintCategory category, String location,
                                     Priority priority, int reporterId) {
        // Builder Pattern in action
        Complaint complaint = new ComplaintBuilder()
                .title(title)
                .description(description)
                .category(category)
                .location(location)
                .priority(priority)
                .reporterId(reporterId)
                .build();

        return complaintDAO.save(complaint);
    }

    public Optional<Complaint> findById(int id) {
        return complaintDAO.findById(id);
    }

    public List<Complaint> getAllComplaints() {
        return complaintDAO.findAll();
    }

    public List<Complaint> getComplaintsByReporter(int reporterId) {
        return complaintDAO.findByReporterId(reporterId);
    }

    public List<Complaint> searchComplaints(String query) {
        if (query == null || query.isBlank()) return getAllComplaints();
        return complaintDAO.search(query);
    }

    // =====================================================================
    // Prototype Pattern — duplicate complaint
    // =====================================================================

    /**
     * Duplicates an existing complaint using the Prototype Pattern.
     */
    public Complaint duplicateComplaint(int originalId) {
        Complaint original = complaintDAO.findById(originalId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint #" + originalId + " not found"));
        return ComplaintCloner.cloneComplaint(original);
    }

    /**
     * Persists a duplicated (cloned) complaint with the reporter as owner.
     */
    public Complaint saveDuplicatedComplaint(Complaint copy, int reporterId) {
        copy.setReporterId(reporterId);
        return complaintDAO.save(copy);
    }

    // =====================================================================
    // Strategy Pattern — auto-assign staff
    // =====================================================================

    /**
     * Assigns a staff member to a complaint using the currently selected
     * {@link StaffAssignmentStrategy}.
     */
    public Optional<MaintenanceStaff> autoAssignStaff(Complaint complaint,
                                                      int changedByUserId) {
        List<MaintenanceStaff> allStaff = staffDAO.findAllWithLoadAndSkills();
        Optional<MaintenanceStaff> selected = assignmentStrategy.assign(complaint, allStaff);

        if (selected.isEmpty()) {
            log.warn("Strategy '{}' found no eligible staff for complaint #{}",
                     assignmentStrategy.getStrategyName(), complaint.getId());
            return Optional.empty();
        }

        MaintenanceStaff staff = selected.get();
        captureMemento(complaint, changedByUserId, "STAFF_ASSIGNMENT");
        complaint.setAssignedStaffId(staff.getId());
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaintDAO.update(complaint);
        persistHistory(complaint, changedByUserId, "STAFF_ASSIGNMENT");
        log.info("Complaint #{} assigned to staff #{} via '{}'",
                 complaint.getId(), staff.getId(), assignmentStrategy.getStrategyName());
        return selected;
    }

    /**
     * Manually assigns a specific staff member (admin override).
     */
    public void manualAssignStaff(Complaint complaint, int staffId, int changedByUserId) {
        captureMemento(complaint, changedByUserId, "STAFF_ASSIGNMENT");
        complaint.setAssignedStaffId(staffId);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaintDAO.update(complaint);
        persistHistory(complaint, changedByUserId, "STAFF_ASSIGNMENT");
    }

    // =====================================================================
    // Template Method Pattern — workflow execution
    // =====================================================================

    /**
     * Processes a complaint through the appropriate workflow based on its category.
     */
    public void resolveComplaint(Complaint complaint, String resolutionNotes,
                                  int changedByUserId) throws WorkflowException {
        captureMemento(complaint, changedByUserId, "RESOLVE");

        // Template Method Pattern — selects workflow based on category
        ComplaintWorkflow workflow = selectWorkflow(complaint.getCategory());
        workflow.processComplaint(complaint, resolutionNotes, changedByUserId);

        complaintDAO.update(complaint);
        persistHistory(complaint, changedByUserId, "RESOLVE");
    }

    private ComplaintWorkflow selectWorkflow(ComplaintCategory category) {
        return switch (category) {
            case NETWORK, IT_SUPPORT -> new ITSupportWorkflow();
            default -> new GeneralMaintenanceWorkflow();
        };
    }

    // =====================================================================
    // Status changes
    // =====================================================================

    public void updateStatus(Complaint complaint, ComplaintStatus newStatus,
                              int changedByUserId) {
        if (!complaint.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException("Invalid status transition from " +
                    complaint.getStatus() + " to " + newStatus);
        }
        captureMemento(complaint, changedByUserId, "STATUS_CHANGE");
        complaint.setStatus(newStatus);
        complaintDAO.update(complaint);
        persistHistory(complaint, changedByUserId, "STATUS_CHANGE");
    }

    public void updateDescription(Complaint complaint, String newDescription,
                                   int changedByUserId) {
        captureMemento(complaint, changedByUserId, "DESCRIPTION_CHANGE");
        complaint.setDescription(newDescription);
        complaintDAO.update(complaint);
        persistHistory(complaint, changedByUserId, "DESCRIPTION_CHANGE");
    }

    // =====================================================================
    // Memento Pattern — undo
    // =====================================================================

    private void captureMemento(Complaint complaint, int changedByUserId, String action) {
        ComplaintMemento memento = new ComplaintMemento(
                complaint.getId(),
                complaint.getStatus(),
                complaint.getAssignedStaffId(),
                complaint.getDescription(),
                complaint.getResolutionNotes(),
                changedByUserId,
                action);

        historyMap.computeIfAbsent(complaint.getId(), id -> new ComplaintHistory())
                  .push(memento);
    }

    /**
     * Undoes the last change to the given complaint.
     */
    public boolean undoLastChange(Complaint complaint, int changedByUserId) {
        ComplaintHistory history = historyMap.get(complaint.getId());
        if (history == null || !history.canUndo()) {
            List<ComplaintMemento> dbHistory = historyDAO.loadHistory(complaint.getId(), 5);
            if (!dbHistory.isEmpty()) {
                history = historyMap.computeIfAbsent(complaint.getId(), id -> new ComplaintHistory());
                for (int i = dbHistory.size() - 1; i >= 0; i--) {
                    history.push(dbHistory.get(i));
                }
            }
        }

        if (history == null || !history.canUndo()) return false;

        ComplaintMemento memento = history.pop();

        complaint.setStatus(memento.getStatus());
        complaint.setAssignedStaffId(memento.getAssignedStaffId());
        if (memento.getDescription() != null) {
            complaint.setDescription(memento.getDescription());
        }
        complaint.setResolutionNotes(memento.getResolutionNotes());

        complaintDAO.update(complaint);
        log.info("Undo performed on complaint #{}: restored from action '{}'",
                 complaint.getId(), memento.getActionLabel());
        return true;
    }

    public boolean canUndo(int complaintId) {
        ComplaintHistory history = historyMap.get(complaintId);
        if (history != null && history.canUndo()) return true;
        return !historyDAO.loadHistory(complaintId, 1).isEmpty();
    }

    // =====================================================================
    // Dashboard statistics
    // =====================================================================

    public Map<ComplaintStatus, Integer> getDashboardStatsForStudent(int reporterId) {
        return complaintDAO.countByStatusForReporter(reporterId);
    }

    public Map<ComplaintStatus, Integer> getDashboardStatsForAdmin() {
        return complaintDAO.countByStatus();
    }

    // =====================================================================
    // Staff retrieval
    // =====================================================================

    public List<MaintenanceStaff> getAllStaff() {
        return staffDAO.findAll();
    }

    public List<MaintenanceStaff> getAllStaffWithLoad() {
        return staffDAO.findAllWithLoadAndSkills();
    }

    // =====================================================================
    // Private helpers
    // =====================================================================

    private void persistHistory(Complaint complaint, int changedByUserId, String action) {
        String json = buildStateJson(complaint);
        ComplaintMemento snapshot = new ComplaintMemento(
                complaint.getId(), complaint.getStatus(),
                complaint.getAssignedStaffId(), complaint.getDescription(),
                complaint.getResolutionNotes(), changedByUserId, action);
        historyDAO.saveHistory(snapshot, null, json);
    }

    private String buildStateJson(Complaint c) {
        return String.format(
                "{\"status\":\"%s\",\"assigned_staff_id\":%s,\"description\":\"%s\",\"resolution_notes\":\"%s\"}",
                c.getStatus().name(),
                c.getAssignedStaffId() == null ? "null" : c.getAssignedStaffId(),
                c.getDescription() != null ? c.getDescription().replace("\"", "\\\"") : "",
                c.getResolutionNotes() != null ? c.getResolutionNotes().replace("\"", "\\\"") : "");
    }
}
