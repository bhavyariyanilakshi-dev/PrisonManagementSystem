package com.prison.ui;

import com.prison.model.Cell;
import com.prison.service.CellService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CellsDialog extends JDialog {
    private CellService cellService;
    private Runnable onCellsChanged;

    private JTable cellsTable;
    private DefaultTableModel tableModel;

    private JTextField blockField;
    private JTextField numberField;
    private JSpinner capacitySpinner;
    private JComboBox<String> securityComboBox;

    public CellsDialog(Frame owner, Runnable onCellsChanged) {
        super(owner, "Manage Prison Cells", true);
        this.cellService = new CellService();
        this.onCellsChanged = onCellsChanged;

        setSize(750, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadCells();
    }

    private void initComponents() {
        // Table panel
        String[] columnNames = {"ID", "Block Name", "Cell Number", "Capacity", "Occupancy", "Security Level", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cellsTable = new JTable(tableModel);
        cellsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(cellsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Prison Cells"));

        // Add Cell Form Panel
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Cell"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("Block Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        blockField = new JTextField("Block A", 12);
        addPanel.add(blockField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        addPanel.add(new JLabel("Cell Number:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        numberField = new JTextField(8);
        addPanel.add(numberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        capacitySpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        addPanel.add(capacitySpinner, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        addPanel.add(new JLabel("Security Level:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        securityComboBox = new JComboBox<>(new String[]{"Minimum", "Medium", "Maximum"});
        addPanel.add(securityComboBox, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        JButton addButton = new JButton("Add Cell");
        addButton.addActionListener(e -> addCell());
        addPanel.add(addButton, gbc);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(addPanel, BorderLayout.CENTER);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        closePanel.add(closeButton);
        bottomPanel.add(closePanel, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadCells() {
        tableModel.setRowCount(0);
        List<Cell> cells = cellService.getAllCells();
        for (Cell cell : cells) {
            String status = cell.isFull() ? "FULL" : "AVAILABLE (" + (cell.getCapacity() - cell.getCurrentOccupancy()) + " left)";
            Object[] row = {
                    cell.getId(),
                    cell.getBlockName(),
                    cell.getCellNumber(),
                    cell.getCapacity(),
                    cell.getCurrentOccupancy(),
                    cell.getSecurityLevel(),
                    status
            };
            tableModel.addRow(row);
        }
    }

    private void addCell() {
        String block = blockField.getText().trim();
        String number = numberField.getText().trim();
        int capacity = (Integer) capacitySpinner.getValue();
        String security = (String) securityComboBox.getSelectedItem();

        if (block.isEmpty() || number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Block Name and Cell Number are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cell cell = new Cell(0, block, number, capacity, 0, security);
        boolean success = cellService.registerCell(cell);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cell added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            numberField.setText("");
            loadCells();
            if (onCellsChanged != null) {
                onCellsChanged.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add cell.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
