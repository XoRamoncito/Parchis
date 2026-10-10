package cliente;

import cliente.controlador.ControladorJuego;
import cliente.modelo.ModeloJuego;
import cliente.red.FachadaJuegoRemota;
import cliente.vista.VistaJuego;
import comun.Mensaje;
import negocio.IFachadaJuego;
import negocio.MovimientoInvalidoException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * Jugador lo abre: pide nombre y dirección del servidor, y arma el MVC.
 * Uso: AppCliente [nombre] [servidor] [puerto]
 */
public class AppCliente {

    public static void main(String[] args) {
        String nombre = args.length > 0 ? args[0] : JOptionPane.showInputDialog(null,
                "Nombre de usuario:", "Parchís", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        String host = args.length > 1 ? args[1] : (String) JOptionPane.showInputDialog(null,
                "Dirección del servidor:", "Parchís", JOptionPane.QUESTION_MESSAGE, null, null, "localhost");
        if (host == null || host.isBlank()) {
            return;
        }
        int puerto = args.length > 2 ? Integer.parseInt(args[2]) : Mensaje.PUERTO;

        try {
            IFachadaJuego fachada = new FachadaJuegoRemota(host.trim(), puerto);
            ModeloJuego modelo = new ModeloJuego(fachada);
            SwingUtilities.invokeAndWait(() -> {
                VistaJuego vista = new VistaJuego(modelo);
                new ControladorJuego(modelo, vista);
                modelo.agregarObservador(vista);
                vista.setVisible(true);
            });
            modelo.unirse(nombre.trim());
        } catch (IOException e) {
            avisar("No se pudo conectar con el servidor en " + host + ":" + puerto);
        } catch (MovimientoInvalidoException e) {
            avisar(e.getMessage());
        } catch (Exception e) {
            avisar("Error al iniciar: " + e.getMessage());
        }
    }

    private static void avisar(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Parchís", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
