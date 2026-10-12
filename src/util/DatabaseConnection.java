package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    // Unica instancia (Singleton)
    private static DatabaseConnection instancia;

    private Connection conexion;

    private DatabaseConnection() throws SQLException {
        conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instancia == null || instancia.conexion == null || instancia.conexion.isClosed()) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    public Connection getConnection() {
        return conexion;
    }

    public static boolean probarConexion() {
        try {
            return getInstance().getConnection().isValid(2);
        } catch (SQLException e) {
            System.err.println("Error de conexion: " + e.getMessage());
            return false;
        }
    }
}
