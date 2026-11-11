package dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Foto de la sala de espera: su código, cuántos caben y quiénes están. */
public class SalaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String codigo;
    private final int capacidad;
    private final List<JugadorDTO> jugadores;

    public SalaDTO(String codigo, int capacidad, List<JugadorDTO> jugadores) {
        this.codigo = codigo;
        this.capacidad = capacidad;
        this.jugadores = new ArrayList<>(jugadores);
    }

    public String getCodigo() {
        return codigo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public List<JugadorDTO> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }

    /** Colores que todavía nadie eligió. */
    public Set<ColorJugador> getColoresLibres() {
        Set<ColorJugador> libres = EnumSet.allOf(ColorJugador.class);
        for (JugadorDTO jugador : jugadores) {
            libres.remove(jugador.getColor());
        }
        return libres;
    }
}
