package view;

import controller.EstudianteController;
import controller.LibroController;
import controller.PrestamoController;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.Usuario;
import util.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoPanel extends JPanel implements Refrescable {

    private final PrestamoController prestamoController = new PrestamoController();
    private final LibroController libroController = new LibroController();
    private final EstudianteController estudianteController = new EstudianteController();
    private final Usuario usuario;

    private final JComboBox<Estudiante> cboEstudiante = new JComboBox<>();
    private final JComboBox<Libro> cboLibro = new JComboBox<>();
    private final JButton btnPrestar = new JButton("Registrar prestamo");
    private final JLabel lblEstado = new JLabel(" ");
    private final JProgressBar barra = new JProgressBar();

    private final TablaSoloLectura modelo = new TablaSoloLectura(
            "ID", "Estudiante", "Libro", "Fecha prestamo", "Vence", "Estado");
    private final JTable tabla = new JTable(modelo);
    private List<Prestamo> prestamos = new ArrayList<>();

    private Estudiante estudianteSesion;

    public PrestamoPanel(Usuario usuario) {
        this.usuario = usuario;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (!usuario.esBibliotecario()) {
            try {
                estudianteSesion = estudianteController.buscarPorCorreoORut(usuario.getCorreo(), usuario.getRut());
            } catch (SQLException ex) {
                Mensajes.errorSQL(this, "buscar tus datos de estudiante", ex);
            }
        }

        add(crearFormulario(), BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(usuario.esBibliotecario()
                ? "Todos los prestamos" : "Mi historial de prestamos"));
        add(scroll, BorderLayout.CENTER);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDevolver = new JButton("Registrar devolucion del prestamo seleccionado");
        btnDevolver.addActionListener(e -> devolver());
        abajo.add(btnDevolver);
        add(abajo, BorderLayout.SOUTH);

        refrescar();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo prestamo (plazo: " + Prestamo.DIAS_PLAZO + " dias)"));

        JPanel campos = new JPanel(new GridLayout(2, 2, 6, 6));
        campos.add(new JLabel("Estudiante:"));
        campos.add(cboEstudiante);
        campos.add(new JLabel("Libro:"));
        campos.add(cboLibro);
        cboEstudiante.setEnabled(usuario.esBibliotecario());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPrestar.addActionListener(e -> prestar());
        botones.add(btnPrestar);
        if (usuario.esBibliotecario()) {
            JButton btnSimular = new JButton("Simular 3 prestamos simultaneos");
            btnSimular.setToolTipText("Prueba de concurrencia: 3 hilos piden el mismo libro al mismo tiempo");
            btnSimular.addActionListener(e -> simularConcurrencia());
            botones.add(btnSimular);
        }
        barra.setIndeterminate(true);
        barra.setVisible(false);
        botones.add(barra);
        botones.add(lblEstado);

        panel.add(campos, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    @Override
    public void refrescar() {
        try {
            cboEstudiante.removeAllItems();
            if (usuario.esBibliotecario()) {
                for (Estudiante e : estudianteController.listar()) cboEstudiante.addItem(e);
                cboEstudiante.setSelectedIndex(-1);
            } else if (estudianteSesion != null) {
                cboEstudiante.addItem(estudianteSesion);
            }

            cboLibro.removeAllItems();
            for (Libro l : libroController.listar()) {
                if (l.hayStock()) cboLibro.addItem(l); // solo libros disponibles
            }
            cboLibro.setSelectedIndex(-1);

            prestamos = usuario.esBibliotecario()
                    ? prestamoController.listar()
                    : (estudianteSesion == null ? new ArrayList<>()
                       : prestamoController.historialEstudiante(estudianteSesion.getId()));
            modelo.setRowCount(0);
            for (Prestamo p : prestamos) {
                modelo.addRow(new Object[]{p.getId(), p.getNombreEstudiante(), p.getTituloLibro(),
                        p.getFechaPrestamo(), p.getFechaDevolucion(), p.getEstado()});
            }
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "cargar los prestamos", ex);
        }
    }

    private void prestar() {
        Estudiante est = (Estudiante) cboEstudiante.getSelectedItem();
        Libro libro = (Libro) cboLibro.getSelectedItem();
        if (est == null) {
            Mensajes.advertencia(this, "Selecciona un estudiante.");
            return;
        }
        if (libro == null) {
            Mensajes.advertencia(this, "Selecciona un libro con stock disponible.");
            return;
        }

        btnPrestar.setEnabled(false);
        barra.setVisible(true);
        lblEstado.setText("Registrando prestamo en segundo plano...");

        prestamoController.registrarPrestamoEnSegundoPlano(est.getId(), libro.getId(),
                prestamo -> {
                    terminarProceso("Prestamo registrado.");
                    Mensajes.exito(this, "Prestamo registrado.\nFecha de vencimiento: " + prestamo.getFechaDevolucion());
                    refrescar();
                },
                error -> {
                    terminarProceso("No se pudo registrar.");
                    Mensajes.error(this, "No se pudo registrar el prestamo.\n" + error);
                    refrescar();
                });
    }

    private void simularConcurrencia() {
        Estudiante est = (Estudiante) cboEstudiante.getSelectedItem();
        Libro libro = (Libro) cboLibro.getSelectedItem();
        if (est == null || libro == null) {
            Mensajes.advertencia(this, "Selecciona un estudiante y un libro para la simulacion.");
            return;
        }
        lblEstado.setText("Simulando 3 prestamos simultaneos del libro con stock " + libro.getStock() + "...");
        for (int i = 1; i <= 3; i++) {
            final int numero = i;
            prestamoController.registrarPrestamoEnSegundoPlano(est.getId(), libro.getId(),
                    p -> {
                        System.out.println("Hilo " + numero + ": prestamo registrado");
                        refrescar();
                    },
                    error -> {
                        System.out.println("Hilo " + numero + ": rechazado -> " + error);
                        lblEstado.setText("Hilo " + numero + " rechazado: " + error);
                        refrescar();
                    });
        }
    }

    private void terminarProceso(String texto) {
        btnPrestar.setEnabled(true);
        barra.setVisible(false);
        lblEstado.setText(texto);
    }

    private void devolver() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona un prestamo de la tabla.");
            return;
        }
        Prestamo p = prestamos.get(fila);
        if (p.isDevuelto()) {
            Mensajes.advertencia(this, "Este prestamo ya fue devuelto.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Registrar la devolucion de \"" + p.getTituloLibro() + "\"?")) return;
        try {
            boolean atrasado = prestamoController.registrarDevolucion(p.getId());
            if (atrasado) {
                // Se deja constancia del atraso
                Mensajes.advertencia(this, "Devolucion registrada CON ATRASO.\n"
                        + "Vencia el " + p.getFechaDevolucion() + ". Queda constancia en el sistema.");
                System.out.println("[ATRASO] Prestamo " + p.getId() + " devuelto con atraso. Vencia: " + p.getFechaDevolucion());
            } else {
                Mensajes.exito(this, "Devolucion registrada dentro del plazo.");
            }
            refrescar();
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "registrar la devolucion", ex);
        }
    }
}
