package presentacion;

import presentacion.fabrica.FabricaSubsistemas;

import javax.swing.SwingUtilities;

/** Arranca el CU-01: la pantalla de registro con la fachada que da la fábrica. */
public class AppParchis {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControlSala(FabricaSubsistemas.crearFachadaSala()).iniciar());
    }
}
