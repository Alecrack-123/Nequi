package co.edu.uniquindio.poo.nequii;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class CuentaController {
    @FXML private TextField txtIdCuenta;
    @FXML private TextField txtBanco;
    @FXML private TextField txtNumeroCuenta;
    @FXML private ComboBox<TipoCuenta> cbTipoCuenta;
    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private TextField txtSaldo;
    @FXML private TableView<Cuenta> tablaCuentas;
    @FXML private TableColumn<Cuenta, String> columnaId;
    @FXML private TableColumn<Cuenta, String> columnaBanco;
    @FXML private TableColumn<Cuenta, String> columnaNumeroCuenta;
    @FXML private TableColumn<Cuenta, TipoCuenta> columnaTipoCuenta;
    @FXML private TableColumn<Cuenta, String> columnaUsuario;
    @FXML private TableColumn<Cuenta, Double> columnaSaldo;

    private ObservableList<Cuenta> listaCuentas;
    private ObservableList<Usuario> listaUsuarios;

    @FXML
    public void initialize() {
        // Inicializar listas
        listaCuentas = FXCollections.observableArrayList();
        listaUsuarios = FXCollections.observableArrayList();

        // Configurar ComboBoxes
        cbTipoCuenta.setItems(FXCollections.observableArrayList(TipoCuenta.values()));
        cbUsuario.setItems(listaUsuarios);

        // Configurar columnas de la tabla
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idCuenta"));
        columnaBanco.setCellValueFactory(new PropertyValueFactory<>("banco"));
        columnaNumeroCuenta.setCellValueFactory(new PropertyValueFactory<>("numeroCuenta"));
        columnaTipoCuenta.setCellValueFactory(new PropertyValueFactory<>("tipoCuenta"));
        columnaUsuario.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsuario().getNombre()));
        columnaSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));

        // Configurar la tabla
        tablaCuentas.setItems(listaCuentas);

        // Configurar listener para selección en la tabla
        tablaCuentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarCuentaEnFormulario(newSelection);
            }
        });
    }

    @FXML
    private void guardarCuenta() {
        try {
            if (!validarCamposRequeridos()) {
                mostrarAlerta("Error", "Todos los campos son requeridos", Alert.AlertType.ERROR);
                return;
            }

            String idCuenta = txtIdCuenta.getText();
            String banco = txtBanco.getText();
            String numeroCuenta = txtNumeroCuenta.getText();
            TipoCuenta tipoCuenta = cbTipoCuenta.getValue();
            Usuario usuario = cbUsuario.getValue();
            double saldo = Double.parseDouble(txtSaldo.getText());

            if (saldo < 0) {
                mostrarAlerta("Error", "El saldo debe ser un número positivo", Alert.AlertType.ERROR);
                return;
            }

            Cuenta cuentaExistente = listaCuentas.stream()
                    .filter(c -> c.getIdCuenta().equals(idCuenta))
                    .findFirst()
                    .orElse(null);

            if (cuentaExistente == null) {
                Cuenta nuevaCuenta = new Cuenta(idCuenta, banco, numeroCuenta, tipoCuenta, usuario, saldo);
                listaCuentas.add(nuevaCuenta);
                Cuenta.guardarCuenta(nuevaCuenta);
                mostrarAlerta("Éxito", "Cuenta guardada correctamente", Alert.AlertType.INFORMATION);
            } else {
                cuentaExistente.setBanco(banco);
                cuentaExistente.setNumeroCuenta(numeroCuenta);
                cuentaExistente.setTipoCuenta(tipoCuenta);
                cuentaExistente.setUsuario(usuario);
                cuentaExistente.setSaldo(saldo);
                mostrarAlerta("Éxito", "Cuenta actualizada correctamente", Alert.AlertType.INFORMATION);
            }

            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El saldo debe ser un número válido", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarCuenta() {
        Cuenta cuentaSeleccionada = tablaCuentas.getSelectionModel().getSelectedItem();
        if (cuentaSeleccionada != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText(null);
            alert.setContentText("¿Está seguro de eliminar esta cuenta?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                listaCuentas.remove(cuentaSeleccionada);
                Cuenta.eliminarCuenta(cuentaSeleccionada);
                limpiarCampos();
            }
        } else {
            mostrarAlerta("Error", "Debe seleccionar una cuenta para eliminar", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void limpiarCampos() {
        txtIdCuenta.clear();
        txtBanco.clear();
        txtNumeroCuenta.clear();
        cbTipoCuenta.setValue(null);
        cbUsuario.setValue(null);
        txtSaldo.clear();
        tablaCuentas.getSelectionModel().clearSelection();
    }

    private void mostrarCuentaEnFormulario(Cuenta cuenta) {
        txtIdCuenta.setText(cuenta.getIdCuenta());
        txtBanco.setText(cuenta.getBanco());
        txtNumeroCuenta.setText(cuenta.getNumeroCuenta());
        cbTipoCuenta.setValue(cuenta.getTipoCuenta());
        cbUsuario.setValue(cuenta.getUsuario());
        txtSaldo.setText(String.valueOf(cuenta.getSaldo()));
    }

    private boolean validarCamposRequeridos() {
        return !txtIdCuenta.getText().isEmpty() &&
                !txtBanco.getText().isEmpty() &&
                !txtNumeroCuenta.getText().isEmpty() &&
                cbTipoCuenta.getValue() != null &&
                cbUsuario.getValue() != null &&
                !txtSaldo.getText().isEmpty();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}