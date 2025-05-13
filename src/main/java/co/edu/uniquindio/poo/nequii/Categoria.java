package co.edu.uniquindio.poo.nequii;

public class Categoria {
    private String idCategoria;
    private String nombre;
    private String descripcion;
    private double presupuestoAsignado;
    private double gastoActual;

    public Categoria(String id, String nombre, double presupuesto) {
        this.idCategoria = id;
        this.nombre = nombre;
        this.presupuestoAsignado = presupuesto;
    }

    public void actualizarGasto(double monto) {
        this.gastoActual += monto;
    }

    // Getters...

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
}

