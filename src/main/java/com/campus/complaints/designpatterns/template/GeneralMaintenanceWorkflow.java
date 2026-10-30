package com.campus.complaints.designpatterns.template;

import com.campus.complaints.model.Complaint;

/**
 * <b>GoF Template Method Pattern</b> — Concrete Workflow Implementation.
 *
 * <p>Specialized workflow for electrical, plumbing, sanitation, and physical equipment issues.
 * Enforces that technicians document the physical work performed before marking as resolved.
 */
public class GeneralMaintenanceWorkflow extends ComplaintWorkflow {

    @Override
    public String getWorkflowName() {
        return "General Maintenance Workflow";
    }

    @Override
    protected void executeResolution(Complaint complaint, String resolutionNotes) throws WorkflowException {
        if (resolutionNotes == null || resolutionNotes.trim().isBlank()) {
            throw new WorkflowException(
                    "Resolution notes are required for general maintenance tasks. " +
                    "Please describe the physical work performed (e.g., parts replaced, repairs completed).");
        }
        log.info("Physical maintenance resolution verified for complaint #{}: {}",
                complaint.getId(), resolutionNotes.substring(0, Math.min(60, resolutionNotes.length())));
    }

    @Override
    protected void recordAndNotify(Complaint complaint, int changedByUserId) {
        log.info("[GeneralMaintenance] Complaint #{} physically resolved. Notified reporter #{}",
                complaint.getId(), complaint.getReporterId());
    }
}
