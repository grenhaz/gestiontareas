package org.obarcia.gestiontareas;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.obarcia.gestiontareas.components.MessageBox;
import org.obarcia.gestiontareas.components.Language;
import org.obarcia.gestiontareas.controllers.sections.TasksController;

/**
 * Clase principal del sistema.
 * 
 * @author obarcia
 */
public class MainApp extends Application
{
    /**
     * Stage de la aplicación.
     */
    private Stage mainStage;
    
    @Override
    public void start(Stage stage) throws Exception
    {
        showSplash(stage);
    }
    /**
     * Mostrar el Splash Screen.
     * @param initStage Instancia del Stage inicial.
     * @throws IOException 
     */
    private void showSplash(final Stage initStage) throws IOException
    {
        // Cargar el Splash
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/SplashScreen.fxml"), Language.getResource());
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        
        // Configurarlo para transparencias
        initStage.setTitle(Language.getString("APP_TITLE"));
        initStage.getIcons().add(new Image("/images/logo.png"));
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setScene(scene);
        initStage.show();
        
        // Lanzar en Thread (para no bloquear) la carga de la aplicación
        Thread thread = new Thread(() -> {
            // Crear la aplicación
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sections/Tasks.fxml"), Language.getResource());
                Parent rootl = (Parent)loader.load();
                final Scene scene1 = new Scene(rootl);
                scene1.getStylesheets().add("/styles/Styles.css");
                
                // Ocultar el splash y mostrar la aplicación
                Platform.runLater(() -> {
                    // Configurar el stage de la aplicación
                    setUserAgentStylesheet(STYLESHEET_CASPIAN);
                    mainStage = new Stage(StageStyle.DECORATED);
                    mainStage.setTitle(Language.getString("APP_TITLE"));
                    TasksController controller = (TasksController)loader.getController();
                    controller.setStage(mainStage);
                    mainStage.getIcons().add(new Image("/images/logo.png"));
                    mainStage.setScene(scene1);
                    mainStage.setOnCloseRequest((WindowEvent t) -> {
                        Platform.exit();
                        System.exit(0);
                    });
                    
                    // Ocultar el splash
                    initStage.hide();
                    initStage.close();
                    
                    // Mostrar la aplicación
                    mainStage.show();
                });
            } catch (ExceptionInInitializerError | Exception ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, "Exception", ex);
                
                // Mensaje de error y cierre de la aplicación
                Platform.runLater(() -> {
                    initStage.hide();
                });
                
                // Mensaje de error
                MessageBox.error(Language.getString("TITLE_ERROR_APP"), Language.getString("MESSAGE_ERROR_APP"), true);
            }
        });
        thread.start();
    }
    /**
     * Inicialización de la aplicación.
     * @param args Listado de argumentos pasados como parámetros.
     */
    public static void main(String[] args)
    {
        launch(MainApp.class, args);
    }
}