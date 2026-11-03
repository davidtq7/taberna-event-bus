package taberna.eventos;

import java.util.List;
import taberna.bus.Evento;

public record PedidoRealizadoEvent(String mesaId, String pedidoId, List<ItemPedido> items)
        implements Evento {
    public PedidoRealizadoEvent {
        if (mesaId == null || mesaId.isBlank() || pedidoId == null || pedidoId.isBlank()) {
            throw new IllegalArgumentException("La mesa y el pedido son obligatorios");
        }
        items = List.copyOf(items); // Evita cambios posteriores desde fuera del evento.
        if (items.isEmpty()) {
            throw new IllegalArgumentException("El pedido necesita al menos un item");
        }
    }
}
