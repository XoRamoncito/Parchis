package presentacion;

import dtos.ColorJugador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

/** Colores y letras de las pantallas, tomados de los storyboards. */
final class Estilo {

    static final Color TINTA = new Color(17, 24, 39);
    static final Color GRIS = new Color(107, 114, 128);
    static final Color BORDE = new Color(229, 231, 235);
    static final Color FONDO_CAJA = new Color(243, 244, 246);
    static final Color ERROR = new Color(200, 50, 50);
    static final Color LISTO = new Color(74, 190, 90);
    static final Font TITULO = new Font("Segoe UI", Font.BOLD, 20);
    static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font NEGRITA = new Font("Segoe UI", Font.BOLD, 13);
    static final Font CHICA = new Font("Segoe UI", Font.BOLD, 10);

    private Estilo() {
    }

    static Color colorDe(ColorJugador color) {
        return switch (color) {
            case ROJO -> new Color(220, 74, 62);
            case AZUL -> new Color(88, 133, 245);
            case VERDE -> new Color(98, 190, 92);
            case AMARILLO -> new Color(236, 196, 52);
        };
    }

    /** Botón principal: oscuro con letra blanca. */
    static JButton botonOscuro(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(TINTA);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(11, 24, 11, 24));
        return boton;
    }

    /** Botón secundario: blanco con borde. */
    static JButton botonClaro(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(NEGRITA);
        boton.setBackground(Color.WHITE);
        boton.setForeground(TINTA);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        return boton;
    }
}
