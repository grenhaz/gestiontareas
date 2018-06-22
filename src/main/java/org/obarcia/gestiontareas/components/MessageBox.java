package org.obarcia.gestiontareas.components;

import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Utilidades para mostrar los mensajes.
 * 
 * @author obarcia
 */
public class MessageBox
{
    /**
     * Mensaje básico.
     * @param title Título.
     * @param message Mensaje.
     */
    public static void message(String title, String message)
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    /**
     * Mensaje de error (No cierra la aplicación).
     * @param title Título.
     * @param message Mensaje.
     */
    public static void error(String title, String message)
    {
        error(title, message, false);
    }
    /**
     * Mensaje de error.
     * @param title Título.
     * @param message Mensaje.
     * @param exit true si se cierra la aplicación, false en caso contrario.
     */
    public static void error(String title, String message, boolean exit)
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
            
            if (exit) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    /**
     * Mensaje de confirmación.
     * @param title Título.
     * @param message Mensaje.
     * @return true si lo confirma, false en caso contrario.
     */
    public static boolean confirm(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("");
        alert.setTitle(title);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }
}
