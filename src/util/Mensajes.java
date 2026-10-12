package util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class Mensajes {

    public static void exito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operacion exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Revisa los datos", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void errorSQL(Component padre, String accion, SQLException e) {
        String detalle;
        if (e instanceof SQLIntegrityConstraintViolationException) {
            detalle = "El registro esta asociado a otros datos (por ejemplo, tiene prestamos)\n"
                    + "o hay un dato duplicado.";
        } else if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
            detalle = "No se pudo conectar con la base de datos.";
        } else {
            detalle = "Detalle: " + e.getMessage();
        }
        System.err.println("[ERROR SQL] " + accion + ": " + e.getMessage());
        error(padre, "No se pudo " + accion + ".\n\n" + detalle);
    }
}
