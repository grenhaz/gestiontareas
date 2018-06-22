package org.obarcia.gestiontareas.components;

import java.util.List;

/**
 * Listado para las tablas paginadas.
 * 
 * @author obarcia
 */
public class ListTable<T>
{
    /**
     * Número total de registros.
     */
    private int total;
    /**
     * Listado de registros.
     */
    private List<T> records;
    
    // ************************************************************
    // GETTER & SETTER
    // ************************************************************
    public int getTotal()
    {
        return total;
    }
    public void setTotal(int total)
    {
        this.total = total;
    }
    public List<T> getRecords()
    {
        return records;
    }
    public void setRecords(List<T> records)
    {
        this.records = records;
    }
}