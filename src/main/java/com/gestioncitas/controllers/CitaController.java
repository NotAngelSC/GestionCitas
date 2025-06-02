package com.gestioncitas.controllers;

import com.gestioncitas.dao.CitaDAO;
import com.gestioncitas.dao.ClienteDAO;
import com.gestioncitas.dao.ServicioDAO;
import com.gestioncitas.dao.SerieRecurrenteDAO;
import com.gestioncitas.models.Cita;
import com.gestioncitas.models.Cliente;
import com.gestioncitas.models.Servicio;
import com.gestioncitas.models.SerieRecurrente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class CitaController {

    @FXML private TableView<Cita> tableCitas;
    @FXML private TableColumn<Cita, String> colCliente;
    @FXML private TableColumn<Cita, String> colServicio;
    @FXML private TableColumn<Cita, LocalDate> colFecha;
    @FXML private TableColumn<Cita, LocalTime> colHoraInicio;
    @FXML private TableColumn<Cita, LocalTime> colHoraFin;
    @FXML private TableColumn<Cita, String> colNotas;

    private CitaDAO citaDAO           = new CitaDAO();
    private ClienteDAO clienteDAO     = new ClienteDAO();
    private ServicioDAO servicioDAO   = new ServicioDAO();
    private SerieRecurrenteDAO serieDAO = new SerieRecurrenteDAO();

    private ObservableList<Cita> listaCitas;

    @FXML
    private void initialize() {
        // Configurar las columnas para que muestren la propiedad adecuada de Cita
        colCliente.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreCliente())
        );
        colServicio.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreServicio())
        );
        colFecha.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFecha())
        );
        colHoraInicio.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getHoraInicio())
        );
        colHoraFin.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getHoraFin())
        );
        colNotas.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNotas())
        );

        cargarTabla();
    }

    /** Carga todas las citas en la TableView */
    private void cargarTabla() {
        try {
            List<Cita> citas = citaDAO.listarTodos();
            listaCitas = FXCollections.observableArrayList(citas);
            tableCitas.setItems(listaCitas);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar citas.").showAndWait();
        }
    }

    @FXML
    private void onNuevaCita() {
        Cita nueva = mostrarDialogoCita(null);
        if (nueva != null) {
            try {
                citaDAO.crear(nueva);
                cargarTabla();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al crear cita.").showAndWait();
            }
        }
    }

    @FXML
    private void onEditarCita() {
        Cita seleccionada = tableCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una cita para editar.").showAndWait();
            return;
        }
        Cita actualizada = mostrarDialogoCita(seleccionada);
        if (actualizada != null) {
            try {
                // Si actualizada.idSerie != null, editamos solo esa cita, no las demás de la serie
                citaDAO.actualizar(actualizada);
                cargarTabla();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al actualizar cita.").showAndWait();
            }
        }
    }

    @FXML
    private void onEliminarCita() {
        Cita seleccionada = tableCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una cita para eliminar.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                  "¿Estás seguro de eliminar esta cita?",
                                  ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                citaDAO.eliminar(seleccionada);
                cargarTabla();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error al eliminar cita.").showAndWait();
            }
        }
    }

    /**
     * Muestra un diálogo para crear o editar una Cita (con opción de recurrencia).
     * @param citaBase si es null → nueva cita; si no → editar.
     * @return Cita con datos (nuevo o modificado) o null si canceló.
     */
    private Cita mostrarDialogoCita(Cita citaBase) {
        Dialog<Cita> dialog = new Dialog<>();
        dialog.setTitle(citaBase == null ? "Nueva Cita" : "Editar Cita");
        dialog.setHeaderText(null);

        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        // ComboBox de clientes
        ComboBox<Cliente> cbClientes = new ComboBox<>();
        cbClientes.setPrefWidth(200);
        try {
            cbClientes.getItems().addAll(clienteDAO.listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cbClientes.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cbClientes.setButtonCell(cbClientes.getCellFactory().call(null));

        // ComboBox de servicios
        ComboBox<Servicio> cbServicios = new ComboBox<>();
        cbServicios.setPrefWidth(200);
        try {
            cbServicios.getItems().addAll(servicioDAO.listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cbServicios.setCellFactory(lv -> new ListCell<Servicio>() {
            @Override
            protected void updateItem(Servicio item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cbServicios.setButtonCell(cbServicios.getCellFactory().call(null));

        // DatePicker para la fecha
        DatePicker dpFecha = new DatePicker();

        // TextField para horaInicio (formato HH:mm)
        TextField txtHoraInicio = new TextField();
        txtHoraInicio.setPromptText("HH:mm");

        // TextField para notas
        TextField txtNotas = new TextField();
        txtNotas.setPromptText("Notas (opcional)");

        // CheckBox que indica si la cita es recurrente
        CheckBox chkRecurrente = new CheckBox("Es recurrente");

        // ComboBox para tipo de recurrencia
        ComboBox<String> cbTipoRecurrencia = new ComboBox<>();
        cbTipoRecurrencia.getItems().addAll("diaria", "semanal", "mensual");
        cbTipoRecurrencia.setPromptText("Tipo");
        cbTipoRecurrencia.setDisable(true);

        // DatePicker para fecha fin de la serie (opcional)
        DatePicker dpFechaFin = new DatePicker();
        dpFechaFin.setDisable(true);

        // TextField para repeticiones (opcional)
        TextField txtRepeticiones = new TextField();
        txtRepeticiones.setPromptText("N° repeticiones");
        txtRepeticiones.setDisable(true);

        // Al marcar/desmarcar chkRecurrente, habilitar/deshabilitar campos de serie
        chkRecurrente.selectedProperty().addListener((obs, wasSelected, isSel) -> {
            cbTipoRecurrencia.setDisable(!isSel);
            dpFechaFin.setDisable(!isSel);
            txtRepeticiones.setDisable(!isSel);
            if (!isSel) {
                cbTipoRecurrencia.getSelectionModel().clearSelection();
                dpFechaFin.setValue(null);
                txtRepeticiones.clear();
            }
        });

        // Si estamos editando, precargar valores
        if (citaBase != null) {
            // Seleccionar cliente
            for (Cliente c : cbClientes.getItems()) {
                if (c.getIdCliente() == citaBase.getIdCliente()) {
                    cbClientes.getSelectionModel().select(c);
                    break;
                }
            }
            // Seleccionar servicio
            for (Servicio s : cbServicios.getItems()) {
                if (s.getIdServicio() == citaBase.getIdServicio()) {
                    cbServicios.getSelectionModel().select(s);
                    break;
                }
            }
            dpFecha.setValue(citaBase.getFecha());
            txtHoraInicio.setText(citaBase.getHoraInicio().toString());
            txtNotas.setText(citaBase.getNotas());

            // Si citaBase.getIdSerie() != null, significa que pertenece a una serie
            if (citaBase.getIdSerie() != null) {
                chkRecurrente.setSelected(true);
                // Cargar datos de la serie
                try {
                    SerieRecurrente sr = serieDAO.buscarPorId(citaBase.getIdSerie());
                    if (sr != null) {
                        cbTipoRecurrencia.getSelectionModel().select(sr.getTipoRecurrencia());
                        dpFechaFin.setValue(sr.getFechaFin());
                        if (sr.getRepeticiones() != null) {
                            txtRepeticiones.setText(sr.getRepeticiones().toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Agregar al GridPane
        grid.add(new Label("Cliente:"), 0, 0);
        grid.add(cbClientes, 1, 0);
        grid.add(new Label("Servicio:"), 0, 1);
        grid.add(cbServicios, 1, 1);
        grid.add(new Label("Fecha:"), 0, 2);
        grid.add(dpFecha, 1, 2);
        grid.add(new Label("Hora Inicio:"), 0, 3);
        grid.add(txtHoraInicio, 1, 3);
        grid.add(new Label("Notas:"), 0, 4);
        grid.add(txtNotas, 1, 4);

        grid.add(chkRecurrente,           0, 5, 2, 1);
        grid.add(new Label("Tipo recurrencia:"), 0, 6);
        grid.add(cbTipoRecurrencia,       1, 6);
        grid.add(new Label("Fecha fin (opcional):"), 0, 7);
        grid.add(dpFechaFin,              1, 7);
        grid.add(new Label("Repeticiones (opcional):"), 0, 8);
        grid.add(txtRepeticiones,         1, 8);

        dialog.getDialogPane().setContent(grid);

        // Deshabilitar botón Aceptar si faltan datos obligatorios
        Node btnAceptarNode = dialog.getDialogPane().lookupButton(btnAceptar);
        btnAceptarNode.setDisable(true);

        // Listener conjunto para activar/desactivar botón Aceptar
        Runnable validarInputs = () -> {
            boolean baseOk = cbClientes.getValue() != null
                          && cbServicios.getValue() != null
                          && dpFecha.getValue() != null
                          && !txtHoraInicio.getText().trim().isEmpty();
            if (!chkRecurrente.isSelected()) {
                btnAceptarNode.setDisable(!baseOk);
            } else {
                boolean serieOk = cbTipoRecurrencia.getValue() != null
                               && (!dpFechaFin.isDisabled() ? true : true);
                // dpFechaFin y txtRepeticiones son opcionales: uno puede estar vacío
                btnAceptarNode.setDisable(!(baseOk && serieOk));
            }
        };

        cbClientes.valueProperty().addListener((o, a, b) -> validarInputs.run());
        cbServicios.valueProperty().addListener((o, a, b) -> validarInputs.run());
        dpFecha.valueProperty().addListener((o, a, b) -> validarInputs.run());
        txtHoraInicio.textProperty().addListener((o, a, b) -> validarInputs.run());
        cbTipoRecurrencia.valueProperty().addListener((o, a, b) -> validarInputs.run());
        chkRecurrente.selectedProperty().addListener((o, a, b) -> validarInputs.run());

        // Convertir resultado del diálogo a objeto Cita
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                Cliente selCliente = cbClientes.getValue();
                Servicio selServicio = cbServicios.getValue();
                LocalDate fechaVal = dpFecha.getValue();
                LocalTime horaInicioVal = LocalTime.parse(txtHoraInicio.getText().trim());
                LocalTime horaFinVal = horaInicioVal.plusMinutes(selServicio.getDuracionMin());
                String notasVal = txtNotas.getText().trim();

                if (citaBase == null) {
                    if (!chkRecurrente.isSelected()) {
                        return new Cita(
                            selCliente.getIdCliente(),
                            selServicio.getIdServicio(),
                            fechaVal,
                            horaInicioVal,
                            horaFinVal,
                            notasVal,
                            null
                        );
                    } else {
                        // Crear la serie en BD
                        String tipo = cbTipoRecurrencia.getValue();
                        LocalDate fin = dpFechaFin.getValue(); // puede ser null
                        Integer reps = null;
                        if (!txtRepeticiones.getText().trim().isEmpty()) {
                            reps = Integer.parseInt(txtRepeticiones.getText().trim());
                        }
                        SerieRecurrente sr = new SerieRecurrente(tipo, fechaVal, fin, reps);
                        try {
                            serieDAO.crear(sr);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                        int idSerieGen = sr.getIdSerie();

                        // Generar todas las citas de la serie
                        LocalDate fechaActual = fechaVal;
                        int contador = 0;
                        while (true) {
                            if (fin != null && fechaActual.isAfter(fin)) break;
                            if (reps != null && contador >= reps) break;

                            Cita c = new Cita(
                                selCliente.getIdCliente(),
                                selServicio.getIdServicio(),
                                fechaActual,
                                horaInicioVal,
                                horaFinVal,
                                notasVal,
                                idSerieGen
                            );
                            try {
                                citaDAO.crear(c);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            switch (tipo) {
                                case "diaria":
                                    fechaActual = fechaActual.plusDays(1);
                                    break;
                                case "semanal":
                                    fechaActual = fechaActual.plusWeeks(1);
                                    break;
                                case "mensual":
                                    fechaActual = fechaActual.plusMonths(1);
                                    break;
                            }
                            contador++;
                        }
                        // Retornamos null porque ya creamos todas en BD, y la tabla se recargará
                        return null;
                    }
                } else {
                    // Editar cita existente (ignorar recurrencia; solo modifica campos básicos)
                    citaBase.setIdCliente(selCliente.getIdCliente());
                    citaBase.setIdServicio(selServicio.getIdServicio());
                    citaBase.setFecha(fechaVal);
                    citaBase.setHoraInicio(horaInicioVal);
                    citaBase.setHoraFin(horaFinVal);
                    citaBase.setNotas(notasVal);
                    // Mantenemos citaBase.idSerie y citaBase.version
                    return citaBase;
                }
            }
            return null;
        });

        Optional<Cita> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }
}
