package dtos;

/** Los cuatro colores del Parchís, en el orden en que se muestran en el registro. */
public enum ColorJugador {
    ROJO("Rojo"),
    AZUL("Azul"),
    VERDE("Verde"),
    AMARILLO("Amarillo");

    private final String nombre;

    ColorJugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
