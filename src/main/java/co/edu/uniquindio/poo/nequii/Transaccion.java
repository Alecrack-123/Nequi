package co.edu.uniquindio.poo.nequii;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Transaccion {
    private String idTransaccion;
    private LocalDate fecha;
    private TipoTransaccion tipo;
    private double monto;
    private String descripcion;
    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;
    private Categoria categoria;
    private EstrategiaTransaccion estrategia;
    private static List<Transaccion> transacciones = new ArrayList<>();

    private static final Map<TipoTransaccion, EstrategiaTransaccion> estrategias = Map.of(
            TipoTransaccion.DEPOSITO, new EstrategiaDeposito(),
            TipoTransaccion.RETIRO, new EstrategiaRetiro(),
            TipoTransaccion.TRANSFERENCIA, new EstrategiaTransferencia()
    );

    public enum TipoTransaccion {
        DEPOSITO("Depósito"),
        RETIRO("Retiro"),
        TRANSFERENCIA("Transferencia");

        private final String descripcion;

        TipoTransaccion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Constructor para la clase Transaccion
     */
    public Transaccion(String idTransaccion, LocalDate fecha, TipoTransaccion tipo,
                       double monto, String descripcion, Cuenta cuentaOrigen,
                       Cuenta cuentaDestino, Categoria categoria) {
        validarParametros(idTransaccion, fecha, tipo, monto, descripcion,
                cuentaOrigen, cuentaDestino, categoria);

        this.idTransaccion = idTransaccion;
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.categoria = categoria;
        this.estrategia = obtenerEstrategiaPorTipo(tipo);
    }

    /**
     * Constructor simplificado para la clase Transaccion
     * @param tipo Tipo de transacción
     * @param monto Monto de la transacción
     * @param descripcion Descripción de la transacción
     */
    public Transaccion(TipoTransaccion tipo, double monto, String descripcion) {
        this.idTransaccion = "TRANS-" + System.currentTimeMillis();
        this.fecha = LocalDate.now();
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.estrategia = obtenerEstrategiaPorTipo(tipo);
    }

    /**
     * Constructor simplificado para la clase Transaccion desde una categoría con tipo definido
     * @param tipo Tipo de transacción (DEPOSITO, RETIRO, etc.)
     * @param monto Monto de la transacción
     * @param descripcion Descripción de la transacción
     * @param categoria Categoría de la transacción
     */
    public Transaccion(TipoTransaccion tipo, double monto, String descripcion, Categoria categoria) {
        this.idTransaccion = "TRANS-" + System.currentTimeMillis();
        this.fecha = LocalDate.now();
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.estrategia = obtenerEstrategiaPorTipo(tipo);
    }

    private EstrategiaTransaccion obtenerEstrategiaPorTipo(TipoTransaccion tipo) {
        EstrategiaTransaccion estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new IllegalArgumentException("Estrategia no definida para tipo: " + tipo);
        }
        return estrategia;
    }

    /**
     * Ejecuta la transacción según su tipo
     */
    public void ejecutarTransaccion() {
        if (estrategia == null) {
            throw new IllegalStateException("Estrategia no definida.");
        }

        estrategia.ejecutar(this);
        transacciones.add(this);
    }

    /**
     * Busca una transacción por su ID
     */
    public static Transaccion buscarTransaccion(String idTransaccion) {
        return transacciones.stream()
                .filter(t -> t.getIdTransaccion().equals(idTransaccion))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene las transacciones de una cuenta específica
     */
    public static List<Transaccion> obtenerTransaccionesPorCuenta(Cuenta cuenta) {
        return transacciones.stream()
                .filter(t -> t.getCuentaOrigen() == cuenta || t.getCuentaDestino() == cuenta)
                .toList();
    }

    private void validarParametros(String idTransaccion, LocalDate fecha,
                                   TipoTransaccion tipo, double monto,
                                   String descripcion, Cuenta cuentaOrigen,
                                   Cuenta cuentaDestino, Categoria categoria) {
        if (idTransaccion == null || idTransaccion.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de transacción no puede estar vacío");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de transacción no puede ser nulo");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }

        switch (tipo) {
            case DEPOSITO:
                if (cuentaDestino == null) {
                    throw new IllegalArgumentException("Se requiere cuenta destino para un depósito");
                }
                break;
            case RETIRO:
                if (cuentaOrigen == null) {
                    throw new IllegalArgumentException("Se requiere cuenta origen para un retiro");
                }
                break;
            case TRANSFERENCIA:
                if (cuentaOrigen == null || cuentaDestino == null) {
                    throw new IllegalArgumentException("Se requieren ambas cuentas para una transferencia");
                }
                if (cuentaOrigen == cuentaDestino) {
                    throw new IllegalArgumentException("Las cuentas origen y destino no pueden ser la misma");
                }
                break;
        }

        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
    }

    // Getters
    public String getIdTransaccion() {
        return idTransaccion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Cuenta getCuentaOrigen() {
        return cuentaOrigen;
    }

    public Cuenta getCuentaDestino() {
        return cuentaDestino;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría de la transacción
     * @param categoria Nueva categoría
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Establece la categoría de la transacción por nombre (si usas objeto Categoria,
     * considera eliminar o modificar este método)
     * @param categoriaNombre Nombre de la categoría
     */
    public void setCategoria(String categoriaNombre) {
        Categoria nuevaCategoria = new Categoria("CAT-" + System.currentTimeMillis(), categoriaNombre, 0);
        this.categoria = nuevaCategoria;
    }

    public static List<Transaccion> getTransacciones() {
        return new ArrayList<>(transacciones);
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "id='" + idTransaccion + '\'' +
                ", fecha=" + fecha +
                ", tipo=" + tipo.getDescripcion() +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                ", cuentaOrigen=" + (cuentaOrigen != null ? cuentaOrigen.getIdCuenta() : "N/A") +
                ", cuentaDestino=" + (cuentaDestino != null ? cuentaDestino.getIdCuenta() : "N/A") +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "N/A") +
                '}';
    }
}