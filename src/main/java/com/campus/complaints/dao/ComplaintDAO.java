package com.campus.complaints.dao;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.Priority;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for {@link Complaint} persistence operations.
 */
public interface ComplaintDAO {

    Complaint save(Complaint complaint);

    Optional<Complaint> findById(int id);

    List<Complaint> findAll();

    List<Complaint> findByReporterId(int reporterId);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByCategory(ComplaintCategory category);

    List<Complaint> findByAssignedStaffId(int staffId);

    /** Returns active (non-resolved, non-cancelled) count for a staff member. */
    int countActiveByStaffId(int staffId);

    /** Updates mutable fields: status, assignedStaffId, description, resolutionNotes, resolvedAt. */
    boolean update(Complaint complaint);

    /** Counts complaints per status for the given reporter (student dashboard). */
    java.util.Map<ComplaintStatus, Integer> countByStatusForReporter(int reporterId);

    /** Counts all complaints per status (admin dashboard). */
    java.util.Map<ComplaintStatus, Integer> countByStatus();

    /** Search complaints by title or ID substring. */
    List<Complaint> search(String query);
}
