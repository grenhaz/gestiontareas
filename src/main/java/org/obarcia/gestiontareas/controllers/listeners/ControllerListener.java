package org.obarcia.gestiontareas.controllers.listeners;

import javafx.stage.Stage;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;

/**
 * Listener para los controles.
 * 
 * @author obarcia
 */
public class ControllerListener
{
    /**
     * Evento de abrir una sección.
     * @param section Identificador de la sección.
     */
    public void openSection(String section) {}
    /**
     * Entidad actualizada.
     * @param newRecord true si es un registro nuevo, false en caso contrario.
     * @param entidad Instancia de la entidad.
     */
    public void update(boolean newRecord, Entidad entidad) {}
    /**
     * Tarea actualizada.
     * @param newRecord true si es un registro nuevo, false en caso contrario.
     * @param tarea Instancia de la tarea.
     */
    public void update(boolean newRecord, Tarea tarea) {}
    /**
     * Entidad eliminada.
     * @param entidad Instancia de la entidad.
     */
    public void delete(Entidad entidad) {}
    /**
     * Tarea eliminada.
     * @param tarea Instancia de la tarea.
     */
    public void delete(Tarea tarea) {}
    /**
     * Seleccionada una entidad.
     * @param parent Stage parent.
     * @param entidad Instancia de la entidad.
     */
    public void select(Stage parent, Entidad entidad) {}
}