package presentacion;

import dtos.ColorJugador;
import dtos.JugadorDTO;
import dtos.SalaDTO;
import negocio.sala.IFachadaSala;
import negocio.sala.ObservadorSala;
import negocio.sala.SalaException;

import javax.swing.SwingUtilities;

/**
 * Control del CU-01 Registrarse en la sala. Recibe lo que hace el jugador en
 * las pantallas y se lo pide a la fachada de la sala. Solo conoce IFachadaSala:
 * no sabe si detrás está el mock o el subsistema real.
 */
public class ControlSala implements ObservadorSala {

    private final IFachadaSala fachada;
    private final FormRegistro formRegistro;
    private final FormSalaEspera formSala;
    private String miNombre;

    public ControlSala(IFachadaSala fachada) {
        this.fachada = fachada;
        this.formRegistro = new FormRegistro(this);
        this.formSala = new FormSalaEspera(this);
        fachada.agregarObservador(this);
    }

    public void iniciar() {
        formRegistro.setVisible(true);
    }

    /** El jugador presionó "Registrar perfil". */
    public void registrar(String nombre, ColorJugador color, int avatar) {
        try {
            SalaDTO sala = fachada.registrarJugador(new JugadorDTO(nombre, color, avatar));
            miNombre = nombre.trim();
            formRegistro.setVisible(false);
            formSala.mostrar(sala, miNombre);
            formSala.setLocation(formRegistro.getLocation());
            formSala.setVisible(true);
        } catch (SalaException e) {
            formRegistro.mostrarError(e.getMessage(), e.getColoresLibres());
        } catch (IllegalStateException e) {
            formRegistro.mostrarError(e.getMessage(), java.util.Set.of());
        }
    }

    /** El jugador presionó "Confirmar estado". */
    public void confirmarEstado() {
        try {
            fachada.marcarListo(miNombre);
        } catch (SalaException e) {
            formSala.mostrarError(e.getMessage());
        }
    }

    /** Aviso de la sala: alguien llegó o se marcó listo. Puede llegar desde otro hilo. */
    @Override
    public void salaActualizada(SalaDTO sala) {
        SwingUtilities.invokeLater(() -> {
            if (miNombre != null) {
                formSala.mostrar(sala, miNombre);
            }
        });
    }

    FormRegistro getFormRegistro() {
        return formRegistro;
    }

    FormSalaEspera getFormSala() {
        return formSala;
    }
}
