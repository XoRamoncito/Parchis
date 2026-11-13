package datos;

import java.util.Optional;

/** Contrato de acceso a los perfiles. El negocio solo conoce esta interfaz, no la base de datos. */
public interface IPerfilDAO {

    /** Inserta un perfil nuevo. */
    void guardar(PerfilEntidad perfil) throws PersistenciaException;

    /** El perfil guardado con ese nombre, o vacío si no existe. */
    Optional<PerfilEntidad> buscarPorNombre(String nombre) throws PersistenciaException;

    /**
     * Suma una partida jugada al perfil y, si la ganó, también una ganada.
     * Falla si no existe un perfil con ese nombre.
     */
    void registrarPartida(String nombre, boolean gano) throws PersistenciaException;
}
