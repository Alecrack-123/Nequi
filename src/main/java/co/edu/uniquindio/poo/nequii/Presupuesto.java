package co.edu.uniquindio.poo.nequii;

import java.util.ArrayList;
import java.util.List;

public class Presupuesto {
    private String idPresupuesto;
    private double montoTotal;
    private double montoGastado;
    private List<Categoria> categorias;
    private EstadoPresupuesto estado;

    public Presupuesto(String id, double montoTotal) {
        this.idPresupuesto = id;
        this.montoTotal = montoTotal;
        this.montoGastado = 0;
        this.categorias = new ArrayList<>();
        this.estado = new DentroPresupuesto(); // Estado inicial
    }

    public void agregarCategoria(Categoria categoria) {
        categorias.add(categoria);
    }

    public void calcularGastoPorCategoria() {
        montoGastado = categorias.stream().mapToDouble(Categoria::getGastoActual).sum();
        estado.actualizarEstado(this);
    }

    public void setEstado(EstadoPresupuesto estado) {
        this.estado = estado;
    }

    // Getters y setters...
}