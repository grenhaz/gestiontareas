package org.obarcia.gestiontareas.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Controlador del Splash Screen.
 *
 * @author obarcia
 */
public class SplashScreenController implements Initializable
{
    /**
     * Imagen de loading
     */
    @FXML
    private ImageView imgLoading;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // Animación del loading
        TranslateTransition tt = new TranslateTransition(Duration.seconds(30), imgLoading);
    }
}