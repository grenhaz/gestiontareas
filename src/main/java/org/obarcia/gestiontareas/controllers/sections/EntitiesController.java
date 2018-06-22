package org.obarcia.gestiontareas.controllers.sections;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import org.obarcia.gestiontareas.components.ButtonCell;
import org.obarcia.gestiontareas.components.Language;
import org.obarcia.gestiontareas.components.MessageBox;
import org.obarcia.gestiontareas.components.TableResizePolicy;
import org.obarcia.gestiontareas.controllers.listeners.ControllerManagerListener;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;
import org.obarcia.gestiontareas.services.GestionService;
import org.obarcia.gestiontareas.services.GestionServiceImpl;

/**
 * Controlador de las entidades.
 *
 * @author obarcia
 */
public class EntitiesController extends SectionController
{
    /**
     * Instancia del servicio.
     */
    private final GestionService service = GestionServiceImpl.getInstance();
    /**
     * Listado de entidades.
     */
    private final ObservableList<Entidad> entidades = FXCollections.observableArrayList();
    /**
     * Listado de entidades (Filtradas).
     */
    private final ObservableList<Entidad> entidadesFiltered = FXCollections.observableArrayList();
    /**
     * Texto del filtro.
     */
    private String filterString = "";
    /**
     * Menú contextual
     */
    private ContextMenu ctxMenu;

    @FXML
    private TableView tblEntities;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btSearch;
    @FXML
    private Label lblStatus;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        super.initialize(url, rb);
        
        // Cargar los datos
        entidades.addAll(service.getEntidades());
        entidadesFiltered.addAll(service.getEntidades());
        
        // Crear la tabla
        tblEntities.setColumnResizePolicy(new TableResizePolicy());
        
        // Definición de columnas
        TableColumn colDoi = new TableColumn(Language.getString("COLUMN_DOI"));
        TableColumn colNombre = new TableColumn(Language.getString("COLUMN_NOMBRE"));
        TableColumn colTelefono = new TableColumn(Language.getString("COLUMN_TELEFONO"));
        TableColumn colEdit = new TableColumn();
        TableColumn colDelete = new TableColumn();
        
        // Sizes
        colDoi.setMinWidth(100);
        colDoi.setMaxWidth(100);
        colDoi.setResizable(false);
        colNombre.setMinWidth(400);
        colTelefono.setMinWidth(100);
        colTelefono.setMaxWidth(100);
        colTelefono.setResizable(false);
        colEdit.setMinWidth(44);
        colEdit.setMaxWidth(44);
        colEdit.setResizable(false);
        colDelete.setMinWidth(44);
        colDelete.setMaxWidth(44);
        colDelete.setResizable(false);
        
