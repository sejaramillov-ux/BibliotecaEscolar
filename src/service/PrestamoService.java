package service;

import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import model.Prestamo;

public class PrestamoService {

    public static synchronized Prestamo registrarPrestamo(int idEstudiante, int idLibro) throws SQLException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        Prestamo prestamo = new Prestamo(idEstudiante, idLibro);
        try {
            con.setAutoCommit(false);

            int stock;
            try (PreparedStatement ps = con.prepareStatement("SELECT stock FROM libros WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, idLibro);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("El libro seleccionado no existe.");
                    }
                    stock = rs.getInt("stock");
                }
            }
            if (stock <= 0) {
                throw new SQLException("No hay stock disponible de este libro.");
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) "
                    + "VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, idEstudiante);
                ps.setInt(2, idLibro);
                ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                ps.setBoolean(5, false);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE libros SET stock = stock - 1 WHERE id = ?")) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            con.commit();
            return prestamo;
        } catch (SQLException e) {
            con.rollback(); // si algo falla, se deshace todo
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static synchronized boolean registrarDevolucion(int idPrestamo) throws SQLException {
        Connection con = DatabaseConnection.getInstance().getConnection();
        try {
            con.setAutoCommit(false);

            int idLibro;
            boolean devuelto;
            LocalDate vencimiento;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_libro, devuelto, fecha_devolucion FROM prestamos WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, idPrestamo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("El prestamo no existe.");
                    }
                    idLibro = rs.getInt("id_libro");
                    devuelto = rs.getBoolean("devuelto");
                    Date fd = rs.getDate("fecha_devolucion");
                    vencimiento = fd != null ? fd.toLocalDate() : LocalDate.now();
                }
            }
            if (devuelto) {
                throw new SQLException("Este prestamo ya fue devuelto.");
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE prestamos SET devuelto = 1 WHERE id = ?")) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("UPDATE libros SET stock = stock + 1 WHERE id = ?")) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            con.commit();
            return vencimiento.isBefore(LocalDate.now());
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}
