package cliente.modelo;

/** Lo implementa quien observa al modelo (la vista). */
public interface ObservadorJuego {

    /** El modelo cambió: hay que volver a leerlo. */
    void actualizar();
}
