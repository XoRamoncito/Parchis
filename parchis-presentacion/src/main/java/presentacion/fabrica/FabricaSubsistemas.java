package presentacion.fabrica;

import negocio.comunicacion.IComunicacion;
import negocio.comunicacion.eventos.BusEventosRed;
import negocio.sala.IFachadaSala;
import negocio.sala.mock.FachadaSalaMock;

/**
 * El único lugar que conoce las clases concretas. Las pantallas y el control
 * piden interfaces; cambiar el mock por el subsistema real (Hito 2B) es
 * cambiar USAR_MOCK aquí.
 */
public final class FabricaSubsistemas {

    /** Hito 2A: los subsistemas de primer nivel son mocks. */
    public static final boolean USAR_MOCK = true;

    private FabricaSubsistemas() {
    }

    public static IFachadaSala crearFachadaSala() {
        if (USAR_MOCK) {
            return new FachadaSalaMock();
        }
        throw new UnsupportedOperationException("El subsistema Sala real llega en el Hito 2B");
    }

    /** Comunicación con el estilo del equipo 3: basado en eventos. */
    public static IComunicacion crearComunicacion() {
        return new BusEventosRed();
    }
}
