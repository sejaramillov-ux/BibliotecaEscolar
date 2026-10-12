package service;

import javax.swing.SwingUtilities;
import java.util.function.Consumer;

import model.Prestamo;

public class PrestamoTask implements Runnable {

    private final int idEstudiante;
    private final int idLibro;
    private final Consumer<Prestamo> alTerminar;
    private final Consumer<String> alFallar;

    public PrestamoTask(int idEstudiante, int idLibro, Consumer<Prestamo> alTerminar, Consumer<String> alFallar) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.alTerminar = alTerminar;
        this.alFallar = alFallar;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            Prestamo prestamo = PrestamoService.registrarPrestamo(idEstudiante, idLibro);
            SwingUtilities.invokeLater(() -> alTerminar.accept(prestamo));
        } catch (Exception e) {
            System.err.println("[" + Thread.currentThread().getName() + "] " + e.getMessage());
            SwingUtilities.invokeLater(() -> alFallar.accept(e.getMessage()));
        }
    }
}
