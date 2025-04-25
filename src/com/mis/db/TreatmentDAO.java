package com.mis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mis.model.Treatment;
import com.mis.model.TreatmentType;

/**
 * Data Access Object for Treatment entities
 */
public class TreatmentDAO {
    private Connection connection;
    
    public TreatmentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Save a new treatment to the database
     */
    public int save(int animalId, Treatment treatment) throws SQLException {
        String query = "INSERT INTO treatments (animal_id, type, name, description, " +
                      "administration_date, next_due_date, administered) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, animalId);
            stmt.setString(2, treatment.getType().toString());
            stmt.setString(3, treatment.getName());
            stmt.setString(4, treatment.getDescription());
            stmt.setString(5, treatment.getAdministrationDate() != null ? treatment.getAdministrationDate().toString() : null);
            stmt.setString(6, treatment.getNextDueDate() != null ? treatment.getNextDueDate().toString() : null);
            stmt.setBoolean(7, treatment.isAdministered());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating treatment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int treatmentId = generatedKeys.getInt(1);
                    treatment.setId(treatmentId);
                    return treatmentId;
                } else {
                    throw new SQLException("Creating treatment failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get a treatment by ID
     */
    public Treatment getById(int id) throws SQLException {
        String query = "SELECT * FROM treatments WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTreatment(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get all treatments for a specific animal
     */
    public List<Treatment> getAllByAnimalId(int animalId) throws SQLException {
        String query = "SELECT * FROM treatments WHERE animal_id = ?";
        List<Treatment> treatments = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, animalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    treatments.add(mapResultSetToTreatment(rs));
                }
            }
        }
        
        return treatments;
    }
    
    /**
     * Update a treatment in the database
     */
    public boolean update(Treatment treatment) throws SQLException {
        String query = "UPDATE treatments SET type = ?, name = ?, description = ?, " +
                      "administration_date = ?, next_due_date = ?, administered = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, treatment.getType().toString());
            stmt.setString(2, treatment.getName());
            stmt.setString(3, treatment.getDescription());
            stmt.setString(4, treatment.getAdministrationDate() != null ? treatment.getAdministrationDate().toString() : null);
            stmt.setString(5, treatment.getNextDueDate() != null ? treatment.getNextDueDate().toString() : null);
            stmt.setBoolean(6, treatment.isAdministered());
            stmt.setInt(7, treatment.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete a treatment from the database
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM treatments WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete all treatments for a specific animal
     */
    public boolean deleteAllByAnimalId(int animalId) throws SQLException {
        String query = "DELETE FROM treatments WHERE animal_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, animalId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map a ResultSet row to a Treatment object
     */
    private Treatment mapResultSetToTreatment(ResultSet rs) throws SQLException {
        Treatment treatment = new Treatment();
        treatment.setId(rs.getInt("id"));
        treatment.setType(TreatmentType.valueOf(rs.getString("type")));
        treatment.setName(rs.getString("name"));
        treatment.setDescription(rs.getString("description"));
        
        String adminDateStr = rs.getString("administration_date");
        if (adminDateStr != null && !adminDateStr.isEmpty()) {
            treatment.setAdministrationDate(LocalDate.parse(adminDateStr));
        }
        
        String nextDueDateStr = rs.getString("next_due_date");
        if (nextDueDateStr != null && !nextDueDateStr.isEmpty()) {
            treatment.setNextDueDate(LocalDate.parse(nextDueDateStr));
        }
        
        treatment.setAdministered(rs.getBoolean("administered"));
        
        return treatment;
    }
} 