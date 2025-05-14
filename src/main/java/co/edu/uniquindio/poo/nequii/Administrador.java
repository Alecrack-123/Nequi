package co.edu.uniquindio.poo.nequii;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import java.util.*;

public class Administrador {
    private String idAdmin;
    private String nombre;
    private String correo;
    private String contrasena;
    private List<Usuario> usuarios;

    public Administrador(String idAdmin, String nombre, String correo, String contrasena) {
        this.idAdmin = idAdmin;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.usuarios = new ArrayList<>();
    }

    public String getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(String idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    // RF-009: Gestión de usuarios
    public void crearUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void actualizarUsuario(Usuario usuario) {
        usuarios.stream()
            .filter(u -> u.getIdUsuario().equals(usuario.getIdUsuario()))
            .findFirst()
            .ifPresent(u -> {
                int index = usuarios.indexOf(u);
                usuarios.set(index, usuario);
            });
    }

    public void eliminarUsuario(String idUsuario) {
        usuarios.removeIf(u -> u.getIdUsuario().equals(idUsuario));
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    // RF-010: Gestión de cuentas
    public void agregarCuenta(String idUsuario, CuentaBancaria cuenta) {
        encontrarUsuario(idUsuario).ifPresent(u -> u.agregarCuentaBancaria(cuenta));
    }

    public void eliminarCuenta(String idUsuario, String numeroCuenta) {
        encontrarUsuario(idUsuario).ifPresent(u -> u.eliminarCuentaBancaria(numeroCuenta));
    }

    // RF-011: Gestión de transacciones
    public List<Transaccion> listarTransacciones(String idUsuario) {
        Optional<Usuario> usuario = encontrarUsuario(idUsuario);
        return usuario.map(Usuario::consultarTransacciones).orElse(new ArrayList<>());
    }

    // RF-012: Estadísticas
    public Map<String, Double> obtenerGastosComunes() {
        Map<String, Double> gastosPorCategoria = new HashMap<>();
        usuarios.forEach(usuario -> 
            usuario.consultarTransacciones().stream()
                .filter(t -> t.getTipo() == TipoTransaccion.RETIRO)
                .forEach(t -> {
                    String categoria = t.getCategoria();
                    gastosPorCategoria.merge(categoria, t.getMonto(), Double::sum);
                })
        );
        return gastosPorCategoria;
    }

    public List<Usuario> obtenerUsuariosConMasTransacciones() {
        return usuarios.stream()
            .sorted((u1, u2) -> Integer.compare(
                u2.consultarTransacciones().size(),
                u1.consultarTransacciones().size()))
            .limit(10)
            .collect(Collectors.toList());
    }

    public double calcularSaldoPromedio() {
        return usuarios.stream()
            .mapToDouble(Usuario::consultarSaldo)
            .average()
            .orElse(0.0);
    }

    // RF-013: Gráficas estadísticas
    public void mostrarGraficaGastos() {
        // Implementar lógica para crear gráfica de gastos usando JavaFX Charts
        PieChart graficaGastos = new PieChart();
        obtenerGastosComunes().forEach((categoria, monto) -> 
            graficaGastos.getData().add(new PieChart.Data(categoria, monto))
        );
    }

    public void mostrarGraficaTransacciones() {
        // Implementar lógica para crear gráfica de transacciones usando JavaFX Charts
        BarChart<String, Number> graficaTransacciones = new BarChart<>(new CategoryAxis(), new NumberAxis());
        // Agregar datos de transacciones por usuario
    }

    private Optional<Usuario> encontrarUsuario(String idUsuario) {
        return usuarios.stream()
            .filter(u -> u.getIdUsuario().equals(idUsuario))
            .findFirst();
    }




}