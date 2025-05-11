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
}

