package negocio;

import comun.EstadoJuego;

/** Cambios de la partida. */
public interface OyenteJuego {

    void estadoCambiado(EstadoJuego estado);
}
