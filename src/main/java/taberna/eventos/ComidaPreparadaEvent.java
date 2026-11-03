package taberna.eventos;

import taberna.bus.Evento;

public record ComidaPreparadaEvent(String pedidoId, String plato) implements Evento {
    public ComidaPreparadaEvent {
        if (pedidoId == null || pedidoId.isBlank() || plato == null || plato.isBlank()) {
            throw new IllegalArgumentException("El pedido y el plato son obligatorios");
        }
    }
}
