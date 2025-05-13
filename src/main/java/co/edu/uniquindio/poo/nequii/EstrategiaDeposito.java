package co.edu.uniquindio.poo.nequii;

public class EstrategiaDeposito implements EstrategiaTransaccion {

    @Override
    public void ejecutar(Transaccion transaccion) {
        if (transaccion.getCuentaDestino() == null) {
            throw new IllegalArgumentException("Cuenta destino requerida para depósito.");
        }
        transaccion.getCuentaDestino().depositar(transaccion.getMonto());
    }
}

