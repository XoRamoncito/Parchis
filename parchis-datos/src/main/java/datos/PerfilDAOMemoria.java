package datos;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPerfilDAO que guarda los perfiles en un mapa en memoria, sin MySQL.
 * Sirve para probar el negocio (PerfilBO) sin base de datos. Es seguro con
 * varios hilos porque las pruebas de red registran jugadores en paralelo.
 */
public class PerfilDAOMemoria implements IPerfilDAO {

    /** La "tabla" perfil: nombre -> fila. */
    private final Map<String, PerfilEntidad> tabla = new ConcurrentHashMap<>();

    @Override
    public void guardar(PerfilEntidad perfil) throws PersistenciaException {
        // Igual que la llave primaria de MySQL: no se permiten dos perfiles con el mismo nombre.
        if (tabla.putIfAbsent(perfil.nombre(), perfil) != null) {
            throw new PersistenciaException("Ya existe el perfil de " + perfil.nombre(), null);
        }
    }

    @Override
    public Optional<PerfilEntidad> buscarPorNombre(String nombre) {
        return Optional.ofNullable(tabla.get(nombre));
    }

    @Override
    public void registrarPartida(String nombre, boolean gano) throws PersistenciaException {
        // PerfilEntidad es inmutable (record): se reemplaza la fila por una con las estadísticas nuevas.
        // computeIfPresent lo hace de forma atómica, como el UPDATE de MySQL.
        PerfilEntidad actualizado = tabla.computeIfPresent(nombre, (clave, p) -> new PerfilEntidad(
                p.nombre(), p.colorPreferido(), p.avatar(),
                p.partidasJugadas() + 1, p.partidasGanadas() + (gano ? 1 : 0)));
        if (actualizado == null) {
            throw new PersistenciaException("No existe el perfil de " + nombre, null);
        }
    }
}
