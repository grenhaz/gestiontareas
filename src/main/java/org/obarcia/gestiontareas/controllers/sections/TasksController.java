package org.obarcia.gestiontareas.controllers.sections;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import org.obarcia.gestiontareas.components.Language;
import org.obarcia.gestiontareas.components.MessageBox;
import org.obarcia.gestiontareas.components.TableResizePolicy;
import org.obarcia.gestiontareas.components.Util;
import org.obarcia.gestiontareas.controllers.listeners.ControllerManagerListener;
import org.obarcia.gestiontareas.models.Entidad;
import org.obarcia.gestiontareas.models.Tarea;
import org.obarcia.gestiontareas.services.GestionService;
import org.obarcia.gestiontareas.services.GestionServiceImpl;

/**
 * Controlador de las tareas.
 *
 * @author obarcia
 */
public class TasksController extends SectionController
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
     * Listado de tareas (Filtro).
     */
    private final ObservableList<Tarea> tareasFiltered = FXCollections.observableArrayList();
    /**
     * Fecha a 7 días.
     */
    private Date dtPlus7;
    /**
     * Texto del filtro.
     */
    private String filterString = "";
    /**
     * Si se filtran las tareas por límite.
     */
    private boolean filterLimited = false;
    /**
     * Menú contextual
     */
    private ContextMenu ctxMenu;
    /**
     * Items del estado.
     */
    private HashMap<String, MenuItem> miStatus;
    /**
     * Items de prioridad.
     */
    private MenuItem[] miPriority;
    /**
     * Listado de warnings.
     */
    private final List<Tarea> alertWarning = new ArrayList<>();
    /**
     * Listado de dangers.
     */
    private final List<Tarea> alertDanger = new ArrayList<>();
    
    @FXML
    private TableView tblTasks;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btSearch;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblAlertsWarnings;
    @FXML
    private Label lblAlertsDangers;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // Inicialización de la BBDD
        service.init();
        
        super.initialize(url, rb);
        
        // Cargar los datos
        tareas.addAll(service.getTareas());
        tareasFiltered.addAll(tareas);
        
        // Crear la tabla
        tblTasks.setColumnResizePolicy(new TableResizePolicy());
        
        // Fecha menos 7 días
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 7);
        dtPlus7 = cal.getTime();
        
        // Definición de columnas
        TableColumn colId = new TableColumn(Language.getString("COLUMN_ID"));
        TableColumn colPriority = new TableColumn("");
        TableColumn colEntity = new TableColumn(Language.getString("COLUMN_ENTITY"));
        TableColumn colTitle = new TableColumn(Language.getString("COLUMN_TITLE"));
        TableColumn colStatus = new TableColumn(Language.getString("COLUMN_STATUS"));
        TableColumn colUpdate = new TableColumn("");
        colUpdate.setGraphic(new ImageView("/images/clock.png"));
        TableColumn colLimit = new TableColumn(Language.getString("COLUMN_LIMIT"));
        
        // Sizes
        colId.setMinWidth(64);
        colId.setMaxWidth(120);
        colPriority.setResizable(false);
        colPriority.setMinWidth(24);
        colPriority.setMaxWidth(24);
        colPriority.setStyle("-fx-alignment: BASELINE_CENTER;");
        colEntity.setMinWidth(150);
        colEntity.setMaxWidth(400);
        colTitle.setMinWidth(250);
        colStatus.setResizable(false);
        colStatus.setMinWidth(80);
        colStatus.setMaxWidth(80);
        colUpdate.setResizable(false);
        colUpdate.setMinWidth(90);
        colUpdate.setMaxWidth(90);
        colUpdate.setStyle("-fx-alignment: BASELINE_CENTER;");
        colLimit.setResizable(false);
        colLimit.setMinWidth(90);
        colLimit.setMaxWidth(90);
        colLimit.setStyle("-fx-alignment: BASELINE_CENTER;");
        
        // Cell factory
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colEntity.setCellValueFactory(new PropertyValueFactory<>("entidad"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("estadoFormateado"));
        colUpdate.setCellValueFactory(new PropertyValueFactory<>("actualizacion"));
        colLimit.setCellValueFactory(new PropertyValueFactory<>("limite"));
        
        // Columna de prioridad
        colPriority.setCellFactory(new Callback<TableColumn<Tarea, Boolean>, TableCell<Tarea, Boolean>>() {
            @Override
            public TableCell<Tarea, Boolean> call(TableColumn<Tarea, Boolean> p)
            {
                return new TableCell<Tarea, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(null);
                        if (item == null || empty || item.equals(Boolean.FALSE)) {
                            setGraphic(null);
                        } else {
                            setGraphic(new ImageView("/images/priority.png"));
                        }
                    }
                };
            }
        });
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
        colUpdate.setCellFactory(new Callback<TableColumn<Tarea, Object>, TableCell<Tarea, Object>>() {
            @Override
            public TableCell<Tarea, Object> call(TableColumn<Tarea, Object> p)
            {
                return new TableCell<Tarea, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
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
        // Columna de límite
        colLimit.setCellFactory(new Callback<TableColumn<Tarea, Object>, TableCell<Tarea, Object>>() {
            @Override
            public TableCell<Tarea, Object> call(TableColumn<Tarea, Object> p)
            {
                return new TableCell<Tarea, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            Tarea tarea = getTableView().getItems().get(getIndex());
                            setText(tarea.getLimiteFormateada());
                            if (Util.isToday(tarea.getLimite()) || tarea.getLimite().before(Util.NOW)) {
                                setStyle("-fx-background-color:#DD8888;-fx-text-fill:#000000;-fx-highlight-text-fill:#000000;");
                            } else if (tarea.getLimite().after(Util.NOW) && tarea.getLimite().before(dtPlus7)) {
                                setStyle("-fx-background-color:#ffcc00;-fx-text-fill:#000000;-fx-highlight-text-fill:#000000;");
                            } else {
                                setStyle(null);
                            }
                        }
                    }
                };
            }
        });
        
        // Añadir las columnas
        tblTasks.getColumns().addAll(colId, colPriority, colEntity, colTitle, colStatus, colUpdate, colLimit);
        
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
            String nv = newValue.toUpperCase();
            if (!filterString.equals(nv)) {
                filterString = nv;
                refreshData();
            }
        });

        // Asignar los items a la tabla
        tblTasks.setItems(tareasFiltered);
        
        // Orden por defecto
        colUpdate.setSortType(TableColumn.SortType.DESCENDING);
        tblTasks.getSortOrder().addAll(colUpdate);
        tblTasks.sort();
        
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
        menu = new Menu(Language.getString("MENU_STATUS"));
        ctxMenu.getItems().add(menu);
        
        // Estados
        miStatus = new HashMap<>();
        for (String status: Tarea.STATUS) {
            item = new MenuItem(Language.getString("STATUS_" + status));
            item.setUserData(status);
            item.setOnAction((ActionEvent event) -> {
                Tarea tarea = (Tarea)ctxMenu.getUserData();
                if (tarea != null) {
                    updateTaskStatus(tarea, (String)((MenuItem)event.getSource()).getUserData());
                }
            });
            miStatus.put(status, item);
            menu.getItems().add(item);
        }
        // Prioridad
        menu = new Menu(Language.getString("MENU_PRIORITY"));
        ctxMenu.getItems().add(menu);
        miPriority = new MenuItem[2];
        miPriority[0] = new MenuItem(Language.getString("TEXT_YES"));
        miPriority[0].setOnAction((ActionEvent event) -> {
            Tarea tarea = (Tarea)ctxMenu.getUserData();
            if (tarea != null) {
                updateTaskPriority(tarea, Boolean.TRUE);
            }
        });
        menu.getItems().add(miPriority[0]);
        miPriority[1] = new MenuItem(Language.getString("TEXT_NO"));
        miPriority[1].setOnAction((ActionEvent event) -> {
            Tarea tarea = (Tarea)ctxMenu.getUserData();
            if (tarea != null) {
                updateTaskPriority(tarea, Boolean.FALSE);
            }
        });
        menu.getItems().add(miPriority[1]);
        
        // Asignar el context menú
        tblTasks.setContextMenu(ctxMenu);
        
        // Evento de apertura del context menú
        tblTasks.setOnContextMenuRequested((ContextMenuEvent event) -> {
            Tarea tarea = (Tarea)ctxMenu.getUserData();
            if (tarea != null) {
                for (String status: Tarea.STATUS) {
                    if (tarea.getEstado().equals(status)) {
                        miStatus.get(status).setDisable(true);
                    } else {
                        miStatus.get(status).setDisable(false);
                    }
                }
                if (tarea.getPrioridad().equals(Boolean.TRUE)) {
                    miPriority[0].setDisable(true);
                    miPriority[1].setDisable(false);
                } else {
                    miPriority[0].setDisable(false);
                    miPriority[1].setDisable(true);
                }
            } else {
                ctxMenu.hide();
            }
        });
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
     * Actualiza el estado de la tarea.
     * @param tarea Instancia de la tarea.
     * @param status Nuevo estado.
     */
    private void updateTaskStatus(Tarea tarea, String status)
    {
        if (tarea != null) {
            // Actualizar el estado
            tarea.setEstado(status);
            service.save(tarea);

            // Evento de actualizar la tarea
            ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                l.update(false, tarea);
            });
        }
    }
    /**
     * Actualiza la prioridad de la tarea.
     * @param tarea Instancia de la tarea.
     * @param priority Nuevo prioridad.
     */
    private void updateTaskPriority(Tarea tarea, Boolean priority)
    {
        if (tarea != null) {
            // Actualizar el estado
            tarea.setPrioridad(priority);
            service.save(tarea);

            // Evento de actualizar la tarea
            ControllerManagerListener.getInstance().getListeners().stream().forEach((l) -> {
                l.update(false, tarea);
            });
        }
    }
    /**
     * Ver mensaje de acerca de.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionAbout(ActionEvent event)
    {
        MessageBox.message(Language.getString("TITLE_ABOUT"), Language.getString("MESSAGE_ABOUT", new Object[]{
            service.getConfig("version", "1")
        }));
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
     * Acción de mostrar o no las tareas con fecha de límite.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionShowLimited(ActionEvent event)
    {
        if (event.getSource() instanceof ToggleButton) {
            ToggleButton bt = (ToggleButton)event.getSource();
            filterLimited = bt.isSelected();
            
            // Refrescar los datos
            refreshData();
        }
    }
    /**
     * Acción de abrir la ventana de entidades.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionEntities(ActionEvent event)
    {
        openWindow(Language.getString("TITLE_ENTITIES"), "sections/Entities.fxml", null);
    }
    /**
     * Acción de abrir la ventana de tareas cerradas.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionTasksClosed(ActionEvent event)
    {
        openWindow(Language.getString("TITLE_TASKS_CLOSED"), "sections/TasksClosed.fxml", null);
    }
    /**
     * Acción de cerrar el sistema.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionClose(ActionEvent event)
    {
        getStage().hide();
        Platform.exit();
        System.exit(0);
    }
    /**
     * Acción de crear una nueva tarea.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionNewTask(ActionEvent event)
    {
        Tarea tarea = new Tarea();
        openWindow(Language.getString("TITLE_TASK_NEW"), "sections/Task.fxml", tarea);
    }
    /**
     * Acción de crear una nueva entidad.
     * @param event Instancia del evento.
     */
    @FXML
    private void actionNewEntity(ActionEvent event)
    {
        Entidad entidad = new Entidad();
        openWindow(Language.getString("TITLE_ENTITY_NEW"), "sections/Entity.fxml", entidad);
    }
    /**
     * Refrescar los datos.
     */
    private void refreshData()
    {
        tareasFiltered.clear();
        if (!filterString.isEmpty() || filterLimited) {
            for (Tarea t: tareas) {
                if (!filterString.isEmpty()) {
                    String id = t.getId().toString();
                    String titulo = (t.getTitulo() != null ? t.getTitulo().toUpperCase() : "");
                    String contenido = (t.getContenido() != null ? t.getContenido().toUpperCase() : "");
                    String entidadDoi = (t.getEntidad() != null && t.getEntidad().getDoi() != null ? t.getEntidad().getDoi().toUpperCase() : "");
                    String entidadNombre = (t.getEntidad() != null && t.getEntidad().getNombre() != null ? t.getEntidad().getNombre().toUpperCase() : "");

                    if (id.contains(filterString) ||
                        titulo.contains(filterString) || contenido.contains(filterString) ||
                        entidadDoi.contains(filterString) || entidadNombre.contains(filterString)) {
                        if (filterLimited && t.getLimite() != null) {
                            tareasFiltered.add(t);
                        } else if (!filterLimited) {
                            tareasFiltered.add(t);
                        }
                    }
                } else {
                    if (filterLimited && t.getLimite() != null) {
                        tareasFiltered.add(t);
                    } else if (!filterLimited) {
                        tareasFiltered.add(t);
                    }
                }
            }
        } else {
            tareasFiltered.addAll(tareas);
        }
        tblTasks.refresh();
        tblTasks.sort();
        
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
            lblStatus.setText(Language.getString("TEXT_TABLE_STATUS_FILTER", new Object[] {tareasFiltered.size(), tareas.size()}));
        } else {
            lblStatus.setText(Language.getString("TEXT_TABLE_STATUS", new Object[] {tareasFiltered.size()}));
        }
        
        // Habilitar o no el botón de limpiar la búsqueda
        btSearch.setDisable(filterString.isEmpty());
        
        // Calcular las alertas
        calculateAlerts();
    }
    /**
     * Buscar el índice en el listado de una tarea por su identificador.
     * @param id Identificador.
     * @return Índice o -1 en caso de no encontrarlo.
     */
    private int indexOfTask(Integer id)
    {
        for (int i = 0; i < tareas.size(); i ++) {
            if (tareas.get(i).getId().equals(id)) {
                return i;
            }
        }
        
        return -1;
    }
    @Override
    protected void onUpdate(boolean newRecord, Tarea tarea)
    {
        // Comprobar si es la marcada
        boolean isSelected = false;
        Tarea tsel = (Tarea)tblTasks.getSelectionModel().getSelectedItem();
        if (tsel != null) {
            isSelected = (tsel.getId().equals(tarea.getId()));
        }
        boolean closed = (tarea.getEstado().equals("RES") || tarea.getEstado().equals("CER"));
        
        // Hay que refrecar el listado
        if (!newRecord) {
            // Buscarla
            int index = indexOfTask(tarea.getId());
            if (index >= 0) {
                if (closed) {
                    // Eliminarla del listado
                    tareas.remove(index);
                } else {
                    // Cambiar la tarea
                    tareas.set(index, tarea);
                }
            } else {
                if (!closed) {
                    // Añadirla al listado
                    tareas.add(tarea);
                }
            }
        } else {
            if (!closed) {
                // Añadirla al listado
                tareas.add(tarea);
            }
        }
        
        // Volver a marcar si lo estuviera previamente
        if (isSelected) {
            tblTasks.getSelectionModel().select(tarea);
        }
        
        // Refrescar el listado
        refreshData();
    }
    @Override
    protected void onUpdate(boolean newRecord, Entidad entidad)
    {
        // Actualizar las entidades a modificar
        for (int i = 0; i < tareas.size(); i ++) {
            Entidad e = tareas.get(i).getEntidad();
            if (e != null && e.getId().equals(entidad.getId())) {
                tareas.get(i).setEntidad(entidad);
            }
        }
        
        // Refrescar el listado
        refreshData();
    }
    /**
     * Calcula las alertas sobre las tareas.
     */
    private void calculateAlerts()
    {
        // Limpiar alertas previas
        alertWarning.clear();
        alertDanger.clear();
        
        // Calcular las alertas actuales
        for (Tarea tarea: tareas) {
            if (tarea.getLimite() != null) {
                if (Util.isToday(tarea.getLimite()) || tarea.getLimite().before(Util.NOW)) {
                    // Danger
                    alertDanger.add(tarea);
                } else if (tarea.getLimite().after(Util.NOW) && tarea.getLimite().before(dtPlus7)) {
                    // Warning
                    alertWarning.add(tarea);
                }
            }
        }
        
        // Warnings
        lblAlertsWarnings.setText("" + (alertWarning.size() > 0 ? alertWarning.size() : "-"));
        // Dangers
        lblAlertsDangers.setText("" + (alertDanger.size() > 0 ? alertDanger.size() : "-"));
    }
}