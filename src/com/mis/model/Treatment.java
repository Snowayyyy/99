package com.mis.model;

import java.time.LocalDate;

/**
 * Represents a treatment administered to an animal
 */
public class Treatment {
    private int id;
    private TreatmentType type;
    private String name;
    private String description;
    private LocalDate administrationDate;
    private LocalDate nextDueDate;
    private boolean administered;
    
    public Treatment() {
    }
    
    public Treatment(int id, TreatmentType type, String name, String description, 
                    LocalDate administrationDate, LocalDate nextDueDate) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.administrationDate = administrationDate;
        this.nextDueDate = nextDueDate;
        this.administered = false;
    }
    
    public boolean isOverdue() {
        return nextDueDate != null && LocalDate.now().isAfter(nextDueDate);
    }
    
    public void administer() {
        this.administered = true;
        this.administrationDate = LocalDate.now();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public TreatmentType getType() {
        return type;
    }
    
    public void setType(TreatmentType type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getAdministrationDate() {
        return administrationDate;
    }
    
    public void setAdministrationDate(LocalDate administrationDate) {
        this.administrationDate = administrationDate;
    }
    
    public LocalDate getNextDueDate() {
        return nextDueDate;
    }
    
    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
    
    public boolean isAdministered() {
        return administered;
    }
    
    public void setAdministered(boolean administered) {
        this.administered = administered;
    }
} 