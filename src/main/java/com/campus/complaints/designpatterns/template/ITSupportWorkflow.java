package com.campus.complaints.designpatterns.template;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;

/**
 * <b>GoF Template Method Pattern</b> — Concrete Workflow Implementation.
 *
 * <p>Specialized workflow for network, Wi-Fi, and IT support complaints.
 * Customizes validation to verify IT categorization and enforces detailed technical diagnostics notes.
 */
public class ITSupportWorkflow extends ComplaintWorkflow {

    @Override
    public String getWorkflowName() {
        return "IT Support Workflow";
    }

    @Override
    protected void validateComplaint(Complaint complaint) throws WorkflowException {
        super.validateComplaint(complaint);
        if (complaint.getCategory() != ComplaintCategory.NETWORK &&
            complaint.getCategory() != ComplaintCategory.IT_SUPPORT) {
            log.warn("ITSupportWorkflow applied to non-IT complaint #{}: {}", complaint.getId(), complaint.getCategory());
        }
    }

    @Override
    protected void executeResolution(Complaint complaint, String resolutionNotes) throws WorkflowException {
        if (resolutionNotes == null || resolutionNotes.trim().isBlank()) {
            throw new WorkflowException(
                    "IT support resolution requires technical details (e.g. device rebooted, IP reconfigured, patch cord replaced).");
        }
        log.info("IT technical resolution logged for complaint #{}: {}",
                complaint.getId(), resolutionNotes.substring(0, Math.min(80, resolutionNotes.length())));
    }

    @Override
    protected void recordAndNotify(Complaint complaint, int changedByUserId) {
        log.info("[ITSupport] Complaint #{} (Category={}) resolved and logged to IT audit. Notified user #{}",
                complaint.getId(), complaint.getCategory(), complaint.getReporterId());
    }
}
