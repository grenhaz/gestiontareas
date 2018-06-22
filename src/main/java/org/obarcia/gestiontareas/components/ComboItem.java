package org.obarcia.gestiontareas.components;

/**
 * Combo Item.
 * 
 * @author obarcia
 */
public class ComboItem
{
    /**
     * Clave.
     */
    private String key;
    /**
     * Valor.
     */
    private String value;
    
    /**
     * Constructor simple de la clase.
     */
    public ComboItem()
    {
        this("", "");
    }
    /**
     * Constructor por parámetros.
     * @param key Clave.
     * @param value Valor.
     */
    public ComboItem(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    @Override
    public String toString()
    {
        return value;
    }
    // ********************************************
    // GETTER & SETTER
    // ********************************************
    public String getKey()
    {
        return key;
    }
    public void setKey(String key)
    {
        this.key = key;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
}