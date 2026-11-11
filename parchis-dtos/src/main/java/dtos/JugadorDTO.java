package dtos;

import java.io.Serializable;

/** Los datos de un jugador que viajan entre capas: lo que captura en el registro y si ya está listo. */
public class JugadorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final ColorJugador color;
    private final int avatar;
    private final boolean listo;

    public JugadorDTO(String nombre, ColorJugador color, int avatar) {
        this(nombre, color, avatar, false);
    }

    public JugadorDTO(String nombre, ColorJugador color, int avatar, boolean listo) {
        this.nombre = nombre;
        this.color = color;
        this.avatar = avatar;
        this.listo = listo;
    }

    public String getNombre() {
        return nombre;
    }

    public ColorJugador getColor() {
        return color;
    }

    public int getAvatar() {
        return avatar;
    }

    public boolean isListo() {
        return listo;
    }

    /** Copia del jugador marcado como listo. */
    public JugadorDTO comoListo() {
        return new JugadorDTO(nombre, color, avatar, true);
    }
}
