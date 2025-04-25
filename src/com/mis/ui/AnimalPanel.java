package com.mis.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;

import com.mis.api.AnimalController;
import com.mis.model.Animal;
import com.mis.model.Box;
import com.mis.model.Owner;
import com.mis.util.Messages;

/**
 * Panel for managing animals
 */
public class AnimalPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private MainWindow mainWindow;
    private AnimalController animalController;
    
    private JTable animalTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton assignBoxButton;
    private JButton assignOwnerButton;
    private JButton treatmentsButton;
    
    /**
     * Constructor
     */
    public AnimalPanel(MainWindow mainWindow, AnimalController animalController) {
        this.mainWindow = mainWindow;
        this.animalController = animalController;
        
        initializeUI();
        refreshData();
    }
    
    /**
     * Initialize the UI
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create table model
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add columns to table model
        tableModel.addColumn("ID");
        tableModel.addColumn(Messages.getString("animal.name"));
        tableModel.addColumn(Messages.getString("animal.species"));
        tableModel.addColumn(Messages.getString("animal.breed"));
        tableModel.addColumn(Messages.getString("animal.birthdate"));
        tableModel.addColumn(Messages.getString("animal.gender"));
        tableModel.addColumn(Messages.getString("animal.size"));
        tableModel.addColumn(Messages.getString("animal.box"));
        tableModel.addColumn(Messages.getString("animal.owner"));
        tableModel.addColumn(Messages.getString("animal.vaccinations"));
        
        // Create table
        animalTable = new JTable(tableModel);
        animalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        animalTable.getTableHeader().setReorderingAllowed(false);
        
        // Create vaccination column renderer
        animalTable.getColumnModel().getColumn(9).setCellRenderer(new VaccinationCellRenderer());
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(animalTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton(Messages.getString("animal.add"));
        editButton = new JButton(Messages.getString("animal.edit"));
        deleteButton = new JButton(Messages.getString("animal.delete"));
        assignBoxButton = new JButton(Messages.getString("animal.assign.box"));
        assignOwnerButton = new JButton(Messages.getString("animal.assign.owner"));
        treatmentsButton = new JButton(Messages.getString("animal.manage.treatments"));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(assignBoxButton);
        buttonPanel.add(assignOwnerButton);
        buttonPanel.add(treatmentsButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> showAnimalDialog(false, null));
        editButton.addActionListener(e -> {
            int selectedRow = animalTable.getSelectedRow();
            if (selectedRow != -1) {
                int animalId = (int) animalTable.getValueAt(selectedRow, 0);
                try {
                    Animal animal = animalController.getAnimalById(animalId);
                    if (animal != null) {
                        showAnimalDialog(true, animal);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            Messages.getString("database.query.error") + ": " + ex.getMessage(),
                            Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un animal à modifier.",
                        Messages.getString("info"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = animalTable.getSelectedRow();
            if (selectedRow != -1) {
                int animalId = (int) animalTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        Messages.getString("animal.delete.confirm"),
                        Messages.getString("warning"), JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = animalController.deleteAnimal(animalId);
                        if (success) {
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Échec de la suppression de l'animal.",
                                    Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                Messages.getString("database.query.error") + ": " + ex.getMessage(),
                                Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un animal à supprimer.",
                        Messages.getString("info"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        assignBoxButton.addActionListener(e -> {
            int selectedRow = animalTable.getSelectedRow();
            if (selectedRow != -1) {
                int animalId = (int) animalTable.getValueAt(selectedRow, 0);
                try {
                    Animal animal = animalController.getAnimalById(animalId);
                    if (animal == null) {
                        JOptionPane.showMessageDialog(this,
                                Messages.getString("animal.error.notfound"),
                                Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    List<Box> suitableBoxes = animalController.getSuitableAvailableBoxes(animal);

                    if (suitableBoxes.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                Messages.getString("box.error.nosuitable") + " (" + animal.getSize() + ").",
                                Messages.getString("info"), JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Créer un ComboBox pour afficher les boxes appropriés
                    JComboBox<Box> boxCombo = new JComboBox<>(suitableBoxes.toArray(new Box[0]));
                    boxCombo.setRenderer(new BoxListCellRenderer()); // Pour afficher le nom du box

                    int result = JOptionPane.showConfirmDialog(this, boxCombo,
                            Messages.getString("box.select.for.animal") + " " + animal.getName(), 
                            JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        Box selectedBox = (Box) boxCombo.getSelectedItem();
                        if (selectedBox != null) {
                            boolean success = animalController.assignBox(animalId, selectedBox.getId());
                            if (success) {
                                refreshData(); // Mettre à jour le tableau des animaux
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        Messages.getString("box.assign.fail"),
                                        Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            Messages.getString("database.query.error") + ": " + ex.getMessage(),
                            Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un animal pour lui assigner un box.",
                        Messages.getString("info"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        assignOwnerButton.addActionListener(e -> {
            int selectedRow = animalTable.getSelectedRow();
            if (selectedRow != -1) {
                int animalId = (int) animalTable.getValueAt(selectedRow, 0);
                // Implémentation de l'assignation de propriétaire
                JOptionPane.showMessageDialog(this,
                        Messages.getString("feature.not.implemented"),
                        Messages.getString("info"), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        Messages.getString("animal.select.assign.owner"),
                        Messages.getString("info"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        treatmentsButton.addActionListener(e -> {
            int selectedRow = animalTable.getSelectedRow();
            if (selectedRow != -1) {
                int animalId = (int) animalTable.getValueAt(selectedRow, 0);
                mainWindow.showPanel("treatments");
                // Ideally filter treatments for this animal
            } else {
                JOptionPane.showMessageDialog(this,
                        Messages.getString("animal.select.manage.treatments"),
                        Messages.getString("info"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    /**
     * Refresh data in the table
     */
    public void refreshData() {
        tableModel.setRowCount(0);
        
        try {
            List<Animal> animals = animalController.getAllAnimals();
            
            for (Animal animal : animals) {
                Object[] rowData = new Object[11];
                rowData[0] = animal.getId();
                rowData[1] = animal.getName();
                rowData[2] = animal.getSpecies();
                rowData[3] = animal.getBreed();
                rowData[4] = animal.getBirthDate() != null ? animal.getBirthDate().toString() : "";
                rowData[5] = animal.getGender();
                
                // Size information - Traduire pour l'affichage
                String sizeValue = animal.getSize();
                String sizeDisplay = "";
                if (sizeValue != null) {
                    if ("Small".equals(sizeValue)) {
                        sizeDisplay = Messages.getString("animal.size.small");
                    } else if ("Medium".equals(sizeValue)) {
                        sizeDisplay = Messages.getString("animal.size.medium");
                    } else if ("Large".equals(sizeValue)) {
                        sizeDisplay = Messages.getString("animal.size.large");
                    } else {
                        sizeDisplay = sizeValue; // Afficher la valeur brute si non reconnue
                    }
                }
                rowData[6] = sizeDisplay;
                
                // Box information
                Box box = animal.getBox();
                rowData[7] = box != null ? box.getName() : "";
                
                // Owner information
                Owner owner = animal.getOwner();
                rowData[8] = owner != null ? owner.getFullName() : "";
                
                // Vaccination status
                rowData[9] = animal.isVaccinationUpToDate();
                
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    Messages.getString("error.loading.animals") + ": " + e.getMessage(),
                    Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Show the add/edit animal dialog
     */
    private void showAnimalDialog(boolean isEdit, Animal animal) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel(Messages.getString("animal.name") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        if (isEdit && animal != null) {
            nameField.setText(animal.getName());
        }
        panel.add(nameField, gbc);
        
        // Species
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel(Messages.getString("animal.species") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField speciesField = new JTextField(20);
        if (isEdit && animal != null) {
            speciesField.setText(animal.getSpecies());
        }
        panel.add(speciesField, gbc);
        
        // Breed
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel(Messages.getString("animal.breed") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField breedField = new JTextField(20);
        if (isEdit && animal != null && animal.getBreed() != null) {
            breedField.setText(animal.getBreed());
        }
        panel.add(breedField, gbc);
        
        // Birth Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel(Messages.getString("animal.birthdate") + " (" + Messages.getString("animal.birthdate.format") + "):"), gbc);
        
        gbc.gridx = 1;
        JTextField birthDateField = new JTextField(20);
        if (isEdit && animal != null && animal.getBirthDate() != null) {
            birthDateField.setText(animal.getBirthDate().toString());
        }
        panel.add(birthDateField, gbc);
        
        // Gender
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel(Messages.getString("animal.gender") + ":"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{
                Messages.getString("animal.gender.male"), 
                Messages.getString("animal.gender.female"), 
                Messages.getString("animal.gender.unknown")
        });
        
        if (isEdit && animal != null && animal.getGender() != null) {
            // Conversion de la valeur anglaise à la valeur française pour l'affichage
            String genderFr = animal.getGender();
            if ("Male".equals(animal.getGender())) {
                genderFr = Messages.getString("animal.gender.male");
            } else if ("Female".equals(animal.getGender())) {
                genderFr = Messages.getString("animal.gender.female");
            } else {
                genderFr = Messages.getString("animal.gender.unknown");
            }
            genderCombo.setSelectedItem(genderFr);
        }
        panel.add(genderCombo, gbc);
        
        // Size
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel(Messages.getString("animal.size") + ":"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> sizeCombo = new JComboBox<>(new String[]{
                Messages.getString("animal.size.small"), 
                Messages.getString("animal.size.medium"), 
                Messages.getString("animal.size.large")
        });
        
        if (isEdit && animal != null && animal.getSize() != null) {
            String sizeFr = animal.getSize();
            if ("Small".equals(animal.getSize())) {
                sizeFr = Messages.getString("animal.size.small");
            } else if ("Medium".equals(animal.getSize())) {
                sizeFr = Messages.getString("animal.size.medium");
            } else if ("Large".equals(animal.getSize())) {
                sizeFr = Messages.getString("animal.size.large");
            }
            sizeCombo.setSelectedItem(sizeFr);
        }
        panel.add(sizeCombo, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
                isEdit ? Messages.getString("animal.edit") : Messages.getString("animal.add"), 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String species = speciesField.getText().trim();
                String breed = breedField.getText().trim();
                String birthDateStr = birthDateField.getText().trim();
                
                // Conversion de la valeur française à la valeur anglaise pour le stockage
                String genderFr = (String) genderCombo.getSelectedItem();
                String gender = genderFr;
                if (Messages.getString("animal.gender.male").equals(genderFr)) {
                    gender = "Male";
                } else if (Messages.getString("animal.gender.female").equals(genderFr)) {
                    gender = "Female";
                } else {
                    gender = "Unknown";
                }
                
                // Conversion de la valeur française à la valeur anglaise pour le stockage
                String sizeFr = (String) sizeCombo.getSelectedItem();
                String size = sizeFr;
                if (Messages.getString("animal.size.small").equals(sizeFr)) {
                    size = "Small";
                } else if (Messages.getString("animal.size.medium").equals(sizeFr)) {
                    size = "Medium";
                } else if (Messages.getString("animal.size.large").equals(sizeFr)) {
                    size = "Large";
                }
                
                if (name.isEmpty() || species.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            Messages.getString("animal.validation.required"),
                            Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                LocalDate birthDate = null;
                if (!birthDateStr.isEmpty()) {
                    try {
                        birthDate = LocalDate.parse(birthDateStr);
                    } catch (DateTimeParseException e) {
                        JOptionPane.showMessageDialog(this,
                                Messages.getString("animal.validation.date"),
                                Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                boolean success;
                if (isEdit) {
                    animal.setName(name);
                    animal.setSpecies(species);
                    animal.setBreed(breed.isEmpty() ? null : breed);
                    animal.setBirthDate(birthDate);
                    animal.setGender(gender);
                    animal.setSize(size);
                    
                    success = animalController.updateAnimal(animal);
                } else {
                    Animal newAnimal = new Animal();
                    newAnimal.setName(name);
                    newAnimal.setSpecies(species);
                    newAnimal.setBreed(breed.isEmpty() ? null : breed);
                    newAnimal.setBirthDate(birthDate);
                    newAnimal.setGender(gender);
                    newAnimal.setSize(size);
                    
                    // Utiliser la méthode createAnimal qui renvoie un objet Animal
                    Animal createdAnimal = animalController.createAnimal(
                        newAnimal.getName(), 
                        newAnimal.getSpecies(), 
                        newAnimal.getBreed(), 
                        newAnimal.getBirthDate(), 
                        newAnimal.getGender(),
                        newAnimal.getSize()
                    );
                    success = createdAnimal != null;
                }
                
                if (success) {
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            Messages.getString("animal.operation.fail"),
                            Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        Messages.getString("database.query.error") + ": " + e.getMessage(),
                        Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Custom renderer for the vaccination status column
     */
    private class VaccinationCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value instanceof Boolean) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected((Boolean) value);
                checkBox.setHorizontalAlignment(JLabel.CENTER);
                
                if (isSelected) {
                    checkBox.setBackground(table.getSelectionBackground());
                    checkBox.setForeground(table.getSelectionForeground());
                } else {
                    checkBox.setBackground(table.getBackground());
                    checkBox.setForeground(table.getForeground());
                }
                
                return checkBox;
            }
            
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    
    // Renderer simple pour afficher le nom et la taille du Box dans le JComboBox
    private class BoxListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Box) {
                Box box = (Box) value;
                setText(box.getName() + " (" + (box.getSize() != null ? box.getSize() : Messages.getString("box.size.undefined")) + ")");
            }
            return this;
        }
    }
} 