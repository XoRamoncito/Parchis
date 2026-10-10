package servidor;

import comun.ColorJugador;
import comun.Mensaje;
import negocio.IFachadaJuego;
import negocio.MovimientoInvalidoException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Atiende a un cliente conectado: lee lo que pide y lo traduce a llamadas a
 * la fachada. No conoce las reglas del juego.
 */
public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final IFachadaJuego fachada;
    private final ServidorParchis servidor;
    private ObjectOutputStream salida;
    private volatile ColorJugador color;

    public ManejadorCliente(Socket socket, IFachadaJuego fachada, ServidorParchis servidor) {
        this.socket = socket;
        this.fachada = fachada;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            while (true) {
                atender((Mensaje) entrada.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Se desconectó " + (color != null ? color.getNombre() : "un cliente"));
        } finally {
            servidor.quitarCliente(this);
            cerrar();
        }
    }

    private void atender(Mensaje mensaje) {
        try {
            switch (mensaje.getTipo()) {
                case UNIRSE -> {
                    ColorJugador asignado = fachada.unirJugador(mensaje.getTexto());
                    enviar(Mensaje.bienvenida(asignado));
                    color = asignado;
                    enviar(Mensaje.estado(fachada.obtenerEstado()));
                    System.out.println(mensaje.getTexto() + " entró como " + asignado.getNombre());
                }
                case LANZAR_DADO -> fachada.lanzarDado(color);
                case MOVER_FICHA -> fachada.moverFicha(color, mensaje.getNumeroFicha());
                default -> System.out.println("Mensaje inesperado: " + mensaje.getTipo());
            }
        } catch (MovimientoInvalidoException e) {
            enviar(Mensaje.error(e.getMessage()));
        }
    }

    /** true cuando el cliente ya está en la partida. */
    public boolean estaUnido() {
        return color != null;
    }

    public synchronized void enviar(Mensaje mensaje) {
        try {
            salida.writeObject(mensaje);
            salida.reset();
            salida.flush();
        } catch (IOException e) {
            cerrar();
        }
    }

    private void cerrar() {
        try {
            socket.close();
        } catch (IOException e) {
            // ya estaba cerrado
        }
    }
}
