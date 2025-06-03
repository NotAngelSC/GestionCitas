package com.gestioncitas.controllers;

import com.gestioncitas.dao.UsuarioDAO;
import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.ConfiguracionVisual;
import com.gestioncitas.util.HashUtils;
import com.gestioncitas.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Ajustado para usar ObservableProperty<String> en lugar de ChangeListener de JavaFX.
 */
public class LoginController {

    @FXML private AnchorPane rootPane;
    @FXML private ImageView imgLogo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    @FXML private Button btnEntrar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ConfiguracionVisual cfg  = ConfiguracionVisual.getInstancia();

    @FXML
    private void initialize() {
        // 1) Ocultar inicialmente mensaje de error
        lblError.setVisible(false);
        lblError.setManaged(false);

        // 2) Logo: suscribirse a cambios en logoPathProperty()
        cfg.logoPathProperty().addListener(nuevoLogoPath -> {
            if (nuevoLogoPath != null && !nuevoLogoPath.trim().isEmpty()) {
                try {
                    Image img = new Image("file:" + nuevoLogoPath, 120, 80, true, true);
                    imgLogo.setImage(img);
                    imgLogo.setVisible(true);
                    imgLogo.setManaged(true);
                } catch (Exception e) {
                    imgLogo.setVisible(false);
                    imgLogo.setManaged(false);
                }
            } else {
                imgLogo.setVisible(false);
                imgLogo.setManaged(false);
            }
        });
        // Cargar logo al iniciar si ya existe ruta en configuración
        String rutaInicial = cfg.getLogoPath();
        if (rutaInicial != null && !rutaInicial.trim().isEmpty()) {
            try {
                Image imgIni = new Image("file:" + rutaInicial, 120, 80, true, true);
                imgLogo.setImage(imgIni);
                imgLogo.setVisible(true);
                imgLogo.setManaged(true);
            } catch (Exception e) {
                imgLogo.setVisible(false);
                imgLogo.setManaged(false);
            }
        } else {
            imgLogo.setVisible(false);
            imgLogo.setManaged(false);
        }

        // 3) Color de fondo: aplicar y suscribirse a cambios
        //    ObservableProperty<String>.addListener recibe solo el nuevo valor
        rootPane.setStyle("-fx-background-color: " + cfg.getColorFondo() + ";");
        cfg.colorFondoProperty().addListener(nuevoColorFondo ->
            rootPane.setStyle("-fx-background-color: " + nuevoColorFondo + ";")
        );

        // 4) Color de texto: aplicar a lblError y suscribirse a cambios
        lblError.setStyle("-fx-text-fill: " + cfg.getColorTexto() + ";");
        cfg.colorTextoProperty().addListener(nuevoColorTexto ->
            lblError.setStyle("-fx-text-fill: " + nuevoColorTexto + ";")
        );

        // 5) Color de botones: aplicar a btnEntrar y suscribirse a cambios
        applyButtonColor(cfg.getColorBotones());
        cfg.colorBotonesProperty().addListener(nuevoColorBotones ->
            applyButtonColor(nuevoColorBotones)
        );
    }

    private void applyButtonColor(String colorHex) {
        btnEntrar.setStyle(
            "-fx-background-color: " + colorHex + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );
    }

    @FXML
    private void onEntrar() {
        try {
            String usuarioText = txtUsuario.getText().trim();
            String passText    = txtContrasena.getText().trim();

            if (usuarioText.isEmpty() || passText.isEmpty()) {
                lblError.setText("Debes ingresar usuario y contraseña.");
                lblError.setVisible(true);
                lblError.setManaged(true);
                return;
            }

            String sha1 = HashUtils.sha1(passText);
            Usuario u   = usuarioDAO.autenticar(usuarioText, sha1);

            if (u != null) {
                Session.setUsuario(u);

                Stage stage = (Stage) rootPane.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
                stage.setTitle("Dashboard - " + u.getNombre());
                stage.setScene(new Scene(root));
                stage.centerOnScreen();

                lblError.setVisible(false);
                lblError.setManaged(false);
            } else {
                lblError.setText("Usuario o contraseña incorrectos.");
                lblError.setVisible(true);
                lblError.setManaged(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblError.setText("Error de conexión o inesperado.");
            lblError.setVisible(true);
            lblError.setManaged(true);
        }
    }
}
