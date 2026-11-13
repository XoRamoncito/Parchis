package presentacion;

import negocio.sala.IFachadaSala;
import presentacion.fabrica.FabricaSubsistemas;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Arranca el CU-01. Sin argumentos esta computadora tiene el bus de eventos;
 * en las demás se pasa la IP de esa computadora: ejecutar-parchis.bat 26.12.34.56
 */
public class AppParchis {

    public static void main(String[] args) {
        String hostBus = args.length > 0 ? args[0] : "localhost";
        IFachadaSala fachada;
        try {
            fachada = FabricaSubsistemas.crearFachadaSala(hostBus);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Parchís - no se pudo iniciar", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }
        SwingUtilities.invokeLater(() -> new ControlSala(fachada).iniciar());
    }
}
