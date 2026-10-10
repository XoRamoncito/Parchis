package servidor;

import comun.Mensaje;
import negocio.FachadaJuegoMock;
import negocio.IFachadaJuego;

import java.io.IOException;

/** Arrancar el servidor.*/
public class AppServidor {

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : Mensaje.PUERTO;
        IFachadaJuego fachada = new FachadaJuegoMock();
        new ServidorParchis(puerto, fachada).iniciar();
    }
}
