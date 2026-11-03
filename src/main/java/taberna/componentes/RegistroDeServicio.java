package taberna.componentes;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import taberna.bus.EventBus;
import taberna.eventos.BebidaServidaEvent;
import taberna.eventos.ComidaPreparadaEvent;

/** Ejemplo de otro suscriptor agregado sin cambiar los publicadores. */
public final class RegistroDeServicio {
    private final AtomicInteger bebidas = new AtomicInteger();
    private final AtomicInteger platos = new AtomicInteger();

    public RegistroDeServicio(EventBus bus) {
        Objects.requireNonNull(bus);
        bus.suscribir(BebidaServidaEvent.class, evento -> {
            int total = bebidas.incrementAndGet();
            Registro.escribir("SERVICIO", "Bebidas servidas: " + total);
        });
        bus.suscribir(ComidaPreparadaEvent.class, evento -> {
            int total = platos.incrementAndGet();
            Registro.escribir("SERVICIO", "Platos preparados: " + total);
        });
    }

    public int bebidasServidas() {
        return bebidas.get();
    }

    public int platosPreparados() {
        return platos.get();
    }
}
