package controller;

import dao.EstudianteDAO;
import model.Estudiante;
import util.Validador;

import java.sql.SQLException;
import java.util.List;

public class EstudianteController {

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public List<Estudiante> listar() throws SQLException {
        return estudianteDAO.listar();
    }

    public Estudiante buscarPorCorreoORut(String correo, String rut) throws SQLException {
        return estudianteDAO.buscarPorCorreoORut(correo, rut);
    }

    public void guardar(Estudiante e) throws SQLException {
        if (Validador.estaVacio(e.getNombre())) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (!Validador.esRutValido(e.getRut())) throw new IllegalArgumentException("RUT invalido. Usa el formato 12345678-9.");
        if (Validador.estaVacio(e.getCurso())) throw new IllegalArgumentException("El curso es obligatorio.");
        if (!Validador.esCorreoValido(e.getCorreo())) throw new IllegalArgumentException("El correo no es valido.");
        if (e.getId() == 0) {
            estudianteDAO.insertar(e);
        } else {
            estudianteDAO.actualizar(e);
        }
    }

    public void eliminar(int id) throws SQLException {
        estudianteDAO.eliminar(id);
    }
}
