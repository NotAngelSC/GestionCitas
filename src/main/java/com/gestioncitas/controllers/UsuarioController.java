package com.gestioncitas.controllers;

import com.gestioncitas.dao.UsuarioDAO;
import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.HashUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    public TableView<Usuario> tableUsuarios;
    public TableColumn<Usuario, Integer> colIdUsuario;
    public TableColumn<Usuario, String> colNombre;
    public TableColumn<Usuario, String> colUsuario;
    public TableColumn<Usuario, String> colRol;
    public TableColumn<Usuario, Boolean> colActivo;

    public Button btnNuevoUsuario;
    public Button btnEditarUsuario;
    public Button btnEliminarUsuario;
    public Button btnCambiarContrasena;

    /**
     * Se invoca automáticamente al cargar el FXML.
     * Configura las columnas y carga la lista de usuarios.
     */
    public void initialize() {
        // Mapear columnas a propiedades de Usuario
        colIdUsuario.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getIdUsuario()).asObject()
        );
        colNombre.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getNombre())
        );
        colUsuario.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getUsuario())
        );
        colRol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRol())
        );
        colActivo.setCellValueFactory(cellData ->
            new SimpleBooleanProperty(cellData.getValue().isActivo()).asObject()
        );

        tableUsuarios.setItems(listaUsuarios);
        cargarListaUsuarios();

        // Deshabilitar botones si no hay selección
        btnEditarUsuario.disableProperty().bind(
            tableUsuarios.getSelectionModel().selectedItemProperty().isNull()
        );
        btnEliminarUsuario.disableProperty().bind(
            tableUsuarios.getSelectionModel().selectedItemProperty().isNull()
        );
        btnCambiarContrasena.disableProperty().bind(
            tableUsuarios.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    /**
     * Carga todos los usuarios desde la base de datos.
     */
    private void cargarListaUsuarios() {
        listaUsuarios.clear();
        try {
            listaUsuarios.addAll(usuarioDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }

    /**
     * Abre un diálogo para crear un nuevo usuario.
     * Recoge datos (nombre, usuario, contraseña, rol, activo), valida y guarda.
     */
    public void onNuevoUsuario() {
        Usuario nuevo = mostrarDialogoUsuario(null);
        if (nuevo != null) {
            try {
                // Hashear la contraseña antes de guardar
                String sha1 = HashUtils.sha1(nuevo.getContrasena());
                nuevo.setContrasena(sha1);
                usuarioDAO.crear(nuevo);
                cargarListaUsuarios();
                mostrarInfo("Usuario creado", "El usuario se guardó exitosamente.");
            } catch (Exception e) {
                mostrarError("Error al crear usuario", e.getMessage());
            }
        }
    }

    /**
     * Abre un diálogo para editar el usuario seleccionado.
     * No permite cambiar contraseña aquí; solo nombre, usuario, rol y activo.
     */
    public void onEditarUsuario() {
        Usuario seleccionado = tableUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección incorrecta", "Debe seleccionar un usuario de la lista.");
            return;
        }

        Usuario editado = mostrarDialogoUsuario(seleccionado);
        if (editado != null) {
            try {
                // Mantener la contraseña actual en el objeto, no se cambia aquí
                editado.setContrasena(seleccionado.getContrasena());
                usuarioDAO.actualizar(editado);
                cargarListaUsuarios();
                mostrarInfo("Usuario actualizado", "El usuario se actualizó correctamente.");
            } catch (Exception e) {
                mostrarError("Error al actualizar usuario", e.getMessage());
            }
        }
    }

    /**
     * Solicita confirmación y elimina el usuario seleccionado.
     */
    public void onEliminarUsuario() {
        Usuario seleccionado = tableUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección incorrecta", "Debe seleccionar un usuario de la lista.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar usuario?");
        confirm.setContentText("¿Está seguro de eliminar el usuario: \"" +
                seleccionado.getUsuario() + "\"?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                usuarioDAO.eliminar(seleccionado);
                cargarListaUsuarios();
                mostrarInfo("Usuario eliminado", "El usuario se eliminó correctamente.");
            } catch (Exception e) {
                mostrarError("Error al eliminar usuario", e.getMessage());
            }
        }
    }

    /**
     * Abre un diálogo para cambiar la contraseña del usuario seleccionado.
     */
    public void onCambiarContrasena() {
        Usuario seleccionado = tableUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección incorrecta", "Debe seleccionar un usuario de la lista.");
            return;
        }

        // Crear diálogo para ingresar nueva contraseña
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Cambiar contraseña");
        dialog.setHeaderText("Usuario: " + seleccionado.getUsuario());

        ButtonType botonGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(botonGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField txtNueva = new PasswordField();
        txtNueva.setPromptText("Nueva contraseña");
        PasswordField txtConfirmar = new PasswordField();
        txtConfirmar.setPromptText("Confirmar contraseña");
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        grid.add(new Label("Nueva contraseña:"), 0, 0);
        grid.add(txtNueva, 1, 0);
        grid.add(new Label("Confirmar contraseña:"), 0, 1);
        grid.add(txtConfirmar, 1, 1);
        grid.add(lblError, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node btnGuardarNode = dialog.getDialogPane().lookupButton(botonGuardar);
        btnGuardarNode.setDisable(true);

        // Validar que ambas contraseñas coincidan y no estén vacías
        txtNueva.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean disable = newVal.trim().isEmpty() ||
                              txtConfirmar.getText().trim().isEmpty() ||
                              !newVal.equals(txtConfirmar.getText());
            btnGuardarNode.setDisable(disable);
            lblError.setText(!newVal.equals(txtConfirmar.getText()) ? "Las contraseñas no coinciden." : "");
        });
        txtConfirmar.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean disable = newVal.trim().isEmpty() ||
                              txtNueva.getText().trim().isEmpty() ||
                              !newVal.equals(txtNueva.getText());
            btnGuardarNode.setDisable(disable);
            lblError.setText(!newVal.equals(txtNueva.getText()) ? "Las contraseñas no coinciden." : "");
        });

        dialog.setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType dialogButton) {
                if (dialogButton == botonGuardar) {
                    return txtNueva.getText().trim();
                }
                return null;
            }
        });

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(nuevaPass -> {
            try {
                String sha1 = HashUtils.sha1(nuevaPass);
                usuarioDAO.cambiarContrasena(seleccionado.getIdUsuario(), sha1);
                mostrarInfo("Contraseña cambiada", "La contraseña se actualizó correctamente.");
            } catch (Exception e) {
                mostrarError("Error al cambiar contraseña", e.getMessage());
            }
        });
    }

    /**
     * Despliega un diálogo para crear o editar un Usuario.
     * Si 'usuarioBase' es null, se asume creación de nuevo.
     * Si no, carga valores preexistentes (excepto la contraseña).
     * Retorna el objeto Usuario con los datos capturados; si cancela, retorna null.
     */
    private Usuario mostrarDialogoUsuario(Usuario usuarioBase) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(usuarioBase == null ? "Nuevo Usuario" : "Editar Usuario");
        ButtonType botonGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(botonGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");
        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Usuario (login)");
        PasswordField txtContrasena = new PasswordField();
        txtContrasena.setPromptText("Contraseña");
        PasswordField txtConfirmar = new PasswordField();
        txtConfirmar.setPromptText("Confirmar contraseña");
        ComboBox<String> cbRol = new ComboBox<>();
        cbRol.getItems().addAll("admin", "gerente", "empleado");
        CheckBox chkActivo = new CheckBox("Activo");

        // Si es edición, precargar valores y ocultar campos de contraseña
        if (usuarioBase != null) {
            txtNombre.setText(usuarioBase.getNombre());
            txtUsuario.setText(usuarioBase.getUsuario());
            cbRol.setValue(usuarioBase.getRol());
            chkActivo.setSelected(usuarioBase.isActivo());
            txtContrasena.setDisable(true);
            txtConfirmar.setDisable(true);
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Usuario:"), 0, 1);
        grid.add(txtUsuario, 1, 1);

        if (usuarioBase == null) {
            grid.add(new Label("Contraseña:"), 0, 2);
            grid.add(txtContrasena, 1, 2);
            grid.add(new Label("Confirmar:"), 0, 3);
            grid.add(txtConfirmar, 1, 3);
            grid.add(new Label("Rol:"), 0, 4);
            grid.add(cbRol, 1, 4);
            grid.add(chkActivo, 1, 5);
        } else {
            grid.add(new Label("Rol:"), 0, 2);
            grid.add(cbRol, 1, 2);
            grid.add(chkActivo, 1, 3);
        }

        dialog.getDialogPane().setContent(grid);

        Node btnGuardarNode = dialog.getDialogPane().lookupButton(botonGuardar);
        btnGuardarNode.setDisable(true);

        // Validaciones
        if (usuarioBase == null) {
            // Nuevo usuario: validar nombre, usuario, contraseñas coincidentes, rol
            txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposNuevo(txtNombre, txtUsuario, txtContrasena, txtConfirmar, cbRol, btnGuardarNode);
            });
            txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposNuevo(txtNombre, txtUsuario, txtContrasena, txtConfirmar, cbRol, btnGuardarNode);
            });
            txtContrasena.textProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposNuevo(txtNombre, txtUsuario, txtContrasena, txtConfirmar, cbRol, btnGuardarNode);
            });
            txtConfirmar.textProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposNuevo(txtNombre, txtUsuario, txtContrasena, txtConfirmar, cbRol, btnGuardarNode);
            });
            cbRol.valueProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposNuevo(txtNombre, txtUsuario, txtContrasena, txtConfirmar, cbRol, btnGuardarNode);
            });
        } else {
            // Edición: validar nombre, usuario y rol
            txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposEdicion(txtNombre, txtUsuario, cbRol, btnGuardarNode);
            });
            txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposEdicion(txtNombre, txtUsuario, cbRol, btnGuardarNode);
            });
            cbRol.valueProperty().addListener((obs, oldVal, newVal) -> {
                validarCamposEdicion(txtNombre, txtUsuario, cbRol, btnGuardarNode);
            });
        }

        dialog.setResultConverter(new Callback<ButtonType, Usuario>() {
            @Override
            public Usuario call(ButtonType dialogButton) {
                if (dialogButton == botonGuardar) {
                    String nombre = txtNombre.getText().trim();
                    String usuarioLogin = txtUsuario.getText().trim();
                    String rol = cbRol.getValue();
                    boolean activo = chkActivo.isSelected();

                    if (usuarioBase == null) {
                        // Nuevo usuario: incluir contraseña temporal en texto plano
                        String pass = txtContrasena.getText().trim();
                        return new Usuario(0, nombre, usuarioLogin, pass, rol, activo);
                    } else {
                        // Edición: mantener ID y contraseña previa
                        Usuario u = new Usuario();
                        u.setIdUsuario(usuarioBase.getIdUsuario());
                        u.setNombre(nombre);
                        u.setUsuario(usuarioLogin);
                        u.setRol(rol);
                        u.setActivo(activo);
                        u.setContrasena(usuarioBase.getContrasena());
                        return u;
                    }
                }
                return null;
            }
        });

        Optional<Usuario> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }

    private void validarCamposNuevo(
            TextField txtNombre,
            TextField txtUsuario,
            PasswordField txtContrasena,
            PasswordField txtConfirmar,
            ComboBox<String> cbRol,
            Node btnGuardarNode) {
        boolean disable = txtNombre.getText().trim().isEmpty()
                       || txtUsuario.getText().trim().isEmpty()
                       || txtContrasena.getText().trim().isEmpty()
                       || txtConfirmar.getText().trim().isEmpty()
                       || !txtContrasena.getText().equals(txtConfirmar.getText())
                       || cbRol.getValue() == null;
        btnGuardarNode.setDisable(disable);
    }

    private void validarCamposEdicion(
            TextField txtNombre,
            TextField txtUsuario,
            ComboBox<String> cbRol,
            Node btnGuardarNode) {
        boolean disable = txtNombre.getText().trim().isEmpty()
                       || txtUsuario.getText().trim().isEmpty()
                       || cbRol.getValue() == null;
        btnGuardarNode.setDisable(disable);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

