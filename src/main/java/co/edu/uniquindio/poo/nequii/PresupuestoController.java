package co.edu.uniquindio.poo.nequii;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PresupuestoController implements Initializable {
    
    // Modelos de datos
    private Presupuesto presupuestoActual;
    private CategoriaFactory categoriaFactory;
    private ObservableList<Categoria> categoriasObservable;
    private ObservableList<Presupuesto> presupuestosObservable;
    
    // Componentes FXML para presupuesto
    @FXML private TextField txtIdPresupuesto;
    @FXML private TextField txtNombrePresupuesto;
    @FXML private TextArea txtDescripcionPresupuesto;
    @FXML private TextField txtMontoTotal;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<Presupuesto> cmbPresupuestos;
    @FXML private Label lblMontoGastado;
    @FXML private Label lblEstadoPresupuesto;
    @FXML private Label lblDisponiblePresupuesto;
    @FXML private ProgressBar progressGastos;
    @FXML private Button btnCrearPresupuesto;
    @FXML private Button btnActualizarPresupuesto;
    @FXML private Button btnEliminarPresupuesto;
    @FXML private Button btnConsultarEstado;
    @FXML private Button btnMonitorearCategorias;
    
    // Componentes FXML para categorías
    @FXML private TextField txtIdCategoria;
    @FXML private TextField txtNombreCategoria;
    @FXML private ComboBox<String> cmbTipoCategoria;
    @FXML private TextField txtPresupuestoCategoria;
    @FXML private TextArea txtDescripcionCategoria;
    @FXML private Button btnCrearCategoria;
    @FXML private Button btnAgregarCategoria;
    @FXML private Button btnActualizarCategoria;
    @FXML private Button btnEliminarCategoria;
    
    // Tabla de categorías
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, String> colIdCategoria;
    @FXML private TableColumn<Categoria, String> colNombreCategoria;
    @FXML private TableColumn<Categoria, String> colDescripcionCategoria;
    @FXML private TableColumn<Categoria, Double> colPresupuestoCategoria;
    @FXML private TableColumn<Categoria, Double> colGastoCategoria;
    @FXML private TableColumn<Categoria, Double> colDisponibleCategoria;
    @FXML private TableColumn<Categoria, String> colEstadoCategoria;
    
    // Componentes para transacciones
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private ComboBox<Transaccion.TipoTransaccion> cmbTipoTransaccion;
    @FXML private TextField txtMontoGasto;
    @FXML private TextArea txtDescripcionGasto;
    @FXML private Button btnRegistrarGasto;
    
    // Lista de categorías en riesgo
    @FXML private ListView<Categoria> lstCategoriasRiesgo;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar componentes y configuraciones
        inicializarComponentes();
        configurarTabla();
        configurarListeners();
        cargarPresupuestosExistentes();
    }
    
    /**
     * Inicializa los componentes y configura valores iniciales
     */
    private void inicializarComponentes() {
        // Inicializar factory de categorías
        categoriaFactory = new CategoriaFactory();
        
        // Configurar combo de tipos de categoría
        cmbTipoCategoria.getItems().addAll("Comida", "Transporte", "Vivienda", "Entretenimiento", "Salud", "Educación", "Otros");
        
        // Inicializar listas observables
        categoriasObservable = FXCollections.observableArrayList();
        presupuestosObservable = FXCollections.observableArrayList();
        
        // Configurar comboBox de presupuestos
        cmbPresupuestos.setItems(presupuestosObservable);
        cmbPresupuestos.setConverter(new StringConverter<Presupuesto>() {
            @Override
            public String toString(Presupuesto presupuesto) {
                return presupuesto != null ? presupuesto.getNombre() + " (" + presupuesto.getIdPresupuesto() + ")" : "";
            }
            
            @Override
            public Presupuesto fromString(String string) {
                return null; // No se usa para conversión inversa
            }
        });
        
        // Configurar comboBox de categorías
        cmbCategoria.setItems(FXCollections.observableArrayList());
        
        // Establecer fechas por defecto en DatePickers
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaFin.setValue(LocalDate.now().plusMonths(1));
    }
    
    /**
     * Configura las columnas de la tabla de categorías
     */
    private void configurarTabla() {
        // Tabla editable
        tblCategorias.setEditable(true);
        tblCategorias.setItems(categoriasObservable);
        
        // Configurar columnas
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        
        colNombreCategoria.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombreCategoria.setCellFactory(TextFieldTableCell.forTableColumn());
        colNombreCategoria.setOnEditCommit(event -> {
            Categoria categoria = event.getRowValue();
            categoria.setNombre(event.getNewValue());
            tblCategorias.refresh();
        });
        
        colDescripcionCategoria = new TableColumn<>("Descripción");
        colDescripcionCategoria.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDescripcionCategoria.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescripcionCategoria.setOnEditCommit(event -> {
            Categoria categoria = event.getRowValue();
            categoria.setDescripcion(event.getNewValue());
            tblCategorias.refresh();
        });
        
        colPresupuestoCategoria.setCellValueFactory(new PropertyValueFactory<>("presupuestoAsignado"));
        colPresupuestoCategoria.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colPresupuestoCategoria.setOnEditCommit(event -> {
            Categoria categoria = event.getRowValue();
            categoria.setPresupuestoAsignado(event.getNewValue());
            presupuestoActual.calcularGastoPorCategoria();
            actualizarVista();
            tblCategorias.refresh();
        });
        
        colGastoCategoria.setCellValueFactory(new PropertyValueFactory<>("gastoActual"));
        
        // Columnas calculadas
        colDisponibleCategoria = new TableColumn<>("Disponible");
        colDisponibleCategoria.setCellValueFactory(cellData -> {
            Categoria categoria = cellData.getValue();
            double disponible = categoria.getPresupuestoAsignado() - categoria.getGastoActual();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> disponible);
        });
        
        colEstadoCategoria = new TableColumn<>("Estado");
        colEstadoCategoria.setCellValueFactory(cellData -> {
            Categoria categoria = cellData.getValue();
            double porcentaje = categoria.getPresupuestoAsignado() > 0 ? 
                (categoria.getGastoActual() / categoria.getPresupuestoAsignado()) * 100 : 0;
            String estado = porcentaje >= 100 ? "EXCEDIDO" : 
                           porcentaje >= 80 ? "EN RIESGO" : "OK";
            return javafx.beans.binding.Bindings.createStringBinding(() -> estado);
        });
        
        // Añadir columnas
        if (!tblCategorias.getColumns().contains(colDescripcionCategoria)) {
            tblCategorias.getColumns().add(2, colDescripcionCategoria);
        }
        
        if (!tblCategorias.getColumns().contains(colDisponibleCategoria)) {
            tblCategorias.getColumns().add(colDisponibleCategoria);
        }
        
        if (!tblCategorias.getColumns().contains(colEstadoCategoria)) {
            tblCategorias.getColumns().add(colEstadoCategoria);
        }
    }
    
    /**
     * Configura los listeners para los controles UI
     */
    private void configurarListeners() {
        // Listener para selección de presupuesto
        cmbPresupuestos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarPresupuesto(newVal);
            }
        });
        
        // Listener para selección de categoría en la tabla
        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesCategoria(newSelection);
                cmbCategoria.setValue(newSelection);
            }
        });
    }
    
    /**
     * Carga los presupuestos existentes
     */
    private void cargarPresupuestosExistentes() {
        List<Presupuesto> presupuestos = Presupuesto.obtenerTodosLosPresupuestos();
        presupuestosObservable.clear();
        presupuestosObservable.addAll(presupuestos);
        
        if (!presupuestos.isEmpty()) {
            cmbPresupuestos.getSelectionModel().select(0);
        } else {
            habilitarControlesCategorias(false);
        }
    }
    
    /**
     * Carga un presupuesto seleccionado en la interfaz
     */
    private void cargarPresupuesto(Presupuesto presupuesto) {
        presupuestoActual = presupuesto;
        
        // Actualizar campos
        txtIdPresupuesto.setText(presupuesto.getIdPresupuesto());
        txtNombrePresupuesto.setText(presupuesto.getNombre());
        txtDescripcionPresupuesto.setText(presupuesto.getDescripcion());
        txtMontoTotal.setText(String.valueOf(presupuesto.getMontoTotal()));
        dpFechaInicio.setValue(presupuesto.getFechaInicio());
        dpFechaFin.setValue(presupuesto.getFechaFin());
        
        // Cargar categorías
        categoriasObservable.clear();
        categoriasObservable.addAll(presupuesto.getCategorias());
        
        // Configurar combo de categorías para transacciones
        ObservableList<Categoria> categoriasCombo = FXCollections.observableArrayList(presupuesto.getCategorias());
        cmbCategoria.setItems(categoriasCombo);
        
        // Habilitar controles
        habilitarControlesCategorias(true);
        
        // Actualizar vista
        actualizarVista();
        
        // Actualizar categorías en riesgo
        actualizarCategoriasEnRiesgo();
    }
    
    /**
     * Muestra los detalles de una categoría seleccionada
     */
    private void mostrarDetallesCategoria(Categoria categoria) {
        if (categoria != null) {
            txtIdCategoria.setText(categoria.getIdCategoria());
            txtNombreCategoria.setText(categoria.getNombre());
            txtDescripcionCategoria.setText(categoria.getDescripcion());
            txtPresupuestoCategoria.setText(String.valueOf(categoria.getPresupuestoAsignado()));
        }
    }
    
    /**
     * Actualiza la vista con los datos actuales del presupuesto
     */
    private void actualizarVista() {
        if (presupuestoActual != null) {
            presupuestoActual.calcularGastoPorCategoria();
            
            // Actualizar etiquetas
            lblMontoGastado.setText(String.format("$%.2f", presupuestoActual.getMontoGastado()));
            
            double disponible = presupuestoActual.getMontoTotal() - presupuestoActual.getMontoGastado();
            lblDisponiblePresupuesto.setText(String.format("$%.2f", disponible));
            
            // Actualizar barra de progreso
            double porcentaje = presupuestoActual.getMontoTotal() > 0 ? 
                presupuestoActual.getMontoGastado() / presupuestoActual.getMontoTotal() : 0;
            progressGastos.setProgress(Math.min(porcentaje, 1.0));
            
            // Determinar el color según el estado
            String estadoActual = presupuestoActual.getEstado().getClass().getSimpleName();
            lblEstadoPresupuesto.setText(estadoActual);
            
            if (estadoActual.equals("DentroPresupuesto")) {
                progressGastos.setStyle("-fx-accent: green;");
            } else if (estadoActual.equals("EnRiesgo")) {
                progressGastos.setStyle("-fx-accent: orange;");
            } else {
                progressGastos.setStyle("-fx-accent: red;");
            }
        }
    }
    
    /**
     * Actualiza la lista de categorías en riesgo
     */
    private void actualizarCategoriasEnRiesgo() {
        if (presupuestoActual == null || lstCategoriasRiesgo == null) {
            return;
        }
        
        List<Categoria> categoriasRiesgo = presupuestoActual.obtenerCategoriasEnRiesgo();
        ObservableList<Categoria> categoriasRiesgoObs = FXCollections.observableArrayList(categoriasRiesgo);
        
        lstCategoriasRiesgo.setItems(categoriasRiesgoObs);
        
        // Configurar visualización
        lstCategoriasRiesgo.setCellFactory(param -> new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    double porcentaje = (item.getGastoActual() / item.getPresupuestoAsignado()) * 100;
                    
                    setText(String.format("%s: %.2f%% usado", item.getNombre(), porcentaje));
                    
                    if (porcentaje >= 100) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (porcentaje >= 80) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }
    
    /**
     * Crea un nuevo presupuesto
     */
    @FXML
    private void crearPresupuesto() {
        try {
            String id = txtIdPresupuesto.getText();
            String nombre = txtNombrePresupuesto.getText();
            String descripcion = txtDescripcionPresupuesto.getText();
            double montoTotal = Double.parseDouble(txtMontoTotal.getText());
            LocalDate fechaInicio = dpFechaInicio.getValue();
            LocalDate fechaFin = dpFechaFin.getValue();
            
            // Validaciones
            if (id == null || id.trim().isEmpty()) {
                mostrarError("El ID del presupuesto es obligatorio");
                return;
            }
            
            if (nombre == null || nombre.trim().isEmpty()) {
                mostrarError("El nombre del presupuesto es obligatorio");
                return;
            }
            
            if (fechaInicio == null) {
                fechaInicio = LocalDate.now();
            }
            
            if (fechaFin == null) {
                fechaFin = LocalDate.now().plusMonths(1);
            }
            
            // Crear presupuesto
            try {
                Presupuesto nuevoPresupuesto = Presupuesto.crearPresupuesto(
                        id, nombre, descripcion, montoTotal, fechaInicio, fechaFin);
                
                // Actualizar UI
                presupuestosObservable.add(nuevoPresupuesto);
                cmbPresupuestos.setValue(nuevoPresupuesto);
                
                mostrarMensaje("Presupuesto creado con éxito",
                        "Se ha creado el presupuesto: " + nombre);
                
            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            }
            
        } catch (NumberFormatException e) {
            mostrarError("El monto total debe ser un número válido");
        }
    }
    
    /**
     * Actualiza un presupuesto existente
     */
    @FXML
    private void actualizarPresupuesto() {
        if (presupuestoActual == null) {
            mostrarError("Debe seleccionar un presupuesto para actualizarlo");
            return;
        }
        
        try {
            String id = presupuestoActual.getIdPresupuesto();
            String nombre = txtNombrePresupuesto.getText();
            String descripcion = txtDescripcionPresupuesto.getText();
            double montoTotal = Double.parseDouble(txtMontoTotal.getText());
            LocalDate fechaInicio = dpFechaInicio.getValue();
            LocalDate fechaFin = dpFechaFin.getValue();
            
            if (Presupuesto.actualizarPresupuesto(id, nombre, descripcion, montoTotal, fechaInicio, fechaFin)) {
                actualizarVista();
                int index = cmbPresupuestos.getItems().indexOf(presupuestoActual);
                if (index >= 0) {
                    cmbPresupuestos.getItems().set(index, presupuestoActual);
                }
                
                mostrarMensaje("Presupuesto actualizado", 
                        "Se ha actualizado el presupuesto: " + nombre);
            } else {
                mostrarError("No se pudo actualizar el presupuesto");
            }
            
        } catch (NumberFormatException e) {
            mostrarError("El monto total debe ser un número válido");
        }
    }
    
    /**
     * Elimina un presupuesto existente
     */
    @FXML
    private void eliminarPresupuesto() {
        if (presupuestoActual == null) {
            mostrarError("Debe seleccionar un presupuesto para eliminarlo");
            return;
        }
        
        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este presupuesto?");
        confirmacion.setContentText("Se eliminará el presupuesto: " + presupuestoActual.getNombre());
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String id = presupuestoActual.getIdPresupuesto();
            String nombre = presupuestoActual.getNombre();
            
            if (Presupuesto.eliminarPresupuesto(id)) {
                presupuestosObservable.remove(presupuestoActual);
                limpiarCamposPresupuesto();
                categoriasObservable.clear();
                
                mostrarMensaje("Presupuesto eliminado", 
                        "Se ha eliminado el presupuesto: " + nombre);
                
                if (!presupuestosObservable.isEmpty()) {
                    cmbPresupuestos.getSelectionModel().select(0);
                } else {
                    presupuestoActual = null;
                    habilitarControlesCategorias(false);
                }
            } else {
                mostrarError("No se pudo eliminar el presupuesto");
            }
        }
    }
    
    /**
     * Consulta y muestra el estado del presupuesto
     */
    @FXML
    private void consultarEstadoPresupuesto() {
        if (presupuestoActual == null) {
            mostrarError("No hay un presupuesto seleccionado");
            return;
        }
        
        String estadoInfo = presupuestoActual.consultarEstadoPresupuesto();
        
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Estado del Presupuesto");
        dialog.setHeaderText("Información del presupuesto: " + presupuestoActual.getNombre());
        
        TextArea textArea = new TextArea(estadoInfo);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        dialog.getDialogPane().setContent(textArea);
        dialog.showAndWait();
    }
    
    /**
     * Monitorea y muestra el gasto por categoría
     */
    @FXML
    private void monitorearGastosPorCategoria() {
        if (presupuestoActual == null) {
            mostrarError("No hay un presupuesto seleccionado");
            return;
        }
        
        String informe = presupuestoActual.monitorearGastoPorCategoria();
        
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Informe de Gastos por Categoría");
        dialog.setHeaderText("Presupuesto: " + presupuestoActual.getNombre());
        
        TextArea textArea = new TextArea(informe);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(400);
        dialog.showAndWait();
        
        actualizarCategoriasEnRiesgo();
    }
    
    /**
     * Crea una nueva categoría personalizada
     */
    @FXML
    private void crearCategoria() {
        if (presupuestoActual == null) {
            mostrarError("Debe seleccionar un presupuesto primero");
            return;
        }
        
        try {
            String id = txtIdCategoria.getText();
            String nombre = txtNombreCategoria.getText();
            String descripcion = txtDescripcionCategoria.getText();
            double presupuesto = Double.parseDouble(txtPresupuestoCategoria.getText());
            
            // Validaciones
            if (id == null || id.trim().isEmpty()) {
                mostrarError("El ID de la categoría es obligatorio");
                return;
            }
            
            if (nombre == null || nombre.trim().isEmpty()) {
                mostrarError("El nombre de la categoría es obligatorio");
                return;
            }
            
            // Crear categoría
            Categoria nuevaCategoria = Categoria.crearCategoria(id, nombre, presupuesto, descripcion);
            
            try {
                presupuestoActual.agregarCategoria(nuevaCategoria);
                categoriasObservable.add(nuevaCategoria);
                cmbCategoria.getItems().add(nuevaCategoria);
                
                limpiarCamposCategoria();
                actualizarVista();
                
                mostrarMensaje("Categoría creada", 
                        "Se ha creado la categoría: " + nombre);
                
            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            }
            
        } catch (NumberFormatException e) {
            mostrarError("El presupuesto debe ser un número válido");
        }
    }
    
    /**
     * Agrega una categoría predefinida mediante el factory
     */
    @FXML
    private void agregarCategoria() {
        if (presupuestoActual == null) {
            mostrarError("Debe seleccionar un presupuesto primero");
            return;
        }
        
        try {
            String tipo = cmbTipoCategoria.getValue();
            double presupuestoCategoria = Double.parseDouble(txtPresupuestoCategoria.getText());
            
            if (tipo == null || tipo.isEmpty()) {
                mostrarError("Seleccione un tipo de categoría");
                return;
            }
            
            Categoria nuevaCategoria = categoriaFactory.crearCategoria(tipo, presupuestoCategoria);
            
            if (txtDescripcionCategoria.getText() != null && !txtDescripcionCategoria.getText().isEmpty()) {
                nuevaCategoria.setDescripcion(txtDescripcionCategoria.getText());
            }
            
            try {
                presupuestoActual.agregarCategoria(nuevaCategoria);
                categoriasObservable.add(nuevaCategoria);
                cmbCategoria.getItems().add(nuevaCategoria);
                
                limpiarCamposCategoria();
                actualizarVista();
                
                mostrarMensaje("Categoría agregada", 
                        "Se ha agregado la categoría: " + nuevaCategoria.getNombre());
                
            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            }
            
        } catch (NumberFormatException e) {
            mostrarError("El presupuesto debe ser un número válido");
        }
    }
    
    /**
     * Actualiza una categoría existente
     */
    @FXML
    private void actualizarCategoria() {
        Categoria categoriaSeleccionada = tblCategorias.getSelectionModel().getSelectedItem();
        
        if (categoriaSeleccionada == null) {
            mostrarError("Debe seleccionar una categoría para actualizarla");
            return;
        }
        
        try {
            String nombre = txtNombreCategoria.getText();
            String descripcion = txtDescripcionCategoria.getText();
            double presupuesto = Double.parseDouble(txtPresupuestoCategoria.getText());
            
            categoriaSeleccionada.actualizarCategoria(nombre, descripcion, presupuesto);
            
            tblCategorias.refresh();
            presupuestoActual.calcularGastoPorCategoria();
            actualizarVista();
            actualizarCategoriasEnRiesgo();
            
            mostrarMensaje("Categoría actualizada", 
                    "Se ha actualizado la categoría: " + nombre);
            
        } catch (NumberFormatException e) {
            mostrarError("El presupuesto debe ser un número válido");
        }
    }
    
    /**
     * Elimina una categoría existente
     */
    @FXML
    private void eliminarCategoria() {
        if (presupuestoActual == null) {
            mostrarError("Debe seleccionar un presupuesto primero");
            return;
        }
        
        Categoria categoriaSeleccionada = tblCategorias.getSelectionModel().getSelectedItem();
        
        if (categoriaSeleccionada == null) {
            mostrarError("Debe seleccionar una categoría para eliminarla");
            return;
        }
        
        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta categoría?");
        confirmacion.setContentText("Se eliminará la categoría: " + categoriaSeleccionada.getNombre());
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String idCategoria = categoriaSeleccionada.getIdCategoria();
            String nombreCategoria = categoriaSeleccionada.getNombre();
            
            if (presupuestoActual.eliminarCategoria(idCategoria)) {
                categoriasObservable.remove(categoriaSeleccionada);
                cmbCategoria.getItems().remove(categoriaSeleccionada);
                
                actualizarVista();
                limpiarCamposCategoria();
                
                mostrarMensaje("Categoría eliminada", 
                        "Se ha eliminado la categoría: " + nombreCategoria);
            } else {
                mostrarError("No se pudo eliminar la categoría");
            }
        }
    }
    
    /**
     * Registra un gasto en una categoría
     */
    @FXML
    private void registrarGasto() {
        if (presupuestoActual == null) {
            mostrarError("Debe seleccionar un presupuesto primero");
            return;
        }
        
        try {
            Categoria categoriaSeleccionada = cmbCategoria.getValue();
            double montoGasto = Double.parseDouble(txtMontoGasto.getText());
            String descripcionGasto = txtDescripcionGasto != null ? txtDescripcionGasto.getText() : "";
            Transaccion.TipoTransaccion tipoSeleccionado = cmbTipoTransaccion.getValue();
            
            if (categoriaSeleccionada == null) {
                mostrarError("Seleccione una categoría");
                return;
            }
            
            // Registrar la transacción y actualizar el gasto
            categoriaSeleccionada.asignarTransaccion(tipoSeleccionado, montoGasto, descripcionGasto);
            presupuestoActual.calcularGastoPorCategoria();
            
            // Actualizar la vista
            actualizarVista();
            tblCategorias.refresh();
            actualizarCategoriasEnRiesgo();
            
            // Limpiar campo de monto
            txtMontoGasto.clear();
            if (txtDescripcionGasto != null) {
                txtDescripcionGasto.clear();
            }
            
            mostrarMensaje("Gasto registrado", 
                    String.format("Se ha registrado un gasto de $%.2f en la categoría %s", 
                            montoGasto, categoriaSeleccionada.getNombre()));
            
        } catch (NumberFormatException e) {
            mostrarError("El monto debe ser un número válido");
        }
    }
    
    /**
     * Habilita o deshabilita los controles de categorías y transacciones
     */
    private void habilitarControlesCategorias(boolean habilitar) {
        txtIdCategoria.setDisable(!habilitar);
        txtNombreCategoria.setDisable(!habilitar);
        cmbTipoCategoria.setDisable(!habilitar);
        txtPresupuestoCategoria.setDisable(!habilitar);
        txtDescripcionCategoria.setDisable(!habilitar);
        btnCrearCategoria.setDisable(!habilitar);
        btnAgregarCategoria.setDisable(!habilitar);
        btnActualizarCategoria.setDisable(!habilitar);
        btnEliminarCategoria.setDisable(!habilitar);
        
        cmbCategoria.setDisable(!habilitar);
        txtMontoGasto.setDisable(!habilitar);
        txtDescripcionGasto.setDisable(!habilitar);
        btnRegistrarGasto.setDisable(!habilitar);
        
        btnConsultarEstado.setDisable(!habilitar);
        btnMonitorearCategorias.setDisable(!habilitar);
    }
    
    /**
     * Limpia los campos relacionados a la creación/edición de categorías
     */
    private void limpiarCamposCategoria() {
        txtIdCategoria.clear();
        txtNombreCategoria.clear();
        cmbTipoCategoria.getSelectionModel().clearSelection();
        txtPresupuestoCategoria.clear();
        txtDescripcionCategoria.clear();
    }
    
    /**
     * Limpia los campos relacionados a la creación/edición de presupuestos
     */
    private void limpiarCamposPresupuesto() {
        txtIdPresupuesto.clear();
        txtNombrePresupuesto.clear();
        txtDescripcionPresupuesto.clear();
        txtMontoTotal.clear();
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaFin.setValue(LocalDate.now().plusMonths(1));
    }
    
    /**
     * Muestra un diálogo de error con el mensaje especificado
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de información con el mensaje especificado
     */
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}