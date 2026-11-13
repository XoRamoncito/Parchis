package negocio.perfiles;

/** Error del negocio. Los errores técnicos de la base de datos llegan traducidos a este. */
public class NegocioException extends Exception {

    public NegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
