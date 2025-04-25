package com.mis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mis.model.Animal;
import com.mis.model.Box;
import com.mis.model.Owner;
import com.mis.model.Treatment;

/**
 * Data Access Object for Animal entities
 */
public class AnimalDAO {
    private Connection connection;
    private TreatmentDAO treatmentDAO;
    private BoxDAO boxDAO;
    private OwnerDAO ownerDAO;
    
    public AnimalDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.treatmentDAO = new TreatmentDAO();
        this.boxDAO = new BoxDAO();
        this.ownerDAO = new OwnerDAO();
    }
    
    /**
     * Save a new animal to the database
     */
    public int save(Animal animal) throws SQLException {
        String query = "INSERT INTO animals (name, species, breed, birth_date, gender, size, owner_id, box_id) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, animal.getName());
            stmt.setString(2, animal.getSpecies());
            stmt.setString(3, animal.getBreed());
            stmt.setString(4, animal.getBirthDate() != null ? animal.getBirthDate().toString() : null);
            stmt.setString(5, animal.getGender());
            stmt.setString(6, animal.getSize());
            
            // Set owner ID if available
            if (animal.getOwner() != null) {
                stmt.setInt(7, animal.getOwner().getId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            
            // Set box ID if available
            if (animal.getBox() != null) {
                stmt.setInt(8, animal.getBox().getId());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating animal failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int animalId = generatedKeys.getInt(1);
                    animal.setId(animalId);
                    
                    // Save treatments if available
                    if (animal.getTreatments() != null && !animal.getTreatments().isEmpty()) {
                        for (Treatment treatment : animal.getTreatments()) {
                            treatment.setId(treatmentDAO.save(animalId, treatment));
                        }
                    }
                    
                    return animalId;
                } else {
                    throw new SQLException("Creating animal failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get an animal by ID
     */
    public Animal getById(int id) throws SQLException {
        String query = "SELECT * FROM animals WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Animal animal = mapResultSetToAnimal(rs);
                    
                    // Get treatments
                    animal.setTreatments(treatmentDAO.getAllByAnimalId(id));
                    
                    return animal;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get all animals
     */
    public List<Animal> getAll() throws SQLException {
        String query = "SELECT * FROM animals";
        List<Animal> animals = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Animal animal = mapResultSetToAnimal(rs);
                
                // Get treatments
                animal.setTreatments(treatmentDAO.getAllByAnimalId(animal.getId()));
                
                animals.add(animal);
            }
        }
        
        return animals;
    }
    
    /**
     * Get all animals that have overdue treatments
     */
    public List<Animal> getAllWithOverdueTreatments() throws SQLException {
        List<Animal> allAnimals = getAll();
        List<Animal> animalsWithOverdueTreatments = new ArrayList<>();
        
        for (Animal animal : allAnimals) {
            if (!animal.getOverdueTreatments().isEmpty()) {
                animalsWithOverdueTreatments.add(animal);
            }
        }
        
        return animalsWithOverdueTreatments;
    }
    
    /**
     * Update an animal in the database
     */
    public boolean update(Animal animal) throws SQLException {
        String query = "UPDATE animals SET name = ?, species = ?, breed = ?, birth_date = ?, " +
                      "gender = ?, size = ?, owner_id = ?, box_id = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, animal.getName());
            stmt.setString(2, animal.getSpecies());
            stmt.setString(3, animal.getBreed());
            stmt.setString(4, animal.getBirthDate() != null ? animal.getBirthDate().toString() : null);
            stmt.setString(5, animal.getGender());
            stmt.setString(6, animal.getSize());
            
            // Set owner ID if available
            if (animal.getOwner() != null) {
                stmt.setInt(7, animal.getOwner().getId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            
            // Set box ID if available
            if (animal.getBox() != null) {
                stmt.setInt(8, animal.getBox().getId());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(9, animal.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete an animal from the database
     */
    public boolean delete(int id) throws SQLException {
        // First delete all treatments for this animal
        treatmentDAO.deleteAllByAnimalId(id);
        
        String query = "DELETE FROM animals WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map a ResultSet row to an Animal object
     */
    private Animal mapResultSetToAnimal(ResultSet rs) throws SQLException {
        Animal animal = new Animal();
        animal.setId(rs.getInt("id"));
        animal.setName(rs.getString("name"));
        animal.setSpecies(rs.getString("species"));
        animal.setBreed(rs.getString("breed"));
        
        String birthDateStr = rs.getString("birth_date");
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            animal.setBirthDate(LocalDate.parse(birthDateStr));
        }
        
        animal.setGender(rs.getString("gender"));
        animal.setSize(rs.getString("size"));
        
        // Get owner if available
        int ownerId = rs.getInt("owner_id");
        if (!rs.wasNull()) {
            Owner owner = ownerDAO.getById(ownerId);
            animal.setOwner(owner);
        }
        
        // Get box if available
        int boxId = rs.getInt("box_id");
        if (!rs.wasNull()) {
            Box box = boxDAO.getById(boxId);
            animal.setBox(box);
        }
        
        return animal;
    }
} 