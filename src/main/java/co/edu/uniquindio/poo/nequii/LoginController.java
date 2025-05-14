package co.edu.uniquindio.poo.nequii;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {
    @FXML private ImageView logoImage;
    @FXML private ComboBox<String> userTypeCombo;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Button loginButton;
    @FXML private Hyperlink signUpLink;

    @FXML
    private void initialize() {
        // Configurar eventos
        loginButton.setOnAction(e -> handleLogin());
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());
        signUpLink.setOnAction(e -> handleSignUp());

        // Configurar efectos hover del botón
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("""
            -fx-background-color: #4338CA;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 5;
            -fx-cursor: hand;
        """));
        
        loginButton.setOnMouseExited(e -> loginButton.setStyle("""
            -fx-background-color: #4F46E5;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 5;
            -fx-cursor: hand;
        """));
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String userType = userTypeCombo.getValue();
        boolean rememberMe = rememberMeCheckbox.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(
                "Error",
                "Campos vacíos",
                "Por favor, complete todos los campos.",
                Alert.AlertType.ERROR
            );
            return;
        }

        try {
            if ("Administrador".equals(userType)) {
                if (autenticarAdministrador(username, password)) {
                    mostrarVentanaAdministrador();
                } else {
                    showAlert(
                        "Error",
                        "Autenticación fallida",
                        "Credenciales de administrador incorrectas.",
                        Alert.AlertType.ERROR
                    );
                }
            } else {
                if (autenticarUsuario(username, password)) {
                    mostrarVentanaUsuario();
                } else {
                    showAlert(
                        "Error",
                        "Autenticación fallida",
                        "Credenciales de usuario incorrectas.",
                        Alert.AlertType.ERROR
                    );
                }
            }
        } catch (Exception e) {
            showAlert(
                "Error",
                "Error de sistema",
                "Ocurrió un error durante la autenticación: " + e.getMessage(),
                Alert.AlertType.ERROR
            );
        }
    }

    private void handleForgotPassword() {
        // Implementar lógica de recuperación de contraseña
    }

    private void handleSignUp() {
        // Implementar lógica de registro
    }

    private boolean autenticarAdministrador(String username, String password) {
        // Implementar lógica de autenticación de administrador
        return true; // Temporal
    }

    private boolean autenticarUsuario(String username, String password) {
        // Implementar lógica de autenticación de usuario
        return true; // Temporal
    }

    private void mostrarVentanaAdministrador() {
        // Implementar apertura de ventana de administrador
        closeCurrentWindow();
    }

    private void mostrarVentanaUsuario() {
        // Implementar apertura de ventana de usuario
        closeCurrentWindow();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}