package negocio.comunicacion.eventos;

import dtos.MensajeDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * El tramo de red del bus. No conoce el juego: recibe cada evento, le pone un
 * número y lo reparte a todas las computadoras conectadas, una por una. Así
 * todas reciben los eventos en el mismo orden (el riesgo principal del estilo).
 */
public class RelevoEventos {

    public static final int PUERTO = 5100;

    private final int puerto;
    private final List<ObjectOutputStream> conectados = new CopyOnWriteArrayList<>();
    private final AtomicLong secuencia = new AtomicLong();
    private ServerSocket servidor;

    public RelevoEventos(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() throws IOException {
        servidor = new ServerSocket(puerto);
        System.out.println("Bus de eventos escuchando en el puerto " + puerto);
        while (!servidor.isClosed()) {
            Socket socket;
            try {
                socket = servidor.accept();
            } catch (IOException e) {
                return;
            }
            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            conectados.add(salida);
            new Thread(() -> atender(socket, salida), "relevo-" + conectados.size()).start();
        }
    }

    private void atender(Socket socket, ObjectOutputStream salida) {
        try (ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                repartir((MensajeDTO) entrada.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            conectados.remove(salida);
        }
    }

    private synchronized void repartir(MensajeDTO mensaje) {
        MensajeDTO numerado = mensaje.conSecuencia(secuencia.incrementAndGet());
        for (ObjectOutputStream salida : conectados) {
            try {
                synchronized (salida) {
                    salida.writeObject(numerado);
                    salida.reset();
                    salida.flush();
                }
            } catch (IOException e) {
                conectados.remove(salida);
            }
        }
    }

    public void detener() throws IOException {
        if (servidor != null) {
            servidor.close();
        }
    }
}
