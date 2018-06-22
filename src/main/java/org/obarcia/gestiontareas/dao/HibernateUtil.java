package org.obarcia.gestiontareas.dao;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author obarcia
 */
public class HibernateUtil
{
    private static SessionFactory SESSIONFACTORY = null;
    
    /**
     * Devuelve el SessionFactory.
     * @return Instancia del SessionFactory.
     */
    public static synchronized SessionFactory getSessionFactory()
    {
        if (SESSIONFACTORY == null) {
            try {
                StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
                Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
                SESSIONFACTORY = metaData.getSessionFactoryBuilder().build();
            } catch (Throwable th) {
                System.err.println("Initial SessionFactory creation failed" + th);
                throw new ExceptionInInitializerError(th);
            }
        }
        return SESSIONFACTORY;
    }
}