package org.obarcia.gestiontareas.components;

import java.util.Calendar;
import java.util.Date;

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
}
