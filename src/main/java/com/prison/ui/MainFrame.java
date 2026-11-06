package com.prison.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private PrisonersPanel prisonersPanel;
    private RegistrationPanel registrationPanel;
    private DashboardPanel dashboardPanel;
    private JPanel currentPrisonerDetailsPanel; // Will hold the current details panel

    public MainFrame() {
        setTitle("Prison Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels
        prisonersPanel = new PrisonersPanel(this);
        registrationPanel = new RegistrationPanel(this);
        dashboardPanel = new DashboardPanel(this);

        // Add panels to card layout
        mainPanel.add(prisonersPanel, "Prisoners");
        mainPanel.add(registrationPanel, "Registration");
        mainPanel.add(dashboardPanel, "Dashboard");

        // Add main panel to frame
        add(mainPanel);

        // Add menu bar
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Navigation menu
        JMenu navMenu = new JMenu("Navigation");

        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> navigateTo("Dashboard"));

        JMenuItem prisonersItem = new JMenuItem("Prisoners");
        prisonersItem.addActionListener(e -> navigateTo("Prisoners"));

        JMenuItem registrationItem = new JMenuItem("Register Prisoner");
        registrationItem.addActionListener(e -> navigateTo("Registration"));

        navMenu.add(dashboardItem);
        navMenu.add(prisonersItem);
        navMenu.add(registrationItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    public void navigateTo(String panelName) {
        if ("Registration".equals(panelName)) {
            registrationPanel.refreshCells();
        } else if ("Dashboard".equals(panelName)) {
            dashboardPanel.refreshDashboard();
        } else if ("Prisoners".equals(panelName)) {
            prisonersPanel.refreshData();
        }
        cardLayout.show(mainPanel, panelName);
    }

    public void switchToPrisonerDetails(int prisonerId) {
        // Remove the previous prisoner details panel if it exists
        if (currentPrisonerDetailsPanel != null) {
            mainPanel.remove(currentPrisonerDetailsPanel);
        }

        // Create a new prisoner details panel
        currentPrisonerDetailsPanel = new PrisonerDetailsPanel(this, prisonerId);

        // Add the new panel and show it
        mainPanel.add(currentPrisonerDetailsPanel, "PrisonerDetails");
        cardLayout.show(mainPanel, "PrisonerDetails");
    }

    public void refreshPrisonersPanel() {
        prisonersPanel.refreshData();
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Prison Management System\nVersion 1.0\n\nDeveloped by Your Team",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}