package com.gestioncitas.controllers;

import com.gestioncitas.observer.ColorChangeListener;
import com.gestioncitas.util.ConfiguracionVisual;
import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private BorderPane rootPane;       // Contenedor raíz para aplicar estilos dinámicos

    @FXML
    private StackPane paneContenido;   // Panel donde se cargan las vistas hijas

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnCitas;

    @FXML
    private Button btnServicios;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private Button btnCerrarSesion;

    private final ConfiguracionVisual cfg = ConfiguracionVisual.getInstancia();

    @FXML
    private void initialize() {
        // 1. Aplicar color de fondo inicial
        String colorFondo = cfg.getColorFondo();
        rootPane.setStyle("-fx-background-color: " + colorFondo + ";");

        // 2. Suscribirse a cambios de color de fondo para actualizar en tiempo real
        cfg.colorFondoProperty().addListener(new ColorChangeListener(rootPane));

        // 3. Habilitar/Deshabilitar botones según rol del usuario
        Usuario usuarioActual = Session.getUsuario();
        if (usuarioActual != null) {
            String rol = usuarioActual.getRol();
            boolean esAdmin = "administrador".equalsIgnoreCase(rol);
            boolean esGerente = "gerente".equalsIgnoreCase(rol);

            // Clientes y Citas: todos los roles pueden acceder
            btnClientes.setDisable(false);
            btnCitas.setDisable(false);

            // Servicios: solo admin y gerente
            btnServicios.setDisable(!(esAdmin || esGerente));

            // Usuarios: solo admin
            btnUsuarios.setDisable(!esAdmin);

            // Configuración: todos los roles pueden acceder
            btnConfiguracion.setDisable(false);
        } else {
            // Si por algún motivo no hay usuario en sesión, deshabilitar todo
            btnClientes.setDisable(true);
            btnCitas.setDisable(true);
            btnServicios.setDisable(true);
            btnUsuarios.setDisable(true);
            btnConfiguracion.setDisable(true);
        }
    }

    @FXML
    private void onCerrarSesion() {
        try {
            // Limpiar sesión antes de volver al login
            Session.setUsuario(null);

            Stage stage = (Stage) paneContenido.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setTitle("GestionCitas - Login");
            stage.setScene(new Scene(loginRoot));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cerrar sesión.").showAndWait();
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
    private void onServicios() {
        cargarVistaEnContenido("/fxml/servicio_form.fxml");
    }

    @FXML
    private void onUsuarios() {
        cargarVistaEnContenido("/fxml/usuario_form.fxml");
    }

    @FXML
    private void onConfiguracion() {
        cargarVistaEnContenido("/fxml/configuracion_visual.fxml");
    }

    private void cargarVistaEnContenido(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent vista = loader.load();
            paneContenido.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                      "No se pudo cargar la vista: " + rutaFxml).showAndWait();
        }
    }
}

