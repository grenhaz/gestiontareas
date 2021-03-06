package org.obarcia.gestiontareas.dao;

import java.util.List;
import org.obarcia.gestiontareas.components.ListTable;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;

/**
 * Interface para el DAO de gestión.
 * 
 * @author obarcia
 */
public interface GestionDAO
{
    /**
     * Inicialización para acelerar el acceso con la BBDD.
     */
    public void init();
    /**
     * Obtener el valor de una configuración.
     * @param clave Clave.
     * @param def Valor por defecto.
     * @return Valor resultante.
     */
    public String getConfig(String clave, String def);
    /**
     * Guardar una configuración.
     * @param clave Clave.
     * @param valor Valor.
     */
    public void save(String clave, String valor);
    /**
     * Devuelve el listado de Entidades.
     * @return Listado de Entidades.
     */
    public List<Entidad> getEntidades();
    /**
     * Devuelve el listado de Tareas.
     * @return Listado de Tareas.
     */
    public List<Tarea> getTareas();
    /**
     * Devuelve el listado de Tareas.
     * @param offset Offset de inicio del listado.
     * @param size Tamaño del listado.
     * @param filter Filtro.
     * @param sorting Listado de ordenación.
     * @return Listado de Tareas.
     */
    public ListTable<Tarea> getTareasCerradas(int offset, int size, String filter, String[] sorting);
    /**
     * Devuelve una entidad.
     * @param id Identificador de la entidad.
     * @return Instancia de la entidad.
     */
    public Entidad getEntidad(Integer id);
    /**
     * Devuelve una tarea.
     * @param id Identificador de la tarea.
     * @return Instancia de la tarea.
     */
    public Tarea getTarea(Integer id);
    /**
     * Guardar una entidad.
     * @param entidad Instancia de la entidad.
     */
    public void save(Entidad entidad);
    /**
     * Guardar una tarea.
     * @param tarea Instancia de la tarea.
     */
    public void save(Tarea tarea);
    /**
     * Eliminar una entidad.
     * @param entidad Instancia de la entidad.
     */
    public void delete(Entidad entidad);
    /**
     * Eliminar una tarea.
     * @param tarea Instancia de la tarea.
     */
    public void delete(Tarea tarea);
}