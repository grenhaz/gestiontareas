package org.obarcia.gestiontareas.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.obarcia.gestiontareas.components.ListTable;
import org.obarcia.gestiontareas.models.Configuracion;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;

/**
 * Implementación del DAO de gestión.
 * 
 * @author obarcia
 */
public class GestionDAOImpl implements GestionDAO
{
    /**
     * Instancia del SINGLETON.
     */
    private static GestionDAOImpl SINGLETON = null;
    
    /**
     * Instancia SINGLETON.
     * 
     * @return Instancia del SINGLETON.
     */
    public synchronized static GestionDAOImpl getInstance()
    {
        if (SINGLETON == null) {
            SINGLETON = new GestionDAOImpl();
        }
        
        return SINGLETON;
    }
    /**
     * Constructor protegido paa el SINGLETON.
     */
    protected GestionDAOImpl() {}
    @Override
    public void init()
    {
        HibernateUtil.getSessionFactory().openSession();
    }
    @Override
    public String getConfig(String clave, String def)
    {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Configuracion cfg = session.get(Configuracion.class, clave);
            if (cfg != null) {
                return cfg.getValor();
            }
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
        
        return def;
    }
    @Override
    public void save(String clave, String valor)
    {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Configuracion cfg = new Configuracion();
            cfg.setClave(clave);
            cfg.setValor(valor);
            session.saveOrUpdate(cfg);
            tx.commit();
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
    @Override
    public List<Entidad> getEntidades()
    {
        List<Entidad> list = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Criteria
            CriteriaBuilder builder = session.getCriteriaBuilder();
        
            // Query
            CriteriaQuery<Entidad> criteria = builder.createQuery(Entidad.class);
            Root<Entidad> root = criteria.from(Entidad.class);
        
            // Query
            return session.createQuery(criteria).list();
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
        
        return list;
    }
    @Override
    public List<Tarea> getTareas()
    {
        List<Tarea> list = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Criteria
            CriteriaBuilder builder = session.getCriteriaBuilder();
        
            // Query
            CriteriaQuery<Tarea> criteria = builder.createQuery(Tarea.class);
            Root<Tarea> root = criteria.from(Tarea.class);
            
            // Solamente las no cerradas
            criteria.where(builder.and(builder.notEqual(root.get("estado"), "RES"), builder.notEqual(root.get("estado"), "CER")));
        
            // Query
            return session.createQuery(criteria).list();
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
        
        return list;
    }
    @Override
    public ListTable<Tarea> getTareasCerradas(int offset, int size, String filter, String[] sorting)
    {
        ListTable<Tarea> list = new ListTable<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Criteria
            CriteriaBuilder builder = session.getCriteriaBuilder();
        
            // Count
            CriteriaQuery<Long> criteriaCount = builder.createQuery(Long.class);
            Root rootCount = criteriaCount.from(Tarea.class);
            criteriaCount.select(builder.count(rootCount));
        
            // Query
            CriteriaQuery<Tarea> criteria = builder.createQuery(Tarea.class);
            Root<Tarea> root = criteria.from(Tarea.class);
            
            // Joins
            rootCount.join("entidad", JoinType.LEFT);
            root.join("entidad", JoinType.LEFT);
            
            // Predicates
            List<Predicate> predicates = new LinkedList<>();
            
            // Filters
            if (filter != null && !filter.trim().isEmpty()) {
                String tfilter = filter.trim().toLowerCase();
                //predicates.add(builder.like(builder.lower(root.<String>get("id").as(String.class)), "%" + tfilter + "%"));
                predicates.add(builder.like(builder.lower(root.<String>get("titulo")), "%" + tfilter + "%"));
                predicates.add(builder.like(builder.lower(root.<String>get("contenido")), "%" + tfilter + "%"));
                predicates.add(builder.like(builder.lower(root.get("entidad").get("doi")), "%" + tfilter + "%"));
                predicates.add(builder.like(builder.lower(root.get("entidad").get("nombre")), "%" + tfilter + "%"));
            }
            
            // Where
            if (predicates.size() > 0) {
                Predicate[] predArray = new Predicate[predicates.size()];
                predicates.toArray(predArray);
                criteriaCount.where(builder.or(predArray), builder.and(builder.or(builder.equal(root.get("estado"), "RES"), builder.equal(root.get("estado"), "CER"))));
                criteria.where(builder.or(predArray), builder.and(builder.or(builder.equal(root.get("estado"), "RES"), builder.equal(root.get("estado"), "CER"))));
            } else {
                criteriaCount.where(builder.and(builder.or(builder.equal(root.get("estado"), "RES"), builder.equal(root.get("estado"), "CER"))));
                criteria.where(builder.and(builder.or(builder.equal(root.get("estado"), "RES"), builder.equal(root.get("estado"), "CER"))));
            }
            
            // Order By
            // TODO: Aplicar la ordenación
            criteria.orderBy(builder.desc(root.get("actualizacion")));
        
            // Query
            list.setRecords(session.createQuery(criteria).setFirstResult(offset).setMaxResults(size).list());
            list.setTotal(session.createQuery(criteriaCount).getSingleResult().intValue());
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
            
            list.setRecords(new ArrayList<>());
            list.setTotal(0);
        }
        
        return list;
    }
    @Override
    public Entidad getEntidad(Integer id)
    {
        Entidad entidad = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            entidad = session.get(Entidad.class, id);
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
        
        return entidad;
    }
    @Override
    public Tarea getTarea(Integer id)
    {
        Tarea tarea = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tarea = session.get(Tarea.class, id);
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
        
        return tarea;
    }
    @Override
    public void save(Entidad entidad)
    {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(entidad);
            tx.commit();
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
    @Override
    public void save(Tarea tarea)
    {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            if (tarea.getCreacion() == null) {
                tarea.setCreacion(new Date());
            }
            tarea.setActualizacion(new Date());
            session.saveOrUpdate(tarea);
            tx.commit();
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
    @Override
    public void delete(Entidad entidad)
    {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(entidad);
            tx.commit();
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
    @Override
    public void delete(Tarea tarea)
    {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(tarea);
            tx.commit();
        } catch (Exception ex) {
            Logger.getLogger(GestionDAOImpl.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
}