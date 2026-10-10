# Parchís

Evaluación en equipo de Arquitectura de Software (ITSON).
Integrantes: Ramon Montoya Manjarrez (00000269974) y Gustavo Romero Gonzalez (00000270001).

Por ahora implementa el caso de uso **Ejercer turno** (lanzar dado, seleccionar ficha, mover ficha, tiro
extra y paso de turno) con el patrón **MVC**, donde la vista **observa** al modelo. El modelo habla con el
subsistema de reglas del juego a través de una **fachada**, de la que solo existe un **objeto mock**. Cada
jugador corre en su propia ventana y se conecta por **sockets** a un servidor; lo que hace el jugador en
turno aparece en la ventana del que espera.

## Cómo ejecutarlo

Requiere Java 17 o superior.

1. Servidor: doble clic en `ejecutar-servidor.bat` (o ejecutar `servidor.AppServidor` desde IntelliJ).
2. Jugador 1: doble clic en `ejecutar-cliente.bat` (o `cliente.AppCliente`). Pide el nombre y la dirección
   del servidor (`localhost` si es la misma computadora).
3. Jugador 2: lo mismo, en la misma computadora o en otra. Desde otra computadora se escribe la IP de la
   que tiene el servidor (las dos deben estar en la misma red y el puerto 5000 permitido en el firewall).

La partida empieza al entrar el segundo jugador. El primero es rojo y el segundo azul.
Cada partida necesita un servidor recién arrancado (no se maneja el abandono de un jugador).

Pruebas: `mvn test` (19 pruebas de la fachada mock y del modelo).

## Reglas que aplica el mock

- Todas las fichas empiezan en la base; se sale con un 5.
- Dan otro tiro: sacar 6, capturar una ficha rival y meter una ficha a la meta. El tercer 6 seguido pierde el turno.
- No se captura en las salidas ni en las casillas grises, ni a dos fichas del mismo color en la misma casilla.
- Si solo hay una ficha que se puede mover, se mueve sola. Si no hay ninguna, el turno pasa.
- El jugador en turno tiene 15 segundos para lanzar; si no, el dado se tira solo.

## Estructura

| Paquete | Clases | Papel |
|---|---|---|
| `cliente.vista` | `VistaJuego`, `PanelTablero`, `AvisoJuego` | Vista (objetos de presentación). Observa al modelo. |
| `cliente.controlador` | `ControladorJuego` | Controlador: recibe las acciones de la vista y llama al modelo. |
| `cliente.modelo` | `ModeloJuego`, `ObservadorJuego` | Modelo y sujeto del patrón observador. |
| `cliente.red` | `FachadaJuegoRemota` | La fachada vista desde el cliente: reenvía por socket. |
| `negocio` | `IFachadaJuego`, `FachadaJuegoMock`, `OyenteJuego`, `MovimientoInvalidoException` | Fachada del subsistema de reglas y su mock. |
| `servidor` | `ServidorParchis`, `ManejadorCliente`, `AppServidor` | Acepta jugadores y difunde el estado. |
| `comun` | `EstadoJuego`, `EstadoFicha`, `ColorJugador`, `Mensaje` | Lo que viaja por el socket. |

Recorrido de un movimiento: `VistaJuego` → `ControladorJuego` → `ModeloJuego` → `FachadaJuegoRemota` →
(socket) → `ManejadorCliente` → `FachadaJuegoMock` → `ServidorParchis.estadoCambiado` → `difundir` →
(socket) → `FachadaJuegoRemota` de cada jugador → `ModeloJuego.estadoCambiado` → `notificarObservadores` →
`VistaJuego.actualizar`.

