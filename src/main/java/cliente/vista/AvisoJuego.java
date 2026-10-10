package cliente.vista;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Aviso que aparece sobre la ventana del juego y se quita solo: "¡Tu turno!",
 * "¡Sacaste un 6!" , etc.
 */
public class AvisoJuego extends JComponent {

    private static final int DURACION_MS = 2200;

    private String titulo = "";
    private String detalle = "";
    private final Timer temporizador = new Timer(DURACION_MS, e -> setVisible(false));

    public AvisoJuego() {
        setOpaque(false);
        temporizador.setRepeats(false);
    }

    /** Muestra el aviso unos segundos. */
    public void mostrar(String titulo, String detalle) {
        this.titulo = titulo;
        this.detalle = detalle;
        setVisible(true);
        temporizador.restart();
        repaint();
    }

    /** El aviso no atrapa el ratón: los clics llegan al tablero y a los botones. */
    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 22);
        Font fuenteDetalle = new Font("Segoe UI", Font.PLAIN, 13);
        FontMetrics medidasTitulo = g2.getFontMetrics(fuenteTitulo);
        FontMetrics medidasDetalle = g2.getFontMetrics(fuenteDetalle);

        int ancho = Math.max(medidasTitulo.stringWidth(titulo), medidasDetalle.stringWidth(detalle)) + 70;
        int alto = 92;
        int x = (getWidth() - ancho) / 2;
        int y = (getHeight() - alto) / 2 - 40;

        g2.setColor(new Color(17, 24, 39, 45));
        g2.fill(new RoundRectangle2D.Double(x + 3, y + 5, ancho, alto, 22, 22));
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(x, y, ancho, alto, 22, 22));
        g2.setColor(new Color(200, 205, 215));
        g2.draw(new RoundRectangle2D.Double(x, y, ancho, alto, 22, 22));

        g2.setFont(fuenteTitulo);
        g2.setColor(new Color(17, 24, 39));
        g2.drawString(titulo, x + (ancho - medidasTitulo.stringWidth(titulo)) / 2, y + 40);
        g2.setFont(fuenteDetalle);
        g2.setColor(new Color(107, 114, 128));
        g2.drawString(detalle, x + (ancho - medidasDetalle.stringWidth(detalle)) / 2, y + 66);
        g2.dispose();
    }
}
