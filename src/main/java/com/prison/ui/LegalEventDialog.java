package com.prison.ui;

import com.prison.model.LegalEvent;
import com.prison.model.Prisoner;
import com.prison.service.LegalEventService;
import com.prison.service.PrisonerService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LegalEventDialog extends JDialog {
    private LegalEventService legalEventService;
    private PrisonerService prisonerService;
    private Runnable onSuccess;

    private JComboBox<PrisonerItem> prisonerComboBox;
    private JComboBox<String> eventTypeComboBox;
    private JTextField eventDateField;
    private JTextArea descriptionArea;

    // Helper wrapper class for prisoner selection
    public static class PrisonerItem {
        private final int id;
        private final String displayName;

        public PrisonerItem(int id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public LegalEventDialog(Frame owner, Runnable onSuccess) {
        super(owner, "Record Legal Event", true);
        this.legalEventService = new LegalEventService();
        this.prisonerService = new PrisonerService();
        this.onSuccess = onSuccess;

        setSize(480, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Select Prisoner
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Prisoner:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        prisonerComboBox = new JComboBox<>();
        List<Prisoner> prisoners = prisonerService.getAllPrisoners();
        for (Prisoner p : prisoners) {
            prisonerComboBox.addItem(new PrisonerItem(p.getId(), "#" + p.getId() + " - " + p.getFirstName() + " " + p.getLastName()));
        }
        formPanel.add(prisonerComboBox, gbc);

        // Event Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Event Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        eventTypeComboBox = new JComboBox<>(new String[]{
                "Hearing", "Trial", "Appeal", "Parole Review",
                "Arraignment", "Medical Review", "Disciplinary Hearing", "Bail Hearing"
        });
        formPanel.add(eventTypeComboBox, gbc);

        // Event Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        eventDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 15);
        formPanel.add(eventDateField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Record");
        saveButton.addActionListener(e -> saveLegalEvent());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveLegalEvent() {
        PrisonerItem item = (PrisonerItem) prisonerComboBox.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "No prisoner selected.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String eventType = (String) eventTypeComboBox.getSelectedItem();
        String dateStr = eventDateField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Event date is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date eventDate = dateFormat.parse(dateStr);

            LegalEvent event = new LegalEvent(0, item.getId(), eventType, eventDate, description);
            boolean success = legalEventService.recordLegalEvent(event);

            if (success) {
                JOptionPane.showMessageDialog(this, "Legal event recorded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to record legal event.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
