package taberna.componentes;

import java.util.List;
import java.util.Objects;
import taberna.bus.EventBus;
import taberna.eventos.ItemPedido;
import taberna.eventos.PedidoRealizadoEvent;

public final class SistemaDePedidos {
    private final EventBus bus;

    public SistemaDePedidos(EventBus bus) {
        this.bus = Objects.requireNonNull(bus);
    }

    public void realizarPedido(String mesaId, String pedidoId, List<ItemPedido> items) {
        var evento = new PedidoRealizadoEvent(mesaId, pedidoId, items);
        Registro.escribir("PEDIDOS", "Pedido " + pedidoId + " de " + mesaId + ": " + items);
        bus.publicar(evento);
    }
}
