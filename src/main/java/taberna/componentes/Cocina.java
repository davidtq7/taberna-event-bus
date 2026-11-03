package taberna.componentes;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import taberna.bus.EventBus;
import taberna.eventos.ComidaPreparadaEvent;
import taberna.eventos.PedidoRealizadoEvent;
import taberna.eventos.TipoItem;

public final class Cocina {
    public Cocina(EventBus bus, ScheduledExecutorService reloj, Duration preparacion) {
        Objects.requireNonNull(bus);
        Objects.requireNonNull(reloj);
        Objects.requireNonNull(preparacion);
        if (preparacion.isNegative()) throw new IllegalArgumentException("Tiempo negativo");

        bus.suscribir(PedidoRealizadoEvent.class, pedido -> {
            pedido.items().stream().filter(item -> item.tipo() == TipoItem.COMIDA).forEach(item -> {
                Registro.escribir("COCINA", "Cocinando " + item.nombre());
                reloj.schedule(() -> {
                    Registro.escribir("COCINA", "Lista " + item.nombre());
                    bus.publicar(new ComidaPreparadaEvent(pedido.pedidoId(), item.nombre()));
                }, preparacion.toMillis(), TimeUnit.MILLISECONDS);
            });
        });
    }
}
