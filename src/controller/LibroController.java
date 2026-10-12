package controller;

import dao.CategoriaDAO;
import dao.LibroDAO;
import model.Categoria;
import model.Libro;
import util.Validador;

import java.sql.SQLException;
import java.util.List;


public class LibroController {

    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Libro> listar() throws SQLException {
        return libroDAO.listar();
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listar();
    }

    public void guardar(Libro libro) throws SQLException {
        validar(libro);
        if (libro.getId() == 0) {
            libroDAO.insertar(libro);
        } else {
            libroDAO.actualizar(libro);
        }
    }

    public void eliminar(int id) throws SQLException {
        libroDAO.eliminar(id);
    }

    private void validar(Libro l) {
        if (Validador.estaVacio(l.getTitulo())) throw new IllegalArgumentException("El titulo es obligatorio.");
        if (Validador.estaVacio(l.getAutor())) throw new IllegalArgumentException("El autor es obligatorio.");
        if (Validador.estaVacio(l.getIsbn())) throw new IllegalArgumentException("El ISBN es obligatorio.");
        if (Validador.estaVacio(l.getEditorial())) throw new IllegalArgumentException("La editorial es obligatoria.");
        if (l.getStock() < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        if (l.getIdCategoria() <= 0) throw new IllegalArgumentException("Debes seleccionar una categoria.");
    }
}
