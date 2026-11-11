# Parchís en Red

Arquitectura de Software (ITSON) · Equipo 3 · Ramon Montoya Manjarrez y Gustavo Romero Gonzalez

Unidad II, Hito 2A: el **CU-01 Registrarse en la sala** funcionando sobre los subsistemas mock, organizado en
capas y en un módulo de Maven por componente, y el prototipo del estilo que nos tocó: **basado en eventos**.

## Cómo ejecutarlo

Requiere Java 17 o superior.

- El CU-01: doble clic en `ejecutar-parchis.bat` (o correr `presentacion.AppParchis`).
- El prototipo del estilo: en una computadora `ejecutar-prototipo.bat bus`; en cada computadora
  `ejecutar-prototipo.bat <nombre> <ip-de-la-del-bus>` y escribir un mensaje. Le llega al otro como evento.
- Pruebas: `mvn test`.

## El CU-01

El jugador escribe su nombre, elige color y avatar, y presiona "Registrar perfil". Si todo está bien entra a la
sala de espera y ve a los demás jugadores; ahí puede confirmar que está listo.

| Flujo | Qué pasa |
|---|---|
| Normal | Queda registrado y entra a la sala de espera. Los demás lo ven llegar. |
| A1 | El nombre está vacío, es muy largo o ya existe: se muestra el mensaje y no se registra. |
| A2 | El color ya está ocupado: se muestra el mensaje y solo se dejan elegir los colores libres. |
| A3 | La sala ya tiene 4 jugadores: se muestra el mensaje y no se registra. |

## Componentes (un módulo de Maven cada uno)

| Módulo | Capa | Qué tiene |
|---|---|---|
| `parchis-presentacion` | Presentación | `FormRegistro` y `FormSala` (las pantallas del storyboard), `ControlSala`, el `main` y `FabricaSubsistemas`. |
| `parchis-sala-api` | Negocio | Paquete de interfaces del subsistema Sala: `IFachadaSala`, `SalaException`, `ObservadorSala`. |
| `parchis-sala-mock` | Negocio | `FachadaSalaMock`: la sala en memoria, con los flujos A1, A2 y A3. |
| `parchis-comunicacion-api` | Negocio | Paquete de interfaces de comunicación: `IComunicacion`, `ReceptorMensajes`. |
| `parchis-comunicacion-eventos` | Negocio | El estilo basado en eventos: `BusEventosRed`, `RelevoEventos` y el prototipo. |
| `parchis-dtos` | Entre capas | `JugadorDTO`, `SalaDTO`, `MensajeDTO`, `ColorJugador`. |

Las pantallas y el control solo conocen las interfaces y los DTOs. La fábrica es la única que nombra al mock:
para el Hito 2B se cambia `FabricaSubsistemas.USAR_MOCK`.

El modelo de StarUML está en `modelo/`.

El código de la Unidad I quedó en la etiqueta `unidad-1`.
