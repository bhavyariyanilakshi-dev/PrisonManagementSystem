package com.prison.ui;

import com.prison.model.Prisoner;
import com.prison.service.PrisonerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class PrisonersPanel extends JPanel {
    private MainFrame mainFrame;
    private PrisonerService prisonerService;
    private JTable prisonerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton resetButton;
    private JLabel statusLabel;

    public PrisonersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.prisonerService = new PrisonerService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadPrisoners();
    }

    private void initComponents() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Prisoners Management", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchPrisoners());
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> loadPrisoners());

        searchPanel.add(new JLabel("Search by name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Table panel
        String[] columnNames = {"ID", "First Name", "Last Name", "DOB", "Crime Type",
                "Sentence Start", "Sentence End", "Cell", "Health Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        prisonerTable = new JTable(tableModel);
        prisonerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prisonerTable.setAutoCreateRowSorter(true);

        // Add double-click listener for details
        prisonerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openPrisonerDetails();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(prisonerTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton detailsButton = new JButton("View Details");
        detailsButton.addActionListener(e -> openPrisonerDetails());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPrisoners());

        buttonPanel.add(detailsButton);
        buttonPanel.add(refreshButton);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Create a bottom panel to hold buttons and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPrisoners() {
        // Set status
        statusLabel.setText("Loading prisoners...");

        // Use SwingWorker to perform database operation in background
        new SwingWorker<List<Prisoner>, Void>() {
            @Override
            protected List<Prisoner> doInBackground() {
                try {
                    return prisonerService.getAllPrisoners();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    // Clear existing data
                    tableModel.setRowCount(0);

                    // Get all prisoners from the worker thread
                    List<Prisoner> prisoners = get();

                    if (prisoners != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        // Add prisoners to table
                        for (Prisoner prisoner : prisoners) {
                            Object[] rowData = {
                                    prisoner.getId(),
                                    prisoner.getFirstName(),
                                    prisoner.getLastName(),
                                    dateFormat.format(prisoner.getDob()),
                                    prisoner.getCrimeType(),
                                    dateFormat.format(prisoner.getSentenceStart()),
                                    dateFormat.format(prisoner.getSentenceEnd()),
                                    prisoner.getCellId(),
                                    prisoner.getHealthStatus()
                            };
                            tableModel.addRow(rowData);
                        }

                        // Update status
                        statusLabel.setText("Loaded " + prisoners.size() + " prisoners");
                    } else {
                        statusLabel.setText("Error loading prisoners");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error loading prisoners");
                    JOptionPane.showMessageDialog(PrisonersPanel.this,
                            "Error loading prisoners: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void searchPrisoners() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPrisoners();
            return;
        }

        // Set status
        statusLabel.setText("Searching...");

        // Use SwingWorker to perform database operation in background
        new SwingWorker<List<Prisoner>, Void>() {
            @Override
            protected List<Prisoner> doInBackground() {
                return prisonerService.searchPrisonersByName(searchTerm);
            }

            @Override
            protected void done() {
                try {
                    // Clear existing data
                    tableModel.setRowCount(0);

                    // Get search results from the worker thread
                    List<Prisoner> prisoners = get();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    // Add prisoners to table
                    for (Prisoner prisoner : prisoners) {
                        Object[] rowData = {
                                prisoner.getId(),
                                prisoner.getFirstName(),
                                prisoner.getLastName(),
                                dateFormat.format(prisoner.getDob()),
                                prisoner.getCrimeType(),
                                dateFormat.format(prisoner.getSentenceStart()),
                                dateFormat.format(prisoner.getSentenceEnd()),
                                prisoner.getCellId(),
                                prisoner.getHealthStatus()
                        };
                        tableModel.addRow(rowData);
                    }

                    // Update status
                    statusLabel.setText("Found " + prisoners.size() + " prisoners");

                } catch (Exception ex) {
                    statusLabel.setText("Error searching prisoners");
                    JOptionPane.showMessageDialog(PrisonersPanel.this,
                            "Error searching prisoners: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void openPrisonerDetails() {
        int selectedRow = prisonerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a prisoner first",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get the selected prisoner ID from the table
        int prisonerId = (int) prisonerTable.getValueAt(selectedRow, 0);

        // Tell the main frame to switch to prisoner details
        mainFrame.switchToPrisonerDetails(prisonerId);
    }

    // Method to refresh data
    public void refreshData() {
        loadPrisoners();
    }
}
