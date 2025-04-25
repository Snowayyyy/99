package com.mis.model;

/**
 * Represents a box or housing unit for an animal
 */
public class Box {
    private int id;
    private String name;
    private String location;
    private String size;
    private BoxStatus status;
    private Animal currentAnimal;
    
    public Box() {
    }
    
    public Box(int id, String name, String location, String size) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.size = size;
        this.status = BoxStatus.AVAILABLE;
    }
    
    public boolean isAvailable() {
        return status == BoxStatus.AVAILABLE;
    }
    
    public void assignAnimal(Animal animal) {
        this.currentAnimal = animal;
        this.status = BoxStatus.OCCUPIED;
        animal.setBox(this);
    }
    
    public void releaseAnimal() {
        if (this.currentAnimal != null) {
            this.currentAnimal.setBox(null);
            this.currentAnimal = null;
            this.status = BoxStatus.AVAILABLE;
        }
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public BoxStatus getStatus() {
        return status;
    }
    
    public void setStatus(BoxStatus status) {
        this.status = status;
    }
    
    public Animal getCurrentAnimal() {
        return currentAnimal;
    }
    
    public void setCurrentAnimal(Animal currentAnimal) {
        this.currentAnimal = currentAnimal;
    }
} 