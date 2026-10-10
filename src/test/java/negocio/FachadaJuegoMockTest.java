package negocio;

import comun.ColorJugador;
import comun.EstadoFicha;
import comun.EstadoJuego;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Rojo (Ramon) sale en la casilla 38 y Azul (Gustavo) en la 55. En cada prueba
 * los tiros están programados y se reparten en el orden en que se lanzan.
 */
class FachadaJuegoMockTest {

    private static final ColorJugador ROJO = ColorJugador.ROJO;
    private static final ColorJugador AZUL = ColorJugador.AZUL;

    /** Partida de dos jugadores con los tiros indicados. */
    private FachadaJuegoMock partida(int... tiros) {
        FachadaJuegoMock fachada = new FachadaJuegoMock(tiros);
        fachada.unirJugador("Ramon");
        fachada.unirJugador("Gustavo");
        return fachada;
    }

    private int pasos(FachadaJuegoMock fachada, ColorJugador color, int numero) {
        for (EstadoFicha ficha : fachada.obtenerEstado().getFichas()) {
            if (ficha.getColor() == color && ficha.getNumero() == numero) {
                return ficha.getPasos();
            }
        }
        throw new AssertionError("No existe la ficha");
    }

    /** Lanza el jugador que esté en turno, las veces indicadas. */
    private void lanzar(FachadaJuegoMock fachada, int veces) {
        for (int i = 0; i < veces; i++) {
            fachada.lanzarDado(fachada.obtenerEstado().getTurno());
        }
    }

    @Test
    void asignaColoresEnOrdenYEmpiezaConDosJugadores() {
        FachadaJuegoMock fachada = new FachadaJuegoMock();
        assertEquals(ROJO, fachada.unirJugador("Ramon"));
        assertNull(fachada.obtenerEstado().getTurno());
        assertEquals(AZUL, fachada.unirJugador("Gustavo"));
        assertEquals(ROJO, fachada.obtenerEstado().getTurno());
    }

    @Test
    void todasLasFichasEmpiezanEnLaBase() {
        FachadaJuegoMock fachada = partida();
        for (EstadoFicha ficha : fachada.obtenerEstado().getFichas()) {
            assertTrue(ficha.estaEnBase());
        }
    }

    @Test
    void sinUnCincoNoSeSaleDeLaBaseYElTurnoPasa() {
        FachadaJuegoMock fachada = partida(3);
        fachada.lanzarDado(ROJO);
        assertEquals(EstadoFicha.BASE, pasos(fachada, ROJO, 0));
        assertEquals(AZUL, fachada.obtenerEstado().getTurno());
    }

    @Test
    void conUnCincoLaFichaSaleSolaPorqueNoHayNadaQueElegir() {
        FachadaJuegoMock fachada = partida(5);
        fachada.lanzarDado(ROJO);
        assertEquals(0, pasos(fachada, ROJO, 0));
        assertFalse(fachada.obtenerEstado().isDadoLanzado());
        assertEquals(AZUL, fachada.obtenerEstado().getTurno());
    }

    @Test
    void siSoloUnaFichaPuedeMoverseSeMueveSola() {
        FachadaJuegoMock fachada = partida(5, 5, 3);
        lanzar(fachada, 3);
        assertEquals(3, pasos(fachada, ROJO, 0));
        assertEquals(AZUL, fachada.obtenerEstado().getTurno());
    }

    @Test
    void conDosOpcionesElJugadorEligeLaFicha() {
        FachadaJuegoMock fachada = partida(5, 5, 5);
        lanzar(fachada, 3);
        EstadoJuego estado = fachada.obtenerEstado();
        assertTrue(estado.isDadoLanzado());
        assertEquals(ROJO, estado.getTurno());
        assertEquals(List.of(0, 1, 2, 3), estado.getFichasMovibles());

        fachada.moverFicha(ROJO, 1);
        assertEquals(0, pasos(fachada, ROJO, 1));
        assertEquals(0, pasos(fachada, ROJO, 0));
        assertEquals(AZUL, fachada.obtenerEstado().getTurno());
    }

    @Test
    void noSePuedeJugarFueraDeTurno() {
        FachadaJuegoMock fachada = partida(5);
        assertThrows(MovimientoInvalidoException.class, () -> fachada.lanzarDado(AZUL));
        assertEquals(0, fachada.obtenerEstado().getValorDado());
    }

    @Test
    void noSePuedeMoverSinLanzarNiLanzarDosVeces() {
        FachadaJuegoMock fachada = partida(5, 5, 5);
        assertThrows(MovimientoInvalidoException.class, () -> fachada.moverFicha(ROJO, 0));
        lanzar(fachada, 3);
        assertThrows(MovimientoInvalidoException.class, () -> fachada.lanzarDado(ROJO));
    }

