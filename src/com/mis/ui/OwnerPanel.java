package com.mis.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.mis.db.OwnerDAO;
import com.mis.model.Animal;
import com.mis.model.Owner;
import com.mis.util.Messages;

/**
 * Panel for managing owners
 */
public class OwnerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private MainWindow mainWindow;
    private OwnerDAO ownerDAO;
    
    private JTable ownerTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewAnimalsButton;
    
    public OwnerPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.ownerDAO = new OwnerDAO();
        
        setLayout(new BorderLayout());
        
        // Create table model
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Address");
        tableModel.addColumn("Animal Count");
        
        // Create table
        ownerTable = new JTable(tableModel);
        ownerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ownerTable.getTableHeader().setReorderingAllowed(false);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(ownerTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton(Messages.getString("owner.add"));
        editButton = new JButton(Messages.getString("owner.edit"));
        deleteButton = new JButton(Messages.getString("owner.delete"));
        viewAnimalsButton = new JButton(Messages.getString("owner.view.animals"));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewAnimalsButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddEditDialog(null);
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ownerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int ownerId = (int) tableModel.getValueAt(selectedRow, 0);
                    try {
                        Owner owner = ownerDAO.getById(ownerId);
                        showAddEditDialog(owner);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(OwnerPanel.this,
                                Messages.getString("owner.error.loading") + ": " + ex.getMessage(),
                                Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(OwnerPanel.this,
                            Messages.getString("owner.select.edit"),
                            Messages.getString("owner.select.required.title"), JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ownerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int ownerId = (int) tableModel.getValueAt(selectedRow, 0);
                    int animalCount = (int) tableModel.getValueAt(selectedRow, 6);
                    
                    if (animalCount > 0) {
                        int confirm = JOptionPane.showConfirmDialog(OwnerPanel.this,
                                Messages.getString("owner.delete.warning.animals.prefix") + " " + animalCount + " " + Messages.getString("owner.delete.warning.animals.suffix"),
                                Messages.getString("warning"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        
                        if (confirm != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    
                    int confirm = JOptionPane.showConfirmDialog(OwnerPanel.this,
                            Messages.getString("owner.delete.confirm"),
                            Messages.getString("owner.delete.confirm.title"), JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            boolean success = ownerDAO.delete(ownerId);
                            if (success) {
                                refreshData();
                            } else {
                                JOptionPane.showMessageDialog(OwnerPanel.this,
                                        Messages.getString("owner.delete.fail"),
                                        Messages.getString("owner.delete.error.title"), JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(OwnerPanel.this,
                                    Messages.getString("owner.error.deleting") + ": " + ex.getMessage(),
                                    Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(OwnerPanel.this,
                            Messages.getString("owner.select.delete"),
                            Messages.getString("owner.select.required.title"), JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        viewAnimalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ownerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int ownerId = (int) tableModel.getValueAt(selectedRow, 0);
                    int animalCount = (int) tableModel.getValueAt(selectedRow, 6);
                    
                    if (animalCount > 0) {
                        try {
                            Owner owner = ownerDAO.getById(ownerId);
                            showOwnerAnimalsDialog(owner);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(OwnerPanel.this,
                                    Messages.getString("owner.error.loading.animals") + ": " + ex.getMessage(),
                                    Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(OwnerPanel.this,
                                Messages.getString("owner.info.noanimals"),
                                Messages.getString("owner.info.noanimals.title"), JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(OwnerPanel.this,
                            Messages.getString("owner.select.view.animals"),
                            Messages.getString("owner.select.required.title"), JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        // Load data
        refreshData();
    }
    
    /**
     * Refresh data in the table
     */
    public void refreshData() {
        tableModel.setRowCount(0);
        
        try {
            List<Owner> owners = ownerDAO.getAll();
            
            for (Owner owner : owners) {
                Object[] rowData = new Object[7];
                rowData[0] = owner.getId();
                rowData[1] = owner.getFirstName();
                rowData[2] = owner.getLastName();
                rowData[3] = owner.getEmail();
                rowData[4] = owner.getPhone();
                rowData[5] = owner.getAddress();
                rowData[6] = owner.getAnimals().size();
                
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    Messages.getString("owner.error.loading.all") + ": " + e.getMessage(),
                    Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Show the add/edit owner dialog
     */
    private void showAddEditDialog(Owner owner) {
        boolean isEdit = owner != null;
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel(Messages.getString("owner.firstname") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField firstNameField = new JTextField(20);
        if (isEdit) {
            firstNameField.setText(owner.getFirstName());
        }
        panel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel(Messages.getString("owner.lastname") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField lastNameField = new JTextField(20);
        if (isEdit) {
            lastNameField.setText(owner.getLastName());
        }
        panel.add(lastNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel(Messages.getString("owner.email") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        if (isEdit && owner.getEmail() != null) {
            emailField.setText(owner.getEmail());
        }
        panel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel(Messages.getString("owner.phone") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(20);
        if (isEdit && owner.getPhone() != null) {
            phoneField.setText(owner.getPhone());
        }
        panel.add(phoneField, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel(Messages.getString("owner.address") + ":"), gbc);
        
        gbc.gridx = 1;
        JTextField addressField = new JTextField(20);
        if (isEdit && owner.getAddress() != null) {
            addressField.setText(owner.getAddress());
        }
        panel.add(addressField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
                isEdit ? Messages.getString("owner.edit") : Messages.getString("owner.add"),
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            Messages.getString("owner.validation.required"),
                            Messages.getString("validation.error.title"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (isEdit) {
                    // Update existing owner
                    owner.setFirstName(firstName);
                    owner.setLastName(lastName);
                    owner.setEmail(email.isEmpty() ? null : email);
                    owner.setPhone(phone.isEmpty() ? null : phone);
                    owner.setAddress(address.isEmpty() ? null : address);
                    
                    boolean success = ownerDAO.update(owner);
                    if (success) {
                        refreshData();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                Messages.getString("owner.update.fail"),
                                Messages.getString("owner.update.error.title"), JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Create new owner
                    Owner newOwner = new Owner();
                    newOwner.setFirstName(firstName);
                    newOwner.setLastName(lastName);
                    newOwner.setEmail(email.isEmpty() ? null : email);
                    newOwner.setPhone(phone.isEmpty() ? null : phone);
                    newOwner.setAddress(address.isEmpty() ? null : address);
                    
                    int ownerId = ownerDAO.save(newOwner);
                    if (ownerId > 0) {
                        refreshData();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                Messages.getString("owner.add.fail"),
                                Messages.getString("owner.add.error.title"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        Messages.getString("database.error.generic") + ": " + e.getMessage(),
                        Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Show dialog with owner's animals
     */
    private void showOwnerAnimalsDialog(Owner owner) {
        DefaultTableModel animalTableModel = new DefaultTableModel();
        animalTableModel.addColumn(Messages.getString("animal.id"));
        animalTableModel.addColumn(Messages.getString("animal.name"));
        animalTableModel.addColumn(Messages.getString("animal.species"));
        animalTableModel.addColumn(Messages.getString("animal.breed"));
        animalTableModel.addColumn(Messages.getString("animal.box"));
        animalTableModel.addColumn(Messages.getString("animal.vaccinations.uptodate"));
        
        for (Animal animal : owner.getAnimals()) {
            Object[] rowData = new Object[6];
            rowData[0] = animal.getId();
            rowData[1] = animal.getName();
            rowData[2] = animal.getSpecies();
            rowData[3] = animal.getBreed();
            rowData[4] = animal.getBox() != null ? animal.getBox().getName() : "";
            rowData[5] = animal.isVaccinationUpToDate() ? Messages.getString("yes") : Messages.getString("no");
            
            animalTableModel.addRow(rowData);
        }
        
        JTable animalTable = new JTable(animalTableModel);
        animalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        animalTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(animalTable);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane,
                Messages.getString("owner.animals.owned.by") + " " + owner.getFullName(),
                JOptionPane.INFORMATION_MESSAGE);
    }
} 