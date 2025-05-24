package co.edu.uniquindio.poo.nequii;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UsuarioController {
    @FXML private Label saldoLabel;
    @FXML private TextField montoField;
    @FXML private TextField destinatarioField;
    @FXML private ListView<String> historialListView;
    @FXML private Button retirarDineroButton;
    @FXML private Button transferirButton;
    @FXML private Button cerrarSesionButton;
    @FXML private Label mensajeLabel;

    private Usuario usuario;
    private ObservableList<String> historial;

    @FXML
    private void initialize() {
        historial = FXCollections.observableArrayList();
        historialListView.setItems(historial);

        retirarDineroButton.setOnAction(e -> handleRetirarDinero());
        transferirButton.setOnAction(e -> handleTransferencia());
        cerrarSesionButton.setOnAction(e -> handleCerrarSesion());
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        saldoLabel.setText(String.format("Saldo: $%.2f", usuario.getSaldoTotal()));
        historial.clear();
        historial.addAll(usuario.getHistorialTransacciones());
    }

    private void handleRetirarDinero() {
        try {
            double monto = Double.parseDouble(montoField.getText());
            if (monto <= 0) {
                mensajeLabel.setText("El monto debe ser mayor a 0");
                return;
            }
            if (usuario.retirarDinero(monto)) {
                usuario.getHistorialTransacciones().add("- Retirado: $" + monto);
                // Registrar transacción global
                Transaccion trans = new Transaccion(usuario, Transaccion.TipoTransaccion.RETIRO, monto, "Retiro realizado por el usuario");
                Transaccion.getTransacciones().add(trans);
                actualizarInterfaz();
                mensajeLabel.setText("Dinero retirado exitosamente");
                montoField.clear();
            } else {
                mensajeLabel.setText("Saldo insuficiente");
            }
        } catch (NumberFormatException e) {
            mensajeLabel.setText("Por favor ingrese un monto válido");
        }
    }

    private void handleTransferencia() {
        try {
            double monto = Double.parseDouble(montoField.getText());
            String destinatarioCorreo = destinatarioField.getText();
            if (monto <= 0) {
                mensajeLabel.setText("El monto debe ser mayor a 0");
                return;
            }
            if (destinatarioCorreo.isEmpty()) {
                mensajeLabel.setText("Por favor ingrese el correo del destinatario");
                return;
            }
            Usuario destinatario = SistemaLogin.getInstance().getUsuario(destinatarioCorreo);
            if (destinatario == null) {
                mensajeLabel.setText("Usuario destinatario no encontrado");
                return;
            }
            if (usuario.transferirDinero(destinatario, monto)) {
                usuario.getHistorialTransacciones().add("- Transferido a " + destinatarioCorreo + ": $" + monto);
                destinatario.getHistorialTransacciones().add("+ Recibido de " + usuario.getCorreoElectronico() + ": $" + monto);
                // Registrar transacción global
                Transaccion trans = new Transaccion(usuario, Transaccion.TipoTransaccion.TRANSFERENCIA, monto, "Transferencia a " + destinatarioCorreo);
                Transaccion.getTransacciones().add(trans);
                actualizarInterfaz();
                mensajeLabel.setText("Transferencia realizada exitosamente");
                montoField.clear();
                destinatarioField.clear();
            } else {
                mensajeLabel.setText("Saldo insuficiente para la transferencia");
            }
        } catch (NumberFormatException e) {
            mensajeLabel.setText("Por favor ingrese un monto válido");
        }
    }

    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/poo/nequii/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) cerrarSesionButton.getScene().getWindow();
            stage.setTitle("Nequii - Login");
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (Exception e) {
            mensajeLabel.setText("Error al cerrar sesión: " + e.getMessage());
        }
    }
}