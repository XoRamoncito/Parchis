package negocio.perfiles;

import datos.IPerfilDAO;
import datos.PerfilEntidad;
import datos.PersistenciaException;
import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.PerfilDTO;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** PerfilBO probado sin MySQL: el DAO de la prueba guarda en un mapa. */
class PerfilBOTest {

    /** DAO de prueba, solo para esta clase. */
    private static class DaoEnMapa implements IPerfilDAO {
        final Map<String, PerfilEntidad> tabla = new HashMap<>();

        @Override
        public void guardar(PerfilEntidad perfil) {
            tabla.put(perfil.nombre(), perfil);
        }

        @Override
        public Optional<PerfilEntidad> buscarPorNombre(String nombre) {
            return Optional.ofNullable(tabla.get(nombre));
        }

        @Override
        public void registrarPartida(String nombre, boolean gano) {
            PerfilEntidad p = tabla.get(nombre);
            tabla.put(nombre, new PerfilEntidad(p.nombre(), p.colorPreferido(), p.avatar(),
                    p.partidasJugadas() + 1, p.partidasGanadas() + (gano ? 1 : 0)));
        }
    }

    @Test
    void creaElPerfilLaPrimeraVezConEstadisticasEnCero() throws NegocioException {
        DaoEnMapa dao = new DaoEnMapa();
        PerfilDTO perfil = new PerfilBO(dao).obtenerOCrear(new JugadorDTO("Ramon", ColorJugador.ROJO, 2));
        assertEquals(ColorJugador.ROJO, perfil.getColorPreferido());
        assertEquals(0, perfil.getPartidasJugadas());
        assertEquals(1, dao.tabla.size());
    }

    @Test
    void siYaExisteDevuelveElGuardado() throws NegocioException {
        DaoEnMapa dao = new DaoEnMapa();
        dao.guardar(new PerfilEntidad("Gustavo", "AZUL", 3, 7, 2));
        PerfilDTO perfil = new PerfilBO(dao).obtenerOCrear(new JugadorDTO("Gustavo", ColorJugador.VERDE, 1));
        assertEquals(ColorJugador.AZUL, perfil.getColorPreferido());
        assertEquals(7, perfil.getPartidasJugadas());
        assertEquals(1, dao.tabla.size());
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
