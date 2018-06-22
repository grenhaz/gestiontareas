package org.obarcia.gestiontareas.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.obarcia.gestiontareas.constraints.DoiConstraint;

/**
 * Modelo de una entidad.
 * 
 * @author obarcia
 */
@Entity
@Table(name = "entidad")
public class Entidad implements Serializable
{
    /**
     * Identificador.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id = 0;
    /**
     * DOI de la entidad.
     */
    @NotEmpty
    @DoiConstraint(message = "MESSAGE_ERROR_DOI")
    @Length(min = 9, max = 9)
    @Column(name = "doi")
    private String doi = "";
    /**
     * Nombre de la entidad.
     */
    @NotEmpty
    @Length(max = 256)
    @Column(name = "nombre")
    private String nombre = "";
    /**
     * Teléfono
     */
    @Length(max = 15)
    @Column(name = "telefono")
    private String telefono = "";
    /**
     * Teléfono
     */
    @Length(max = 15)
    @Column(name = "fax")
    private String fax = "";
    /**
     * Observaciones.
     */
    @Column(name = "observaciones")
    private String observaciones = "";

    @Override
    public String toString() {
        return getDoi() + " - " + getNombre();
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
    public String getNombre()
    {
        return nombre;
    }
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    public String getDoi()
    {
        return doi;
    }
    public void setDoi(String doi)
    {
        this.doi = doi;
    }
    public String getTelefono()
    {
        return telefono;
    }
    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }
    public String getFax()
    {
        return fax;
    }
    public void setFax(String fax)
    {
        this.fax = fax;
    }
    public String getObservaciones()
    {
        return observaciones;
    }
    public void setObservaciones(String observaciones)
    {
        this.observaciones = observaciones;
    }
}