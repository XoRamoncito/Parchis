package negocio.sala.impl;

import dtos.JugadorDTO;
import negocio.sala.SalaException;

/** A3: no entra nadie si la sala ya tiene cuatro jugadores. */
public class SalaConLugar implements ValidacionRegistro {

    @Override
    public void validar(JugadorDTO jugador, EstadoSala sala) throws SalaException {
        if (sala.estaLlena()) {
            throw new SalaException(SalaException.Motivo.SALA_LLENA, "La sala ya está llena.");
        }
    }
}
