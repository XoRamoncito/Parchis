package negocio.perfiles;

import datos.PerfilDAOMemoria;
import datos.PersistenciaException;
import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.PerfilDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ejercicio 7: PerfilBO probado con PerfilDAOMemoria, sin MySQL.
 * El negocio no se entera de dónde se guardan los perfiles porque solo conoce IPerfilDAO.
 */
class PerfilBOConMemoriaTest {

    @Test
    void obtenerOCrearEmpiezaEnCeroYRegistrarPartidaSumaLasEstadisticas()
            throws NegocioException, PersistenciaException {
        PerfilDAOMemoria dao = new PerfilDAOMemoria();
        PerfilBO bo = new PerfilBO(dao);
        JugadorDTO gustavo = new JugadorDTO("Gustavo", ColorJugador.AZUL, 3);

        // La primera vez se crea el perfil con las estadísticas en cero.
        PerfilDTO nuevo = bo.obtenerOCrear(gustavo);
        assertEquals("Gustavo", nuevo.getNombre());
        assertEquals(ColorJugador.AZUL, nuevo.getColorPreferido());
        assertEquals(0, nuevo.getPartidasJugadas());
        assertEquals(0, nuevo.getPartidasGanadas());

        // Juega dos partidas: gana una y pierde otra.
        dao.registrarPartida("Gustavo", true);
        dao.registrarPartida("Gustavo", false);

        // Al volver a obtenerlo se ven los cambios: 2 jugadas y 1 ganada.
        PerfilDTO despues = bo.obtenerOCrear(gustavo);
        assertEquals(2, despues.getPartidasJugadas());
        assertEquals(1, despues.getPartidasGanadas());
    }
}
