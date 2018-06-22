package org.obarcia.gestiontareas.test;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;
import org.obarcia.gestiontareas.services.GestionService;
import org.obarcia.gestiontareas.services.GestionServiceImpl;

/**
 * Test para generar la demo.
 * 
 * @author obarcia
 */
public class DemoTest
{
    /**
     * Instancia del servicio.
     */
    private final GestionService service = GestionServiceImpl.getInstance();
    
    /**
     * Constructor de la clase.
     */
    public DemoTest() {}
    @BeforeClass
    public static void setUpClass() {}
    @AfterClass
    public static void tearDownClass() {}
    @Before
    public void setUp() {}
    @After
    public void tearDown() {}
    /**
     * Generación de la DEMO.
     */
    @Test
    public void demo()
    {
        Entidad e;
        Tarea t;
        
        List<Entidad> entidades = new ArrayList<>();
        for (int i = 0; i < 1000; i ++) {
            e = new Entidad();
            e.setDoi("76931598W");
            e.setNombre("ENTIDAD " + (i + 1));
            e.setTelefono("986998877");
            service.save(e);
            
            entidades.add(e);
        }
        
        for (int i = 0; i < 1000; i ++) {
            t = new Tarea();
            t.setTitulo("TAREA " + (i + 1));
            t.setPrioridad((int)(Math.random() * 2) == 0 ? Boolean.TRUE : Boolean.FALSE);
            t.setEstado(Tarea.STATUS[(int)(Math.random() * Tarea.STATUS.length)]);
            t.setEntidad(entidades.get((int)(Math.random() * entidades.size())));
            service.save(t);
        }
    }
}
