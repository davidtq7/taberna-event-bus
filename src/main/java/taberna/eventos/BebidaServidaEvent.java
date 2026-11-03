package taberna.eventos;

import taberna.bus.Evento;

public record BebidaServidaEvent(String mesaId, String bebida) implements Evento {
    public BebidaServidaEvent {
        if (mesaId == null || mesaId.isBlank() || bebida == null || bebida.isBlank()) {
            throw new IllegalArgumentException("La mesa y la bebida son obligatorias");
        }
    }
}
