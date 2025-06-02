package com.gestioncitas.controllers;

import com.gestioncitas.observer.ColorChangeListener;
import com.gestioncitas.util.ConfiguracionVisual;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class DashboardController {

    @FXML
    private BorderPane rootPane;       // Contenedor raíz para aplicar estilos dinámicos

    @FXML
    private StackPane paneContenido;   // Panel donde se cargan las vistas hijas

    private final ConfiguracionVisual cfg = ConfiguracionVisual.getInstancia();

    @FXML
    private void initialize() {
        // 1. Aplicar color de fondo inicial
        String colorFondo = cfg.getColorFondo();
        rootPane.setStyle("-fx-background-color: " + colorFondo + ";");

        // 2. Suscribirse a cambios de color de fondo para actualizar en tiempo real
        cfg.colorFondoProperty().addListener(new ColorChangeListener(rootPane));
    }

    @FXML
    private void onCerrarSesion() {
        try {
            Stage stage = (Stage) paneContenido.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setTitle("GestionCitas - Login");
            stage.setScene(new Scene(loginRoot));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClientes() {
        cargarVistaEnContenido("/fxml/cliente_form.fxml");
    }

    @FXML
    private void onCitas() {
        cargarVistaEnContenido("/fxml/cita_form.fxml");
    }

    @FXML
    private void onConfiguracion() {
        cargarVistaEnContenido("/fxml/configuracion_visual.fxml");
    }

    private void cargarVistaEnContenido(String rutaFxml) {
        try {
            Node vista = FXMLLoader.load(getClass().getResource(rutaFxml));
            paneContenido.getChildren().clear();
            paneContenido.getChildren().add(vista);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la vista: " + rutaFxml).showAndWait();
        }
    }
}
