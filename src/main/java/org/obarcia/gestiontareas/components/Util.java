package org.obarcia.gestiontareas.components;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Clase general de utilidades.
 * 
 * @author obarcia
 */
public class Util
{
    /**
     * Fecha / Hora actual.
     */
    public static final Date NOW = new Date();
    
    /**
     * Devuelve si una fecha es de hoy o no.
     * @param dt Instancia de la fecha.
     * @return true si es de hoy, false en caso contrario.
     */
    public static boolean isToday(Date dt)
    {
        if (dt != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(dt);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(NOW);
            return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
        }
        
        return false;
    }
    public static String getVersion()
    {
        try {
            // Example using HTMLEmail from Apache Commons Email 
            Class theClass = Util.class;

            // Find the path of the compiled class 
            String classPath = theClass.getResource(theClass.getSimpleName() + ".class").toString(); 

            // Find the path of the lib which includes the class 
            String libPath = classPath.substring(0, classPath.lastIndexOf("!")); 

            // Find the path of the file inside the lib jar 
            String filePath = libPath + "!/META-INF/MANIFEST.MF"; 

            // We look at the manifest file, getting two attributes out of it 
            Manifest manifest = new Manifest(new URL(filePath).openStream()); 
            Attributes attr = manifest.getMainAttributes(); 
            
            return attr.getValue("Version-Number");
        } catch (Exception ex) {}
        
        return "1.0.0";
    }
}