package org.obarcia.gestiontareas.controllers.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manager para el listener de los controles.
 * 
 * @author obarcia
 */
public class ControllerManagerListener
{
    /**
     * Instancia del SINGLETON.
     */
    private static ControllerManagerListener SINGLETON = null;
    /**
     * Listado de listeners.
     */
    private static final List<ControllerListener> LISTENERS = new ArrayList<>();
    
    /**
     * Instancia SINGLETON.
     * 
     * @return Instancia del SINGLETON.
     */
    public synchronized static ControllerManagerListener getInstance()
    {
        if (SINGLETON == null) {
            SINGLETON = new ControllerManagerListener();
        }
        
        return SINGLETON;
    }
    /**
     * Constructor protegido paa el SINGLETON.
     */
    protected ControllerManagerListener() {}
    /**
     * Añadir un Listener.
     * @param l Instancia del listener.
     */
    public synchronized void addListener(ControllerListener l)
    {
        LISTENERS.add(l);
    }
    /**
     * Eliminar un listener.
     * @param l Instancia del listener.
     */
    public synchronized void removeListener(ControllerListener l)
    {
        LISTENERS.remove(l);
    }
    /**
     * Obtener el listado de listeners.
     * @return Listado de listeners.
     */
    public synchronized List<ControllerListener> getListeners()
    {
        return Collections.unmodifiableList(LISTENERS);
    }
}