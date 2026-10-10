package comun;

import java.io.Serializable;

/** Lo que viaja por el socket entre un cliente y el servidor. */
public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Puerto en el que escucha el servidor si no se indica otro. */
    public static final int PUERTO = 5000;

    public enum Tipo {
        /** cliente → servidor: quiero entrar a la partida (texto = nombre). */
        UNIRSE,
        /** servidor → cliente: entraste, este es tu color. */
        BIENVENIDA,
        /** cliente → servidor: lanzo el dado. */
        LANZAR_DADO,
        /** cliente → servidor: muevo la ficha indicada. */
        MOVER_FICHA,
        /** servidor → todos los clientes: así quedó la partida. */
        ESTADO,
        /** servidor → cliente: no se pudo hacer lo que pediste (texto = motivo). */
        ERROR
    }

    private final Tipo tipo;
    private final String texto;
    private final ColorJugador color;
    private final int numeroFicha;
    private final EstadoJuego estado;

    private Mensaje(Tipo tipo, String texto, ColorJugador color, int numeroFicha, EstadoJuego estado) {
        this.tipo = tipo;
        this.texto = texto;
        this.color = color;
        this.numeroFicha = numeroFicha;
        this.estado = estado;
    }

    public static Mensaje unirse(String nombre) {
        return new Mensaje(Tipo.UNIRSE, nombre, null, 0, null);
    }

    public static Mensaje bienvenida(ColorJugador color) {
        return new Mensaje(Tipo.BIENVENIDA, null, color, 0, null);
    }

    public static Mensaje lanzarDado(ColorJugador jugador) {
        return new Mensaje(Tipo.LANZAR_DADO, null, jugador, 0, null);
    }

    public static Mensaje moverFicha(ColorJugador jugador, int numeroFicha) {
        return new Mensaje(Tipo.MOVER_FICHA, null, jugador, numeroFicha, null);
    }

    public static Mensaje estado(EstadoJuego estado) {
        return new Mensaje(Tipo.ESTADO, null, null, 0, estado);
    }

    public static Mensaje error(String motivo) {
        return new Mensaje(Tipo.ERROR, motivo, null, 0, null);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getTexto() {
        return texto;
    }

    public ColorJugador getColor() {
        return color;
    }

    public int getNumeroFicha() {
        return numeroFicha;
    }

    public EstadoJuego getEstado() {
        return estado;
    }
}
