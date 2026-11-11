package negocio.sala;

import dtos.JugadorDTO;
import dtos.SalaDTO;

/**
 * Fachada del subsistema Sala: la única puerta de entrada. En sus firmas solo
 * hay DTOs y tipos de Java. Los flujos alternos llegan como SalaException.
 */
public interface IFachadaSala {

    /**
     * Registra al jugador en la sala (CU-01).
     * @throws SalaException A1 si el nombre no es válido, A2 si el color está ocupado, A3 si la sala está llena
     */
    SalaDTO registrarJugador(JugadorDTO jugador) throws SalaException;

    /** El jugador avisa que está listo para jugar. */
    SalaDTO marcarListo(String nombre) throws SalaException;

    SalaDTO consultarSala();

    /** Quien quiera enterarse cuando alguien llega o cambia de estado. */
    void agregarObservador(ObservadorSala observador);
}
