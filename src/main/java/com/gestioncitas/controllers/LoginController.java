package com.gestioncitas.controllers;

import com.gestioncitas.dao.UsuarioDAO;
import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.HashUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void onEntrar(ActionEvent event) {
        try {
            String usuario = txtUsuario.getText().trim();
            String pass = txtContrasena.getText().trim();
            if (usuario.isEmpty() || pass.isEmpty()) {
                lblError.setText("Debes ingresar usuario y contraseña.");
                lblError.setVisible(true);
                return;
            }

            // Generar SHA-1
            String sha1 = HashUtils.sha1(pass);

            Usuario u = usuarioDAO.autenticar(usuario, sha1);
            if (u != null) {
                // Login exitoso: cargar el Dashboard (dashboard.fxml)
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
