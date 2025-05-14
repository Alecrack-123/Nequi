package co.edu.uniquindio.poo.nequii;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Categoria {
    private String idCategoria;
    private String nombre;
    private String descripcion;
    private double presupuestoAsignado;
    private double gastoActual;
    private List<Transaccion> transacciones;

    public Categoria(String id, String nombre, double presupuesto) {
        this.idCategoria = id;
        this.nombre = nombre;
        this.presupuestoAsignado = presupuesto;
        this.gastoActual = 0;
        this.transacciones = new ArrayList<>();
    }

    /**
     * Actualiza el gasto actual de la categoría sumando el monto proporcionado
     * @param monto Monto a sumar al gasto actual
     */
    public void actualizarGasto(double monto) {
        this.gastoActual += monto;
    }
    
    /**
     * Crea una nueva categoría con los parámetros especificados
     * @param id Identificador único de la categoría
     * @param nombre Nombre de la categoría
     * @param presupuesto Presupuesto asignado a la categoría
     * @param descripcion Descripción de la categoría (opcional)
     * @return La nueva categoría creada
     */
    public static Categoria crearCategoria(String id, String nombre, double presupuesto, String descripcion) {
        Categoria nuevaCategoria = new Categoria(id, nombre, presupuesto);
        nuevaCategoria.setDescripcion(descripcion);
        return nuevaCategoria;
    }
    
    /**
     * Asigna una transacción a esta categoría
     * @param monto Monto de la transacción
     * @param descripcion Descripción de la transacción
     * @return La transacción creada
     */
    public Transaccion asignarTransaccion(double monto, String descripcion) {
        Transaccion nuevaTransaccion = new Transaccion(monto, descripcion, this);
        transacciones.add(nuevaTransaccion);
        actualizarGasto(monto);
        return nuevaTransaccion;
    }
    
    /**
     * Actualiza los datos de la categoría
     * @param nombre Nuevo nombre (null si no se desea actualizar)
     * @param descripcion Nueva descripción (null si no se desea actualizar)
     * @param presupuesto Nuevo presupuesto (0 si no se desea actualizar)
     */
    public void actualizarCategoria(String nombre, String descripcion, double presupuesto) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            this.nombre = nombre;
        }
        
        if (descripcion != null) {
            this.descripcion = descripcion;
        }
        
        if (presupuesto > 0) {
            this.presupuestoAsignado = presupuesto;
        }
    }
    
    /**
     * Devuelve una representación en texto de la categoría
     * @return Información detallada de la categoría
     */
    public String obtenerInformacionCategoria() {
        return String.format(
                "ID: %s\nNombre: %s\nDescripción: %s\nPresupuesto asignado: %.2f\nGasto actual: %.2f\nDisponible: %.2f",
                idCategoria, nombre, descripcion != null ? descripcion : "N/A",
                presupuestoAsignado, gastoActual, presupuestoAsignado - gastoActual);
    }
    
    /**
     * Obtiene todas las transacciones asociadas a esta categoría
     * @return Lista de transacciones
     */
    public List<Transaccion> listarTransacciones() {
        return new ArrayList<>(transacciones);
    }
    
    /**
     * Compara esta categoría con otra para verificar si son iguales
     * @param o Objeto a comparar
     * @return true si las categorías son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(idCategoria, categoria.idCategoria);
    }
    
    /**
     * Genera un hash code para esta categoría
     * @return Hash code basado en el ID de la categoría
     */
    @Override
    public int hashCode() {
        return Objects.hash(idCategoria);
    }
    
    /**
     * Devuelve una representación en texto de la categoría
     * @return Nombre de la categoría
     */
    @Override
    public String toString() {
        return nombre;
    }

    // Getters y setters existentes
    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
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

    public double getPresupuestoAsignado() {
        return presupuestoAsignado;
    }

    public void setPresupuestoAsignado(double presupuestoAsignado) {
        this.presupuestoAsignado = presupuestoAsignado;
    }

    public double getGastoActual() {
        return gastoActual;
    }

    public void setGastoActual(double gastoActual) {
        this.gastoActual = gastoActual;
    }
    
    public List<Transaccion> getTransacciones() {
        return transacciones;
    }
    
    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }
    

}