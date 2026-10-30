package com.campus.complaints.dao;

import com.campus.complaints.config.DatabaseConfiguration;
import com.campus.complaints.designpatterns.memento.ComplaintMemento;
import com.campus.complaints.model.ComplaintStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for reading and persisting complaint history records.
 * Supports the database-backed Memento history that survives restarts.
 */
public class ComplaintHistoryDAO {

    private static final Logger log = LoggerFactory.getLogger(ComplaintHistoryDAO.class);

    /**
     * Persists a memento snapshot to the complaint_history table.
     */
    public void saveHistory(ComplaintMemento memento, String previousStateJson, String newStateJson) {
        String sql = "INSERT INTO complaint_history " +
                     "(complaint_id, changed_by, action, previous_state, new_state) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memento.getComplaintId());
            ps.setInt(2, memento.getChangedByUserId());
            ps.setString(3, memento.getActionLabel());
            ps.setString(4, previousStateJson);
            ps.setString(5, newStateJson);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("saveHistory failed for complaint #{}: {}", memento.getComplaintId(), e.getMessage());
        }
    }

    /**
     * Loads the most recent N history entries for a complaint, newest first.
     * Used to reconstruct the undo stack on application startup.
     */
    public List<ComplaintMemento> loadHistory(int complaintId, int limit) {
        String sql = "SELECT complaint_id, changed_by, action, previous_state, created_at " +
                     "FROM complaint_history WHERE complaint_id = ? " +
                     "ORDER BY created_at DESC LIMIT ?";
        List<ComplaintMemento> mementos = new ArrayList<>();
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String prevJson = rs.getString("previous_state");
                    ComplaintMemento m = parseMementoFromJson(
                            rs.getInt("complaint_id"),
                            rs.getInt("changed_by"),
                            rs.getString("action"),
                            prevJson);
                    if (m != null) mementos.add(m);
                }
            }
        } catch (SQLException e) {
            log.error("loadHistory for complaint #{} failed: {}", complaintId, e.getMessage());
        }
        return mementos;
    }

    /**
     * Simple JSON parser for complaint state snapshots stored in the DB.
     * Avoids a JSON library dependency by parsing the simple key-value JSON manually.
     */
    private ComplaintMemento parseMementoFromJson(int complaintId, int changedBy,
                                                   String action, String json) {
        if (json == null || json.isBlank()) return null;
        try {
            ComplaintStatus status = null;
            Integer staffId = null;
            String description = null;
            String resolutionNotes = null;

            // Parse "status":"VALUE"
            status = extractEnum(json, "status", ComplaintStatus.class);
            // Parse "assigned_staff_id":123 or null
            staffId = extractInteger(json, "assigned_staff_id");
            // Parse "description":"..."
            description = extractString(json, "description");
            resolutionNotes = extractString(json, "resolution_notes");

            if (status == null) return null; // malformed

            return new ComplaintMemento(complaintId, status, staffId,
                                        description, resolutionNotes, changedBy, action);
        } catch (Exception e) {
            log.warn("parseMementoFromJson failed: {}", e.getMessage());
            return null;
        }
    }

    private <E extends Enum<E>> E extractEnum(String json, String key, Class<E> enumClass) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        try {
            return Enum.valueOf(enumClass, json.substring(start, end));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Integer extractInteger(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        start += pattern.length();
        // skip whitespace
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (start >= json.length()) return null;
        if (json.startsWith("null", start)) return null;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)))) end++;
        if (end == start) return null;
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }
}
