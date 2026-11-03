package taberna.eventos;

import java.util.Objects;

public record ItemPedido(String nombre, TipoItem tipo) {
    public ItemPedido {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del item es obligatorio");
        }
        Objects.requireNonNull(tipo, "tipo");
    }
}
