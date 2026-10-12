package model;

public class Usuario extends Persona {

    private String contrasena;
    private String rol;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String rut, String correo, String contrasena, String rol) {
        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    /** El rol bibliotecario tiene gestion total; el estudiante solo prestamos y consultas. */
    public boolean esBibliotecario() {
        return rol != null && (rol.toUpperCase().contains("BIBLIO") || rol.toUpperCase().contains("ADMIN"));
    }

    @Override
    public String getTipo() {
        return esBibliotecario() ? "Bibliotecario" : "Estudiante";
    }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
