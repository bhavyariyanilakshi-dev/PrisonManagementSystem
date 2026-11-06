package com.prison.ui;

import com.prison.model.Cell;
import com.prison.model.Prisoner;
import com.prison.service.CellService;
import com.prison.service.PrisonerService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferCellDialog extends JDialog {
    private Prisoner prisoner;
    private PrisonerService prisonerService;
    private CellService cellService;
    private Runnable onSuccess;

    private JComboBox<Cell> cellComboBox;
    private JLabel currentCellLabel;
    private JLabel infoLabel;

    public TransferCellDialog(Frame owner, Prisoner prisoner, PrisonerService prisonerService, CellService cellService, Runnable onSuccess) {
        super(owner, "Transfer Cell - Prisoner #" + prisoner.getId(), true);
        this.prisoner = prisoner;
        this.prisonerService = prisonerService;
        this.cellService = cellService;
        this.onSuccess = onSuccess;

        setSize(450, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Current cell info
        Cell currentCell = cellService.getCell(prisoner.getCellId());
        String currentCellStr = (currentCell != null) ?
                currentCell.getBlockName() + " - " + currentCell.getCellNumber() + " (" + currentCell.getSecurityLevel() + ")" :
                "Not Assigned (ID: " + prisoner.getCellId() + ")";

        currentCellLabel = new JLabel("<html><b>Current Cell:</b> " + currentCellStr + "</html>");
        contentPanel.add(currentCellLabel);

        // Target cell combo
        JPanel selectPanel = new JPanel(new BorderLayout(5, 5));
        selectPanel.add(new JLabel("Select Destination Cell:"), BorderLayout.NORTH);

        cellComboBox = new JComboBox<>();
        List<Cell> availableCells = cellService.getAvailableCells();
        for (Cell c : availableCells) {
            // Exclude current cell
            if (c.getId() != prisoner.getCellId()) {
                cellComboBox.addItem(c);
            }
        }
        selectPanel.add(cellComboBox, BorderLayout.CENTER);
        contentPanel.add(selectPanel);

        // Security level info
        infoLabel = new JLabel("<html><font color='gray'>Prisoner Security Level: <b>" +
                prisoner.getBehaviorStatus() + " / " + prisoner.getCrimeType() + "</b></font></html>");
        contentPanel.add(infoLabel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton transferButton = new JButton("Confirm Transfer");
        transferButton.addActionListener(e -> executeTransfer());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(transferButton);
        buttonPanel.add(cancelButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void executeTransfer() {
        Cell targetCell = (Cell) cellComboBox.getSelectedItem();
        if (targetCell == null) {
            JOptionPane.showMessageDialog(this, "No available cell selected.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = prisonerService.transferPrisonerToCell(prisoner.getId(), targetCell.getId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Prisoner transferred to " + targetCell.getBlockName() + " - " + targetCell.getCellNumber() + " successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to transfer prisoner. Cell may be full.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
