package negocio.comunicacion.eventos;

import dtos.MensajeDTO;
import negocio.comunicacion.IComunicacion;
import negocio.comunicacion.ReceptorMensajes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Estilo basado en eventos, visto desde una computadora: publica eventos en el
 * bus y entrega a cada receptor los del tipo al que se suscribió. Nunca le
 * habla directamente a otra computadora.
 */
public class BusEventosRed implements IComunicacion {

    private Socket socket;
    private ObjectOutputStream salida;
    private final Map<String, List<ReceptorMensajes>> receptores = new ConcurrentHashMap<>();

    /**
     * Se conecta al bus. Si el bus es esta misma computadora y todavía nadie lo
     * abrió, lo abre aquí: así la primera computadora en entrar es la que tiene el bus.
     */
    @Override
    public void conectar(String host, int puerto) throws IOException {
        try {
            socket = new Socket(host, puerto);
        } catch (ConnectException e) {
            if (!esEstaComputadora(host)) {
                throw e;
            }
            new RelevoEventos(puerto).iniciarEnSegundoPlano();
            socket = new Socket(host, puerto);
        }
        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        Thread hilo = new Thread(() -> escuchar(entrada), "bus-eventos");
        hilo.setDaemon(true);
        hilo.start();
    }

    @Override
    public synchronized void enviar(MensajeDTO mensaje) {
        try {
            salida.writeObject(mensaje);
            salida.reset();
            salida.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void suscribirse(String tipo, ReceptorMensajes receptor) {
        receptores.computeIfAbsent(tipo, t -> new CopyOnWriteArrayList<>()).add(receptor);
    }

    @Override
    public void desconectar() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // ya estaba cerrado
        }
    }

    private static boolean esEstaComputadora(String host) {
        return host == null || host.isBlank() || host.equalsIgnoreCase("localhost") || host.startsWith("127.");
    }

    private void escuchar(ObjectInputStream entrada) {
        try {
            while (true) {
                MensajeDTO mensaje = (MensajeDTO) entrada.readObject();
                for (ReceptorMensajes receptor : receptores.getOrDefault(mensaje.getTipo(), List.of())) {
                    try {
                        receptor.recibir(mensaje);
                    } catch (RuntimeException e) {
                        System.err.println("Error al atender el evento " + mensaje + ": " + e);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Se cerró la conexión con el bus de eventos");
        }
    }
}
