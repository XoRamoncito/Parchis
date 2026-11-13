package negocio.sala.impl;

import dtos.JugadorDTO;
import negocio.sala.SalaException;

/** A2: el color elegido no lo puede tener otro jugador; se ofrecen los que quedan libres. */
public class ColorLibre implements ValidacionRegistro {

    @Override
    public void validar(JugadorDTO jugador, EstadoSala sala) throws SalaException {
        if (!sala.getColoresLibres().contains(jugador.getColor())) {
            throw new SalaException(SalaException.Motivo.COLOR_OCUPADO,
                    "El " + jugador.getColor().getNombre().toLowerCase() + " ya está ocupado. Elige otro color.",
                    sala.getColoresLibres());
        }
    }
}
