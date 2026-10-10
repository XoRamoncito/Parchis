package cliente.red;

import comun.ColorJugador;
import comun.EstadoJuego;
import comun.Mensaje;
import negocio.IFachadaJuego;
import negocio.MovimientoInvalidoException;
import negocio.OyenteJuego;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cumple la misma interfaz que la del servidor, pero en vez de aplicar reglas
 * manda cada petición por el socket avisa a sus oyentes cuando el servidor difunde
 * un estado nuevo.
 */
public class FachadaJuegoRemota implements IFachadaJuego {

    private final Socket socket;
    private final ObjectOutputStream salida;
    private final ObjectInputStream entrada;
    private final List<OyenteJuego> oyentes = new CopyOnWriteArrayList<>();
    private volatile EstadoJuego ultimoEstado;

    public FachadaJuegoRemota(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public ColorJugador unirJugador(String nombre) {
        enviar(Mensaje.unirse(nombre));
        Mensaje respuesta = leer();
        if (respuesta.getTipo() == Mensaje.Tipo.ERROR) {
            throw new MovimientoInvalidoException(respuesta.getTexto());
        }
        Thread hilo = new Thread(this::escuchar, "escucha-servidor");
        hilo.setDaemon(true);
        hilo.start();
        return respuesta.getColor();
    }

    @Override
    public void lanzarDado(ColorJugador jugador) {
        enviar(Mensaje.lanzarDado(jugador));
    }

    @Override
    public void moverFicha(ColorJugador jugador, int numeroFicha) {
        enviar(Mensaje.moverFicha(jugador, numeroFicha));
    }

    @Override
    public EstadoJuego obtenerEstado() {
        return ultimoEstado;
    }

    @Override
    public void registrarOyente(OyenteJuego oyente) {
        oyentes.add(oyente);
    }

    /** Corre en su propio hilo: recibe los estados que difunde el servidor. */
    private void escuchar() {
        try {
            while (true) {
                Mensaje mensaje = leer();
                if (mensaje.getTipo() == Mensaje.Tipo.ESTADO) {
                    ultimoEstado = mensaje.getEstado();
                    for (OyenteJuego oyente : oyentes) {
                        oyente.estadoCambiado(ultimoEstado);
                    }
                }
            }
        } catch (UncheckedIOException e) {
            System.out.println("Se perdió la conexión con el servidor");
        }
    }

    private synchronized void enviar(Mensaje mensaje) {
        try {
            salida.writeObject(mensaje);
            salida.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Mensaje leer() {
        try {
            return (Mensaje) entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new UncheckedIOException(new IOException(e));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
