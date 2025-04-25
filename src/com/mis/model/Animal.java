package com.mis.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an animal in the system
 */
public class Animal {
    private int id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private String gender;
    private String size;
    private Box box;
    private Owner owner;
    private List<Treatment> treatments;
    
    public Animal() {
        this.treatments = new ArrayList<>();
    }
    
    public Animal(int id, String name, String species, String breed, LocalDate birthDate, String gender) {
        this();
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.gender = gender;
    }
    
    public boolean isVaccinationUpToDate() {
        for (Treatment treatment : treatments) {
            if (treatment.getType() == TreatmentType.VACCINE && 
                treatment.isOverdue()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isDewormingUpToDate() {
        for (Treatment treatment : treatments) {
            if (treatment.getType() == TreatmentType.DEWORMING && 
                treatment.isOverdue()) {
                return false;
            }
        }
        return true;
    }
    
    public List<Treatment> getOverdueTreatments() {
        List<Treatment> overdue = new ArrayList<>();
        for (Treatment treatment : treatments) {
            if (treatment.isOverdue()) {
                overdue.add(treatment);
            }
        }
        return overdue;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSpecies() {
        return species;
    }
    
    public void setSpecies(String species) {
        this.species = species;
    }
    
    public String getBreed() {
        return breed;
    }
    
    public void setBreed(String breed) {
        this.breed = breed;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public Box getBox() {
        return box;
    }
    
    public void setBox(Box box) {
        this.box = box;
    }
    
    public Owner getOwner() {
        return owner;
    }
    
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    
    public List<Treatment> getTreatments() {
        return treatments;
    }
    
    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }
    
    public void addTreatment(Treatment treatment) {
        this.treatments.add(treatment);
    }
} 