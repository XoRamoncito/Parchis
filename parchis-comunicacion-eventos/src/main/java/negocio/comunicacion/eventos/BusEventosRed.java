package negocio.comunicacion.eventos;

import dtos.MensajeDTO;
import negocio.comunicacion.IComunicacion;
import negocio.comunicacion.ReceptorMensajes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
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

    @Override
    public void conectar(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
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

    private void escuchar(ObjectInputStream entrada) {
        try {
            while (true) {
                MensajeDTO mensaje = (MensajeDTO) entrada.readObject();
                for (ReceptorMensajes receptor : receptores.getOrDefault(mensaje.getTipo(), List.of())) {
                    receptor.recibir(mensaje);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Se cerró la conexión con el bus de eventos");
        }
    }
}
