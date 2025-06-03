package com.gestioncitas.controllers;

import com.gestioncitas.dao.UsuarioDAO;
import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.HashUtils;
import com.gestioncitas.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void initialize() {
        // Al iniciar, ocultar el mensaje de error
        lblError.setVisible(false);
    }

    @FXML
    private void onEntrar(ActionEvent event) {
        try {
            String usuarioText = txtUsuario.getText().trim();
            String passText    = txtContrasena.getText().trim();

            if (usuarioText.isEmpty() || passText.isEmpty()) {
                lblError.setText("Debes ingresar usuario y contraseña.");
                lblError.setVisible(true);
                return;
            }

            String sha1 = HashUtils.sha1(passText);
            Usuario u   = usuarioDAO.autenticar(usuarioText, sha1);

            if (u != null) {
                // Guardar en sesión antes de cargar el Dashboard
                Session.setUsuario(u);

                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));

                stage.setTitle("Dashboard - " + u.getNombre());
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                lblError.setVisible(false);
            } else {
                lblError.setText("Usuario o contraseña incorrectos.");
                lblError.setVisible(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblError.setText("Error de conexión o inesperado.");
            lblError.setVisible(true);
        }
    }
}
