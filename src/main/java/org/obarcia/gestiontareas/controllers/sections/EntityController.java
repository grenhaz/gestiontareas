package org.obarcia.gestiontareas.controllers.sections;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.obarcia.gestiontareas.components.MessageBox;
import org.obarcia.gestiontareas.components.Language;
import org.obarcia.gestiontareas.controllers.listeners.ControllerManagerListener;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.services.GestionService;
import org.obarcia.gestiontareas.services.GestionServiceImpl;

/**
 * Controlador del formulario de una Entidad.
 * 
 * @author obarcia
 */
public class EntityController extends SectionController
{
    /**
     * Instancia del servicio.
     */
    private final GestionService service = GestionServiceImpl.getInstance();
    
    @FXML
    private TextField txtDoi;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtFax;
    @FXML
    private TextArea txtObservations;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        super.initialize(url, rb);
    }
    /**
     * Acción de guardar la tarea.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionSave(ActionEvent event)
    {
        Entidad entidad = (Entidad)getData();
        if (entidad != null) {
            boolean newRecord = (entidad.getId() == null || entidad.getId() == 0);
            entidad.setDoi(txtDoi.getText().toUpperCase());
            entidad.setNombre(txtName.getText());
            entidad.setTelefono(txtPhone.getText());
            entidad.setFax(txtFax.getText());
            entidad.setObservaciones(txtObservations.getText());
            Set<ConstraintViolation<Entidad>> errors = service.save(entidad);
            if (errors != null) {
                // Mostrar los errores
                String errStr = "";
                for (ConstraintViolation<Entidad> e: errors) {
                    String attributeName = Language.getString("ATTRIBUTE_" + ((PathImpl)e.getPropertyPath()).getLeafNode().getName().toUpperCase());
                    errStr += attributeName + ": " + e.getMessage() + "\n";
                }
                MessageBox.error(Language.getString("TITLE_ERROR_ATTRIBUTES"), errStr);
            } else {
                // Evento de actualizar la tarea
                ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                    l.update(newRecord, entidad);
                });
                
                close();
            }
        } else {
            close();
        }
    }
    @Override
    public void setData(Object data)
    {
        super.setData(data);
        
        Entidad entidad = (Entidad)getData();
        if (entidad != null) {
            txtDoi.setText(entidad.getDoi());
            txtName.setText(entidad.getNombre());
            txtPhone.setText(entidad.getTelefono());
            txtFax.setText(entidad.getFax());
            txtObservations.setText(entidad.getObservaciones());
        }
    }
}