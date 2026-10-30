package com.campus.complaints.fake;

import com.campus.complaints.dao.ComplaintDAO;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.ComplaintStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory test double for {@link ComplaintDAO}.
 */
public class FakeComplaintDAO implements ComplaintDAO {

    private final Map<Integer, Complaint> storage = new LinkedHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Complaint save(Complaint complaint) {
        if (complaint.getId() <= 0) {
            complaint.setId(idGenerator.getAndIncrement());
        }
        storage.put(complaint.getId(), complaint);
        return complaint;
    }

    @Override
    public Optional<Complaint> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Complaint> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Complaint> findByReporterId(int reporterId) {
        return storage.values().stream()
                .filter(c -> c.getReporterId() == reporterId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        return storage.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByCategory(ComplaintCategory category) {
        return storage.values().stream()
                .filter(c -> c.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByAssignedStaffId(int staffId) {
        return storage.values().stream()
                .filter(c -> c.getAssignedStaffId() != null && c.getAssignedStaffId() == staffId)
                .collect(Collectors.toList());
    }

    @Override
    public int countActiveByStaffId(int staffId) {
        return (int) storage.values().stream()
                .filter(c -> c.getAssignedStaffId() != null && c.getAssignedStaffId() == staffId)
                .filter(c -> c.getStatus() != ComplaintStatus.RESOLVED && c.getStatus() != ComplaintStatus.CANCELLED)
                .count();
    }

    @Override
    public boolean update(Complaint complaint) {
        if (!storage.containsKey(complaint.getId())) return false;
        storage.put(complaint.getId(), complaint);
        return true;
    }

    @Override
    public Map<ComplaintStatus, Integer> countByStatusForReporter(int reporterId) {
        Map<ComplaintStatus, Integer> counts = new EnumMap<>(ComplaintStatus.class);
        for (ComplaintStatus s : ComplaintStatus.values()) counts.put(s, 0);
        storage.values().stream()
                .filter(c -> c.getReporterId() == reporterId)
                .forEach(c -> counts.put(c.getStatus(), counts.get(c.getStatus()) + 1));
        return counts;
    }

    @Override
    public Map<ComplaintStatus, Integer> countByStatus() {
        Map<ComplaintStatus, Integer> counts = new EnumMap<>(ComplaintStatus.class);
        for (ComplaintStatus s : ComplaintStatus.values()) counts.put(s, 0);
        storage.values().forEach(c -> counts.put(c.getStatus(), counts.get(c.getStatus()) + 1));
        return counts;
    }

    @Override
    public List<Complaint> search(String query) {
        String lower = query.toLowerCase();
        return storage.values().stream()
                .filter(c -> c.getTitle().toLowerCase().contains(lower) || String.valueOf(c.getId()).contains(lower))
                .collect(Collectors.toList());
    }
}
