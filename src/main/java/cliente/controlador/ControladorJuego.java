package cliente.controlador;

import cliente.modelo.ModeloJuego;
import cliente.vista.VistaJuego;
import negocio.MovimientoInvalidoException;

import java.io.UncheckedIOException;

/**
 * Controlador del MVC. Recibe las acciones del jugador desde la vista y las
 * convierte en operaciones del modelo.
 */
public class ControladorJuego {

    private final ModeloJuego modelo;
    private final VistaJuego vista;

    public ControladorJuego(ModeloJuego modelo, VistaJuego vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.setControlador(this);
    }

    public void lanzarDado() {
        try {
            modelo.lanzarDado();
        } catch (MovimientoInvalidoException e) {
            vista.mostrarError(e.getMessage());
        } catch (UncheckedIOException e) {
            vista.mostrarError("Se perdió la conexión con el servidor");
        }
    }

    /** Se acabó el tiempo para lanzar: el dado se tira solo. */
    public void tiempoAgotado() {
        if (modelo.puedeLanzar()) {
            lanzarDado();
        }
    }

    public void seleccionarFicha(int numeroFicha) {
        try {
            modelo.moverFicha(numeroFicha);
        } catch (MovimientoInvalidoException e) {
            vista.mostrarError(e.getMessage());
        } catch (UncheckedIOException e) {
            vista.mostrarError("Se perdió la conexión con el servidor");
        }
    }
}
