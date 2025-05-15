package co.edu.uniquindio.poo.nequii;

import co.edu.uniquindio.poo.nequii.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;

public class UsuarioController {

    private Usuario usuario;

    @FXML private Label saldoLabel;
    @FXML private TextField montoField;
    @FXML private TextField destinatarioField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private TextField telefonoField;
    @FXML private TextField categoriaField;
    @FXML private TextField numeroCuentaField;
    @FXML private TextField bancoField; // nuevo campo para banco
    @FXML private ComboBox<TipoCuenta> tipoCuentaComboBox; // selector para tipo cuenta
    @FXML private PasswordField passwordField;
    @FXML private ListView<Transaccion> listaTransacciones;
    @FXML private ListView<Presupuesto> listaPresupuestos;
    @FXML private ListView<Cuenta> listaCuentas;

    @FXML
    public void initialize() {
        // Inicializar el combo box con los valores del enum TipoCuenta
        tipoCuentaComboBox.setItems(FXCollections.observableArrayList(TipoCuenta.values()));
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarInterfaz();
    }

    // RF-001: Registrarse y loguearse
    @FXML
    private void autenticar() {
        String correo = correoField.getText();
        String contrasena = passwordField.getText();

        if (usuario.autenticar(correo, contrasena)) {
            mostrarMensaje("Autenticación exitosa", Alert.AlertType.INFORMATION);
            // Aquí se podría mostrar la vista principal
        } else {
            mostrarMensaje("Credenciales incorrectas", Alert.AlertType.ERROR);
        }
    }

    // RF-002: Modificar perfil
    @FXML
    private void actualizarPerfil() {
        try {
            usuario.actualizarPerfil(
                nombreField.getText(),
                correoField.getText(),
                telefonoField.getText()
            );
            mostrarMensaje("Perfil actualizado exitosamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarMensaje("Error al actualizar perfil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // RF-003: Gestión de dinero
    @FXML
    private void agregarDinero() {
        try {
            double monto = Double.parseDouble(montoField.getText());
            usuario.agregarDinero(monto);
            actualizarInterfaz();
            mostrarMensaje("Dinero agregado exitosamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor ingrese un monto válido", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void retirarDinero() {
        try {
            double monto = Double.parseDouble(montoField.getText());
            if (usuario.retirarDinero(monto)) {
                actualizarInterfaz();
                mostrarMensaje("Retiro exitoso", Alert.AlertType.INFORMATION);
            } else {
                mostrarMensaje("Saldo insuficiente", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor ingrese un monto válido", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void transferirDinero(Usuario destinatario) {
        try {
            double monto = Double.parseDouble(montoField.getText());
            if (usuario.transferirDinero(destinatario, monto)) {
                actualizarInterfaz();
                mostrarMensaje("Transferencia exitosa", Alert.AlertType.INFORMATION);
            } else {
                mostrarMensaje("Saldo insuficiente para la transferencia", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor ingrese un monto válido", Alert.AlertType.ERROR);
        }
    }

    // RF-004: Gestión de presupuestos
    @FXML
    private void crearPresupuesto() {
        try {
            double monto = Double.parseDouble(montoField.getText());
            String idPresupuesto = "PRES-" + System.currentTimeMillis();
            Presupuesto presupuesto = new Presupuesto(idPresupuesto, monto);
            presupuesto.setNombre("Nuevo Presupuesto");
            usuario.crearPresupuesto(presupuesto);
            actualizarInterfaz();
            mostrarMensaje("Presupuesto creado exitosamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor ingrese un monto válido", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarPresupuesto() {
        try {
            Presupuesto presupuestoSeleccionado = listaPresupuestos.getSelectionModel().getSelectedItem();
            if (presupuestoSeleccionado != null) {
                double nuevoMonto = Double.parseDouble(montoField.getText());
                usuario.modificarPresupuesto(presupuestoSeleccionado.getIdPresupuesto(), nuevoMonto);
                actualizarInterfaz();
                mostrarMensaje("Presupuesto modificado exitosamente", Alert.AlertType.INFORMATION);
            } else {
                mostrarMensaje("Por favor seleccione un presupuesto", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor ingrese un monto válido", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarPresupuesto() {
        Presupuesto presupuestoSeleccionado = listaPresupuestos.getSelectionModel().getSelectedItem();
        if (presupuestoSeleccionado != null) {
            usuario.eliminarPresupuesto(presupuestoSeleccionado.getIdPresupuesto());
            actualizarInterfaz();
            mostrarMensaje("Presupuesto eliminado exitosamente", Alert.AlertType.INFORMATION);
        } else {
            mostrarMensaje("Por favor seleccione un presupuesto", Alert.AlertType.WARNING);
        }
    }

    // RF-005: Gestión de transacciones
    @FXML
    private void categorizarTransaccion() {
        Transaccion transaccionSeleccionada = listaTransacciones.getSelectionModel().getSelectedItem();
        if (transaccionSeleccionada != null && !categoriaField.getText().isEmpty()) {
            usuario.categorizarTransaccion(transaccionSeleccionada.getIdTransaccion(), 
                categoriaField.getText());
            actualizarInterfaz();
            mostrarMensaje("Transacción categorizada exitosamente", Alert.AlertType.INFORMATION);
        } else {
            mostrarMensaje("Seleccione una transacción e ingrese una categoría", Alert.AlertType.WARNING);
        }
    }

    // RF-006: Gestión de cuentas bancarias
    @FXML
    private void agregarCuentaBancaria() {
        try {
            TipoCuenta tipoSeleccionado = tipoCuentaComboBox.getValue();
            if (tipoSeleccionado == null) {
                mostrarMensaje("Por favor seleccione un tipo de cuenta", Alert.AlertType.WARNING);
                return;
            }

            String idCuenta = "ID-" + System.currentTimeMillis();
            String banco = bancoField.getText(); // ahora se lee desde el campo banco
            String numeroCuenta = numeroCuentaField.getText();
            double saldoInicial = 0.0;

            if (banco == null || banco.trim().isEmpty()) {
                mostrarMensaje("Por favor ingrese el nombre del banco", Alert.AlertType.WARNING);
                return;
            }

            if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
                mostrarMensaje("Por favor ingrese el número de cuenta", Alert.AlertType.WARNING);
                return;
            }

            Cuenta cuenta = new Cuenta(idCuenta, banco, numeroCuenta, tipoSeleccionado, usuario, saldoInicial);

            usuario.agregarCuentaBancaria(cuenta);
            actualizarInterfaz();
            mostrarMensaje("Cuenta bancaria agregada exitosamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarMensaje("Error al agregar cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarCuentaBancaria() {
        Cuenta cuentaSeleccionada = listaCuentas.getSelectionModel().getSelectedItem();
        if (cuentaSeleccionada != null) {
            usuario.eliminarCuentaBancaria(cuentaSeleccionada.getNumeroCuenta());
            actualizarInterfaz();
            mostrarMensaje("Cuenta bancaria eliminada exitosamente", Alert.AlertType.INFORMATION);
        } else {
            mostrarMensaje("Por favor seleccione una cuenta bancaria", Alert.AlertType.WARNING);
        }
    }

    private void actualizarInterfaz() {
        saldoLabel.setText(String.format("Saldo: $%.2f", usuario.consultarSaldo()));
        listaTransacciones.setItems(FXCollections.observableArrayList(usuario.consultarTransacciones()));
        listaPresupuestos.setItems(FXCollections.observableArrayList(usuario.consultarPresupuestos()));
        listaCuentas.setItems(FXCollections.observableArrayList(usuario.getCuentasBancarias()));
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Nequii");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}