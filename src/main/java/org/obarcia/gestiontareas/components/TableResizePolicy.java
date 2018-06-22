package org.obarcia.gestiontareas.components;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.util.Callback;

/**
 * Personalización del sistema de redimensionar las columnas del TableView.
 * 
 * @author obarcia
 */
public class TableResizePolicy implements Callback<TableView.ResizeFeatures, Boolean>
{
    @Override
    public Boolean call(ResizeFeatures rf)
    {
        TableView table = rf.getTable();
        TableColumn column = rf.getColumn();
        double twidth = table.getWidth() - 2;
        
        // Ajustar según la scrollbar
        ScrollBar sbh = table.lookupAll(".scroll-bar")
                    .stream()
                    .filter(n -> n instanceof ScrollBar)
                    .map(n -> (ScrollBar) n)
                    .filter(sb -> sb.getOrientation() == Orientation.VERTICAL)
                    .findFirst()
                    .orElse(null);
        if (sbh != null && sbh.isVisible()) {
            twidth -= sbh.getWidth();
        }
        if (column != null) {
            return false;
        }
        /*if (column != null) {
            // XXX: Reajustar columnas para encajar esta
            // Resize manual de una columna
            if (column.isResizable() && column.isVisible()) {
                // Calcular el total de las columnas
                double total = 0;
                for (Object o: table.getColumns()) {
                    TableColumn c = (TableColumn)o;
                    total += c.getWidth();
                }
                double delta = rf.getDelta();
                if (total + rf.getDelta() > twidth){
                    delta = twidth - total - rf.getDelta();
                }
                double nw = column.getWidth() + delta;
                column.setPrefWidth(nw);
                return true;
            } else {
                return false;
            }
        }*/
        
        // Ajuste completo
        // En la primera pasada asigna el mínimo a cada columna
        double ptotal = 0;
        int columns = 0;
        for (Object o: table.getColumns()) {
            TableColumn c = (TableColumn)o;
            c.setPrefWidth(c.getMinWidth());
            ptotal += c.getMinWidth();
            
            if (c.isResizable() && c.isVisible()) {
                columns ++;
            }
        }

        // Asignar width a cada columna que se pueda proporcionalmente
        double total = twidth - ptotal;
        double size;
        int acolumns = columns;
        boolean canTry = false;
        do {
            for (Object o: table.getColumns()) {
                TableColumn c = (TableColumn)o;

                if (c.isResizable() && c.isVisible()) {
                    // Tamaño proporcional
                    size = (total / acolumns);
                    
                    if (c.getPrefWidth() + size < c.getMinWidth()) {
                        size = c.getMinWidth() - c.getPrefWidth();
                    }
                    if (c.getPrefWidth() + size > c.getMaxWidth()) {
                        size = c.getMaxWidth() - c.getPrefWidth();
                    }
                    if (size > 0) {
                        canTry = true;
                    }

                    c.setPrefWidth(c.getPrefWidth() + size);
                    total -= size;
                    acolumns --;
                }
            }
        } while(total >= 1 && canTry);

        return true;
    }
}