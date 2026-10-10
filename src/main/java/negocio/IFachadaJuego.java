package negocio;

import comun.ColorJugador;
import comun.EstadoJuego;

/**
 * Fachada del subsistema de reglas del Parchís (Partida, Tablero, Casilla,
 * Dado, Ficha, Jugador).
 */
public interface IFachadaJuego {

    /** Agrega un jugador a la partida y devuelve el color que el sistema le asignó. */
    ColorJugador unirJugador(String nombre);

    /** El jugador indicado lanza el dado. Rechaza la operación si no es su turno. */
    void lanzarDado(ColorJugador jugador);

    /** El jugador indicado mueve una de sus fichas (0 a 3) lo que marcó el dado. */
    void moverFicha(ColorJugador jugador, int numeroFicha);

    /** Estado actual de la partida. */
    EstadoJuego obtenerEstado();

    /** Registra a quién se le avisa cada vez que la partida cambia. */
    void registrarOyente(OyenteJuego oyente);
}
