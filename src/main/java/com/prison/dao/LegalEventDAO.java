package com.prison.dao;

import com.prison.config.DBConnection;
import com.prison.model.LegalEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LegalEventDAO {

    public boolean addLegalEvent(LegalEvent event) {
        String sql = "INSERT INTO legal_events (prisoner_id, event_type, event_date, description) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, event.getPrisonerId());
            stmt.setString(2, event.getEventType());
            stmt.setDate(3, new java.sql.Date(event.getEventDate().getTime()));
            stmt.setString(4, event.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
                else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LegalEvent getLegalEvent(int id) {
        String sql = "SELECT * FROM legal_events WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LegalEvent event = new LegalEvent();
                    event.setId(rs.getInt("id"));
                    event.setPrisonerId(rs.getInt("prisoner_id"));
                    event.setEventType(rs.getString("event_type"));
                    event.setEventDate(rs.getDate("event_date"));
                    event.setDescription(rs.getString("description"));
                    return event;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<LegalEvent> getLegalEventsForPrisoner(int prisonerId) {
        List<LegalEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM legal_events WHERE prisoner_id = ? ORDER BY event_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, prisonerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LegalEvent event = new LegalEvent();
                    event.setId(rs.getInt("id"));
                    event.setPrisonerId(rs.getInt("prisoner_id"));
                    event.setEventType(rs.getString("event_type"));
                    event.setEventDate(rs.getDate("event_date"));
                    event.setDescription(rs.getString("description"));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public List<LegalEvent> getAllLegalEvents() {
        List<LegalEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM legal_events ORDER BY event_date";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LegalEvent event = new LegalEvent();
                event.setId(rs.getInt("id"));
                event.setPrisonerId(rs.getInt("prisoner_id"));
                event.setEventType(rs.getString("event_type"));
                event.setEventDate(rs.getDate("event_date"));
                event.setDescription(rs.getString("description"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public List<LegalEvent> getLegalEventsBetweenDates(Date startDate, Date endDate) {
        List<LegalEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM legal_events WHERE event_date BETWEEN ? AND ? ORDER BY event_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LegalEvent event = new LegalEvent();
                    event.setId(rs.getInt("id"));
                    event.setPrisonerId(rs.getInt("prisoner_id"));
                    event.setEventType(rs.getString("event_type"));
                    event.setEventDate(rs.getDate("event_date"));
                    event.setDescription(rs.getString("description"));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public boolean updateLegalEvent(LegalEvent event) {
        String sql = "UPDATE legal_events SET prisoner_id = ?, event_type = ?, " +
                "event_date = ?, description = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, event.getPrisonerId());
            stmt.setString(2, event.getEventType());
            stmt.setDate(3, new java.sql.Date(event.getEventDate().getTime()));
            stmt.setString(4, event.getDescription());
            stmt.setInt(5, event.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLegalEvent(int id) {
        String sql = "DELETE FROM legal_events WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalLegalEvents() {
        String sql = "SELECT COUNT(*) FROM legal_events";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalLegalEventsForPrisoner(int prisonerId) {
        String sql = "SELECT COUNT(*) FROM legal_events WHERE prisoner_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, prisonerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}