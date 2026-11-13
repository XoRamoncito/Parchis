# Parchís en Red

Arquitectura de Software (ITSON) · Equipo 3 · Ramon Montoya Manjarrez y Gustavo Romero Gonzalez

Unidad II, Hito 2B: el **CU-01 Registrarse en la sala** funcionando **entre varias computadoras** con el estilo que nos
tocó, **basado en eventos**, y el perfil del jugador guardado en **MySQL**. Organizado en capas (presentación, negocio y
datos) y en un módulo de Maven por componente.

## Cómo ejecutarlo

Requiere Java 17 o superior, MySQL 8 y Maven (el que trae NetBeans sirve).

1. **Base de datos (una vez en cada computadora).** Correr `bd/parchis.sql` en MySQL (Workbench: File > Open SQL Script,
   y ejecutar). Crea la base `parchis`, la tabla `perfil` y un usuario solo para el juego.
2. **Conexión.** Copiar `parchis-bd.properties.ejemplo` como `parchis-bd.properties` y poner ahí el usuario y la
   contraseña. Ese archivo no se sube a GitHub.
3. **Compilar.** En NetBeans: abrir el proyecto `parchis` y hacer *Clean and Build*. O `mvn install`.
4. **Jugar.** En la computadora que tendrá el bus: `ejecutar-parchis.bat`. En las demás:
   `ejecutar-parchis.bat <ip-de-esa-computadora>` (la de Radmin, por ejemplo). La primera que entra abre el bus.

- El prototipo del estilo (Hito 2A): `ejecutar-prototipo.bat bus` en una computadora y
  `ejecutar-prototipo.bat <nombre> <ip-del-bus>` en cada una.
- Pruebas: `mvn test` (20 pruebas). Incluyen la prueba de contrato contra el mock y contra el subsistema real, pruebas con
  dos "computadoras" conectadas al bus y pruebas de `PerfilBO` con `PerfilDAOMemoria`, sin MySQL.

## El CU-01

El jugador escribe su nombre, elige color y avatar, y presiona "Registrar perfil". Si todo está bien entra a la
sala de espera y ve a los demás jugadores, aunque estén en otra computadora; ahí puede confirmar que está listo.
Su perfil se guarda en MySQL de su computadora (si ya existía, se carga).

| Flujo | Qué pasa |
|---|---|
| Normal | Queda registrado y entra a la sala de espera. En las demás computadoras lo ven llegar. |
| A1 | El nombre está vacío, es muy largo o ya existe: se muestra el mensaje y no se registra. |
| A2 | El color ya lo tiene alguien (en cualquier computadora): se muestra el mensaje y solo se dejan elegir los libres. |
| A3 | La sala ya tiene 4 jugadores: se muestra el mensaje y no se registra. |

## El riesgo del estilo y cómo lo resolvimos

En el estilo basado en eventos el orden en que llegan los eventos no está garantizado. Si dos jugadores eligen el mismo
color al mismo tiempo, cada computadora podría ver un ganador distinto. Lo resolvimos así:

- Nadie se agrega a la sala directamente: quien se registra **publica una solicitud** en el bus.
- El bus (`RelevoEventos`) **numera cada evento** y lo reparte a todas las computadoras en ese orden.
- Cada computadora aplica las mismas validaciones en ese mismo orden, así que **en todas gana el primero** y al
  segundo le llega el flujo A2.
- El bus guarda el historial: quien se conecta tarde recibe primero lo que ya pasó y su sala queda igual a las demás.

## Componentes (un módulo de Maven cada uno)

| Módulo | Capa | Qué tiene |
|---|---|---|
| `parchis-presentacion` | Presentación | `FormRegistro` y `FormSalaEspera` (las pantallas del storyboard), `ControlSala`, el `main` y `FabricaSubsistemas`. |
| `parchis-sala-api` | Negocio | Paquete de interfaces del subsistema Sala: `IFachadaSala`, `SalaException`, `ObservadorSala`. |
| `parchis-sala` | Negocio | Subsistema Sala real: `FachadaSala`, `EstadoSala` y las validaciones `NombreValido`, `SalaConLugar`, `ColorLibre`. |
| `parchis-sala-mock` | Negocio | `FachadaSalaMock` del Hito 2A; pasa la misma prueba de contrato que el real. |
| `parchis-perfiles` | Negocio | Objeto de negocio `PerfilBO` (obtener o crear el perfil) y `NegocioException`. |
| `parchis-comunicacion-api` | Negocio | Paquete de interfaces de comunicación: `IComunicacion`, `ReceptorMensajes`. |
| `parchis-comunicacion-eventos` | Negocio | El estilo basado en eventos: `BusEventosRed`, `RelevoEventos` y el prototipo. |
| `parchis-datos` | Datos | `IPerfilDAO` (`guardar`, `buscarPorNombre`, `registrarPartida`), `PerfilDAOMySQL` (todo el SQL), `PerfilDAOMemoria` (para probar sin MySQL), `PerfilEntidad`, `ConexionBD`, `PersistenciaException`. |
| `parchis-dtos` | Entre capas | `JugadorDTO`, `SalaDTO`, `PerfilDTO`, `MensajeDTO`, `ColorJugador`. |

Las pantallas y el control solo conocen `IFachadaSala`, `ObservadorSala` y los DTOs. `FabricaSubsistemas` es la única
clase que nombra implementaciones concretas.

El modelo de StarUML está en `modelo/ParchisEnRed_CU01_Equipo3.mdj` (análisis actualizado, diseño de cada subsistema con sus
secuencias y la vista de componentes). El código de la Unidad I quedó en la etiqueta `unidad-1`.
