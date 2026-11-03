package taberna.bus;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Distribuye cada evento a los suscriptores de su clase o de una superclase/interfaz. */
public final class EventBus {
    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Consumer<?>>> suscriptores =
            new ConcurrentHashMap<>();

    public <T extends Evento> void suscribir(Class<T> tipo, Consumer<? super T> suscriptor) {
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(suscriptor, "suscriptor");
        suscriptores.computeIfAbsent(tipo, clave -> new CopyOnWriteArrayList<>())
                .addIfAbsent(suscriptor);
    }

    public <T extends Evento> boolean desuscribir(Class<T> tipo, Consumer<? super T> suscriptor) {
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(suscriptor, "suscriptor");
        var lista = suscriptores.get(tipo);
        return lista != null && lista.remove(suscriptor);
    }

    public void publicar(Evento evento) {
        Objects.requireNonNull(evento, "evento");
        // CopyOnWriteArrayList permite altas y bajas mientras se publica.
        suscriptores.forEach((tipo, lista) -> {
            if (tipo.isAssignableFrom(evento.getClass())) {
                for (Consumer<?> suscriptor : lista) {
                    notificar(suscriptor, evento);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void notificar(Consumer<?> suscriptor, Evento evento) {
        try {
            ((Consumer<Evento>) suscriptor).accept(evento);
        } catch (RuntimeException error) {
            // Un suscriptor defectuoso no impide que los demás reciban el evento.
            System.err.println("[EventBus] Error en suscriptor: " + error.getMessage());
        }
    }
}
