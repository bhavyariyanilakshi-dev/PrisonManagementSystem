package com.prison.service;

import com.prison.dao.PrisonerDAO;
import com.prison.dao.CellDAO;
import com.prison.model.Prisoner;
import com.prison.model.Cell;
import com.prison.utils.AIUtility;

import java.util.List;
import java.util.Date;

public class PrisonerService {
    private PrisonerDAO prisonerDAO;
    private CellDAO cellDAO;
    private AIUtility aiUtility;

    public PrisonerService() {
        prisonerDAO = new PrisonerDAO();
        cellDAO = new CellDAO();
        aiUtility = new AIUtility();
    }

    public List<Prisoner> getAllPrisoners() {
        return prisonerDAO.getAllPrisoners();
    }

    public Prisoner getPrisoner(int id) {
        return prisonerDAO.getPrisoner(id);
    }

    public List<Prisoner> searchPrisonersByName(String name) {
        return prisonerDAO.searchPrisoners(name);
    }

    public List<Prisoner> getPrisonersByCell(int cellId) {
        return prisonerDAO.getPrisonersByCell(cellId);
    }

    public List<Prisoner> getPrisonersByCrimeType(String crimeType) {
        return prisonerDAO.getPrisonersByCrimeType(crimeType);
    }

    public boolean registerPrisoner(Prisoner prisoner) {
        // If AI cell allocation is desired, we could handle it here
        return prisonerDAO.addPrisoner(prisoner);
    }

    public boolean registerPrisonerWithAI(Prisoner prisoner) {
        Cell recommendedCell = aiUtility.recommendCell(prisoner);
        if (recommendedCell != null) {
            prisoner.setCellId(recommendedCell.getId());
            return prisonerDAO.addPrisoner(prisoner);
        }
        return false;
    }

    public boolean updatePrisoner(Prisoner prisoner) {
        return prisonerDAO.updatePrisoner(prisoner);
    }

    public boolean transferPrisonerToCell(int prisonerId, int newCellId) {
        // Get the current prisoner info
        Prisoner prisoner = prisonerDAO.getPrisoner(prisonerId);
        if (prisoner == null) {
            return false;
        }

        // Get the new cell info
        Cell newCell = cellDAO.getCell(newCellId);
        if (newCell == null || newCell.isFull()) {
            return false;
        }

        // Update prisoner with new cell
        int oldCellId = prisoner.getCellId();
        prisoner.setCellId(newCellId);

        return prisonerDAO.updatePrisonerCell(prisonerId, newCellId, oldCellId);
    }

    public boolean updateHealthStatus(int prisonerId, String newStatus) {
        return prisonerDAO.updateHealthStatus(prisonerId, newStatus);
    }

    public boolean updateBehaviorStatus(int prisonerId, String newStatus) {
        return prisonerDAO.updateBehaviorStatus(prisonerId, newStatus);
    }

    public boolean releasePrisoner(int id) {
        // In a real system, we might:
        // 1. Move to a released prisoners table
        // 2. Generate release documents
        // 3. Update cell occupancy

        // For now we just delete
        return prisonerDAO.deletePrisoner(id);
    }

    public int getTotalPrisoners() {
        return prisonerDAO.getTotalPrisoners();
    }

    public int getPrisonersReleasingInDays(int days) {
        // Calculate date that is 'days' from now
        long millisecondsInDay = 24 * 60 * 60 * 1000;
        Date futureDate = new Date(System.currentTimeMillis() + (days * millisecondsInDay));
        return prisonerDAO.getPrisonersReleasingByDate(futureDate);
    }
}