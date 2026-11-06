package com.prison.ui;

import com.prison.model.Prisoner;
import com.prison.service.PrisonerService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPrisonerDialog extends JDialog {
    private Prisoner prisoner;
    private PrisonerService prisonerService;
    private Runnable onSaveSuccess;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JComboBox<String> genderComboBox;
    private JTextField crimeTypeField;
    private JTextField sentenceStartField;
    private JTextField sentenceEndField;
    private JComboBox<String> healthStatusComboBox;
    private JComboBox<String> behaviorStatusComboBox;

    public EditPrisonerDialog(Frame owner, Prisoner prisoner, PrisonerService prisonerService, Runnable onSaveSuccess) {
        super(owner, "Edit Prisoner - #" + prisoner.getId(), true);
        this.prisoner = prisoner;
        this.prisonerService = prisonerService;
        this.onSaveSuccess = onSaveSuccess;

        setSize(480, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        firstNameField = new JTextField(prisoner.getFirstName(), 20);
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        lastNameField = new JTextField(prisoner.getLastName(), 20);
        formPanel.add(lastNameField, gbc);

        // DOB
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("DOB (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        dobField = new JTextField(prisoner.getDob() != null ? dateFormat.format(prisoner.getDob()) : "", 20);
        formPanel.add(dobField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderComboBox.setSelectedItem(prisoner.getGender());
        formPanel.add(genderComboBox, gbc);

        // Crime Type
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Crime Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        crimeTypeField = new JTextField(prisoner.getCrimeType(), 20);
        formPanel.add(crimeTypeField, gbc);

        // Sentence Start
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Sentence Start (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        sentenceStartField = new JTextField(prisoner.getSentenceStart() != null ? dateFormat.format(prisoner.getSentenceStart()) : "", 20);
        formPanel.add(sentenceStartField, gbc);

        // Sentence End
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Sentence End (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        sentenceEndField = new JTextField(prisoner.getSentenceEnd() != null ? dateFormat.format(prisoner.getSentenceEnd()) : "", 20);
        formPanel.add(sentenceEndField, gbc);

        // Health Status
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Health Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        healthStatusComboBox = new JComboBox<>(new String[]{"Good", "Fair", "Poor", "Critical"});
        healthStatusComboBox.setSelectedItem(prisoner.getHealthStatus());
        formPanel.add(healthStatusComboBox, gbc);

        // Behavior Status
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Behavior Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8;
        behaviorStatusComboBox = new JComboBox<>(new String[]{"Good", "Normal", "Problematic", "Violent"});
        behaviorStatusComboBox.setSelectedItem(prisoner.getBehaviorStatus());
        formPanel.add(behaviorStatusComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveChanges() {
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            dobField.getText().trim().isEmpty() ||
            crimeTypeField.getText().trim().isEmpty() ||
            sentenceStartField.getText().trim().isEmpty() ||
            sentenceEndField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = dateFormat.parse(dobField.getText().trim());
            Date start = dateFormat.parse(sentenceStartField.getText().trim());
            Date end = dateFormat.parse(sentenceEndField.getText().trim());

            if (end.before(start)) {
                JOptionPane.showMessageDialog(this, "Sentence end date must be after start date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            prisoner.setFirstName(firstNameField.getText().trim());
            prisoner.setLastName(lastNameField.getText().trim());
            prisoner.setDob(dob);
            prisoner.setGender((String) genderComboBox.getSelectedItem());
            prisoner.setCrimeType(crimeTypeField.getText().trim());
            prisoner.setSentenceStart(start);
            prisoner.setSentenceEnd(end);
            prisoner.setHealthStatus((String) healthStatusComboBox.getSelectedItem());
            prisoner.setBehaviorStatus((String) behaviorStatusComboBox.getSelectedItem());

            boolean success = prisonerService.updatePrisoner(prisoner);
            if (success) {
                JOptionPane.showMessageDialog(this, "Prisoner details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSaveSuccess != null) {
                    onSaveSuccess.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update prisoner in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
