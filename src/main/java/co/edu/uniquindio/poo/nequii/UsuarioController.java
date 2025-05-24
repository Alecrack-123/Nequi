package co.edu.uniquindio.poo.nequii;


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
    private void transferirDinero() {
        try {
            // Validar que los campos no estén vacíos
            if (montoField.getText() == null || montoField.getText().trim().isEmpty()) {
                mostrarMensaje("Por favor ingrese un monto", Alert.AlertType.WARNING);
                return;
            }

            if (destinatarioField.getText() == null || destinatarioField.getText().trim().isEmpty()) {
                mostrarMensaje("Por favor ingrese un destinatario", Alert.AlertType.WARNING);
                return;
            }

            // Validar y convertir el monto
            double monto = Double.parseDouble(montoField.getText());

            // Validar monto mínimo y que no sea negativo
            if (monto <= 0) {
                mostrarMensaje("El monto debe ser mayor a cero", Alert.AlertType.WARNING);
                return;
            }

            // Validar que tenga saldo suficiente
            if (monto > usuario.consultarSaldo()) {
                mostrarMensaje("Saldo insuficiente para realizar la transferencia", Alert.AlertType.WARNING);
                return;
            }

            // Buscar el destinatario (esto dependerá de tu implementación del sistema de usuarios)
            String idDestinatario = destinatarioField.getText();
            Usuario destinatario = buscarDestinatario(idDestinatario);

            if (destinatario == null) {
                mostrarMensaje("Destinatario no encontrado", Alert.AlertType.ERROR);
                return;
            }

            // Validar que no sea una transferencia a sí mismo
            if (destinatario.equals(usuario)) {
                mostrarMensaje("No puedes transferir dinero a ti mismo", Alert.AlertType.WARNING);
                return;
            }

            // Mostrar diálogo de confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar transferencia");
            confirmacion.setHeaderText("¿Estás seguro de realizar esta transferencia?");
            confirmacion.setContentText(String.format("Monto: $%.2f\nDestinatario: %s", monto, idDestinatario));

            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                // Realizar la transferencia
                if (usuario.transferirDinero(destinatario, monto)) {
                    actualizarInterfaz();
                    mostrarMensaje("Transferencia realizada con éxito", Alert.AlertType.INFORMATION);
                    // Limpiar los campos después de una transferencia exitosa
                    montoField.clear();
                    destinatarioField.clear();
                } else {
                    mostrarMensaje("Error al realizar la transferencia", Alert.AlertType.ERROR);
                }
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Por favor ingrese un monto válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarMensaje("Error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Usuario buscarDestinatario(String idDestinatario) {
        return null;
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