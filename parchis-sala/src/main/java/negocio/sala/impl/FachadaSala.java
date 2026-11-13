package negocio.sala.impl;

import dtos.JugadorDTO;
import dtos.MensajeDTO;
import dtos.SalaDTO;
import negocio.comunicacion.IComunicacion;
import negocio.perfiles.NegocioException;
import negocio.perfiles.PerfilBO;
import negocio.sala.IFachadaSala;
import negocio.sala.ObservadorSala;
import negocio.sala.SalaException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Subsistema Sala real. Implementa la misma IFachadaSala que el mock (la
 * interfaz no cambió). Cada computadora tiene su copia de la sala (EstadoSala).
 *
 * Cómo se resuelve el riesgo del estilo (el orden de los eventos): nadie se
 * agrega a la sala directamente. Quien se registra publica una solicitud en el
 * bus; el bus numera los eventos y todas las computadoras los reciben en el
 * mismo orden y les aplican las mismas validaciones. Así, si dos eligen el
 * mismo color al mismo tiempo, en todas las copias gana el que llegó primero
 * al bus y el otro recibe el flujo alterno A2.
 */
public class FachadaSala implements IFachadaSala {

    public static final String SOLICITUD_REGISTRO = "SolicitudRegistro";
    public static final String SOLICITUD_LISTO = "SolicitudListo";
    private static final long ESPERA_SEGUNDOS = 5;

    private final EstadoSala estado;
    private final List<ValidacionRegistro> validaciones;
    private final IComunicacion comunicacion;
    private final PerfilBO perfiles;
    private final List<ObservadorSala> observadores = new CopyOnWriteArrayList<>();
    private final String idAplicacion = UUID.randomUUID().toString().substring(0, 8);
    private final AtomicLong contador = new AtomicLong();
    private final Map<String, CompletableFuture<Optional<SalaException>>> pendientes = new ConcurrentHashMap<>();

    public FachadaSala(IComunicacion comunicacion, PerfilBO perfiles) {
        this(comunicacion, perfiles, "AX7-B9Q");
    }

    public FachadaSala(IComunicacion comunicacion, PerfilBO perfiles, String codigo) {
        this.comunicacion = comunicacion;
        this.perfiles = perfiles;
        this.estado = new EstadoSala(codigo);
        this.validaciones = List.of(new NombreValido(), new SalaConLugar(), new ColorLibre());
        comunicacion.suscribirse(SOLICITUD_REGISTRO, this::aplicarRegistro);
        comunicacion.suscribirse(SOLICITUD_LISTO, this::aplicarListo);
    }

    @Override
    public SalaDTO registrarJugador(JugadorDTO jugador) throws SalaException {
        String nombre = jugador.getNombre() == null ? "" : jugador.getNombre().trim();
        JugadorDTO limpio = new JugadorDTO(nombre, jugador.getColor(), jugador.getAvatar());
        synchronized (estado) {
            validar(limpio); // aviso inmediato si con lo que ya se sabe no se puede
        }
        esperar(publicar(SOLICITUD_REGISTRO, limpio));
        try {
            perfiles.obtenerOCrear(limpio);
        } catch (NegocioException e) {
            System.err.println(e.getMessage() + ": " + e.getCause());
        }
        return consultarSala();
    }

    @Override
    public SalaDTO marcarListo(String nombre) throws SalaException {
        synchronized (estado) {
            if (nombre == null || estado.buscar(nombre) == null) {
                throw new SalaException(SalaException.Motivo.JUGADOR_NO_REGISTRADO, nombre + " no está en la sala.");
            }
        }
        esperar(publicar(SOLICITUD_LISTO, nombre.trim()));
        return consultarSala();
    }

    @Override
    public SalaDTO consultarSala() {
        synchronized (estado) {
            return estado.aSalaDTO();
        }
    }

    @Override
    public void agregarObservador(ObservadorSala observador) {
        observadores.add(observador);
    }

    private void validar(JugadorDTO jugador) throws SalaException {
        for (ValidacionRegistro validacion : validaciones) {
            validacion.validar(jugador, estado);
        }
    }

    /** Llega del bus, en el orden que puso el bus, igual en todas las computadoras. */
    private void aplicarRegistro(MensajeDTO evento) {
        JugadorDTO jugador = (JugadorDTO) evento.getDatos();
        SalaException rechazo = null;
        synchronized (estado) {
            try {
                validar(jugador);
                estado.agregar(jugador);
            } catch (SalaException e) {
                rechazo = e;
            }
        }
        terminar(evento.getOrigen(), rechazo);
    }

    private void aplicarListo(MensajeDTO evento) {
        String nombre = (String) evento.getDatos();
        SalaException rechazo = null;
        synchronized (estado) {
            if (estado.buscar(nombre) == null) {
                rechazo = new SalaException(SalaException.Motivo.JUGADOR_NO_REGISTRADO, nombre + " no está en la sala.");
            } else {
                estado.marcarListo(nombre);
            }
        }
        terminar(evento.getOrigen(), rechazo);
    }

    private String publicar(String tipo, java.io.Serializable datos) {
        String id = idAplicacion + "-" + contador.incrementAndGet();
        pendientes.put(id, new CompletableFuture<>());
        comunicacion.enviar(new MensajeDTO(tipo, id, datos));
        return id;
    }

    /** Si la solicitud era de esta computadora, le responde a quien la está esperando; y avisa a los observadores. */
    private void terminar(String origen, SalaException rechazo) {
        CompletableFuture<Optional<SalaException>> pendiente = pendientes.remove(origen);
        if (pendiente != null) {
            pendiente.complete(Optional.ofNullable(rechazo));
        }
        if (rechazo == null) {
            SalaDTO sala = consultarSala();
            for (ObservadorSala observador : observadores) {
                observador.salaActualizada(sala);
            }
        }
    }

    private void esperar(String id) throws SalaException {
        try {
            Optional<SalaException> rechazo = pendientes.getOrDefault(id, CompletableFuture.completedFuture(Optional.empty()))
                    .get(ESPERA_SEGUNDOS, TimeUnit.SECONDS);
            if (rechazo.isPresent()) {
                throw rechazo.get();
            }
        } catch (TimeoutException | ExecutionException e) {
            pendientes.remove(id);
            throw new IllegalStateException("El bus de eventos no respondió. Revisa la conexión.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Se interrumpió la espera del bus.", e);
        }
    }
}
