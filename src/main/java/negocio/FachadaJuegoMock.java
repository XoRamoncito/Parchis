package negocio;

import comun.ColorJugador;
import comun.EstadoFicha;
import comun.EstadoJuego;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * - todas las fichas empiezan en la base y se sale con un 5;
 * - la ficha avanza lo que marca el dado y entra a la meta con el número exacto;
 * - caer sobre una ficha rival la regresa a su base, salvo que esté en una
 *   casilla segura (salidas y casillas grises) o que sean dos fichas del mismo
 *   color en la casilla (barrera);
 * - dan otro tiro: sacar 6, capturar una ficha y meter una ficha a la meta;
 *   el tercer 6 seguido hace perder el turno;
 * - si no hay ficha que se pueda mover, el turno pasa solo; si solo hay una
 *   opción, la ficha se mueve sola sin preguntar.
 */
public class FachadaJuegoMock implements IFachadaJuego {

    public static final int JUGADORES_MINIMOS = 2;
    public static final int FICHAS_POR_JUGADOR = 4;
    private static final int MAXIMO_SEISES = 3;

    private final List<ColorJugador> orden = new ArrayList<>();
    private final Map<ColorJugador, String> nombres = new LinkedHashMap<>();
    private final Map<ColorJugador, int[]> pasos = new EnumMap<>(ColorJugador.class);
    private final List<OyenteJuego> oyentes = new CopyOnWriteArrayList<>();
    private final Deque<Integer> tirosProgramados = new ArrayDeque<>();
    private final Random azar = new Random();

    private int indiceTurno;
    private int valorDado;
    private boolean dadoLanzado;
    private boolean tiroExtra;
    private int seisesSeguidos;
    private boolean enCurso;
    private ColorJugador ganador;
    private String mensaje = "Esperando jugadores...";
    private int version;

    public FachadaJuegoMock(int... tiros) {
        for (int tiro : tiros) {
            tirosProgramados.add(tiro);
        }
    }

    @Override
    public synchronized ColorJugador unirJugador(String nombre) {
        if (enCurso) {
            throw new MovimientoInvalidoException("La partida ya comenzó");
        }
        if (orden.size() == ColorJugador.values().length) {
            throw new MovimientoInvalidoException("La partida está llena");
        }
        ColorJugador color = ColorJugador.values()[orden.size()];
        orden.add(color);
        nombres.put(color, nombre);
        int[] fichas = new int[FICHAS_POR_JUGADOR];
        java.util.Arrays.fill(fichas, EstadoFicha.BASE);
        pasos.put(color, fichas);

        if (orden.size() < JUGADORES_MINIMOS) {
            mensaje = "Esperando a otro jugador...";
        } else {
            mensaje = "Empieza " + nombres.get(orden.get(indiceTurno)) + ". Lanza el dado.";
        }
        notificar();
        return color;
    }

    @Override
    public synchronized void lanzarDado(ColorJugador jugador) {
        validarTurno(jugador);
        if (dadoLanzado) {
            throw new MovimientoInvalidoException("Ya lanzaste el dado: mueve una ficha");
        }
        enCurso = true;
        tiroExtra = false;
        valorDado = siguienteTiro();
        seisesSeguidos = valorDado == 6 ? seisesSeguidos + 1 : 0;
        String nombre = nombres.get(jugador);
        List<Integer> movibles = fichasMovibles(jugador);

        if (seisesSeguidos == MAXIMO_SEISES) {
            mensaje = nombre + " sacó tres 6 seguidos y pierde el turno.";
            pasarTurno();
        } else if (movibles.isEmpty()) {
            if (valorDado == 6) {
                tiroExtra = true;
                mensaje = nombre + " sacó 6: no puede mover, pero tiene otro tiro.";
            } else {
                mensaje = nombre + " sacó " + valorDado + " y no puede mover. Pasa el turno.";
                pasarTurno();
            }
        } else if (esUnicaOpcion(jugador, movibles)) {
            aplicarMovimiento(jugador, movibles.get(0), nombre + " sacó " + valorDado + " y ");
        } else {
            dadoLanzado = true;
            mensaje = nombre + " sacó " + valorDado + ". Elige una ficha.";
        }
        notificar();
    }

    @Override
    public synchronized void moverFicha(ColorJugador jugador, int numeroFicha) {
        validarTurno(jugador);
        if (!dadoLanzado) {
            throw new MovimientoInvalidoException("Primero lanza el dado");
        }
        if (!fichasMovibles(jugador).contains(numeroFicha)) {
            throw new MovimientoInvalidoException("Esa ficha no se puede mover con un " + valorDado);
        }
        aplicarMovimiento(jugador, numeroFicha, nombres.get(jugador) + " ");
        notificar();
    }

