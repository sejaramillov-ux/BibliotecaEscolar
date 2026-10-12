package model;

import java.time.LocalDate;

public class Prestamo {

    public static final int DIAS_PLAZO = 7;

    private int id;
    private int idEstudiante;
    private int idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion; // fecha de vencimiento
    private boolean devuelto;

    // Solo para mostrar en tablas (vienen de un JOIN)
    private String nombreEstudiante;
    private String tituloLibro;

    public Prestamo() {
    }

    public Prestamo(int idEstudiante, int idLibro) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = LocalDate.now();
        // La fecha de vencimiento se calcula automaticamente
        this.fechaDevolucion = fechaPrestamo.plusDays(DIAS_PLAZO);
        this.devuelto = false;
    }

    /** Un prestamo esta atrasado si no se ha devuelto y ya paso la fecha de vencimiento. */
    public boolean estaAtrasado() {
        return !devuelto && fechaDevolucion != null && fechaDevolucion.isBefore(LocalDate.now());
    }

    public String getEstado() {
        if (devuelto) {
            return "DEVUELTO";
        }
        return estaAtrasado() ? "ATRASADO" : "EN PRESTAMO";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public boolean isDevuelto() { return devuelto; }
    public void setDevuelto(boolean devuelto) { this.devuelto = devuelto; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }
    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
}
