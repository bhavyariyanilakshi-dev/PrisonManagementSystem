package com.prison.utils;

import com.prison.model.LegalEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.prison.service.LegalEventService;

public class ChartUtility {

    public static ChartPanel createHealthStatusChart(int prisonerId) {
        // Create a sample pie chart for health status distribution
        // In a real app, this would fetch actual data from the database

        DefaultPieDataset dataset = new DefaultPieDataset();

        if (prisonerId == 0) {
            // General stats for all prisoners
            dataset.setValue("Good", 65);
            dataset.setValue("Fair", 25);
            dataset.setValue("Poor", 8);
            dataset.setValue("Critical", 2);
        } else {
            // For specific prisoner, would typically show their health history
            dataset.setValue("Good", 75);
            dataset.setValue("Fair", 20);
            dataset.setValue("Poor", 5);
            dataset.setValue("Critical", 0);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Health Status Distribution",
                dataset,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );

        // Customize chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Good", new Color(50, 200, 50));
        plot.setSectionPaint("Fair", new Color(200, 200, 50));
        plot.setSectionPaint("Poor", new Color(200, 100, 50));
        plot.setSectionPaint("Critical", new Color(200, 50, 50));

        return new ChartPanel(chart);
    }

    public static ChartPanel createLegalTimelineChart(int prisonerId) {
        // Create a sample bar chart for legal timeline
        // In a real app, this would fetch actual data from the database

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (prisonerId == 0) {
            // General crime type distribution for all prisoners
            dataset.addValue(35, "Crime Types", "Theft");
            dataset.addValue(25, "Crime Types", "Assault");
            dataset.addValue(15, "Crime Types", "Fraud");
            dataset.addValue(10, "Crime Types", "Drug Related");
            dataset.addValue(15, "Crime Types", "Other");
        } else {
            // For specific prisoner, fetch their legal events
            LegalEventService legalEventService = new LegalEventService();
            List<LegalEvent> events = legalEventService.getLegalHistoryForPrisoner(prisonerId);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Group events by type for the chart
            Map<String, Integer> eventCounts = new HashMap<>();

            for (LegalEvent event : events) {
                String eventType = event.getEventType();
                eventCounts.put(eventType, eventCounts.getOrDefault(eventType, 0) + 1);
            }

            // Add to dataset
            for (Map.Entry<String, Integer> entry : eventCounts.entrySet()) {
                dataset.addValue(entry.getValue(), "Events", entry.getKey());
            }

            // If no events, add placeholder
            if (events.isEmpty()) {
                dataset.addValue(0, "Events", "No Events");
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                prisonerId == 0 ? "Crime Type Distribution" : "Legal Events Timeline",
                prisonerId == 0 ? "Crime Type" : "Event Type",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );

        return new ChartPanel(chart);
    }
}