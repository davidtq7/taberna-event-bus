package taberna.eventos;

import taberna.bus.Evento;

public record BandaTocandoEvent(String nombreBanda, String nombreCancion, int duracionSegundos)
        implements Evento {
    public BandaTocandoEvent {
        if (nombreBanda == null || nombreBanda.isBlank()
                || nombreCancion == null || nombreCancion.isBlank() || duracionSegundos <= 0) {
            throw new IllegalArgumentException("Datos de canción inválidos");
        }
    }
}
