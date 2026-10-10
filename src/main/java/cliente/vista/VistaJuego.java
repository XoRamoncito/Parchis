package cliente.vista;

import cliente.controlador.ControladorJuego;
import cliente.modelo.ModeloJuego;
import cliente.modelo.ObservadorJuego;
import comun.ColorJugador;
import comun.EstadoJuego;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Ventana del juego
 */
public class VistaJuego extends JFrame implements ObservadorJuego {

    /** Tiempo que tiene el jugador en turno para lanzar antes de que el dado se tire solo. */
    public static final int TIEMPO_PARA_LANZAR_MS = 15000;
    private static final int PASO_RELOJ_MS = 100;
    private static final Color TINTA = new Color(17, 24, 39);
    private static final Color GRIS = new Color(107, 114, 128);
    private static final Color APAGADO = new Color(209, 213, 219);

    private final ModeloJuego modelo;
    private ControladorJuego controlador;

    private final PanelTablero panelTablero = new PanelTablero(this);
    private final AvisoJuego avisoJuego = new AvisoJuego();
    private final JButton btnAbandonar = new JButton("ABANDONAR PARTIDA");
    private final JLabel lblTurno = new JLabel("Conectando...");
    private final JProgressBar barraTurno = new JProgressBar(0, TIEMPO_PARA_LANZAR_MS);
    private final JLabel lblDado = new JLabel(" ");
    private final JLabel lblMensaje = new JLabel(" ");
    private final JButton btnLanzarDado = new JButton("Lanzar dado");
    private final Timer relojTurno = new Timer(PASO_RELOJ_MS, e -> avanzarReloj());
    private int tiempoRestante;
    /** Versión del último estado que ya se mostró, para no repetir avisos ni reiniciar el reloj. */
    private int versionMostrada = -1;
    private boolean eraMiTurno;

    public VistaJuego(ModeloJuego modelo) {
        super("Parchís");
        this.modelo = modelo;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(crearPanelTurno(), BorderLayout.SOUTH);
        setGlassPane(avisoJuego);
        btnLanzarDado.addActionListener(e -> lanzarDado());
        pack();
        setLocationByPlatform(true);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        btnAbandonar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnAbandonar.setForeground(new Color(200, 50, 50));
        btnAbandonar.setBackground(Color.WHITE);
        btnAbandonar.setFocusPainted(false);
        btnAbandonar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        // "Abandonar partida" es otro caso de uso; el botón solo ocupa su lugar del storyboard.
        btnAbandonar.setEnabled(false);
        btnAbandonar.setToolTipText("Caso de uso Abandonar partida (fuera de esta entrega)");

        JLabel titulo = new JLabel("PARCHIS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TINTA);
        barra.add(btnAbandonar, BorderLayout.WEST);
        barra.add(titulo, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearPanelTurno() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 18, 14, 18)));

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTurno.setForeground(TINTA);
        lblDado.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
        lblDado.setForeground(TINTA);
        lblDado.setPreferredSize(new Dimension(52, 48));
        fila.add(lblTurno, BorderLayout.WEST);
        fila.add(lblDado, BorderLayout.EAST);

        barraTurno.setValue(TIEMPO_PARA_LANZAR_MS);
        barraTurno.setBorderPainted(false);
        barraTurno.setBackground(new Color(238, 240, 244));
        barraTurno.setForeground(APAGADO);
        barraTurno.setPreferredSize(new Dimension(10, 6));
        barraTurno.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMensaje.setForeground(GRIS);

        vestirBotonOscuro(btnLanzarDado);
        btnLanzarDado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLanzarDado.setPreferredSize(new Dimension(10, 46));
        btnLanzarDado.setEnabled(false);
        btnLanzarDado.setBackground(APAGADO);

        for (Component pieza : new Component[]{fila, barraTurno, lblMensaje, btnLanzarDado}) {
            ((javax.swing.JComponent) pieza).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        panel.add(fila);
        panel.add(barraTurno);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblMensaje);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLanzarDado);
        return panel;
    }

    /** Aspecto del botón principal del storyboard: oscuro, sin borde, letra blanca. */
    static void vestirBotonOscuro(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(TINTA);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
    }

    /** Aviso del modelo (patrón observador). Puede llegar desde el hilo de red. */
    @Override
    public void actualizar() {
        SwingUtilities.invokeLater(this::refrescar);
    }

    /** Lee el modelo y pone la ventana al día. */
    private void refrescar() {
        EstadoJuego estado = modelo.getEstado();
        ColorJugador miColor = modelo.getMiColor();
        if (estado == null || miColor == null) {
            return;
        }
        setTitle("Parchís - " + estado.getNombre(miColor) + " (" + miColor.getNombre() + ")");
        panelTablero.mostrar(estado, miColor, modelo.getFichasMovibles());

        ColorJugador turno = estado.getTurno();
        boolean miTurno = modelo.esMiTurno();
        if (estado.getGanador() != null) {
            lblTurno.setText("Ganó " + estado.getNombre(estado.getGanador()));
        } else if (turno == null) {
            lblTurno.setText("Esperando jugadores");
        } else if (miTurno) {
            lblTurno.setText("Tu turno");
        } else {
            lblTurno.setText("Turno de " + estado.getNombre(turno));
        }
        barraTurno.setForeground(turno == null ? APAGADO : PanelTablero.colorDe(turno));
        lblDado.setText(estado.getValorDado() == 0 ? " " : String.valueOf((char) ('⚀' + estado.getValorDado() - 1)));
        lblMensaje.setText(estado.getMensaje());

        boolean puedeLanzar = modelo.puedeLanzar();
        btnLanzarDado.setEnabled(puedeLanzar);
        btnLanzarDado.setBackground(puedeLanzar ? TINTA : APAGADO);

        if (estado.getVersion() != versionMostrada) {
            versionMostrada = estado.getVersion();
            if (miTurno && estado.isTiroExtra()) {
                avisoJuego.mostrar(estado.getValorDado() == 6 ? "¡Sacaste un 6!" : "¡Tiro extra!",
                        "Tienes otro tiro. Lanza el dado.");
            } else if (miTurno && !eraMiTurno) {
                avisoJuego.mostrar("¡Tu turno!", "Lanza el dado.");
            }
            eraMiTurno = miTurno;
            if (puedeLanzar) {
                iniciarReloj();
            } else {
                detenerReloj();
            }
        }
    }

    /** Empieza la cuenta atrás para lanzar; la barra de turno se va vaciando. */
    private void iniciarReloj() {
        tiempoRestante = TIEMPO_PARA_LANZAR_MS;
        barraTurno.setValue(tiempoRestante);
        relojTurno.restart();
    }

    private void detenerReloj() {
        relojTurno.stop();
        barraTurno.setValue(TIEMPO_PARA_LANZAR_MS);
    }

    private void avanzarReloj() {
        tiempoRestante -= PASO_RELOJ_MS;
        barraTurno.setValue(tiempoRestante);
        if (tiempoRestante <= 0) {
            relojTurno.stop();
            controlador.tiempoAgotado();
        }
    }

    /** El jugador presionó "Lanzar dado". */
    void lanzarDado() {
        relojTurno.stop();
        controlador.lanzarDado();
    }

    /** El jugador hizo clic en una de sus fichas movibles; lo avisa el tablero. */
    void fichaElegida(int numeroFicha) {
        controlador.seleccionarFicha(numeroFicha);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Parchís", JOptionPane.WARNING_MESSAGE);
    }
}
