package com.prison.service;

import com.prison.dao.StaffDAO;
import com.prison.model.Staff;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class StaffService {
    private StaffDAO staffDAO;
    private SecureRandom random;

    public StaffService() {
        staffDAO = new StaffDAO();
        random = new SecureRandom();
    }

    public boolean registerStaff(Staff staff, String password) {
        // Generate a salt for this user
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String saltString = Base64.getEncoder().encodeToString(salt);

        // Hash the password with the salt
        String hashedPassword = hashPassword(password, saltString);

        // Store both the hash and salt
        staff.setPasswordHash(hashedPassword);
        staff.setSalt(saltString);

        return staffDAO.addStaff(staff);
    }

    public Staff getStaff(int id) {
        return staffDAO.getStaff(id);
    }

    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    public List<Staff> getStaffByDepartment(String department) {
        return staffDAO.getStaffByDepartment(department);
    }

    public boolean validateLogin(String username, String password) {
        Staff staff = staffDAO.getStaffByUsername(username);
        if (staff == null) {
            return false;
        }

        // Get the stored salt and hash the provided password with it
        String salt = staff.getSalt();
        String hashedPassword = hashPassword(password, salt);

        // Compare with stored hash
        return hashedPassword.equals(staff.getPasswordHash());
    }

    public boolean updateStaff(Staff staff) {
        return staffDAO.updateStaff(staff);
    }

    public boolean changePassword(int staffId, String oldPassword, String newPassword) {
        Staff staff = staffDAO.getStaff(staffId);
        if (staff == null) {
            return false;
        }

        // Verify old password first
        String salt = staff.getSalt();
        String oldPasswordHash = hashPassword(oldPassword, salt);
        if (!oldPasswordHash.equals(staff.getPasswordHash())) {
            return false; // Old password doesn't match
        }

        // Generate new hash from new password (keeping same salt)
        String newPasswordHash = hashPassword(newPassword, salt);
        staff.setPasswordHash(newPasswordHash);

        return staffDAO.updateStaff(staff);
    }

    public boolean removeStaff(int staffId) {
        return staffDAO.deleteStaff(staffId);
    }

    public boolean isAuthorized(int staffId, String action) {
        // Simplified authorization logic
        // In a real system, would check role/permissions
        Staff staff = staffDAO.getStaff(staffId);
        if (staff == null) {
            return false;
        }

        // Example: only administrators can delete records
        if (action.equals("DELETE_PRISONER") && !staff.getPosition().equals("Administrator")) {
            return false;
        }

        return true;
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // This would be a critical error in a real system
        }
    }

    public int getTotalStaff() {
        return staffDAO.getTotalStaff();
    }

    public Staff getLoggedInStaff(String username) {
        return staffDAO.getStaffByUsername(username);
    }
}