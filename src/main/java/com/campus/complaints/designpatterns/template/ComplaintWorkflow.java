package com.campus.complaints.designpatterns.template;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * <b>GoF Template Method Pattern</b> — Abstract Class.
 *
 * <p>Defines the invariant skeleton algorithm for processing and resolving campus complaints.
 * The core template method {@link #processComplaint(Complaint, String, int)} is declared
 * {@code final} so subclasses cannot alter the sequence of execution. Subclasses override
 * hook methods to provide category-specific validation and logging logic.
 */
public abstract class ComplaintWorkflow {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Template Method — defines and enforces the invariant sequence of steps.
     * Declared final to prevent subclasses from altering the workflow structure.
     *
     * @param complaint       the complaint to process
     * @param resolutionNotes notes detailing the corrective action taken
     * @param changedByUserId user ID of the administrator performing the resolution
     * @throws WorkflowException if any step fails validation
     */
    public final void processComplaint(Complaint complaint,
                                       String resolutionNotes,
                                       int changedByUserId) throws WorkflowException {
        log.info("Starting {} for complaint #{}", getWorkflowName(), complaint.getId());

        // Step 1: Validate complaint readiness
        validateComplaint(complaint);

        // Step 2: Confirm assigned technician exists
        confirmAssignment(complaint);

        // Step 3: Transition status to IN_PROGRESS
        setInProgress(complaint);

        // Step 4: Subclass-specific resolution hook
        executeResolution(complaint, resolutionNotes);

        // Step 5: Mark complaint as RESOLVED
        markResolved(complaint, resolutionNotes);

        // Step 6: Post-resolution notifications & audit hook
        recordAndNotify(complaint, changedByUserId);

        log.info("Successfully completed {} for complaint #{}", getWorkflowName(), complaint.getId());
    }

    /** Returns the display name of this workflow. */
    public abstract String getWorkflowName();

    /**
     * Step 1 — Validate complaint is eligible for processing.
     */
    protected void validateComplaint(Complaint complaint) throws WorkflowException {
        if (complaint == null) {
            throw new WorkflowException("Complaint cannot be null.");
        }
        if (complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw new WorkflowException("Complaint #" + complaint.getId() + " is already resolved.");
        }
        if (complaint.getStatus() == ComplaintStatus.CANCELLED) {
            throw new WorkflowException("Complaint #" + complaint.getId() + " has been cancelled.");
        }
    }

    /**
     * Step 2 — Verify technician assignment.
     */
    protected void confirmAssignment(Complaint complaint) throws WorkflowException {
        if (complaint.getAssignedStaffId() == null) {
            throw new WorkflowException(
                    "No staff assigned to complaint #" + complaint.getId() +
                    ". Please assign a maintenance technician before resolving.");
        }
    }

    /**
     * Step 3 — Move status to IN_PROGRESS.
     */
    protected void setInProgress(Complaint complaint) throws WorkflowException {
        if (complaint.getStatus() != ComplaintStatus.IN_PROGRESS) {
            if (!complaint.getStatus().canTransitionTo(ComplaintStatus.IN_PROGRESS)) {
                throw new WorkflowException("Cannot transition complaint #" + complaint.getId() +
                        " from " + complaint.getStatus() + " to IN_PROGRESS.");
            }
            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        }
    }

    /**
     * Step 4 (Hook Method) — Subclass-specific resolution checks.
     */
    protected abstract void executeResolution(Complaint complaint, String resolutionNotes)
            throws WorkflowException;

    /**
     * Step 5 — Mark complaint as RESOLVED and update timestamps.
     */
    protected void markResolved(Complaint complaint, String resolutionNotes) throws WorkflowException {
        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolutionNotes(resolutionNotes != null ? resolutionNotes.trim() : "");
        LocalDateTime now = LocalDateTime.now();
        complaint.setResolvedAt(now);
        complaint.setUpdatedAt(now);
    }

    /**
     * Step 6 (Hook Method) — Post-resolution notification and recording.
     */
    protected void recordAndNotify(Complaint complaint, int changedByUserId) {
        log.info("Complaint #{} resolved by user #{}", complaint.getId(), changedByUserId);
    }
}
