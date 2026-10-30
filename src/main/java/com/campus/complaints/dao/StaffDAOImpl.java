package com.campus.complaints.dao;

import com.campus.complaints.config.DatabaseConfig;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.MaintenanceStaff.AvailabilityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * JDBC implementation of {@link StaffDAO}.
 */
public class StaffDAOImpl implements StaffDAO {

    private static final Logger log = LoggerFactory.getLogger(StaffDAOImpl.class);

    @Override
    public Optional<MaintenanceStaff> findById(int id) {
        String sql = "SELECT id, user_id, name, email, phone, availability_status, created_at " +
                     "FROM maintenance_staff WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MaintenanceStaff staff = mapRow(rs);
                    loadSkills(conn, staff);
                    return Optional.of(staff);
                }
            }
        } catch (SQLException e) {
            log.error("findById({}) failed: {}", id, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<MaintenanceStaff> findAll() {
        String sql = "SELECT id, user_id, name, email, phone, availability_status, created_at " +
                     "FROM maintenance_staff ORDER BY name";
        List<MaintenanceStaff> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                MaintenanceStaff staff = mapRow(rs);
                loadSkills(conn, staff);
                list.add(staff);
            }
        } catch (SQLException e) {
            log.error("findAll() failed: {}", e.getMessage());
        }
        return list;
    }

    @Override
    public List<MaintenanceStaff> findAllWithLoadAndSkills() {
        String sql = "SELECT ms.id, ms.user_id, ms.name, ms.email, ms.phone, " +
                     "       ms.availability_status, ms.created_at, " +
                     "       COUNT(c.id) AS active_count " +
                     "FROM maintenance_staff ms " +
                     "LEFT JOIN complaints c ON c.assigned_staff_id = ms.id " +
                     "    AND c.status NOT IN ('RESOLVED','CANCELLED') " +
                     "GROUP BY ms.id, ms.user_id, ms.name, ms.email, ms.phone, " +
                     "         ms.availability_status, ms.created_at " +
                     "ORDER BY ms.name";
        List<MaintenanceStaff> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                MaintenanceStaff staff = mapRow(rs);
                staff.setActiveComplaintCount(rs.getInt("active_count"));
                loadSkills(conn, staff);
                list.add(staff);
            }
        } catch (SQLException e) {
            log.error("findAllWithLoadAndSkills() failed: {}", e.getMessage());
        }
        return list;
    }

    @Override
    public MaintenanceStaff save(MaintenanceStaff staff) {
        String sql = "INSERT INTO maintenance_staff (user_id, name, email, phone, availability_status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, staff.getUserId());
            ps.setString(2, staff.getName());
            ps.setString(3, staff.getEmail());
            ps.setString(4, staff.getPhone());
            ps.setString(5, staff.getAvailabilityStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) staff.setId(keys.getInt(1));
            }
            if (staff.getSkills() != null && !staff.getSkills().isEmpty()) {
                updateSkills(staff.getId(), staff.getSkills());
            }
        } catch (SQLException e) {
            log.error("save staff failed: {}", e.getMessage());
            throw new RuntimeException("Failed to save staff: " + e.getMessage(), e);
        }
        return staff;
    }

    @Override
    public boolean update(MaintenanceStaff staff) {
        String sql = "UPDATE maintenance_staff SET name=?, email=?, phone=?, " +
                     "availability_status=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staff.getName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getAvailabilityStatus().name());
            ps.setInt(5, staff.getId());
            int updated = ps.executeUpdate();
            if (updated > 0 && staff.getSkills() != null) {
                updateSkills(staff.getId(), staff.getSkills());
            }
            return updated > 0;
        } catch (SQLException e) {
            log.error("update staff #{} failed: {}", staff.getId(), e.getMessage());
            throw new RuntimeException("Failed to update staff: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateSkills(int staffId, List<ComplaintCategory> skills) {
        String deleteSql = "DELETE FROM staff_skills WHERE staff_id = ?";
        String insertSql = "INSERT INTO staff_skills (staff_id, category) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement del = conn.prepareStatement(deleteSql);
                 PreparedStatement ins = conn.prepareStatement(insertSql)) {
                del.setInt(1, staffId);
                del.executeUpdate();
                for (ComplaintCategory cat : skills) {
                    ins.setInt(1, staffId);
                    ins.setString(2, cat.name());
                    ins.addBatch();
                }
                ins.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("updateSkills for staff #{} failed: {}", staffId, e.getMessage());
            throw new RuntimeException("Failed to update staff skills: " + e.getMessage(), e);
        }
    }

    private void loadSkills(Connection conn, MaintenanceStaff staff) throws SQLException {
        String sql = "SELECT category FROM staff_skills WHERE staff_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staff.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        staff.addSkill(ComplaintCategory.valueOf(rs.getString("category")));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }
    }

    private MaintenanceStaff mapRow(ResultSet rs) throws SQLException {
        MaintenanceStaff staff = new MaintenanceStaff();
        staff.setId(rs.getInt("id"));
        staff.setUserId(rs.getInt("user_id"));
        staff.setName(rs.getString("name"));
        staff.setEmail(rs.getString("email"));
        staff.setPhone(rs.getString("phone"));
        String statusStr = rs.getString("availability_status");
        try {
            staff.setAvailabilityStatus(AvailabilityStatus.valueOf(statusStr));
        } catch (IllegalArgumentException e) {
            staff.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        }
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) staff.setCreatedAt(ts.toLocalDateTime());
        return staff;
    }
}