    /** Mueve la ficha y decide si el jugador repite (6, captura o meta) o si pasa el turno. */
    private void aplicarMovimiento(ColorJugador jugador, int numeroFicha, String inicio) {
        int[] fichas = pasos.get(jugador);
        String detalle;
        boolean premio = false;

        if (fichas[numeroFicha] == EstadoFicha.BASE) {
            fichas[numeroFicha] = 0;
            detalle = "sacó una ficha de su base";
        } else {
            fichas[numeroFicha] += valorDado;
            detalle = "avanzó " + valorDado + (valorDado == 1 ? " casilla" : " casillas");
        }
        if (fichas[numeroFicha] == EstadoFicha.META) {
            detalle = "metió una ficha a la meta";
            premio = true;
        } else if (capturar(jugador, fichas[numeroFicha])) {
            detalle += " y capturó una ficha rival";
            premio = true;
        }
        dadoLanzado = false;

        if (todasEnMeta(fichas)) {
            ganador = jugador;
            mensaje = nombres.get(jugador) + " metió sus cuatro fichas y ganó la partida.";
        } else if (valorDado == 6 || premio) {
            tiroExtra = true;
            mensaje = inicio + detalle + ". Tiene otro tiro.";
        } else {
            mensaje = inicio + detalle + ".";
            pasarTurno();
        }
    }

    /** true si no hay nada que elegir: una sola ficha movible, o todas las movibles están en la base. */
    private boolean esUnicaOpcion(ColorJugador jugador, List<Integer> movibles) {
        if (movibles.size() == 1) {
            return true;
        }
        int[] fichas = pasos.get(jugador);
        for (int numero : movibles) {
            if (fichas[numero] != EstadoFicha.BASE) {
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized EstadoJuego obtenerEstado() {
        List<EstadoFicha> fichas = new ArrayList<>();
        for (ColorJugador color : orden) {
            int[] suyas = pasos.get(color);
            for (int i = 0; i < suyas.length; i++) {
                fichas.add(new EstadoFicha(color, i, suyas[i]));
            }
        }
        ColorJugador turno = haIniciado() ? orden.get(indiceTurno) : null;
        List<Integer> movibles = dadoLanzado ? fichasMovibles(turno) : new ArrayList<>();
        return new EstadoJuego(version, nombres, fichas, turno, valorDado, dadoLanzado,
                tiroExtra, movibles, ganador, mensaje);
    }

    @Override
    public void registrarOyente(OyenteJuego oyente) {
        oyentes.add(oyente);
    }

    private boolean haIniciado() {
        return orden.size() >= JUGADORES_MINIMOS;
    }

    private void validarTurno(ColorJugador jugador) {
        if (!haIniciado()) {
            throw new MovimientoInvalidoException("Faltan jugadores para empezar");
        }
        if (ganador != null) {
            throw new MovimientoInvalidoException("La partida ya terminó");
        }
        if (orden.get(indiceTurno) != jugador) {
            throw new MovimientoInvalidoException("No es tu turno");
        }
    }

    private int siguienteTiro() {
        Integer programado = tirosProgramados.poll();
        return programado != null ? programado : azar.nextInt(6) + 1;
    }

    private List<Integer> fichasMovibles(ColorJugador jugador) {
        List<Integer> movibles = new ArrayList<>();
        int[] fichas = pasos.get(jugador);
        for (int i = 0; i < fichas.length; i++) {
            if (fichas[i] == EstadoFicha.BASE) {
                if (valorDado == 5) {
                    movibles.add(i);
                }
            } else if (fichas[i] + valorDado <= EstadoFicha.META) {
                movibles.add(i);
            }
        }
        return movibles;
    }

    /** Regresa a su base a la ficha rival que esté sola donde cayó la ficha. */
    private boolean capturar(ColorJugador jugador, int pasosFicha) {
        if (pasosFicha > EstadoFicha.FIN_RECORRIDO) {
            return false;
        }
        int casilla = new EstadoFicha(jugador, 0, pasosFicha).getCasilla();
        if (esCasillaSegura(casilla)) {
            return false;
        }
        boolean capturo = false;
        for (ColorJugador rival : orden) {
            if (rival == jugador) {
                continue;
            }
            int[] suyas = pasos.get(rival);
            List<Integer> enLaCasilla = new ArrayList<>();
            for (int i = 0; i < suyas.length; i++) {
                EstadoFicha ficha = new EstadoFicha(rival, i, suyas[i]);
                if (ficha.estaEnRecorrido() && ficha.getCasilla() == casilla) {
                    enLaCasilla.add(i);
                }
            }
            // Dos fichas del mismo color forman barrera y no se pueden capturar.
            if (enLaCasilla.size() == 1) {
                suyas[enLaCasilla.get(0)] = EstadoFicha.BASE;
                capturo = true;
            }
        }
        return capturo;
    }

    /** Son seguras las salidas (de color) y las casillas grises: ahí no se captura. */
    public static boolean esCasillaSegura(int casilla) {
        int resto = casilla % 17;
        return resto == 4 || resto == 11 || resto == 16;
    }

    private boolean todasEnMeta(int[] fichas) {
        for (int ficha : fichas) {
            if (ficha != EstadoFicha.META) {
                return false;
            }
        }
        return true;
    }

    private void pasarTurno() {
        indiceTurno = (indiceTurno + 1) % orden.size();
        seisesSeguidos = 0;
        dadoLanzado = false;
        tiroExtra = false;
    }

    private void notificar() {
        version++;
        EstadoJuego estado = obtenerEstado();
        for (OyenteJuego oyente : oyentes) {
            oyente.estadoCambiado(estado);
        }
    }
}
