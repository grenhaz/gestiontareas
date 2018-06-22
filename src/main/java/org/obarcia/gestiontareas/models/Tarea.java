package org.obarcia.gestiontareas.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.obarcia.gestiontareas.components.Language;

/**
 * Modelo de una tarea.
 * 
 * @author obarcia
 */
@Entity
@Table(name = "tarea")
public class Tarea implements Serializable
{
    /**
     * Formateo de fecha.
     */
    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("dd/MM/yyyy");
    /**
     * Formateo de hora.
     */
    private static final SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("HH:mm");
    /**
     * Fecha / Hora actual.
     */
    private static final Date NOW = new Date();
    /**
     * Estados.
     */
    public final static String[] STATUS = {"ABI", "PEN", "RES", "CER"};
    /**
     * Estado ABIERTA
     */
    public final static String STATUS_ABIERTA = "ABI";
    /**
     * Estado PENDIENTE
     */
    public final static String STATUS_PENDIENTE = "PEN";
    /**
     * Estado RESUELTA
     */
    public final static String STATUS_RESUELTA = "RES";
    /**
     * Estado CERRADA
     */
    public final static String STATUS_CERRADA = "CER";
    
    /**
     * Identificador.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id = 0;
    /**
     * Titulo.
     */
    @NotEmpty
    @Length(max = 256)
    @Column(name = "titulo")
    private String titulo = "";
    /**
     * Contenido.
     */
    @Column(name = "contenido")
    private String contenido = "";
    /**
     * Estado.
     */
    @NotEmpty
    @Column(name = "estado")
    private String estado = "ABI";
    /**
     * Estado.
     */
    @Column(name = "tipo")
    private String tipo = "";
    /**
     * Fecha de creación
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion")
    private Date creacion;
    /**
     * Fecha de creación
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_actualizacion")
    private Date actualizacion;
    /**
     * Si tiene o no prioridad.
     */
    @Column(name = "prioridad")
    private Boolean prioridad = Boolean.FALSE;
    /**
     * Fecha de creación
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "limite")
    private Date limite;
    /**
     * Entidad.
     */
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_entidad", nullable = true)
    private Entidad entidad;
    
    @Override
    public String toString() {
        return "Tarea[" + getId() + "::" + titulo + "]";
    }
    /**
     * Devuelve si es una tarea nueva.
     * @return true si lo es, false en caso contrario.
     */
    public boolean isNew()
    {
        return (id == null || id.equals(0));
    }
    /**
     * Devuelve el estado formateado.
     * @return Estado formateado.
     */
    public String getEstadoFormateado()
    {
        if (estado != null) {
            return Language.getString("STATUS_" + estado);
        }
        
        return "";
    }
    /**
     * Devuelve la fecha formateada para la fecha de actualización.
     * @return Fecha formateada.
     */
    public String getActualizacionFormateada()
    {
        if (actualizacion != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(actualizacion);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(NOW);
            if (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                return FORMAT_TIME.format(actualizacion);
            } else {
                return FORMAT_DATE.format(actualizacion);
            }
        }
        
        return "";
    }
    /**
     * Devuelve la fecha formateada para la fecha de límite.
     * @return Fecha formateada.
     */
    public String getLimiteFormateada()
    {
        return (limite != null ? FORMAT_DATE.format(limite) : "");
    }
    // *****************************************************
    // GETTER & SETTER
    // *****************************************************
    public Integer getId()
    {
        return id;
    }
    public void setId(Integer id)
    {
        this.id = id;
    }
    public Entidad getEntidad()
    {
        return entidad;
    }
    public void setEntidad(Entidad entidad)
    {
        this.entidad = entidad;
    }
    public String getTitulo()
    {
        return titulo;
    }
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }
    public String getContenido()
    {
        return contenido;
    }
    public void setContenido(String contenido)
    {
        this.contenido = contenido;
    }
    public String getEstado()
    {
        return estado;
    }
    public void setEstado(String estado)
    {
        this.estado = estado;
    }
    public String getTipo()
    {
        return tipo;
    }
    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }
    public Date getCreacion()
    {
        return creacion;
    }
    public void setCreacion(Date creacion)
    {
        this.creacion = creacion;
    }
    public Date getActualizacion()
    {
        return actualizacion;
    }
    public void setActualizacion(Date actualizacion)
    {
        this.actualizacion = actualizacion;
    }
    public Boolean getPrioridad()
    {
        return prioridad;
    }
    public void setPrioridad(Boolean prioridad)
    {
        this.prioridad = prioridad;
    }
    public Date getLimite()
    {
        return limite;
    }
    public void setLimite(Date limite)
    {
        this.limite = limite;
    }
}