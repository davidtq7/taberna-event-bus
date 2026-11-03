package taberna.componentes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Mantiene legibles los mensajes escritos por varios hilos. */
public final class Registro {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private Registro() {
    }

    public static synchronized void escribir(String origen, String mensaje) {
        imprimir(origen, mensaje, false);
    }

    public static synchronized void escribirPanel(String mensaje) {
        imprimir("PANEL LED", mensaje, true);
    }

    private static void imprimir(String origen, String mensaje, boolean colorPanel) {
        String linea = String.format("[%s] %-16s %s", LocalTime.now().format(FORMATO), origen, mensaje);
        if (colorPanel && System.console() != null) {
            System.out.println("\u001B[36m" + linea + "\u001B[0m");
        } else {
            System.out.println(linea);
        }
    }
}
