package presentacion;

import dtos.ColorJugador;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Pantalla de registro (storyboard 1): nombre de usuario, paleta de cuatro
 * colores, avatar y el botón "Registrar perfil". Solo captura y muestra; lo que
 * pasa al registrar lo decide ControlSala.
 */
public class FormRegistro extends JFrame {

    private static final int AVATARES = 4;

    private final ControlSala control;
    private final JTextField txtNombre = new JTextField();
    private final Map<ColorJugador, JButton> btnColores = new EnumMap<>(ColorJugador.class);
    private final JButton[] btnAvatares = new JButton[AVATARES];
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnRegistrar = Estilo.botonOscuro("Registrar Perfil");
    private ColorJugador colorElegido = ColorJugador.ROJO;
    private int avatarElegido = 1;

    public FormRegistro(ControlSala control) {
        super("Parchís - Registro");
        this.control = control;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel titulo = new JLabel("Registro");
        titulo.setFont(Estilo.TITULO);
        JLabel subtitulo = new JLabel("Crea tu perfil para entrar a la sala");
        subtitulo.setFont(Estilo.NORMAL);
        subtitulo.setForeground(Estilo.GRIS);

        txtNombre.setFont(Estilo.NORMAL);
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JPanel paleta = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        paleta.setOpaque(false);
        for (ColorJugador color : ColorJugador.values()) {
            JButton boton = new JButton();
            boton.setPreferredSize(new Dimension(52, 52));
            boton.setBackground(Estilo.colorDe(color));
            boton.setFocusPainted(false);
            boton.setToolTipText(color.getNombre());
            boton.addActionListener(e -> elegirColor(color));
            btnColores.put(color, boton);
            paleta.add(boton);
        }

        JPanel avatares = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        avatares.setOpaque(false);
        for (int i = 0; i < AVATARES; i++) {
            final int numero = i + 1;
            JButton boton = Estilo.botonClaro(String.valueOf(numero));
            boton.setPreferredSize(new Dimension(52, 40));
            boton.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true));
            boton.addActionListener(e -> elegirAvatar(numero));
            btnAvatares[i] = boton;
            avatares.add(boton);
        }

        lblError.setFont(Estilo.NORMAL);
        lblError.setForeground(Estilo.ERROR);
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnRegistrar.addActionListener(e -> control.registrar(txtNombre.getText(), colorElegido, avatarElegido));

        Component[] piezas = {titulo, subtitulo, Box.createVerticalStrut(22), etiqueta("NOMBRE DE USUARIO"),
                Box.createVerticalStrut(6), txtNombre, Box.createVerticalStrut(18), etiqueta("COLOR"),
                Box.createVerticalStrut(8), paleta, Box.createVerticalStrut(18), etiqueta("AVATAR"),
                Box.createVerticalStrut(8), avatares, Box.createVerticalStrut(14), lblError,
                Box.createVerticalStrut(10), btnRegistrar};
        for (Component pieza : piezas) {
            if (pieza instanceof JComponent c) {
                c.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            panel.add(pieza);
        }
        setContentPane(panel);
        elegirColor(ColorJugador.ROJO);
        elegirAvatar(1);
        setSize(380, 520);
        setLocationByPlatform(true);
    }

    private static JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(Estilo.CHICA);
        etiqueta.setForeground(Estilo.GRIS);
        return etiqueta;
    }

    private void elegirColor(ColorJugador color) {
        colorElegido = color;
        btnColores.forEach((c, boton) -> boton.setBorder(c == color
                ? BorderFactory.createLineBorder(Estilo.TINTA, 3, true)
                : BorderFactory.createLineBorder(Color.WHITE, 3, true)));
    }

    private void elegirAvatar(int numero) {
        avatarElegido = numero;
        for (int i = 0; i < btnAvatares.length; i++) {
            boolean elegido = i + 1 == numero;
            btnAvatares[i].setBackground(elegido ? Estilo.TINTA : Color.WHITE);
            btnAvatares[i].setForeground(elegido ? Color.WHITE : Estilo.TINTA);
        }
    }

    /** Flujos alternos: muestra el mensaje y, si se indican, deja elegir solo los colores libres. */
    public void mostrarError(String mensaje, Set<ColorJugador> coloresLibres) {
        lblError.setText(mensaje);
        if (!coloresLibres.isEmpty()) {
            btnColores.forEach((color, boton) -> {
                boolean libre = coloresLibres.contains(color);
                boton.setEnabled(libre);
                boton.setBackground(libre ? Estilo.colorDe(color) : new Color(229, 231, 235));
                boton.setText(libre ? "" : "X");
                boton.setToolTipText(color.getNombre() + (libre ? "" : " (ocupado)"));
            });
            elegirColor(coloresLibres.iterator().next());
        }
    }

    /** Para las pruebas y la demostración: captura los datos como lo haría el jugador. */
    void capturar(String nombre, ColorJugador color, int avatar) {
        txtNombre.setText(nombre);
        elegirColor(color);
        elegirAvatar(avatar);
    }

    void presionarRegistrar() {
        btnRegistrar.doClick();
    }
}
