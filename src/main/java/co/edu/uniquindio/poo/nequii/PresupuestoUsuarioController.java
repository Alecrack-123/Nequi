package co.edu.uniquindio.poo.nequii;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PresupuestoUsuarioController {
    @FXML private TableView<Presupuesto> tablaPresupuestos;
    @FXML private TableColumn<Presupuesto, String> columnaNombrePresupuesto;
    @FXML private TableColumn<Presupuesto, Double> columnaMontoPresupuesto;
    @FXML private TableColumn<Presupuesto, String> columnaEstadoPresupuesto;
    @FXML private TextField nombrePresupuestoField;
    @FXML private TextField montoPresupuestoField;
    @FXML private Button crearPresupuestoButton;
    @FXML private Button modificarPresupuestoButton;
    @FXML private Button eliminarPresupuestoButton;
    @FXML private Label mensajeLabel;

    private Usuario usuario;
    private ObservableList<Presupuesto> presupuestosObservable = FXCollections.observableArrayList();

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        presupuestosObservable.setAll(usuario.getPresupuestos());
        tablaPresupuestos.setItems(presupuestosObservable);
    }

    @FXML
    private void initialize() {
        columnaNombrePresupuesto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        columnaMontoPresupuesto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMontoTotal()).asObject());
        columnaEstadoPresupuesto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado() != null ? cellData.getValue().getEstado().toString() : ""));

        crearPresupuestoButton.setOnAction(e -> handleCrearPresupuesto());
        modificarPresupuestoButton.setOnAction(e -> handleModificarPresupuesto());
        eliminarPresupuestoButton.setOnAction(e -> handleEliminarPresupuesto());
        tablaPresupuestos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> mostrarPresupuestoEnCampos(newSel));
    }

    private void handleCrearPresupuesto() {
        String nombre = nombrePresupuestoField.getText();
        String montoStr = montoPresupuestoField.getText();
        if (nombre.isEmpty() || montoStr.isEmpty()) {
            mensajeLabel.setText("Ingrese nombre y monto");
            return;
        }
        try {
            double monto = Double.parseDouble(montoStr);
            Presupuesto presupuesto = Presupuesto.crearPresupuesto(
                    "PRE-" + System.currentTimeMillis(),
                    nombre,
                    "",
                    monto,
                    java.time.LocalDate.now(),
                    java.time.LocalDate.now().plusMonths(1)
            );
            usuario.crearPresupuesto(presupuesto);
            presupuestosObservable.setAll(usuario.getPresupuestos());
            mensajeLabel.setText("Presupuesto creado");
            limpiarCamposPresupuesto();
        } catch (NumberFormatException e) {
            mensajeLabel.setText("Monto inválido");
        }
    }
    //Metodo para modificar un presupuesto
    private void handleModificarPresupuesto() {
        Presupuesto seleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();
        String montoStr = montoPresupuestoField.getText();
        if (seleccionado == null) {
            mensajeLabel.setText("Seleccione un presupuesto");
            return;
        }
        if (montoStr.isEmpty()) {
            mensajeLabel.setText("Ingrese el nuevo monto");
            return;
        }
        try {
            double nuevoMonto = Double.parseDouble(montoStr);
            usuario.modificarPresupuesto(seleccionado.getIdPresupuesto(), nuevoMonto);
            presupuestosObservable.setAll(usuario.getPresupuestos());
            mensajeLabel.setText("Presupuesto modificado");
            limpiarCamposPresupuesto();
        } catch (NumberFormatException e) {
            mensajeLabel.setText("Monto inválido");
        }
    }
    //Metodo para eliminar un presupuesto
    private void handleEliminarPresupuesto() {
        Presupuesto seleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("Seleccione un presupuesto");
            return;
        }
        usuario.eliminarPresupuesto(seleccionado.getIdPresupuesto());
        presupuestosObservable.setAll(usuario.getPresupuestos());
        mensajeLabel.setText("Presupuesto eliminado");
        limpiarCamposPresupuesto();
    }
    // Método para mostrar los datos del presupuesto seleccionado en los campos de texto
    private void mostrarPresupuestoEnCampos(Presupuesto presupuesto) {
        if (presupuesto != null) {
            nombrePresupuestoField.setText(presupuesto.getNombre());
            montoPresupuestoField.setText(String.valueOf(presupuesto.getMontoTotal()));
        }
    }

    private void limpiarCamposPresupuesto() {
        nombrePresupuestoField.clear();
        montoPresupuestoField.clear();
    }
}