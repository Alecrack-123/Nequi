package co.edu.uniquindio.poo.nequii;

public class EstrategiaTransferencia implements EstrategiaTransaccion {

    @Override
    public void ejecutar(Transaccion transaccion) {
        Cuenta origen = transaccion.getCuentaOrigen();
        Cuenta destino = transaccion.getCuentaDestino();

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("cuentas origen y destino requeridas para transferencia.");
        }
        if (origen == destino) {
            throw new IllegalArgumentException("La cuentas tienen que ser distintas.");
        }
        origen.retirar(transaccion.getMonto());
        destino.depositar(transaccion.getMonto());
    }
}
