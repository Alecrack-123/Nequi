package co.edu.uniquindio.poo.nequii;

import java.util.ArrayList;
import java.util.List;
import co.edu.uniquindio.poo.nequii.Transaccion.TipoTransaccion;

public class Usuario {
    private String idUsuario;
    private String nombreCompleto;
    private String correoElectronico;
    private String numeroTelefono;
    private String direccion;
    private String contrasena;
    private double saldoTotal;
    private List<Cuenta> cuentasBancarias;
    private List<Transaccion> transacciones;
    private List<Presupuesto> presupuestos;
    private TipoCuenta tipoCuenta;
    private List<String> historialTransacciones;

    // Constructor
    public Usuario(String nombre, String correo, String password, TipoCuenta tipoCuenta) {
        this.idUsuario = "USR-" + System.currentTimeMillis();
        this.nombreCompleto = nombre;
        this.correoElectronico = correo;
        this.contrasena = password;
        this.tipoCuenta = tipoCuenta;
        this.saldoTotal = 0.0;
        this.cuentasBancarias = new ArrayList<>();
        this.transacciones = new ArrayList<>();
        this.presupuestos = new ArrayList<>();
        this.historialTransacciones = new ArrayList<>();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public double getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(double saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public List<Cuenta> getCuentasBancarias() {
        return cuentasBancarias;
    }

    public void setCuentasBancarias(List<Cuenta> cuentasBancarias) {
        this.cuentasBancarias = cuentasBancarias;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    public List<Presupuesto> getPresupuestos() {
        return presupuestos;
    }

    public void setPresupuestos(List<Presupuesto> presupuestos) {
        this.presupuestos = presupuestos;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public List<String> getHistorialTransacciones() {
        return new ArrayList<>(historialTransacciones);
    }

    public String getNombre() {
        return this.nombreCompleto;
    }

    // RF-001: Registrarse y loguearse
    public boolean autenticar(String correo, String contrasena) {
        return this.correoElectronico.equals(correo) && this.contrasena.equals(contrasena);
    }

    // RF-002: Modificar perfil
    public void actualizarPerfil(String nombre, String correo, String telefono) {
        this.nombreCompleto = nombre;
        this.correoElectronico = correo;
        this.numeroTelefono = telefono;
    }

    // RF-003: Gestión de dinero
    public void agregarDinero(double monto, boolean esAdministrador) {
        if (!esAdministrador) {
            throw new IllegalStateException("Solo el administrador puede agregar dinero a las cuentas");
        }
        if (monto > 0) {
            saldoTotal += monto;
            historialTransacciones.add(String.format("Depósito: +$%.2f", monto));
            Transaccion trans = new Transaccion(this, TipoTransaccion.DEPOSITO, monto, "Ingreso de dinero");
            registrarTransaccion(trans);
            Transaccion.getTransacciones().add(trans);
        }
    }

    public boolean retirarDinero(double monto) {
        if (monto > 0 && saldoTotal >= monto) {
            saldoTotal -= monto;
            historialTransacciones.add(String.format("Retiro: -$%.2f", monto));
            Transaccion trans = new Transaccion(this, TipoTransaccion.RETIRO, monto, "Retiro de dinero");
            registrarTransaccion(trans);
            Transaccion.getTransacciones().add(trans);
            return true;
        }
        return false;
    }

    public boolean transferirDinero(Usuario destinatario, double monto) {
        if (monto > 0 && saldoTotal >= monto) {
            saldoTotal -= monto;
            destinatario.agregarDinero(monto, true);
            historialTransacciones.add(String.format("Transferencia a %s: -$%.2f", destinatario.getCorreoElectronico(), monto));
            Transaccion trans = new Transaccion(this, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia a " + destinatario.getNombreCompleto());
            registrarTransaccion(trans);
            Transaccion.getTransacciones().add(trans);
            return true;
        }
        return false;
    }
    public void setNombre(String nombre) {
        this.nombreCompleto = nombreCompleto;
    }

    public double consultarSaldo() {
        return saldoTotal;
    }

    // RF-004: Gestión de presupuestos
    public void crearPresupuesto(Presupuesto presupuesto) {
        this.presupuestos.add(presupuesto);
    }

    public void modificarPresupuesto(String idPresupuesto, double nuevoMonto) {
        presupuestos.stream()
                .filter(p -> p.getIdPresupuesto().equals(idPresupuesto))
                .findFirst()
                .ifPresent(p -> p.setMontoTotal(nuevoMonto));
    }

    public void eliminarPresupuesto(String idPresupuesto) {
        presupuestos.removeIf(p -> p.getIdPresupuesto().equals(idPresupuesto));
    }

    // RF-005: Gestión de transacciones
    private void registrarTransaccion(Transaccion transaccion) {
        this.transacciones.add(transaccion);
    }

    public void categorizarTransaccion(String idTransaccion, String categoria) {
        transacciones.stream()
                .filter(t -> t.getIdTransaccion().equals(idTransaccion))
                .findFirst()
                .ifPresent(t -> t.setCategoria(categoria));
    }

    // RF-006: Gestión de cuentas bancarias
    public void agregarCuentaBancaria(Cuenta cuenta) {
        this.cuentasBancarias.add(cuenta);
    }

    public void eliminarCuentaBancaria(String numeroCuenta) {
        cuentasBancarias.removeIf(c -> c.getNumeroCuenta().equals(numeroCuenta));
    }

    // RF-007 y RF-008: Consultas
    public List<Transaccion> consultarTransacciones() {
        return new ArrayList<>(transacciones);
    }

    public List<Presupuesto> consultarPresupuestos() {
        return new ArrayList<>(presupuestos);
    }
}

