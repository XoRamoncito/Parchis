package negocio.comunicacion.eventos;

import dtos.MensajeDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Dos "computadoras" (dos buses con su propio socket) intercambian eventos por el relevo. */
class BusEventosRedTest {

    private static int siguientePuerto = 5190;
    private int puerto;
    private RelevoEventos relevo;

    @BeforeEach
    void arrancarRelevo() throws InterruptedException {
        puerto = siguientePuerto++;
        relevo = new RelevoEventos(puerto);
        new Thread(() -> {
            try {
                relevo.iniciar();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(300);
    }

    @AfterEach
    void detenerRelevo() throws Exception {
        relevo.detener();
    }

    private BusEventosRed conectar() throws Exception {
        BusEventosRed bus = new BusEventosRed();
        bus.conectar("localhost", puerto);
        return bus;
    }

    @Test
    void elMensajeLlegaALaOtraComputadora() throws Exception {
        BusEventosRed ramon = conectar();
        BusEventosRed gustavo = conectar();
        CountDownLatch llego = new CountDownLatch(1);
        List<MensajeDTO> recibidos = new CopyOnWriteArrayList<>();
        gustavo.suscribirse("Saludo", m -> {
            recibidos.add(m);
            llego.countDown();
        });
        Thread.sleep(200);

        ramon.enviar(new MensajeDTO("Saludo", "Ramon", "hola desde la otra computadora"));

        assertTrue(llego.await(3, TimeUnit.SECONDS));
        assertEquals("hola desde la otra computadora", recibidos.get(0).getDatos());
        ramon.desconectar();
        gustavo.desconectar();
    }

    @Test
    void soloRecibenLosSuscritosYTodosEnElMismoOrden() throws Exception {
        BusEventosRed a = conectar();
        BusEventosRed b = conectar();
        List<Long> ordenA = new CopyOnWriteArrayList<>();
        List<Long> ordenB = new CopyOnWriteArrayList<>();
        List<MensajeDTO> otrosEnB = new CopyOnWriteArrayList<>();
        CountDownLatch listos = new CountDownLatch(10);
        a.suscribirse("JugadorUnido", m -> ordenA.add(m.getSecuencia()));
        b.suscribirse("JugadorUnido", m -> {
            ordenB.add(m.getSecuencia());
            listos.countDown();
        });
        b.suscribirse("Saludo", otrosEnB::add);
        Thread.sleep(200);

        for (int i = 0; i < 5; i++) {
            a.enviar(new MensajeDTO("JugadorUnido", "A", i));
            b.enviar(new MensajeDTO("JugadorUnido", "B", i));
        }

        assertTrue(listos.await(3, TimeUnit.SECONDS));
        Thread.sleep(200);
        assertEquals(ordenA, ordenB);
        assertTrue(otrosEnB.isEmpty());
        a.desconectar();
        b.desconectar();
    }
}
