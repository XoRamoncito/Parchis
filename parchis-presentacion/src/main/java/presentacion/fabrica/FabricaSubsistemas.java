package presentacion.fabrica;

import datos.ConexionBD;
import datos.PerfilDAOMySQL;
import datos.PersistenciaException;
import negocio.comunicacion.IComunicacion;
import negocio.comunicacion.eventos.BusEventosRed;
import negocio.comunicacion.eventos.RelevoEventos;
import negocio.perfiles.PerfilBO;
import negocio.sala.IFachadaSala;
import negocio.sala.impl.FachadaSala;

import java.io.IOException;
import java.nio.file.Path;

/**
 * El único lugar que conoce las clases concretas. Las pantallas y el control
 * piden interfaces. En el Hito 2B la fábrica ya no crea el mock sino el
 * subsistema Sala real, con su comunicación por eventos y el perfil en MySQL.
 */
public final class FabricaSubsistemas {

    private FabricaSubsistemas() {
    }

    /**
     * Arma el subsistema Sala: se conecta al bus de eventos en hostBus (si es
     * esta computadora y nadie lo ha abierto, lo abre) y a MySQL con
     * parchis-bd.properties.
     */
    public static IFachadaSala crearFachadaSala(String hostBus) throws IOException, PersistenciaException {
        ConexionBD baseDeDatos = ConexionBD.desdeArchivo(Path.of(ConexionBD.ARCHIVO));
        baseDeDatos.probar();
        PerfilBO perfiles = new PerfilBO(new PerfilDAOMySQL(baseDeDatos));
        IComunicacion comunicacion = crearComunicacion();
        FachadaSala sala = new FachadaSala(comunicacion, perfiles);
        comunicacion.conectar(hostBus, RelevoEventos.PUERTO);
        return sala;
    }

    /** Comunicación con el estilo del equipo 3: basado en eventos. */
    private static IComunicacion crearComunicacion() {
        return new BusEventosRed();
    }
}
