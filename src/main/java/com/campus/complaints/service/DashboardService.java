package com.campus.complaints.service;

import com.campus.complaints.dao.ComplaintDAO;
import com.campus.complaints.dao.MaintenanceStaffDAO;
import com.campus.complaints.dao.impl.ComplaintDAOImpl;
import com.campus.complaints.dao.impl.MaintenanceStaffDAOImpl;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintStatus;
import com.campus.complaints.model.MaintenanceStaff;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for aggregating dashboard metrics, KPIs, and summary cards
 * for both Student and Administrator views.
 */
public class DashboardService {

    private final ComplaintDAO complaintDAO;
    private final MaintenanceStaffDAO staffDAO;

    public DashboardService() {
        this.complaintDAO = new ComplaintDAOImpl();
        this.staffDAO = new MaintenanceStaffDAOImpl();
    }

    public DashboardService(ComplaintDAO complaintDAO, MaintenanceStaffDAO staffDAO) {
        this.complaintDAO = complaintDAO;
        this.staffDAO = staffDAO;
    }

    /**
     * Aggregates complaint status statistics for a specific student.
     */
    public Map<ComplaintStatus, Integer> getStudentStats(int studentUserId) {
        return complaintDAO.countByStatusForReporter(studentUserId);
    }

    /**
     * Aggregates global complaint status statistics for campus administrators.
     */
    public Map<ComplaintStatus, Integer> getAdminStats() {
        return complaintDAO.countByStatus();
    }

    /**
     * Returns total count of registered maintenance staff.
     */
    public int getTotalStaffCount() {
        return staffDAO.findAll().size();
    }

    /**
     * Returns count of currently available maintenance staff.
     */
    public long getAvailableStaffCount() {
        return staffDAO.findAll().stream()
                .filter(MaintenanceStaff::isAvailable)
                .count();
    }

    /**
     * Returns recent complaints for dashboard previews.
     */
    public List<Complaint> getRecentComplaints(int limit) {
        List<Complaint> all = complaintDAO.findAll();
        if (all.size() <= limit) return all;
        return all.subList(0, limit);
    }

    /**
     * Returns recent complaints reported by a specific student.
     */
    public List<Complaint> getRecentComplaintsForStudent(int studentUserId, int limit) {
        List<Complaint> studentComplaints = complaintDAO.findByReporterId(studentUserId);
        if (studentComplaints.size() <= limit) return studentComplaints;
        return studentComplaints.subList(0, limit);
    }
}
