package cliente.vista;

import comun.ColorJugador;
import comun.EstadoFicha;
import comun.EstadoJuego;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dibuja el tablero y las fichas a partir de un {@link EstadoJuego}, y avisa
 * a la ventana cuando el jugador hace clic en una de sus fichas movibles.
 * El tablero - cuadrícula de 19 x 19: cuatro brazos de 3 x 8 casillas
 * alrededor de un centro de 3 x 3 (68 casillas de recorrido, 7 de pasillo
 * por color y la meta al centro). las fichas no saltan a su lugar: caminan una
 * casilla cada {@value #PASO_ANIMACION_MS} ms.
 */
public class PanelTablero extends JPanel {

    private static final int CELDAS = 19;
    private static final int PASO_ANIMACION_MS = 110;
    private static final int[][] RECORRIDO = crearRecorrido();
    private static final Color BORDE = new Color(212, 216, 224);
    private static final Color SEGURA = new Color(226, 229, 235);
    private static final Color TINTA = new Color(17, 24, 39);

    private final VistaJuego vista;
    private EstadoJuego estado;
    private ColorJugador miColor;
    private List<Integer> movibles = new ArrayList<>();
    /** Centro en pantalla de cada ficha movible, por número de ficha, para detectar el clic. */
    private final Map<Integer, double[]> centrosMovibles = new HashMap<>();
    private double radioFicha;
    /** Pasos con los que se está dibujando cada ficha mientras camina hacia su lugar real. */
    private final Map<String, Integer> pasosDibujados = new HashMap<>();
    private final Timer animador = new Timer(PASO_ANIMACION_MS, e -> animar());

    public PanelTablero(VistaJuego vista) {
        this.vista = vista;
        setPreferredSize(new Dimension(570, 570));
        setBackground(new Color(248, 249, 252));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                alHacerClic(e.getX(), e.getY());
            }
        });
    }

    /** Recibe lo que hay que dibujar y repinta. */
    public void mostrar(EstadoJuego estado, ColorJugador miColor, List<Integer> movibles) {
        this.estado = estado;
        this.miColor = miColor;
        this.movibles = movibles;
        for (EstadoFicha ficha : estado.getFichas()) {
            pasosDibujados.putIfAbsent(identificador(ficha), ficha.getPasos());
        }
        if (!animador.isRunning()) {
            animador.start();
        }
        setCursor(Cursor.getPredefinedCursor(movibles.isEmpty() ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        repaint();
    }

    /** Un paso de la animación: acerca cada ficha dibujada una casilla a donde está en el estado. */
    private void animar() {
        boolean avanzo = false;
        for (EstadoFicha ficha : estado.getFichas()) {
            int dibujados = pasosDibujados.get(identificador(ficha));
            if (dibujados < ficha.getPasos()) {
                pasosDibujados.put(identificador(ficha), dibujados + 1);
                avanzo = true;
            }
        }
        if (!avanzo) {
            // Las fichas capturadas vuelven a su base cuando la que las capturó ya llegó.
            for (EstadoFicha ficha : estado.getFichas()) {
                pasosDibujados.put(identificador(ficha), ficha.getPasos());
            }
            animador.stop();
        }
        repaint();
    }

    private static String identificador(EstadoFicha ficha) {
        return ficha.getColor() + "-" + ficha.getNumero();
    }

    private void alHacerClic(int x, int y) {
        for (Map.Entry<Integer, double[]> entrada : centrosMovibles.entrySet()) {
            double[] centro = entrada.getValue();
            if (Math.hypot(x - centro[0], y - centro[1]) <= radioFicha * 1.4) {
                vista.fichaElegida(entrada.getKey());
                return;
            }
        }
    }

    public static Color colorDe(ColorJugador color) {
        return switch (color) {
            case ROJO -> new Color(220, 74, 62);
            case AZUL -> new Color(88, 133, 245);
            case VERDE -> new Color(98, 190, 92);
            case AMARILLO -> new Color(236, 196, 52);
        };
    }

    private static Color claro(Color color, double cuanto) {
        return new Color(
                (int) (color.getRed() + (255 - color.getRed()) * cuanto),
                (int) (color.getGreen() + (255 - color.getGreen()) * cuanto),
                (int) (color.getBlue() + (255 - color.getBlue()) * cuanto));
    }

    /** Las 68 casillas del recorrido común, en el orden en que se avanza: {columna, fila}. */
    private static int[][] crearRecorrido() {
        List<int[]> casillas = new ArrayList<>();
        for (int fila = 18; fila >= 11; fila--) casillas.add(new int[]{10, fila});
        for (int col = 11; col <= 18; col++) casillas.add(new int[]{col, 10});
        casillas.add(new int[]{18, 9});
        for (int col = 18; col >= 11; col--) casillas.add(new int[]{col, 8});
        for (int fila = 7; fila >= 0; fila--) casillas.add(new int[]{10, fila});
        casillas.add(new int[]{9, 0});
        for (int fila = 0; fila <= 7; fila++) casillas.add(new int[]{8, fila});
        for (int col = 7; col >= 0; col--) casillas.add(new int[]{col, 8});
        casillas.add(new int[]{0, 9});
        for (int col = 0; col <= 7; col++) casillas.add(new int[]{col, 10});
        for (int fila = 11; fila <= 18; fila++) casillas.add(new int[]{8, fila});
        casillas.add(new int[]{9, 18});
        return casillas.toArray(new int[0][]);
    }

    /** Esquina (en celdas) del cuadrante donde está la base de un color. */
    private static int[] esquinaBase(ColorJugador color) {
        return switch (color) {
            case ROJO -> new int[]{0, 0};
            case VERDE -> new int[]{11, 0};
            case AZUL -> new int[]{0, 11};
            case AMARILLO -> new int[]{11, 11};
        };
    }

    /** Celda del pasillo de un color; paso va de 1 (la primera) a 7 (junto a la meta). */
    private static int[] celdaPasillo(ColorJugador color, int paso) {
        return switch (color) {
            case ROJO -> new int[]{9, paso};
            case VERDE -> new int[]{18 - paso, 9};
            case AZUL -> new int[]{paso, 9};
            case AMARILLO -> new int[]{9, 18 - paso};
        };
    }

    /** Centro (en celdas) donde se dibuja una ficha, sin contar el corrimiento por compartir casilla. */
    private static double[] centroFicha(EstadoFicha ficha) {
        ColorJugador color = ficha.getColor();
        if (ficha.estaEnBase()) {
            int[] esquina = esquinaBase(color);
            double x = esquina[0] + (ficha.getNumero() % 2 == 0 ? 2.9 : 5.1);
            double y = esquina[1] + (ficha.getNumero() < 2 ? 2.4 : 4.6);
            return new double[]{x, y};
        }
        if (ficha.estaEnMeta()) {
            double corrimiento = (ficha.getNumero() - 1.5) * 0.42;
            return switch (color) {
                case ROJO -> new double[]{9.5 + corrimiento, 8.45};
                case VERDE -> new double[]{10.55, 9.5 + corrimiento};
                case AZUL -> new double[]{8.45, 9.5 + corrimiento};
                case AMARILLO -> new double[]{9.5 + corrimiento, 10.55};
            };
        }
        int[] celda = ficha.estaEnPasillo()
                ? celdaPasillo(color, ficha.getPasos() - EstadoFicha.FIN_RECORRIDO)
                : RECORRIDO[ficha.getCasilla()];
        return new double[]{celda[0] + 0.5, celda[1] + 0.5};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double lado = Math.min(getWidth(), getHeight()) - 24;
        double celda = lado / CELDAS;
        double x0 = (getWidth() - lado) / 2;
        double y0 = (getHeight() - lado) / 2;
        radioFicha = celda * 0.38;

        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(x0 - 8, y0 - 8, lado + 16, lado + 16, 22, 22));
        g2.setColor(BORDE);
        g2.draw(new RoundRectangle2D.Double(x0 - 8, y0 - 8, lado + 16, lado + 16, 22, 22));

        g2.translate(x0, y0);
        dibujarBases(g2, celda);
        dibujarCasillas(g2, celda);
        dibujarCentro(g2, celda);
        dibujarFichas(g2, celda, x0, y0);
        g2.dispose();
    }

    private void dibujarBases(Graphics2D g2, double celda) {
        for (ColorJugador color : ColorJugador.values()) {
            int[] esquina = esquinaBase(color);
            Color base = colorDe(color);
            boolean juega = estado != null && estado.getNombre(color) != null;
            RoundRectangle2D marco = new RoundRectangle2D.Double(
                    (esquina[0] + 0.7) * celda, (esquina[1] + 0.7) * celda, 6.6 * celda, 6.6 * celda, 18, 18);
            g2.setColor(claro(base, juega ? 0.86 : 0.95));
            g2.fill(marco);
            g2.setColor(claro(base, juega ? 0.55 : 0.85));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(marco);

            // Lugares vacíos de la base
            for (int numero = 0; numero < 4; numero++) {
                double[] centro = centroFicha(new EstadoFicha(color, numero, EstadoFicha.BASE));
                g2.setColor(claro(base, 0.7));
                g2.fill(circulo(centro[0] * celda, centro[1] * celda, radioFicha));
            }
            if (juega) {
                String nombre = estado.getNombre(color) + (color == miColor ? " (tú)" : "");
                g2.setFont(new Font("Segoe UI", Font.BOLD, (int) Math.max(11, celda * 0.5)));
                FontMetrics medidas = g2.getFontMetrics();
                g2.setColor(new Color(55, 65, 81));
                g2.drawString(nombre, (float) ((esquina[0] + 4) * celda - medidas.stringWidth(nombre) / 2.0),
                        (float) ((esquina[1] + 6.55) * celda));
            }
        }
    }

    private void dibujarCasillas(Graphics2D g2, double celda) {
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < RECORRIDO.length; i++) {
            Color relleno = Color.WHITE;
            if (i % 17 == 11 || i % 17 == 16) {
                relleno = SEGURA;
            }
            for (ColorJugador color : ColorJugador.values()) {
                if (color.getCasillaSalida() == i) {
                    relleno = claro(colorDe(color), 0.45);
                }
            }
            dibujarCelda(g2, RECORRIDO[i], celda, relleno);
        }
        for (ColorJugador color : ColorJugador.values()) {
            for (int paso = 1; paso <= 7; paso++) {
                dibujarCelda(g2, celdaPasillo(color, paso), celda, claro(colorDe(color), 0.72));
            }
        }
    }

    private void dibujarCelda(Graphics2D g2, int[] donde, double celda, Color relleno) {
        Rectangle2D cuadro = new Rectangle2D.Double(donde[0] * celda, donde[1] * celda, celda, celda);
        g2.setColor(relleno);
        g2.fill(cuadro);
        g2.setColor(BORDE);
        g2.draw(cuadro);
    }

    /** La meta: cuatro triángulos, cada uno del color cuyo pasillo llega a él. */
    private void dibujarCentro(Graphics2D g2, double celda) {
        int a = (int) Math.round(8 * celda);
        int b = (int) Math.round(11 * celda);
        int c = (int) Math.round(9.5 * celda);
        g2.setColor(colorDe(ColorJugador.ROJO));
        g2.fill(new Polygon(new int[]{a, b, c}, new int[]{a, a, c}, 3));
        g2.setColor(colorDe(ColorJugador.VERDE));
        g2.fill(new Polygon(new int[]{b, b, c}, new int[]{a, b, c}, 3));
        g2.setColor(colorDe(ColorJugador.AMARILLO));
        g2.fill(new Polygon(new int[]{a, b, c}, new int[]{b, b, c}, 3));
        g2.setColor(colorDe(ColorJugador.AZUL));
        g2.fill(new Polygon(new int[]{a, a, c}, new int[]{a, b, c}, 3));
    }

    private void dibujarFichas(Graphics2D g2, double celda, double x0, double y0) {
        centrosMovibles.clear();
        if (estado == null) {
            return;
        }
        // Cuántas fichas comparten cada punto, para dibujarlas una junto a otra
        Map<String, Integer> total = new HashMap<>();
        Map<String, Integer> vistas = new HashMap<>();
        List<EstadoFicha> dibujadas = new ArrayList<>();
        for (EstadoFicha real : estado.getFichas()) {
            int pasos = pasosDibujados.getOrDefault(identificador(real), real.getPasos());
            dibujadas.add(new EstadoFicha(real.getColor(), real.getNumero(), pasos));
        }
        for (EstadoFicha ficha : dibujadas) {
            total.merge(clave(ficha), 1, Integer::sum);
        }
        for (EstadoFicha ficha : dibujadas) {
            double[] centro = centroFicha(ficha);
            String clave = clave(ficha);
            int posicion = vistas.merge(clave, 1, Integer::sum) - 1;
            double x = (centro[0] + (posicion - (total.get(clave) - 1) / 2.0) * 0.45) * celda;
            double y = centro[1] * celda;
            boolean movible = !animador.isRunning() && ficha.getColor() == miColor
                    && movibles.contains(ficha.getNumero());

            Color base = colorDe(ficha.getColor());
            if (movible) {
                g2.setColor(new Color(17, 24, 39, 40));
                g2.fill(circulo(x, y, radioFicha * 1.55));
                centrosMovibles.put(ficha.getNumero(), new double[]{x + x0, y + y0});
            }
            g2.setColor(base);
            g2.fill(circulo(x, y, radioFicha));
            g2.setStroke(new BasicStroke(movible ? 3f : 1.6f));
            g2.setColor(movible ? TINTA : base.darker());
            g2.draw(circulo(x, y, radioFicha));
        }
    }

    private static String clave(EstadoFicha ficha) {
        double[] centro = centroFicha(ficha);
        return centro[0] + ":" + centro[1];
    }

    private static Ellipse2D circulo(double x, double y, double radio) {
        return new Ellipse2D.Double(x - radio, y - radio, radio * 2, radio * 2);
    }
}
