package com.prison.utils;

import com.prison.dao.CellDAO;
import com.prison.dao.PrisonerDAO;
import com.prison.model.Cell;
import com.prison.model.Prisoner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AIUtility {

    private CellDAO cellDAO;
    private PrisonerDAO prisonerDAO;

    public AIUtility() {
        cellDAO = new CellDAO();
        prisonerDAO = new PrisonerDAO();
    }

    public Cell recommendCell(Prisoner prisoner) {
        // Get all available cells
        List<Cell> availableCells = cellDAO.getAvailableCells();
        if (availableCells.isEmpty()) {
            return null; // No available cells
        }

        // Assess security need based on crime type and behavior
        String securityLevel = determinePrisonerSecurityLevel(prisoner);

        // First, try to find perfect match by security level
        List<Cell> matchingCells = availableCells.stream()
                .filter(cell -> cell.getSecurityLevel().equalsIgnoreCase(securityLevel))
                .collect(Collectors.toList());

        if (!matchingCells.isEmpty()) {
            // Found matching cells, now optimize further

            // If prisoner health is poor, prioritize cells with fewer inmates
            if ("Poor".equals(prisoner.getHealthStatus()) || "Critical".equals(prisoner.getHealthStatus())) {
                matchingCells.sort(Comparator.comparingInt(Cell::getCurrentOccupancy));
                return matchingCells.get(0);
            }

            // If behavior is problematic, prioritize cells with more space
            if ("Problematic".equals(prisoner.getBehaviorStatus()) || "Violent".equals(prisoner.getBehaviorStatus())) {
                matchingCells.sort(Comparator.comparing(cell ->
                        (double) cell.getCurrentOccupancy() / cell.getCapacity()));
                return matchingCells.get(0);
            }

            // Default: return first matching cell
            return matchingCells.get(0);
        }

        // If no exact match, look for closest appropriate level (never downgrade security)
        if ("Minimum".equals(securityLevel)) {
            // For minimum security, try medium or maximum if no minimum available
            List<Cell> mediumCells = availableCells.stream()
                    .filter(cell -> cell.getSecurityLevel().equalsIgnoreCase("Medium"))
                    .collect(Collectors.toList());

            if (!mediumCells.isEmpty()) {
                return mediumCells.get(0);
            }

            List<Cell> maximumCells = availableCells.stream()
                    .filter(cell -> cell.getSecurityLevel().equalsIgnoreCase("Maximum"))
                    .collect(Collectors.toList());

            if (!maximumCells.isEmpty()) {
                return maximumCells.get(0);
            }
        } else if ("Medium".equals(securityLevel)) {
            // For medium security, try maximum if no medium available
            List<Cell> maximumCells = availableCells.stream()
                    .filter(cell -> cell.getSecurityLevel().equalsIgnoreCase("Maximum"))
                    .collect(Collectors.toList());

            if (!maximumCells.isEmpty()) {
                return maximumCells.get(0);
            }
        }

        // Last resort: return first available cell regardless of security level
        // In a real system, this would likely raise an alert to prison staff
        System.out.println("Warning: No matching security level cells available for prisoner " +
                prisoner.getFirstName() + " " + prisoner.getLastName() +
                ". Assigning to first available cell.");
        return availableCells.get(0);
    }

    private String determinePrisonerSecurityLevel(Prisoner prisoner) {
        String crimeType = prisoner.getCrimeType().toLowerCase();
        String behaviorStatus = prisoner.getBehaviorStatus();

        // Default to medium security
        String securityLevel = "Medium";

        // Assess based on crime type
        if (crimeType.contains("murder") ||
                crimeType.contains("homicide") ||
                crimeType.contains("armed") ||
                crimeType.contains("terrorism") ||
                crimeType.contains("violent") ||
                crimeType.contains("sexual assault") ||
                crimeType.contains("kidnapping")) {
            securityLevel = "Maximum";
        } else if (crimeType.contains("theft") ||
                crimeType.contains("fraud") ||
                crimeType.contains("drug") ||
                crimeType.contains("assault") ||
                crimeType.contains("burglary")) {
            securityLevel = "Medium";
        } else {
            securityLevel = "Minimum";
        }

        // Adjust based on behavior
        if ("Violent".equals(behaviorStatus)) {
            securityLevel = "Maximum"; // Violent behavior always warrants maximum security
        } else if ("Problematic".equals(behaviorStatus) && "Minimum".equals(securityLevel)) {
            securityLevel = "Medium"; // Upgrade problematic prisoners from minimum to medium
        }

        return securityLevel;
    }

    public List<String> generateRiskReport(Prisoner prisoner) {
        List<String> risks = new ArrayList<>();

        // Check for health risks
        if ("Poor".equals(prisoner.getHealthStatus()) || "Critical".equals(prisoner.getHealthStatus())) {
            risks.add("Health risk: Prisoner requires regular medical attention");
        }

        // Check for behavior risks
        if ("Problematic".equals(prisoner.getBehaviorStatus())) {
            risks.add("Behavior risk: Prisoner has problematic behavior record");
        } else if ("Violent".equals(prisoner.getBehaviorStatus())) {
            risks.add("High risk: Prisoner has history of violent behavior");
        }

        // Check for sentence-related risks
        long remainingDays = (prisoner.getSentenceEnd().getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
        if (remainingDays < 30) {
            risks.add("Release risk: Prisoner is due for release in less than 30 days");
        }

        return risks;
    }
}