    @Test
    void unSeisDaOtroTiroYConservaElTurno() {
        FachadaJuegoMock fachada = partida(5, 5, 6);
        lanzar(fachada, 3);
        EstadoJuego estado = fachada.obtenerEstado();
        assertTrue(estado.isTiroExtra());
        assertEquals(ROJO, estado.getTurno());
        assertEquals(6, pasos(fachada, ROJO, 0));
    }

    @Test
    void elTercerSeisSeguidoPierdeElTurno() {
        FachadaJuegoMock fachada = partida(5, 5, 6, 6, 6);
        lanzar(fachada, 5);
        EstadoJuego estado = fachada.obtenerEstado();
        assertEquals(AZUL, estado.getTurno());
        assertEquals(12, pasos(fachada, ROJO, 0));
        assertFalse(estado.isTiroExtra());
    }

    @Test
    void capturarUnaFichaRivalLaRegresaASuBaseYDaOtroTiro() {
        // Azul queda en su paso 3 (casilla 58) y Rojo llega ahí con 6 + 6 + 4 + 4 = 20 pasos.
        FachadaJuegoMock fachada = partida(5, 5, 6, 6, 4, 3, 4);
        lanzar(fachada, 7);
        EstadoJuego estado = fachada.obtenerEstado();
        assertEquals(20, pasos(fachada, ROJO, 0));
        assertEquals(EstadoFicha.BASE, pasos(fachada, AZUL, 0));
        assertTrue(estado.isTiroExtra());
        assertEquals(ROJO, estado.getTurno());
    }

    @Test
    void dosFichasEnLaMismaCasillaFormanBarreraYNoSeCapturan() {
        // Azul junta sus fichas 0 y 1 en el paso 3 (casilla 58); Rojo cae ahí con su paso 20.
        FachadaJuegoMock fachada = partida(5, 5, 6, 2, 5, 4, 3, 4, 3, 4);
        lanzar(fachada, 5);              // Rojo sale; Azul sale; Rojo 6 y 2; Azul saca 5
        fachada.moverFicha(AZUL, 1);     // Azul saca su segunda ficha
        lanzar(fachada, 2);              // Rojo 4; Azul saca 3
        fachada.moverFicha(AZUL, 0);
        lanzar(fachada, 2);              // Rojo 4; Azul saca 3
        fachada.moverFicha(AZUL, 1);
        lanzar(fachada, 1);              // Rojo 4: cae en la casilla de la barrera

        assertEquals(20, pasos(fachada, ROJO, 0));
        assertEquals(3, pasos(fachada, AZUL, 0));
        assertEquals(3, pasos(fachada, AZUL, 1));
        assertEquals(AZUL, fachada.obtenerEstado().getTurno());
    }

    @Test
    void enUnaCasillaSeguraNoSeCaptura() {
        // Azul acaba de salir (casilla 55, su salida) y Rojo cae ahí con su paso 17.
        assertTrue(FachadaJuegoMock.esCasillaSegura(55));
        FachadaJuegoMock fachada = partida(5, 1, 6, 4, 2, 4, 5, 3);
        lanzar(fachada, 8);
        assertEquals(17, pasos(fachada, ROJO, 0));
        assertEquals(0, pasos(fachada, AZUL, 0));
        assertEquals(AZUL, fachada.obtenerEstado().getTurno());
    }

    @Test
    void lasCasillasGrisesYLasSalidasSonSeguras() {
        assertTrue(FachadaJuegoMock.esCasillaSegura(ROJO.getCasillaSalida()));
        assertTrue(FachadaJuegoMock.esCasillaSegura(11));
        assertTrue(FachadaJuegoMock.esCasillaSegura(16));
        assertFalse(FachadaJuegoMock.esCasillaSegura(58));
    }

    @Test
    void meterUnaFichaALaMetaDaOtroTiro() {
        // Rojo recorre 71 pasos (cuatro turnos de 6 + 6 + 4, luego 4 y 3) mientras Azul no logra salir.
        FachadaJuegoMock fachada = partida(5, 1, 6, 6, 4, 1, 6, 6, 4, 1, 6, 6, 4, 1, 6, 6, 4, 1, 4, 1, 3);
        lanzar(fachada, 21);
        EstadoJuego estado = fachada.obtenerEstado();
        assertEquals(EstadoFicha.META, pasos(fachada, ROJO, 0));
        assertTrue(estado.isTiroExtra());
        assertEquals(ROJO, estado.getTurno());
    }

    @Test
    void avisaASusOyentesEnCadaCambio() {
        FachadaJuegoMock fachada = partida(5, 5);
        List<EstadoJuego> recibidos = new ArrayList<>();
        fachada.registrarOyente(recibidos::add);

        lanzar(fachada, 2);

        assertEquals(2, recibidos.size());
        assertEquals(ROJO, recibidos.get(1).getTurno());
        assertTrue(recibidos.get(1).getVersion() > recibidos.get(0).getVersion());
    }
}
