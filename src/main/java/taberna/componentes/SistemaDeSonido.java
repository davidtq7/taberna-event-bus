package taberna.componentes;

import java.util.Objects;
import taberna.bus.EventBus;
import taberna.eventos.BandaTocandoEvent;

public final class SistemaDeSonido {
    public SistemaDeSonido(EventBus bus) {
        Objects.requireNonNull(bus).suscribir(BandaTocandoEvent.class,
                evento -> Registro.escribir("SONIDO", "Ajustando ecualizador para: " + evento.nombreCancion()));
    }
}
