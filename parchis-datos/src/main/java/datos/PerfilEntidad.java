package datos;

/**
 * Una fila de la tabla perfil. Solo la usan la capa de datos y el objeto de
 * negocio; la presentación nunca la ve (recibe un PerfilDTO).
 */
public record PerfilEntidad(String nombre, String colorPreferido, int avatar, int partidasJugadas, int partidasGanadas) {

    /** Un perfil nuevo: las estadísticas empiezan en cero. */
    public static PerfilEntidad nuevo(String nombre, String colorPreferido, int avatar) {
        return new PerfilEntidad(nombre, colorPreferido, avatar, 0, 0);
    }
}
