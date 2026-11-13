package negocio.sala.impl;

import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.SalaDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** La copia de la sala que tiene esta computadora: quién está, con qué color y si ya está listo. */
public class EstadoSala {

    public static final int CAPACIDAD = 4;

    private final String codigo;
    private final List<JugadorDTO> jugadores = new ArrayList<>();

    public EstadoSala(String codigo) {
        this.codigo = codigo;
    }

    public JugadorDTO buscar(String nombre) {
        for (JugadorDTO jugador : jugadores) {
            if (jugador.getNombre().equalsIgnoreCase(nombre.trim())) {
                return jugador;
            }
        }
        return null;
    }

    public boolean estaLlena() {
        return jugadores.size() >= CAPACIDAD;
    }

    public Set<ColorJugador> getColoresLibres() {
        Set<ColorJugador> libres = EnumSet.allOf(ColorJugador.class);
        for (JugadorDTO jugador : jugadores) {
            libres.remove(jugador.getColor());
        }
        return libres;
    }

    public void agregar(JugadorDTO jugador) {
        jugadores.add(jugador);
    }

    public void marcarListo(String nombre) {
        JugadorDTO jugador = buscar(nombre);
        jugadores.set(jugadores.indexOf(jugador), jugador.comoListo());
    }

    public SalaDTO aSalaDTO() {
        return new SalaDTO(codigo, CAPACIDAD, jugadores);
    }
}
