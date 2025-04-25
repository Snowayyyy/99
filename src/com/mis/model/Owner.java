package com.mis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an animal owner
 */
public class Owner {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private List<Animal> animals;
    
    public Owner() {
        this.animals = new ArrayList<>();
    }
    
    public Owner(int id, String firstName, String lastName, String email, String phone, String address) {
        this();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    public void addAnimal(Animal animal) {
        this.animals.add(animal);
        animal.setOwner(this);
    }
    
    public void removeAnimal(Animal animal) {
        this.animals.remove(animal);
        if (animal.getOwner() == this) {
            animal.setOwner(null);
        }
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public List<Animal> getAnimals() {
        return animals;
    }
    
    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }
} 