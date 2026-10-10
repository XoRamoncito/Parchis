package cliente.modelo;

import comun.ColorJugador;
import comun.EstadoJuego;
import negocio.IFachadaJuego;
import negocio.MovimientoInvalidoException;
import negocio.OyenteJuego;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Modelo del MVC del cliente. Guarda la partida tal como la ve este jugador
 * y es el sujeto que observa la vista. No aplica reglas: se las pide al
 * subsistema de negocio a través de la fachada, y cuando la fachada le avisa
 * de un estado nuevo lo guarda y notifica a sus observadores.
 */
public class ModeloJuego implements OyenteJuego {

    private final IFachadaJuego fachada;
    private final List<ObservadorJuego> observadores = new CopyOnWriteArrayList<>();
    private volatile ColorJugador miColor;
    private volatile EstadoJuego estado;

    public ModeloJuego(IFachadaJuego fachada) {
        this.fachada = fachada;
        fachada.registrarOyente(this);
    }

    /** Entra a la partida con el nombre dado; el sistema asigna el color. */
    public void unirse(String nombre) {
        miColor = fachada.unirJugador(nombre);
        // El primer estado pudo llegar antes de conocer mi color: se avisa otra vez.
        notificarObservadores();
    }

    public void lanzarDado() {
        if (!puedeLanzar()) {
            throw new MovimientoInvalidoException(esMiTurno() ? "Ya lanzaste: mueve una ficha" : "No es tu turno");
        }
        fachada.lanzarDado(miColor);
    }

    public void moverFicha(int numeroFicha) {
        if (!getFichasMovibles().contains(numeroFicha)) {
            throw new MovimientoInvalidoException("Esa ficha no se puede mover ahora");
        }
        fachada.moverFicha(miColor, numeroFicha);
    }

    /** Lo llama la fachada cada vez que la partida cambia, sea quien sea el que jugó. */
    @Override
    public void estadoCambiado(EstadoJuego nuevo) {
        estado = nuevo;
        notificarObservadores();
    }

    public void agregarObservador(ObservadorJuego observador) {
        observadores.add(observador);
    }

    public void quitarObservador(ObservadorJuego observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores() {
        for (ObservadorJuego observador : observadores) {
            observador.actualizar();
        }
    }

    /** Último estado recibido, o null si todavía no llega ninguno. */
    public EstadoJuego getEstado() {
        return estado;
    }

    public ColorJugador getMiColor() {
        return miColor;
    }

    public boolean esMiTurno() {
        EstadoJuego actual = estado;
        return actual != null && miColor != null && actual.getTurno() == miColor && actual.getGanador() == null;
    }

    /** true si a este jugador le toca lanzar el dado. */
    public boolean puedeLanzar() {
        return esMiTurno() && !estado.isDadoLanzado();
    }

    /** Fichas propias que se pueden mover ahora; vacía si no es mi turno. */
    public List<Integer> getFichasMovibles() {
        return esMiTurno() ? estado.getFichasMovibles() : new ArrayList<>();
    }
}
