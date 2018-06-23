package org.obarcia.gestiontareas.controllers.sections;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import org.obarcia.gestiontareas.components.Language;
import org.obarcia.gestiontareas.components.ListTable;
import org.obarcia.gestiontareas.components.TableResizePolicy;
import org.obarcia.gestiontareas.controllers.listeners.ControllerManagerListener;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;
import org.obarcia.gestiontareas.services.GestionService;
import org.obarcia.gestiontareas.services.GestionServiceImpl;

/**
 * Controlador para los listados de Tareas cerradas.
 * 
 * @author obarcia
 */
public class TasksClosedController extends SectionController
{
    /**
     * Instancia del servicio.
     */
    private final GestionService service = GestionServiceImpl.getInstance();
    /**
     * Listado de tareas.
     */
    private final ObservableList<Tarea> tareas = FXCollections.observableArrayList();
    /**
     * Offset del listado.
     */
    private int offset = 0;
    /**
     * Tamaño del listado.
     */
    private final int size = 20;
    /**
     * Número total de registros.
     */
    private int totalRecords = 0;
    /**
     * Texto del filtro.
     */
    private String filterString = "";
    /**
     * Listado de sorting.
     */
    private List<String> sorting = new ArrayList<>();
    /**
     * Menú contextual
     */
    private ContextMenu ctxMenu;
    
    @FXML
    private TableView<Tarea> tblTasks;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btSearch;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btPageFirst;
    @FXML
    private Button btPagePrevious;
    @FXML
    private Button btPageNext;
    @FXML
    private Button btPageLast;
    @FXML
    private ComboBox cbPage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // Inicialización de la BBDD
        service.init();
        
        super.initialize(url, rb);
        
        // Cargar los datos
        sorting.add("actualizacion desc");
        ListTable<Tarea> l = service.getTareasCerradas(offset, size, filterString, sorting.toArray(new String[sorting.size()]));
        totalRecords = l.getTotal();
        tareas.addAll(l.getRecords());
        
        // Crear la tabla
        tblTasks.setColumnResizePolicy(new TableResizePolicy());
        
        // Definición de columnas
        TableColumn colId = new TableColumn(Language.getString("COLUMN_ID"));
        TableColumn colEntity = new TableColumn(Language.getString("COLUMN_ENTITY"));
        TableColumn colTitle = new TableColumn(Language.getString("COLUMN_TITLE"));
        TableColumn colStatus = new TableColumn(Language.getString("COLUMN_STATUS"));
        TableColumn colUpdate = new TableColumn("");
        colUpdate.setGraphic(new ImageView("/images/clock.png"));
        
        // Asignar la columna de ordenación
        colId.setUserData("id");
        colEntity.setUserData("entidad");
        colTitle.setUserData("titulo");
        colStatus.setUserData("estado");
        colUpdate.setUserData("actualizacion");
        
        // Sizes
        colId.setMinWidth(64);
        colId.setMaxWidth(120);
        colEntity.setMinWidth(150);
        colEntity.setMaxWidth(400);
        colTitle.setMinWidth(300);
        colStatus.setMinWidth(80);
        colStatus.setMaxWidth(80);
        colStatus.setResizable(false);
        colUpdate.setMinWidth(100);
        colUpdate.setMaxWidth(100);
        colUpdate.setResizable(false);
        colUpdate.setStyle("-fx-alignment: BASELINE_CENTER;");
        
