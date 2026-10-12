package dao;

import model.Prestamo;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PrestamoDAO implements CrudDAO<Prestamo> {

    private static final String SELECT_BASE =
            "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, "
            + "e.nombre AS estudiante, l.titulo AS libro "
            + "FROM prestamos p "
            + "LEFT JOIN estudiantes e ON p.id_estudiante = e.id "
            + "LEFT JOIN libros l ON p.id_libro = l.id ";

    private Connection con() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean insertar(Prestamo p) throws SQLException {
        String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, p.getIdEstudiante());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, Date.valueOf(p.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(p.getFechaDevolucion()));
            ps.setBoolean(5, p.isDevuelto());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Prestamo> listar() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY p.fecha_prestamo DESC, p.id DESC", null);
    }

    public List<Prestamo> listarPorEstudiante(int idEstudiante) throws SQLException {
        return consultar(SELECT_BASE + "WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC", idEstudiante);
    }

    public List<Prestamo> listarNoDevueltos() throws SQLException {
        return consultar(SELECT_BASE + "WHERE p.devuelto = 0 ORDER BY p.fecha_devolucion", null);
    }

    public List<Object[]> librosMasPrestados() throws SQLException {
        String sql = "SELECT l.titulo, COUNT(p.id) AS total FROM prestamos p "
                + "JOIN libros l ON p.id_libro = l.id GROUP BY l.id, l.titulo ORDER BY total DESC";
        List<Object[]> filas = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getString("titulo"), rs.getInt("total")});
            }
        }
        return filas;
    }

    @Override
    public Prestamo buscarPorId(int id) throws SQLException {
        List<Prestamo> lista = consultar(SELECT_BASE + "WHERE p.id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public boolean actualizar(Prestamo p) throws SQLException {
        String sql = "UPDATE prestamos SET id_estudiante = ?, id_libro = ?, fecha_prestamo = ?, "
                + "fecha_devolucion = ?, devuelto = ? WHERE id = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, p.getIdEstudiante());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, Date.valueOf(p.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(p.getFechaDevolucion()));
            ps.setBoolean(5, p.isDevuelto());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM prestamos WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private List<Prestamo> consultar(String sql, Integer parametro) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("id"));
                    p.setIdEstudiante(rs.getInt("id_estudiante"));
                    p.setIdLibro(rs.getInt("id_libro"));
                    Date fp = rs.getDate("fecha_prestamo");
                    if (fp != null) p.setFechaPrestamo(fp.toLocalDate());
                    Date fd = rs.getDate("fecha_devolucion");
                    if (fd != null) p.setFechaDevolucion(fd.toLocalDate());
                    p.setDevuelto(rs.getBoolean("devuelto"));
                    p.setNombreEstudiante(rs.getString("estudiante"));
                    p.setTituloLibro(rs.getString("libro"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}
