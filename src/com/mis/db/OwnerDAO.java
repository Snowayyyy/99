package com.mis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mis.model.Owner;

/**
 * Data Access Object for Owner entities
 */
public class OwnerDAO {
    private Connection connection;
    
    public OwnerDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Save a new owner to the database
     */
    public int save(Owner owner) throws SQLException {
        String query = "INSERT INTO owners (first_name, last_name, email, phone, address) " +
                       "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, owner.getFirstName());
            stmt.setString(2, owner.getLastName());
            stmt.setString(3, owner.getEmail());
            stmt.setString(4, owner.getPhone());
            stmt.setString(5, owner.getAddress());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating owner failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int ownerId = generatedKeys.getInt(1);
                    owner.setId(ownerId);
                    return ownerId;
                } else {
                    throw new SQLException("Creating owner failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get an owner by ID
     */
    public Owner getById(int id) throws SQLException {
        String query = "SELECT * FROM owners WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOwner(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get all owners
     */
    public List<Owner> getAll() throws SQLException {
        String query = "SELECT * FROM owners";
        List<Owner> owners = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                owners.add(mapResultSetToOwner(rs));
            }
        }
        
        return owners;
    }
    
    /**
     * Update an owner in the database
     */
    public boolean update(Owner owner) throws SQLException {
        String query = "UPDATE owners SET first_name = ?, last_name = ?, email = ?, " +
                      "phone = ?, address = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, owner.getFirstName());
            stmt.setString(2, owner.getLastName());
            stmt.setString(3, owner.getEmail());
            stmt.setString(4, owner.getPhone());
            stmt.setString(5, owner.getAddress());
            stmt.setInt(6, owner.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete an owner from the database
     */
    public boolean delete(int id) throws SQLException {
        // First, update any animals owned by this owner to have no owner
        String updateAnimalsQuery = "UPDATE animals SET owner_id = NULL WHERE owner_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateAnimalsQuery)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        
        // Then delete the owner
        String query = "DELETE FROM owners WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map a ResultSet row to an Owner object
     */
    private Owner mapResultSetToOwner(ResultSet rs) throws SQLException {
        Owner owner = new Owner();
        owner.setId(rs.getInt("id"));
        owner.setFirstName(rs.getString("first_name"));
        owner.setLastName(rs.getString("last_name"));
        owner.setEmail(rs.getString("email"));
        owner.setPhone(rs.getString("phone"));
        owner.setAddress(rs.getString("address"));
        
        return owner;
    }
} 