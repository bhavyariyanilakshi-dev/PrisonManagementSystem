package com.prison.dao;

import com.prison.config.DBConnection;
import com.prison.model.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public boolean addStaff(Staff staff) {
        String sql = "INSERT INTO staff (first_name, last_name, position, department, username, password_hash, salt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, staff.getFirstName());
            stmt.setString(2, staff.getLastName());
            stmt.setString(3, staff.getPosition());
            stmt.setString(4, staff.getDepartment());
            stmt.setString(5, staff.getUsername());
            stmt.setString(6, staff.getPasswordHash());
            stmt.setString(7, staff.getSalt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    staff.setId(generatedKeys.getInt(1));
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

    public Staff getStaff(int id) {
        String sql = "SELECT * FROM staff WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Staff staff = new Staff();
                    staff.setId(rs.getInt("id"));
                    staff.setFirstName(rs.getString("first_name"));
                    staff.setLastName(rs.getString("last_name"));
                    staff.setPosition(rs.getString("position"));
                    staff.setDepartment(rs.getString("department"));
                    staff.setUsername(rs.getString("username"));
                    staff.setPasswordHash(rs.getString("password_hash"));
                    staff.setSalt(rs.getString("salt"));
                    return staff;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Staff getStaffByUsername(String username) {
        String sql = "SELECT * FROM staff WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Staff staff = new Staff();
                    staff.setId(rs.getInt("id"));
                    staff.setFirstName(rs.getString("first_name"));
                    staff.setLastName(rs.getString("last_name"));
                    staff.setPosition(rs.getString("position"));
                    staff.setDepartment(rs.getString("department"));
                    staff.setUsername(rs.getString("username"));
                    staff.setPasswordHash(rs.getString("password_hash"));
                    staff.setSalt(rs.getString("salt"));
                    return staff;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Staff staff = new Staff();
                staff.setId(rs.getInt("id"));
                staff.setFirstName(rs.getString("first_name"));
                staff.setLastName(rs.getString("last_name"));
                staff.setPosition(rs.getString("position"));
                staff.setDepartment(rs.getString("department"));
                staff.setUsername(rs.getString("username"));
                staff.setPasswordHash(rs.getString("password_hash"));
                staff.setSalt(rs.getString("salt"));
                staffList.add(staff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffList;
    }

    public List<Staff> getStaffByDepartment(String department) {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE department = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, department);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Staff staff = new Staff();
                    staff.setId(rs.getInt("id"));
                    staff.setFirstName(rs.getString("first_name"));
                    staff.setLastName(rs.getString("last_name"));
                    staff.setPosition(rs.getString("position"));
                    staff.setDepartment(rs.getString("department"));
                    staff.setUsername(rs.getString("username"));
                    staff.setPasswordHash(rs.getString("password_hash"));
                    staff.setSalt(rs.getString("salt"));
                    staffList.add(staff);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffList;
    }

    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staff SET first_name = ?, last_name = ?, position = ?, " +
                "department = ?, username = ?, password_hash = ?, salt = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getFirstName());
            stmt.setString(2, staff.getLastName());
            stmt.setString(3, staff.getPosition());
            stmt.setString(4, staff.getDepartment());
            stmt.setString(5, staff.getUsername());
            stmt.setString(6, staff.getPasswordHash());
            stmt.setString(7, staff.getSalt());
            stmt.setInt(8, staff.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStaff(int id) {
        String sql = "DELETE FROM staff WHERE id = ?";

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

    public int getTotalStaff() {
        String sql = "SELECT COUNT(*) FROM staff";

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