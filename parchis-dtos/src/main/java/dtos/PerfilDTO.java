package dtos;

import java.io.Serializable;

/**
 * El perfil guardado de un jugador, como lo ve la presentación: nombre, color
 * preferido, avatar y sus estadísticas (empiezan en cero; se actualizan en el CU-05).
 */
public class PerfilDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final ColorJugador colorPreferido;
    private final int avatar;
    private final int partidasJugadas;
    private final int partidasGanadas;

    public PerfilDTO(String nombre, ColorJugador colorPreferido, int avatar, int partidasJugadas, int partidasGanadas) {
        this.nombre = nombre;
        this.colorPreferido = colorPreferido;
        this.avatar = avatar;
        this.partidasJugadas = partidasJugadas;
        this.partidasGanadas = partidasGanadas;
    }

    public String getNombre() {
        return nombre;
    }

    public ColorJugador getColorPreferido() {
        return colorPreferido;
    }

    public int getAvatar() {
        return avatar;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public int getPartidasGanadas() {
        return partidasGanadas;
    }
}
