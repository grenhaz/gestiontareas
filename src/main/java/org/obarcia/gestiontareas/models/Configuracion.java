package org.obarcia.gestiontareas.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Modelo de la configuración.
 * 
 * @author obarcia
 */
@Entity
@Table(name = "configuracion")
public class Configuracion implements Serializable
{
    /**
     * Identificador.
     */
    @Id
    @NotEmpty
    @Column(name = "clave")
    private String clave;
    /**
     * Valor.
     */
    @Column(name = "valor")
    private String valor;
    
    // *****************************************************
    // GETTER & SETTER
    // *****************************************************
    public String getClave()
    {
        return clave;
    }
    public void setClave(String clave)
    {
        this.clave = clave;
    }
    public String getValor()
    {
        return valor;
    }
    public void setValor(String valor)
    {
        this.valor = valor;
    }
}