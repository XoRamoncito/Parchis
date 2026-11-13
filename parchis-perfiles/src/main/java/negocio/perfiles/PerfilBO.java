package negocio.perfiles;

import datos.IPerfilDAO;
import datos.PerfilEntidad;
import datos.PersistenciaException;
import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.PerfilDTO;

import java.util.Optional;

/**
 * Objeto de negocio del perfil: aplica las reglas sin escribir SQL. Usa el DAO
 * por su interfaz (en una prueba se le puede pasar uno en memoria), convierte
 * entre entidad y DTO, y traduce los errores técnicos a errores de negocio.
 */
public class PerfilBO {

    private final IPerfilDAO dao;

    public PerfilBO(IPerfilDAO dao) {
        this.dao = dao;
    }

    /**
     * Si el nombre ya está guardado en esta computadora, devuelve ese perfil; si
     * no, crea uno nuevo con el color y el avatar elegidos, lo guarda y lo devuelve.
     */
    public PerfilDTO obtenerOCrear(JugadorDTO jugador) throws NegocioException {
        try {
            Optional<PerfilEntidad> guardado = dao.buscarPorNombre(jugador.getNombre());
            if (guardado.isPresent()) {
                return aDTO(guardado.get());
            }
            PerfilEntidad nuevo = PerfilEntidad.nuevo(jugador.getNombre(), jugador.getColor().name(), jugador.getAvatar());
            dao.guardar(nuevo);
            return aDTO(nuevo);
        } catch (PersistenciaException e) {
            throw new NegocioException("No se pudo obtener el perfil de " + jugador.getNombre(), e);
        }
    }

    private static PerfilDTO aDTO(PerfilEntidad entidad) {
        return new PerfilDTO(entidad.nombre(), ColorJugador.valueOf(entidad.colorPreferido()), entidad.avatar(),
                entidad.partidasJugadas(), entidad.partidasGanadas());
    }
}
