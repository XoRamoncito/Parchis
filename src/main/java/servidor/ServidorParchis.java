package servidor;

import comun.EstadoJuego;
import comun.Mensaje;
import negocio.IFachadaJuego;
import negocio.OyenteJuego;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Acepta a los jugadores por socket: cada vez
 * que la partida cambia, reenvía el estado nuevo a todos los clientes.
 */
public class ServidorParchis implements OyenteJuego {

    private final int puerto;
    private final IFachadaJuego fachada;
    private final List<ManejadorCliente> clientes = new CopyOnWriteArrayList<>();

    public ServidorParchis(int puerto, IFachadaJuego fachada) {
        this.puerto = puerto;
        this.fachada = fachada;
        fachada.registrarOyente(this);
    }

    /** Espera conexiones; cada cliente se atiende en su propio hilo. */
    public void iniciar() throws IOException {
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor de Parchís escuchando en el puerto " + puerto);
            while (true) {
                Socket socket = servidor.accept();
                ManejadorCliente manejador = new ManejadorCliente(socket, fachada, this);
                clientes.add(manejador);
                new Thread(manejador, "cliente-" + clientes.size()).start();
            }
        }
    }

    @Override
    public void estadoCambiado(EstadoJuego estado) {
        difundir(estado);
    }

    /** Manda el estado a todos los clientes que ya entraron a la partida. */
    public void difundir(EstadoJuego estado) {
        Mensaje mensaje = Mensaje.estado(estado);
        for (ManejadorCliente cliente : clientes) {
            if (cliente.estaUnido()) {
                cliente.enviar(mensaje);
            }
        }
    }

    public void quitarCliente(ManejadorCliente cliente) {
        clientes.remove(cliente);
    }
}
