package taberna;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import taberna.bus.EventBus;
import taberna.componentes.Banda;
import taberna.componentes.Barra;
import taberna.componentes.Cocina;
import taberna.componentes.PanelLED;
import taberna.componentes.Registro;
import taberna.componentes.RegistroDeServicio;
import taberna.componentes.SistemaDeHumo;
import taberna.componentes.SistemaDePedidos;
import taberna.componentes.SistemaDeSonido;
import taberna.eventos.BandaTocandoEvent;
import taberna.eventos.BebidaServidaEvent;
import taberna.eventos.ComidaPreparadaEvent;
import taberna.eventos.ItemPedido;
import taberna.eventos.TipoItem;

public final class App {
    private App() {
    }

    public static void main(String[] args) throws InterruptedException {
        EventBus bus = new EventBus();
        // Tres hilos permiten que barra, cocina y banda trabajen al mismo tiempo.
        var reloj = Executors.newScheduledThreadPool(3);
        var pendientes = new CountDownLatch(4); // bebida, comida y dos canciones
        bus.suscribir(BebidaServidaEvent.class, evento -> pendientes.countDown());
        bus.suscribir(ComidaPreparadaEvent.class, evento -> pendientes.countDown());
        bus.suscribir(BandaTocandoEvent.class, evento -> pendientes.countDown());

        try {
            var pedidos = new SistemaDePedidos(bus);
            new Barra(bus, reloj, Duration.ofSeconds(2));
            new Cocina(bus, reloj, Duration.ofSeconds(5));
            var banda = new Banda(bus, reloj);
            new SistemaDeSonido(bus);
            new PanelLED(bus);
            new SistemaDeHumo(bus); // Nuevo suscriptor: no modifica Banda ni EventBus.
            new RegistroDeServicio(bus); // Segundo suscriptor nuevo para eventos de servicio.

            Registro.escribir("TABERNA", "Abre El Golem Alquimico");
            pedidos.realizarPedido("mesa_5", "pedido_123", List.of(
                    new ItemPedido("cerveza", TipoItem.BEBIDA),
                    new ItemPedido("hamburguesa", TipoItem.COMIDA)));
            banda.programarCanciones("Los Seguidores de Dijkstra", List.of(
                    new Banda.Cancion("El Algoritmo del Amor", 180),
                    new Banda.Cancion("Through the Fire and Flames", 420)), Duration.ofSeconds(3));

            if (!pendientes.await(8, TimeUnit.SECONDS)) {
                throw new IllegalStateException("No terminaron todos los eventos esperados");
            }
            reloj.shutdown();
            if (!reloj.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Quedaron tareas sin terminar");
            }
            Registro.escribir("TABERNA", "Simulacion terminada");
        } finally {
            reloj.shutdownNow();
        }
    }
}
