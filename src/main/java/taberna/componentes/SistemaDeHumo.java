package taberna.componentes;

import java.util.Objects;
import taberna.bus.EventBus;
import taberna.eventos.BandaTocandoEvent;

/** Se añadió después, sin cambios en Banda ni EventBus. */
public final class SistemaDeHumo {
    public SistemaDeHumo(EventBus bus) {
        Objects.requireNonNull(bus).suscribir(BandaTocandoEvent.class, evento -> {
            if (evento.nombreCancion().equals("Through the Fire and Flames")) {
                Registro.escribir("HUMO", "Activando humo para: " + evento.nombreCancion());
            }
        });
    }
}
