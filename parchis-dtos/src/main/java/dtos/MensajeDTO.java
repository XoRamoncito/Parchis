package dtos;

import java.io.Serializable;

/**
 * Lo que viaja entre computadoras: un tipo (por ejemplo "JugadorUnido") y sus datos.
 * En el estilo basado en eventos es el evento; quien lo envía no sabe quién lo recibe.
 */
public class MensajeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String tipo;
    private final String origen;
    private final Serializable datos;
    private final long secuencia;

    public MensajeDTO(String tipo, String origen, Serializable datos) {
        this(tipo, origen, datos, 0);
    }

    private MensajeDTO(String tipo, String origen, Serializable datos, long secuencia) {
        this.tipo = tipo;
        this.origen = origen;
        this.datos = datos;
        this.secuencia = secuencia;
    }

    /** Copia con el número de orden que le asigna el bus. */
    public MensajeDTO conSecuencia(long numero) {
        return new MensajeDTO(tipo, origen, datos, numero);
    }

    public String getTipo() {
        return tipo;
    }

    public String getOrigen() {
        return origen;
    }

    public Serializable getDatos() {
        return datos;
    }

    public long getSecuencia() {
        return secuencia;
    }

    @Override
    public String toString() {
        return "#" + secuencia + " " + tipo + " de " + origen + ": " + datos;
    }
}
