package com.gestioncitas.controllers;

import com.gestioncitas.dao.ServicioDAO;
import com.gestioncitas.models.Servicio;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;                    // <- Import requerido para Node
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.Optional;

public class ServicioController {

    // DAO para manejar operaciones sobre la tabla servicios
    private final ServicioDAO servicioDAO = new ServicioDAO();

    // Lista observable que alimenta la TableView
    private final ObservableList<Servicio> listaServicios = FXCollections.observableArrayList();

    // Controles definidos en el FXML (identificadores deben coincidir)
    public TableView<Servicio> tableServicios;
    public TableColumn<Servicio, Integer> colIdServicio;
    public TableColumn<Servicio, String> colNombre;
    public TableColumn<Servicio, Integer> colDuracion;
    public TableColumn<Servicio, Double> colPrecio;
    public TableColumn<Servicio, String> colDescripcion;

    public Button btnNuevoServicio;
    public Button btnEditarServicio;
    public Button btnEliminarServicio;

    /**
     * Método llamado automáticamente tras cargar el FXML.
     * Configura las columnas y carga inicialmente la lista de servicios.
     */
    public void initialize() {
        // Mapear columnas a propiedades de la clase Servicio
        colIdServicio.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getIdServicio()).asObject()
        );
        colNombre.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getNombre())
        );
        colDuracion.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getDuracionMin()).asObject()
        );
        colPrecio.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject()
        );
        colDescripcion.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDescripcion())
        );

        // Asignar la lista observable a la tabla
        tableServicios.setItems(listaServicios);

        // Cargar datos desde la base de datos
        cargarListaServicios();

        // Deshabilitar edición/eliminación si no hay selección
        btnEditarServicio.disableProperty().bind(
            tableServicios.getSelectionModel().selectedItemProperty().isNull()
        );
        btnEliminarServicio.disableProperty().bind(
            tableServicios.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    /**
     * Recupera todos los servicios de la base de datos y los muestra en la tabla.
     */
    private void cargarListaServicios() {
        listaServicios.clear();
        try {
            listaServicios.addAll(servicioDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar servicios", e.getMessage());
        }
    }

    /**
     * Abre un diálogo para crear un nuevo servicio.
     * Si el usuario confirma y los datos son válidos, llama a servicioDAO.crear(...)
     * y recarga la tabla.
     */
    public void onNuevoServicio() {
        Servicio nuevo = mostrarDialogoServicio(null);
        if (nuevo != null) {
            try {
                servicioDAO.crear(nuevo);
                cargarListaServicios();
                mostrarInfo("Servicio creado", "El servicio se guardó exitosamente.");
            } catch (Exception e) {
                mostrarError("Error al crear servicio", e.getMessage());
            }
        }
    }

    /**
     * Abre un diálogo para editar el servicio seleccionado.
     * Si el usuario confirma, llama a servicioDAO.actualizar(...) y recarga la tabla.
     */
    public void onEditarServicio() {
        Servicio seleccionado = tableServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección incorrecta", "Debe seleccionar un servicio de la lista.");
            return;
        }

        Servicio editado = mostrarDialogoServicio(seleccionado);
        if (editado != null) {
            try {
                servicioDAO.actualizar(editado);
                cargarListaServicios();
                mostrarInfo("Servicio actualizado", "El servicio se actualizó correctamente.");
            } catch (Exception e) {
                mostrarError("Error al actualizar servicio", e.getMessage());
            }
        }
    }

    /**
     * Solicita confirmación al usuario y, si confirma, elimina el servicio seleccionado.
     */
    public void onEliminarServicio() {
        Servicio seleccionado = tableServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selección incorrecta", "Debe seleccionar un servicio de la lista.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar servicio?");
        confirm.setContentText("¿Está seguro de eliminar el servicio: \"" +
                seleccionado.getNombre() + "\"?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                servicioDAO.eliminar(seleccionado);
                cargarListaServicios();
                mostrarInfo("Servicio eliminado", "El servicio se eliminó correctamente.");
            } catch (Exception e) {
                mostrarError("Error al eliminar servicio", e.getMessage());
            }
        }
    }

    /**
     * Muestra un Alert de tipo ERROR con título y mensaje.
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un Alert de tipo INFORMATION con título y mensaje.
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Despliega un diálogo (Dialog) para crear o editar un Servicio.
     * Si 'servicioBase' es null, crea un diálogo en blanco para nueva entidad.
     * Si no, precarga campos con los datos de 'servicioBase'. Al confirmar,
     * retorna un objeto Servicio con los valores ingresados; si cancela, retorna null.
     */
    private Servicio mostrarDialogoServicio(Servicio servicioBase) {
        // Crear el diálogo
        Dialog<Servicio> dialog = new Dialog<>();
        dialog.setTitle(servicioBase == null ? "Nuevo Servicio" : "Editar Servicio");

        // Botones OK y Cancelar
        ButtonType botonGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(botonGuardar, ButtonType.CANCEL);

        // Panel de entrada: GridPane con etiquetas y campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del servicio");
        TextField txtDuracion = new TextField();
        txtDuracion.setPromptText("Duración (minutos)");
        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Precio");
        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripción");

        // Si es edición, precargar valores
        if (servicioBase != null) {
            txtNombre.setText(servicioBase.getNombre());
            txtDuracion.setText(String.valueOf(servicioBase.getDuracionMin()));
            txtPrecio.setText(String.valueOf(servicioBase.getPrecio()));
            txtDescripcion.setText(servicioBase.getDescripcion());
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Duración (min):"), 0, 1);
        grid.add(txtDuracion, 1, 1);
        grid.add(new Label("Precio:"), 0, 2);
        grid.add(txtPrecio, 1, 2);
        grid.add(new Label("Descripción:"), 0, 3);
        grid.add(txtDescripcion, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Habilitar/Deshabilitar botón Guardar según validación básica
        Node btnGuardarNode = dialog.getDialogPane().lookupButton(botonGuardar);
        btnGuardarNode.setDisable(true);

        // Validar en tiempo real: todos los campos requeridos
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            btnGuardarNode.setDisable(
                newVal.trim().isEmpty() ||
                txtDuracion.getText().trim().isEmpty() ||
                txtPrecio.getText().trim().isEmpty()
            );
        });
        txtDuracion.textProperty().addListener((obs, oldVal, newVal) -> {
            btnGuardarNode.setDisable(
                txtNombre.getText().trim().isEmpty() ||
                newVal.trim().isEmpty() ||
                txtPrecio.getText().trim().isEmpty()
            );
        });
        txtPrecio.textProperty().addListener((obs, oldVal, newVal) -> {
            btnGuardarNode.setDisable(
                txtNombre.getText().trim().isEmpty() ||
                txtDuracion.getText().trim().isEmpty() ||
                newVal.trim().isEmpty()
            );
        });

        // Convertir resultado al presionar Guardar
        dialog.setResultConverter(new Callback<ButtonType, Servicio>() {
            @Override
            public Servicio call(ButtonType dialogButton) {
                if (dialogButton == botonGuardar) {
                    try {
                        String nombre = txtNombre.getText().trim();
                        int duracion = Integer.parseInt(txtDuracion.getText().trim());
                        double precio = Double.parseDouble(txtPrecio.getText().trim());
                        String descripcion = txtDescripcion.getText().trim();

                        if (servicioBase == null) {
                            // Nueva entidad
                            return new Servicio(0, nombre, duracion, precio, descripcion);
                        } else {
                            // Reutilizar ID en edición
                            Servicio s = new Servicio();
                            s.setIdServicio(servicioBase.getIdServicio());
                            s.setNombre(nombre);
                            s.setDuracionMin(duracion);
                            s.setPrecio(precio);
                            s.setDescripcion(descripcion);
                            return s;
                        }
                    } catch (NumberFormatException e) {
                        // Si hay error en conversión de números, retornar null
                        return null;
                    }
                }
                return null;
            }
        });

        Optional<Servicio> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }
}
