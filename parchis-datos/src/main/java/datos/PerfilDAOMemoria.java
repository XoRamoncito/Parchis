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
}
