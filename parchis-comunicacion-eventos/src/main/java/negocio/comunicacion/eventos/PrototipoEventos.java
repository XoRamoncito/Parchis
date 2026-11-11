package negocio.comunicacion.eventos;

import dtos.MensajeDTO;

import java.util.Scanner;

/**
 * Prototipo del estilo: dos computadoras se mandan un mensaje por el bus de eventos.
 *   PrototipoEventos bus                    arranca el bus (en una de las computadoras)
 *   PrototipoEventos nombre ip-del-bus      entra al bus y manda lo que se escriba
 */
public class PrototipoEventos {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("bus")) {
            new RelevoEventos(RelevoEventos.PUERTO).iniciar();
            return;
        }
        String nombre = args.length > 0 ? args[0] : "Jugador";
        String host = args.length > 1 ? args[1] : "localhost";

        BusEventosRed bus = new BusEventosRed();
        bus.conectar(host, RelevoEventos.PUERTO);
        bus.suscribirse("Saludo", mensaje -> System.out.println("Recibido " + mensaje));
        System.out.println("Conectado al bus en " + host + ". Escribe un mensaje y Enter (vacío para salir):");
        Scanner teclado = new Scanner(System.in);
        while (teclado.hasNextLine()) {
            String texto = teclado.nextLine();
            if (texto.isBlank()) {
                break;
            }
            bus.enviar(new MensajeDTO("Saludo", nombre, texto));
        }
        bus.desconectar();
    }
}
