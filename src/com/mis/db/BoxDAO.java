package com.mis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mis.model.Box;
import com.mis.model.BoxStatus;

/**
 * Data Access Object for Box entities
 */
public class BoxDAO {
    private Connection connection;
    
    public BoxDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Save a new box to the database
     */
    public int save(Box box) throws SQLException {
        String query = "INSERT INTO boxes (name, location, size, status) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, box.getName());
            stmt.setString(2, box.getLocation());
            stmt.setString(3, box.getSize());
            stmt.setString(4, box.getStatus().toString());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating box failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int boxId = generatedKeys.getInt(1);
                    box.setId(boxId);
                    return boxId;
                } else {
                    throw new SQLException("Creating box failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get a box by ID
     */
    public Box getById(int id) throws SQLException {
        String query = "SELECT * FROM boxes WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBox(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get all boxes
     */
    public List<Box> getAll() throws SQLException {
        String query = "SELECT * FROM boxes";
        List<Box> boxes = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                boxes.add(mapResultSetToBox(rs));
            }
        }
        
        return boxes;
    }
    
    /**
     * Get all available boxes
     */
    public List<Box> getAllAvailable() throws SQLException {
        String query = "SELECT * FROM boxes WHERE status = ?";
        List<Box> boxes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, BoxStatus.AVAILABLE.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    boxes.add(mapResultSetToBox(rs));
                }
            }
        }
        
        return boxes;
    }
    
    /**
     * Get all available boxes.
     * @return A list of boxes with status AVAILABLE.
     * @throws SQLException If a database error occurs.
     */
    public List<Box> getAvailableBoxes() throws SQLException {
        String query = "SELECT * FROM boxes WHERE status = ?";
        List<Box> availableBoxes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, BoxStatus.AVAILABLE.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    availableBoxes.add(mapResultSetToBox(rs));
                }
            }
        }
        
        return availableBoxes;
    }
    
    /**
     * Update a box in the database
     */
    public boolean update(Box box) throws SQLException {
        String query = "UPDATE boxes SET name = ?, location = ?, size = ?, status = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, box.getName());
            stmt.setString(2, box.getLocation());
            stmt.setString(3, box.getSize());
            stmt.setString(4, box.getStatus().toString());
            stmt.setInt(5, box.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete a box from the database
     */
    public boolean delete(int id) throws SQLException {
        // First, update any animals that are in this box to have no box
        String updateAnimalsQuery = "UPDATE animals SET box_id = NULL WHERE box_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateAnimalsQuery)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        
        // Then delete the box
        String query = "DELETE FROM boxes WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map a ResultSet row to a Box object
     */
    private Box mapResultSetToBox(ResultSet rs) throws SQLException {
        Box box = new Box();
        box.setId(rs.getInt("id"));
        box.setName(rs.getString("name"));
        box.setLocation(rs.getString("location"));
        box.setSize(rs.getString("size"));
        box.setStatus(BoxStatus.valueOf(rs.getString("status")));
        
        return box;
    }
} 