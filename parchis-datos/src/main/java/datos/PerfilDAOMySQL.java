package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Todo el SQL de la tabla perfil está aquí. Usa sentencias preparadas (evitan la
 * inyección de SQL) y try-with-resources (cierra la conexión aunque haya error).
 * Cambiar de base de datos es cambiar esta clase.
 */
public class PerfilDAOMySQL implements IPerfilDAO {

    private static final String INSERTAR =
            "INSERT INTO perfil (nombre, color_preferido, avatar) VALUES (?, ?, ?)";
    private static final String BUSCAR =
            "SELECT nombre, color_preferido, avatar, partidas_jugadas, partidas_ganadas FROM perfil WHERE nombre = ?";

    private final ConexionBD conexionBD;

    public PerfilDAOMySQL(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(PerfilEntidad perfil) throws PersistenciaException {
        try (Connection conexion = conexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(INSERTAR)) {
            sentencia.setString(1, perfil.nombre());
            sentencia.setString(2, perfil.colorPreferido());
            sentencia.setInt(3, perfil.avatar());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el perfil de " + perfil.nombre(), e);
        }
    }

    @Override
    public Optional<PerfilEntidad> buscarPorNombre(String nombre) throws PersistenciaException {
        try (Connection conexion = conexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(BUSCAR)) {
            sentencia.setString(1, nombre);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return Optional.empty();
                }
                return Optional.of(new PerfilEntidad(fila.getString("nombre"), fila.getString("color_preferido"),
                        fila.getInt("avatar"), fila.getInt("partidas_jugadas"), fila.getInt("partidas_ganadas")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo leer el perfil de " + nombre, e);
        }
    }
}
