package negocio.sala.impl;

import dtos.MensajeDTO;
import negocio.comunicacion.IComunicacion;
import negocio.comunicacion.ReceptorMensajes;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Bus de prueba dentro del mismo programa: numera y entrega al momento, en orden. */
class ComunicacionEnMemoria implements IComunicacion {

    private final Map<String, List<ReceptorMensajes>> receptores = new ConcurrentHashMap<>();
    private long secuencia;

    @Override
    public void conectar(String host, int puerto) {
    }

    @Override
    public synchronized void enviar(MensajeDTO mensaje) {
        MensajeDTO numerado = mensaje.conSecuencia(++secuencia);
        for (ReceptorMensajes receptor : receptores.getOrDefault(mensaje.getTipo(), List.of())) {
            receptor.recibir(numerado);
        }
    }

    @Override
    public void suscribirse(String tipo, ReceptorMensajes receptor) {
        receptores.computeIfAbsent(tipo, t -> new CopyOnWriteArrayList<>()).add(receptor);
    }

    @Override
    public void desconectar() {
    }
}
