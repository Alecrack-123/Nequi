package co.edu.uniquindio.poo.nequii;

public class Excedido implements EstadoPresupuesto {
    @Override
    public void actualizarEstado(Presupuesto presupuesto) {
        System.out.println("¡Presupuesto excedido!");
    }
}