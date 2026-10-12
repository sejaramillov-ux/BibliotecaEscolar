package controller;

import dao.UsuarioDAO;
import model.Usuario;
import util.Validador;

import java.sql.SQLException;


public class AuthController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario iniciarSesion(String correoORut, String clave) throws SQLException {
        if (Validador.estaVacio(correoORut) || Validador.estaVacio(clave)) {
            throw new IllegalArgumentException("Ingresa tu correo (o RUT) y tu contraseña.");
        }
        Usuario usuario = usuarioDAO.autenticar(correoORut.trim(), clave);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        }
        return usuario;
    }
}
