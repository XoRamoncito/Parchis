package presentacion;

import dtos.JugadorDTO;
import dtos.SalaDTO;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Sala de espera (storyboard 3): el código de la sala, la lista de jugadores
 * con su color y si ya están listos, y el botón "Confirmar estado".
 * "Iniciar partida" es de otro caso de uso y aparece deshabilitado.
 */
public class FormSalaEspera extends JFrame {

    private final ControlSala control;
    private final JLabel lblCodigo = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblCuantos = new JLabel(" ");
    private final JPanel lista = new JPanel();
    private final JButton btnConfirmar = Estilo.botonClaro("Confirmar Estado");
    private final JButton btnIniciar = Estilo.botonOscuro("Iniciar Partida");

    public FormSalaEspera(ControlSala control) {
        super("Parchís - Sala de espera");
        this.control = control;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createEmptyBorder(22, 24, 12, 24));
        JLabel titulo = new JLabel("Sala de Espera", SwingConstants.CENTER);
        titulo.setFont(Estilo.TITULO);
        JLabel etiquetaCodigo = new JLabel("CÓDIGO DE LA SALA", SwingConstants.CENTER);
        etiquetaCodigo.setFont(Estilo.CHICA);
        etiquetaCodigo.setForeground(Estilo.GRIS);
        lblCodigo.setFont(new Font("Consolas", Font.BOLD, 18));
        lblCodigo.setOpaque(true);
        lblCodigo.setBackground(Estilo.FONDO_CAJA);
        lblCodigo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        lblCodigo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        lblCuantos.setFont(Estilo.CHICA);
        lblCuantos.setForeground(Estilo.GRIS);
        for (JComponent c : new JComponent[]{titulo, etiquetaCodigo, lblCodigo, lblCuantos}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        lblCuantos.setAlignmentX(Component.CENTER_ALIGNMENT);
        cabecera.add(titulo);
        cabecera.add(Box.createVerticalStrut(4));
        cabecera.add(etiquetaCodigo);
        cabecera.add(Box.createVerticalStrut(8));
        cabecera.add(lblCodigo);
        cabecera.add(Box.createVerticalStrut(16));
        cabecera.add(lblCuantos);

        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel pie = new JPanel();
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
                BorderFactory.createEmptyBorder(14, 24, 18, 24)));
        btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnIniciar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIniciar.setEnabled(false);
        btnIniciar.setBackground(new Color(209, 213, 219));
        btnIniciar.setToolTipText("Caso de uso Iniciar partida (fuera de este caso de uso)");
        btnConfirmar.addActionListener(e -> control.confirmarEstado());
        pie.add(btnConfirmar);
        pie.add(Box.createVerticalStrut(8));
        pie.add(btnIniciar);

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(lista, BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);
        setContentPane(panel);
        setSize(380, 600);
    }

    /** Pinta la sala tal como la manda la fachada. */
    public void mostrar(SalaDTO sala, String miNombre) {
        lblCodigo.setText(sala.getCodigo());
        lblCuantos.setText("JUGADORES (" + sala.getJugadores().size() + "/" + sala.getCapacidad() + ")");
        lista.removeAll();
        for (JugadorDTO jugador : sala.getJugadores()) {
            lista.add(fila(jugador, jugador.getNombre().equalsIgnoreCase(miNombre)));
            lista.add(Box.createVerticalStrut(8));
        }
        for (JugadorDTO jugador : sala.getJugadores()) {
            if (jugador.getNombre().equalsIgnoreCase(miNombre) && jugador.isListo()) {
                btnConfirmar.setText("Listo");
                btnConfirmar.setForeground(Estilo.LISTO);
                btnConfirmar.setEnabled(false);
            }
        }
        lista.revalidate();
        lista.repaint();
    }

    private JPanel fila(JugadorDTO jugador, boolean soyYo) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setBackground(Color.WHITE);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 12)));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cuadro = new JLabel(String.valueOf(jugador.getAvatar()), SwingConstants.CENTER);
        cuadro.setOpaque(true);
        cuadro.setBackground(Estilo.colorDe(jugador.getColor()));
        cuadro.setForeground(Color.WHITE);
        cuadro.setFont(Estilo.NEGRITA);
        cuadro.setPreferredSize(new Dimension(40, 40));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        JLabel nombre = new JLabel(jugador.getNombre() + (soyYo ? "   (Tú)" : ""));
        nombre.setFont(Estilo.NEGRITA);
        JLabel equipo = new JLabel("Equipo " + jugador.getColor().getNombre());
        equipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        equipo.setForeground(Estilo.GRIS);
        textos.add(nombre);
        textos.add(equipo);

        Color colorPunto = jugador.isListo() ? Estilo.LISTO : new Color(209, 213, 219);
        JComponent punto = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorPunto);
                g2.fillOval(getWidth() / 2 - 5, getHeight() / 2 - 5, 10, 10);
            }
        };
        punto.setPreferredSize(new Dimension(16, 40));
        punto.setToolTipText(jugador.isListo() ? "Listo" : "Esperando");

        fila.add(cuadro, BorderLayout.WEST);
        fila.add(textos, BorderLayout.CENTER);
        fila.add(punto, BorderLayout.EAST);
        return fila;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Parchís", JOptionPane.WARNING_MESSAGE);
    }

    void presionarConfirmar() {
        btnConfirmar.doClick();
    }
}
