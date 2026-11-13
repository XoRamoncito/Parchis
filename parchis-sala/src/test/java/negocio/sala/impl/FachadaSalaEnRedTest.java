package negocio.sala.impl;

import dtos.ColorJugador;
import dtos.JugadorDTO;
import negocio.comunicacion.eventos.BusEventosRed;
import negocio.comunicacion.eventos.RelevoEventos;
import negocio.sala.SalaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Dos computadoras (dos fachadas con su propio socket) comparten la sala por el bus de eventos. */
class FachadaSalaEnRedTest {

    private static int siguientePuerto = 5230;
    private int puerto;
    private RelevoEventos relevo;

    @BeforeEach
    void arrancarBus() throws Exception {
        puerto = siguientePuerto++;
        relevo = new RelevoEventos(puerto);
        relevo.iniciarEnSegundoPlano();
    }

    @AfterEach
    void detenerBus() throws Exception {
        relevo.detener();
    }

    private FachadaSala computadora() throws Exception {
        BusEventosRed bus = new BusEventosRed();
        FachadaSala fachada = new FachadaSala(bus, PerfilesDePrueba.crear());
        bus.conectar("localhost", puerto);
        return fachada;
    }

    private static void esperarHasta(FachadaSala sala, int jugadores) throws InterruptedException {
        for (int i = 0; i < 50 && sala.consultarSala().getJugadores().size() < jugadores; i++) {
            Thread.sleep(50);
        }
    }

    @Test
    void elQueSeRegistraEnUnaComputadoraApareceEnLaOtra() throws Exception {
        FachadaSala ramon = computadora();
        FachadaSala gustavo = computadora();
        ramon.registrarJugador(new JugadorDTO("Ramon", ColorJugador.ROJO, 2));
        esperarHasta(gustavo, 1);
        assertEquals("Ramon", gustavo.consultarSala().getJugadores().get(0).getNombre());
    }

    @Test
    void a2EnRedColorQueYaTomoLaOtraComputadora() throws Exception {
        FachadaSala beto = computadora();
        FachadaSala ana = computadora();
        beto.registrarJugador(new JugadorDTO("Beto", ColorJugador.AZUL, 1));
        esperarHasta(ana, 1);
        try {
            ana.registrarJugador(new JugadorDTO("Ana", ColorJugador.AZUL, 2));
        } catch (SalaException e) {
            assertEquals(SalaException.Motivo.COLOR_OCUPADO, e.getMotivo());
            return;
        }
        throw new AssertionError("Ana no debió quedar registrada con el azul");
    }

    @Test
    void siDosPidenElMismoColorAlMismoTiempoGanaUnoYTodosVenLoMismo() throws Exception {
        FachadaSala a = computadora();
        FachadaSala b = computadora();
        List<String> ganadores = new CopyOnWriteArrayList<>();
        List<SalaException.Motivo> rechazos = new CopyOnWriteArrayList<>();
        CountDownLatch salida = new CountDownLatch(1);
        CountDownLatch fin = new CountDownLatch(2);
        for (Object[] p : new Object[][]{{a, "Ana"}, {b, "Beto"}}) {
            new Thread(() -> {
                try {
                    salida.await();
                    ((FachadaSala) p[0]).registrarJugador(new JugadorDTO((String) p[1], ColorJugador.VERDE, 1));
                    ganadores.add((String) p[1]);
                } catch (SalaException e) {
                    rechazos.add(e.getMotivo());
                } catch (InterruptedException ignored) {
                } finally {
                    fin.countDown();
                }
            }).start();
        }
        salida.countDown();
        fin.await();
        esperarHasta(a, 1);
        esperarHasta(b, 1);
        assertEquals(1, ganadores.size());
        assertEquals(List.of(SalaException.Motivo.COLOR_OCUPADO), rechazos);
        assertEquals(a.consultarSala().getJugadores().get(0).getNombre(), b.consultarSala().getJugadores().get(0).getNombre());
    }

    @Test
    void quienLlegaTardeRecibeLaSalaComoEsta() throws Exception {
        FachadaSala ramon = computadora();
        ramon.registrarJugador(new JugadorDTO("Ramon", ColorJugador.ROJO, 2));
        ramon.marcarListo("Ramon");
        FachadaSala gustavo = computadora();
        esperarHasta(gustavo, 1);
        assertEquals(1, gustavo.consultarSala().getJugadores().size());
        assertTrue(gustavo.consultarSala().getJugadores().get(0).isListo());
    }
}
