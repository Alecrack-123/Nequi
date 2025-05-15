package co.edu.uniquindio.poo.nequii;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransaccionController {
    @FXML private TableView<Transaccion> tablaTransacciones;
    @FXML private TableColumn<Transaccion, String> columnaId;
    @FXML private TableColumn<Transaccion, LocalDate> columnaFecha;
    @FXML private TableColumn<Transaccion, String> columnaTipo;
    @FXML private TableColumn<Transaccion, Double> columnaMonto;
    @FXML private TableColumn<Transaccion, String> columnaDescripcion;
    @FXML private TableColumn<Transaccion, String> columnaCuentaOrigen;
    @FXML private TableColumn<Transaccion, String> columnaCuentaDestino;
    @FXML private TableColumn<Transaccion, String> columnaCategoria;

    @FXML private TextField campoId;
    @FXML private DatePicker campoFecha;
    @FXML private ComboBox<Transaccion.TipoTransaccion> comboTipo;
    @FXML private TextField campoMonto;
    @FXML private TextField campoDescripcion;
    @FXML private ComboBox<Cuenta> comboCuentaOrigen;
    @FXML private ComboBox<Cuenta> comboCuentaDestino;
    @FXML private ComboBox<Categoria> comboCategoria;

    private ObservableList<Transaccion> listaTransacciones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFormatoColumnas();
        configurarTabla();
        configurarCombos();
        configurarValidaciones();
        cargarTransacciones();
    }

    private void configurarColumnas() {
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idTransaccion"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnaTipo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTipo().getDescripcion()));
        columnaMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaCuentaOrigen.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCuentaOrigen() != null ?
                        cellData.getValue().getCuentaOrigen().getIdCuenta() : "N/A"));
        columnaCuentaDestino.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCuentaDestino() != null ?
                        cellData.getValue().getCuentaDestino().getIdCuenta() : "N/A"));
        columnaCategoria.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategoria().getNombre()));
    }

    private void configurarFormatoColumnas() {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        columnaFecha.setCellFactory(column -> new TableCell<Transaccion, LocalDate>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(formatoFecha.format(fecha));
                }
            }
        });

        columnaMonto.setCellFactory(column -> new TableCell<Transaccion, Double>() {
            @Override
            protected void updateItem(Double monto, boolean empty) {
                super.updateItem(monto, empty);
                if (empty || monto == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", monto));
                }
            }
        });
    }

    @FXML
    private void agregarTransaccion() {
        try {
            validarCampos();

            Transaccion nuevaTransaccion = new Transaccion(
                    campoId.getText().trim(),
                    campoFecha.getValue(),
                    comboTipo.getValue(),
                    Double.parseDouble(campoMonto.getText().trim()),
                    campoDescripcion.getText().trim(),
                    comboCuentaOrigen.getValue(),
                    comboCuentaDestino.getValue(),
                    comboCategoria.getValue()
            );

            nuevaTransaccion.ejecutarTransaccion();
            listaTransacciones.add(nuevaTransaccion);
            limpiarCampos();
            mostrarAlerta("Éxito", "Transacción realizada correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta("Error", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error", "Error inesperado: " + e.getMessage());
        }
    }

    private void configurarCombos() {
        // Configurar ComboBox de tipo de transacción
        comboTipo.setItems(FXCollections.observableArrayList(Transaccion.TipoTransaccion.values()));
        comboTipo.setConverter(new StringConverter<Transaccion.TipoTransaccion>() {
            @Override
            public String toString(Transaccion.TipoTransaccion tipo) {
                return tipo != null ? tipo.getDescripcion() : "";
            }

            @Override
            public Transaccion.TipoTransaccion fromString(String string) {
                return null; // No necesario para este caso
            }
        });

        // Configurar ComboBox de cuentas
        comboCuentaOrigen.setItems(FXCollections.observableArrayList(Cuenta.getCuentas()));
        comboCuentaDestino.setItems(FXCollections.observableArrayList(Cuenta.getCuentas()));

        // Configurar ComboBox de categorías
        comboCategoria.setItems(FXCollections.observableArrayList(Categoria.getCategorias()));

        // Listener para el tipo de transacción
        comboTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switch (newVal) {
                    case DEPOSITO:
                        comboCuentaOrigen.setDisable(true);
                        comboCuentaDestino.setDisable(false);
                        break;
                    case RETIRO:
                        comboCuentaOrigen.setDisable(false);
                        comboCuentaDestino.setDisable(true);
                        break;
                    case TRANSFERENCIA:
                        comboCuentaOrigen.setDisable(false);
                        comboCuentaDestino.setDisable(false);
                        break;
                }
            }
        });
    }

    private void configurarValidaciones() {
        // Solo números y punto decimal en el campo de monto
        campoMonto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                campoMonto.setText(oldValue);
            }
        });

        // No permitir fechas futuras
        campoFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }

    private void configurarTabla() {
        tablaTransacciones.setItems(listaTransacciones);

        tablaTransacciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mostrarDatosTransaccion(newSelection);
                    }
                });
    }

    private void mostrarDatosTransaccion(Transaccion transaccion) {
        campoId.setText(transaccion.getIdTransaccion());
        campoFecha.setValue(transaccion.getFecha());
        comboTipo.setValue(transaccion.getTipo());
        campoMonto.setText(String.valueOf(transaccion.getMonto()));
        campoDescripcion.setText(transaccion.getDescripcion());
        comboCuentaOrigen.setValue(transaccion.getCuentaOrigen());
        comboCuentaDestino.setValue(transaccion.getCuentaDestino());
        comboCategoria.setValue(transaccion.getCategoria());
    }

    private void validarCampos() {
        if (camposVacios()) {
            throw new IllegalArgumentException("Todos los campos requeridos deben estar completos");
        }

        try {
            double monto = Double.parseDouble(campoMonto.getText().trim());
            if (monto <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor que cero");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El monto debe ser un número válido");
        }

        Transaccion.TipoTransaccion tipo = comboTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de transacción");
        }

        switch (tipo) {
            case DEPOSITO:
                if (comboCuentaDestino.getValue() == null) {
                    throw new IllegalArgumentException("Debe seleccionar una cuenta destino");
                }
                break;
            case RETIRO:
                if (comboCuentaOrigen.getValue() == null) {
                    throw new IllegalArgumentException("Debe seleccionar una cuenta origen");
                }
                break;
            case TRANSFERENCIA:
                if (comboCuentaOrigen.getValue() == null || comboCuentaDestino.getValue() == null) {
                    throw new IllegalArgumentException("Debe seleccionar ambas cuentas");
                }
                if (comboCuentaOrigen.getValue() == comboCuentaDestino.getValue()) {
                    throw new IllegalArgumentException("Las cuentas origen y destino no pueden ser la misma");
                }
                break;
        }
    }

    private boolean camposVacios() {
        return campoId.getText().trim().isEmpty() ||
                campoFecha.getValue() == null ||
                comboTipo.getValue() == null ||
                campoMonto.getText().trim().isEmpty() ||
                campoDescripcion.getText().trim().isEmpty() ||
                comboCategoria.getValue() == null;
    }

    @FXML
    private void limpiarCampos() {
        campoId.clear();
        campoFecha.setValue(null);
        comboTipo.setValue(null);
        campoMonto.clear();
        campoDescripcion.clear();
        comboCuentaOrigen.setValue(null);
        comboCuentaDestino.setValue(null);
        comboCategoria.setValue(null);
        tablaTransacciones.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarTransacciones() {
        listaTransacciones.setAll(Transaccion.getTransacciones());
    }

    public void actualizarTabla() {
        listaTransacciones.clear();
        listaTransacciones.addAll(Transaccion.getTransacciones());
        tablaTransacciones.refresh();
    }
}
