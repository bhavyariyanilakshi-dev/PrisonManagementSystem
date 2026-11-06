package com.prison.dao;

import com.prison.config.DBConnection;
import com.prison.model.Prisoner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrisonerDAO {

    public List<Prisoner> getAllPrisoners() {
        List<Prisoner> prisoners = new ArrayList<>();
        String sql = "SELECT * FROM prisoners";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Prisoner prisoner = extractPrisonerFromResultSet(rs);
                prisoners.add(prisoner);
            }
            System.out.println("Retrieved " + prisoners.size() + " prisoners from database");
        } catch (SQLException e) {
            System.err.println("Error retrieving prisoners: " + e.getMessage());
            e.printStackTrace();
        }

        return prisoners;
    }

    public Prisoner getPrisoner(int id) {
        String sql = "SELECT * FROM prisoners WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPrisonerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving prisoner with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Prisoner> searchPrisoners(String searchTerm) {
        List<Prisoner> prisoners = new ArrayList<>();
        String sql = "SELECT * FROM prisoners WHERE first_name LIKE ? OR last_name LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String term = "%" + searchTerm + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prisoner prisoner = extractPrisonerFromResultSet(rs);
                    prisoners.add(prisoner);
                }
            }
            System.out.println("Found " + prisoners.size() + " prisoners matching '" + searchTerm + "'");
        } catch (SQLException e) {
            System.err.println("Error searching prisoners: " + e.getMessage());
            e.printStackTrace();
        }

        return prisoners;
    }

    public List<Prisoner> getPrisonersByCell(int cellId) {
        List<Prisoner> prisoners = new ArrayList<>();
        String sql = "SELECT * FROM prisoners WHERE cell_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cellId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prisoner prisoner = extractPrisonerFromResultSet(rs);
                    prisoners.add(prisoner);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving prisoners by cell: " + e.getMessage());
            e.printStackTrace();
        }

        return prisoners;
    }

    public List<Prisoner> getPrisonersByCrimeType(String crimeType) {
        List<Prisoner> prisoners = new ArrayList<>();
        String sql = "SELECT * FROM prisoners WHERE crime_type LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + crimeType + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prisoner prisoner = extractPrisonerFromResultSet(rs);
                    prisoners.add(prisoner);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving prisoners by crime type: " + e.getMessage());
            e.printStackTrace();
        }

        return prisoners;
    }

    public int getPrisonersReleasingByDate(Date releaseDate) {
        String sql = "SELECT COUNT(*) FROM prisoners WHERE sentence_end <= ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(releaseDate.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting prisoners by release date: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public boolean addPrisoner(Prisoner prisoner) {
        String sql = "INSERT INTO prisoners (first_name, last_name, dob, gender, crime_type, " +
                "sentence_start, sentence_end, cell_id, health_status, behavior_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, prisoner.getFirstName());
            stmt.setString(2, prisoner.getLastName());
            stmt.setDate(3, new java.sql.Date(prisoner.getDob().getTime()));
            stmt.setString(4, prisoner.getGender());
            stmt.setString(5, prisoner.getCrimeType());
            stmt.setDate(6, new java.sql.Date(prisoner.getSentenceStart().getTime()));
            stmt.setDate(7, new java.sql.Date(prisoner.getSentenceEnd().getTime()));
            stmt.setInt(8, prisoner.getCellId());
            stmt.setString(9, prisoner.getHealthStatus());
            stmt.setString(10, prisoner.getBehaviorStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        prisoner.setId(generatedKeys.getInt(1));

                        // Update cell occupancy
                        updateCellOccupancy(conn, prisoner.getCellId(), 1);

                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding prisoner: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePrisoner(Prisoner prisoner) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get the current cell ID before updating
            int oldCellId = -1;
            String getCellSql = "SELECT cell_id FROM prisoners WHERE id = ?";
            try (PreparedStatement getStmt = conn.prepareStatement(getCellSql)) {
                getStmt.setInt(1, prisoner.getId());
                try (ResultSet rs = getStmt.executeQuery()) {
                    if (rs.next()) {
                        oldCellId = rs.getInt("cell_id");
                    }
                }
            }

            // Update prisoner record
            String sql = "UPDATE prisoners SET first_name = ?, last_name = ?, dob = ?, gender = ?, " +
                    "crime_type = ?, sentence_start = ?, sentence_end = ?, cell_id = ?, " +
                    "health_status = ?, behavior_status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, prisoner.getFirstName());
            stmt.setString(2, prisoner.getLastName());
            stmt.setDate(3, new java.sql.Date(prisoner.getDob().getTime()));
            stmt.setString(4, prisoner.getGender());
            stmt.setString(5, prisoner.getCrimeType());
            stmt.setDate(6, new java.sql.Date(prisoner.getSentenceStart().getTime()));
            stmt.setDate(7, new java.sql.Date(prisoner.getSentenceEnd().getTime()));
            stmt.setInt(8, prisoner.getCellId());
            stmt.setString(9, prisoner.getHealthStatus());
            stmt.setString(10, prisoner.getBehaviorStatus());
            stmt.setInt(11, prisoner.getId());

            int affectedRows = stmt.executeUpdate();

            // Update cell occupancy if cell has changed
            if (affectedRows > 0 && oldCellId != prisoner.getCellId()) {
                if (oldCellId > 0) {
                    updateCellOccupancy(conn, oldCellId, -1);
                }
                updateCellOccupancy(conn, prisoner.getCellId(), 1);
            }

            conn.commit();
            return affectedRows > 0;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error updating prisoner: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updatePrisonerCell(int prisonerId, int newCellId, int oldCellId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Update prisoner's cell ID
            String sql = "UPDATE prisoners SET cell_id = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newCellId);
            stmt.setInt(2, prisonerId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Update the occupancy of both cells
                if (oldCellId > 0) {
                    updateCellOccupancy(conn, oldCellId, -1);
                }
                updateCellOccupancy(conn, newCellId, 1);
            }

            conn.commit();
            return affectedRows > 0;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error updating prisoner cell: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateHealthStatus(int prisonerId, String newStatus) {
        String sql = "UPDATE prisoners SET health_status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, prisonerId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating prisoner health status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBehaviorStatus(int prisonerId, String newStatus) {
        String sql = "UPDATE prisoners SET behavior_status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, prisonerId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating prisoner behavior status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePrisoner(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get the current cell ID before deleting
            int cellId = -1;
            String getCellSql = "SELECT cell_id FROM prisoners WHERE id = ?";
            try (PreparedStatement getStmt = conn.prepareStatement(getCellSql)) {
                getStmt.setInt(1, id);
                try (ResultSet rs = getStmt.executeQuery()) {
                    if (rs.next()) {
                        cellId = rs.getInt("cell_id");
                    }
                }
            }

            // Delete prisoner record
            String sql = "DELETE FROM prisoners WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            // Update cell occupancy if prisoner had a cell
            if (affectedRows > 0 && cellId > 0) {
                updateCellOccupancy(conn, cellId, -1);
            }

            conn.commit();
            return affectedRows > 0;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting prisoner: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getTotalPrisoners() {
        String sql = "SELECT COUNT(*) FROM prisoners";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting prisoners: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private void updateCellOccupancy(Connection conn, int cellId, int change) throws SQLException {
        String sql = "UPDATE cells SET current_occupancy = current_occupancy + ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, change);
            stmt.setInt(2, cellId);
            stmt.executeUpdate();
        }
    }

    private Prisoner extractPrisonerFromResultSet(ResultSet rs) throws SQLException {
        Prisoner prisoner = new Prisoner();
        prisoner.setId(rs.getInt("id"));
        prisoner.setFirstName(rs.getString("first_name"));
        prisoner.setLastName(rs.getString("last_name"));
        prisoner.setDob(rs.getDate("dob"));
        prisoner.setGender(rs.getString("gender"));
        prisoner.setCrimeType(rs.getString("crime_type"));
        prisoner.setSentenceStart(rs.getDate("sentence_start"));
        prisoner.setSentenceEnd(rs.getDate("sentence_end"));
        prisoner.setCellId(rs.getInt("cell_id"));
        prisoner.setHealthStatus(rs.getString("health_status"));
        prisoner.setBehaviorStatus(rs.getString("behavior_status"));
        return prisoner;
    }
}