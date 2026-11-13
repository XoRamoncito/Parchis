package negocio.sala.impl;

import dtos.JugadorDTO;
import negocio.sala.SalaException;

/** A1: el nombre no puede estar vacío, pasar de 15 letras ni repetirse en la sala. */
public class NombreValido implements ValidacionRegistro {

    public static final int LARGO_MAXIMO = 15;

    @Override
    public void validar(JugadorDTO jugador, EstadoSala sala) throws SalaException {
        String nombre = jugador.getNombre() == null ? "" : jugador.getNombre().trim();
        if (nombre.isEmpty() || nombre.length() > LARGO_MAXIMO) {
            throw new SalaException(SalaException.Motivo.NOMBRE_INVALIDO, "Escribe un nombre de 1 a " + LARGO_MAXIMO + " letras.");
        }
        if (sala.buscar(nombre) != null) {
            throw new SalaException(SalaException.Motivo.NOMBRE_INVALIDO, "Ya hay un jugador llamado " + nombre + ".");
        }
    }
}
