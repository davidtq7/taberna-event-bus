package taberna.componentes;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import taberna.bus.EventBus;
import taberna.eventos.BandaTocandoEvent;

public final class Banda {
    private final EventBus bus;
    private final ScheduledExecutorService reloj;

    public Banda(EventBus bus, ScheduledExecutorService reloj) {
        this.bus = Objects.requireNonNull(bus);
        this.reloj = Objects.requireNonNull(reloj);
    }

    public void programarCanciones(String nombreBanda, List<Cancion> canciones, Duration intervalo) {
        Objects.requireNonNull(canciones);
        Objects.requireNonNull(intervalo);
        if (intervalo.isNegative() || intervalo.isZero()) {
            throw new IllegalArgumentException("El intervalo debe ser positivo");
        }
        for (int i = 0; i < canciones.size(); i++) {
            Cancion cancion = canciones.get(i);
            long demora = Math.multiplyExact(i, intervalo.toMillis());
            reloj.schedule(() -> {
                Registro.escribir("BANDA", "Tocando: " + cancion.nombre());
                bus.publicar(new BandaTocandoEvent(nombreBanda, cancion.nombre(), cancion.duracionSegundos()));
            }, demora, TimeUnit.MILLISECONDS);
        }
    }

    public record Cancion(String nombre, int duracionSegundos) {
        public Cancion {
            if (nombre == null || nombre.isBlank() || duracionSegundos <= 0) {
                throw new IllegalArgumentException("Canción inválida");
            }
        }
    }
}
