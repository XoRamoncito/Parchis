package negocio.sala.impl;

import dtos.JugadorDTO;
import negocio.sala.SalaException;

/**
 * Una regla del registro (abierto-cerrado): para agregar una regla nueva se
 * escribe otra clase y se agrega a la lista de la fachada; no se toca lo que ya hay.
 */
public interface ValidacionRegistro {

    void validar(JugadorDTO jugador, EstadoSala sala) throws SalaException;
}
