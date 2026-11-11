package negocio.sala;

import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.SalaDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * El mismo escenario del CU-01 contra cualquier IFachadaSala (ejercicio 6).
 * Hoy lo cumple el mock; en el Hito 2B se hereda para probar el subsistema real.
 */
public abstract class ContratoFachadaSala {

    /** Una sala nueva y vacía. */
    protected abstract IFachadaSala crearFachada();

    @Test
    void flujoNormalRegistraAlJugadorYLoMuestraEnLaSala() throws SalaException {
        IFachadaSala sala = crearFachada();
        SalaDTO resultado = sala.registrarJugador(new JugadorDTO("Ramon", ColorJugador.ROJO, 2));
        assertEquals(1, resultado.getJugadores().size());
        assertEquals("Ramon", resultado.getJugadores().get(0).getNombre());
        assertEquals(ColorJugador.ROJO, resultado.getJugadores().get(0).getColor());
    }

    @Test
    void a1NombreVacioNoSeRegistra() {
        IFachadaSala sala = crearFachada();
        SalaException e = assertThrows(SalaException.class, () -> sala.registrarJugador(new JugadorDTO("  ", ColorJugador.ROJO, 1)));
        assertEquals(SalaException.Motivo.NOMBRE_INVALIDO, e.getMotivo());
        assertTrue(sala.consultarSala().getJugadores().isEmpty());
    }

    @Test
    void a2ColorOcupadoOfreceLosLibresYNoRegistra() throws SalaException {
        IFachadaSala sala = crearFachada();
        sala.registrarJugador(new JugadorDTO("Gustavo", ColorJugador.AZUL, 1));
        SalaException e = assertThrows(SalaException.class, () -> sala.registrarJugador(new JugadorDTO("Ramon", ColorJugador.AZUL, 2)));
        assertEquals(SalaException.Motivo.COLOR_OCUPADO, e.getMotivo());
        assertEquals(EnumSet.of(ColorJugador.ROJO, ColorJugador.VERDE, ColorJugador.AMARILLO), e.getColoresLibres());
        assertEquals(1, sala.consultarSala().getJugadores().size());
    }

    @Test
    void a3SalaLlenaNoRegistraAlQuintoJugador() throws SalaException {
        IFachadaSala sala = crearFachada();
        ColorJugador[] colores = ColorJugador.values();
        for (int i = 0; i < colores.length; i++) {
            sala.registrarJugador(new JugadorDTO("J" + i, colores[i], 1));
        }
        SalaException e = assertThrows(SalaException.class, () -> sala.registrarJugador(new JugadorDTO("Ramon", ColorJugador.ROJO, 1)));
        assertEquals(SalaException.Motivo.SALA_LLENA, e.getMotivo());
    }

    @Test
    void avisaALosObservadoresCuandoAlguienLlegaOSeMarcaListo() throws SalaException {
        IFachadaSala sala = crearFachada();
        List<SalaDTO> avisos = new ArrayList<>();
        sala.agregarObservador(avisos::add);
        sala.registrarJugador(new JugadorDTO("Ramon", ColorJugador.ROJO, 2));
        sala.marcarListo("Ramon");
        assertEquals(2, avisos.size());
        assertTrue(avisos.get(1).getJugadores().get(0).isListo());
    }
}
