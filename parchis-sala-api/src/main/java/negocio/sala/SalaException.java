package negocio.sala;

import dtos.ColorJugador;

import java.util.EnumSet;
import java.util.Set;

/** Los flujos alternos del CU-01 son parte del contrato de la fachada. */
public class SalaException extends Exception {

    public enum Motivo {
        /** A1: el nombre está vacío, es muy largo o ya lo usa otro jugador. */
        NOMBRE_INVALIDO,
        /** A2: el color elegido ya lo tiene otro jugador. */
        COLOR_OCUPADO,
        /** A3: la sala ya tiene a todos los jugadores que caben. */
        SALA_LLENA,
        /** El jugador no está en la sala. */
        JUGADOR_NO_REGISTRADO
    }

    private final Motivo motivo;
    private final Set<ColorJugador> coloresLibres;

    public SalaException(Motivo motivo, String mensaje) {
        this(motivo, mensaje, EnumSet.noneOf(ColorJugador.class));
    }

    public SalaException(Motivo motivo, String mensaje, Set<ColorJugador> coloresLibres) {
        super(mensaje);
        this.motivo = motivo;
        this.coloresLibres = coloresLibres.isEmpty() ? EnumSet.noneOf(ColorJugador.class) : EnumSet.copyOf(coloresLibres);
    }

    public Motivo getMotivo() {
        return motivo;
    }

    /** En A2: los colores que todavía se pueden elegir. */
    public Set<ColorJugador> getColoresLibres() {
        return coloresLibres;
    }
}
