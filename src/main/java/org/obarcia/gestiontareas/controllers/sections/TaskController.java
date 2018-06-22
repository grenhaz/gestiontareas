package org.obarcia.gestiontareas.controllers.sections;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.obarcia.gestiontareas.components.ComboItem;
import org.obarcia.gestiontareas.components.MessageBox;
import org.obarcia.gestiontareas.components.Language;
import org.obarcia.gestiontareas.controllers.listeners.ControllerManagerListener;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;
import org.obarcia.gestiontareas.services.GestionService;
import org.obarcia.gestiontareas.services.GestionServiceImpl;

/**
 * Controlador del formulario de una Tarea.
 * 
 * @author obarcia
 */
public class TaskController extends SectionController
{
    /**
     * Instancia del servicio.
     */
    private final GestionService service = GestionServiceImpl.getInstance();
    
    @FXML
    private TextField txtTitle;
    @FXML
    private TextArea txtContent;
    @FXML
    private TextField txtEntity;
    @FXML
    private ComboBox cbStatus;
    @FXML
    private CheckBox chkPriority;
    @FXML
    private DatePicker dtLimite;
    @FXML
    private Button btEntity;
    @FXML
    private Button btSave;
    @FXML
    private Button btRestore;
    @FXML
    private Button btEntityClean;
    @FXML
    private Button btLimiteClean;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        super.initialize(url, rb);
        
        // Readonly el TextField del DatePicker
        dtLimite.getEditor().setDisable(true);
        
        // Estado
        for (String s: Tarea.STATUS) {
            cbStatus.getItems().add(new ComboItem(s, Language.getString("STATUS_" + s)));
        }
    }
    /**
     * Acción para seleccionar una Entidad.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionEntitySearch(ActionEvent event)
    {
        openWindow("Entidades", "sections/Entities.fxml", (Tarea)getData());
    }
    /**
     * Evento de click sobre el campo TextField de la entidad.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionTextEntitySelect(MouseEvent event)
    {
        if (event.getSource()instanceof HBox && !btEntity.isDisabled()) {
            openWindow("Entidades", "sections/Entities.fxml", (Tarea)getData());
        }
    }
    /**
     * Acción de guardar la tarea.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionSave(ActionEvent event)
    {
        Tarea tarea = (Tarea)getData();
        if (tarea != null) {
            boolean newRecord = (tarea.getId() == null || tarea.getId() == 0);
            tarea.setTitulo(txtTitle.getText());
            tarea.setContenido(txtContent.getText());
            tarea.setEstado(getComboItemSelected(cbStatus));
            tarea.setPrioridad(chkPriority.isSelected());
            if (dtLimite.getValue() != null) {
                tarea.setLimite(Date.from(dtLimite.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else {
                tarea.setLimite(null);
            }
            Set<ConstraintViolation<Tarea>> errors = service.save(tarea);
            if (errors != null) {
                // Mostrar los errores
                String errStr = "";
                for (ConstraintViolation<Tarea> e: errors) {
                    String attributeName = Language.getString("ATTRIBUTE_" + ((PathImpl)e.getPropertyPath()).getLeafNode().getName().toUpperCase());
                    errStr += attributeName + ": " + e.getMessage() + "\n";
                }
                MessageBox.error(Language.getString("TITLE_ERROR_ATTRIBUTES"), errStr);
            } else {
                // Evento de actualizar la tarea
                ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                    l.update(newRecord, tarea);
                });
                
                close();
            }
        } else {
            close();
        }
    }
    /**
     * Acción de reabrir la tarea.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionRestore(ActionEvent event)
    {
        Tarea tarea = (Tarea)getData();
        if (tarea != null) {
            tarea.setEstado("ABI");
            Set<ConstraintViolation<Tarea>> errors = service.save(tarea);
            if (errors != null) {
                // Mostrar los errores
                String errStr = "";
                for (ConstraintViolation<Tarea> e: errors) {
                    String attributeName = Language.getString("ATTRIBUTE_" + ((PathImpl)e.getPropertyPath()).getLeafNode().getName().toUpperCase());
                    errStr += attributeName + ": " + e.getMessage() + "\n";
                }
                MessageBox.error(Language.getString("TITLE_ERROR_ATTRIBUTES"), errStr);
            } else {
                // Recuperar la tarea desde la BBDD
                Tarea t = service.getTarea(tarea.getId());
                
                // Evento de actualizar la tarea
                ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                    l.update(false, t);
                });
                
                close();
            }
        } else {
            close();
        }
    }
    /**
     * Acción de limpiar la fecha de límite.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionEntityClean(ActionEvent event)
    {
        Tarea tarea = (Tarea)getData();
        if (tarea != null) {
            tarea.setEntidad(null);
            txtEntity.setText("");
        }
    }
    /**
     * Acción de limpiar la fecha de límite.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionLimiteClean(ActionEvent event)
    {
        dtLimite.setValue(null);
    }
    @Override
    protected void onSelect(Stage parent, Entidad entidad)
    {
        if (parent != null && parent == getStage()) {
            Tarea tarea = (Tarea)getData();
            if (tarea != null) {
                tarea.setEntidad(entidad);
                if (entidad != null) {
                    txtEntity.setText(entidad.getDoi() + " - " + entidad.getNombre());
                } else {
                    txtEntity.setText("");
                }
            }
        }
    }
    @Override
    public void setData(Object data)
    {
        super.setData(data);
        
        Tarea tarea = (Tarea)getData();
        if (tarea != null) {
            txtTitle.setText(tarea.getTitulo());
            txtContent.setText(tarea.getContenido());
            String estado = tarea.getEstado();
            if (estado == null || estado.isEmpty()) {
                tarea.setEstado("ABI");
            }
            setComboItem(cbStatus, tarea.getEstado());
            Entidad entidad = tarea.getEntidad();
            if (entidad != null) {
                txtEntity.setText(entidad.getDoi() + " - " + entidad.getNombre());
            } else {
                txtEntity.setText("");
            }
            txtEntity.setEditable(false);
            chkPriority.setSelected(tarea.getPrioridad());
            if (tarea.getLimite() != null) {
                LocalDate ld = tarea.getLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                dtLimite.setValue(LocalDate.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth()));
            } else {
                dtLimite.setValue(null);
            }
            
            // Si está cerrada es readonly
            boolean readonly = (estado != null && (estado.equals("RES") || estado.equals("CER")));
            txtTitle.setEditable(!readonly);
            txtContent.setEditable(!readonly);
            cbStatus.setDisable(readonly);
            chkPriority.setDisable(readonly);
            btLimiteClean.setDisable(readonly);
            dtLimite.setDisable(readonly);
            btEntityClean.setDisable(readonly);
            btEntity.setDisable(readonly);
            btSave.setVisible(!readonly);
            btRestore.setVisible(readonly);
        }
    }
}