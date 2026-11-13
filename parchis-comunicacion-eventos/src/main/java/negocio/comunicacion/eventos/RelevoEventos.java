package negocio.comunicacion.eventos;

import dtos.MensajeDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * El tramo de red del bus. No conoce el juego: recibe cada evento, le pone un
 * número y lo reparte a todas las computadoras conectadas, una por una. Así
 * todas reciben los eventos en el mismo orden (el riesgo principal del estilo).
 * Además guarda los eventos ya repartidos: a quien se conecta tarde se los
 * entrega primero, en el mismo orden, para que su copia quede igual a las demás.
 */
public class RelevoEventos {

    public static final int PUERTO = 5100;

    private final int puerto;
    private final List<ObjectOutputStream> conectados = new CopyOnWriteArrayList<>();
    private final List<MensajeDTO> historial = new ArrayList<>();
    private final CountDownLatch escuchando = new CountDownLatch(1);
    private long secuencia;
    private volatile ServerSocket servidor;

    public RelevoEventos(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() throws IOException {
        try {
            servidor = new ServerSocket(puerto);
        } finally {
            escuchando.countDown();
        }
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
            ponerAlDia(salida);
            Thread hilo = new Thread(() -> atender(socket, salida), "relevo-" + conectados.size());
            hilo.setDaemon(true);
            hilo.start();
        }
    }

    /** Arranca el bus en un hilo aparte y regresa cuando ya acepta conexiones. */
    public void iniciarEnSegundoPlano() throws IOException {
        Thread hilo = new Thread(() -> {
            try {
                iniciar();
            } catch (IOException e) {
                System.err.println("No se pudo abrir el bus en el puerto " + puerto + ": " + e.getMessage());
            }
        }, "bus-eventos-relevo");
        hilo.setDaemon(true);
        hilo.start();
        try {
            escuchando.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (servidor == null) {
            throw new IOException("No se pudo abrir el bus en el puerto " + puerto);
        }
    }

    /** A quien se acaba de conectar le manda primero lo que ya pasó, y luego queda en la lista. */
    private synchronized void ponerAlDia(ObjectOutputStream salida) throws IOException {
        for (MensajeDTO anterior : historial) {
            salida.writeObject(anterior);
        }
        salida.reset();
        salida.flush();
        conectados.add(salida);
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
        MensajeDTO numerado = mensaje.conSecuencia(++secuencia);
        historial.add(numerado);
        for (ObjectOutputStream salida : conectados) {
            try {
                salida.writeObject(numerado);
                salida.reset();
                salida.flush();
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
