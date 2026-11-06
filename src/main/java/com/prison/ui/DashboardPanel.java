package com.prison.ui;

import com.prison.service.CellService;
import com.prison.service.PrisonerService;
import com.prison.utils.ChartUtility;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private PrisonerService prisonerService;
    private CellService cellService;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.prisonerService = new PrisonerService();
        this.cellService = new CellService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Dashboard", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshDashboard());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        // Summary Statistics Panel
        JPanel statsPanel = createStatsPanel();

        // Charts Panel
        JPanel chartsPanel = createChartsPanel();

        // Quick Access Panel
        JPanel quickAccessPanel = createQuickAccessPanel();

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(chartsPanel, BorderLayout.CENTER);
        contentPanel.add(quickAccessPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Summary Statistics"));

        int totalPrisoners = prisonerService.getAllPrisoners().size();
        int totalCells = cellService.getAllCells().size();
        int availableCells = cellService.getAvailableCells().size();
        int occupancyRate = totalCells > 0 ? ((totalCells - availableCells) * 100 / totalCells) : 0;

        statsPanel.add(createStatCard("Total Prisoners", String.valueOf(totalPrisoners)));
        statsPanel.add(createStatCard("Total Cells", String.valueOf(totalCells)));
        statsPanel.add(createStatCard("Available Cells", String.valueOf(availableCells)));
        statsPanel.add(createStatCard("Occupancy Rate", occupancyRate + "%"));

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createChartsPanel() {
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 5));
        chartsPanel.setBorder(BorderFactory.createTitledBorder("Prison Analytics"));

        ChartPanel occupancyChart = ChartUtility.createHealthStatusChart(0);
        ChartPanel crimesChart = ChartUtility.createLegalTimelineChart(0);

        chartsPanel.add(occupancyChart);
        chartsPanel.add(crimesChart);

        return chartsPanel;
    }

    private JPanel createQuickAccessPanel() {
        JPanel quickAccessPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        quickAccessPanel.setBorder(BorderFactory.createTitledBorder("Quick Access"));

        JButton prisonersButton = new JButton("View All Prisoners");
        prisonersButton.addActionListener(e -> mainFrame.navigateTo("Prisoners"));

        JButton registerButton = new JButton("Register New Prisoner");
        registerButton.addActionListener(e -> mainFrame.navigateTo("Registration"));

        JButton cellsButton = new JButton("Manage Cells");
        cellsButton.addActionListener(e -> {
            CellsDialog dialog = new CellsDialog(mainFrame, this::refreshDashboard);
            dialog.setVisible(true);
        });

        JButton legalEventsButton = new JButton("Record Legal Event");
        legalEventsButton.addActionListener(e -> {
            LegalEventDialog dialog = new LegalEventDialog(mainFrame, this::refreshDashboard);
            dialog.setVisible(true);
        });

        quickAccessPanel.add(prisonersButton);
        quickAccessPanel.add(registerButton);
        quickAccessPanel.add(cellsButton);
        quickAccessPanel.add(legalEventsButton);

        return quickAccessPanel;
    }

    public void refreshDashboard() {
        removeAll();
        initComponents();
        revalidate();
        repaint();
    }
}