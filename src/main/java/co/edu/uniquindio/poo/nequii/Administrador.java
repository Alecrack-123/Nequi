package co.edu.uniquindio.poo.nequii;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;

import java.util.*;
import java.util.stream.Collectors;

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

    // Getters y setters
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
        return new ArrayList<>(usuarios);
    }

    public void setUsuarios(List<Usuario> usuarios) {
        if (usuarios != null) {
            this.usuarios = new ArrayList<>(usuarios);
        }
    }

    // RF-009: Gestión de usuarios
    public void crearUsuario(Usuario usuario) {
        if (usuario != null && usuarios.stream().noneMatch(u -> u.getIdUsuario().equals(usuario.getIdUsuario()))) {
            usuarios.add(usuario);
        }
    }

    public void actualizarUsuario(Usuario usuario) {
        if (usuario != null) {
            usuarios.stream()
                    .filter(u -> u.getIdUsuario().equals(usuario.getIdUsuario()))
                    .findFirst()
                    .ifPresent(u -> {
                        int index = usuarios.indexOf(u);
                        usuarios.set(index, usuario);
                    });
        }
    }

    public void eliminarUsuario(String idUsuario) {
        usuarios.removeIf(u -> u.getIdUsuario().equals(idUsuario));
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    // RF-010: Gestión de cuentas
    public void agregarCuenta(String idUsuario, Cuenta cuenta) {
        encontrarUsuario(idUsuario).ifPresent(u -> {
            if (cuenta != null) {
                u.agregarCuentaBancaria(cuenta);
            }
        });
    }

    public void eliminarCuenta(String idUsuario, String numeroCuenta) {
        encontrarUsuario(idUsuario).ifPresent(u -> {
            if (numeroCuenta != null && !numeroCuenta.trim().isEmpty()) {
                u.eliminarCuentaBancaria(numeroCuenta);
            }
        });
    }

    // RF-011: Gestión de transacciones
    public List<Transaccion> listarTransacciones(String idUsuario) {
        return encontrarUsuario(idUsuario)
                .map(Usuario::consultarTransacciones)
                .orElseGet(ArrayList::new);
    }

    // RF-012: Estadísticas
    public Map<String, Double> obtenerGastosComunes() {
        Map<String, Double> gastosPorCategoria = new HashMap<>();
        for (Usuario usuario : usuarios) {
            for (Transaccion transaccion : usuario.consultarTransacciones()) {
                if (transaccion.getTipo() == Transaccion.TipoTransaccion.RETIRO) {
                    Categoria categoriaObj = transaccion.getCategoria();
                    String categoria = categoriaObj != null ? categoriaObj.getNombre() : null;
                    if (categoria != null) {
                        double montoActual = gastosPorCategoria.getOrDefault(categoria, 0.0);
                        gastosPorCategoria.put(categoria, montoActual + transaccion.getMonto());
                    }
                }
            }
        }
        return gastosPorCategoria;
    }

    public List<Usuario> obtenerUsuariosConMasTransacciones() {
        return usuarios.stream()
                .sorted(Comparator.comparingInt((Usuario u) -> u.consultarTransacciones().size()).reversed())
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
    public PieChart crearGraficaGastos() {
        PieChart graficaGastos = new PieChart();
        obtenerGastosComunes().forEach((categoria, monto) ->
                graficaGastos.getData().add(new PieChart.Data(categoria, monto))
        );
        return graficaGastos;
    }

    public BarChart<String, Number> crearGraficaTransacciones() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> graficaTransacciones = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Usuario");
        yAxis.setLabel("Cantidad Transacciones");

        usuarios.forEach(usuario -> {
            int cantidad = usuario.consultarTransacciones().size();
            var data = new javafx.scene.chart.XYChart.Series<String, Number>();
            data.setName(usuario.getNombreCompleto());
            data.getData().add(new javafx.scene.chart.XYChart.Data<>(usuario.getNombreCompleto(), cantidad));
            graficaTransacciones.getData().add(data);
        });

        return graficaTransacciones;
    }

    // Método auxiliar para encontrar un usuario por su ID
    private Optional<Usuario> encontrarUsuario(String idUsuario) {
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarios.stream()
                .filter(u -> u.getIdUsuario().equals(idUsuario))
                .findFirst();
    }
}