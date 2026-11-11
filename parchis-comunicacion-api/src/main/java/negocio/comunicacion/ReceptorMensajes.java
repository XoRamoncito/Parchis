package negocio.comunicacion;

import dtos.MensajeDTO;

/** Lo implementa quien quiere recibir los mensajes de un tipo. */
public interface ReceptorMensajes {

    void recibir(MensajeDTO mensaje);
}
