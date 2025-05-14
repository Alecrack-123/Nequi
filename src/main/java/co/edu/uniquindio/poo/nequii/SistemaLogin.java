package co.edu.uniquindio.poo.nequii;

import java.util.ArrayList;
import java.util.List;

public class SistemaLogin {
    private List<Usuario> usuariosRegistrados;

    public SistemaLogin() {
        this.usuariosRegistrados = new ArrayList<>();
    }

    /**
     * Registra un nuevo usuario en el sistema
     */
    public boolean registrarUsuario(String idUsuario, String nombreCompleto, 
                                  String correoElectronico, String numeroTelefono, 
                                  String direccion, String contrasena) {
        // Verificar si ya existe un usuario con el mismo correo
        if (usuariosRegistrados.stream()
                .anyMatch(u -> u.getCorreoElectronico().equals(correoElectronico))) {
            return false;
        }

        Usuario nuevoUsuario = new Usuario(idUsuario, nombreCompleto, correoElectronico,
                                         numeroTelefono, direccion, contrasena);
        usuariosRegistrados.add(nuevoUsuario);
        return true;
    }

    /**
     * Realiza el login de un usuario
     * @return El usuario si las credenciales son correctas, null en caso contrario
     */
    public Usuario iniciarSesion(String correoElectronico, String contrasena) {
        return usuariosRegistrados.stream()
                .filter(u -> u.autenticar(correoElectronico, contrasena))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica si un correo electrónico ya está registrado
     */
    public boolean existeCorreoElectronico(String correoElectronico) {
        return usuariosRegistrados.stream()
                .anyMatch(u -> u.getCorreoElectronico().equals(correoElectronico));
    }

    /**
     * Obtiene la lista de usuarios registrados
     */
    public List<Usuario> getUsuariosRegistrados() {
        return new ArrayList<>(usuariosRegistrados);
    }
}