package co.edu.uniquindio.poo.nequii;

import java.util.ArrayList;
import java.util.List;

public class Cuenta {
    private String idCuenta;
    private String banco;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private Usuario usuario;
    private double saldo;
    private static List<Cuenta> cuentas = new ArrayList<>();
    private final List<ObservadorCuenta> observadores = new ArrayList<>();

    //Constructor parcial
    public Cuenta(String numeroCuenta){
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * Constructor para la clase Cuenta
     * @param idCuenta ID único de la cuenta
     * @param banco Nombre del banco
     * @param numeroCuenta Número de cuenta bancaria
     * @param tipoCuenta Tipo de cuenta (AHORRO, CORRIENTE, INVERSION)
     * @param usuario Usuario propietario de la cuenta
     * @param saldo Saldo inicial de la cuenta
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Cuenta(String idCuenta, String banco, String numeroCuenta,
                  TipoCuenta tipoCuenta, Usuario usuario, double saldo) {
        validarParametros(idCuenta, banco, numeroCuenta, tipoCuenta, usuario, saldo);

        this.idCuenta = idCuenta;
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.usuario = usuario;
        this.saldo = saldo;
    }



    /**
     * Valida los parámetros del constructor
     */
    private void validarParametros(String idCuenta, String banco, String numeroCuenta,
                                   TipoCuenta tipoCuenta, Usuario usuario, double saldo) {
        if (idCuenta == null || idCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la cuenta no puede estar vacío");
        }
        if (banco == null || banco.trim().isEmpty()) {
            throw new IllegalArgumentException("El banco no puede estar vacío");
        }
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío");
        }
        if (tipoCuenta == null) {
            throw new IllegalArgumentException("El tipo de cuenta no puede ser nulo");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (saldo < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
    }

    /**
     * Realiza un depósito en la cuenta
     * @param monto Monto a depositar
     * @throws IllegalArgumentException si el monto es inválido
     */
    public void depositar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor que cero");
        }
        this.saldo += monto;
        notificarObservadores();
    }

    /**
     * Realiza un retiro de la cuenta
     * @param monto Monto a retirar
     * @throws IllegalArgumentException si el monto es inválido o no hay saldo suficiente
     */
    public void retirar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor que cero");
        }
        if (monto > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        this.saldo -= monto;
        notificarObservadores();
    }

    /**
     * Guarda una nueva cuenta en la lista de cuentas
     * @param cuenta Cuenta a guardar
     * @throws IllegalArgumentException si la cuenta ya existe
     */
    public static void guardarCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        if (existeCuenta(cuenta.getIdCuenta())) {
            throw new IllegalArgumentException("Ya existe una cuenta con el ID: " + cuenta.getIdCuenta());
        }
        cuentas.add(cuenta);
    }

    /**
     * Elimina una cuenta de la lista de cuentas
     * @param cuenta Cuenta a eliminar
     * @throws IllegalArgumentException si la cuenta no existe
     */
    public static void eliminarCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        if (!cuentas.remove(cuenta)) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
    }

    /**
     * Verifica si existe una cuenta con el ID especificado
     * @param idCuenta ID de la cuenta a buscar
     * @return true si existe la cuenta, false en caso contrario
     */
    public static boolean existeCuenta(String idCuenta) {
        return cuentas.stream()
                .anyMatch(c -> c.getIdCuenta().equals(idCuenta));
    }

    /**
     * Busca una cuenta por su ID
     * @param idCuenta ID de la cuenta a buscar
     * @return la cuenta encontrada o null si no existe
     */
    public static Cuenta buscarCuenta(String idCuenta) {
        return cuentas.stream()
                .filter(c -> c.getIdCuenta().equals(idCuenta))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene las cuentas de un usuario específico
     * @param usuario Usuario del que se quieren obtener las cuentas
     * @return lista de cuentas del usuario
     */
    public static List<Cuenta> obtenerCuentasUsuario(Usuario usuario) {
        return cuentas.stream()
                .filter(c -> c.getUsuario().equals(usuario))
                .toList();
    }

    /**
     * Obtiene todas las cuentas registradas
     * @return lista de todas las cuentas
     */
    public static List<Cuenta> getCuentas() {
        return new ArrayList<>(cuentas);
    }

    // Getters y setters
    public String getIdCuenta() {
        return idCuenta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        if (banco == null || banco.trim().isEmpty()) {
            throw new IllegalArgumentException("El banco no puede estar vacío");
        }
        this.banco = banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío");
        }
        this.numeroCuenta = numeroCuenta;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        if (tipoCuenta == null) {
            throw new IllegalArgumentException("El tipo de cuenta no puede ser nulo");
        }
        this.tipoCuenta = tipoCuenta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        this.usuario = usuario;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public static void setCuentas(List<Cuenta> cuentas) {
        Cuenta.cuentas = cuentas;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "id='" + idCuenta + '\'' +
                ", banco='" + banco + '\'' +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", tipo=" + tipoCuenta +
                ", usuario=" + usuario.getNombreCompleto() +
                ", saldo=" + saldo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return idCuenta.equals(cuenta.idCuenta);
    }

    @Override
    public int hashCode() {
        return idCuenta.hashCode();
    }

    public void agregarObservador(ObservadorCuenta observador) {
        observadores.add(observador);
    }

    public void eliminarObservador(ObservadorCuenta observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores() {
        for (ObservadorCuenta o : observadores) {
            o.actualizar(this, this.saldo);
        }
    }
}
