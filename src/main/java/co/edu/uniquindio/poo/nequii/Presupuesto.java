package co.edu.uniquindio.poo.nequii;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Presupuesto {
    private String idPresupuesto;
    private String nombre;
    private String descripcion;
    private double montoTotal;
    private double montoGastado;
    private List<Categoria> categorias;
    private EstadoPresupuesto estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private static Map<String, Presupuesto> presupuestosRegistrados = new HashMap<>();

    public Presupuesto(String id, double montoTotal) {
        this.idPresupuesto = id;
        this.montoTotal = montoTotal;
        this.montoGastado = 0;
        this.categorias = new ArrayList<>();
        this.estado = new DentroPresupuesto(); // Estado inicial
        this.fechaInicio = LocalDate.now();
        this.fechaFin = LocalDate.now().plusMonths(1); // Por defecto 1 mes
    }
    
    /**
     * Constructor completo para la creación de presupuestos
     */
    public Presupuesto(String id, String nombre, String descripcion, double montoTotal, 
                      LocalDate fechaInicio, LocalDate fechaFin) {
        this.idPresupuesto = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.montoTotal = montoTotal;
        this.montoGastado = 0;
        this.categorias = new ArrayList<>();
        this.estado = new DentroPresupuesto();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    /**
     * Crea un nuevo presupuesto y lo registra en el mapa de presupuestos
     * @return El presupuesto creado
     */
    public static Presupuesto crearPresupuesto(String id, String nombre, String descripcion, 
                                             double montoTotal, LocalDate fechaInicio, LocalDate fechaFin) {
        // Validar que no exista un presupuesto con el mismo ID
        if (presupuestosRegistrados.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un presupuesto con el ID: " + id);
        }
        
        // Validar monto total
        if (montoTotal <= 0) {
            throw new IllegalArgumentException("El monto total debe ser mayor a cero");
        }
        
        // Validar fechas
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        
        Presupuesto nuevoPresupuesto = new Presupuesto(id, nombre, descripcion, montoTotal, fechaInicio, fechaFin);
        presupuestosRegistrados.put(id, nuevoPresupuesto);
        return nuevoPresupuesto;
    }
    
    /**
     * Actualiza los datos de un presupuesto existente
     * @return true si se actualizó correctamente, false si no existe
     */
    public static boolean actualizarPresupuesto(String id, String nombre, String descripcion, 
                                              double montoTotal, LocalDate fechaInicio, LocalDate fechaFin) {
        Presupuesto presupuesto = presupuestosRegistrados.get(id);
        
        if (presupuesto == null) {
            return false;
        }
        
        // Actualizar solo los campos proporcionados
        if (nombre != null && !nombre.trim().isEmpty()) {
            presupuesto.setNombre(nombre);
        }
        
        if (descripcion != null) {
            presupuesto.setDescripcion(descripcion);
        }
        
        if (montoTotal > 0) {
            presupuesto.setMontoTotal(montoTotal);
            // Recalcular estado con el nuevo monto total
            presupuesto.calcularGastoPorCategoria();
        }
        
        if (fechaInicio != null) {
            presupuesto.setFechaInicio(fechaInicio);
        }
        
        if (fechaFin != null) {
            presupuesto.setFechaFin(fechaFin);
        }
        
        return true;
    }
    
    /**
     * Elimina un presupuesto por su ID
     * @return true si se eliminó correctamente, false si no existe
     */
    public static boolean eliminarPresupuesto(String id) {
        if (!presupuestosRegistrados.containsKey(id)) {
            return false;
        }
        
        presupuestosRegistrados.remove(id);
        return true;
    }
    
    /**
     * Busca un presupuesto por su ID
     * @return El presupuesto encontrado o null si no existe
     */
    public static Presupuesto buscarPresupuestoPorId(String id) {
        return presupuestosRegistrados.get(id);
    }
    
    /**
     * Obtiene todos los presupuestos registrados
     * @return Lista de presupuestos
     */
    public static List<Presupuesto> obtenerTodosLosPresupuestos() {
        return new ArrayList<>(presupuestosRegistrados.values());
    }
    
    /**
     * Agrega una categoría al presupuesto
     */
    public void agregarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        
        // Verificar si ya existe la categoría (por ID)
        boolean existeCategoria = categorias.stream()
                .anyMatch(cat -> cat.getIdCategoria().equals(categoria.getIdCategoria()));
        
        if (existeCategoria) {
            throw new IllegalArgumentException("Ya existe una categoría con el ID: " + categoria.getIdCategoria());
        }
        
        categorias.add(categoria);
    }
    
    /**
     * Elimina una categoría del presupuesto
     * @return true si se eliminó correctamente, false si no existe
     */
    public boolean eliminarCategoria(String idCategoria) {
        Optional<Categoria> categoriaOpt = categorias.stream()
                .filter(cat -> cat.getIdCategoria().equals(idCategoria))
                .findFirst();
        
        if (categoriaOpt.isPresent()) {
            categorias.remove(categoriaOpt.get());
            calcularGastoPorCategoria(); // Actualizar gasto total
            return true;
        }
        
        return false;
    }
    
    /**
     * Calcula el gasto total sumando los gastos de todas las categorías
     * y actualiza el estado del presupuesto
     */
    public void calcularGastoPorCategoria() {
        montoGastado = categorias.stream().mapToDouble(Categoria::getGastoActual).sum();
        estado.actualizarEstado(this);
    }
    
    /**
     * Consulta el estado actual del presupuesto
     * @return Información detallada sobre el estado del presupuesto
     */
    public String consultarEstadoPresupuesto() {
        calcularGastoPorCategoria(); // Asegurar que los datos estén actualizados
        
        String nombreEstado = estado.getClass().getSimpleName();
        double disponible = montoTotal - montoGastado;
        double porcentajeUsado = (montoGastado / montoTotal) * 100;
        
        return String.format(
                "Estado del presupuesto: %s\n" +
                "Monto total: $%.2f\n" +
                "Monto gastado: $%.2f\n" +
                "Disponible: $%.2f\n" +
                "Porcentaje utilizado: %.2f%%\n" +
                "Vigencia: %s al %s",
                nombreEstado, montoTotal, montoGastado, disponible, 
                porcentajeUsado, fechaInicio, fechaFin
        );
    }
    
    /**
     * Monitorea el gasto por categoría y genera un informe
     * @return Informe detallado del gasto por categoría
     */
    public String monitorearGastoPorCategoria() {
        if (categorias.isEmpty()) {
            return "No hay categorías en este presupuesto.";
        }
        
        StringBuilder informe = new StringBuilder("INFORME DE GASTOS POR CATEGORÍA\n\n");
        
        for (Categoria categoria : categorias) {
            double porcentajeUsado = (categoria.getGastoActual() / categoria.getPresupuestoAsignado()) * 100;
            double disponible = categoria.getPresupuestoAsignado() - categoria.getGastoActual();
            String estado = porcentajeUsado >= 100 ? "EXCEDIDO" : 
                           porcentajeUsado >= 80 ? "EN RIESGO" : "DENTRO DEL PRESUPUESTO";
            
            informe.append(String.format(
                    "Categoría: %s (%s)\n" +
                    "Presupuesto: $%.2f\n" +
                    "Gasto actual: $%.2f\n" +
                    "Disponible: $%.2f\n" +
                    "Porcentaje utilizado: %.2f%%\n" +
                    "Estado: %s\n\n",
                    categoria.getNombre(), categoria.getIdCategoria(),
                    categoria.getPresupuestoAsignado(), categoria.getGastoActual(),
                    disponible, porcentajeUsado, estado
            ));
        }
        
        // Agregar resumen del presupuesto total
        informe.append(String.format(
                "RESUMEN TOTAL\n" +
                "Presupuesto total: $%.2f\n" +
                "Gasto total: $%.2f\n" +
                "Disponible total: $%.2f\n" +
                "Porcentaje utilizado: %.2f%%\n",
                montoTotal, montoGastado, (montoTotal - montoGastado),
                (montoGastado / montoTotal) * 100
        ));
        
        return informe.toString();
    }
    
    /**
     * Identifica las categorías que están en riesgo de exceder su presupuesto
     * @return Lista de categorías en riesgo (>= 80% usado)
     */
    public List<Categoria> obtenerCategoriasEnRiesgo() {
        return categorias.stream()
                .filter(cat -> (cat.getGastoActual() / cat.getPresupuestoAsignado()) >= 0.8)
                .collect(Collectors.toList());
    }
    
    /**
     * Identifica las categorías que han excedido su presupuesto
     * @return Lista de categorías excedidas (>= 100% usado)
     */
    public List<Categoria> obtenerCategoriasExcedidas() {
        return categorias.stream()
                .filter(cat -> cat.getGastoActual() > cat.getPresupuestoAsignado())
                .collect(Collectors.toList());
    }
    
    /**
     * Compara este presupuesto con otro para verificar si son iguales
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Presupuesto that = (Presupuesto) o;
        return Objects.equals(idPresupuesto, that.idPresupuesto);
    }
    
    /**
     * Genera un hash code para este presupuesto
     */
    @Override
    public int hashCode() {
        return Objects.hash(idPresupuesto);
    }
    
    /**
     * Devuelve una representación en texto del presupuesto
     */
    @Override
    public String toString() {
        return String.format("Presupuesto %s: $%.2f (%s)", 
                nombre != null ? nombre : idPresupuesto, montoTotal, 
                estado.getClass().getSimpleName());
    }

    // Getters y setters
    public void setEstado(EstadoPresupuesto estado) {
        this.estado = estado;
    }

    public void setMontoGastado(double montoGastado) {
        this.montoGastado = montoGastado;
    }

    public String getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(String idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    public double getMontoGastado() {
        return montoGastado;
    }
    
    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }
    
    public EstadoPresupuesto getEstado() {
        return estado;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = new ArrayList<>(categorias);
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}