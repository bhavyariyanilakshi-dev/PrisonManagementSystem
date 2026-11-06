package com.prison.ui;

import com.prison.model.Cell;
import com.prison.model.Prisoner;
import com.prison.service.CellService;
import com.prison.service.PrisonerService;
import com.prison.utils.AIUtility;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RegistrationPanel extends JPanel {
    private MainFrame mainFrame;
    private PrisonerService prisonerService;
    private CellService cellService;
    private AIUtility aiUtility;

    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JComboBox<String> genderComboBox;
    private JTextField crimeTypeField;
    private JTextField sentenceStartField;
    private JTextField sentenceEndField;
    private JComboBox<Cell> cellComboBox;
    private JComboBox<String> healthStatusComboBox;
    private JComboBox<String> behaviorStatusComboBox;
    private JCheckBox aiAllocationCheckbox;

    public RegistrationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.prisonerService = new PrisonerService();
        this.cellService = new CellService();
        this.aiUtility = new AIUtility();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Register New Prisoner", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // First name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);

        // Last name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);

        // Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        dobField = new JTextField(20);
        formPanel.add(dobField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Gender:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderComboBox, gbc);

        // Crime Type
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Crime Type:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        crimeTypeField = new JTextField(20);
        formPanel.add(crimeTypeField, gbc);

        // Sentence Start
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Sentence Start (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        sentenceStartField = new JTextField(20);
        // Default to today's date
        sentenceStartField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(sentenceStartField, gbc);

        // Sentence End
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Sentence End (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        sentenceEndField = new JTextField(20);
        formPanel.add(sentenceEndField, gbc);

        // Cell
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Cell:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        cellComboBox = new JComboBox<>();
        refreshCells();
        formPanel.add(cellComboBox, gbc);

        // Health Status
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Health Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        healthStatusComboBox = new JComboBox<>(new String[]{"Good", "Fair", "Poor", "Critical"});
        formPanel.add(healthStatusComboBox, gbc);

        // Behavior Status
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(new JLabel("Behavior Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        behaviorStatusComboBox = new JComboBox<>(new String[]{"Good", "Normal", "Problematic", "Violent"});
        formPanel.add(behaviorStatusComboBox, gbc);

        // AI Cell Allocation checkbox
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        aiAllocationCheckbox = new JCheckBox("Use AI for Cell Allocation");
        aiAllocationCheckbox.addActionListener(e -> {
            cellComboBox.setEnabled(!aiAllocationCheckbox.isSelected());
        });
        formPanel.add(aiAllocationCheckbox, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> savePrisoner());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> mainFrame.navigateTo("Dashboard"));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add all components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void savePrisoner() {
        // Validate inputs
        if (firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                dobField.getText().trim().isEmpty() ||
                crimeTypeField.getText().trim().isEmpty() ||
                sentenceStartField.getText().trim().isEmpty() ||
                sentenceEndField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Parse dates
            Date dob = dateFormat.parse(dobField.getText());
            Date sentenceStart = dateFormat.parse(sentenceStartField.getText());
            Date sentenceEnd = dateFormat.parse(sentenceEndField.getText());

            // Validate dates
            if (sentenceEnd.before(sentenceStart)) {
                JOptionPane.showMessageDialog(this, "Sentence end date must be after start date",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create prisoner object
            Prisoner prisoner = new Prisoner();
            prisoner.setFirstName(firstNameField.getText());
            prisoner.setLastName(lastNameField.getText());
            prisoner.setDob(dob);
            prisoner.setGender((String) genderComboBox.getSelectedItem());
            prisoner.setCrimeType(crimeTypeField.getText());
            prisoner.setSentenceStart(sentenceStart);
            prisoner.setSentenceEnd(sentenceEnd);

            // Determine cell assignment
            if (aiAllocationCheckbox.isSelected()) {
                // Use AI to recommend a cell
                Cell recommendedCell = aiUtility.recommendCell(prisoner);
                if (recommendedCell != null) {
                    prisoner.setCellId(recommendedCell.getId());
                } else {
                    JOptionPane.showMessageDialog(this, "No available cells found",
                            "Cell Assignment", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                // Get selected cell
                if (cellComboBox.getSelectedItem() != null) {
                    Cell selectedCell = (Cell) cellComboBox.getSelectedItem();
                    prisoner.setCellId(selectedCell.getId());
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a cell",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            prisoner.setHealthStatus((String) healthStatusComboBox.getSelectedItem());
            prisoner.setBehaviorStatus((String) behaviorStatusComboBox.getSelectedItem());

            // Save prisoner
            boolean success = prisonerService.registerPrisoner(prisoner);

            if (success) {
                JOptionPane.showMessageDialog(this, "Prisoner registered successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                mainFrame.refreshPrisonersPanel();
                mainFrame.navigateTo("Prisoners");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register prisoner",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshCells() {
        if (cellComboBox == null) return;
        cellComboBox.removeAllItems();
        List<Cell> availableCells = cellService.getAvailableCells();
        for (Cell cell : availableCells) {
            cellComboBox.addItem(cell);
        }
    }

    private void resetForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        genderComboBox.setSelectedIndex(0);
        crimeTypeField.setText("");
        sentenceStartField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        sentenceEndField.setText("");
        refreshCells();
        if (cellComboBox.getItemCount() > 0) {
            cellComboBox.setSelectedIndex(0);
        }
        healthStatusComboBox.setSelectedIndex(0);
        behaviorStatusComboBox.setSelectedIndex(0);
        aiAllocationCheckbox.setSelected(false);
        cellComboBox.setEnabled(true);
    }
}