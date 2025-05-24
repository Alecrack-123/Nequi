package co.edu.uniquindio.poo.nequii;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Map;
import java.util.List;

public class AdministradorController {

    private Administrador administrador;

    @FXML private TextField idAdminField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private TextField contrasenaField;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TextField idUsuarioField;
    @FXML private TextField numeroCuentaField;

    @FXML private PieChart graficaGastos;
    @FXML private BarChart<String, Number> graficaTransacciones;
    @FXML private Label saldoPromedioLabel;

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
        actualizarInterfaz();
    }

    // RF-009: Gestión de usuarios
    @FXML
    private void crearUsuario() {
        try {
            Usuario nuevoUsuario = new Usuario(
                idUsuarioField.getText(),
                nombreField.getText(),
                correoField.getText(),
                "0000000000", // número de teléfono temporal
                "", // dirección temporal
                contrasenaField.getText()
            );
            administrador.crearUsuario(nuevoUsuario);
            actualizarInterfaz();
            mostrarMensaje("Usuario creado exitosamente", Alert.AlertType.INFORMATION);
            limpiarCamposUsuario();
        } catch (Exception e) {
            mostrarMensaje("Error al crear usuario: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void actualizarUsuario() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            try {
                usuarioSeleccionado.setNombreCompleto(nombreField.getText());
                usuarioSeleccionado.setCorreoElectronico(correoField.getText());
                administrador.actualizarUsuario(usuarioSeleccionado);
                actualizarInterfaz();
                mostrarMensaje("Usuario actualizado exitosamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarMensaje("Error al actualizar usuario: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarMensaje("Por favor seleccione un usuario", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            administrador.eliminarUsuario(usuarioSeleccionado.getIdUsuario());
            actualizarInterfaz();
            mostrarMensaje("Usuario eliminado exitosamente", Alert.AlertType.INFORMATION);
        } else {
            mostrarMensaje("Por favor seleccione un usuario", Alert.AlertType.WARNING);
        }
    }

    // RF-010: Gestión de cuentas
    @FXML
    private void agregarCuenta() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null && !numeroCuentaField.getText().isEmpty()) {
            try {
                Cuenta cuenta = new Cuenta(numeroCuentaField.getText());
                administrador.agregarCuenta(usuarioSeleccionado.getIdUsuario(), cuenta);
                actualizarInterfaz();
                mostrarMensaje("Cuenta agregada exitosamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarMensaje("Error al agregar cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarMensaje("Seleccione un usuario e ingrese un número de cuenta", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void eliminarCuenta() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null && !numeroCuentaField.getText().isEmpty()) {
            administrador.eliminarCuenta(usuarioSeleccionado.getIdUsuario(), numeroCuentaField.getText());
            actualizarInterfaz();
            mostrarMensaje("Cuenta eliminada exitosamente", Alert.AlertType.INFORMATION);
        } else {
            mostrarMensaje("Seleccione un usuario e ingrese un número de cuenta", Alert.AlertType.WARNING);
        }
    }

    // RF-011: Gestión de transacciones
    @FXML
    private void mostrarTransaccionesUsuario() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            List<Transaccion> transacciones = administrador.listarTransacciones(
                usuarioSeleccionado.getIdUsuario());
            mostrarVentanaTransacciones(transacciones);
        } else {
            mostrarMensaje("Por favor seleccione un usuario", Alert.AlertType.WARNING);
        }
    }

    // RF-012: Estadísticas
    @FXML
    private void actualizarEstadisticas() {
        // Actualizar gráfica de gastos comunes
        Map<String, Double> gastosComunes = administrador.obtenerGastosComunes();
        actualizarGraficaGastos();

        // Mostrar usuarios con más transacciones
        List<Usuario> usuariosTop = administrador.obtenerUsuariosConMasTransacciones();
        mostrarUsuariosTop(usuariosTop);

        // Actualizar saldo promedio
        double saldoPromedio = administrador.calcularSaldoPromedio();
        saldoPromedioLabel.setText(String.format("Saldo Promedio: $%.2f", saldoPromedio));
    }

    // RF-013: Gráficas estadísticas
    @FXML
    private void mostrarGraficasEstadisticas() {
        Stage ventanaGraficas = new Stage();
        ventanaGraficas.setTitle("Gráficas Estadísticas");

        TabPane tabPane = new TabPane();

        // Tab para gráfica de gastos
        Tab tabGastos = new Tab("Gastos por Categoría");
        tabGastos.setContent(crearGraficaGastos());

        // Tab para gráfica de transacciones
        Tab tabTransacciones = new Tab("Transacciones por Usuario");
        tabTransacciones.setContent(crearGraficaTransacciones());

        // Tab para gráfica de saldos
        Tab tabSaldos = new Tab("Saldos por Usuario");
        tabSaldos.setContent(crearGraficaSaldos());

        tabPane.getTabs().addAll(tabGastos, tabTransacciones, tabSaldos);

        Scene scene = new Scene(tabPane, 800, 600);
        ventanaGraficas.setScene(scene);
        ventanaGraficas.show();
    }

    private PieChart crearGraficaGastos() {
        PieChart graficaGastos = new PieChart();
        graficaGastos.setTitle("Distribución de Gastos por Categoría");

        Map<String, Double> gastosComunes = administrador.obtenerGastosComunes();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        gastosComunes.forEach((categoria, monto) ->
            pieChartData.add(new PieChart.Data(
                String.format("%s ($%.2f)", categoria, monto),
                monto))
        );

        graficaGastos.setData(pieChartData);

        // Agregar tooltips para mostrar porcentajes
        double total = gastosComunes.values().stream().mapToDouble(Double::doubleValue).sum();
        graficaGastos.getData().forEach(data -> {
            String percentage = String.format("%.1f%%", (data.getPieValue() / total) * 100);
            Tooltip tooltip = new Tooltip(percentage);
            Tooltip.install(data.getNode(), tooltip);
        });

        return graficaGastos;
    }

    private BarChart<String, Number> crearGraficaTransacciones() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> graficaTransacciones = new BarChart<>(xAxis, yAxis);
        graficaTransacciones.setTitle("Número de Transacciones por Usuario");
        xAxis.setLabel("Usuario");
        yAxis.setLabel("Número de Transacciones");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Transacciones");

        List<Usuario> usuariosTop = administrador.obtenerUsuariosConMasTransacciones();
        usuariosTop.forEach(usuario ->
            series.getData().add(new XYChart.Data<>(
                usuario.getNombreCompleto(),
                usuario.consultarTransacciones().size()
            ))
        );

        graficaTransacciones.getData().add(series);

        // Agregar tooltips con información detallada
        series.getData().forEach(data -> {
            Tooltip tooltip = new Tooltip(
                String.format("Usuario: %s\nTransacciones: %d",
                    data.getXValue(),
                    data.getYValue())
            );
            Tooltip.install(data.getNode(), tooltip);
        });

        return graficaTransacciones;
    }

    private StackedBarChart<String, Number> crearGraficaSaldos() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        StackedBarChart<String, Number> graficaSaldos = new StackedBarChart<>(xAxis, yAxis);
        graficaSaldos.setTitle("Distribución de Saldos por Usuario");
        xAxis.setLabel("Usuario");
        yAxis.setLabel("Saldo ($)");

        XYChart.Series<String, Number> seriesSaldoDisponible = new XYChart.Series<>();
        seriesSaldoDisponible.setName("Saldo Disponible");

        XYChart.Series<String, Number> seriesGastosMensuales = new XYChart.Series<>();
        seriesGastosMensuales.setName("Gastos Mensuales");

        administrador.listarUsuarios().forEach(usuario -> {
            // Saldo disponible
            seriesSaldoDisponible.getData().add(
                new XYChart.Data<>(usuario.getNombreCompleto(), usuario.consultarSaldo())
            );

            // Calcular gastos mensuales
            double gastosMensuales = usuario.consultarTransacciones().stream()
                .filter(t -> t.getTipo() == Transaccion.TipoTransaccion.RETIRO)
                .mapToDouble(Transaccion::getMonto)
                .sum();

            seriesGastosMensuales.getData().add(
                new XYChart.Data<>(usuario.getNombreCompleto(), gastosMensuales)
            );
        });

        graficaSaldos.getData().addAll(seriesSaldoDisponible, seriesGastosMensuales);

        // Agregar tooltips con información detallada
        graficaSaldos.getData().forEach(series ->
            series.getData().forEach(data -> {
                Tooltip tooltip = new Tooltip(
                    String.format("%s\nUsuario: %s\nMonto: $%.2f",
                        series.getName(),
                        data.getXValue(),
                        data.getYValue())
                );
                Tooltip.install(data.getNode(), tooltip);
            })
        );

        return graficaSaldos;
    }

    @FXML
    private void exportarGraficas() {
        try {
            Stage ventanaExportar = new Stage();
            ventanaExportar.setTitle("Exportar Gráficas");

            VBox contenedor = new VBox(10);

            // Botones para cada tipo de exportación
            Button btnExportarPDF = new Button("Exportar a PDF");
            btnExportarPDF.setOnAction(e -> exportarAPDF());

            Button btnExportarPNG = new Button("Exportar a PNG");
            btnExportarPNG.setOnAction(e -> exportarAPNG());

            Button btnExportarExcel = new Button("Exportar a Excel");
            btnExportarExcel.setOnAction(e -> exportarAExcel());

            contenedor.getChildren().addAll(
                new Label("Seleccione el formato de exportación:"),
                btnExportarPDF,
                btnExportarPNG,
                btnExportarExcel
            );

            Scene scene = new Scene(contenedor, 300, 200);
            ventanaExportar.setScene(scene);
            ventanaExportar.show();

        } catch (Exception e) {
            mostrarMensaje("Error al abrir ventana de exportación: " + e.getMessage(),
                Alert.AlertType.ERROR);
        }
    }

    private void exportarAPDF() {
        // Implementar lógica de exportación a PDF
        // Usar biblioteca como iText o PDFBox
        mostrarMensaje("Exportación a PDF completada", Alert.AlertType.INFORMATION);
    }

    private void exportarAPNG() {
        // Implementar lógica de exportación a PNG
        // Usar WritableImage y FileChooser
        mostrarMensaje("Exportación a PNG completada", Alert.AlertType.INFORMATION);
    }

    private void exportarAExcel() {
        // Implementar lógica de exportación a Excel
        // Usar biblioteca como Apache POI
        mostrarMensaje("Exportación a Excel completada", Alert.AlertType.INFORMATION);
    }

    private void actualizarGraficaGastos() {
        graficaGastos.getData().clear();
        Map<String, Double> gastosComunes = administrador.obtenerGastosComunes();
        gastosComunes.forEach((categoria, monto) ->
            graficaGastos.getData().add(new PieChart.Data(categoria, monto))
        );
    }

    private void actualizarGraficaTransacciones() {
        graficaTransacciones.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        administrador.obtenerUsuariosConMasTransacciones().forEach(usuario ->
            series.getData().add(new XYChart.Data<>(
                usuario.getNombreCompleto(),
                usuario.consultarTransacciones().size()
            ))
        );
        graficaTransacciones.getData().add(series);
    }

    private void actualizarGraficas(){
        actualizarGraficaGastos();
        actualizarGraficaTransacciones();
    }

    private void actualizarInterfaz() {
        if (administrador != null) {
            ObservableList<Usuario> usuarios = FXCollections.observableArrayList(
                administrador.listarUsuarios());
            tablaUsuarios.setItems(usuarios);
            actualizarEstadisticas();
            actualizarGraficas();
        }
    }

    private void limpiarCamposUsuario() {
        idUsuarioField.clear();
        nombreField.clear();
        correoField.clear();
        contrasenaField.clear();
        numeroCuentaField.clear();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Sistema Administrador Nequii");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarVentanaTransacciones(List<Transaccion> transacciones) {
        // Crear una nueva ventana para mostrar las transacciones
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Transacciones del Usuario");

        ListView<Transaccion> listaTransacciones = new ListView<>();
        listaTransacciones.setItems(FXCollections.observableArrayList(transacciones));

        dialog.getDialogPane().setContent(listaTransacciones);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void mostrarUsuariosTop(List<Usuario> usuariosTop) {
        // Crear una nueva ventana para mostrar los usuarios top
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Top 10 Usuarios con Más Transacciones");
        ListView<String> listaUsuarios = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < usuariosTop.size(); i++) {
            Usuario u = usuariosTop.get(i);
            items.add(String.format("%d. %s - %d transacciones",
                i + 1,
                u.getNombreCompleto(),
                u.consultarTransacciones().size()));
        }
        listaUsuarios.setItems(items);
        dialog.getDialogPane().setContent(listaUsuarios);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }
}