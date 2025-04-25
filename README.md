# Animal Management System

## Description

This application provides a comprehensive system for managing animals in a veterinary clinic or animal shelter environment. The system focuses on:

- Management of animals with their details (species, breed, etc.)
- Tracking of animal housing/boxes
- Management of animal owners
- Vaccination and treatment tracking with reminders for overdue treatments

## Key Features

- **Animal Management**: Add, edit, and delete animal records with detailed information.
- **Box Assignment**: Assign animals to specific boxes/housing units and track their status.
- **Owner Management**: Maintain owner details and associate them with their animals.
- **Treatment Tracking**: Record vaccinations and other treatments, with automatic tracking of due dates.
- **Notifications**: Alerts for overdue vaccinations and treatments.
- **Data Persistence**: All information is stored in a SQLite database.

## Technical Details

- Built in Java with Swing UI
- Database: SQLite (file-based)
- Architecture: Layered architecture with Model-View-Controller pattern
  - Model: Data entities
  - DAOs: Data access objects for CRUD operations
  - Controllers: Business logic
  - UI: Swing interface

## Requirements

- Java 11 or higher
- SQLite JDBC driver (included in the lib folder)

## Installation

1. Clone the repository
2. Ensure Java is installed on your system
3. Run the application using: `java -jar animalMIS.jar`

## Database Schema

The application uses the following database tables:

- **animals**: Stores animal information
- **boxes**: Stores housing unit information
- **owners**: Stores owner information
- **treatments**: Stores treatment/vaccination records

## Usage

The application provides a user-friendly interface with the following main sections:

1. **Animals**: View and manage animal records
2. **Boxes**: View and manage housing units
3. **Treatments**: View and manage vaccinations and treatments
4. **Owners**: View and manage owner information

## Future Enhancements

- Mobile application for field use
- Web interface for owner access to treatment information
- Reporting functionality
- Appointment scheduling 

JMenu configMenu = new JMenu("Configuration");
JMenuItem dbMenuItem = new JMenuItem("Utiliser Base de Données Locale");
JMenuItem apiMenuItem = new JMenuItem("Utiliser API Externe");

dbMenuItem.addActionListener(e -> {
    DAOFactory.setDaoType(DAOFactory.DB_TYPE);
    JOptionPane.showMessageDialog(this, "Base de données locale activée");
});

apiMenuItem.addActionListener(e -> {
    String apiUrl = JOptionPane.showInputDialog(this, "URL de l'API:", "http://");
    if (apiUrl != null && !apiUrl.isEmpty()) {
        DAOFactory.setApiUrl(apiUrl);
        DAOFactory.setDaoType(DAOFactory.API_TYPE);
        JOptionPane.showMessageDialog(this, "API externe activée");
    }
});

configMenu.add(dbMenuItem);
configMenu.add(apiMenuItem);
menuBar.add(configMenu);

setTitle("Système de Gestion Animale");

JMenu animalMenu = new JMenu("Animaux");
JMenu boxMenu = new JMenu("Boxes");
JMenu treatmentMenu = new JMenu("Traitements");
JMenu ownerMenu = new JMenu("Propriétaires");

JMenuItem animalListItem = new JMenuItem("Liste des Animaux");
JMenuItem boxListItem = new JMenuItem("Liste des Boxes");
JMenuItem treatmentListItem = new JMenuItem("Liste des Traitements");
JMenuItem ownerListItem = new JMenuItem("Liste des Propriétaires");

try {
    // Existing code
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, 
        Messages.getString("error.loading.animals") + ": " + e.getMessage(), 
        Messages.getString("dialog.database.error"), 
        JOptionPane.ERROR_MESSAGE);
} 

// Colonnes du tableau
tableModel.addColumn("ID");
tableModel.addColumn("Nom");
tableModel.addColumn("Espèce");
tableModel.addColumn("Race");
tableModel.addColumn("Date de Naissance");
tableModel.addColumn("Genre");
tableModel.addColumn("Box");
tableModel.addColumn("Propriétaire");
tableModel.addColumn("Vaccinations à jour");

// Boutons
addButton = new JButton("Ajouter Animal");
editButton = new JButton("Modifier Animal");
deleteButton = new JButton("Supprimer Animal");
assignBoxButton = new JButton("Assigner Box");
assignOwnerButton = new JButton("Assigner Propriétaire");
treatmentsButton = new JButton("Gérer Traitements"); 

UIManager.put("OptionPane.okButtonText", "OK");
UIManager.put("OptionPane.cancelButtonText", "Annuler");
UIManager.put("OptionPane.yesButtonText", "Oui");
UIManager.put("OptionPane.noButtonText", "Non"); 