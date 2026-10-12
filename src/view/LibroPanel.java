package view;

import controller.LibroController;
import model.Categoria;
import model.Libro;
import util.Mensajes;
import util.Validador;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroPanel extends JPanel implements Refrescable {

    private final LibroController controller = new LibroController();
    private final boolean puedeEditar;

    private final JTextField txtId = new JTextField();
    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtAutor = new JTextField();
    private final JTextField txtIsbn = new JTextField();
    private final JTextField txtEditorial = new JTextField();
    private final JTextField txtStock = new JTextField();
    private final JComboBox<Categoria> cboCategoria = new JComboBox<>();

    private final TablaSoloLectura modelo = new TablaSoloLectura("ID", "Titulo", "Autor", "ISBN", "Editorial", "Stock", "Categoria");
    private final JTable tabla = new JTable(modelo);
    private List<Libro> libros = new ArrayList<>();

    public LibroPanel(boolean puedeEditar) {
        this.puedeEditar = puedeEditar;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (puedeEditar) {
            add(crearFormulario(), BorderLayout.NORTH);
        }
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(puedeEditar
                ? "Inventario de libros (selecciona uno para editar)" : "Catalogo de libros"));
        add(scroll, BorderLayout.CENTER);

        refrescar();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del libro"));
        JPanel campos = new JPanel(new GridLayout(4, 4, 6, 6));
        txtId.setEditable(false);
        campos.add(new JLabel("ID:")); campos.add(txtId);
        campos.add(new JLabel("Titulo (*):")); campos.add(txtTitulo);
        campos.add(new JLabel("Autor (*):")); campos.add(txtAutor);
        campos.add(new JLabel("ISBN (*):")); campos.add(txtIsbn);
        campos.add(new JLabel("Editorial (*):")); campos.add(txtEditorial);
        campos.add(new JLabel("Stock (*):")); campos.add(txtStock);
        campos.add(new JLabel("Categoria (*):")); campos.add(cboCategoria);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        botones.add(btnGuardar); botones.add(btnEliminar); botones.add(btnLimpiar);

        panel.add(campos, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    @Override
    public void refrescar() {
        try {
            if (puedeEditar) {
                cboCategoria.removeAllItems();
                for (Categoria c : controller.listarCategorias()) cboCategoria.addItem(c);
                cboCategoria.setSelectedIndex(-1);
            }
            libros = controller.listar();
            modelo.setRowCount(0);
            for (Libro l : libros) {
                modelo.addRow(new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(),
                        l.getEditorial(), l.getStock(), l.getNombreCategoria()});
            }
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "cargar los libros", ex);
        }
        limpiar();
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || !puedeEditar) return;
        Libro l = libros.get(fila);
        txtId.setText(String.valueOf(l.getId()));
        txtTitulo.setText(l.getTitulo());
        txtAutor.setText(l.getAutor());
        txtIsbn.setText(l.getIsbn());
        txtEditorial.setText(l.getEditorial());
        txtStock.setText(String.valueOf(l.getStock()));
        for (int i = 0; i < cboCategoria.getItemCount(); i++) {
            if (cboCategoria.getItemAt(i).getId() == l.getIdCategoria()) cboCategoria.setSelectedIndex(i);
        }
    }

    private void guardar() {
        try {
            Libro l = new Libro();
            l.setId(txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()));
            l.setTitulo(txtTitulo.getText().trim());
            l.setAutor(txtAutor.getText().trim());
            l.setIsbn(txtIsbn.getText().trim());
            l.setEditorial(txtEditorial.getText().trim());
            l.setStock(Validador.aEntero(txtStock.getText(), "Stock"));
            Categoria c = (Categoria) cboCategoria.getSelectedItem();
            l.setIdCategoria(c == null ? 0 : c.getId());
            controller.guardar(l);
            Mensajes.exito(this, l.getId() == 0 ? "Libro registrado." : "Libro modificado.");
            refrescar();
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "guardar el libro", ex);
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            Mensajes.advertencia(this, "Selecciona un libro de la tabla.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el libro \"" + txtTitulo.getText() + "\"?")) return;
        try {
            controller.eliminar(Integer.parseInt(txtId.getText()));
            Mensajes.exito(this, "Libro eliminado.");
            refrescar();
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "eliminar el libro", ex);
        }
    }

    private void limpiar() {
        txtId.setText(""); txtTitulo.setText(""); txtAutor.setText("");
        txtIsbn.setText(""); txtEditorial.setText(""); txtStock.setText("");
        cboCategoria.setSelectedIndex(-1);
        tabla.clearSelection();
    }
}
