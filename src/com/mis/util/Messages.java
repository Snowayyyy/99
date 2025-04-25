package com.mis.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Classe pour gérer l'internationalisation des messages dans l'application
 */
public class Messages {
    private static final String BUNDLE_NAME = "com.mis.resources.messages";
    private static ResourceBundle RESOURCE_BUNDLE;
    
    static {
        // Initialiser avec la locale française
        Locale.setDefault(Locale.FRENCH);
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.FRENCH);
    }
    
    private Messages() {
        // Constructeur privé pour éviter l'instanciation
    }
    
    /**
     * Récupère une chaîne traduite du bundle de ressources
     * @param key La clé de la chaîne à récupérer
     * @return La chaîne traduite ou une chaîne d'erreur si la clé n'est pas trouvée
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    /**
     * Change la locale utilisée par l'application
     * @param locale La nouvelle locale à utiliser
     */
    public static void setLocale(Locale locale) {
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }
} 