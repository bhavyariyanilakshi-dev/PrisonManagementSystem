package com.prison.ui;

import com.prison.model.Prisoner;
import com.prison.model.Cell;
import com.prison.service.CellService;
import com.prison.service.PrisonerService;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class PrisonerDetailsPanel extends JPanel {
    private MainFrame mainFrame;
    private PrisonerService prisonerService;
    private CellService cellService;
    private int prisonerId;
    private Prisoner prisoner;

    // UI components
    private JPanel mainInfoPanel;
    private JPanel sentencePanel;
    private JPanel cellInfoPanel;
    private JPanel actionButtonsPanel;

    public PrisonerDetailsPanel(MainFrame mainFrame, int prisonerId) {
        this.mainFrame = mainFrame;
        this.prisonerId = prisonerId;
        this.prisonerService = new PrisonerService();
        this.cellService = new CellService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadPrisoner();
    }

    private void loadPrisoner() {
        new SwingWorker<Prisoner, Void>() {
            @Override
            protected Prisoner doInBackground() {
                return prisonerService.getPrisoner(prisonerId);
            }

            @Override
            protected void done() {
                try {
                    prisoner = get();
                    if (prisoner != null) {
                        initComponents();
                    } else {
                        JOptionPane.showMessageDialog(PrisonerDetailsPanel.this,
                                "Prisoner not found",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        mainFrame.navigateTo("Prisoners");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PrisonerDetailsPanel.this,
                            "Error loading prisoner details: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void initComponents() {
        removeAll();

        // Header with prisoner name
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(prisoner.getFirstName() + " " + prisoner.getLastName(), SwingConstants.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton backButton = new JButton("← Back to Prisoners");
        backButton.addActionListener(e -> {
            mainFrame.refreshPrisonersPanel();
            mainFrame.navigateTo("Prisoners");
        });

        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Personal Information Section
        createPersonalInfoPanel();
        contentPanel.add(mainInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Sentence Information Section
        createSentencePanel();
        contentPanel.add(sentencePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Cell Information Section
        createCellInfoPanel();
        contentPanel.add(cellInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Actions Section
        createActionButtonsPanel();
        contentPanel.add(actionButtonsPanel);

        // Add components to main panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void createPersonalInfoPanel() {
        mainInfoPanel = new JPanel();
        mainInfoPanel.setLayout(new GridLayout(0, 2, 10, 5));
        mainInfoPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        addLabeledField(mainInfoPanel, "ID:", String.valueOf(prisoner.getId()));
        addLabeledField(mainInfoPanel, "First Name:", prisoner.getFirstName());
        addLabeledField(mainInfoPanel, "Last Name:", prisoner.getLastName());
        addLabeledField(mainInfoPanel, "Date of Birth:", prisoner.getDob() != null ? dateFormat.format(prisoner.getDob()) : "N/A");
        addLabeledField(mainInfoPanel, "Gender:", prisoner.getGender());
        addLabeledField(mainInfoPanel, "Health Status:", prisoner.getHealthStatus());
        addLabeledField(mainInfoPanel, "Behavior Status:", prisoner.getBehaviorStatus());
    }

    private void createSentencePanel() {
        sentencePanel = new JPanel();
        sentencePanel.setLayout(new GridLayout(0, 2, 10, 5));
        sentencePanel.setBorder(BorderFactory.createTitledBorder("Sentence Information"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        addLabeledField(sentencePanel, "Crime:", prisoner.getCrimeType());
        addLabeledField(sentencePanel, "Sentence Start:", prisoner.getSentenceStart() != null ? dateFormat.format(prisoner.getSentenceStart()) : "N/A");
        addLabeledField(sentencePanel, "Sentence End:", prisoner.getSentenceEnd() != null ? dateFormat.format(prisoner.getSentenceEnd()) : "N/A");

        if (prisoner.getSentenceEnd() != null) {
            long remainingDays = (prisoner.getSentenceEnd().getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
            addLabeledField(sentencePanel, "Remaining Time:", (remainingDays > 0 ? remainingDays : 0) + " days");
        } else {
            addLabeledField(sentencePanel, "Remaining Time:", "N/A");
        }
    }

    private void createCellInfoPanel() {
        cellInfoPanel = new JPanel();
        cellInfoPanel.setLayout(new GridLayout(0, 2, 10, 5));
        cellInfoPanel.setBorder(BorderFactory.createTitledBorder("Cell Information"));

        new SwingWorker<Cell, Void>() {
            @Override
            protected Cell doInBackground() {
                return cellService.getCell(prisoner.getCellId());
            }

            @Override
            protected void done() {
                try {
                    Cell cell = get();
                    cellInfoPanel.removeAll();
                    if (cell != null) {
                        addLabeledField(cellInfoPanel, "Cell ID:", String.valueOf(cell.getId()));
                        addLabeledField(cellInfoPanel, "Block:", cell.getBlockName());
                        addLabeledField(cellInfoPanel, "Cell Number:", cell.getCellNumber());
                        addLabeledField(cellInfoPanel, "Security Level:", cell.getSecurityLevel());
                        addLabeledField(cellInfoPanel, "Occupancy:", cell.getCurrentOccupancy() + " / " + cell.getCapacity());
                    } else {
                        addLabeledField(cellInfoPanel, "Cell:", "Not Assigned");
                    }
                    cellInfoPanel.revalidate();
                    cellInfoPanel.repaint();
                } catch (Exception e) {
                    addLabeledField(cellInfoPanel, "Cell:", "Error loading cell information");
                }
            }
        }.execute();
    }

    private void createActionButtonsPanel() {
        actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionButtonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton editButton = new JButton("Edit Prisoner");
        editButton.addActionListener(e -> editPrisoner());

        JButton transferButton = new JButton("Transfer Cell");
        transferButton.addActionListener(e -> transferCell());

        JButton healthButton = new JButton("Update Health Record");
        healthButton.addActionListener(e -> updateHealthRecord());

        JButton releaseButton = new JButton("Release Prisoner");
        releaseButton.setForeground(new Color(180, 0, 0));
        releaseButton.addActionListener(e -> releasePrisoner());

        actionButtonsPanel.add(editButton);
        actionButtonsPanel.add(transferButton);
        actionButtonsPanel.add(healthButton);
        actionButtonsPanel.add(releaseButton);
    }

    private void addLabeledField(JPanel panel, String labelText, String value) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueLabel = new JLabel(value != null ? value : "");
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(label);
        panel.add(valueLabel);
    }

    private void editPrisoner() {
        EditPrisonerDialog dialog = new EditPrisonerDialog(mainFrame, prisoner, prisonerService, this::loadPrisoner);
        dialog.setVisible(true);
    }

    private void transferCell() {
        TransferCellDialog dialog = new TransferCellDialog(mainFrame, prisoner, prisonerService, cellService, this::loadPrisoner);
        dialog.setVisible(true);
    }

    private void updateHealthRecord() {
        String[] statuses = {"Good", "Fair", "Poor", "Critical"};
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select current health status for " + prisoner.getFirstName() + " " + prisoner.getLastName() + ":",
                "Update Health Record",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                prisoner.getHealthStatus()
        );

        if (selected != null && !selected.equals(prisoner.getHealthStatus())) {
            boolean success = prisonerService.updateHealthStatus(prisoner.getId(), selected);
            if (success) {
                prisoner.setHealthStatus(selected);
                loadPrisoner();
                JOptionPane.showMessageDialog(this, "Health status updated to: " + selected, "Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update health status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void releasePrisoner() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to release prisoner " + prisoner.getFirstName() + " " + prisoner.getLastName() + " (ID: " + prisoner.getId() + ")?\n" +
                "This will discharge the inmate and free up cell capacity.",
                "Confirm Prisoner Release",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            boolean success = prisonerService.releasePrisoner(prisoner.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Prisoner " + prisoner.getFirstName() + " " + prisoner.getLastName() + " has been released.", "Released", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.refreshPrisonersPanel();
                mainFrame.navigateTo("Prisoners");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to release prisoner.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
