package com.mis.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mis.api.AnimalController;
import com.mis.db.DatabaseConnection;
import com.mis.model.Animal;
import com.mis.util.Messages;

/**
 * Main application window
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private AnimalController animalController;
    
    private AnimalPanel animalPanel;
    private BoxPanel boxPanel;
    private TreatmentPanel treatmentPanel;
    private OwnerPanel ownerPanel;
    
    public MainWindow() {
        initializeDatabase();
        initializeControllers();
        initializeUI();
        checkOverdueTreatments();
    }
    
    /**
     * Initialize the database
     */
    private void initializeDatabase() {
        try {
            DatabaseConnection.getInstance().initializeDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    Messages.getString("database.connection.error") + ": " + e.getMessage(),
                    Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    /**
     * Initialize controllers
     */
    private void initializeControllers() {
        this.animalController = new AnimalController();
    }
    
    /**
     * Initialize the UI
     */
    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Définition des textes des boutons de dialogue standard
        UIManager.put("OptionPane.okButtonText", Messages.getString("ok"));
        UIManager.put("OptionPane.cancelButtonText", Messages.getString("cancel"));
        UIManager.put("OptionPane.yesButtonText", Messages.getString("yes"));
        UIManager.put("OptionPane.noButtonText", Messages.getString("no"));
        
        setTitle(Messages.getString("application.title"));
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create content panel with card layout
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // Menu de configuration
        JMenu configMenu = new JMenu(Messages.getString("menu.config"));
        JMenuItem dbMenuItem = new JMenuItem(Messages.getString("menu.db.local"));
        JMenuItem apiMenuItem = new JMenuItem(Messages.getString("menu.api.external"));
        
        // Listeners pour le menu de configuration
        dbMenuItem.addActionListener(e -> {
            // Implémentation à venir
            JOptionPane.showMessageDialog(this, Messages.getString("config.db.activated"));
        });
        
        apiMenuItem.addActionListener(e -> {
            String apiUrl = JOptionPane.showInputDialog(this, Messages.getString("config.api.url.prompt"), "http://");
            if (apiUrl != null && !apiUrl.isEmpty()) {
                // Implémentation à venir
                JOptionPane.showMessageDialog(this, Messages.getString("config.api.activated") + ": " + apiUrl);
            }
        });
        
        configMenu.add(dbMenuItem);
        configMenu.add(apiMenuItem);
        
        // Animals menu
        JMenu animalMenu = new JMenu(Messages.getString("menu.animals"));
        JMenuItem animalListItem = new JMenuItem(Messages.getString("menu.animals.list"));
        animalListItem.addActionListener(e -> showPanel("animals"));
        animalMenu.add(animalListItem);
        
        // Boxes menu
        JMenu boxMenu = new JMenu(Messages.getString("menu.boxes"));
        JMenuItem boxListItem = new JMenuItem(Messages.getString("menu.boxes.list"));
        boxListItem.addActionListener(e -> showPanel("boxes"));
        boxMenu.add(boxListItem);
        
        // Treatments menu
        JMenu treatmentMenu = new JMenu(Messages.getString("menu.treatments"));
        JMenuItem treatmentListItem = new JMenuItem(Messages.getString("menu.treatments.list"));
        treatmentListItem.addActionListener(e -> showPanel("treatments"));
        treatmentMenu.add(treatmentListItem);
        
        // Owners menu
        JMenu ownerMenu = new JMenu(Messages.getString("menu.owners"));
        JMenuItem ownerListItem = new JMenuItem(Messages.getString("menu.owners.list"));
        ownerListItem.addActionListener(e -> showPanel("owners"));
        ownerMenu.add(ownerListItem);
        
        menuBar.add(animalMenu);
        menuBar.add(boxMenu);
        menuBar.add(treatmentMenu);
        menuBar.add(ownerMenu);
        menuBar.add(configMenu);
        
        setJMenuBar(menuBar);
        
        // Create panels
        animalPanel = new AnimalPanel(this, animalController);
        boxPanel = new BoxPanel(this);
        treatmentPanel = new TreatmentPanel(this, animalController);
        ownerPanel = new OwnerPanel(this);
        
        // Add panels to card layout
        contentPanel.add(animalPanel, "animals");
        contentPanel.add(boxPanel, "boxes");
        contentPanel.add(treatmentPanel, "treatments");
        contentPanel.add(ownerPanel, "owners");
        
        // Show the animal panel by default
        cardLayout.show(contentPanel, "animals");
        
        // Add content panel to frame
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    // Close database connection
                    DatabaseConnection.getInstance().getConnection().close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Show the specified panel
     */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        
        // Refresh the panel data
        if (panelName.equals("animals")) {
            animalPanel.refreshData();
        } else if (panelName.equals("boxes")) {
            boxPanel.refreshData();
        } else if (panelName.equals("treatments")) {
            treatmentPanel.refreshData();
        } else if (panelName.equals("owners")) {
            ownerPanel.refreshData();
        }
    }
    
    /**
     * Check for animals with overdue treatments
     */
    private void checkOverdueTreatments() {
        try {
            List<Animal> animalsWithOverdueTreatments = animalController.getAnimalsWithOverdueTreatments();
            
            if (!animalsWithOverdueTreatments.isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append(Messages.getString("treatment.overdue")).append("\n\n");
                
                for (Animal animal : animalsWithOverdueTreatments) {
                    message.append(animal.getName()).append(" (").append(animal.getSpecies()).append(")\n");
                    animal.getOverdueTreatments().forEach(t -> 
                        message.append("- ").append(t.getName())
                              .append(" (").append(t.getType()).append(") ")
                              .append(Messages.getString("treatment.due.on"))
                              .append(" ").append(t.getNextDueDate()).append("\n")
                    );
                    message.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, message.toString(), 
                        Messages.getString("warning"), JOptionPane.WARNING_MESSAGE);
                
                // Show treatments panel
                showPanel("treatments");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                    Messages.getString("treatment.error.overdue.check") + ": " + e.getMessage(),
                    Messages.getString("database.error"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
} 