package com.mis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage the database connection
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url = "jdbc:sqlite:animalMIS.db";
    
    private DatabaseConnection() {
        try {
            // Register JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Open connection
            this.connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Get singleton instance of database connection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                instance = new DatabaseConnection();
            }
        }
        return instance;
    }
    
    /**
     * Initialize the database tables if they don't exist
     */
    public void initializeDatabase() {
        try {
            // Create tables if they don't exist
            String createAnimalTable = "CREATE TABLE IF NOT EXISTS animals (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "species TEXT NOT NULL," +
                    "breed TEXT," +
                    "birth_date TEXT," +
                    "gender TEXT," +
                    "size TEXT," +
                    "owner_id INTEGER," +
                    "box_id INTEGER," +
                    "FOREIGN KEY (owner_id) REFERENCES owners(id)," +
                    "FOREIGN KEY (box_id) REFERENCES boxes(id)" +
                    ")";
            
            String createOwnerTable = "CREATE TABLE IF NOT EXISTS owners (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "first_name TEXT NOT NULL," +
                    "last_name TEXT NOT NULL," +
                    "email TEXT," +
                    "phone TEXT," +
                    "address TEXT" +
                    ")";
            
            String createBoxTable = "CREATE TABLE IF NOT EXISTS boxes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "location TEXT," +
                    "size TEXT," +
                    "status TEXT NOT NULL" +
                    ")";
            
            String createTreatmentTable = "CREATE TABLE IF NOT EXISTS treatments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "animal_id INTEGER NOT NULL," +
                    "type TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "administration_date TEXT," +
                    "next_due_date TEXT," +
                    "administered BOOLEAN DEFAULT 0," +
                    "FOREIGN KEY (animal_id) REFERENCES animals(id)" +
                    ")";
            
            connection.createStatement().execute(createOwnerTable);
            connection.createStatement().execute(createBoxTable);
            connection.createStatement().execute(createAnimalTable);
            connection.createStatement().execute(createTreatmentTable);

            // Add the 'size' column to the 'animals' table if it doesn't exist
            try {
                String addAnimalSizeColumn = "ALTER TABLE animals ADD COLUMN size TEXT";
                connection.createStatement().execute(addAnimalSizeColumn);
                System.out.println("Column 'size' added to 'animals' table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.err.println("Error adding 'size' column to animals (might already exist): " + e.getMessage());
                }
            }

            // Add the 'size' column to the 'boxes' table if it doesn't exist
            try {
                String addBoxSizeColumn = "ALTER TABLE boxes ADD COLUMN size TEXT";
                connection.createStatement().execute(addBoxSizeColumn);
                System.out.println("Column 'size' added to 'boxes' table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.err.println("Error adding 'size' column to boxes (might already exist): " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 