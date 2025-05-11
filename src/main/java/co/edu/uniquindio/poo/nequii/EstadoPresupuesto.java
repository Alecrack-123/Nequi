package co.edu.uniquindio.poo.nequii;

public interface EstadoPresupuesto {
    void actualizarEstado(Presupuesto presupuesto);
}

public class DentroPresupuesto implements EstadoPresupuesto {
    @Override
    public void actualizarEstado(Presupuesto presupuesto) {
        if (presupuesto.getMontoGastado() > presupuesto.getMontoTotal()) {
            presupuesto.setEstado(new Excedido());
        } else if (presupuesto.getMontoGastado() > presupuesto.getMontoTotal() * 0.8) {
            presupuesto.setEstado(new EnRiesgo());
        }
    }
}

public class EnRiesgo implements EstadoPresupuesto {
    @Override
    public void actualizarEstado(Presupuesto presupuesto) {
        if (presupuesto.getMontoGastado() > presupuesto.getMontoTotal()) {
            presupuesto.setEstado(new Excedido());
        }
    }
}