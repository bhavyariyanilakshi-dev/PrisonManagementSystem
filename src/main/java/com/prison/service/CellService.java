package com.prison.service;

import com.prison.dao.CellDAO;
import com.prison.dao.PrisonerDAO;
import com.prison.model.Cell;
import com.prison.model.Prisoner;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CellService {
    private CellDAO cellDAO;
    private PrisonerDAO prisonerDAO;

    public CellService() {
        cellDAO = new CellDAO();
        prisonerDAO = new PrisonerDAO();
    }

    public boolean registerCell(Cell cell) {
        return cellDAO.addCell(cell);
    }

    public Cell getCell(int id) {
        return cellDAO.getCell(id);
    }

    public List<Cell> getAllCells() {
        return cellDAO.getAllCells();
    }

    public List<Cell> getAvailableCells() {
        return cellDAO.getAvailableCells();
    }

    public List<Cell> getCellsBySecurityLevel(String securityLevel) {
        return cellDAO.getCellsBySecurityLevel(securityLevel);
    }

    public List<Cell> getAvailableCellsBySecurityLevel(String securityLevel) {
        return cellDAO.getAvailableCellsBySecurityLevel(securityLevel);
    }

    public boolean updateCell(Cell cell) {
        return cellDAO.updateCell(cell);
    }

    public boolean deleteCell(int cellId) {
        // Check if any prisoners are assigned to this cell
        List<Prisoner> prisonersInCell = prisonerDAO.getPrisonersByCell(cellId);
        if (!prisonersInCell.isEmpty()) {
            return false;  // Can't delete a cell with prisoners in it
        }

        return cellDAO.deleteCell(cellId);
    }

    public int getTotalCells() {
        return cellDAO.getTotalCells();
    }

    public int getAvailableCellCount() {
        return cellDAO.getAvailableCells().size();
    }

    public Map<String, Integer> getCellOccupancyByBlock() {
        List<Cell> allCells = cellDAO.getAllCells();
        Map<String, Integer> occupancyMap = new HashMap<>();
        Map<String, Integer> capacityMap = new HashMap<>();

        // Calculate total occupancy and capacity per block
        for (Cell cell : allCells) {
            String block = cell.getBlockName();

            occupancyMap.put(block, occupancyMap.getOrDefault(block, 0) + cell.getCurrentOccupancy());
            capacityMap.put(block, capacityMap.getOrDefault(block, 0) + cell.getCapacity());
        }

        // Calculate occupancy percentage
        Map<String, Integer> percentageMap = new HashMap<>();
        for (String block : occupancyMap.keySet()) {
            int occupancy = occupancyMap.get(block);
            int capacity = capacityMap.get(block);
            int percentage = (capacity > 0) ? (occupancy * 100 / capacity) : 0;
            percentageMap.put(block, percentage);
        }

        return percentageMap;
    }

    public boolean isCellAvailable(int cellId) {
        Cell cell = cellDAO.getCell(cellId);
        return cell != null && cell.getCurrentOccupancy() < cell.getCapacity();
    }

    public boolean incrementOccupancy(int cellId) {
        return cellDAO.incrementOccupancy(cellId);
    }

    public boolean decrementOccupancy(int cellId) {
        return cellDAO.decrementOccupancy(cellId);
    }
}