package negocio.sala.mock;

import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.SalaDTO;
import negocio.sala.IFachadaSala;
import negocio.sala.ObservadorSala;
import negocio.sala.SalaException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Subsistema Sala simulado: guarda los jugadores en una lista en memoria y
 * lanza las mismas excepciones que el subsistema real (Liskov):
 * A1 nombre inválido, A2 color ocupado y A3 sala llena.
 */
public class FachadaSalaMock implements IFachadaSala {

    public static final int CAPACIDAD = 4;
    public static final int LARGO_MAXIMO_NOMBRE = 15;

    private final String codigo;
    private final List<JugadorDTO> jugadores = new ArrayList<>();
    private final List<ObservadorSala> observadores = new CopyOnWriteArrayList<>();

    public FachadaSalaMock() {
        this.codigo = "AX7-B9Q";
    }

    @Override
    public synchronized SalaDTO registrarJugador(JugadorDTO jugador) throws SalaException {
        String nombre = jugador.getNombre() == null ? "" : jugador.getNombre().trim();
        if (nombre.isEmpty() || nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw new SalaException(SalaException.Motivo.NOMBRE_INVALIDO,
                    "Escribe un nombre de 1 a " + LARGO_MAXIMO_NOMBRE + " letras.");
        }
        if (buscar(nombre) != null) {
            throw new SalaException(SalaException.Motivo.NOMBRE_INVALIDO, "Ya hay un jugador llamado " + nombre + ".");
        }
        if (jugadores.size() >= CAPACIDAD) {
            throw new SalaException(SalaException.Motivo.SALA_LLENA, "La sala ya está llena.");
        }
        for (JugadorDTO otro : jugadores) {
            if (otro.getColor() == jugador.getColor()) {
                throw new SalaException(SalaException.Motivo.COLOR_OCUPADO,
                        "El " + jugador.getColor().getNombre().toLowerCase() + " ya está ocupado. Elige otro color.",
                        crearSala().getColoresLibres());
            }
        }
        jugadores.add(new JugadorDTO(nombre, jugador.getColor(), jugador.getAvatar()));
        return avisar();
    }

    @Override
    public synchronized SalaDTO marcarListo(String nombre) throws SalaException {
        JugadorDTO jugador = buscar(nombre);
        if (jugador == null) {
            throw new SalaException(SalaException.Motivo.JUGADOR_NO_REGISTRADO, nombre + " no está en la sala.");
        }
        jugadores.set(jugadores.indexOf(jugador), jugador.comoListo());
        return avisar();
    }

    @Override
    public synchronized SalaDTO consultarSala() {
        return crearSala();
    }

    @Override
    public void agregarObservador(ObservadorSala observador) {
        observadores.add(observador);
    }

    private JugadorDTO buscar(String nombre) {
        for (JugadorDTO jugador : jugadores) {
            if (jugador.getNombre().equalsIgnoreCase(nombre.trim())) {
                return jugador;
            }
        }
        return null;
    }

    private SalaDTO crearSala() {
        return new SalaDTO(codigo, CAPACIDAD, jugadores);
    }

    private SalaDTO avisar() {
        SalaDTO sala = crearSala();
        for (ObservadorSala observador : observadores) {
            observador.salaActualizada(sala);
        }
        return sala;
    }
}
