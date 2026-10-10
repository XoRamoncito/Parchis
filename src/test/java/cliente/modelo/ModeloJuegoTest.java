package cliente.modelo;

import comun.ColorJugador;
import negocio.FachadaJuegoMock;
import negocio.MovimientoInvalidoException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Dos modelos (uno por jugador) conectados a la misma fachada mock, sin red:
 * lo mismo que pasa con sockets, pero dentro de una prueba.
 */
class ModeloJuegoTest {

    @Test
    void elJugadorQueNoEstaEnTurnoVeElMovimientoDelOtro() {
        FachadaJuegoMock fachada = new FachadaJuegoMock(5);
        ModeloJuego ramon = new ModeloJuego(fachada);
        ModeloJuego gustavo = new ModeloJuego(fachada);
        ramon.unirse("Ramon");
        gustavo.unirse("Gustavo");

        AtomicInteger avisosAGustavo = new AtomicInteger();
        gustavo.agregarObservador(avisosAGustavo::incrementAndGet);

        ramon.lanzarDado();   // saca 5 y su ficha sale de la base

        assertEquals(1, avisosAGustavo.get());
        assertEquals(0, gustavo.getEstado().getFichas().get(0).getPasos());
        assertTrue(gustavo.esMiTurno());
        assertFalse(ramon.esMiTurno());
    }

    @Test
    void asignaColorYSabeDeQuienEsElTurno() {
        FachadaJuegoMock fachada = new FachadaJuegoMock(5);
        ModeloJuego ramon = new ModeloJuego(fachada);
        ModeloJuego gustavo = new ModeloJuego(fachada);
        ramon.unirse("Ramon");
        gustavo.unirse("Gustavo");

        assertEquals(ColorJugador.ROJO, ramon.getMiColor());
        assertEquals(ColorJugador.AZUL, gustavo.getMiColor());
        assertTrue(ramon.puedeLanzar());
        assertFalse(gustavo.puedeLanzar());
    }

    @Test
    void rechazaLanzarYMoverFueraDeTurno() {
        FachadaJuegoMock fachada = new FachadaJuegoMock(5, 5, 5);
        ModeloJuego ramon = new ModeloJuego(fachada);
        ModeloJuego gustavo = new ModeloJuego(fachada);
        ramon.unirse("Ramon");
        gustavo.unirse("Gustavo");

        assertThrows(MovimientoInvalidoException.class, gustavo::lanzarDado);
        ramon.lanzarDado();
        gustavo.lanzarDado();
        ramon.lanzarDado();   // segundo 5 de Ramon: puede avanzar o sacar otra ficha
        assertEquals(List.of(0, 1, 2, 3), ramon.getFichasMovibles());
        assertFalse(ramon.puedeLanzar());
        assertTrue(gustavo.getFichasMovibles().isEmpty());
        assertThrows(MovimientoInvalidoException.class, () -> gustavo.moverFicha(0));
    }
}
