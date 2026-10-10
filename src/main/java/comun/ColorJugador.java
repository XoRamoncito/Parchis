package comun;

/**
 * Color de un jugador. El orden de la enumeración es el orden en que el
 * sistema asigna los colores a quienes se van uniendo.
 */
public enum ColorJugador {
    ROJO("Rojo", 38),
    AZUL("Azul", 55),
    VERDE("Verde", 21),
    AMARILLO("Amarillo", 4);

    private final String nombre;
    private final int casillaSalida;

    ColorJugador(String nombre, int casillaSalida) {
        this.nombre = nombre;
        this.casillaSalida = casillaSalida;
    }

    public String getNombre() {
        return nombre;
    }

    /** Casilla del recorrido común (0 a 67) donde salen las fichas de este color. */
    public int getCasillaSalida() {
        return casillaSalida;
    }
}
