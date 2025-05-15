package co.edu.uniquindio.poo.nequii;

public class EstrategiaRetiro implements EstrategiaTransaccion {

    @Override
    public void ejecutar(Transaccion transaccion) {
        if (transaccion.getCuentaOrigen() == null) {
            throw new IllegalArgumentException("Cuenta origen requerida para retiro.");
        }
        transaccion.getCuentaOrigen().retirar(transaccion.getMonto());
    }
}