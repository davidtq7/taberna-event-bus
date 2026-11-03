package taberna;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import taberna.bus.EventBus;
import taberna.bus.Evento;
import taberna.componentes.Barra;
import taberna.componentes.Cocina;
import taberna.componentes.RegistroDeServicio;
import taberna.componentes.SistemaDePedidos;
import taberna.eventos.BebidaServidaEvent;
import taberna.eventos.ComidaPreparadaEvent;
import taberna.eventos.ItemPedido;
import taberna.eventos.PedidoRealizadoEvent;
import taberna.eventos.TipoItem;

/** Pruebas sin dependencias externas: java -cp out taberna.Pruebas */
public final class Pruebas {
    private Pruebas() {
    }

    public static void main(String[] args) throws Exception {
        probarSuscripcionYDesuscripcion();
        probarInmutabilidad();
        probarNuevoSuscriptor();
        probarFlujoConcurrente();
        System.out.println("Todas las pruebas pasaron.");
    }

    private static void probarSuscripcionYDesuscripcion() {
        EventBus bus = new EventBus();
        AtomicInteger bebidas = new AtomicInteger();
        AtomicInteger todos = new AtomicInteger();
        Consumer<BebidaServidaEvent> receptor = evento -> bebidas.incrementAndGet();
        Consumer<Evento> general = evento -> todos.incrementAndGet();
        bus.suscribir(BebidaServidaEvent.class, receptor);
        bus.suscribir(BebidaServidaEvent.class, receptor); // No debe duplicarse.
        bus.suscribir(Evento.class, general); // Recibe cualquier clase de evento.
        bus.publicar(new BebidaServidaEvent("mesa_5", "cerveza"));
        verificar(bebidas.get() == 1 && todos.get() == 1, "Entrega por tipo o duplicada");
        verificar(bus.desuscribir(BebidaServidaEvent.class, receptor), "No se desuscribió");
        bus.publicar(new BebidaServidaEvent("mesa_5", "agua"));
        verificar(bebidas.get() == 1 && todos.get() == 2, "Desuscripción o receptor general");
        verificar(!bus.desuscribir(BebidaServidaEvent.class, receptor), "Desuscripción repetida");

        EventBus aislado = new EventBus();
        AtomicInteger recibidos = new AtomicInteger();
        aislado.suscribir(BebidaServidaEvent.class, evento -> { throw new IllegalStateException("falla de prueba"); });
        aislado.suscribir(BebidaServidaEvent.class, evento -> recibidos.incrementAndGet());
        aislado.publicar(new BebidaServidaEvent("mesa_5", "café"));
        verificar(recibidos.get() == 1, "Un error detuvo a los otros suscriptores");
    }

    private static void probarInmutabilidad() {
        List<ItemPedido> original = new ArrayList<>();
        original.add(new ItemPedido("cerveza", TipoItem.BEBIDA));
        var evento = new PedidoRealizadoEvent("mesa_5", "pedido_123", original);
        original.add(new ItemPedido("hamburguesa", TipoItem.COMIDA));
        verificar(evento.items().size() == 1, "La lista original alteró el evento");
        try {
            evento.items().add(new ItemPedido("agua", TipoItem.BEBIDA));
            throw new AssertionError("El evento permitió modificar su lista");
        } catch (UnsupportedOperationException esperado) {
            // Correcto.
        }
    }

    private static void probarNuevoSuscriptor() {
        EventBus bus = new EventBus();
        var registro = new RegistroDeServicio(bus);
        bus.publicar(new BebidaServidaEvent("mesa_5", "cerveza"));
        bus.publicar(new ComidaPreparadaEvent("pedido_123", "hamburguesa"));
        verificar(registro.bebidasServidas() == 1 && registro.platosPreparados() == 1,
                "El registro de servicio no recibió ambos eventos");
    }

    private static void probarFlujoConcurrente() throws InterruptedException {
        EventBus bus = new EventBus();
        var reloj = Executors.newScheduledThreadPool(2);
        var finalizados = new CountDownLatch(2);
        AtomicInteger bebidas = new AtomicInteger();
        AtomicInteger comidas = new AtomicInteger();
        bus.suscribir(BebidaServidaEvent.class, evento -> {
            bebidas.incrementAndGet();
            finalizados.countDown();
        });
        bus.suscribir(ComidaPreparadaEvent.class, evento -> {
            comidas.incrementAndGet();
            finalizados.countDown();
        });
        try {
            new Barra(bus, reloj, Duration.ofMillis(30));
            new Cocina(bus, reloj, Duration.ofMillis(60));
            new SistemaDePedidos(bus).realizarPedido("mesa_5", "pedido_123", List.of(
                    new ItemPedido("cerveza", TipoItem.BEBIDA),
                    new ItemPedido("hamburguesa", TipoItem.COMIDA)));
            verificar(finalizados.await(2, TimeUnit.SECONDS), "No terminó el flujo");
            verificar(bebidas.get() == 1 && comidas.get() == 1, "Faltó un evento de respuesta");
        } finally {
            reloj.shutdownNow();
        }
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
