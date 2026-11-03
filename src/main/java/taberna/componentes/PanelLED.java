package taberna.componentes;

import java.util.Objects;
import taberna.bus.EventBus;
import taberna.bus.Evento;
import taberna.eventos.BandaTocandoEvent;
import taberna.eventos.BebidaServidaEvent;
import taberna.eventos.ComidaPreparadaEvent;
import taberna.eventos.PedidoRealizadoEvent;

public final class PanelLED {
    public PanelLED(EventBus bus) {
        Objects.requireNonNull(bus).suscribir(Evento.class, this::mostrar);
    }

    private void mostrar(Evento evento) {
        if (evento instanceof PedidoRealizadoEvent pedido) {
            Registro.escribirPanel("Nuevo pedido en " + pedido.mesaId());
        } else if (evento instanceof BebidaServidaEvent bebida) {
            Registro.escribirPanel(bebida.bebida() + " servida en " + bebida.mesaId());
        } else if (evento instanceof ComidaPreparadaEvent comida) {
            Registro.escribirPanel("Plato listo: " + comida.plato() + " (" + comida.pedidoId() + ")");
        } else if (evento instanceof BandaTocandoEvent banda) {
            Registro.escribirPanel(banda.nombreBanda() + " - " + banda.nombreCancion());
        }
    }
}