        // Cell factory
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEntity.setCellValueFactory(new PropertyValueFactory<>("entidad"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("estadoFormateado"));
        colUpdate.setCellValueFactory(new PropertyValueFactory<>("actualizacion"));
        
        // Celda de estado con color
        colStatus.setCellFactory(new Callback<TableColumn<Tarea, String>, TableCell<Tarea, String>>() {
            @Override
            public TableCell<Tarea, String> call(TableColumn<Tarea, String> p)
            {
                return new TableCell<Tarea, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);

                            Tarea tarea = getTableView().getItems().get(getIndex());
                            String estado = tarea.getEstado();
                            if (estado != null && !estado.isEmpty()) {
                                switch (estado) {
                                    case "ABI":
                                        setStyle("-fx-background-color:#c2dfff;-fx-text-fill:#000000;-fx-highlight-text-fill:#000000;");
                                        break;
                                    case "PEN":
                                        setStyle("-fx-background-color:#e3b7eb;-fx-text-fill:#000000;-fx-highlight-text-fill:#000000;");
                                        break;
                                    case "RES":
                                        setStyle("-fx-background-color:#d2f5b0;-fx-text-fill:#000000;-fx-highlight-text-fill:#000000;");
                                        break;
                                    case "CER":
                                        setStyle("-fx-background-color:#CCCCCC;-fx-text-fill:#000000;-fx-highlight-text-fill:#000000;");
                                        break;
                                    default:
                                        setStyle("");
                                }
                            } else {
                                setStyle("");
                            }
                        }
                    }
                };
            }
        });
        // Columna de actualizacion
        colUpdate.setCellFactory(new Callback<TableColumn<Tarea, Timestamp>, TableCell<Tarea, Timestamp>>() {
            @Override
            public TableCell<Tarea, Timestamp> call(TableColumn<Tarea, Timestamp> p)
            {
                return new TableCell<Tarea, Timestamp>() {
                    @Override
                    protected void updateItem(Timestamp item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            Tarea tarea = getTableView().getItems().get(getIndex());
                            setText(tarea.getActualizacionFormateada());
                        }
                    }
                };
            }
        });
        
        // Añadir las columnas
        tblTasks.getColumns().addAll(colId, colEntity, colTitle, colStatus, colUpdate);
        
        // Evento de click
        tblTasks.setRowFactory(tv -> {
            TableRow<Tarea> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Tarea tarea = row.getItem();
                
                // Guarda en el context menú la tarea
                ctxMenu.setUserData(tarea);
                
                // Eventos de botones
                if (tarea != null) {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                        clickTask(tarea);
                    }
                }
            });
            return row;
        });
        
        // Evento del buscador
        txtSearch.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            offset = 0;
            filterString = newValue.toUpperCase();
            refreshData();
        });
        
        // Ordenación personalizada
        tblTasks.sortPolicyProperty().set(new Callback<TableView<Tarea>, Boolean>() {
            @Override
            public Boolean call(TableView<Tarea> param) {
                sorting.clear();
                for (TableColumn c: param.getSortOrder()) {
                    sorting.add(c.getUserData() + " " + (c.getSortType() == SortType.ASCENDING ? "asc" : "desc"));
                }
                refreshData();
                
                return true;
            }
        });

        // Asignar los items a la tabla
        tblTasks.setItems(tareas);
        
        // Orden por defecto
        colUpdate.setSortType(TableColumn.SortType.DESCENDING);
        tblTasks.getSortOrder().addAll(colUpdate);
        
        // Refrescar la paginación
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
        
        Menu menu;
        MenuItem item;
        
        // Item de abrir
        item = new MenuItem(Language.getString("MENU_OPEN"));
        item.setOnAction((ActionEvent event) -> {
            Tarea tarea = (Tarea)ctxMenu.getUserData();
            if (tarea != null) {
                clickTask(tarea);
            }
        });
        ctxMenu.getItems().add(item);
        // Menú de estado
        ctxMenu.getItems().add(new SeparatorMenuItem());
        // rEOPEN
        item = new MenuItem(Language.getString("MENU_REOPEN"));
        item.setOnAction((ActionEvent event) -> {
            Tarea tarea = (Tarea)ctxMenu.getUserData();
            if (tarea != null) {
                reopenTask(tarea);
            }
        });
        ctxMenu.getItems().add(item);
        
        // Menu de estado
        tblTasks.setContextMenu(ctxMenu);
        
        tblTasks.setOnContextMenuRequested((ContextMenuEvent event) -> {
            Tarea tarea = (Tarea)ctxMenu.getUserData();
            if (tarea == null) {
                ctxMenu.hide();
            }
        });
    }
    /**
     * Reabre la tarea.
     * @param tarea Instancia de la tarea.
     */
    private void reopenTask(Tarea tarea)
    {
        if (tarea != null) {
            // Actualizar el estado
            tarea.setEstado(Tarea.STATUS_ABIERTA);
            service.save(tarea);

            // Evento de actualizar la tarea
            ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                l.update(false, tarea);
            });
        }
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
     * Acción de página inicial.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionPageFirst(ActionEvent event)
    {
        firstPage();
    }
    /**
     * Acción de página anterior.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionPagePrevious(ActionEvent event)
    {
        previousPage();
    }
    /**
     * Acción de página siguiente.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionPageNext(ActionEvent event)
    {
        nextPage();
    }
    /**
     * Acción de página final.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionPageLast(ActionEvent event)
    {
        lastPage();
    }
    /**
     * Acci´no de cambio de página.
     * @param event Instanci del evento.
     */
    @FXML
    private void actionPageSelect(ActionEvent event)
    {
        if (!cbPage.isDisabled()) {
            goToPage(((int)cbPage.getValue()) - 1);
        }
    }
    /**
     * Acción al pulsar sobre una tarea.
     * @param tarea Instancia de la tarea.
     */
    private void clickTask(Tarea tarea)
    {
        openWindow(Language.getString("TITLE_TASK") + " #" + tarea.getId(), "sections/Task.fxml", tarea);
    }
    /**
     * Página inicial.
     */
    private void firstPage()
    {
        // Calcular el offset
        offset = 0;

        // Refrescar los datos
        refreshData();
    }
    /**
     * Página previa.
     */
    private void previousPage()
    {
        // Calcular el offset
        if (offset - size >= 0) {
            offset -= size;

            // Refrescar los datos
            refreshData();
        }
    }
    /**
     * Ir a una pagína.
     * @param page Índice de la página.
     */
    private void goToPage(int page)
    {
        if (page * size < totalRecords) {
            // Calcular el offset
            offset = (page * size);

            // Refrescar los datos
            refreshData();
        }
    }
    /**
     * Página siguiente.
     */
    private void nextPage()
    {
        if (offset + size < totalRecords) {
            // Calcular el offset
            offset += size;

            // Refrescar los datos
            refreshData();
        }
    }
    /**
     * Página final.
     */
    private void lastPage()
    {
        // Calcular el offset
        offset = ((int)(totalRecords / size)) * size;

        // Refrescar los datos
        refreshData();
    }
    /**
     * Refrescar los datos.
     */
    private void refreshData()
    {
        // Obtener las tareas
        ListTable<Tarea> l = service.getTareasCerradas(offset, size, filterString, sorting.toArray(new String[sorting.size()]));
        totalRecords = l.getTotal();
        tareas.clear();
        tareas.addAll(l.getRecords());
        
        if (offset > totalRecords) {
            // Recolocar el offset
            offset = (((int)(totalRecords / size)) * size);
                    
            refreshData();
            
            return;
        }
        
        tblTasks.refresh();
        
        refreshInternal();
    }
    /**
     * Refresco interno.
     */
    private void refreshInternal()
    {
        // Estado de la tabla
        lblStatus.setText(Language.getString("TEXT_TABLE_STATUS_PAGING", new Object[] {offset + 1, offset + tareas.size(), totalRecords}));
        
        // Habilitar o no el botón de limpiar la búsqueda
        btSearch.setDisable(filterString.isEmpty());
        
        // Paginación
        refreshPagination();
    }
    /**
     * Refrescar la paginación.
     */
    private void refreshPagination()
    {
        // Refrescar los botones de paginación
        btPageFirst.setDisable(offset == 0);
        btPagePrevious.setDisable(offset == 0);
        btPageNext.setDisable(offset + size >= totalRecords);
        btPageLast.setDisable(offset + size >= totalRecords);
        
        // Paginas en el ComboBox
        cbPage.setDisable(true);
        cbPage.getItems().clear();
        int pages = (totalRecords / size) + 1;
        for (int i = 0; i < pages; i ++) {
            cbPage.getItems().add(i + 1);
        }
        cbPage.setValue((offset / size) + 1);
        cbPage.setDisable(pages <= 1);
    }
    @Override
    protected void onUpdate(boolean newRecord, Tarea tarea)
    {
        // Refrescar el listado
        refreshData();
    }
    @Override
    protected void onUpdate(boolean newRecord, Entidad entidad)
    {
        // Refrescar el listado
        refreshData();
    }
}