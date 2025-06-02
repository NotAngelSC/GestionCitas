package com.gestioncitas.controllers;

import com.gestioncitas.dao.ClienteDAO;
import com.gestioncitas.models.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClienteController {

    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, LocalDate> colFechaRegistro;

    private ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<Cliente> listaClientes;

    @FXML
    private void initialize() {
        // Configurar cada columna para que muestre la propiedad adecuada del modelo
        colId.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdCliente()).asObject()
        );
        colNombre.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre())
        );
        colTelefono.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono())
        );
        colCorreo.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCorreo())
        );
        colFechaRegistro.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFechaRegistro())
        );

        cargarTabla();
    }

    /** Carga todos los clientes en la TableView */
    private void cargarTabla() {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            listaClientes = FXCollections.observableArrayList(clientes);
            tableClientes.setItems(listaClientes);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar clientes.").showAndWait();
        }
    }

    @FXML
    private void onNuevoCliente() {
        Cliente nuevo = mostrarDialogoCliente(null);
        if (nuevo != null) {
            try {
                clienteDAO.crear(nuevo);
                cargarTabla();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al crear cliente.").showAndWait();
            }
        }
    }

    @FXML
    private void onEditarCliente() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un cliente para editar.").showAndWait();
            return;
        }
        Cliente actualizado = mostrarDialogoCliente(seleccionado);
        if (actualizado != null) {
            try {
                clienteDAO.actualizar(actualizado);
                cargarTabla();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al actualizar cliente.").showAndWait();
            }
        }
    }

    @FXML
    private void onEliminarCliente() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un cliente para eliminar.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                  "¿Estás seguro de eliminar este cliente?",
                                  ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                clienteDAO.eliminar(seleccionado);
                cargarTabla();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al eliminar cliente.").showAndWait();
            }
        }
    }

    /**
     * Muestra un diálogo para crear o editar un Cliente.
     * @param cliente si es null → nuevo; si no null → editar.
     * @return Cliente con datos (nuevo o modificado) o null si el usuario canceló.
     */
    private Cliente mostrarDialogoCliente(Cliente cliente) {
        // Crear el diálogo
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
        dialog.setHeaderText(null);

        // Botones: Aceptar y Cancelar
        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);

        // Grid con campos de texto
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");

        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Teléfono");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo");

        if (cliente != null) {
            txtNombre.setText(cliente.getNombre());
            txtTelefono.setText(cliente.getTelefono());
            txtCorreo.setText(cliente.getCorreo());
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Teléfono:"), 0, 1);
        grid.add(txtTelefono, 1, 1);
        grid.add(new Label("Correo:"), 0, 2);
        grid.add(txtCorreo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Deshabilitar botón Aceptar si el campo Nombre está vacío
        Node btnAceptarNode = dialog.getDialogPane().lookupButton(btnAceptar);
        btnAceptarNode.setDisable(true);
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            btnAceptarNode.setDisable(newVal.trim().isEmpty());
        });

        // Convertir resultado al objeto Cliente
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                String nombreVal    = txtNombre.getText().trim();
                String telefonoVal  = txtTelefono.getText().trim();
                String correoVal    = txtCorreo.getText().trim();
                if (cliente == null) {
                    return new Cliente(nombreVal, telefonoVal, correoVal);
                } else {
                    cliente.setNombre(nombreVal);
                    cliente.setTelefono(telefonoVal);
                    cliente.setCorreo(correoVal);
                    return cliente;
                }
            }
            return null;
        });

        Optional<Cliente> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }
}
