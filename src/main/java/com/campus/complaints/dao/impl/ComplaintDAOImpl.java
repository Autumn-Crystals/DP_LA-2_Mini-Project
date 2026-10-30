package com.campus.complaints.dao.impl;

import com.campus.complaints.config.DatabaseConfiguration;
import com.campus.complaints.dao.ComplaintDAO;
import com.campus.complaints.designpatterns.builder.ComplaintBuilder;
import com.campus.complaints.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * JDBC implementation of {@link ComplaintDAO}.
 * Uses prepared statements throughout to prevent SQL injection.
 */
public class ComplaintDAOImpl implements ComplaintDAO {

    private static final Logger log = LoggerFactory.getLogger(ComplaintDAOImpl.class);

    @Override
    public Complaint save(Complaint c) {
        String sql = "INSERT INTO complaints " +
                     "(title, description, category, location, priority, status, " +
                     " reporter_id, assigned_staff_id, resolved_at, resolution_notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setComplaintParams(ps, c);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            log.error("save complaint failed: {}", e.getMessage());
            throw new RuntimeException("Failed to save complaint: " + e.getMessage(), e);
        }
        return c;
    }

    @Override
    public Optional<Complaint> findById(int id) {
        String sql = buildSelectBase() + " WHERE c.id = ?";
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("findById({}) failed: {}", id, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Complaint> findAll() {
        return queryList(buildSelectBase() + " ORDER BY c.created_at DESC");
    }

    @Override
    public List<Complaint> findByReporterId(int reporterId) {
        String sql = buildSelectBase() + " WHERE c.reporter_id = ? ORDER BY c.created_at DESC";
        return queryListWithInt(sql, reporterId);
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        String sql = buildSelectBase() + " WHERE c.status = ? ORDER BY c.created_at DESC";
        return queryListWithString(sql, status.name());
    }

    @Override
    public List<Complaint> findByCategory(ComplaintCategory category) {
        String sql = buildSelectBase() + " WHERE c.category = ? ORDER BY c.created_at DESC";
        return queryListWithString(sql, category.name());
    }

    @Override
    public List<Complaint> findByAssignedStaffId(int staffId) {
        String sql = buildSelectBase() + " WHERE c.assigned_staff_id = ? ORDER BY c.created_at DESC";
        return queryListWithInt(sql, staffId);
    }

    @Override
    public int countActiveByStaffId(int staffId) {
        String sql = "SELECT COUNT(*) FROM complaints " +
                     "WHERE assigned_staff_id = ? AND status NOT IN ('RESOLVED','CANCELLED')";
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("countActiveByStaffId({}) failed: {}", staffId, e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean update(Complaint c) {
        String sql = "UPDATE complaints SET title=?, description=?, category=?, location=?, " +
                     "priority=?, status=?, assigned_staff_id=?, resolved_at=?, " +
                     "resolution_notes=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getCategory().name());
            ps.setString(4, c.getLocation());
            ps.setString(5, c.getPriority().name());
            ps.setString(6, c.getStatus().name());
            if (c.getAssignedStaffId() != null) {
                ps.setInt(7, c.getAssignedStaffId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            if (c.getResolvedAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(c.getResolvedAt()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }
            ps.setString(9, c.getResolutionNotes());
            ps.setInt(10, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("update complaint #{} failed: {}", c.getId(), e.getMessage());
            throw new RuntimeException("Failed to update complaint: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<ComplaintStatus, Integer> countByStatusForReporter(int reporterId) {
        String sql = "SELECT status, COUNT(*) as cnt FROM complaints " +
                     "WHERE reporter_id = ? GROUP BY status";
        return countByStatusQuery(sql, reporterId);
    }

    @Override
    public Map<ComplaintStatus, Integer> countByStatus() {
        String sql = "SELECT status, COUNT(*) as cnt FROM complaints GROUP BY status";
        return countByStatusQuery(sql, -1);
    }

    @Override
    public List<Complaint> search(String query) {
        String sql = buildSelectBase() +
                     " WHERE c.title LIKE ? OR CAST(c.id AS CHAR) = ? " +
                     " ORDER BY c.created_at DESC LIMIT 100";
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, query.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            log.error("search('{}') failed: {}", query, e.getMessage());
        }
        return Collections.emptyList();
    }

    // ---- Private helpers ----

    private String buildSelectBase() {
        return "SELECT c.id, c.title, c.description, c.category, c.location, " +
               "       c.priority, c.status, c.reporter_id, c.assigned_staff_id, " +
               "       c.created_at, c.updated_at, c.resolved_at, c.resolution_notes " +
               "FROM complaints c";
    }

    private void setComplaintParams(PreparedStatement ps, Complaint c) throws SQLException {
        ps.setString(1, c.getTitle());
        ps.setString(2, c.getDescription());
        ps.setString(3, c.getCategory().name());
        ps.setString(4, c.getLocation());
        ps.setString(5, c.getPriority().name());
        ps.setString(6, c.getStatus().name());
        ps.setInt(7, c.getReporterId());
        if (c.getAssignedStaffId() != null) {
            ps.setInt(8, c.getAssignedStaffId());
        } else {
            ps.setNull(8, Types.INTEGER);
        }
        if (c.getResolvedAt() != null) {
            ps.setTimestamp(9, Timestamp.valueOf(c.getResolvedAt()));
        } else {
            ps.setNull(9, Types.TIMESTAMP);
        }
        ps.setString(10, c.getResolutionNotes());
    }

    private Complaint mapRow(ResultSet rs) throws SQLException {
        ComplaintBuilder b = new ComplaintBuilder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .category(ComplaintCategory.valueOf(rs.getString("category")))
                .location(rs.getString("location"))
                .priority(Priority.valueOf(rs.getString("priority")))
                .status(ComplaintStatus.valueOf(rs.getString("status")))
                .reporterId(rs.getInt("reporter_id"));

        int staffId = rs.getInt("assigned_staff_id");
        if (!rs.wasNull()) b.assignedStaffId(staffId);

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) b.createdAt(created.toLocalDateTime());

        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) b.updatedAt(updated.toLocalDateTime());

        Timestamp resolved = rs.getTimestamp("resolved_at");
        if (resolved != null) b.resolvedAt(resolved.toLocalDateTime());

        b.resolutionNotes(rs.getString("resolution_notes"));

        return b.build();
    }

    private List<Complaint> queryList(String sql) {
        try (Connection conn = DatabaseConfiguration.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return mapResultSet(rs);
        } catch (SQLException e) {
            log.error("queryList failed: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<Complaint> queryListWithInt(String sql, int param) {
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            log.error("queryListWithInt failed: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<Complaint> queryListWithString(String sql, String param) {
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            log.error("queryListWithString failed: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<Complaint> mapResultSet(ResultSet rs) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private Map<ComplaintStatus, Integer> countByStatusQuery(String sql, int reporterId) {
        Map<ComplaintStatus, Integer> counts = new EnumMap<>(ComplaintStatus.class);
        for (ComplaintStatus s : ComplaintStatus.values()) counts.put(s, 0);

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (reporterId >= 0) ps.setInt(1, reporterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        ComplaintStatus status = ComplaintStatus.valueOf(rs.getString("status"));
                        counts.put(status, rs.getInt("cnt"));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } catch (SQLException e) {
            log.error("countByStatus query failed: {}", e.getMessage());
        }
        return counts;
    }
}
