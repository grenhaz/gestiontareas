package org.obarcia.gestiontareas.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

/**
 * Botón para las celdas de un tabla.
 * 
 * @author obarcia
 */
public class ButtonCell<T, V> extends TableCell<T, V>
{
    /**
     * Botón de la acción.
     */
    final Button cellButton = new Button();
    
    /**
     * Constructor de la clase.
     */
    public ButtonCell() {}
    /**
     * Asignar una acción al botón.
     * @param value Acción al botón.
     */
    public final void setOnAction(EventHandler<ActionEvent> value)
    {
        cellButton.setOnAction(value);
    }
    @Override
    protected void updateItem(V t, boolean empty)
    {
        super.updateItem(t, empty);
        if (!empty) {
            setGraphic(cellButton);
        }
    }
    /**
     * Asignarle una image al botón.
     * @param url URL de la iamgen.
     */
    public void setImage(String url)
    {
        if (url != null) {
            cellButton.setStyle("-fx-graphic: url('" + url + "')");
        } else {
            cellButton.setStyle("-fx-graphic: none");
        }
    }
}