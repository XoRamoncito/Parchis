package negocio.perfiles;

import datos.IPerfilDAO;
import datos.PerfilDAOMemoria;
import datos.PerfilEntidad;
import datos.PersistenciaException;
import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.PerfilDTO;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** PerfilBO probado sin MySQL, con PerfilDAOMemoria. */
class PerfilBOTest {

    @Test
    void creaElPerfilLaPrimeraVezConEstadisticasEnCero() throws Exception {
        PerfilDAOMemoria dao = new PerfilDAOMemoria();
        PerfilDTO perfil = new PerfilBO(dao).obtenerOCrear(new JugadorDTO("Ramon", ColorJugador.ROJO, 2));
        assertEquals(ColorJugador.ROJO, perfil.getColorPreferido());
        assertEquals(0, perfil.getPartidasJugadas());
        assertTrue(dao.buscarPorNombre("Ramon").isPresent());
    }

    @Test
    void siYaExisteDevuelveElGuardado() throws Exception {
        PerfilDAOMemoria dao = new PerfilDAOMemoria();
        dao.guardar(new PerfilEntidad("Gustavo", "AZUL", 3, 7, 2));
        PerfilDTO perfil = new PerfilBO(dao).obtenerOCrear(new JugadorDTO("Gustavo", ColorJugador.VERDE, 1));
        assertEquals(ColorJugador.AZUL, perfil.getColorPreferido());
        assertEquals(7, perfil.getPartidasJugadas());
        assertEquals(3, dao.buscarPorNombre("Gustavo").orElseThrow().avatar());
    }

    @Test
    void traduceElErrorTecnicoAErrorDeNegocio() {
        IPerfilDAO roto = new IPerfilDAO() {
            @Override
            public void guardar(PerfilEntidad perfil) throws PersistenciaException {
                throw new PersistenciaException("sin base", null);
            }

            @Override
            public Optional<PerfilEntidad> buscarPorNombre(String nombre) throws PersistenciaException {
                throw new PersistenciaException("sin base", null);
            }

            @Override
            public void registrarPartida(String nombre, boolean gano) throws PersistenciaException {
                throw new PersistenciaException("sin base", null);
            }
        };
        assertThrows(NegocioException.class,
                () -> new PerfilBO(roto).obtenerOCrear(new JugadorDTO("Ramon", ColorJugador.ROJO, 1)));
    }
}
