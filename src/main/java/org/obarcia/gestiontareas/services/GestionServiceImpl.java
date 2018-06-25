package org.obarcia.gestiontareas.services;

import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.obarcia.gestiontareas.components.ListTable;
import org.obarcia.gestiontareas.components.Util;
import org.obarcia.gestiontareas.dao.GestionDAO;
import org.obarcia.gestiontareas.dao.GestionDAOImpl;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;

/**
 * Implementación del servicio de Gestión.
 * 
 * @author obarcia
 */
public class GestionServiceImpl implements GestionService
{
    /**
     * Instancia del SINGLETON.
     */
    private static GestionServiceImpl SINGLETON = null;
    /**
     * Instancia del DAO.
     */
    private final GestionDAO dao = GestionDAOImpl.getInstance();
    /**
     * Validator interno.
     */
    private static Validator validator;
    
    /**
     * Instancia del SINGLETON.
     * @return Instancia del SINGLETON.
     */
    public synchronized static GestionServiceImpl getInstance()
    {
        if (SINGLETON == null) {
            SINGLETON = new GestionServiceImpl();
        }
        
        return SINGLETON;
    }
    /**
     * Constructor protegido del SINGLETON.
     */
    protected GestionServiceImpl() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Override
    public void init()
    {
        // Inicialización del DAO
        dao.init();
        
        // Control de versiones
        String version = Util.getVersion();
        if (version == null) {
            save("version", "1");
        }
        controlVersiones(version);
    }
    @Override
    public String getConfig(String clave, String def)
    {
        return dao.getConfig(clave, def);
    }
    
    @Override
    public void save(String clave, String valor)
    {
        dao.save(clave, valor);
    }
    @Override
    public List<Entidad> getEntidades()
    {
        return dao.getEntidades();
    }
    @Override
    public List<Tarea> getTareas()
    {
        return dao.getTareas();
    }
    @Override
    public ListTable<Tarea> getTareasCerradas(int offset, int size, String filter, String[] sorting)
    {
        return dao.getTareasCerradas(offset, size, filter, sorting);
    }
    @Override
    public Entidad getEntidad(Integer id)
    {
        return dao.getEntidad(id);
    }
    @Override
    public Tarea getTarea(Integer id)
    {
        return dao.getTarea(id);
    }
    @Override
    public Set<ConstraintViolation<Entidad>> save(Entidad entidad)
    {
        Set<ConstraintViolation<Entidad>> errors = validator.validate( entidad );
        if (errors.isEmpty()) {
            dao.save(entidad);
        } else {
            return errors;
        }
        
        return null;
    }
    @Override
    public Set<ConstraintViolation<Tarea>> save(Tarea tarea)
    {
        Set<ConstraintViolation<Tarea>> errors = validator.validate( tarea );
        if (errors.isEmpty()) {
            dao.save(tarea);
        } else {
            return errors;
        }
        
        return null;
    }
    @Override
    public void delete(Entidad entidad)
    {
        dao.delete(entidad);
    }
    @Override
    public void delete(Tarea tarea)
    {
        dao.delete(tarea);
    }
    /**
     * Control de versiones.
     * @param version Versión actual.
     */
    private void controlVersiones(String version)
    {
    }
}