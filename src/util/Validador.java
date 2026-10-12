package util;

public class Validador {

    public static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean esCorreoValido(String correo) {
        return !estaVacio(correo) && correo.trim().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$");
    }

    public static boolean esRutValido(String rut) {
        return !estaVacio(rut) && rut.trim().matches("^[0-9.]{7,10}-[0-9kK]$");
    }

    public static int aEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser un numero entero.");
        }
    }
}