        // Cell factory
        colDoi.setCellValueFactory(new PropertyValueFactory<>("doi"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEdit.setCellValueFactory(new PropertyValueFactory<>("EDIT"));
        colDelete.setCellValueFactory(new PropertyValueFactory<>("DELETE"));
        
        // Acciones
        colEdit.setCellFactory(new Callback<TableColumn<Entidad, String>, TableCell<Entidad, String>>() {
            @Override
            public TableCell<Entidad, String> call(TableColumn<Entidad, String> p)
            {
                ButtonCell<Entidad, String> btn = new ButtonCell<Entidad, String>();
                btn.setImage("/images/edit.png");
                btn.setOnAction((ActionEvent t) -> {
                    // Editar el registro
                    Entidad entidad = (Entidad)btn.getTableView().getItems().get(btn.getIndex());
                    entityEdit(entidad);
                });
                return btn;
            }
        });
        colDelete.setCellFactory(new Callback<TableColumn<Entidad, String>, TableCell<Entidad, String>>() {
            @Override
            public TableCell<Entidad, String> call(TableColumn<Entidad, String> p)
            {
                ButtonCell<Entidad, String> btn = new ButtonCell<Entidad, String>();
                btn.setImage("/images/delete.png");
                btn.setOnAction((ActionEvent t) -> {
                    // Borrar el registro
                    Entidad entidad = (Entidad)btn.getTableView().getItems().get(btn.getIndex());
                    entityDelete(entidad);
                });
                return btn;
            }
        });
        
        // Añadir las columnas
        tblEntities.getColumns().addAll(colDoi, colNombre, colTelefono, colEdit);
        
        // Evento de click
        tblEntities.setRowFactory(tv -> {
            TableRow<Entidad> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Entidad entidad = row.getItem();

                // Guarda en el context menú la entidad
                ctxMenu.setUserData(entidad);

                if (entidad != null) {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                        clickEntity(entidad);
                    }
                }
            });
            return row ;
        });

        // Evento del buscador
        txtSearch.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            filterString = newValue.toUpperCase();
            refreshData();
        });

        // Asignar los items a la tabla
        tblEntities.setItems(entidadesFiltered);
        
        // Orden por defecto
        colDoi.setSortType(TableColumn.SortType.ASCENDING);
        tblEntities.getSortOrder().addAll(colDoi);
        tblEntities.sort();

        // Refresco interno
        refreshInternal();
        
        // Crear el menú contextual
        createContextMenu();
    }
    /**
     * Crear el menú contextual.
     */
    private void createContextMenu()
    {
        ctxMenu = new ContextMenu();
        
        MenuItem item;
        
        // Item de abrir
        item = new MenuItem(Language.getString("MENU_OPEN"));
        item.setOnAction((ActionEvent event) -> {
            Entidad entidad = (Entidad)ctxMenu.getUserData();
            if (entidad != null) {
                clickEntity(entidad);
            }
        });
        ctxMenu.getItems().add(item);
        
        // Asignar el context menú
        tblEntities.setContextMenu(ctxMenu);
        
        // Evento de apertura del context menú
        tblEntities.setOnContextMenuRequested((ContextMenuEvent event) -> {
            Entidad entidad = (Entidad)ctxMenu.getUserData();
            if (entidad != null) {
                // Si hay algo que cambiar en el menú
            } else {
                ctxMenu.hide();
            }
        });
    }
    /**
     * Borrar la búsqueda actual.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionClearSearch(ActionEvent event)
    {
        txtSearch.setText("");
    }
    /**
     * Acción de crear una nueva tarea.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionNewEntity(ActionEvent event)
    {
        Entidad entity = new Entidad();
        openWindow(Language.getString("TITLE_ENTITY_NEW"), "sections/Entity.fxml", entity);
    }
    /**
     * Acción al pulsar sobre una entidad.
     * @param entidad Instancia de la entidad.
     */
    private void clickEntity(Entidad entidad)
    {
        Object data = getData();
        // Si el objeto receptor es una tarea
        if (data != null && data instanceof Tarea) {
            // Lanzar el listener
            ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                l.select(getParent(), entidad);
            });

            close();
        } else {
            // Editar la entidad
            entityEdit(entidad);
        }
    }
    /**
     * Abrir la ventana de edición de una entidad.
     * @param entidad Instancia de la entidad.
     */
    private void entityEdit(Entidad entidad)
    {
        openWindow(Language.getString("TITLE_ENTITY") +" #" + entidad.getId(), "sections/Entity.fxml", entidad);
    }
    /**
     * Eliminar una entidad.
     * @param entidad Instancia de la entidad.
     */
    private void entityDelete(Entidad entidad)
    {
        if (MessageBox.confirm("Confirmar la eliminación", "¿Realmente desea eliminar la entidad?")) {
            service.delete(entidad);
            
            // Evento de actualizar la tarea
            ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                l.delete(entidad);
            });
        }
    }
    /**
     * Refrescar los datos.
     */
    private void refreshData()
    {
        entidadesFiltered.clear();
        if (!filterString.isEmpty()) {
            for (Entidad e: entidades) {
                String doi = (e.getDoi() != null ? e.getDoi().toUpperCase() : "");
                String nombre = (e.getNombre() != null ? e.getNombre().toUpperCase() : "");
                String telefono = (e.getTelefono() != null ? e.getTelefono().toUpperCase() : "");

                if (doi.contains(filterString) || nombre.contains(filterString) || 
                    telefono.contains(filterString)) {
                    entidadesFiltered.add(e);
                }
            }
        } else {
            entidadesFiltered.addAll(entidades);
        }
        tblEntities.refresh();
        tblEntities.sort();
        
        // Refresco interno
        refreshInternal();
    }
    /**
     * Refresco interno.
     */
    private void refreshInternal()
    {
        // Estado de la tabla
        if (!filterString.isEmpty()) {
            lblStatus.setText(Language.getString("TEXT_TABLE_STATUS_FILTER", new Object[] {entidadesFiltered.size(), entidades.size()}));
        } else {
            lblStatus.setText(Language.getString("TEXT_TABLE_STATUS", new Object[] {entidadesFiltered.size()}));
        }
        
        // Habilitar o no el botón de limpiar la búsqueda
        btSearch.setDisable(filterString.isEmpty());
    }
    /**
     * Buscar el índice en el listado de una entidad por su identificador.
     * @param id Identificador.
     * @return Índice o -1 en caso de no encontrarlo.
     */
    private int indexOfEntity(Integer id)
    {
        for (int i = 0; i < entidades.size(); i ++) {
            if (entidades.get(i).getId().equals(id)) {
                return i;
            }
        }
        
        return -1;
    }
    @Override
    protected void onUpdate(boolean newRecord, Entidad entidad)
    {
        // Comprobar si es la marcada
        boolean isSelected = false;
        Entidad tsel = (Entidad)tblEntities.getSelectionModel().getSelectedItem();
        if (tsel != null) {
            isSelected = (tsel.getId().equals(entidad.getId()));
        }
        
        // Hay que refrecar el listado
        if (!newRecord) {
            // Buscarla
            int index = indexOfEntity(entidad.getId());
            if (index >= 0) {
                // Eliminarla
                entidades.set(index, entidad);
            }
        } else {
            // Añadirla al listado
            entidades.add(entidad);
        }
        
        // Volver a marcar si lo estuviera previamente
        if (isSelected) {
            tblEntities.getSelectionModel().select(entidad);
        }
        
        // Refrescar el listado
        refreshData();
    }
    @Override
    protected void onDelete(Entidad entidad)
    {
        // Buscarla
        int index = indexOfEntity(entidad.getId());
        if (index >= 0) {
            // Eliminarla
            entidades.remove(index);
        }
        
        // Refrescar el listado
        refreshData();
    }
}