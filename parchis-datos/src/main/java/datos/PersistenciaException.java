package datos;

/** Error técnico al guardar o leer. La SQLException nunca sale de la capa de datos. */
public class PersistenciaException extends Exception {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
