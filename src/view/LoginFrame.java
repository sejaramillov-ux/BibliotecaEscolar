package view;

import controller.AuthController;
import model.Usuario;
import util.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final AuthController authController = new AuthController();
    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtClave = new JPasswordField(18);

    public LoginFrame() {
        super("Biblioteca Escolar - Iniciar sesion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Sistema de Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(titulo, g);

        g.gridwidth = 1;
        g.gridy = 1; g.gridx = 0; panel.add(new JLabel("Correo o RUT:"), g);
        g.gridx = 1; panel.add(txtUsuario, g);
        g.gridy = 2; g.gridx = 0; panel.add(new JLabel("Contraseña:"), g);
        g.gridx = 1; panel.add(txtClave, g);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnIngresar); // Enter tambien ingresa
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        panel.add(btnIngresar, g);

        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        try {
            Usuario usuario = authController.iniciarSesion(txtUsuario.getText(), new String(txtClave.getPassword()));
            Mensajes.exito(this, "Bienvenido/a " + usuario.getNombre() + " (" + usuario.getTipo() + ")");
            new MainFrame(usuario).setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            Mensajes.advertencia(this, ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.errorSQL(this, "iniciar sesion", ex);
        }
    }
}
