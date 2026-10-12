package app;

import util.DatabaseConnection;
import view.LoginFrame;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el Look and Feel: " + e.getMessage());
        }

        if (!DatabaseConnection.probarConexion()) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos 'biblioteca'.\n\n"
                            + "Revisa que MySQL este encendido, que se hayan ejecutado los scripts\n"
                            + "y que el usuario y la clave en DatabaseConnection sean correctos.",
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
