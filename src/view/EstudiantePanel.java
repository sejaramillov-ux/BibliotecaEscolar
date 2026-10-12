package view;

import controller.EstudianteController;
import model.Estudiante;
import util.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudiantePanel extends JPanel implements Refrescable {

    private final EstudianteController controller = new EstudianteController();

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtRut = new JTextField();
    private final JTextField txtCurso = new JTextField();
    private final JTextField txtCorreo = new JTextField();

    private final TablaSoloLectura modelo = new TablaSoloLectura("ID", "Nombre", "RUT", "Curso", "Correo");
    private final JTable tabla = new JTable(modelo);
    private List<Estudiante> estudiantes = new ArrayList<>();

    public EstudiantePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos del estudiante"));
        JPanel campos = new JPanel(new GridLayout(3, 4, 6, 6));
        txtId.setEditable(false);
        campos.add(new JLabel("ID:")); campos.add(txtId);
        campos.add(new JLabel("Nombre (*):")); campos.add(txtNombre);
        campos.add(new JLabel("RUT (*):")); campos.add(txtRut);
        campos.add(new JLabel("Curso (*):")); campos.add(txtCurso);
        campos.add(new JLabel("Correo (*):")); campos.add(txtCorreo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        botones.add(btnGuardar); botones.add(btnEliminar); botones.add(btnLimpiar);
        form.add(campos, BorderLayout.CENTER);
        form.add(botones, BorderLayout.SOUTH);
        add(form, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Estudiantes registrados"));
        add(scroll, BorderLayout.CENTER);

        refrescar();
    }

    @Override
    public void refrescar() {
        try {
            estudiantes = controller.listar();
            modelo.setRowCount(0);
            for (Estudiante e : estudiantes) {
                modelo.addRow(new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
            }
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "cargar los estudiantes", ex);
        }
        limpiar();
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        Estudiante e = estudiantes.get(fila);
        txtId.setText(String.valueOf(e.getId()));
        txtNombre.setText(e.getNombre());
        txtRut.setText(e.getRut());
        txtCurso.setText(e.getCurso());
        txtCorreo.setText(e.getCorreo());
    }

    private void guardar() {
        try {
            Estudiante e = new Estudiante(txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()),
                    txtNombre.getText().trim(), txtRut.getText().trim(), txtCurso.getText().trim(), txtCorreo.getText().trim());
            controller.guardar(e);
            Mensajes.exito(this, e.getId() == 0 ? "Estudiante registrado." : "Estudiante modificado.");
            refrescar();
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "guardar el estudiante", ex);
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            Mensajes.advertencia(this, "Selecciona un estudiante de la tabla.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar a " + txtNombre.getText() + "?")) return;
        try {
            controller.eliminar(Integer.parseInt(txtId.getText()));
            Mensajes.exito(this, "Estudiante eliminado.");
            refrescar();
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "eliminar el estudiante", ex);
        }
    }

    private void limpiar() {
        txtId.setText(""); txtNombre.setText(""); txtRut.setText(""); txtCurso.setText(""); txtCorreo.setText("");
        tabla.clearSelection();
    }
}
