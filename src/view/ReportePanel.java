package view;

import controller.EstudianteController;
import controller.PrestamoController;
import model.Estudiante;
import model.Prestamo;
import util.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ReportePanel extends JPanel implements Refrescable {

    private final PrestamoController prestamoController = new PrestamoController();
    private final EstudianteController estudianteController = new EstudianteController();

    private final JComboBox<Estudiante> cboEstudiante = new JComboBox<>();
    private final JTable tabla = new JTable();
    private final JLabel lblTitulo = new JLabel("Selecciona un reporte");

    public ReportePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel opciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnMasPrestados = new JButton("Libros mas prestados");
        JButton btnEnPrestamo = new JButton("Libros actualmente en prestamo");
        JButton btnHistorial = new JButton("Historial del estudiante:");
        btnMasPrestados.addActionListener(e -> librosMasPrestados());
        btnEnPrestamo.addActionListener(e -> librosEnPrestamo());
        btnHistorial.addActionListener(e -> historial());
        opciones.add(btnMasPrestados);
        opciones.add(btnEnPrestamo);
        opciones.add(btnHistorial);
        opciones.add(cboEstudiante);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(opciones, BorderLayout.NORTH);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));
        arriba.add(lblTitulo, BorderLayout.SOUTH);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        refrescar();
    }

    @Override
    public void refrescar() {
        try {
            cboEstudiante.removeAllItems();
            for (Estudiante e : estudianteController.listar()) cboEstudiante.addItem(e);
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "cargar los estudiantes", ex);
        }
    }

    private void librosMasPrestados() {
        try {
            TablaSoloLectura modelo = new TablaSoloLectura("Libro", "Veces prestado");
            for (Object[] fila : prestamoController.librosMasPrestados()) modelo.addRow(fila);
            tabla.setModel(modelo);
            lblTitulo.setText("Libros mas prestados");
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "generar el reporte", ex);
        }
    }

    private void librosEnPrestamo() {
        try {
            mostrarPrestamos(prestamoController.librosEnPrestamo());
            lblTitulo.setText("Libros actualmente en prestamo");
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "generar el reporte", ex);
        }
    }

    private void historial() {
        Estudiante est = (Estudiante) cboEstudiante.getSelectedItem();
        if (est == null) {
            Mensajes.advertencia(this, "Selecciona un estudiante.");
            return;
        }
        try {
            mostrarPrestamos(prestamoController.historialEstudiante(est.getId()));
            lblTitulo.setText("Historial de prestamos de " + est.getNombre());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "generar el reporte", ex);
        }
    }

    private void mostrarPrestamos(java.util.List<Prestamo> lista) {
        TablaSoloLectura modelo = new TablaSoloLectura("ID", "Estudiante", "Libro", "Fecha prestamo", "Vence", "Estado");
        for (Prestamo p : lista) {
            modelo.addRow(new Object[]{p.getId(), p.getNombreEstudiante(), p.getTituloLibro(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(), p.getEstado()});
        }
        tabla.setModel(modelo);
    }
}
