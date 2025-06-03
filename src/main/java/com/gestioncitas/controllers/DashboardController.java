package com.gestioncitas.controllers;

import com.gestioncitas.observer.ColorChangeListener;
import com.gestioncitas.util.ConfiguracionVisual;
import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private BorderPane rootPane;
    @FXML private StackPane paneContenido;

    @FXML private Button btnClientes;
    @FXML private Button btnCitas;
    @FXML private Button btnServicios;
    @FXML private Button btnUsuarios;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;

    private final ConfiguracionVisual cfg = ConfiguracionVisual.getInstancia();

    @FXML
    private void initialize() {
        // 1. Aplicar color de fondo y suscribirse a cambios
        String colorFondo = cfg.getColorFondo();
        rootPane.setStyle("-fx-background-color: " + colorFondo + ";");
        cfg.colorFondoProperty().addListener(new ColorChangeListener(rootPane));

        // 2. Ajustar visibilidad según el rol
        Usuario usuarioActual = Session.getUsuario();
        if (usuarioActual != null) {
            String rol = usuarioActual.getRol();
            boolean esAdmin   = "administrador".equalsIgnoreCase(rol);
            boolean esGerente = "gerente".equalsIgnoreCase(rol);
            boolean esCliente = "cliente".equalsIgnoreCase(rol);

            if (esAdmin) {
                // Admin ve todo
                btnClientes.setVisible(true);    btnClientes.setManaged(true);
                btnCitas.setVisible(true);       btnCitas.setManaged(true);
                btnServicios.setVisible(true);   btnServicios.setManaged(true);
                btnUsuarios.setVisible(true);    btnUsuarios.setManaged(true);
                btnConfiguracion.setVisible(true);btnConfiguracion.setManaged(true);
            } else if (esGerente) {
                // Gerente ve Clientes, Citas, Servicios
                btnClientes.setVisible(true);    btnClientes.setManaged(true);
                btnCitas.setVisible(true);       btnCitas.setManaged(true);
                btnServicios.setVisible(true);   btnServicios.setManaged(true);
                btnUsuarios.setVisible(false);   btnUsuarios.setManaged(false);
                btnConfiguracion.setVisible(false);btnConfiguracion.setManaged(false);
            } else if (esCliente) {
                // Cliente solo ve sus citas
                btnClientes.setVisible(false);    btnClientes.setManaged(false);
                btnCitas.setVisible(true);        btnCitas.setManaged(true);
                btnServicios.setVisible(false);   btnServicios.setManaged(false);
                btnUsuarios.setVisible(false);    btnUsuarios.setManaged(false);
                btnConfiguracion.setVisible(false);btnConfiguracion.setManaged(false);
            } else {
                // Otros roles (si los hay) ven solo Citas
                btnClientes.setVisible(false);    btnClientes.setManaged(false);
                btnCitas.setVisible(true);        btnCitas.setManaged(true);
                btnServicios.setVisible(false);   btnServicios.setManaged(false);
                btnUsuarios.setVisible(false);    btnUsuarios.setManaged(false);
                btnConfiguracion.setVisible(false);btnConfiguracion.setManaged(false);
            }
        } else {
            // Sin sesión, ocultar todo
            btnClientes.setVisible(false);    btnClientes.setManaged(false);
            btnCitas.setVisible(false);       btnCitas.setManaged(false);
            btnServicios.setVisible(false);   btnServicios.setManaged(false);
            btnUsuarios.setVisible(false);    btnUsuarios.setManaged(false);
            btnConfiguracion.setVisible(false);btnConfiguracion.setManaged(false);
        }
    }

    @FXML
    private void onCerrarSesion() {
        try {
            Session.clear();
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

    @FXML private void onClientes() {
        cargarVistaEnContenido("/fxml/cliente_form.fxml");
    }
    @FXML private void onCitas() {
        cargarVistaEnContenido("/fxml/cita_form.fxml");
    }
    @FXML private void onServicios() {
        cargarVistaEnContenido("/fxml/servicio_form.fxml");
    }
    @FXML private void onUsuarios() {
        cargarVistaEnContenido("/fxml/usuario_form.fxml");
    }
    @FXML private void onConfiguracion() {
        cargarVistaEnContenido("/fxml/configuracion_visual.fxml");
    }

    private void cargarVistaEnContenido(String rutaFxml) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaFxml));
            paneContenido.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la vista: " + rutaFxml)
                .showAndWait();
        }
    }
}


