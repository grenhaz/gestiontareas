package org.obarcia.gestiontareas.controllers.sections;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.obarcia.gestiontareas.components.ComboItem;
import org.obarcia.gestiontareas.components.MessageBox;
import org.obarcia.gestiontareas.components.Language;
import org.obarcia.gestiontareas.controllers.listeners.ControllerListener;
import org.obarcia.gestiontareas.controllers.listeners.ControllerManagerListener;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;

/**
 * Controlador genérico para las secciones.
 * 
 * @author obarcia
 */
public class SectionController implements Initializable
{
    /**
     * Datos internos del controlador.
     */
    private Object data = null;
    /**
     * Listener interno.
     */
    private ControllerListener listener = null;
    /**
     * Current stage.
     */
    private Stage stage = null;
    /**
     * Parent stage.
     */
    private Stage parent = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    /**
     * Devuelve los datos internos del controlador.
     * @return Datos internos del controlador.
     */
    public Object getData()
    {
        return data;
    }
    /**
     * Asigna los datos internos del controlador.
     * @param data Datos internos del controlador.
     */
    public void setData(Object data)
    {
        this.data = data;
    }
    /**
     * Devuelve el Stage asignado.
     * @return Instancia del Stage asignado.
     */
    public Stage getStage()
    {
        return stage;
    }
    /**
     * Asignar el Stage del controlador.
     * @param stage Instancia del stage.
     */
    public void setStage(Stage stage)
    {
        this.stage = stage;
        
        this.listener = new ControllerListener() {
            @Override
            public void update(boolean newRecord, Entidad entidad) { onUpdate(newRecord, entidad); }
            @Override
            public void update(boolean newRecord, Tarea tarea) { onUpdate(newRecord, tarea); }
            @Override
            public void delete(Entidad entidad) { onDelete(entidad); }
            @Override
            public void delete(Tarea tarea) { onDelete(tarea); }
            @Override
            public void select(Stage parent, Entidad entidad) { onSelect(parent, entidad); }
        };
        ControllerManagerListener.getInstance().addListener(listener);
        this.stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            onResize();
        });
        this.stage.setOnCloseRequest((WindowEvent t) -> {
            if (listener != null) {
                ControllerManagerListener.getInstance().removeListener(listener);
            }
            this.stage.close();
        });
    }
    /**
     * Devuelve el stage padre. 
     * @return Instancia del stage padre.
     */
    public Stage getParent()
    {
        return parent;
    }
    /**
     * Asigna el Stage padre.
     * @param parent Instancia del stage padre.
     */
    public void setParent(Stage parent)
    {
        this.parent = parent;
    }
    /**
     * Evento de cambio de tamaño de la ventana.
     */
    protected void onResize() {}
    /**
     * Evento de actualización de una entidad.
     * @param newRecord true si es un registro nuevo, false en caso contrario.
     * @param entidad Instancia de la entidad.
     */
    protected void onUpdate(boolean newRecord, Entidad entidad) {}
    /**
     * Evento de actualización de una tarea.
     * @param newRecord true si es un registro nuevo, false en caso contrario.
     * @param tarea Instancia de la tarea.
     */
    protected void onUpdate(boolean newRecord, Tarea tarea) {}
    /**
     * Evento de eliminación de una entidad.
     * @param entidad Instancia de la entidad.
     */
    protected void onDelete(Entidad entidad) {}
    /**
     * Evento de eliminación de una tarea.
     * @param tarea Instancia de la tarea.
     */
    protected void onDelete(Tarea tarea) {}
    /**
     * Evento de selección de una entidad.
     * @param parent Stage parent.
     * @param entidad Instancia de la entidad.
     */
    protected void onSelect(Stage parent, Entidad entidad) {}
    /**
     * Abrir una ventana modal.
     * @param title Título de la ventana.
     * @param fxml Fichero FXML.
     * @param data Datos a enviar al controlador.
     */
    protected void openWindow(String title, String fxml, Object data)
    {
        Platform.runLater(() -> {
            try {
                // Loader
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
                loader.setResources(Language.getResource());
                Parent root = (Parent)loader.load();
                Object ctrl = loader.getController();
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/Styles.css");
                
                // Configurar el stage de la aplicación
                Stage st = new Stage(StageStyle.DECORATED);
                st.getIcons().add(new Image("/images/logo.png"));
                st.setTitle(title);
                st.setScene(scene);
                st.initOwner(getStage());
                st.initModality(Modality.APPLICATION_MODAL);
                
                // Asignar los datos internos del controlador
                if (ctrl != null && ctrl instanceof SectionController) {
                    SectionController sctrl = ((SectionController)ctrl);
                    sctrl.setData(data);
                    sctrl.setParent(getStage());
                    sctrl.setStage(st);
                }
                
                // Lanzar el listener de sección abierta
                ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                    l.openSection(fxml);
                });
                
                // Mostrar la ventana
                st.showAndWait();
            } catch (Exception ex) {
                Logger.getLogger(SectionController.class.getName()).log(Level.SEVERE, "Exception", ex);
                
                // Mensaje de error
                MessageBox.error(Language.getString("TITLE_ERROR_APP"), Language.getString("MESSAGE_ERROR_WINDOW"));
            }
        });
    }
    /**
     * Cerrar la ventana.
     */
    public void close()
    {
        if (stage != null) {
            stage.hide();
        }
    }
    /**
     * Devuelve la clave del ComboItem seleccionado en el ComboBox.
     * @param cb Instancia del ComboBox.
     * @return Devuelve la clave seleccionada.
     */
    protected String getComboItemSelected(ComboBox cb)
    {
        ComboItem item = (ComboItem)cb.getValue();
        return (item != null ? item.getKey() : "");
    }
    /**
     * Devuelve el ComboItem seleccionado en el ComboBox.
     * @param cb Instancia del ComboBox.
     * @param key Clave a asignar.
     * @return Instancia del ComboItem.
     */
    protected ComboItem getComboItem(ComboBox cb, String key)
    {
        ObservableList<ComboItem> lst = cb.getItems();
        if (lst != null) {
            for (ComboItem item: lst) {
                if (item.getKey().equals(key)) {
                    return item;
                }
            }
        }
        
        return null;
    }
    /**
     * Asignar un ComboItem por su clave en un ComboBox.
     * @param cb Instancia del ComboBox.
     * @param key Clave a asignar.
     */
    protected void setComboItem(ComboBox cb, String key)
    {
        ObservableList<ComboItem> lst = cb.getItems();
        if (lst != null) {
            for (ComboItem item: lst) {
                if (item.getKey().equals(key)) {
                    cb.setValue(item);
                    return;
                }
            }
        }
    }
}