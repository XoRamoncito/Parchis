package negocio.comunicacion;

import dtos.MensajeDTO;

import java.io.IOException;

/**
 * Contrato del subsistema de comunicación. El resto del sistema no sabe qué
 * estilo hay detrás; el equipo 3 lo implementa con un bus de eventos.
 */
public interface IComunicacion {

    void conectar(String host, int puerto) throws IOException;

    /** Envía el mensaje a todos los que estén suscritos a su tipo. */
    void enviar(MensajeDTO mensaje);

    /** Recibir lo llama un hilo de red: quien toque la interfaz gráfica debe usar invokeLater. */
    void suscribirse(String tipo, ReceptorMensajes receptor);

    void desconectar();
}
