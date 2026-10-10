package comun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Foto completa de la partida.
 */
public class EstadoJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int version;
    private final Map<ColorJugador, String> jugadores;
    private final List<EstadoFicha> fichas;
    private final ColorJugador turno;
    private final int valorDado;
    private final boolean dadoLanzado;
    private final boolean tiroExtra;
    private final List<Integer> fichasMovibles;
    private final ColorJugador ganador;
    private final String mensaje;

    public EstadoJuego(int version, Map<ColorJugador, String> jugadores, List<EstadoFicha> fichas,
                       ColorJugador turno, int valorDado, boolean dadoLanzado, boolean tiroExtra,
                       List<Integer> fichasMovibles, ColorJugador ganador, String mensaje) {
        this.version = version;
        this.jugadores = new LinkedHashMap<>(jugadores);
        this.fichas = new ArrayList<>(fichas);
        this.turno = turno;
        this.valorDado = valorDado;
        this.dadoLanzado = dadoLanzado;
        this.tiroExtra = tiroExtra;
        this.fichasMovibles = new ArrayList<>(fichasMovibles);
        this.ganador = ganador;
        this.mensaje = mensaje;
    }

    /** Crece en 1 con cada cambio; sirve para distinguir un estado del anterior. */
    public int getVersion() {
        return version;
    }

    /** Jugadores en el orden en que les toca, con su nombre. */
    public Map<ColorJugador, String> getJugadores() {
        return Collections.unmodifiableMap(jugadores);
    }

    public String getNombre(ColorJugador color) {
        return jugadores.get(color);
    }

    public List<EstadoFicha> getFichas() {
        return Collections.unmodifiableList(fichas);
    }

    /** Color del jugador en turno, o null si la partida aún no empieza. */
    public ColorJugador getTurno() {
        return turno;
    }

    /** Último valor que salió en el dado; 0 si todavía no se lanza. */
    public int getValorDado() {
        return valorDado;
    }

    /** true cuando el jugador en turno ya lanzó y le falta mover una ficha. */
    public boolean isDadoLanzado() {
        return dadoLanzado;
    }

    /** true cuando el jugador en turno ganó otro lanzamiento por sacar 6. */
    public boolean isTiroExtra() {
        return tiroExtra;
    }

    /** Números de las fichas que el jugador en turno puede mover con el dado actual. */
    public List<Integer> getFichasMovibles() {
        return Collections.unmodifiableList(fichasMovibles);
    }

    public ColorJugador getGanador() {
        return ganador;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isIniciada() {
        return turno != null;
    }
}
