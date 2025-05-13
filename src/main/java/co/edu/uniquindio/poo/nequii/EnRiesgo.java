package co.edu.uniquindio.poo.nequii;

public class EnRiesgo implements EstadoPresupuesto {
    @Override
    public void actualizarEstado(Presupuesto presupuesto) {
        if (presupuesto.getMontoGastado() > presupuesto.getMontoTotal()) {
            presupuesto.setEstado(new Excedido());
        }

    }

}
