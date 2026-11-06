package com.prison;

import com.prison.ui.MainFrame;
import com.prison.config.DBConnection;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

public class MainApp {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("System look and feel set successfully");
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }

        // Test database connection (MySQL or embedded fallback)
        boolean dbConnectionSuccessful = DBConnection.testConnection();

        if (!dbConnectionSuccessful) {
            System.err.println("WARNING: Database connection failed. Starting in limited demo mode.");
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to connect to MySQL or Embedded Database.\n" +
                    "Application will start with limited functionality.\n" +
                    "Please check database settings in config.properties.",
                    "Database Notice",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        // Start the application on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);

                if (DBConnection.isUsingEmbeddedFallback()) {
                    System.out.println("Application running with Embedded Database (Full functionality enabled).");
                } else if (dbConnectionSuccessful) {
                    System.out.println("Application started successfully with MySQL database connection.");
                }
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();

                JOptionPane.showMessageDialog(
                        null,
                        "Failed to start application: " + e.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
