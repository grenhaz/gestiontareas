package org.obarcia.gestiontareas.components;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase para el manejo de los recursos.
 * 
 * @author obarcia
 */
public class Language
{
    private static ResourceBundle lang = null;
    
    /**
     * Devuelve los recursos de idiomas.
     * @return Instancia de los recursos.
     */
    public static synchronized ResourceBundle getResource()
    {
        if (lang == null) {
            lang = ResourceBundle.getBundle("bundles.i18n", new Locale("en", "EN"));
        }
        
        return lang;
    }
    /**
     * Traduce una cadena.
     * @param key Clave de la cadena.
     * @return Cadena traducida o, si no la encuentra la clave.
     */
    public static synchronized String getString(String key)
    {
        return getString(key, null);
    }
    /**
     * Traduce una cadena y completa con los parámetros.
     * @param key Clave de la cadena.
     * @param params Listado de parámetros.
     * @return Cadena traducida o, si no la encuentra la clave.
     */
    public static synchronized String getString(String key, Object[] params)
    {
        try {
            String str = getResource().getString(key);
            
            // Sustituir parámetros
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i ++) {
                    str = str.replace("%" + (i + 1), "" + params[i]);
                }
            }
            
            return str;
        } catch (Exception e) {}
        
        return key;
    }
}