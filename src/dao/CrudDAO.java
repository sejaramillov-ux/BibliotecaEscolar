package dao;

import java.sql.SQLException;
import java.util.List;


public interface CrudDAO<T> {

    boolean insertar(T objeto) throws SQLException;

    List<T> listar() throws SQLException;

    T buscarPorId(int id) throws SQLException;

    boolean actualizar(T objeto) throws SQLException;

    boolean eliminar(int id) throws SQLException;
}
