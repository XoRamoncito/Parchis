package comun;

import java.io.Serializable;

/**
 * Ficha en un momento de la partida. La posición se guarda como
 * los pasos que la ficha lleva recorridos desde su salida:
 * -1 = en la base, 0 a 63 = recorrido común, 64 a 70 = pasillo de su color, 71 = meta.
 */
public class EstadoFicha implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int BASE = -1;
    public static final int FIN_RECORRIDO = 63;
    public static final int META = 71;
    public static final int CASILLAS_RECORRIDO = 68;

    private final ColorJugador color;
    private final int numero;
    private final int pasos;

    public EstadoFicha(ColorJugador color, int numero, int pasos) {
        this.color = color;
        this.numero = numero;
        this.pasos = pasos;
    }

    public ColorJugador getColor() {
        return color;
    }

    /** Número de la ficha dentro de su color (0 a 3). */
    public int getNumero() {
        return numero;
    }

    public int getPasos() {
        return pasos;
    }

    public boolean estaEnBase() {
        return pasos == BASE;
    }

    public boolean estaEnRecorrido() {
        return pasos >= 0 && pasos <= FIN_RECORRIDO;
    }

    public boolean estaEnPasillo() {
        return pasos > FIN_RECORRIDO && pasos < META;
    }

    public boolean estaEnMeta() {
        return pasos == META;
    }

    /** Casilla del recorrido común (0 a 67) que ocupa; solo vale si está en el recorrido. */
    public int getCasilla() {
        return (color.getCasillaSalida() + pasos) % CASILLAS_RECORRIDO;
    }
}
