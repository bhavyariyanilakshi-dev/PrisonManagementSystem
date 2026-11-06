package com.prison.model;

public class Cell {
    private int id;
    private String blockName;
    private String cellNumber;
    private int capacity;
    private int currentOccupancy;
    private String securityLevel;

    // Constructors
    public Cell() {}

    public Cell(int id, String blockName, String cellNumber, int capacity,
                int currentOccupancy, String securityLevel) {
        this.id = id;
        this.blockName = blockName;
        this.cellNumber = cellNumber;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
        this.securityLevel = securityLevel;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getCellNumber() { return cellNumber; }
    public void setCellNumber(String cellNumber) { this.cellNumber = cellNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }

    public boolean isFull() {
        return currentOccupancy >= capacity;
    }

    @Override
    public String toString() {
        return blockName + "-" + cellNumber;
    }
}
