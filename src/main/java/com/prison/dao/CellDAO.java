package com.prison.dao;

import com.prison.config.DBConnection;
import com.prison.model.Cell;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CellDAO {

    public boolean addCell(Cell cell) {
        String sql = "INSERT INTO cells (block_name, cell_number, capacity, current_occupancy, security_level) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cell.getBlockName());
            stmt.setString(2, cell.getCellNumber());
            stmt.setInt(3, cell.getCapacity());
            stmt.setInt(4, cell.getCurrentOccupancy());
            stmt.setString(5, cell.getSecurityLevel());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cell.setId(generatedKeys.getInt(1));
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

    public Cell getCell(int id) {
        String sql = "SELECT * FROM cells WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cell cell = new Cell();
                    cell.setId(rs.getInt("id"));
                    cell.setBlockName(rs.getString("block_name"));
                    cell.setCellNumber(rs.getString("cell_number"));
                    cell.setCapacity(rs.getInt("capacity"));
                    cell.setCurrentOccupancy(rs.getInt("current_occupancy"));
                    cell.setSecurityLevel(rs.getString("security_level"));
                    return cell;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Cell> getAllCells() {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT * FROM cells";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cell cell = new Cell();
                cell.setId(rs.getInt("id"));
                cell.setBlockName(rs.getString("block_name"));
                cell.setCellNumber(rs.getString("cell_number"));
                cell.setCapacity(rs.getInt("capacity"));
                cell.setCurrentOccupancy(rs.getInt("current_occupancy"));
                cell.setSecurityLevel(rs.getString("security_level"));
                cells.add(cell);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cells;
    }

    public List<Cell> getAvailableCells() {
        List<Cell> availableCells = new ArrayList<>();
        String sql = "SELECT * FROM cells WHERE current_occupancy < capacity";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cell cell = new Cell();
                cell.setId(rs.getInt("id"));
                cell.setBlockName(rs.getString("block_name"));
                cell.setCellNumber(rs.getString("cell_number"));
                cell.setCapacity(rs.getInt("capacity"));
                cell.setCurrentOccupancy(rs.getInt("current_occupancy"));
                cell.setSecurityLevel(rs.getString("security_level"));
                availableCells.add(cell);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableCells;
    }

    public List<Cell> getCellsBySecurityLevel(String securityLevel) {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT * FROM cells WHERE security_level = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, securityLevel);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cell cell = new Cell();
                    cell.setId(rs.getInt("id"));
                    cell.setBlockName(rs.getString("block_name"));
                    cell.setCellNumber(rs.getString("cell_number"));
                    cell.setCapacity(rs.getInt("capacity"));
                    cell.setCurrentOccupancy(rs.getInt("current_occupancy"));
                    cell.setSecurityLevel(rs.getString("security_level"));
                    cells.add(cell);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cells;
    }

    public List<Cell> getAvailableCellsBySecurityLevel(String securityLevel) {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT * FROM cells WHERE security_level = ? AND current_occupancy < capacity";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, securityLevel);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cell cell = new Cell();
                    cell.setId(rs.getInt("id"));
                    cell.setBlockName(rs.getString("block_name"));
                    cell.setCellNumber(rs.getString("cell_number"));
                    cell.setCapacity(rs.getInt("capacity"));
                    cell.setCurrentOccupancy(rs.getInt("current_occupancy"));
                    cell.setSecurityLevel(rs.getString("security_level"));
                    cells.add(cell);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cells;
    }

    public boolean updateCell(Cell cell) {
        String sql = "UPDATE cells SET block_name = ?, cell_number = ?, capacity = ?, " +
                "current_occupancy = ?, security_level = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cell.getBlockName());
            stmt.setString(2, cell.getCellNumber());
            stmt.setInt(3, cell.getCapacity());
            stmt.setInt(4, cell.getCurrentOccupancy());
            stmt.setString(5, cell.getSecurityLevel());
            stmt.setInt(6, cell.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean incrementOccupancy(int cellId) {
        String sql = "UPDATE cells SET current_occupancy = current_occupancy + 1 WHERE id = ? AND current_occupancy < capacity";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cellId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decrementOccupancy(int cellId) {
        String sql = "UPDATE cells SET current_occupancy = current_occupancy - 1 WHERE id = ? AND current_occupancy > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cellId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCell(int id) {
        String sql = "DELETE FROM cells WHERE id = ?";

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

    public int getTotalCells() {
        String sql = "SELECT COUNT(*) FROM cells";

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
}