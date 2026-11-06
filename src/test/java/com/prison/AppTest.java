package com.prison;

import com.prison.config.DBConnection;
import com.prison.model.Cell;
import com.prison.model.Prisoner;
import com.prison.service.CellService;
import com.prison.service.PrisonerService;
import com.prison.utils.AIUtility;

import java.util.Date;
import java.util.List;

public class AppTest {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Running Prison Management System Verification Tests");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: DB Connection Test
        System.out.print("[TEST 1] Testing Database Connection... ");
        boolean connected = DBConnection.testConnection();
        if (connected) {
            System.out.println("PASSED (Embedded/MySQL active)");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        // Test 2: Cell Service & Seed Data
        System.out.print("[TEST 2] Verifying Seed Cells... ");
        CellService cellService = new CellService();
        List<Cell> cells = cellService.getAllCells();
        if (cells != null && !cells.isEmpty()) {
            System.out.println("PASSED (Found " + cells.size() + " cells)");
            passed++;
        } else {
            System.out.println("FAILED (No cells found)");
            failed++;
        }

        // Test 3: Prisoner Service & Seed Data
        System.out.print("[TEST 3] Verifying Seed Prisoners... ");
        PrisonerService prisonerService = new PrisonerService();
        List<Prisoner> prisoners = prisonerService.getAllPrisoners();
        if (prisoners != null && !prisoners.isEmpty()) {
            System.out.println("PASSED (Found " + prisoners.size() + " prisoners)");
            passed++;
        } else {
            System.out.println("FAILED (No prisoners found)");
            failed++;
        }

        // Test 4: AI Cell Recommendation
        System.out.print("[TEST 4] Testing AI Cell Recommendation... ");
        AIUtility aiUtility = new AIUtility();
        Prisoner testInmate = new Prisoner();
        testInmate.setFirstName("Test");
        testInmate.setLastName("Inmate");
        testInmate.setCrimeType("Armed Robbery");
        testInmate.setBehaviorStatus("Violent");
        testInmate.setHealthStatus("Good");

        Cell recommendedCell = aiUtility.recommendCell(testInmate);
        if (recommendedCell != null) {
            System.out.println("PASSED (Recommended cell: " + recommendedCell.getBlockName() + "-" + recommendedCell.getCellNumber() + " with security: " + recommendedCell.getSecurityLevel() + ")");
            passed++;
        } else {
            System.out.println("FAILED (No cell recommended)");
            failed++;
        }

        // Test 5: Prisoner Registration & Transfer
        System.out.print("[TEST 5] Testing Prisoner Registration and Transfer... ");
        Prisoner newPrisoner = new Prisoner();
        newPrisoner.setFirstName("Alex");
        newPrisoner.setLastName("Mercer");
        newPrisoner.setDob(new Date(700000000000L));
        newPrisoner.setGender("Male");
        newPrisoner.setCrimeType("Burglary");
        newPrisoner.setSentenceStart(new Date());
        newPrisoner.setSentenceEnd(new Date(System.currentTimeMillis() + 86400000L * 365));
        newPrisoner.setCellId(cells.get(0).getId());
        newPrisoner.setHealthStatus("Good");
        newPrisoner.setBehaviorStatus("Normal");

        boolean regSuccess = prisonerService.registerPrisoner(newPrisoner);
        if (regSuccess && newPrisoner.getId() > 0) {
            // Test transfer
            if (cells.size() > 1) {
                prisonerService.transferPrisonerToCell(newPrisoner.getId(), cells.get(1).getId());
            }
            // Test update health
            prisonerService.updateHealthStatus(newPrisoner.getId(), "Fair");
            // Test release
            prisonerService.releasePrisoner(newPrisoner.getId());
            System.out.println("PASSED");
            passed++;
        } else {
            System.out.println("FAILED");
            failed++;
        }

        System.out.println("==================================================");
        System.out.println("Results: " + passed + " Passed, " + failed + " Failed");
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
