package controller;

import dao.PrestamoDAO;
import model.Prestamo;
import service.PrestamoService;
import service.PrestamoTask;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;


public class PrestamoController {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public void registrarPrestamoEnSegundoPlano(int idEstudiante, int idLibro,
                                                Consumer<Prestamo> alTerminar, Consumer<String> alFallar) {
        Thread hilo = new Thread(new PrestamoTask(idEstudiante, idLibro, alTerminar, alFallar), "Hilo-Prestamo");
        hilo.start();
    }

    public boolean registrarDevolucion(int idPrestamo) throws SQLException {
        return PrestamoService.registrarDevolucion(idPrestamo);
    }

    public List<Prestamo> listar() throws SQLException {
        return prestamoDAO.listar();
    }

    public List<Prestamo> historialEstudiante(int idEstudiante) throws SQLException {
        return prestamoDAO.listarPorEstudiante(idEstudiante);
    }

    public List<Prestamo> librosEnPrestamo() throws SQLException {
        return prestamoDAO.listarNoDevueltos();
    }

    public List<Object[]> librosMasPrestados() throws SQLException {
        return prestamoDAO.librosMasPrestados();
    }
}
