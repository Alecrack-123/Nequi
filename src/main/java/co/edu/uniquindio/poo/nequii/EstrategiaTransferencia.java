package co.edu.uniquindio.poo.nequii;

public class EstrategiaTransferencia implements EstrategiaTransaccion {

    @Override
    public void ejecutar(Transaccion transaccion) {
        Cuenta origen = transaccion.getCuentaOrigen();
        Cuenta destino = transaccion.getCuentaDestino();

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Cuentas origen y destino requeridas para transferencia.");
        }
        if (origen == destino) {
            throw new IllegalArgumentException("Las cuentas no pueden ser la misma.");
        }
        origen.retirar(transaccion.getMonto());
        destino.depositar(transaccion.getMonto());
    }
}
