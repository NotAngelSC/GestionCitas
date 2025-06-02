package com.gestioncitas.controllers;

import com.gestioncitas.observer.ColorChangeListener;
import com.gestioncitas.util.ConfiguracionVisual;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import java.io.File;

/**
 * Controlador para el FXML de Configuración Visual.
 * Permite editar: nombre de negocio, colores y logo.
 */
public class ConfiguracionController {

    @FXML private TextField txtNombreNegocio;
    @FXML private ColorPicker cpColorFondo;
    @FXML private ColorPicker cpColorTexto;
    @FXML private ColorPicker cpColorBotones;
    @FXML private Button btnSeleccionarLogo;
    @FXML private ImageView imgLogo;

    private final ConfiguracionVisual cfg = ConfiguracionVisual.getInstancia();

    @FXML
    private void initialize() {
        // 1. Cargar valores actuales desde ConfiguracionVisual
        txtNombreNegocio.setText(cfg.getNombreNegocio());

        // ColorPickers trabajan con javafx.scene.paint.Color,
        // pero en ConfiguracionVisual guardamos hex como String (ej. "#FF0000").
        cpColorFondo.setValue(javafx.scene.paint.Color.web(cfg.getColorFondo()));
        cpColorTexto.setValue(javafx.scene.paint.Color.web(cfg.getColorTexto()));
        cpColorBotones.setValue(javafx.scene.paint.Color.web(cfg.getColorBotones()));

        // Si existe un logo (ruta), cargarlo en ImageView
        String logoPath = cfg.getLogoPath();
        if (logoPath != null && !logoPath.trim().isEmpty()) {
            File f = new File(logoPath);
            if (f.exists()) {
                imgLogo.setImage(new Image(f.toURI().toString()));
            }
        }

        // 2. Registrar listeners para cambios inmediatos

        // Nombre del negocio: actualizar CUANDO el usuario
        // presione Enter en el TextField o pierda el foco.
        txtNombreNegocio.setOnAction(e -> guardarNombre());
        txtNombreNegocio.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) {
                guardarNombre();
            }
        });

        // Color de fondo: al cambiar, guardar en ConfiguracionVisual y notificar listeners
        cpColorFondo.setOnAction(e -> {
            String hex = toHexString(cpColorFondo.getValue());
            cfg.setColorFondo(hex);
        });

        // Color de texto:
        cpColorTexto.setOnAction(e -> {
            String hex = toHexString(cpColorTexto.getValue());
            cfg.setColorTexto(hex);
        });

        // Color de botones:
        cpColorBotones.setOnAction(e -> {
            String hex = toHexString(cpColorBotones.getValue());
            cfg.setColorBotones(hex);
        });

        // 3. Conectar ObservableProperty de colores con listeners para aplicar estilo en tiempo real
        //    Por ejemplo, hacemos que el rootPane del Dashboard cambie de fondo automáticamente.
        //    Para ello, cada controlador que importe ConfiguracionVisual puede registrar su propio listener.

        // 4. Botón para seleccionar logo:
        btnSeleccionarLogo.setOnAction(e -> seleccionarLogo());
    }

    /** Guarda el texto como nuevo nombre de negocio en ConfiguracionVisual */
    private void guardarNombre() {
        String nuevoNombre = txtNombreNegocio.getText().trim();
        if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(cfg.getNombreNegocio())) {
            cfg.setNombreNegocio(nuevoNombre);
        }
    }

    /** Abre un FileChooser para seleccionar imagen de logo y la guarda en ConfiguracionVisual */
    private void seleccionarLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar logo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        // Directorio inicial: si ya hay un logo, abrir ahí
        String rutaExistente = cfg.getLogoPath();
        if (rutaExistente != null && !rutaExistente.trim().isEmpty()) {
            File inicial = new File(rutaExistente).getParentFile();
            if (inicial != null && inicial.exists()) {
                fileChooser.setInitialDirectory(inicial);
            }
        }

        File archivo = fileChooser.showOpenDialog(btnSeleccionarLogo.getScene().getWindow());
        if (archivo != null) {
            String ruta = archivo.getAbsolutePath();
            // Actualizar utilitario y persistir en BD
            cfg.setLogoPath(ruta);
            // Mostrar preview en el ImageView
            imgLogo.setImage(new Image(archivo.toURI().toString()));
        }
    }

    /** Convierte un Color de JavaFX a su representación Hex (#RRGGBB) */
    private String toHexString(javafx.scene.paint.Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}

