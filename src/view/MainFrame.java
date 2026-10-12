package view;

import model.Usuario;
import util.Mensajes;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(Usuario usuario) {
        super("Biblioteca Escolar - " + usuario.getNombre() + " (" + usuario.getTipo() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        boolean esBibliotecario = usuario.esBibliotecario();

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Libros", new LibroPanel(esBibliotecario));
        if (esBibliotecario) {
            pestanas.addTab("Estudiantes", new EstudiantePanel());
        }
        PrestamoPanel prestamoPanel = new PrestamoPanel(usuario);
        pestanas.addTab("Prestamos y devoluciones", prestamoPanel);
        if (esBibliotecario) {
            pestanas.addTab("Reportes", new ReportePanel());
        }

        pestanas.addChangeListener(e -> {
            Component actual = pestanas.getSelectedComponent();
            if (actual instanceof Refrescable) {
                ((Refrescable) actual).refrescar();
            }
        });

        JMenuBar menu = new JMenuBar();
        JMenu menuSesion = new JMenu("Sesion");
        JMenuItem itemCerrar = new JMenuItem("Cerrar sesion");
        itemCerrar.addActionListener(e -> {
            if (Mensajes.confirmar(this, "¿Deseas cerrar sesion?")) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        menuSesion.add(itemCerrar);
        menu.add(menuSesion);
        setJMenuBar(menu);

        add(pestanas);
    }
}
