package taberna.componentes;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import taberna.bus.EventBus;
import taberna.eventos.BebidaServidaEvent;
import taberna.eventos.PedidoRealizadoEvent;
import taberna.eventos.TipoItem;

public final class Barra {
    public Barra(EventBus bus, ScheduledExecutorService reloj, Duration preparacion) {
        Objects.requireNonNull(bus);
        Objects.requireNonNull(reloj);
        Objects.requireNonNull(preparacion);
        if (preparacion.isNegative()) throw new IllegalArgumentException("Tiempo negativo");

        bus.suscribir(PedidoRealizadoEvent.class, pedido -> {
            pedido.items().stream().filter(item -> item.tipo() == TipoItem.BEBIDA).forEach(item -> {
                Registro.escribir("BARRA", "Preparando " + item.nombre());
                reloj.schedule(() -> {
                    Registro.escribir("BARRA", "Lista " + item.nombre());
                    bus.publicar(new BebidaServidaEvent(pedido.mesaId(), item.nombre()));
                }, preparacion.toMillis(), TimeUnit.MILLISECONDS);
            });
        });
    }
}
