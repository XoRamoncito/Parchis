package negocio;

/** Se lanza cuando se pide algo que las reglas del juego no permiten. */
public class MovimientoInvalidoException extends RuntimeException {

    public MovimientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
